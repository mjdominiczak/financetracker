package com.mancode.financetracker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.mancode.financetracker.R
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import kotlinx.android.synthetic.main.single_balance.view.*

class BalanceInputView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var account: AccountNameCurrency

    init {
        LayoutInflater.from(context).inflate(R.layout.single_balance, this, true)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            run {
                balanceValue.isEnabled = isChecked
            }
        }
    }

    fun setAccount(account: AccountNameCurrency) {
        this.account = account
        accountName.text = account.accountName
        currency.text = account.currency
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

    fun isActive() = checkBox.isChecked

    fun setActive(active: Boolean) {
        checkBox.isChecked = active
        balanceValue.isEnabled = active
    }

}