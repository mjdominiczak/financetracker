package com.mancode.financetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mancode.financetracker.R
import com.mancode.financetracker.databinding.FragmentFirstRunBinding
import com.mancode.financetracker.viewmodel.FirstRunViewModel
import com.mancode.financetracker.workers.runUpdateWorker

class FirstRunFragment : Fragment(R.layout.fragment_first_run) {

    private var _binding: FragmentFirstRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstRunViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFirstRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                viewModel.currencyCodesList
        )
        with(binding.defaultCurrency) {
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
        binding.openingBalance.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                try {
                    val double = it.toString().trim().toDouble()
                    viewModel.openingBalance = double
                    binding.openingBalanceInputLayout.error = null
                } catch (e: NumberFormatException) {
                    binding.openingBalanceInputLayout.error = getString(R.string.error_not_a_number)
                }
            } else {
                binding.openingBalanceInputLayout.error = null
            }
        }

        binding.skipButton.setOnClickListener {
            viewModel.skip()
            findNavController().navigate(R.id.action_firstRunFragment_to_dashboardFragment)
        }

        binding.saveButton.setOnClickListener {
            viewModel.storeInitialData()
            requireContext().runUpdateWorker()
            findNavController().navigate(R.id.action_firstRunFragment_to_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}