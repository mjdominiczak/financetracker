package com.mancode.financetracker;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.database.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AccountFragment.OnListFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

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
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_clearDB) {
            clearUri(
                    DatabaseContract.AccountEntry.CONTENT_URI,
                    DatabaseContract.BalanceEntry.CONTENT_URI,
                    DatabaseContract.CategoryEntry.CONTENT_URI,
                    DatabaseContract.CurrencyEntry.CONTENT_URI,
                    DatabaseContract.TransactionEntry.CONTENT_URI
            );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearUri(final Uri... contentUris) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_SHORT).show();
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                for (Uri uri : contentUris) {
                    ops.add(ContentProviderOperation.newDelete(uri).build());
                }
                try {
                    getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onListFragmentInteraction(DatabaseHelper.AccountItem item) {
        // TODO
    }
}