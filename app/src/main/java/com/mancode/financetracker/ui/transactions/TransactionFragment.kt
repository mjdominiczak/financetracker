package com.mancode.financetracker.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.ui.SetDateView
import com.mancode.financetracker.ui.SwipeToDeleteCallback
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.CUSTOM
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.LAST_MONTH
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.LAST_WEEK
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.PREVIOUS_MONTH
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.THIS_MONTH
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.THIS_YEAR
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.TYPE_ALL
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.TYPE_INCOME
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.TYPE_OUTCOME
import com.mancode.financetracker.ui.transactions.FilterQuery.Companion.UNCONSTRAINED
import com.mancode.financetracker.viewmodel.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

/**
 * Created by Manveru on 23.11.2017.
 */

class TransactionFragment : Fragment(), TransactionRecyclerViewAdapter.ModifyRequestListener {

    private lateinit var navController: NavController

    private val adapter: TransactionRecyclerViewAdapter by lazy {
        TransactionRecyclerViewAdapter(context!!, this)
    }
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        viewModel.allTransactions.observe(viewLifecycleOwner,
                Observer { transactions -> adapter.setTransactions(transactions) })
        transactions_list.layoutManager = LinearLayoutManager(context)
        transactions_list.adapter = adapter
        ItemTouchHelper(SwipeToDeleteCallback(adapter))
                .attachToRecyclerView(transactions_list)
        transactions_toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_bar_filter -> {
                    FilterDialog().show()
                    true
                }
                else -> false
            }

        }
        fab.setOnClickListener {
            navController.navigate(R.id.action_transactionFragment_to_addEditTransactionFragment)
        }
        val searchView = transactions_toolbar.menu.findItem(R.id.search_transactions).actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.updateFilterQueryDescription(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.updateFilterQueryDescription(newText)
                return false
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onEditRequested(transaction: TransactionEntity) {
        val action = TransactionFragmentDirections
                .actionTransactionFragmentToAddEditTransactionFragment(transaction.id)
        navController.navigate(action)
    }

    override fun onDeleteRequested(transaction: TransactionEntity?) {
        viewModel.deleteTransaction(transaction)
        val text: String = getString(R.string.snackbar_transaction_removed, transaction?.description)
        Snackbar.make(view!!, text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {
                    onRestoreRequested(transaction)
                }.show()
    }

    override fun onRestoreRequested(transaction: TransactionEntity?) {
        viewModel.restoreTransaction(transaction)
    }

    override fun onBookmarkToggleRequested(transaction: TransactionEntity) {
        viewModel.toggleBookmark(transaction)
    }

    private inner class FilterDialog {

        private val typeAdapter: ArrayAdapter<String> =
                ArrayAdapter(context!!, R.layout.dropdown_menu_popup_item,
                        resources.getStringArray(R.array.transaction_filter_type))
        private val timespanAdapter: ArrayAdapter<String> =
                ArrayAdapter(context!!, R.layout.dropdown_menu_popup_item,
                        resources.getStringArray(R.array.transaction_filter_timespan))
        private val dropdownTransactionType: AutoCompleteTextView
        private val dropdownTimespan: AutoCompleteTextView
        private val fromDateView: SetDateView
        private val toDateView: SetDateView
        private val bookmarkFilter: CheckBox
        private val dialogView: View =
                layoutInflater.inflate(R.layout.fragment_transaction_filter, null)

        init {
            dropdownTransactionType = dialogView.findViewById(R.id.transactionTypeField)
            val query = adapter.filterQuery
            with(dropdownTransactionType) {
                setAdapter(typeAdapter)
                setText(typeAdapter.getItem(typeToPosition(query.type)), false)
                setOnClickListener {
                    val index = typeAdapter.getPosition(text.toString())
                    listSelection = index
                }
            }
            dropdownTimespan = dialogView.findViewById(R.id.timespanField)
            with(dropdownTimespan) {
                setAdapter(timespanAdapter)
                setText(timespanAdapter.getItem(query.timespan), false)
                setOnClickListener {
                    val index = timespanAdapter.getPosition(text.toString())
                    listSelection = index
                }
                setOnItemClickListener { _, _, position, _ -> handleTimespan(position) }
            }
            fromDateView = dialogView.findViewById(R.id.sd_transaction_filter_from)
            toDateView = dialogView.findViewById(R.id.sd_transaction_filter_to)
            bookmarkFilter = dialogView.findViewById(R.id.bookmarkFilter)
            bookmarkFilter.isChecked = query.bookmarked
            handleTimespan(query.timespan)
        }

        internal fun show() {
            val builder = MaterialAlertDialogBuilder(context!!)
            builder.setTitle(R.string.title_transaction_filter)
                    .setView(dialogView)
                    .setNeutralButton(R.string.neutral_filter) { _, _ -> adapter.resetFilter() }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.positive_filter) { _, _ ->
                        adapter.updateFilterQuery(
                                positionToType(typeAdapter.getPosition(dropdownTransactionType.text.toString())),
                                fromDateView.date,
                                toDateView.date,
                                timespanAdapter.getPosition(dropdownTimespan.text.toString()),
                                bookmarkFilter.isChecked)
                    }
                    .show()
        }

        private fun typeToPosition(type: Int): Int {
            return when (type) {
                TYPE_INCOME -> 1
                TYPE_OUTCOME -> 2
                TYPE_ALL -> 0
                else -> 0
            }
        }

        private fun positionToType(position: Int): Int {
            return when (position) {
                1 -> TYPE_INCOME
                2 -> TYPE_OUTCOME
                0 -> TYPE_ALL
                else -> TYPE_ALL
            }
        }

        private fun handleTimespan(position: Int) {
            when (position) {
                UNCONSTRAINED -> {
                    toDateView.resetDate()
                    fromDateView.resetDate()
                }
                LAST_WEEK -> {
                    toDateView.date = LocalDate.now()
                    fromDateView.date = LocalDate.now().minusDays(7)
                }
                LAST_MONTH -> {
                    toDateView.date = LocalDate.now()
                    fromDateView.date = LocalDate.now().minusMonths(1)
                }
                THIS_MONTH -> {
                    toDateView.date = LocalDate.now()
                    fromDateView.date = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                }
                PREVIOUS_MONTH -> {
                    toDateView.date = LocalDate.now()
                            .minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())
                    fromDateView.date = LocalDate.now()
                            .minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())
                }
                THIS_YEAR -> {
                    toDateView.date = LocalDate.now()
                    fromDateView.date = LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                }
                CUSTOM -> {
                    toDateView.isEnabled = true
                    fromDateView.isEnabled = true
                    return
                }
            }
            toDateView.isEnabled = false
            fromDateView.isEnabled = false
        }
    }

    companion object {
        fun newInstance(): TransactionFragment {
            return TransactionFragment()
        }
    }
}
