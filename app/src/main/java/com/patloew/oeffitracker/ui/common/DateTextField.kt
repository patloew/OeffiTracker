package com.patloew.oeffitracker.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow


@Composable
fun DateTextField(
    onDateClick: () -> Unit,
    dateStringFlow: Flow<String>,
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int
) {
    Box(modifier = Modifier.padding(top = 16.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = dateStringFlow.collectAsState(initial = "").value,
            onValueChange = { },
            leadingIcon = {
                Icon(
                    painterResource(id = iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            },
            label = { Text(stringResource(id = labelRes)) }
        )

        Box(modifier = Modifier
            .matchParentSize()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onDateClick() })
    }
}