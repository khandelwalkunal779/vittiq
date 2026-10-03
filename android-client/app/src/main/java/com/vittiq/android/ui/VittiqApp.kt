package com.vittiq.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vittiq.android.data.repository.VittiqRepository
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.ui.accounts.AllAccountsScreen
import com.vittiq.android.ui.accounts.AllAccountsViewModel
import com.vittiq.android.ui.ai.AiInsightsScreen
import com.vittiq.android.ui.components.AddTransactionSheet
import com.vittiq.android.ui.components.VittiqBottomNavBar
import com.vittiq.android.ui.components.VittiqTab
import com.vittiq.android.ui.components.VittiqTopAppBar
import com.vittiq.android.ui.home.HomeScreen
import com.vittiq.android.ui.home.HomeViewModel
import com.vittiq.android.ui.logs.LogsScreen
import com.vittiq.android.ui.logs.LogsViewModel
import com.vittiq.android.ui.profile.ProfileScreen
import com.vittiq.android.ui.profile.ProfileViewModel
import com.vittiq.android.ui.settings.ManageAccountsScreen
import com.vittiq.android.ui.settings.ManageTransactionCategoriesScreen
import com.vittiq.android.ui.settings.SettingsScreen
import com.vittiq.android.ui.settings.SettingsViewModel
import com.vittiq.android.ui.shared.SharedScreen
import kotlinx.coroutines.flow.flowOf

sealed interface ScreenRoute {
    data object Main : ScreenRoute
    data class AllAccounts(val focusCategoryId: String? = null) : ScreenRoute
    data object Settings : ScreenRoute
    data object ManageAccounts : ScreenRoute
    data object ManageTransactionCategories : ScreenRoute
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VittiqApp(
    repository: VittiqRepository,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(VittiqTab.HOME) }
    var currentRoute by remember { mutableStateOf<ScreenRoute>(ScreenRoute.Main) }
    var isAddSheetOpen by remember { mutableStateOf(false) }

    // Search state
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by remember(searchQuery) {
        if (searchQuery.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchTransactions(searchQuery.trim())
        }
    }.collectAsState(initial = emptyList())

