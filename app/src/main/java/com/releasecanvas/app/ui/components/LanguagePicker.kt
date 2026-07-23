package com.releasecanvas.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.releasecanvas.app.data.locale.AppLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerField(
    label: String,
    supportingText: String?,
    selectedTag: String,
    includeSystemOption: Boolean,
    systemOptionLabel: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = buildList {
        if (includeSystemOption) add(AppLocale.UI_FOLLOW_SYSTEM)
        addAll(AppLocale.SUPPORTED_TAGS)
    }
    val display = when (selectedTag) {
        AppLocale.UI_FOLLOW_SYSTEM -> systemOptionLabel
        else -> AppLocale.displayName(selectedTag)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            supportingText = supportingText?.let { { Text(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { tag ->
                val text = if (tag == AppLocale.UI_FOLLOW_SYSTEM) {
                    systemOptionLabel
                } else {
                    AppLocale.displayName(tag)
                }
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(tag)
                        expanded = false
                    },
                )
            }
        }
    }
}
