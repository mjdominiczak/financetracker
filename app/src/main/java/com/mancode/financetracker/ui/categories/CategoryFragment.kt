package com.mancode.financetracker.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pagerCategories.adapter = CategoryPagerAdapter(this)
        TabLayoutMediator(binding.tabLayoutCategories, binding.pagerCategories) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.outcome)
                1 -> getString(R.string.income)
                else -> throw IllegalStateException("Wrong number of tabs")
            }
        }.attach()

        val navController = findNavController()
        val appBarConfig = AppBarConfiguration(navController.graph)
        binding.categoriesToolbar.setupWithNavController(navController, appBarConfig)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    class CategoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CategoryListFragment.newInstance(TransactionEntity.TYPE_OUTCOME)
            1 -> CategoryListFragment.newInstance(TransactionEntity.TYPE_INCOME)
            else -> throw IllegalStateException("Wrong number of tabs")
        }
    }
}