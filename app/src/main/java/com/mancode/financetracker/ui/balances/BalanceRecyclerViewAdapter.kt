package com.mancode.financetracker.ui.balances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.databinding.FragmentBalanceBinding
import com.mancode.financetracker.ui.setFormattedMoney
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter(private val modifyRequestListener: ModifyRequestListener) :
        ListAdapter<NetValue, BalanceRecyclerViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindTo(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NetValue>() {
        override fun areItemsTheSame(oldItem: NetValue, newItem: NetValue): Boolean =
                oldItem.date.isEqual(newItem.date)

        override fun areContentsTheSame(oldItem: NetValue, newItem: NetValue): Boolean =
                oldItem.date.isEqual(newItem.date) && oldItem.value == newItem.value // TODO NetValue to Kotlin and implement "equals" (data class)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener { modifyRequestListener.onEditRequested(balanceDate) }
        }

        private val binding = FragmentBalanceBinding.bind(view)
        private lateinit var balanceDate: LocalDate

        fun bindTo(netValue: NetValue) {
            balanceDate = netValue.date
            binding.balanceDate.text = balanceDate.format(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.getDefault()))
            binding.balanceDaily.setFormattedMoney(netValue.value)
        }
    }

    interface ModifyRequestListener {
        fun onEditRequested(date: LocalDate)
    }
}
