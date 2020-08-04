package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancode.financetracker.R
import com.mancode.financetracker.viewmodel.BalanceViewModel
import kotlinx.android.synthetic.main.fragment_balance_list.*
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 03.09.2017.
 */

class BalanceFragment : Fragment(), BalanceRecyclerViewAdapter.ModifyRequestListener {

    private lateinit var navController: NavController

    private val adapter: BalanceRecyclerViewAdapter by lazy { BalanceRecyclerViewAdapter(this) }
    private val viewModel: BalanceViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_balance_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.netValues.observe(viewLifecycleOwner,
                Observer { netValues ->
                    adapter.submitList(netValues)
                    if (netValues.isNotEmpty()) {
                        balancesList.visibility = View.VISIBLE
                        emptyListInfo.visibility = View.GONE
                    } else {
                        balancesList.visibility = View.GONE
                        emptyListInfo.visibility = View.VISIBLE
                    }
                })
        navController = findNavController()
        balancesList.layoutManager = LinearLayoutManager(context)
        balancesList.adapter = adapter
        fab.setOnClickListener {
            navController.navigate(R.id.action_balanceFragment_to_addBalanceFragment)
        }
    }

    override fun onEditRequested(date: LocalDate) {
        val action = BalanceFragmentDirections.actionBalanceFragmentToAddBalanceFragment(date)
        navController.navigate(action)
    }
}
