package com.mancode.financetracker.ui.balances;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancode.financetracker.R;
import com.mancode.financetracker.database.viewmodel.BalanceViewModel;
import com.mancode.financetracker.ui.UIUtils;

/**
 * Created by Manveru on 03.09.2017.
 */

public class BalanceFragment extends Fragment {

    private BalanceRecyclerViewAdapter mAdapter;

    public BalanceFragment() { }

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BalanceViewModel balanceViewModel =
                ViewModelProviders.of(getActivity()).get(BalanceViewModel.class);
        balanceViewModel.getNetValues().observe(this,
                netValues -> mAdapter.submitList(netValues));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false); // TODO layout to change?
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            rView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new BalanceRecyclerViewAdapter();
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> UIUtils.showFullScreenDialog(
                getFragmentManager(), AddBalanceFragment.newInstance()));
        return view;
    }
}
