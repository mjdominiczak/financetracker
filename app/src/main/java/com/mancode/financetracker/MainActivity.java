package com.mancode.financetracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mancode.financetracker.database.DatabaseJson;
import com.mancode.financetracker.database.viewmodel.BalanceViewModel;
import com.mancode.financetracker.notifications.ReminderNotificationReceiver;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE   = 0;
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE    = 1;

    public static final String CHANNEL_ID_REMINDER = "channel_reminder";
    public static final String EXTRA_VISIBLE_FRAGMENT = "visible_fragment";

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab3_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab4_name));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.pager);
        final MyTabsPagerAdapter adapter = new MyTabsPagerAdapter(
                (getSupportFragmentManager()));
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

        int visibleFragment = getIntent().getIntExtra(EXTRA_VISIBLE_FRAGMENT, MyTabsPagerAdapter.ACCOUNT_FRAGMENT);
        viewPager.setCurrentItem(visibleFragment);

        setAlarmForNotification();
        createNotificationChannel();
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
            case R.id.action_clearLastBalance:
                BalanceViewModel model = ViewModelProviders.of(this).get(BalanceViewModel.class);
                model.removeLastBalance();
                Toast.makeText(getApplicationContext(), "Last balance removed", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreDbWithToasts() {
        if (DatabaseJson.importJson()) {
            Toast.makeText(this,
                    "Import started with " + DatabaseJson.JSON_FILE.getPath(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Database restore failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void exportDbWithToasts() {
        if (DatabaseJson.exportJson()) {
            Toast.makeText(this,
                    "Database exported to " + DatabaseJson.JSON_FILE.getPath(),
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

    private void setAlarmForNotification() {
        Intent intent = new Intent(this, ReminderNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_name_transaction_input_reminder);
            String desc = getString(R.string.channel_desc_transaction_input_reminder);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REMINDER, name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}