package com.mancode.financetracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mancode.financetracker.R
import com.mancode.financetracker.database.entity.CategoryEntity

class CategoryListAdapter(private val listener: OnCategoryAddedListener) :
        ListAdapter<CategoryEntity, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_category_item, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_category_add, parent, false)
            AddViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            val item: CategoryEntity? = currentList[position]
            (holder as ViewHolder).categoryName.text = item?.category ?: "Brak"
        }
    }

    override fun getItemCount(): Int = currentList.size + 1

    private class DiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryEntity, newItem: CategoryEntity): Boolean {
            return areItemsTheSame(oldItem, newItem) &&
                    oldItem.categoryType == newItem.categoryType &&
                    oldItem.category == newItem.category
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
    }

    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val editText = view.findViewById<EditText>(R.id.addCategory)
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val name = editText.text.toString().trim()
                    val alreadyExists = currentList.map { it.category }.contains(name)
                    if (!name.isBlank() && !alreadyExists) {
                        listener.onCategoryAdded(name)
                        editText.clearFocus()
                        editText.text = null
                        Snackbar.make(view, "Category \"$name\" added", Snackbar.LENGTH_SHORT).show()
                        return@setOnEditorActionListener true
                    } else if (alreadyExists) {
                        editText.clearFocus()
                        editText.text = null
                        Snackbar.make(view, "Category \"$name\" already exists", Snackbar.LENGTH_SHORT).show()
                    }
                }
                false
            }
        }
    }

    interface OnCategoryAddedListener {
        fun onCategoryAdded(name: String)
    }
}