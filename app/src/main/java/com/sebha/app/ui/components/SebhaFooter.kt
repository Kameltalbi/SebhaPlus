package com.sebha.app.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebha.app.R

/** Bottom destinations for the main navigation bar. */
enum class BottomDestination {
    SEBHA,
    HIJRI_CALENDAR,
    HOLIDAYS,
    SETTINGS
}

/**
 * Four-item footer: Sebha, Hijri calendar, Holidays, Settings.
 */
@Composable
fun SebhaFooter(
    selected: BottomDestination,
    onDestinationSelected: (BottomDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        FooterItem(
            selected = selected == BottomDestination.SEBHA,
            onClick = { onDestinationSelected(BottomDestination.SEBHA) },
            icon = Icons.Outlined.RadioButtonChecked,
            label = stringResource(R.string.footer_sebha)
        )
        FooterItem(
            selected = selected == BottomDestination.HIJRI_CALENDAR,
            onClick = { onDestinationSelected(BottomDestination.HIJRI_CALENDAR) },
            icon = Icons.Outlined.CalendarMonth,
            label = stringResource(R.string.footer_hijri)
        )
        FooterItem(
            selected = selected == BottomDestination.HOLIDAYS,
            onClick = { onDestinationSelected(BottomDestination.HOLIDAYS) },
            icon = Icons.Outlined.Celebration,
            label = stringResource(R.string.footer_holidays)
        )
        FooterItem(
            selected = selected == BottomDestination.SETTINGS,
            onClick = { onDestinationSelected(BottomDestination.SETTINGS) },
            icon = Icons.Outlined.Settings,
            label = stringResource(R.string.footer_settings)
        )
    }
}

@Composable
private fun RowScope.FooterItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.primary
        )
    )
}
