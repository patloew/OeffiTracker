package com.patloew.oeffitracker.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    @StringRes titleRes: Int,
    @StringRes textRes: Int
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = titleRes),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = textRes),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }

}