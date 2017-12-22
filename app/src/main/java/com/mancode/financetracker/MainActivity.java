package com.mancode.financetracker;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mancode.financetracker.database.DatabaseContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AccountFragment.OnListFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3_name));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final MyTabsPagerAdapter adapter = new MyTabsPagerAdapter(
                (getFragmentManager()));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_clearDB:
                new ClearUriTask(getApplicationContext()).execute(
                        DatabaseContract.AccountEntry.CONTENT_URI,
                        DatabaseContract.BalanceEntry.CONTENT_URI,
                        DatabaseContract.CategoryEntry.CONTENT_URI,
                        DatabaseContract.CurrencyEntry.CONTENT_URI,
                        DatabaseContract.TransactionEntry.CONTENT_URI
                );
                return true;
            case R.id.action_clearLastBalance:
                Cursor cursor = getContentResolver().query(
                        DatabaseContract.BalanceEntry.CONTENT_URI,
                        new String[]{DatabaseContract.BalanceEntry._ID, DatabaseContract.BalanceEntry.COL_CHECK_DATE},
                        null,
                        null,
                        DatabaseContract.BalanceEntry._ID + " DESC LIMIT 1"
                );
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.BalanceEntry._ID));
                        getContentResolver().delete(
                                DatabaseContract.BalanceEntry.CONTENT_URI,
                                DatabaseContract.BalanceEntry._ID + " =?",
                                new String[]{Integer.toString(id)}
                        );
                        BalanceFragment fragment = (BalanceFragment) ((MyTabsPagerAdapter) viewPager.getAdapter()).getRegisteredFragment(viewPager.getCurrentItem());
                        if (fragment != null) {
                            fragment.syncAdapterWithCursor();
                        }
                        Toast.makeText(getApplicationContext(), "Balance of id " + id + " removed", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction() {
        // TODO
    }

    private static class ClearUriTask extends AsyncTask<Uri, Void, Void> {

        private Context mContext;

        ClearUriTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mContext, "Uris cleared", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Uri... params) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            for (Uri uri : params) {
                ops.add(ContentProviderOperation.newDelete(uri).build());
            }
            try {
                mContext.getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

}