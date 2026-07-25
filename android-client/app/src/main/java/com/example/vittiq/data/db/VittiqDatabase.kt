package com.example.vittiq.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Account::class, Transaction::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VittiqDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: VittiqDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VittiqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VittiqDatabase::class.java,
                    "vittiq_database"
                )
                    .addCallback(VittiqDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class VittiqDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.accountDao(), database.userProfileDao())
                }
            }
        }

        suspend fun populateInitialData(accountDao: AccountDao, userProfileDao: UserProfileDao) {
            // Seed Default User Profile
            userProfileDao.insertUserProfile(
                UserProfile(
                    id = "default_user_1",
                    username = "vittiq_member",
                    fullName = "Alex Vance",
                    photoUri = null
                )
            )

            // Seed Default Accounts
            val defaultAccounts = listOf(
                Account(
                    id = "cash_account_1",
                    name = "Main Cash Wallet",
                    type = AccountType.CASH,
                    initialBalance = 500.0,
                    currentBalance = 500.0
                ),
                Account(
                    id = "bank_account_1",
                    name = "Primary Checking",
                    type = AccountType.BANK_ACCOUNT,
                    initialBalance = 4500.0,
                    currentBalance = 4500.0
                ),
                Account(
                    id = "card_account_1",
                    name = "Premium Credit Card",
                    type = AccountType.CARD,
                    initialBalance = -250.0,
                    currentBalance = -250.0
                ),
                Account(
                    id = "investment_account_1",
                    name = "Growth Portfolio",
                    type = AccountType.INVESTMENT,
                    initialBalance = 12500.0,
                    currentBalance = 12500.0
                )
            )
            defaultAccounts.forEach { account ->
                accountDao.insertAccount(account)
            }
        }
    }
}
