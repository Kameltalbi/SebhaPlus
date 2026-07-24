package com.sebha.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.unit.sp
import com.sebha.app.R
import com.sebha.app.ui.theme.SebhaButtonDark
import com.sebha.app.ui.theme.SebhaPrimary

/**
 * Large, stable circular primary action designed for one-handed use.
 */
@Composable
fun TapButton(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cycleProgress = when {
        count == 0 -> 0f
        count % 33 == 0 -> 1f
        else -> (count % 33) / 33f
    }
    val animatedCycleProgress by animateFloatAsState(
        targetValue = cycleProgress,
        animationSpec = tween(durationMillis = 260),
        label = "thirtyThreeProgress"
    )

    val buttonColor = if (isSystemInDarkTheme()) SebhaButtonDark else SebhaPrimary
    val labelColor = if (isSystemInDarkTheme()) Color(0xFFF7F5EF) else Color.White
    val ringColor = if (isSystemInDarkTheme()) Color(0xFF78B895) else Color(0xFF4E8A6B)
    val counterDescription = stringResource(R.string.daily_counter_content_desc, count)
    // Keep the number physically inside the circle even with a large system font scale.
    val fontScale = LocalDensity.current.fontScale

    Box(
        modifier = modifier
            .size(274.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = ringColor.copy(alpha = 0.16f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = animatedCycleProgress * 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Surface(
            modifier = Modifier
                .size(248.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = counterDescription
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.25f)),
                    onClickLabel = stringResource(R.string.tap_button_content_desc),
                    onClick = onClick
                ),
            shape = CircleShape,
            color = buttonColor,
            shadowElevation = 4.dp,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.9f)) togetherWith
                            (fadeOut() + scaleOut(targetScale = 1.05f))
                    },
                    label = "buttonCounter"
                ) { value ->
                    val digits = value.toString().length
                    val baseFontSize = when {
                        digits <= 2 -> 88
                        digits == 3 -> 80
                        digits == 4 -> 68
                        digits == 5 -> 58
                        digits == 6 -> 48
                        else -> 40
                    }
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = (baseFontSize / fontScale).sp,
                            lineHeight = ((baseFontSize + 6) / fontScale).sp
                        ),
                        color = labelColor,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
