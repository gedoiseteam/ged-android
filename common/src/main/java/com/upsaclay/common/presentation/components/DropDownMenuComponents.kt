package com.upsaclay.common.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.upsaclay.common.presentation.theme.GedoiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropDownMenu(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: String,
    onItemClicked: (String) -> Unit,
    expanded: Boolean,
    isEnable: Boolean = true,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            enabled = isEnable,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = fillTextFieldColor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = { onItemClicked(item) },
                    modifier = modifier
                )
            }
        }
    }
}

/*
 =====================================================================
                                Preview
 =====================================================================
 */

@Preview(showBackground = true)
@Composable
private fun SimpleDropDownMenuPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3")
    var selectedItem by remember {
        mutableStateOf(items[0])
    }
    var expanded by remember {
        mutableStateOf(false)
    }

    GedoiseTheme {
        SimpleDropDownMenu(
            items = items,
            selectedItem = selectedItem,
            onItemClicked = { item ->
                selectedItem = item
            },
            expanded = expanded,
            onExpandedChange = { isExpanded ->
                expanded = isExpanded
            },
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}