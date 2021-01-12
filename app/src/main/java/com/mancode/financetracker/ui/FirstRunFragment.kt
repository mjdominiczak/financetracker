package com.mancode.financetracker.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mancode.financetracker.R
import com.mancode.financetracker.viewmodel.FirstRunViewModel
import com.mancode.financetracker.workers.runUpdateWorker
import kotlinx.android.synthetic.main.fragment_first_run.*

class FirstRunFragment : Fragment() {

    private val viewModel: FirstRunViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first_run, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.currencyCodesList
        )
        with (defaultCurrency) {
            setAdapter(currencyAdapter)
            setText(viewModel.defaultCurrency, false)
            setOnClickListener {
                val preselectionIndex = viewModel.currencyCodesList.indexOf(text.toString())
                listSelection = preselectionIndex
            }
            setOnItemClickListener { _, _, position, _ ->
                viewModel.defaultCurrency = currencyAdapter.getItem(position)!!
            }
        }
        openingBalance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    try {
                        val double = s.toString().trim().toDouble()
                        viewModel.openingBalance = double
                        openingBalanceInputLayout.error = null
                    } catch (e: NumberFormatException) {
                        openingBalanceInputLayout.error = getString(R.string.error_not_a_number)
                    }
                } else {
                    openingBalanceInputLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        skipButton.setOnClickListener {
            viewModel.skip()
            findNavController().navigate(R.id.action_firstRunFragment_to_dashboardFragment)
        }

        saveButton.setOnClickListener {
            viewModel.storeInitialData()
            requireContext().runUpdateWorker()
            findNavController().navigate(R.id.action_firstRunFragment_to_dashboardFragment)
        }
    }
}