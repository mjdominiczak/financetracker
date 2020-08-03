package com.mancode.financetracker.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
import com.mancode.financetracker.workers.UpdateStateWorker;

/**
 * Created by Manveru on 31.01.2018.
 */

@Database(entities = {AccountEntity.class,
        BalanceEntity.class,
        CategoryEntity.class,
        CurrencyEntity.class,
        TransactionEntity.class,
        NetValue.class},
        views = {AccountExtended.class},
        version = FTDatabase.DATABASE_VERSION)
public abstract class FTDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "database.db";
    private static FTDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    /**
     * Migration from version 1 to version 2:
     * Introducing net_values table
     */
    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `net_values` " +
                    "(`date` TEXT NOT NULL, `value` REAL NOT NULL, `calculated` INTEGER NOT NULL, " +
                    "`complete` INTEGER NOT NULL, PRIMARY KEY(`date`))");
        }
    };

    /**
     * Migration from version 2 to version 3:
     * TODO
     */
    private static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("PRAGMA foreign_keys=off;");
                db.execSQL("ALTER TABLE currencies RENAME TO old;");
                db.execSQL("CREATE TABLE currencies (" +
                        "    currency_symbol TEXT NOT NULL PRIMARY KEY," +
                        "    currency_exchange_rate DOUBLE NOT NULL DEFAULT 1.0," +
                        "    currency_rate_date TEXT" +
                        ");");
                db.execSQL("INSERT INTO currencies(currency_symbol, currency_exchange_rate) " +
                        "SELECT currency_symbol, currency_exchange_rate FROM old;");
                db.execSQL("DROP TABLE old;");
                db.execSQL("PRAGMA foreign_keys=on;");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    };

    @VisibleForTesting
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("PRAGMA foreign_keys=off;");
                db.execSQL("ALTER TABLE accounts RENAME TO old;");
                db.execSQL("CREATE TABLE accounts (" +
                        "    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "    account_name TEXT NOT NULL," +
                        "    account_type INTEGER NOT NULL," +
                        "    account_currency TEXT NOT NULL," +
                        "    account_open_date TEXT NOT NULL," +
                        "    account_close_date TEXT," +
                        "    FOREIGN KEY(account_currency) REFERENCES currencies(currency_symbol)" +
                        "    ON UPDATE NO ACTION ON DELETE NO ACTION" +
                        ");");
                db.execSQL("INSERT INTO accounts " +
                        "SELECT _id, account_name, account_type, account_currency, " +
                        "account_open_date, account_close_date FROM old;");
                db.execSQL("DROP TABLE old;");

                db.execSQL("ALTER TABLE balances RENAME TO old;");
                db.execSQL("CREATE TABLE balances (" +
                        "    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "    balance_check_date TEXT NOT NULL," +
                        "    balance_account_id INTEGER NOT NULL," +
                        "    balance_value REAL NOT NULL," +
                        "    balance_fixed INTEGER NOT NULL," +
                        "    FOREIGN KEY(balance_account_id) REFERENCES accounts(_id)" +
                        "    ON UPDATE NO ACTION ON DELETE NO ACTION" +
                        ");");
                db.execSQL("INSERT INTO balances " +
                        "SELECT _id, balance_check_date, balance_account_id, balance_value, " +
                        "balance_fixed FROM old;");
                db.execSQL("DROP TABLE old;");

                db.execSQL("ALTER TABLE categories RENAME TO old;");
                db.execSQL("CREATE TABLE categories (" +
                        "    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "    category TEXT NOT NULL," +
                        "    category_type INTEGER NOT NULL)");
                db.execSQL("INSERT INTO categories " +
                        "SELECT _id, category, category_type FROM old;");
                db.execSQL("DROP TABLE old;");

                db.execSQL("ALTER TABLE transactions RENAME TO old;");
                db.execSQL("CREATE TABLE transactions (" +
                        "    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "    transaction_date TEXT NOT NULL," +
                        "    transaction_type INTEGER NOT NULL," +
                        "    transaction_description TEXT NOT NULL," +
                        "    transaction_value REAL NOT NULL," +
                        "    transaction_flags INTEGER NOT NULL DEFAULT 0," +
                        "    transaction_account INTEGER NOT NULL," +
                        "    transaction_category INTEGER NOT NULL," +
                        "    FOREIGN KEY(transaction_account) REFERENCES accounts(_id)" +
                        "    ON UPDATE NO ACTION ON DELETE NO ACTION," +
                        "    FOREIGN KEY(transaction_category) REFERENCES categories(_id)" +
                        "    ON UPDATE NO ACTION ON DELETE NO ACTION" +
                        ");");
                db.execSQL("INSERT INTO transactions(_id, transaction_date, transaction_type, " +
                        "transaction_description, transaction_value, transaction_account, transaction_category) " +
                        "SELECT _id, transaction_date, transaction_type, transaction_description, " +
                        "transaction_value, transaction_account, transaction_category FROM old;");
                db.execSQL("DROP TABLE old;");
                db.execSQL("PRAGMA foreign_keys=on;");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    };

    /**
     * Migration from version 4 to version 5:
     * - AccountExtended view modified
     */
    private static Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("PRAGMA foreign_keys=off;");
                db.execSQL("DROP VIEW accounts_view;");
                db.execSQL("CREATE VIEW `accounts_view` AS SELECT accounts._id, account_name, " +
                        "account_type, account_currency, account_close_date, balance_check_date, " +
                        "balance_value, " +
                        "(SELECT balances._id FROM balances WHERE balance_account_id = accounts._id " +
                        "ORDER BY date(balance_check_date) DESC LIMIT 1) AS balance_id " +
                        "FROM accounts LEFT JOIN balances ON balance_id = balances._id");
                db.execSQL("PRAGMA foreign_keys=on;");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    };

    /**
     * Migration from version 5 to version 6:
     * - added columns hidden and position in categories table
     */
    private static Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("PRAGMA foreign_keys=off;");
                db.execSQL("ALTER TABLE categories ADD COLUMN hidden INTEGER NOT NULL DEFAULT 0");
                db.execSQL("ALTER TABLE categories ADD COLUMN position INTEGER");
                db.execSQL("PRAGMA foreign_keys=on;");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
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
                .addMigrations(MIGRATION_3_4)
                .addMigrations(MIGRATION_4_5)
                .addMigrations(MIGRATION_5_6)
                .addCallback(new Callback() {
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        OneTimeWorkRequest request =
                                new OneTimeWorkRequest.Builder(UpdateStateWorker.class).build();
                        WorkManager.getInstance(applicationContext).enqueue(request);
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
