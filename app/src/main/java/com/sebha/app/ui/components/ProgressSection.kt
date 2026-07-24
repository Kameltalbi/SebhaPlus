package com.sebha.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebha.app.R
import com.sebha.app.ui.theme.SebhaGold
import com.sebha.app.ui.theme.SebhaProgressTrack
import com.sebha.app.ui.theme.SebhaProgressTrackDark
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * Elegant progress row under the counter: label + animated bar toward [goal].
 */
@Composable
fun ProgressSection(
    count: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (count.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 280),
        label = "progress"
    )
    val track = if (isSystemInDarkTheme()) SebhaProgressTrackDark else SebhaProgressTrack

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.progress_format, count, goal),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(100.dp)),
            color = SebhaGold,
            trackColor = track
        )
    }
}
