package com.mancode.financetracker.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.mancode.financetracker.R

class DropdownInputLayout : TextInputLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, R.attr.dropdownInputStyle)
}