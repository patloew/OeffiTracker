package com.patloew.oeffitracker.ui

import com.patloew.oeffitracker.data.model.PriceDeduction
import java.math.BigDecimal

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

private val amountRegex = Regex("^\\d+(\\.\\d{0,2})?$")

/**
 * Parses and validates an amount and calls [setAmount] if valid.
 *
 * @return true if amount is valid
 */
fun checkAndSetAmount(amountString: String, setAmount: (amount: Long?) -> Unit): Boolean {
    val newAmount = amountString.replace(',', '.')
    return when {
        newAmount.isEmpty() -> {
            setAmount(null)
            true
        }
        newAmount.matches(amountRegex) ->
            newAmount.toBigDecimalOrNull()?.let { newAmountBigDecimal ->
                val newAmountLong = newAmountBigDecimal.times(BigDecimal.valueOf(100)).toLong()
                val comparison = newAmountLong.toBigDecimal().divide(BigDecimal.valueOf(100))
                if (comparison.compareTo(newAmountBigDecimal) == 0) {
                    setAmount(newAmountLong)
                    true
                } else {
                    false
                }
            } ?: false
        else -> false
    }
}

/** Formats this Long as amount, omitting trailing zeros if not needed */
fun Long?.formatAmount(): String? =
    if (this?.mod(100) == 0) {
        div(100).toString()
    } else {
        this?.let {
            val bd = toBigDecimal().divide(BigDecimal.valueOf(100))
            amountFormatFloat.format(bd).replace('.', ',')
        }
    }

/** Formats this Long as price with currency, omitting trailing zeros if not needed */
fun Long.formatPrice(): String {
    val formatter = if (mod(100) == 0) {
        priceFormatInteger
    } else {
        priceFormatFloat
    }

    return formatter.format(toBigDecimal().divide(BigDecimal.valueOf(100)))
}

fun getProgressSum(sum: Long, deduction: Long?, includeDeduction: Boolean): Long =
    if (includeDeduction && deduction != null) {
        sum + deduction
    } else {
        sum
    }

fun PriceDeduction.getSum(sum: Long, includeDeduction: Boolean): Long =
    getProgressSum(sum, deduction, includeDeduction)

fun getProgressGoal(price: Long, deduction: Long?, includeDeduction: Boolean): Long =
    if (!includeDeduction && deduction != null) {
        price - deduction
    } else {
        price
    }

fun PriceDeduction.getGoal(includeDeduction: Boolean): Long =
    getProgressGoal(price, deduction, includeDeduction)