package com.example.cipherappproject

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cipherappproject.ui.campsite.EntrantCampsiteView
import com.example.cipherappproject.ui.lottery.EntrantLotteriesView
import com.example.cipherappproject.ui.lottery.OwnerLotteriesView
import com.example.cipherappproject.ui.lottery.PLACEHOLDER_USER_ID
import com.example.cipherappproject.ui.lottery.lotteryScreens
import com.example.cipherappproject.ui.reservations.ReservationScreen

/**
 * Origin of all navigation, handles persistence of nav-bar and title bar
 *
 * Outstanding issue: implement user types, which will change the start destination and nav-bar options
 */
/*
 * Built by Bailey Nov 2 2026
 * Updates:
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteApp(){
    //TODO get correct user profile for current device.
    val currentUserId = PLACEHOLDER_USER_ID
    // TEMPORARY role switch until login decides the role
    var role by rememberSaveable { mutableStateOf(UserRole.ENTRANT) }

    key(role) {
        val navController = rememberNavController()
        val tabs = BottomBarDestinations.tabsFor(role)
        val startDestination = BottomBarDestinations.SEARCH
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
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        // TEMPORARY: remove once login sets the role
                        TextButton(
                            onClick = {
                                role = if (role == UserRole.ENTRANT) UserRole.OWNER else UserRole.ENTRANT
                            },
                            modifier = Modifier.testTag("switchRoleButton")
                        ) {
                            Text(if (role == UserRole.ENTRANT) "Owner view" else "Entrant view")
                        }
                        // Entrant: received notifications, Owner: sent notifications
                        IconButton(
                            onClick = {
                                navController.navigate(AppRoutes.notificationsFor(role)) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.testTag("notificationsButton")
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
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
                composable(AppRoutes.SEARCH){
                    CampsiteSearch(
                        onNavigateToCampsite = {
                            navController.navigate(AppRoutes.campsite(it.getCampsiteId()))
                        }
                    )
                }
                composable(AppRoutes.CAMPSITE) { backStackEntry ->
                    val campsiteId = backStackEntry.arguments?.getString("campsiteId") ?: ""
                    EntrantCampsiteView(campsiteId)
                }
                composable(AppRoutes.ENTRANT_RESERVATIONS) {
                    ReservationScreen()
                }
                composable(AppRoutes.ENTRANT_LOTTERIES) {
                    EntrantLotteriesView(navController, currentUserId)
                }
                composable(AppRoutes.MESSAGES) {
                    MessageView()
                }
                composable(AppRoutes.ENTRANT_NOTIFICATIONS) {
                    NotificationView()
                }
                composable(AppRoutes.SCAN_QR) {
                    QRScanView()
                }

                // Owner
                composable(AppRoutes.OWNER_CAMPSITES) {
                    OwnerOverview()
                }
                composable(AppRoutes.OWNER_LOTTERIES) {
                    OwnerLotteriesView(navController, currentUserId)
                }
                composable(AppRoutes.OWNER_RESERVATIONS) {
                    ReservationScreen()
                }
                composable(AppRoutes.OWNER_NOTIFICATIONS) {
                    SentNotificationView()
                }

                // Shared
                composable(AppRoutes.PROFILE){
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
                composable(AppRoutes.PROFILE_SETTINGS){
                    ProfileView(navController)
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

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}