package com.mancode.financetracker.ui.transactions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
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

internal class TransactionRecyclerViewAdapter(
        private val context: Context,
        private val modifyRequestListener: ModifyRequestListener?) :
        RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>(), Filterable {

    private var allTransactions: MutableList<TransactionFull>? = null
    private var filteredTransactions: List<TransactionFull>? = null
    private var isFiltered = false
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

    override fun getItemCount(): Int {
        return filteredTransactions?.size ?: 0
    }

    fun setTransactions(transactions: MutableList<TransactionFull>) {
        allTransactions = transactions
        filteredTransactions = transactions  // TODO think through
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return TransactionFilter()
    }

    fun buildFilterQuery(type: Int, from: LocalDate?, to: LocalDate?): String {
        filterQuery = FilterQuery(type, from, to)
        return filterQuery!!.query
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvDate: TextView = view.findViewById(R.id.transaction_date)
        private val tvValue: TextView = view.findViewById(R.id.transaction_value)
        private val tvDescription: TextView = view.findViewById(R.id.transaction_description)
        private val menuButton: ImageButton = view.findViewById(R.id.transaction_menu_button)
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
            var filteredList: MutableList<TransactionFull>? = ArrayList()
            if (query.isEmpty()) {
                isFiltered = false
                filteredList = allTransactions
            } else {
                isFiltered = true
                filterQuery = FilterQuery(query)
                for (transaction in allTransactions!!) {
                    if (filterQuery!!.isMatch(transaction)) {
                        filteredList!!.add(transaction)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            filteredTransactions = filterResults.values as ArrayList<TransactionFull>
            notifyDataSetChanged()
        }
    }

    interface ModifyRequestListener {
        fun onEditRequested(transaction: TransactionEntity)
        fun onDeleteRequested(transaction: TransactionEntity)
    }

    companion object {
        private val TAG = TransactionRecyclerViewAdapter::class.java.simpleName
    }
}
