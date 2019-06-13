package com.mancode.financetracker.database;

import android.os.Environment;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.mancode.financetracker.workers.ExportToJsonWorker;
import com.mancode.financetracker.workers.ImportFromJsonWorker;

import java.io.File;

public class DatabaseJson {

    private static final String TAG = DatabaseJson.class.getName();

    private static final File EXPORT_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(), "FinanceTracker");

    private static final String JSON_FILENAME = "ft_dump.json";

    public static final File JSON_FILE =
            new File(EXPORT_DIRECTORY, JSON_FILENAME);

    public static boolean exportJson() {
        if (!sdMounted()) {
            Log.d(TAG, "Storage not mounted.");
            return false;
        }
        if (!EXPORT_DIRECTORY.exists()) {
            EXPORT_DIRECTORY.mkdirs();
        }
        OneTimeWorkRequest exportRequest = new OneTimeWorkRequest.Builder(ExportToJsonWorker.class).build();
        WorkManager.getInstance().enqueue(exportRequest);
        return true;
    }

    public static boolean importJson() {
        if (!sdMounted()) {
            Log.d(TAG, "Storage not mounted.");
            return false;
        }
        if (!JSON_FILE.exists()) {
            Log.d(TAG, "No json file to import found.");
            return false;
        }
        OneTimeWorkRequest importRequest = new OneTimeWorkRequest.Builder(ImportFromJsonWorker.class).build();
        WorkManager.getInstance().enqueue(importRequest);
        return true;
    }

        private static boolean sdMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
