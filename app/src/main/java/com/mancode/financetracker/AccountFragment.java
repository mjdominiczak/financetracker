package com.mancode.financetracker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.database.DatabaseHelper;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AccountFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private AccountRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    // TODO: Customize parameter initialization
    public static AccountFragment newInstance(int columnCount) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);
        View recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView rView = (RecyclerView) recyclerView;
            if (mColumnCount <= 1) {
                rView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                rView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new AccountRecyclerViewAdapter(getActivity(), null); // TODO check null
            rView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAccountDialog();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    static final String[] ACCOUNTS_PROJECTION = new String[]{
            DatabaseContract.AccountEntry._ID,
            DatabaseContract.AccountEntry.COLUMN_NAME_NAME,
            DatabaseContract.AccountEntry.COLUMN_NAME_TYPE
    };

    // TODO
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DatabaseContract.AccountEntry.CONTENT_URI;
        String select = "(" + DatabaseContract.AccountEntry.COLUMN_NAME_NAME + " NOTNULL)";
        return new CursorLoader(
                getActivity(),
                uri,
                ACCOUNTS_PROJECTION,
                select,
                null,
                DatabaseContract.AccountEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DatabaseHelper.AccountItem item);
    }

    public void showAddAccountDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        AddAccountFragment addAccountFragment = new AddAccountFragment();

//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.addToBackStack(null);
//        addAccountFragment.show(transaction, "addAccountFragment");
//
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.add(android.R.id.content, addAccountFragment).addToBackStack(null).commit();
    }
}
