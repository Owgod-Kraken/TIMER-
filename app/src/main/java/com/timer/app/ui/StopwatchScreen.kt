package com.timer.app.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timer.app.stopwatch.LapRecord
import com.timer.app.stopwatch.StopwatchState
import com.timer.app.ui.theme.Green
import com.timer.app.ui.theme.LapBest
import com.timer.app.ui.theme.LapWorst
import com.timer.app.ui.theme.Orange
import com.timer.app.ui.theme.Red

@Composable
fun StopwatchScreen(
    state: StopwatchState,
    isDarkTheme: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Toggle theme",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isLandscape) {
            LandscapeLayout(state, onStart, onPause, onReset, onLap)
        } else {
            PortraitLayout(state, onStart, onPause, onReset, onLap)
        }
    }
}

@Composable
private fun PortraitLayout(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.15f))

        TimerDisplay(state = state)

        Spacer(modifier = Modifier.height(40.dp))

        ControlButtons(
            state = state,
            onStart = onStart,
            onPause = onPause,
            onReset = onReset,
            onLap = onLap
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = state.laps.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.weight(0.5f)
        ) {
            LapsList(laps = state.laps)
        }

        if (state.laps.isEmpty()) {
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun LandscapeLayout(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerDisplay(state = state, compact = true)
            Spacer(modifier = Modifier.height(24.dp))
            ControlButtons(
                state = state,
                onStart = onStart,
                onPause = onPause,
                onReset = onReset,
                onLap = onLap,
                compact = true
            )
        }

        if (state.laps.isNotEmpty()) {
            Spacer(modifier = Modifier.width(24.dp))
            Box(modifier = Modifier.weight(0.8f)) {
                LapsList(laps = state.laps)
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    state: StopwatchState,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val colonAlpha = if (!state.isRunning && state.hasStarted) pulseAlpha else 1f
    val fontSize = if (compact) 56.sp else 72.sp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = state.minutes,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.alpha(colonAlpha)
        )
        Text(
            text = state.seconds,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.alpha(colonAlpha)
        )
        Text(
            text = state.centiseconds,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ControlButtons(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
    compact: Boolean = false
) {
    val buttonSize = if (compact) 56.dp else 68.dp
    val mainButtonSize = if (compact) 72.dp else 84.dp

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset button
        val resetScale by animateFloatAsState(
            targetValue = if (state.hasStarted) 1f else 0.8f,
            animationSpec = tween(300),
            label = "resetScale"
        )
        val resetAlpha by animateFloatAsState(
            targetValue = if (state.hasStarted) 1f else 0.3f,
            animationSpec = tween(300),
            label = "resetAlpha"
        )

        ActionButton(
            icon = Icons.Rounded.RestartAlt,
            contentDescription = "Reset",
            onClick = onReset,
            enabled = state.hasStarted,
            modifier = Modifier
                .size(buttonSize)
                .scale(resetScale)
                .alpha(resetAlpha),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Red
        )

        // Main Start/Pause button
        val mainButtonColor by animateColorAsState(
            targetValue = if (state.isRunning) Orange else Green,
            animationSpec = tween(300),
            label = "mainColor"
        )

        FilledIconButton(
            onClick = { if (state.isRunning) onPause() else onStart() },
            modifier = Modifier.size(mainButtonSize),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = mainButtonColor
            )
        ) {
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(200),
                label = "iconScale"
            )
            Icon(
                imageVector = if (state.isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (state.isRunning) "Pause" else "Start",
                modifier = Modifier
                    .size(if (compact) 32.dp else 40.dp)
                    .scale(scale),
                tint = MaterialTheme.colorScheme.surface
            )
        }

        // Lap button
        val lapScale by animateFloatAsState(
            targetValue = if (state.isRunning) 1f else 0.8f,
            animationSpec = tween(300),
            label = "lapScale"
        )
        val lapAlpha by animateFloatAsState(
            targetValue = if (state.isRunning) 1f else 0.3f,
            animationSpec = tween(300),
            label = "lapAlpha"
        )

        ActionButton(
            icon = Icons.Rounded.Flag,
            contentDescription = "Lap",
            onClick = onLap,
            enabled = state.isRunning,
            modifier = Modifier
                .size(buttonSize)
                .scale(lapScale)
                .alpha(lapAlpha),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = CircleShape,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun LapsList(laps: List<LapRecord>) {
    val bestSplit = laps.minByOrNull { it.splitMs }?.splitMs
    val worstSplit = if (laps.size > 1) laps.maxByOrNull { it.splitMs }?.splitMs else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "LAP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = "SPLIT",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
            @Suppress("DEPRECATION")
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(laps) { lap ->
                    val lapColor = when (lap.splitMs) {
                        bestSplit -> if (laps.size > 1) LapBest else MaterialTheme.colorScheme.onSurface
                        worstSplit -> LapWorst
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    LapRow(lap = lap, color = lapColor)
                }
            }
        }
    }
}

@Composable
private fun LapRow(
    lap: LapRecord,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${lap.number}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = color,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = formatTime(lap.splitMs),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            ),
            color = color,
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.Center
        )
        Text(
            text = formatTime(lap.elapsedMs),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = color.copy(alpha = 0.7f),
            modifier = Modifier.weight(0.4f),
            textAlign = TextAlign.End
        )
    }
}

private fun formatTime(ms: Long): String {
    val minutes = (ms / 60_000) % 100
    val seconds = (ms / 1_000) % 60
    val centis = (ms / 10) % 100
    return String.format("%02d:%02d:%02d", minutes, seconds, centis)
}
