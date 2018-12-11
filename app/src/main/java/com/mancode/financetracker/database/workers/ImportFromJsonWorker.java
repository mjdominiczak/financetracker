package com.mancode.financetracker.database.workers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.json.DatabaseObject;

import java.io.FileNotFoundException;
import java.util.Scanner;

import androidx.annotation.NonNull;
import androidx.work.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.mancode.financetracker.database.DatabaseJson.JSON_FILE;

public class ImportFromJsonWorker extends Worker {

    private static final String TAG = ImportFromJsonWorker.class.getName();

    public ImportFromJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Gson gson = new GsonBuilder()
                .setDateFormat(DateConverter.DATE_FORMAT_STRING)
                .create();
        String json;

        try {
            if (JSON_FILE.exists()) {
                Scanner scanner = new Scanner(JSON_FILE);
                json = scanner.useDelimiter("\\Z").next();
                scanner.close();

                DatabaseObject databaseObject = gson.fromJson(json, DatabaseObject.class);
                if (FTDatabase.DATABASE_VERSION == databaseObject.version) {
                    FTDatabase database = FTDatabase.getInstance(getApplicationContext());
                    database.clearAllTables();
                    database.currencyDao().insertAll(databaseObject.currencyList);
                    database.categoryDao().insertAll(databaseObject.categoryList);
                    database.accountDao().insertAll(databaseObject.accountList);
                    database.balanceDao().insertAll(databaseObject.balanceList);
                    database.transactionDao().insertAll(databaseObject.transactionList);
                    return Result.success();
                } else {
                    Log.d(TAG, "Databases version does not match");
                }
            } else {
                Log.d(TAG, "Json file does not exist");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Json file not found!");
            e.printStackTrace();
        }
        return Result.failure();
    }
}
