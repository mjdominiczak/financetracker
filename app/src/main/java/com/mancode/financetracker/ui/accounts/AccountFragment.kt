package com.mancode.financetracker.ui.accounts

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
import com.mancode.financetracker.viewmodel.AccountViewModel
import kotlinx.android.synthetic.main.fragment_account_list.*

/**
 * Created by Manveru on 03.09.2017.
 */

class AccountFragment : Fragment(), AccountRecyclerViewAdapter.ModifyRequestListener {

    lateinit var navController: NavController
    private val adapter: AccountRecyclerViewAdapter by lazy { AccountRecyclerViewAdapter(this) }
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allAccounts.observe(viewLifecycleOwner,
                Observer { accounts ->
                    adapter.setAccounts(accounts)
                    if (accounts.isNotEmpty()) {
                        accountsList.visibility = View.VISIBLE
                        emptyListInfo.visibility = View.GONE
                    } else {
                        accountsList.visibility = View.GONE
                        emptyListInfo.visibility = View.VISIBLE
                    }
                })
        navController = findNavController()
        accountsList.layoutManager = LinearLayoutManager(context)
        accountsList.adapter = adapter
        fab.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_addEditAccountFragment)
        }
    }

    override fun onEditRequested(accountId: Int) {
        val action = AccountFragmentDirections.actionAccountFragmentToAddEditAccountFragment(accountId)
        navController.navigate(action)
    }
}
