package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancode.financetracker.R
import com.mancode.financetracker.databinding.FragmentBalanceListBinding
import com.mancode.financetracker.viewmodel.BalanceViewModel
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 03.09.2017.
 */

class BalanceFragment : Fragment(R.layout.fragment_balance_list), BalanceRecyclerViewAdapter.ModifyRequestListener {

    private var _binding: FragmentBalanceListBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val adapter: BalanceRecyclerViewAdapter by lazy { BalanceRecyclerViewAdapter(this) }
    private val viewModel: BalanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentBalanceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.netValues.observe(viewLifecycleOwner, { netValues ->
            adapter.submitList(netValues)
            if (netValues.isNotEmpty()) {
                binding.balancesList.visibility = View.VISIBLE
                binding.emptyListInfo.visibility = View.GONE
            } else {
                binding.balancesList.visibility = View.GONE
                binding.emptyListInfo.visibility = View.VISIBLE
            }
        })
        navController = findNavController()
        binding.balancesList.layoutManager = LinearLayoutManager(context)
        binding.balancesList.adapter = adapter
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_balanceFragment_to_addBalanceFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onEditRequested(date: LocalDate) {
        val action = BalanceFragmentDirections.actionBalanceFragmentToAddBalanceFragment(date)
        navController.navigate(action)
    }
}
