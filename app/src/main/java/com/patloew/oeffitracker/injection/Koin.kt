package com.patloew.oeffitracker.injection

import androidx.room.Room
import com.patloew.oeffitracker.BuildConfig
import com.patloew.oeffitracker.data.AppDatabase
import com.patloew.oeffitracker.data.CustomTypeConverters
import com.patloew.oeffitracker.data.export.CsvExporter
import com.patloew.oeffitracker.data.export.JsonExporter
import com.patloew.oeffitracker.data.export.JsonImporter
import com.patloew.oeffitracker.data.migration.Migration5To6
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.ui.settings.SettingsViewModel
import com.patloew.oeffitracker.ui.ticket.create.CreateTicketViewModel
import com.patloew.oeffitracker.ui.ticket.list.TicketListViewModel
import com.patloew.oeffitracker.ui.trip.create.CreateTripViewModel
import com.patloew.oeffitracker.ui.trip.list.TripListViewModel
import com.patloew.oeffitracker.ui.trip.list.search.TripSearchViewModel
import com.squareup.moshi.Moshi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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

val appModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "main")
            .addMigrations(Migration5To6)
            .apply { if (BuildConfig.DEBUG) fallbackToDestructiveMigration() }
            .build()
    }
    single { get<AppDatabase>().tripDao() }
    single { get<AppDatabase>().ticketDao() }
    single { get<AppDatabase>().importDao() }
    single { SettingsRepo(get()) }
    single { CsvExporter(get(), get()) }
    single { JsonExporter(get(), get(), get(), get(), get()) }
    single { JsonImporter(get(), get(), get(), get()) }

    single { CreateTripViewModel.Factory(get(), get()) }
    single { CreateTicketViewModel.Factory(get()) }

    single { Moshi.Builder().add(CustomTypeConverters).build() }

    viewModel { TripListViewModel(get(), get(), get()) }
    viewModel { TripSearchViewModel(get()) }
    viewModel { TicketListViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
}