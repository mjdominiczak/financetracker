package com.mancode.financetracker.ui.transactions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.database.pojos.TransactionFull
import com.mancode.financetracker.ui.setFormattedMoney
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Created by Manveru on 18.12.2017.
 */

class TransactionRecyclerViewAdapter(
        private val context: Context,
        val modifyRequestListener: ModifyRequestListener) :
        ListAdapter<TransactionFull, TransactionRecyclerViewAdapter.ViewHolder>(DiffCallback()), Filterable {

    private var allTransactions: List<TransactionFull>? = null

    private var filteredTransactions: List<TransactionFull>? = null
    val filterQuery = FilterQuery()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_wrapper, parent, false)
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

    fun resetFilter() {
        filterQuery.resetExceptDescription()
        filter.filter("")
    }

    fun updateFilterQuery(type: Int, from: LocalDate?, to: LocalDate?, timespan: Int, bookmarked: Boolean) {
        filterQuery.update(type = type, from = from, to = to, timespan = timespan, bookmark = bookmarked)
        filter.filter("")
    }

    fun updateFilterQueryDescription(description: String?) {
        filterQuery.update(description = description)
        filter.filter("")
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

        val foregroundView: MaterialCardView = view.findViewById(R.id.transaction_card_foreground)
        private val tvDate: TextView = view.findViewById(R.id.transaction_date)
        private val tvValue: TextView = view.findViewById(R.id.transaction_value)
        private val tvDescription: TextView = view.findViewById(R.id.transaction_description)
        private val bookmark: CheckBox = view.findViewById(R.id.bookmarkButton)
        var mTransaction: TransactionFull? = null

        fun init(transaction: TransactionFull) {
            mTransaction = transaction
            tvDate.text = DateConverter.toString(mTransaction!!.transaction.date)
            tvValue.setFormattedMoney(mTransaction!!.transaction.value, mTransaction!!.currency)
            val color = if (mTransaction!!.transaction.type == TransactionEntity.TYPE_INCOME)
                ContextCompat.getColor(context, R.color.colorPositiveValue)
            else
                ContextCompat.getColor(context, R.color.colorNegativeValue)
            tvValue.setTextColor(color)
            tvDescription.text = mTransaction!!.transaction.description
            bookmark.setOnClickListener {
                modifyRequestListener.onBookmarkToggleRequested(mTransaction!!.transaction)
            }
            bookmark.isChecked = (mTransaction!!.transaction.flags and TransactionEntity.BOOKMARKED) == 1
            foregroundView.setOnClickListener {
                modifyRequestListener.onEditRequested(transaction.transaction)
            }
        }
    }

    private inner class TransactionFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val query = filterQuery.query
            val filterResults = FilterResults()
            if (query.isEmpty()) {
                filterResults.values = allTransactions
            } else {
                val filteredList: MutableList<TransactionFull> = ArrayList()
                for (transaction in allTransactions!!) {
                    if (filterQuery.isMatch(transaction)) {
                        filteredList.add(transaction)
                    }
                }
                filterResults.values = filteredList
            }
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            filteredTransactions = filterResults.values as ArrayList<TransactionFull>   // TODO crash here
            submitList(filteredTransactions)
        }
    }

    interface ModifyRequestListener {
        fun onEditRequested(transaction: TransactionEntity)
        fun onDeleteRequested(transaction: TransactionEntity?)
        fun onRestoreRequested(transaction: TransactionEntity?)
        fun onBookmarkToggleRequested(transaction: TransactionEntity)
    }

    companion object {
        private val TAG = TransactionRecyclerViewAdapter::class.java.simpleName
    }
}
