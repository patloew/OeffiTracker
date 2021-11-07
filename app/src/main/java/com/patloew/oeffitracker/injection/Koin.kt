package com.patloew.oeffitracker.injection

import androidx.room.Room
import com.patloew.oeffitracker.BuildConfig
import com.patloew.oeffitracker.data.AppDatabase
import com.patloew.oeffitracker.ui.create.CreateViewModel
import com.patloew.oeffitracker.ui.list.ListViewModel
import com.patloew.oeffitracker.ui.ticket.TicketViewModel
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
            .apply { if (BuildConfig.DEBUG) fallbackToDestructiveMigration() }
            .build()
    }
    single { get<AppDatabase>().tripDao() }
    single { get<AppDatabase>().ticketDao() }

    viewModel { ListViewModel(get()) }
    viewModel { TicketViewModel(get()) }
    viewModel { CreateViewModel(get()) }
}