package com.mancode.financetracker.ui.accounts

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
import com.mancode.financetracker.databinding.FragmentAccountListBinding
import com.mancode.financetracker.viewmodel.AccountViewModel

/**
 * Created by Manveru on 03.09.2017.
 */

class AccountFragment : Fragment(R.layout.fragment_account_list), AccountRecyclerViewAdapter.ModifyRequestListener {

    private var _binding: FragmentAccountListBinding? = null
    private val binding get() = _binding!!

    lateinit var navController: NavController
    private val adapter: AccountRecyclerViewAdapter by lazy { AccountRecyclerViewAdapter(this) }
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAccountListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allAccounts.observe(viewLifecycleOwner, { accounts ->
                    adapter.setAccounts(accounts)
                    if (accounts.isNotEmpty()) {
                        binding.accountsList.visibility = View.VISIBLE
                        binding.emptyListInfo.visibility = View.GONE
                    } else {
                        binding.accountsList.visibility = View.GONE
                        binding.emptyListInfo.visibility = View.VISIBLE
                    }
                })
        navController = findNavController()
        binding.accountsList.layoutManager = LinearLayoutManager(context)
        binding.accountsList.adapter = adapter
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_addEditAccountFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onEditRequested(accountId: Int) {
        val action = AccountFragmentDirections.actionAccountFragmentToAddEditAccountFragment(accountId)
        navController.navigate(action)
    }
}
