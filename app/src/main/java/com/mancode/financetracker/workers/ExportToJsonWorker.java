package com.mancode.financetracker.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mancode.financetracker.database.FTDatabase;
import com.mancode.financetracker.database.json.DatabaseObject;
import com.mancode.financetracker.database.json.LocalDateGsonAdapter;

import org.threeten.bp.LocalDate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
                .registerTypeAdapter(LocalDate.class, new LocalDateGsonAdapter())
                .create();

        FTDatabase database = FTDatabase.getInstance(getApplicationContext());
        DatabaseObject databaseObject = new DatabaseObject(
                FTDatabase.DATABASE_VERSION,
                database.currencyDao().getAllCurrenciesSimple(),
                database.categoryDao().getAllCategoriesSimple(),
                database.accountDao().getAllAccountsSimple(),
                database.balanceDao().getAllBalancesSimple(),
                database.transactionDao().getAllTransactionsSimple()
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
