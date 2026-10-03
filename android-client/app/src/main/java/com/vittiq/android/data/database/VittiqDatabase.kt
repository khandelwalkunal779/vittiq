package com.vittiq.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vittiq.android.data.dao.AccountDao
import com.vittiq.android.data.dao.CurrencyRateDao
import com.vittiq.android.data.dao.TransactionDao
import com.vittiq.android.data.dao.UserProfileDao
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountType
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.TransactionType
import com.vittiq.android.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@Database(
    entities = [Account::class, Transaction::class, UserProfile::class, CurrencyRate::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VittiqDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun currencyRateDao(): CurrencyRateDao

    companion object {
        @Volatile
        private var INSTANCE: VittiqDatabase? = null

        // Constant IDs for default accounts so transactions can establish foreign key links
        const val ACCOUNT_ID_BANK = "acc-bank-checking"
        const val ACCOUNT_ID_CASH = "acc-cash-wallet"
        const val ACCOUNT_ID_CARDS = "acc-cards-credit"
        const val ACCOUNT_ID_INVESTMENTS = "acc-investments"
        const val ACCOUNT_ID_EWALLETS = "acc-ewallets"
        const val ACCOUNT_ID_OTHERS = "acc-others"

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): VittiqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VittiqDatabase::class.java,
                    "vittiq.db"
                )
                    .fallbackToDestructiveMigration(true)
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
                    populateInitialData(database)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    if (database.accountDao().getCount() == 0) {
                        populateInitialData(database)
                    }
                }
            }
        }
    }
}

