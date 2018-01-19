package com.mancode.financetracker.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mancode.financetracker.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle("FT Reminder")
                .setContentText("Any transactions to enter?")
                .setSmallIcon(R.drawable.ic_launcher_mono)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }
}
