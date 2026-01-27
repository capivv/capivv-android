package com.capivv.sdk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A feature item to display in a list.
 */
data class FeatureItem(
    val text: String,
    val icon: ImageVector = Icons.Filled.CheckCircle
)

/**
 * A list of features with checkmarks.
 *
 * @param features List of feature strings
 * @param modifier Modifier for the list
 * @param iconColor Color for the check icons
 */
@Composable
fun FeatureList(
    features: List<String>,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            FeatureRow(
                text = feature,
                iconColor = iconColor
            )
        }
    }
}

/**
 * A list of feature items with custom icons.
 */
@Composable
fun FeatureListWithIcons(
    features: List<FeatureItem>,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            FeatureRow(
                text = feature.text,
                icon = feature.icon,
                iconColor = iconColor
            )
        }
    }
}

@Composable
private fun FeatureRow(
    text: String,
    icon: ImageVector = Icons.Filled.CheckCircle,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
