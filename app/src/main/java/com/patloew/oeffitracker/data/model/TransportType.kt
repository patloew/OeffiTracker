package com.patloew.oeffitracker.data.model

import androidx.annotation.StringRes
import com.patloew.oeffitracker.R
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = false)
enum class TransportType(@StringRes val stringRes: Int) {
    TRAIN(R.string.transport_type_train),
    COACH(R.string.transport_type_coach),
    BUS(R.string.transport_type_bus),
    TRAM(R.string.transport_type_tram),
    SUBWAY(R.string.transport_type_subway)
}