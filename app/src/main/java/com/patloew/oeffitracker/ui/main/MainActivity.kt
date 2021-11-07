package com.patloew.oeffitracker.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.navigate
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import com.patloew.oeffitracker.ui.ticket.list.TicketListScreen
import com.patloew.oeffitracker.ui.ticket.list.TicketListViewModel
import com.patloew.oeffitracker.ui.trip.list.TripListScreen
import com.patloew.oeffitracker.ui.trip.list.TripListViewModel
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
    object Statistics : Screen("statistics", R.string.bottombar_statistics, R.drawable.ic_line_chart)
}

class MainActivity : ComponentActivity() {

    private val tripListViewModel: TripListViewModel by viewModel()
    private val ticketListViewModel: TicketListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()

                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }) },
                        bottomBar = {
                            BottomNavigation {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                listOf(Screen.List, Screen.Tickets, Screen.Statistics).forEach { screen ->
                                    BottomNavigationItem(
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
                        content = {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.List.route,
                                Modifier.padding(
                                    it.calculateStartPadding(LayoutDirection.Ltr),
                                    it.calculateTopPadding(),
                                    it.calculateEndPadding(LayoutDirection.Ltr),
                                    it.calculateBottomPadding()
                                )
                            ) {
                                composable(Screen.List.route) { TripListScreen(navController, tripListViewModel) }
                                composable(Screen.Tickets.route) { TicketListScreen(ticketListViewModel) }
                                composable(Screen.Statistics.route) {
                                    Text("Statistics", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    )
                }

            }
        }
    }
}
