package com.mancode.financetracker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.mancode.financetracker.R
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import kotlinx.android.synthetic.main.single_balance.view.*

class BalanceInputView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var account: AccountNameCurrency
    var balanceId: Int? = null
    var deleted = false

    init {
        LayoutInflater.from(context).inflate(R.layout.single_balance, this, true)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            balanceValue.isEnabled = isChecked
        }
        deleteBalance.setOnClickListener { delete() }
        undeleteBalance.setOnClickListener { undelete() }
    }

    fun setAccount(account: AccountNameCurrency) {
        this.account = account
        accountName.text = account.accountName
        currency.text = account.currency
        setHint(0.0)
    }

    fun getAccountId(): Int = account.id

    fun getValue(): Double {
        val string = balanceValue.text.toString()
        return if (string.isNotEmpty())
            string.toDouble()
        else 0.0
    }

    fun setValue(value: Double) {
        balanceValue.setText(value.toString())
    }

    fun setHint(value: Double) {
        balanceValue.hint = value.toString()
    }

    fun isActive() = checkBox.isChecked

    fun setActive(active: Boolean) {
        if (checkBox.isEnabled) {
            checkBox.isChecked = active
            balanceValue.isEnabled = active
        }
    }

    fun setExisting() {
        setActive(false)
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPositiveBackground))
        deleteBalance.visibility = View.VISIBLE
    }

    private fun delete() {
        checkBox.isChecked = false
        checkBox.isEnabled = false
        deleteBalance.visibility = View.GONE
        undeleteBalance.visibility = View.VISIBLE
        setBackgroundColor(0)
        deleted = true
    }

    private fun undelete() {
        setExisting()
        checkBox.isEnabled = true
        undeleteBalance.visibility = View.GONE
        deleted = false
    }
}