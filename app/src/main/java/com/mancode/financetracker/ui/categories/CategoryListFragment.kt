package com.mancode.financetracker.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity
import com.mancode.financetracker.database.entity.TransactionEntity
import com.mancode.financetracker.viewmodel.CategoryViewModel

class CategoryListFragment : Fragment(), CategoryListAdapter.ModifyRequestListener {

    private val categoryType: Int by lazy {
        arguments!!.getInt(ARG_CATEGORY_TYPE)
    }

    private val viewModel: CategoryViewModel by viewModels()
    private val adapter by lazy { CategoryListAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_list, container, false)
        with(view as RecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CategoryListFragment.adapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listToObserve =
                if (categoryType == TransactionEntity.TYPE_OUTCOME) viewModel.outcomeCategories
                else viewModel.incomeCategories
        listToObserve.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onCategoryAdded(name: String) {
        viewModel.insertCategory(name, categoryType)
    }

    override fun onCategoryModified(categoryEntity: CategoryEntity) {
        val layout = layoutInflater.inflate(R.layout.dialog_category_edit, null)
        val name = layout.findViewById<TextInputEditText>(R.id.categoryName)
        name.setText(categoryEntity.category)
        MaterialAlertDialogBuilder(context)
                .setView(layout)
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .setPositiveButton(R.string.toolbar_action_save) { _, _ ->
                    viewModel.updateCategory(CategoryEntity(categoryEntity.id, name.text.toString(),
                            categoryEntity.categoryType))
                }.show()
    }

    companion object {
        const val ARG_CATEGORY_TYPE = "category-type"

        @JvmStatic
        fun newInstance(categoryType: Int) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_CATEGORY_TYPE, categoryType)
                    }
                }
    }
}