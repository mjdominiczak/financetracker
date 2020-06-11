package com.mancode.financetracker.ui

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mancode.financetracker.ui.transactions.TransactionRecyclerViewAdapter

class SwipeToDeleteCallback(private val adapter: TransactionRecyclerViewAdapter) : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT) {
    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val viewHolderCast = viewHolder as TransactionRecyclerViewAdapter.ViewHolder
        val transactionRemoved = viewHolderCast.mTransaction?.transaction
        adapter.modifyRequestListener.onDeleteRequested(transactionRemoved)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = (viewHolder as TransactionRecyclerViewAdapter.ViewHolder).foregroundView
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as TransactionRecyclerViewAdapter.ViewHolder).foregroundView
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
    }
}