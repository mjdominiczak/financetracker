package com.mancode.financetracker;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.mancode.financetracker.database.DatabaseContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manveru on 07.09.2017.
 */

public class AddBalanceFragment extends AddItemFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;

    private Date mDate;

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

        EditText dateText = (EditText) view.findViewById(R.id.tf_balance_check_date);
        updateDate(dateText, Calendar.getInstance().getTime());
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                EditText dateText = (EditText) view.findViewById(R.id.tf_balance_check_date);
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                updateDate(dateText, c.getTime());
            }
        };
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(
                        getActivity(),
                        // R.style.AppTheme, TODO - style datepicker
                        dateSetListener,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        Spinner accountSpinner = (Spinner) view.findViewById(R.id.spinner_balance_account);
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                null,
                new String[] {DatabaseContract.AccountEntry.COL_NAME},
                new int[] {android.R.id.text1},
                0
        );
        accountSpinner.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_balance_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_menu_save) {
                    String checkDate = new SimpleDateFormat(DatabaseContract.DATE_FORMAT_STRING, Locale.US).format(mDate);
                    int accountPos = ((Spinner) getView().findViewById(R.id.spinner_balance_account)).getSelectedItemPosition();
                    Cursor cursor = mAdapter.getCursor();
                    cursor.moveToPosition(accountPos);
                    int account = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AccountEntry._ID));
                    double balance = Double.parseDouble(((EditText) getView().findViewById(R.id.tf_balance)).getText().toString());
                    String fixed = ((EditText) getView().findViewById(R.id.tf_balance_fixed)).getText().toString();

                    if (BalanceListItem.validate(checkDate, account, balance, fixed)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.BalanceEntry.COL_CHECK_DATE, checkDate);
                        cv.put(DatabaseContract.BalanceEntry.COL_ACCOUNT_ID, account);
                        cv.put(DatabaseContract.BalanceEntry.COL_BALANCE, balance);
                        cv.put(DatabaseContract.BalanceEntry.COL_FIXED, fixed);
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
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void updateDate(EditText dateText, Date date) {
        mDate = date;
        DateFormat df = DateFormat.getDateInstance();
        if (dateText != null) {
            dateText.setText(df.format(mDate));
        }
    }
}
