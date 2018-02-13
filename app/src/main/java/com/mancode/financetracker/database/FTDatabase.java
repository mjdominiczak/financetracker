package com.mancode.financetracker.database;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

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

/**
 * Created by Manveru on 31.01.2018.
 */

@Database   (entities = {AccountEntity.class,
                        BalanceEntity.class,
                        CategoryEntity.class,
                        CurrencyEntity.class,
                        TransactionEntity.class},
            version = 3)
public abstract class FTDatabase extends RoomDatabase {

    private static FTDatabase sInstance;

    public static final String DATABASE_NAME = "database.db";

    public abstract AccountDao accountDao();
    public abstract BalanceDao balanceDao();
    public abstract CategoryDao categoryDao();
    public abstract CurrencyDao currencyDao();
    public abstract TransactionDao transactionDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

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
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();
    }
}
