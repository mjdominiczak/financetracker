package com.mancode.financetracker.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.TransactionEntity
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerCategories.adapter = CategoryPagerAdapter(this)
        TabLayoutMediator(tabLayoutCategories, pagerCategories) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.outcome)
                1 -> getString(R.string.income)
                else -> throw IllegalStateException("Wrong number of tabs")
            }
        }.attach()
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