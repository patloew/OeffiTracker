package com.patloew.oeffitracker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.navigate
import com.patloew.oeffitracker.ui.settings.SettingsActivity
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import com.patloew.oeffitracker.ui.ticket.list.TicketListScreen
import com.patloew.oeffitracker.ui.ticket.list.TicketListViewModel
import com.patloew.oeffitracker.ui.trip.list.TripListScreen
import com.patloew.oeffitracker.ui.trip.list.TripListViewModel
import com.patloew.oeffitracker.ui.trip.list.search.TripSearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

/* Copyright 2021 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

sealed class Screen(val route: String, @StringRes val stringRes: Int, @DrawableRes val iconRes: Int) {
    object List : Screen("list", R.string.bottombar_list, R.drawable.ic_tram)
    object Tickets : Screen("tickets", R.string.bottombar_tickets, R.drawable.ic_receipt)
}

class MainActivity : AppCompatActivity() {

    private val tripListViewModel: TripListViewModel by viewModel()
    private val ticketListViewModel: TicketListViewModel by viewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OeffiTrackerTheme {
                val navController = rememberNavController()
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

                Scaffold(
                    topBar = {
                        MediumTopAppBar(
                            title = { Text(stringResource(id = R.string.app_name)) },
                            actions = { TopAppBarActions(navController) },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            listOf(Screen.List, Screen.Tickets).forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painterResource(id = screen.iconRes),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(screen.stringRes)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = { navController.navigate(screen) }
                                )
                            }
                        }
                    },
                    content = { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.List.route,
                            Modifier
                                .padding(paddingValues)
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                        ) {
                            composable(Screen.List.route) { TripListScreen(navController, tripListViewModel) }
                            composable(Screen.Tickets.route) { TicketListScreen(ticketListViewModel) }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun TopAppBarActions(navController: NavController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        if (navBackStackEntry?.destination?.route == Screen.List.route) {
            IconButton(onClick = ::startTripSearch) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(id = R.string.accessibility_icon_search),
                )
            }
        }

        IconButton(onClick = ::startSettings) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = stringResource(id = R.string.accessibility_icon_settings),
            )
        }
    }

    private fun startSettings() = startActivity(Intent(this, SettingsActivity::class.java))

    private fun startTripSearch() = startActivity(Intent(this, TripSearchActivity::class.java))
}
