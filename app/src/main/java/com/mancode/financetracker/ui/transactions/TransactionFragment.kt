package com.mancode.financetracker.ui.transactions

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.ui.SetDateView
import com.mancode.financetracker.ui.UIUtils
import com.mancode.financetracker.ui.transactions.FilterQuery.*
import com.mancode.financetracker.viewmodel.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_account_list.*
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters

/**
 * Created by Manveru on 23.11.2017.
 */

class TransactionFragment : Fragment(), TransactionRecyclerViewAdapter.ModifyRequestListener {

    private val adapter: TransactionRecyclerViewAdapter by lazy {
        TransactionRecyclerViewAdapter(context!!, this)
    }
    private val viewModel: TransactionViewModel by lazy {
        ViewModelProviders.of(this).get(TransactionViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.allTransactions.observe(this,
                Observer { transactions -> adapter.setTransactions(transactions) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_transaction_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_filter -> {
                FilterDialog().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        fab.setOnClickListener {UIUtils.showFullScreenDialog(
                parentFragmentManager, AddEditTransactionFragment())
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onEditRequested(transaction: TransactionEntity) {
        UIUtils.showFullScreenDialog(
                parentFragmentManager, AddEditTransactionFragment.newInstance(transaction)
        )
    }

    override fun onDeleteRequested(transaction: TransactionEntity) {
        viewModel.deleteTransaction(transaction)
    }

    override fun onBookmarkToggleRequested(transaction: TransactionEntity) {
        viewModel.toggleBookmark(transaction)
    }

    private inner class FilterDialog {

        private var mTransactionTypeSpinner: Spinner? = null
        private var mTransactionTimespanSpinner: Spinner? = null
        private var mFromDate: SetDateView? = null
        private var mToDate: SetDateView? = null
        private var mBuilder: MaterialAlertDialogBuilder? = null

        private val type: Int
            get() {
                return when (mTransactionTypeSpinner!!.selectedItemPosition) {
                    1 -> TYPE_INCOME
                    2 -> TYPE_OUTCOME
                    0 -> TYPE_ALL
                    else -> TYPE_ALL
                }
            }

        private val fromDate: LocalDate?
            get() = mFromDate!!.date

        private val toDate: LocalDate?
            get() = mToDate!!.date

        internal fun show() {
            mBuilder = MaterialAlertDialogBuilder(context!!)
            val dialogView = layoutInflater.inflate(R.layout.fragment_transaction_filter, null)
            mTransactionTypeSpinner = dialogView.findViewById(R.id.sp_transaction_filter_type)
            mTransactionTimespanSpinner = dialogView.findViewById(R.id.sp_transaction_filter_timespan)
            mTransactionTimespanSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    handleTimespan(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
            mFromDate = dialogView.findViewById(R.id.sd_transaction_filter_from)
            mToDate = dialogView.findViewById(R.id.sd_transaction_filter_to)
            val query = adapter.filterQuery
            if (query != null) {
                mTransactionTypeSpinner!!.setSelection(getTypePosition(query.type))
                val fromDate = query.fromDate
                if (fromDate != null) mFromDate!!.date = fromDate
                val toDate = query.toDate
                if (toDate != null) mToDate!!.date = toDate
            }
            mBuilder!!.setTitle(R.string.title_transaction_filter)
                    .setView(dialogView)
                    .setNeutralButton(R.string.neutral_filter) { _, _ -> adapter.filter.filter(getEmptyQuery()) }
                    .setNegativeButton(R.string.negative_filter) { _, _ -> }
                    .setPositiveButton(R.string.positive_filter) { _, _ ->
                        val newQuery = adapter.buildFilterQuery(type, fromDate, toDate)
                        adapter.filter.filter(newQuery)
                    }
                    .show()
        }

        private fun getTypePosition(type: Int): Int {
            return when (type) {
                TYPE_INCOME -> 1
                TYPE_OUTCOME -> 2
                TYPE_ALL -> 0
                else -> 0
            }
        }

        private fun handleTimespan(position: Int) {
            when (position) {
                0 // UNCONSTRAINED
                -> {
                    mToDate!!.resetDate()
                    mFromDate!!.resetDate()
                }
                1 // LAST WEEK
                -> {
                    mToDate!!.date = LocalDate.now()
                    mFromDate!!.date = LocalDate.now().minusDays(7)
                }
                2 // LAST MONTH
                -> {
                    mToDate!!.date = LocalDate.now()
                    mFromDate!!.date = LocalDate.now().minusMonths(1)
                }
                3 // THIS MONTH
                -> {
                    mToDate!!.date = LocalDate.now()
                    mFromDate!!.date = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
                }
                4 // PREVIOUS MONTH
                -> {
                    mToDate!!.date = LocalDate.now()
                            .minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())
                    mFromDate!!.date = LocalDate.now()
                            .minusMonths(1).with(TemporalAdjusters.firstDayOfMonth())
                }
                5 // THIS YEAR
                -> {
                    mToDate!!.date = LocalDate.now()
                    mFromDate!!.date = LocalDate.now().with(TemporalAdjusters.firstDayOfYear())
                }
                6 // CUSTOM
                -> {
                    mToDate!!.isEnabled = true
                    mFromDate!!.isEnabled = true
                    return
                }
            }
            mToDate!!.isEnabled = false
            mFromDate!!.isEnabled = false
        }
    }

    companion object {
        fun newInstance(): TransactionFragment {
            return TransactionFragment()
        }
    }

}