suspend fun populateInitialData(database: VittiqDatabase) {
    val accountDao = database.accountDao()
    val transactionDao = database.transactionDao()
    val userProfileDao = database.userProfileDao()
    val currencyRateDao = database.currencyRateDao()

    // 1. Pre-populate Accounts matching Vittiq (Home).png
    val initialAccounts = listOf(
        Account(
            id = VittiqDatabase.ACCOUNT_ID_BANK,
            name = "Accounts",
            type = AccountType.BANK_ACCOUNT,
            currentBalance = 8430.00,
            subcategoriesCount = 4,
            isProfit = true
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_CASH,
            name = "Cash",
            type = AccountType.CASH,
            currentBalance = 14200.00,
            subcategoriesCount = 2,
            isProfit = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_CARDS,
            name = "Cards",
            type = AccountType.CARD,
            currentBalance = 8430.00,
            subcategoriesCount = 4,
            isProfit = true
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_INVESTMENTS,
            name = "Investments",
            type = AccountType.INVESTMENT,
            currentBalance = 8430.00,
            subcategoriesCount = 4,
            isProfit = true
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_EWALLETS,
            name = "e-Wallets",
            type = AccountType.E_WALLET,
            currentBalance = 8430.00,
            subcategoriesCount = 4,
            isProfit = true
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_OTHERS,
            name = "Others",
            type = AccountType.OTHER,
            currentBalance = 8430.00,
            subcategoriesCount = 4,
            isProfit = true
        )
    )
    accountDao.insertAll(initialAccounts)

    // Helper to construct exact timestamps
    fun getTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // 2. Pre-populate Transactions matching Vittiq (Logs).png
    // We add both July 2026 transactions and current month transactions so both display immediately
    val now = Calendar.getInstance()
    val curYear = now.get(Calendar.YEAR)
    val curMonth = now.get(Calendar.MONTH) + 1
    val curDay = now.get(Calendar.DAY_OF_MONTH)

    val transactionsToInsert = mutableListOf<Transaction>()

    // July 2026 transactions matching mockup
    transactionsToInsert.addAll(
        listOf(
            Transaction(
                id = "tx-1-chipotle",
                timestamp = getTimestamp(2026, 7, 9, 13, 15),
                accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                name = "Chipotle",
                category = "Food & Dining",
                amount = 13.50,
                type = TransactionType.DEBIT,
                description = "Chase Checking"
            ),
            Transaction(
                id = "tx-2-paycheck",
                timestamp = getTimestamp(2026, 7, 9, 9, 0),
                accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                name = "Paycheck",
                category = "Income",
                amount = 2620.00,
                type = TransactionType.CREDIT,
                description = "Payroll Direct Deposit"
            ),
            Transaction(
                id = "tx-3-wholefoods",
                timestamp = getTimestamp(2026, 7, 8, 17, 30),
                accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                name = "Whole Foods",
                category = "Groceries",
                amount = 87.34,
                type = TransactionType.DEBIT,
                description = "Chase Checking"
            ),
            Transaction(
                id = "tx-4-uberpool",
                timestamp = getTimestamp(2026, 7, 8, 8, 45),
                accountId = VittiqDatabase.ACCOUNT_ID_CARDS,
                name = "UberPool",
                category = "Transportation",
                amount = 42.00,
                type = TransactionType.DEBIT,
                description = "Chase Credit"
            ),
            Transaction(
                id = "tx-5-starbucks",
                timestamp = getTimestamp(2026, 7, 7, 8, 15),
                accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                name = "Starbucks",
                category = "Coffee",
                amount = 8.75,
                type = TransactionType.DEBIT,
                description = "Chase Checking"
            ),
            Transaction(
                id = "tx-6-rentrefund",
                timestamp = getTimestamp(2026, 7, 6, 14, 20),
                accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                name = "Rent Refund",
                category = "Housing",
                amount = 1250.00,
                type = TransactionType.CREDIT,
                description = "Chase Savings"
            )
        )
    )

    // If current date is not July 2026, duplicate entries for current month so home & logs immediately show data
    if (!(curYear == 2026 && curMonth == 7)) {
        transactionsToInsert.addAll(
            listOf(
                Transaction(
                    id = "tx-cur-1-chipotle",
                    timestamp = getTimestamp(curYear, curMonth, curDay, 13, 15),
                    accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                    name = "Chipotle",
                    category = "Food & Dining",
                    amount = 13.50,
                    type = TransactionType.DEBIT,
                    description = "Chase Checking"
                ),
                Transaction(
                    id = "tx-cur-2-paycheck",
                    timestamp = getTimestamp(curYear, curMonth, curDay, 9, 0),
                    accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                    name = "Paycheck",
                    category = "Income",
                    amount = 2620.00,
                    type = TransactionType.CREDIT,
                    description = "Payroll Direct Deposit"
                ),
                Transaction(
                    id = "tx-cur-3-wholefoods",
                    timestamp = getTimestamp(curYear, curMonth, if (curDay > 1) curDay - 1 else 1, 17, 30),
                    accountId = VittiqDatabase.ACCOUNT_ID_BANK,
                    name = "Whole Foods",
                    category = "Groceries",
                    amount = 87.34,
                    type = TransactionType.DEBIT,
                    description = "Chase Checking"
                ),
                Transaction(
                    id = "tx-cur-4-uberpool",
                    timestamp = getTimestamp(curYear, curMonth, if (curDay > 1) curDay - 1 else 1, 8, 45),
                    accountId = VittiqDatabase.ACCOUNT_ID_CARDS,
                    name = "UberPool",
                    category = "Transportation",
                    amount = 42.00,
                    type = TransactionType.DEBIT,
                    description = "Chase Credit"
                )
            )
        )
    }

    transactionDao.insertAll(transactionsToInsert)

    // 3. Pre-populate UserProfile
    val initialProfile = UserProfile(
        id = 1,
        firstName = "Kunal",
        lastName = "Khandelwal",
        handle = "@kunal.khandelwal",
        avatarInitial = "K"
    )
    userProfileDao.insert(initialProfile)

    // 4. Pre-populate Currency Rates
    val initialRates = listOf(
        CurrencyRate(currencyCode = "INR", symbol = "₹", rateToInr = 1.0),
        CurrencyRate(currencyCode = "USD", symbol = "$", rateToInr = 83.50),
        CurrencyRate(currencyCode = "EUR", symbol = "€", rateToInr = 90.20),
        CurrencyRate(currencyCode = "GBP", symbol = "£", rateToInr = 106.00),
        CurrencyRate(currencyCode = "AED", symbol = "AED", rateToInr = 22.75),
        CurrencyRate(currencyCode = "JPY", symbol = "¥", rateToInr = 0.55),
        CurrencyRate(currencyCode = "CAD", symbol = "C$", rateToInr = 61.20)
    )
    currencyRateDao.insertAll(initialRates)
}

