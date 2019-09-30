package com.mancode.financetracker.ui.transactions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.TransactionFull
import com.mancode.financetracker.ui.UIUtils
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter(
        private val context: Context,
        private val modifyRequestListener: ModifyRequestListener?) :
        ListAdapter<TransactionFull, TransactionRecyclerViewAdapter.ViewHolder>(DiffCallback()), Filterable {

    private var allTransactions: List<TransactionFull>? = null

    private var filteredTransactions: List<TransactionFull>? = null
    var filterQuery: FilterQuery? = null
        private set
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (filteredTransactions != null && filteredTransactions!!.size > position) {
            val transaction = filteredTransactions!![position]
            viewHolder.init(transaction)
        } else {
            Log.e(TAG, "")
        }
    }

    fun setTransactions(transactions: List<TransactionFull>) {
        allTransactions = transactions
        filteredTransactions = transactions  // TODO think through
        submitList(transactions)
    }

    override fun getFilter(): Filter {
        return TransactionFilter()
    }

    fun buildFilterQuery(type: Int, from: LocalDate?, to: LocalDate?, timespan: Int, bookmarked: Boolean): String {
        filterQuery = FilterQuery(type, from, to, timespan, bookmarked)
        return filterQuery!!.query
    }

    private class DiffCallback : DiffUtil.ItemCallback<TransactionFull>() {
        override fun areItemsTheSame(oldItem: TransactionFull, newItem: TransactionFull): Boolean =
                oldItem.transaction.id == newItem.transaction.id

        override fun areContentsTheSame(oldItem: TransactionFull, newItem: TransactionFull): Boolean =
                oldItem.transaction.id == newItem.transaction.id &&
                oldItem.transaction.value == newItem.transaction.value &&
                oldItem.transaction.date == newItem.transaction.date &&
                oldItem.transaction.description == newItem.transaction.description
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvDate: TextView = view.findViewById(R.id.transaction_date)
        private val tvValue: TextView = view.findViewById(R.id.transaction_value)
        private val tvDescription: TextView = view.findViewById(R.id.transaction_description)
        private val menuButton: ImageButton = view.findViewById(R.id.transaction_menu_button)
        private val bookmark: CheckBox = view.findViewById(R.id.bookmarkButton)
        private var mTransaction: TransactionFull? = null

        fun init(transaction: TransactionFull) {
            mTransaction = transaction
            tvDate.text = DateConverter.toString(mTransaction!!.transaction.date)
            tvValue.text = UIUtils.getFormattedMoney(
                    mTransaction!!.transaction.value, mTransaction!!.currency)
            val color = if (mTransaction!!.transaction.type == TransactionEntity.TYPE_INCOME)
                ContextCompat.getColor(context, R.color.colorPositiveValue)
            else
                ContextCompat.getColor(context, R.color.colorNegativeValue)
            tvValue.setTextColor(color)
            tvDescription.text = mTransaction!!.transaction.description
            menuButton.setOnClickListener { this.showTransactionPopup(it) }
            bookmark.setOnClickListener {
                modifyRequestListener?.onBookmarkToggleRequested(mTransaction!!.transaction) }
            bookmark.isChecked = (mTransaction!!.transaction.flags and TransactionEntity.BOOKMARKED) == 1
        }

        private fun showTransactionPopup(view: View) {
            val popup = PopupMenu(view.context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.transaction_actions, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_transaction -> {
                        modifyRequestListener?.onEditRequested(mTransaction!!.transaction)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_delete_transaction -> {
                        modifyRequestListener?.onDeleteRequested(mTransaction!!.transaction)
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
            popup.show()
        }
    }

    private inner class TransactionFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val query = charSequence.toString()
            val filterResults = FilterResults()
            if (query.isEmpty()) {
                filterResults.values = allTransactions
            } else {
                filterQuery = FilterQuery(query)
                val filteredList: MutableList<TransactionFull> = ArrayList()
                for (transaction in allTransactions!!) {
                    if (filterQuery!!.isMatch(transaction)) {
                        filteredList.add(transaction)
                    }
                }
                filterResults.values = filteredList
            }
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            filteredTransactions = filterResults.values as ArrayList<TransactionFull>
            submitList(filteredTransactions)
        }
    }

    interface ModifyRequestListener {
        fun onEditRequested(transaction: TransactionEntity)
        fun onDeleteRequested(transaction: TransactionEntity)
        fun onBookmarkToggleRequested(transaction: TransactionEntity)
    }

    companion object {
        private val TAG = TransactionRecyclerViewAdapter::class.java.simpleName
    }
}
