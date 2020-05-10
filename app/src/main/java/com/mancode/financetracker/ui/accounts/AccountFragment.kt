package com.mancode.financetracker.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private val viewModel: AccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.allAccounts.observe(viewLifecycleOwner,
                Observer { accounts -> adapter.setAccounts(accounts) })
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
            navController.navigate(R.id.action_accountFragment_to_addEditAccountFragment)
        }
    }

    override fun onEditRequested(accountId: Int) {
        val action = AccountFragmentDirections.actionAccountFragmentToAddEditAccountFragment(accountId)
        navController.navigate(action)
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}
