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
import java.io.OutputStream;
import java.io.PrintWriter;

public class ExportToJsonWorker extends Worker {

    public static final String KEY_URI_ARG = "uri";
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
            Uri u = Uri.parse(getInputData().getString(KEY_URI_ARG));
            OutputStream outputStream = getApplicationContext().getContentResolver().openOutputStream(u);
            if (outputStream != null) {
                PrintWriter writer = new PrintWriter(outputStream);
                writer.write(json);
                writer.close();
                return Result.success();
            } else return Result.failure();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found!");
            e.printStackTrace();
            return Result.failure();
        }
    }
}
