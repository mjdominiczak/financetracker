package com.mancode.financetracker.database.workers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.converter.DateConverter;
import com.mancode.financetracker.database.json.DatabaseObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PrepopulateDatabaseWorker extends Worker {

    private static final String TAG = PrepopulateDatabaseWorker.class.getName();

    private static final String fileName = "db_stub.json";

    public PrepopulateDatabaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Gson gson = new GsonBuilder()
                .setDateFormat(DateConverter.DATE_FORMAT_STRING)
                .create();
        JsonReader reader;

        try {
            InputStream in = getApplicationContext().getAssets().open(fileName);
            reader = new JsonReader(new InputStreamReader(in));
            DatabaseObject stub = gson.fromJson(reader, DatabaseObject.class);
            reader.close();
            if (FTDatabase.DATABASE_VERSION == stub.version) {
                FTDatabase database = FTDatabase.getInstance(getApplicationContext());
                database.clearAllTables();
                database.currencyDao().insertAll(stub.currencyList);
                database.categoryDao().insertAll(stub.categoryList);
                database.accountDao().insertAll(stub.accountList);
                database.balanceDao().insertAll(stub.balanceList);
                database.transactionDao().insertAll(stub.transactionList);
                return Result.success();
            } else {
                Log.e(TAG, "Wrong stub database version!");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error seeding database");
        }

        return Result.failure();
    }
}
