package com.mancode.financetracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.mancode.financetracker.database.DBUtils;
import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.ui.SetDateView;

/**
 * Created by Manveru on 07.09.2017.
 */

public class AddBalanceFragment extends AddItemFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SetDateView mCheckDate;
    private Spinner mAccountSpinner;
    private EditText mBalanceValue;

    private SimpleCursorAdapter mAccountSpinnerAdapter;

    private static final String[] ACCOUNTS_PROJECTION = {
            DatabaseContract.AccountEntry._ID,
            DatabaseContract.AccountEntry.COL_NAME
    };

    public AddBalanceFragment() {
    }

    public static AddBalanceFragment newInstance() {
        return new AddBalanceFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);

        mCheckDate = (SetDateView) view.findViewById(R.id.tf_balance_check_date);
        mAccountSpinner = (Spinner) view.findViewById(R.id.spinner_balance_account);
        mBalanceValue = (EditText) view.findViewById(R.id.tf_balance);

        mAccountSpinnerAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                null,
                new String[] {DatabaseContract.AccountEntry.COL_NAME},
                new int[] {android.R.id.text1},
                0
        );
        mAccountSpinner.setAdapter(mAccountSpinnerAdapter);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_balance_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_menu_save) {
                    String checkDate = DBUtils.formatDate(mCheckDate.getDate());
                    Cursor cursor = mAccountSpinnerAdapter.getCursor();
                    cursor.moveToPosition(mAccountSpinner.getSelectedItemPosition());
                    int account = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry._ID));
                    double balance = Double.parseDouble(mBalanceValue.getText().toString());

                    if (BalanceListItem.validate(checkDate, account, balance)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.BalanceEntry.COL_CHECK_DATE, checkDate);
                        cv.put(DatabaseContract.BalanceEntry.COL_ACCOUNT_ID, account);
                        cv.put(DatabaseContract.BalanceEntry.COL_BALANCE, balance);
                        getActivity().getContentResolver().insert(DatabaseContract.BalanceEntry.CONTENT_URI, cv);
                        dismiss();
                    }
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DatabaseContract.AccountEntry.CONTENT_URI;
        String sortOrder = ACCOUNTS_PROJECTION[1] + " ASC";
        return new CursorLoader(
                getActivity(),
                uri,
                ACCOUNTS_PROJECTION,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAccountSpinnerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccountSpinnerAdapter.swapCursor(null);
    }

}
