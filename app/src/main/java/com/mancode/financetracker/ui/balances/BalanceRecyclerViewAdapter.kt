package com.mancode.financetracker.ui.balances

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mancode.financetracker.R
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.pojos.BalanceExtended
import com.mancode.financetracker.ui.UIUtils
import net.cachapa.expandablelayout.ExpandableLayout
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Created by Manveru on 06.09.2017.
 */

class BalanceRecyclerViewAdapter : RecyclerView.Adapter<BalanceRecyclerViewAdapter.ViewHolder>() {

    private var mRecyclerView: RecyclerView? = null
    private var mBalancesMap: LinkedHashMap<LocalDate, MutableList<BalanceExtended>>? = null
    private var mAllBalances: List<BalanceExtended>? = null

    private var mExpandedPosition = -1

    override fun getItemId(position: Int): Long {
        if (mBalancesMap != null && mBalancesMap!!.size >= position) {
            for ((i, entry) in mBalancesMap!!.entries.withIndex()) {
                if (i == position) {
                    return entry.hashCode().toLong()
                }
            }
        }
        return 0
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    private fun syncMapWithData() {
        if (mBalancesMap == null) {
            mBalancesMap = LinkedHashMap()
        } else {
            mBalancesMap!!.clear()
        }
        if (mAllBalances != null) {
            for (balance in mAllBalances!!) {
                val date = balance.checkDate
                val list: MutableList<BalanceExtended>?
                if (mBalancesMap!!.containsKey(date)) {
                    list = mBalancesMap!![date]
                } else {
                    list = mutableListOf()
                    mBalancesMap!![date] = list
                }
                list!!.add(balance)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mBalancesMap != null) {
            mBalancesMap!!.size
        } else
            0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_balance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (!(position >= 0 && position < mBalancesMap!!.size)) {
            throw IllegalStateException("Position invalid: $position")
        }
        var i = 0
        for ((key, value) in mBalancesMap!!) {
            if (i == position) {
                viewHolder.initFromList(key, value)
                break
            } else
                i++
        }
    }

    fun setBalances(balances: List<BalanceExtended>) {
        mAllBalances = balances
        syncMapWithData()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView), View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private val mLayout: ExpandableLayout
        private val mList: LinearLayout

        init {
            mView.setOnClickListener(this)
            mLayout = mView.findViewById(R.id.balances_list_expandable)
            mLayout.setOnExpansionUpdateListener(this)
            mList = mView.findViewById(R.id.balances_list)
        }

        fun initFromList(key: LocalDate, itemList: List<BalanceExtended>) {
            mList.removeAllViews()
            val balanceDate = mView.findViewById<TextView>(R.id.balance_date)
            balanceDate.text = DateConverter.toString(key)
            val balanceDaily = mView.findViewById<TextView>(R.id.balance_daily)
            balanceDaily.text = UIUtils.getFormattedMoney(calculateDailyBalance(itemList),
                    Currency.getInstance(Locale.getDefault()).currencyCode) // TODO default currency??? - selectable?
            for (item in itemList) {
                val innerLayout = LayoutInflater.from(mView.context)
                        .inflate(R.layout.single_balance, mList, false) as LinearLayout
                val balanceValue = innerLayout.findViewById<TextView>(R.id.balance_value)
                balanceValue.text = UIUtils.getFormattedMoney(item.value, item.accountCurrency)
                if (item.value != 0.0) {
                    val color = if (item.accountType == 1)
                        ContextCompat.getColor(mRecyclerView!!.context, R.color.colorPositiveValue)
                    else
                        ContextCompat.getColor(mRecyclerView!!.context, R.color.colorNegativeValue)
                    balanceValue.setTextColor(color)
                }
                val balanceAccount = innerLayout.findViewById<TextView>(R.id.balance_account)
                balanceAccount.text = item.accountName
                mList.addView(innerLayout)
            }
        }

        override fun onClick(v: View) {
            mExpandedPosition = if (adapterPosition == mExpandedPosition) {
                mLayout.collapse()
                -1
            } else {
                if (mExpandedPosition != -1) {
                    val holder = mRecyclerView!!.findViewHolderForAdapterPosition(mExpandedPosition) as ViewHolder?
                    holder?.mLayout?.collapse()
                }
                mLayout.expand()
                adapterPosition
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("Expandable layout", "State: $state")
            mRecyclerView!!.smoothScrollToPosition(adapterPosition)
        }

        private fun calculateDailyBalance(itemList: List<BalanceExtended>): Double {
            var result = 0.0
            for (item in itemList) {
                result += item.accountType.toDouble() * item.value
            }
            return result
        }

    }
}
