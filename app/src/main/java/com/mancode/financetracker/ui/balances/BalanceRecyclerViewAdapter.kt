package com.mancode.financetracker.ui.balances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.NetValue
import com.mancode.financetracker.ui.UIUtils
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_balance.view.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter : ListAdapter<NetValue, BalanceRecyclerViewAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindTo(getItem(position))
    }

    class DiffCallback: DiffUtil.ItemCallback<NetValue>() {
        override fun areItemsTheSame(oldItem: NetValue, newItem: NetValue): Boolean =
                oldItem.date.isEqual(newItem.date)

        override fun areContentsTheSame(oldItem: NetValue, newItem: NetValue): Boolean =
                oldItem.date.isEqual(newItem.date) && oldItem.value == newItem.value // TODO NetValue to Kotlin and implement "equals" (data class)
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bindTo(netValue: NetValue) {
            containerView.balanceDate.text = netValue.date.format(DateTimeFormatter.ISO_LOCAL_DATE.withLocale(Locale.getDefault()))
            containerView.balanceDaily.text = UIUtils.getFormattedMoney(netValue.value, PreferenceAccessor.defaultCurrency)
        }
    }
}