    // ViewModels
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
    val logsViewModel: LogsViewModel = viewModel(factory = LogsViewModel.Factory(repository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))
    val allAccountsViewModel: AllAccountsViewModel = viewModel(factory = AllAccountsViewModel.Factory(repository))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(repository))

    val homeState by homeViewModel.uiState.collectAsState()
    val logsState by logsViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val allAccountsState by allAccountsViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    val userName = profileState.userProfile?.firstName ?: "Kunal"

    // Hardware / Gesture back navigation handler
    BackHandler(enabled = currentRoute != ScreenRoute.Main || isSearchActive) {
        if (isSearchActive) {
            isSearchActive = false
            searchQuery = ""
        } else {
            currentRoute = when (currentRoute) {
                ScreenRoute.ManageAccounts -> ScreenRoute.Settings
                ScreenRoute.ManageTransactionCategories -> ScreenRoute.Settings
                else -> ScreenRoute.Main
            }
        }
    }

    when (val route = currentRoute) {
        is ScreenRoute.AllAccounts -> {
            AllAccountsScreen(
                uiState = allAccountsState,
                initialCategoryId = route.focusCategoryId,
                onBackClick = { currentRoute = ScreenRoute.Main },
                onSelectCategory = { allAccountsViewModel.selectCategory(it) },
                onAddAccount = { catId, name, initBal ->
                    allAccountsViewModel.addAccount(catId, name, initBal)
                }
            )
        }

        ScreenRoute.Settings -> {
            SettingsScreen(
                currencyRates = settingsState.currencyRates,
                uiState = settingsState,
                onRateUpdated = { settingsViewModel.updateCurrencyRate(it) },
                onManageAccountsClick = { currentRoute = ScreenRoute.ManageAccounts },
                onManageTransactionCategoriesClick = { currentRoute = ScreenRoute.ManageTransactionCategories },
                onExportBackup = { uri, cr -> settingsViewModel.exportBackup(uri, cr) },
                onPrepareRestore = { uri, cr -> settingsViewModel.prepareRestore(uri, cr) },
                onConfirmRestore = { mode -> settingsViewModel.confirmRestore(mode) },
                onDismissRestoreDialog = { settingsViewModel.dismissRestoreDialog() },
                onClearBackupMessage = { settingsViewModel.clearBackupMessage() },
                onBackClick = { currentRoute = ScreenRoute.Main }
            )
        }

        ScreenRoute.ManageAccounts -> {
            ManageAccountsScreen(
                uiState = settingsState,
                onAddCategory = { settingsViewModel.addCategory(it) },
                onUpdateCategory = { settingsViewModel.updateCategory(it) },
                onDeleteCategory = { settingsViewModel.deleteCategory(it) },
                onAddAccount = { catId, name, initBal ->
                    settingsViewModel.addAccount(catId, name, initBal)
                },
                onUpdateAccount = { settingsViewModel.updateAccount(it) },
                onDeleteOrArchiveAccount = { accId, onResult ->
                    settingsViewModel.deleteOrArchiveAccount(accId, onResult)
                },
                onUnarchiveAccount = { settingsViewModel.unarchiveAccount(it) },
                onBackClick = { currentRoute = ScreenRoute.Settings }
            )
        }

        ScreenRoute.ManageTransactionCategories -> {
            ManageTransactionCategoriesScreen(
                categories = settingsState.transactionCategories,
                onAddCategory = { settingsViewModel.addTransactionCategory(it) },
                onUpdateCategory = { settingsViewModel.updateTransactionCategory(it) },
                onArchiveCategory = { settingsViewModel.archiveTransactionCategory(it) },
                onUnarchiveCategory = { settingsViewModel.unarchiveTransactionCategory(it) },
                onBackClick = { currentRoute = ScreenRoute.Settings }
            )
        }

        ScreenRoute.Main -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    VittiqTopAppBar(
                        userName = userName,
                        greeting = "Good morning,",
                        showSettingsAction = true,
                        onSettingsClick = { currentRoute = ScreenRoute.Settings },
                        isSearchActive = isSearchActive,
                        onSearchActiveChange = { active ->
                            isSearchActive = active
                            if (!active) searchQuery = ""
                        },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        searchResults = searchResults
                    )
                },
                bottomBar = {
                    VittiqBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                },
                containerColor = BrightSnow
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(BrightSnow)
                ) {
                    when (selectedTab) {
                        VittiqTab.HOME -> {
                            HomeScreen(
                                uiState = homeState,
                                onAddClick = { isAddSheetOpen = true },
                                onSeeAllAccountsClick = {
                                    currentRoute = ScreenRoute.AllAccounts(null)
                                },
                                onCategoryClick = { categoryId ->
                                    currentRoute = ScreenRoute.AllAccounts(categoryId)
                                }
                            )
                        }
                        VittiqTab.LOGS -> {
                            LogsScreen(
                                uiState = logsState,
                                onPreviousMonth = { logsViewModel.previousMonth() },
                                onNextMonth = { logsViewModel.nextMonth() },
                                onAddClick = { isAddSheetOpen = true },
                                onEditTransaction = { oldTx, newTx ->
                                    logsViewModel.updateTransaction(oldTx, newTx)
                                },
                                onDeleteTransaction = { tx ->
                                    logsViewModel.deleteTransaction(tx)
                                }
                            )
                        }
                        VittiqTab.AI_INSIGHTS -> {
                            AiInsightsScreen()
                        }
                        VittiqTab.SHARED -> {
                            SharedScreen()
                        }
                        VittiqTab.PROFILE -> {
                            ProfileScreen(
                                uiState = profileState,
                                onUpdateProfile = { profileViewModel.updateUserProfile(it) },
                                onNavigateToSettings = { currentRoute = ScreenRoute.Settings }
                            )
                        }
                    }
                }

                // Add Transaction Modal Bottom Sheet
                if (isAddSheetOpen) {
                    AddTransactionSheet(
                        accounts = homeState.accountsWithCategory,
                        currencyRates = homeState.currencyRates,
                        transactionCategories = settingsState.transactionCategories,
                        onDismiss = { isAddSheetOpen = false },
                        onSaveTransaction = { transaction ->
                            homeViewModel.addTransaction(transaction)
                            isAddSheetOpen = false
                        }
                    )
                }
            }
        }
    }
}

