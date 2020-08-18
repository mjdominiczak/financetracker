package com.mancode.financetracker.workers;

import android.content.Context;
import android.net.Uri;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class ImportFromJsonWorker extends Worker {

    public static final String KEY_URI_ARG = "uri";
    private static final String TAG = ImportFromJsonWorker.class.getName();

    public ImportFromJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateGsonAdapter())
                .create();
        StringBuilder json = new StringBuilder();

        try {
            Uri u = Uri.parse(getInputData().getString(KEY_URI_ARG));
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(u);
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\z");
                while (scanner.hasNext()) {
                    json.append(scanner.next());
                }
                scanner.close();

                DatabaseObject databaseObject = gson.fromJson(json.toString(), DatabaseObject.class);
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
