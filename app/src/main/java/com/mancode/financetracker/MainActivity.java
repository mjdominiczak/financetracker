package com.mancode.financetracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mancode.financetracker.database.DBBackupRestore;
import com.mancode.financetracker.database.DatabaseContract;
import com.mancode.financetracker.notifications.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AccountFragment.OnListFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE   = 0;
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE    = 1;

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

        setAlarmForNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exportDB:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // TODO rationale for permissions
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    exportDbWithToasts();
                }
                return true;
            case R.id.action_restoreDB:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // TODO rationale for permissions
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {
                    restoreDbWithToasts();
                }
                return true;
//            case R.id.action_clearDB:
//                new ClearUriTask(getApplicationContext()).execute(
//                        DatabaseContract.AccountEntry.CONTENT_URI,
//                        DatabaseContract.BalanceEntry.CONTENT_URI,
//                        DatabaseContract.CategoryEntry.CONTENT_URI,
//                        DatabaseContract.CurrencyEntry.CONTENT_URI,
//                        DatabaseContract.TransactionEntry.CONTENT_URI
//                );
//                return true;

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

    private void restoreDbWithToasts() {
        if (DBBackupRestore.restore()) {
            Toast.makeText(this,
                    "Database restored from " + DBBackupRestore.EXPORT_DATABASE_FILE.getPath(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Database restore failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void exportDbWithToasts() {
        if (DBBackupRestore.export()) {
            Toast.makeText(this,
                    "Database exported to " + DBBackupRestore.EXPORT_DATABASE_FILE.getPath(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Database export failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportDbWithToasts();
                }
                break;
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restoreDbWithToasts();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onListFragmentInteraction() {
        // TODO
    }

    private void setAlarmForNotification() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_DAY,
                    pendingIntent);
        }
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