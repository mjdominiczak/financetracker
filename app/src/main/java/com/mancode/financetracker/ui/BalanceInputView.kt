package com.mancode.financetracker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.mancode.financetracker.R
import com.mancode.financetracker.database.pojos.AccountNameCurrency
import com.mancode.financetracker.databinding.SingleBalanceBinding

class BalanceInputView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var binding = SingleBalanceBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var account: AccountNameCurrency
    var balanceId: Int? = null
    var deleted = false

    init {
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.balanceValue.isEnabled = isChecked
        }
        binding.deleteBalance.setOnClickListener { delete() }
        binding.undeleteBalance.setOnClickListener { undelete() }
    }

    fun setAccount(account: AccountNameCurrency) {
        this.account = account
        binding.accountName.text = account.accountName
        binding.currency.text = account.currency
        setHint(0.0)
    }

    fun getAccountId(): Int = account.id

    fun getValue(): Double {
        val string = binding.balanceValue.text.toString()
        return if (string.isNotEmpty())
            string.toDouble()
        else 0.0
    }

    fun setValue(value: Double) {
        binding.balanceValue.setText(value.toString())
    }

    fun setHint(value: Double) {
        binding.balanceValue.hint = value.toString()
    }

    fun isActive() = binding.checkBox.isChecked

    fun setActive(active: Boolean) {
        if (binding.checkBox.isEnabled) {
            binding.checkBox.isChecked = active
            binding.balanceValue.isEnabled = active
        }
    }

    fun setExisting() {
        setActive(false)
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorPositiveBackground))
        binding.deleteBalance.visibility = View.VISIBLE
    }

    private fun delete() {
        with(binding) {
            checkBox.isChecked = false
            checkBox.isEnabled = false
            deleteBalance.visibility = View.GONE
            undeleteBalance.visibility = View.VISIBLE
        }
        setBackgroundColor (0)
        deleted = true
    }

    private fun undelete() {
        setExisting()
        binding.checkBox.isEnabled = true
        binding.undeleteBalance.visibility = View.GONE
        deleted = false
    }
}