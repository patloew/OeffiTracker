package com.patloew.oeffitracker.ui

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

/**
 * Parses and validates an amount and calls [setAmount] if valid.
 *
 * @return true if amount is valid
 */
fun checkAndSetAmount(amountString: String, setAmount: (amount: Int?) -> Unit): Boolean {
    val newAmount = amountString.replace(',', '.')
    return when {
        newAmount.isEmpty() -> {
            setAmount(null)
            true
        }
        newAmount.matches(Regex("^\\d+(\\.\\d{0,2})?$")) ->
            newAmount.toFloatOrNull()?.let { newFareFloat ->
                setAmount(newFareFloat.times(100).toInt())
                true
            } ?: false
        else -> false
    }
}