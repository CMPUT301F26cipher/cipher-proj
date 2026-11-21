package com.example.cipherappproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cipherappproject.database.enums.ProfileRole
import com.example.cipherappproject.reservations.OwnerOverview
import com.example.cipherappproject.ui.campsite.EntrantCampsiteView
import com.example.cipherappproject.ui.lottery.EntrantLotteriesView
import com.example.cipherappproject.ui.lottery.OwnerLotteriesView
import com.example.cipherappproject.ui.lottery.lotteryScreens
import com.example.cipherappproject.reservations.ReservationScreen
import com.example.cipherappproject.ui.registration.AccountErrorView
import com.example.cipherappproject.ui.registration.Registration
import com.example.cipherappproject.database.provider.RepositoryDatabase


/**
 * Origin of all navigation, handles persistence of nav-bar and title bar
 *
 * The user's role (entrant/owner) comes from their Profile, loaded by device ID on startup.
 * Devices without a profile are sent to registration first.
 */
/*
 * Built by Bailey Nov 2 2026
 * Updates:
 * Updated By Madison Nov 6 2026 - integrated registration
 * Updated Nov 6 2026 - removed temporary role switch button, pass current user id to screens
 * Updated by Justin on November 19th, 2026
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteApp(){

    val accountController = remember { AccountController() }

    var deviceId by remember { mutableStateOf<String?>(null) }
    var deviceError by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf<String?>(null) }
    var loadTimedOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        accountController.getAccountId { id ->
            if (id != null) deviceId = id else deviceError = true
        }
    }

    // If the device id or the profiles never arrive (no network, Firestore rules, ...)
    // show a message instead of spinning forever.
    LaunchedEffect(Unit) {
        delay(10_000)
        loadTimedOut = true
    }


    // It should draw any lottery whose registration period has ended, and also 
    // once lotteries and entries have loaded from Firestore

    val drawController = remember { LotteryDrawController() }
    val lotteriesLoaded = RepositoryDatabase.lotteryRepository.isLotteriesLoaded.value
    val entriesLoaded = RepositoryDatabase.lotteryEntryRepository.isLotteriesEntriesLoaded.value

    LaunchedEffect(lotteriesLoaded, entriesLoaded) {
        if (lotteriesLoaded && entriesLoaded) {
            drawController.drawExpiredLotteries()
        }
    }

    // profileRepository.profiles is Compose State: once a profile is created below, the
    // snapshot listener adds it to the list and this recomposes into the main app.
    val profilesLoaded = accountController.isProfilesLoaded()
    val profile = deviceId?.takeIf { profilesLoaded }?.let { accountController.findProfile(it) }
    val currentUserId = profile?.profileId ?: ""
    val role = if (profile?.role?.contains(ProfileRole.OWNER.name) == true) UserRole.OWNER else UserRole.ENTRANT
    val needsRegistration = deviceId != null && profilesLoaded && profile == null

    when {

        deviceError -> {
            AccountErrorView()
        }

        needsRegistration -> {
          Column {
            registrationError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Registration(

                onEntrantRegistration = { entrantDetails ->
                    accountController.createEntrantAccount(
                        entrantDetails = entrantDetails,
                        deviceId = deviceId!!
                    ) { createResult ->
                        createResult.onFailure {
                            registrationError = it.message ?: "Could not create your account."
                        }
                    }
                },

                onOwnerRegistration = { ownerDetails ->
                    accountController.createOwnerAccount(
                        ownerDetails = ownerDetails,
                        deviceId = deviceId!!
                    ) { createResult ->
                        createResult.onFailure {
                            registrationError = it.message ?: "Could not create your account."
                        }
                    }
                }
            )
          }
        }

        currentUserId.isNotBlank() -> {

            key(role) {
                val navController = rememberNavController()
                val tabs = BottomBarDestinations.tabsFor(role)
                // First tab of the role, so owners do not start on the entrant-only Search screen
                val startDestination = tabs.first()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val showBackArrow = currentRoute != null && tabs.none { it.route == currentRoute }
//      var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier,
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text(text = "Cipher App") },
                            navigationIcon = {
                                if (showBackArrow) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            actions = {
                                // Entrant: received notifications, Owner: sent notifications
                                IconButton(
                                    onClick = {
                                        navController.navigate(AppRoutes.notificationsFor(role)) {
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier.testTag("notificationsButton")
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                            windowInsets = NavigationBarDefaults.windowInsets
                        ) {
                            tabs.forEach { destination ->
                                NavigationBarItem(
                                    // startsWith so e.g. "owner/lotteries/create" still highlights Lotteries
                                    selected = currentRoute?.startsWith(destination.route) == true,
                                    onClick = { navigateToTab(navController, destination.route) },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.contentDescription
                                        )
                                    },
                                    label = { Text(destination.label) },
                                    modifier = Modifier.testTag(destination.route)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Entrant
                        composable(AppRoutes.SEARCH) {
                            CampsiteSearch(
                                onNavigateToCampsite = {
                                    navController.navigate(AppRoutes.campsite(it.campsiteId))
                                }
                            )
                        }
                        composable(AppRoutes.CAMPSITE) { backStackEntry ->
                            val campsiteId = backStackEntry.arguments?.getString("campsiteId") ?: ""
                            EntrantCampsiteView(campsiteId, currentUserId)
                        }
                        composable(AppRoutes.ENTRANT_RESERVATIONS) {
                            ReservationScreen(userID = currentUserId, isOwner = false)
                        }
                        composable(AppRoutes.ENTRANT_LOTTERIES) {
                            EntrantLotteriesView(navController, currentUserId)
                        }
                        composable(AppRoutes.MESSAGES) {
                            MessageView()
                        }
                        composable(AppRoutes.ENTRANT_NOTIFICATIONS) {
                            NotificationView(currentUserId)
                        }
                        composable(AppRoutes.SCAN_QR) {
                            QRScanView()
                        }

                        // Owner
                        composable(AppRoutes.OWNER_CAMPSITES) {
                            OwnerOverview(ownerId = currentUserId)
                        }
                        composable(AppRoutes.OWNER_LOTTERIES) {
                            OwnerLotteriesView(navController, currentUserId)
                        }
                        composable(AppRoutes.OWNER_RESERVATIONS) {
                            ReservationScreen(userID = currentUserId, isOwner = true)
                        }
                        composable(AppRoutes.OWNER_NOTIFICATIONS) {
                            SentNotificationView(currentUserId)
                        }
                        composable(AppRoutes.PROFILE_SETTINGS) { // profile settings, including the notifications on/off switch
                            ProfileSettingsView(currentUserId)
                        }

                        // Shared
                        composable(AppRoutes.PROFILE) {
                            ProfileMenuView(
                                role = role,
                                onOpen = { route ->
                                    if (tabs.any { it.route == route }) {
                                        navigateToTab(navController, route)
                                    } else {
                                        navController.navigate(route)
                                    }
                                }
                            )
                        }
                        composable(AppRoutes.POLICY) {
                            PolicyView()
                        }

                        // FEATURE SUB SCREENS ADD UNDER HERE

                        lotteryScreens(navController = navController, currentUserId = currentUserId)

                        //TODO: implement Admin navigation
                    }
                }
            }
        }

        // Still looking up the device's profile
        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (loadTimedOut) {
                    Text(
                        "Could not load your account. Check your internet connection and the database permissions, then restart the app."
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
