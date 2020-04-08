package com.mancode.financetracker.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancode.financetracker.R
import com.mancode.financetracker.viewmodel.BalanceViewModel
import kotlinx.android.synthetic.main.fragment_account_list.*
import org.threeten.bp.LocalDate

/**
 * Created by Manveru on 03.09.2017.
 */

class BalanceFragment : Fragment(), BalanceRecyclerViewAdapter.ModifyRequestListener {

    lateinit var navController: NavController

    private val adapter: BalanceRecyclerViewAdapter by lazy { BalanceRecyclerViewAdapter(this) }
    private val viewModel: BalanceViewModel by lazy {
        ViewModelProviders.of(this).get(BalanceViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.netValues.observe(viewLifecycleOwner, Observer { netValues -> adapter.submitList(netValues) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        fab.setOnClickListener {
            navController.navigate(R.id.action_balanceFragment_to_addBalanceFragment)
        }
    }

    override fun onEditRequested(date: LocalDate) {
        val action = BalanceFragmentDirections.actionBalanceFragmentToAddBalanceFragment(date)
        navController.navigate(action)
    }

    companion object {
        fun newInstance(): BalanceFragment {
            return BalanceFragment()
        }
    }
}
