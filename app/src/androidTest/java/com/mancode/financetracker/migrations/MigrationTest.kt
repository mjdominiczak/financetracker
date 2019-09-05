package com.mancode.financetracker.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.mancode.financetracker.database.FTDatabase
import com.mancode.financetracker.database.converter.DateConverter
import com.mancode.financetracker.database.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.io.IOException

class MigrationTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            FTDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    @Test
    @Throws(IOException::class)
    fun migrationFrom3To4_containsCorrectData() {

        val db = migrationTestHelper.createDatabase(TEST_DB_NAME, 3)
        insertTransactionV3(
                TRANSACTION.id, DateConverter.toString(TRANSACTION.date)!!, TRANSACTION.type,
                TRANSACTION.description, TRANSACTION.value, TRANSACTION.accountId, TRANSACTION.categoryId, db)
        db.close()

        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 4, true,
                FTDatabase.MIGRATION_3_4)

        val transaction = getMigratedDB().transactionDao().allTransactions[0]

        assertEquals(transaction.id, TRANSACTION.id)
        assertEquals(transaction.date, TRANSACTION.date)
        assertEquals(transaction.type, TRANSACTION.type)
        assertEquals(transaction.description, TRANSACTION.description)
        assertEquals(transaction.value, TRANSACTION.value, 0.0)
        assertEquals(transaction.flags, TRANSACTION.flags)
        assertEquals(transaction.accountId, TRANSACTION.categoryId)
    }

    private fun getMigratedDB(): FTDatabase {
        val db = Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
                FTDatabase::class.java, TEST_DB_NAME)
                .addMigrations(FTDatabase.MIGRATION_3_4)
                .build()
        migrationTestHelper.closeWhenFinished(db)
        return db
    }

    private fun insertTransactionV3(
            id: Int, date: String, type: Int, description: String, value: Double,
            accountId: Int, categoryId: Int, db: SupportSQLiteDatabase) {
        val values = ContentValues()
        values.put("_id", id)
        values.put("transaction_date", date)
        values.put("transaction_type", type)
        values.put("transaction_description", description)
        values.put("transaction_value", value)
        values.put("transaction_account", accountId)
        values.put("transaction_category", categoryId)

        db.insert("transactions", CONFLICT_REPLACE, values)
    }

    companion object {
        private const val TEST_DB_NAME = "test-db"

        private val TRANSACTION = TransactionEntity(
                1,
                LocalDate.of(1990, 1, 1),
                1,
                "description",
                1000.0,
                0,
                1,
                1
        )
    }

}