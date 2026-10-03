package com.vittiq.android.ui

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
import com.vittiq.android.ui.shared.SharedScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VittiqApp(
    repository: VittiqRepository,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(VittiqTab.HOME) }
    var isAddSheetOpen by remember { mutableStateOf(false) }
    var isCurrencySettingsOpen by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
    val logsViewModel: LogsViewModel = viewModel(factory = LogsViewModel.Factory(repository))
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory(repository))

    val homeState by homeViewModel.uiState.collectAsState()
    val logsState by logsViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    val userName = profileState.userProfile?.firstName ?: "Kunal"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VittiqTopAppBar(
                userName = userName,
                greeting = "Good morning,",
                showSettingsAction = selectedTab == VittiqTab.PROFILE,
                onSettingsClick = { isCurrencySettingsOpen = true },
                onSearchClick = { /* Global search trigger */ }
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
                        onAddClick = { isAddSheetOpen = true }
                    )
                }
                VittiqTab.LOGS -> {
                    LogsScreen(
                        uiState = logsState,
                        onPreviousMonth = { logsViewModel.previousMonth() },
                        onNextMonth = { logsViewModel.nextMonth() },
                        onAddClick = { isAddSheetOpen = true }
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
                        onRateUpdated = { profileViewModel.updateCurrencyRate(it) },
                        showCurrencyDialogDirectly = isCurrencySettingsOpen,
                        onDismissCurrencyDialogDirectly = { isCurrencySettingsOpen = false }
                    )
                }
            }
        }

        // Add Transaction Modal Bottom Sheet
        if (isAddSheetOpen) {
            AddTransactionSheet(
                accounts = homeState.accounts,
                currencyRates = homeState.currencyRates,
                onDismiss = { isAddSheetOpen = false },
                onSaveTransaction = { transaction ->
                    homeViewModel.addTransaction(transaction)
                    isAddSheetOpen = false
                }
            )
        }
    }
}
