package com.vittiq.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vittiq.android.data.dao.AccountCategoryDao
import com.vittiq.android.data.dao.AccountDao
import com.vittiq.android.data.dao.CurrencyRateDao
import com.vittiq.android.data.dao.TransactionDao
import com.vittiq.android.data.dao.UserProfileDao
import com.vittiq.android.data.model.Account
import com.vittiq.android.data.model.AccountCategory
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.Transaction
import com.vittiq.android.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountCategory::class,
        Account::class,
        Transaction::class,
        UserProfile::class,
        CurrencyRate::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VittiqDatabase : RoomDatabase() {

    abstract fun accountCategoryDao(): AccountCategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun currencyRateDao(): CurrencyRateDao

    companion object {
        @Volatile
        internal var INSTANCE: VittiqDatabase? = null

        // Constant IDs for default categories
        const val CATEGORY_ID_ACCOUNTS = "cat-accounts"
        const val CATEGORY_ID_CARDS = "cat-cards"
        const val CATEGORY_ID_CASH = "cat-cash"
        const val CATEGORY_ID_INVESTMENTS = "cat-investments"
        const val CATEGORY_ID_EWALLETS = "cat-ewallets"
        const val CATEGORY_ID_OTHERS = "cat-others"

        // Default initial account IDs
        const val ACCOUNT_ID_DEFAULT_ACCOUNTS = "acc-default-accounts"
        const val ACCOUNT_ID_DEFAULT_CARDS = "acc-default-cards"
        const val ACCOUNT_ID_DEFAULT_CASH = "acc-default-cash"
        const val ACCOUNT_ID_DEFAULT_INVESTMENTS = "acc-default-investments"
        const val ACCOUNT_ID_DEFAULT_EWALLETS = "acc-default-ewallets"
        const val ACCOUNT_ID_DEFAULT_OTHERS = "acc-default-others"

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
}

private class VittiqDatabaseCallback(
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        VittiqDatabase.INSTANCE?.let { database ->
            scope.launch(Dispatchers.IO) {
                populateCleanSlateData(database)
            }
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        VittiqDatabase.INSTANCE?.let { database ->
            scope.launch(Dispatchers.IO) {
                if (database.accountCategoryDao().getCount() == 0) {
                    populateCleanSlateData(database)
                }
            }
        }
    }
}

suspend fun populateCleanSlateData(database: VittiqDatabase) {
    val categoryDao = database.accountCategoryDao()
    val accountDao = database.accountDao()
    val userProfileDao = database.userProfileDao()
    val currencyRateDao = database.currencyRateDao()

    // 1. Seed 6 Core Account Categories
    val initialCategories = listOf(
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_ACCOUNTS,
            name = "Accounts",
            displayOrder = 1,
            isCustom = false
        ),
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_CARDS,
            name = "Cards",
            displayOrder = 2,
            isCustom = false
        ),
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_CASH,
            name = "Cash",
            displayOrder = 3,
            isCustom = false
        ),
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_INVESTMENTS,
            name = "Investments",
            displayOrder = 4,
            isCustom = false
        ),
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_EWALLETS,
            name = "e-Wallets",
            displayOrder = 5,
            isCustom = false
        ),
        AccountCategory(
            id = VittiqDatabase.CATEGORY_ID_OTHERS,
            name = "Others",
            displayOrder = 6,
            isCustom = false
        )
    )
    categoryDao.insertAll(initialCategories)

    // 2. Under each category, seed exactly ONE default Account sharing the category name with 0.00 balance
    val initialAccounts = listOf(
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_ACCOUNTS,
            categoryId = VittiqDatabase.CATEGORY_ID_ACCOUNTS,
            name = "Accounts",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_CARDS,
            categoryId = VittiqDatabase.CATEGORY_ID_CARDS,
            name = "Cards",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_CASH,
            categoryId = VittiqDatabase.CATEGORY_ID_CASH,
            name = "Cash",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_INVESTMENTS,
            categoryId = VittiqDatabase.CATEGORY_ID_INVESTMENTS,
            name = "Investments",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_EWALLETS,
            categoryId = VittiqDatabase.CATEGORY_ID_EWALLETS,
            name = "e-Wallets",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        ),
        Account(
            id = VittiqDatabase.ACCOUNT_ID_DEFAULT_OTHERS,
            categoryId = VittiqDatabase.CATEGORY_ID_OTHERS,
            name = "Others",
            initialBalance = 0.0,
            currentBalance = 0.0,
            isArchived = false
        )
    )
    accountDao.insertAll(initialAccounts)

    // 3. UserProfile
    val initialProfile = UserProfile(
        id = 1,
        firstName = "Kunal",
        lastName = "Khandelwal",
        handle = "@kunal.khandelwal",
        avatarInitial = "K"
    )
    userProfileDao.insert(initialProfile)

    // 4. Currency Rates
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

    // Clean-slate mandate: ZERO transactions seeded
}

