package com.mancode.financetracker.database;

import android.os.Environment;
import android.util.Log;

import com.mancode.financetracker.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Manveru on 27.12.2017.
 */

public class DBBackupRestore {

    private static final String TAG = DBBackupRestore.class.getName();

    protected static final File DATABASE_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(), "FinanceTracker");

    protected static final String EXPORT_FILENAME = "ft.db";

    public static final File EXPORT_DATABASE_FILE =
            new File(DATABASE_DIRECTORY, EXPORT_FILENAME);

    private static final String PACKAGE_NAME = MainActivity.class.getPackage().getName();

    private static final File DATA_DATABASE_FILE =
            new File(Environment.getDataDirectory() +
                    "/data/" + PACKAGE_NAME +
                    "/databases/" + FTDatabase.DATABASE_NAME);

    public static boolean export() {
        if (!sdMounted()) return false;

        if (!DATABASE_DIRECTORY.exists()) {
            DATABASE_DIRECTORY.mkdirs();
        }

        try {
            EXPORT_DATABASE_FILE.createNewFile();
            copyFile(DATA_DATABASE_FILE, EXPORT_DATABASE_FILE);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Creating new database file during export failed");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean restore() {
        if (!sdMounted()) return false;

        File importFile = EXPORT_DATABASE_FILE;

        // TODO check if importFile is valid

        if (!importFile.exists()) {
            Log.d(TAG, "Import file does not exist");
            return false;
        }

        try {
            if (!DATA_DATABASE_FILE.createNewFile()) {
                if (!DATA_DATABASE_FILE.delete()) {
                    Log.d(TAG, "Database file already exists and could not be deleted");
                } else {
                    Log.d(TAG, "Database file already existed but could be deleted");
                }
            }
            copyFile(importFile, DATA_DATABASE_FILE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private static boolean sdMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
