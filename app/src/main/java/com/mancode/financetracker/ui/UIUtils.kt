package com.mancode.financetracker.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mancode.financetracker.ui.prefs.PreferenceAccessor
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow

fun Fragment.hideKeyboard() {
    val context = context
    val view = view
    if (context != null && view != null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun View.showKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun TextView.setFormattedMoney(
    value: Double,
    currencyCode: String = PreferenceAccessor.defaultCurrency
) {
    text = value.formatAsMoney(currencyCode)
}

fun Double.formatAsMoney(currencyCode: String = PreferenceAccessor.defaultCurrency): String {
    val currency = Currency.getInstance(currencyCode)
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    formatter.currency = currency
    formatter.maximumFractionDigits = currency.defaultFractionDigits
    val result = if ((this - 0.0).absoluteValue < 10.0.pow(-currency.defaultFractionDigits))
        this.absoluteValue else
        this
    return formatter.format(result)
}