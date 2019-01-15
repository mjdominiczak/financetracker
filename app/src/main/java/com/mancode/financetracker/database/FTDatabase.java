package com.mancode.financetracker.database;

import android.content.Context;

import com.mancode.financetracker.database.dao.AccountDao;
import com.mancode.financetracker.database.dao.BalanceDao;
import com.mancode.financetracker.database.dao.CategoryDao;
import com.mancode.financetracker.database.dao.CurrencyDao;
import com.mancode.financetracker.database.dao.NetValueDao;
import com.mancode.financetracker.database.dao.TransactionDao;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.CurrencyEntity;
import com.mancode.financetracker.database.entity.NetValue;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.views.AccountExtended;
import com.mancode.financetracker.database.workers.PrepopulateDatabaseWorker;
import com.mancode.financetracker.database.workers.UpdateStateWorker;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by Manveru on 31.01.2018.
 */

@Database   (entities = {AccountEntity.class,
                        BalanceEntity.class,
                        CategoryEntity.class,
                        CurrencyEntity.class,
                        TransactionEntity.class,
                        NetValue.class},
            views = {AccountExtended.class},
            version = FTDatabase.DATABASE_VERSION)
public abstract class FTDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";
    private static FTDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static FTDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (FTDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static FTDatabase buildDatabase(final Context applicationContext) {
        return Room.databaseBuilder(applicationContext, FTDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        OneTimeWorkRequest request =
                                new OneTimeWorkRequest.Builder(PrepopulateDatabaseWorker.class).build();
                        WorkManager.getInstance().enqueue(request);
                    }
                })
                .addCallback(new Callback() {
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        OneTimeWorkRequest request =
                                new OneTimeWorkRequest.Builder(UpdateStateWorker.class).build();
                        WorkManager.getInstance().enqueue(request);
                    }
                })
                .build();
    }

    public abstract AccountDao accountDao();

    public abstract BalanceDao balanceDao();

    public abstract CategoryDao categoryDao();

    public abstract CurrencyDao currencyDao();

    public abstract TransactionDao transactionDao();

    public abstract NetValueDao netValueDao();
}
