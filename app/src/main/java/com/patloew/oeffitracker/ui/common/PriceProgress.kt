package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import kotlinx.coroutines.flow.Flow

data class ProgressData(
    val progress: Float,
    val percentageString: String,
    val priceString: String
)

@Composable
fun PriceProgress(
    progressData: Flow<ProgressData>,
    modifier: Modifier
) {
    val progressData = progressData.collectAsState(initial = ProgressData(0f, "", "")).value
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = progressData.progress,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Text(
            text = stringResource(
                id = R.string.progress_description,
                progressData.percentageString,
                progressData.priceString,
            ),
            style = MaterialTheme.typography.subtitle2
        )
    }
}