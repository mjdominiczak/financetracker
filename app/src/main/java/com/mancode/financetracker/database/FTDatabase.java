package com.mancode.financetracker.database;

import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

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
            version = FTDatabase.DATABASE_VERSION)
public abstract class FTDatabase extends RoomDatabase {

    private static FTDatabase sInstance;

    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 3;

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
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        new PopulateTask().execute(applicationContext);
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();
    }

    private static class PopulateTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... c) {
            if (c[0] != null) {
                prepopulateDb(FTDatabase.getInstance(c[0]));
            }
            return null;
        }

        private void prepopulateDb(FTDatabase db) {
            CurrencyDao currencyDao = db.currencyDao();
            if (currencyDao.count() == 0) {
                CurrencyEntity currency = new CurrencyEntity();

                db.beginTransaction();
                try {
                    currency.initFromValues(1, "PLN", 1.0);
                    currencyDao.insertCurrency(currency);
                    currency.initFromValues(2, "EUR", 4.2969);
                    currencyDao.insertCurrency(currency);
                    db.setTransactionSuccessful();
                    Log.d("AsyncTaskPrepopulate", "Transaction successful");
                } finally {
                    db.endTransaction();
                }

            }
        }
    }
}
