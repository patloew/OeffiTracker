package com.patloew.oeffitracker.ui

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