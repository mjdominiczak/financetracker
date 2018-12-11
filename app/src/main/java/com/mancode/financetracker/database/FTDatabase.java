package com.mancode.financetracker.database;

import android.content.Context;

import com.mancode.financetracker.database.dao.AccountDao;
import com.mancode.financetracker.database.dao.BalanceDao;
import com.mancode.financetracker.database.dao.CategoryDao;
import com.mancode.financetracker.database.dao.CurrencyDao;
import com.mancode.financetracker.database.dao.TransactionDao;
import com.mancode.financetracker.database.entity.AccountEntity;
import com.mancode.financetracker.database.entity.BalanceEntity;
import com.mancode.financetracker.database.entity.CategoryEntity;
import com.mancode.financetracker.database.entity.CurrencyEntity;
import com.mancode.financetracker.database.entity.TransactionEntity;
import com.mancode.financetracker.database.workers.PrepopulateDatabaseWorker;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
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
                        TransactionEntity.class},
            version = FTDatabase.DATABASE_VERSION)
public abstract class FTDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "database.db";
    private static FTDatabase sInstance;
    /**
     * Migration from:
     * version 1 - using SQLiteDatabase API
     * to
     * version 2 - using Room
     */
    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };
    /**
     * Migration from:
     * version 2
     * to
     * version 3 - using autoGenerate = true
     */
    private static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };
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
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();
    }

    public abstract AccountDao accountDao();

    public abstract BalanceDao balanceDao();

    public abstract CategoryDao categoryDao();

    public abstract CurrencyDao currencyDao();

    public abstract TransactionDao transactionDao();
}
