package com.buildmasterapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.buildmasterapp.R
import com.buildmasterapp.ui.theme.BuildMasterTheme


@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.home))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BuildMasterTheme {
        HomeScreen()
    }
}