package com.upsaclay.common.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upsaclay.common.R
import com.upsaclay.common.domain.entity.SnackbarType
import com.upsaclay.common.presentation.theme.GedoiseTheme
import com.upsaclay.common.presentation.theme.spacing
import kotlinx.coroutines.launch

private val infoContentColor = Color(0xFF3975EA)
private val successContentColor = Color(0xFF55AB43)
private val errorContentColor = Color(0xFFC65052)
private val warningContentColor = Color(0xFFB79633)

@Composable
fun InfoSnackbar(
    modifier: Modifier = Modifier,
    message: String
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = infoContentColor
            )
            Text(message)
        }
    }
}

@Composable
fun SuccessSnackBar(
    modifier: Modifier = Modifier,
    message: String
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = successContentColor
            )
            Text(message)
        }
    }
}
@Composable
fun ErrorSnackBar(
    modifier: Modifier = Modifier,
    message: String
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fill_cross_circle),
                contentDescription = null,
                tint = errorContentColor
            )

            Text(message)
        }
    }
}

@Composable
fun WarningSnackBar(
    modifier: Modifier = Modifier,
    message: String
) {
    Snackbar(
        modifier = modifier.padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = warningContentColor
            )

            Text(message)
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SnackBarPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    var snackBarType by remember { mutableStateOf(SnackbarType.INFO) }

    val scope = rememberCoroutineScope()
    GedoiseTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    when (snackBarType) {
                        SnackbarType.INFO -> InfoSnackbar(message = "Info snackbar")
                        SnackbarType.SUCCESS -> SuccessSnackBar(message = "Success snackbar")
                        SnackbarType.ERROR -> ErrorSnackBar(message = "Error snackbar")
                        SnackbarType.WARNING -> WarningSnackBar(message = "Warning snackbar")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = infoContentColor
                    ),
                    onClick = {
                        scope.launch {
                            snackBarType = SnackbarType.INFO
                            snackbarHostState.showSnackbar(message = "Info snackbar")
                        }
                    }
                ) {
                    Text("Info snackbar")
                }

                Button(
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = successContentColor
                    ),
                    onClick = {
                        scope.launch {
                            snackBarType = SnackbarType.SUCCESS
                            snackbarHostState.showSnackbar(message = "Success snackbar")
                        }
                    }
                ) {
                    Text("Sucess snackbar")
                }

                Button(
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = errorContentColor
                    ),
                    onClick = {
                        scope.launch {
                            snackBarType = SnackbarType.ERROR
                            snackbarHostState.showSnackbar(message = "Error snackbar")
                        }
                    }
                ) {
                    Text("Error snackbar")
                }

                Button(
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = warningContentColor
                    ),
                    onClick = {
                        scope.launch {
                            snackBarType = SnackbarType.WARNING
                            snackbarHostState.showSnackbar(message = "Warnnig snackbar")
                        }
                    }
                ) {
                    Text("Warning snackbar")
                }
            }
        }
    }
}