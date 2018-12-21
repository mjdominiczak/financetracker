package com.mancode.financetracker.database.workers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.json.DatabaseObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.mancode.financetracker.database.DatabaseJson.JSON_FILE;

public class ExportToJsonWorker extends Worker {

    private static final String TAG = ExportToJsonWorker.class.getName();

    public ExportToJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Gson gson = new GsonBuilder()
                .setDateFormat(DateConverter.DATE_FORMAT_STRING)
                .create();

        FTDatabase database = FTDatabase.getInstance(getApplicationContext());
        DatabaseObject databaseObject = new DatabaseObject(
                FTDatabase.DATABASE_VERSION,
                database.currencyDao().getAllCurrencies(),
                database.categoryDao().getAllCategories(),
                database.accountDao().getAllAccounts(),
                database.balanceDao().getAllBalances(),
                database.transactionDao().getAllTransactions()
        );

        String json = gson.toJson(databaseObject);

        try {
            if (JSON_FILE.exists() && JSON_FILE.delete()) {
                Log.d(TAG, "File: " + JSON_FILE.getPath() + "already existed and has been successfully deleted.");
            }
            JSON_FILE.createNewFile();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(JSON_FILE)));
            writer.write(json);
            writer.close();
            return Result.success();
        } catch (IOException e) {
            Log.e(TAG, "Creating new file during export failed!");
            e.printStackTrace();
            return Result.failure();
        }
    }
}
