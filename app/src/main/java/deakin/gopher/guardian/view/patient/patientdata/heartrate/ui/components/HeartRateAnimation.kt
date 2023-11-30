package deakin.gopher.guardian.view.patient.patientdata.heartrate.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import deakin.gopher.guardian.view.theme.GuardianTheme
import kotlinx.coroutines.delay

@Composable
fun HeartRateAnimation() {
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(isAnimating) {
        while (isAnimating) {
            delay(1000)
            isAnimating = false
        }
    }

    var animatedProgress by remember { mutableStateOf(0f) }

    val infiniteTransition =
        rememberInfiniteTransition(label = "heart-rate-animation-infinite-transition")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "heart-rate-animation-progress"
    )

    animatedProgress = progress

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        HeartAnimation(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            progress = animatedProgress
        )
    }
}

@Composable
fun HeartAnimation(modifier: Modifier = Modifier, progress: Float) {
    Canvas(
        modifier = modifier
    ) {
        val heartPath = Path().apply {
            moveTo(size.width / 2, size.height / 5f * 4)
            cubicTo(
                size.width / 10 * 2, size.height / 5f * 4,
                size.width / 4, size.height / 5f * 3,
                size.width / 2, size.height / 5f * 3,
            )
            cubicTo(
                size.width / 4 * 3, size.height / 5f * 3,
                size.width / 10 * 8, size.height / 5f * 4,
                size.width / 2, size.height / 5f * 4,
            )
            close()
        }

        drawPath(
            path = heartPath,
            color = Color.Green,
//            color = MaterialTheme.colorScheme.primary,
            style = Stroke(8.dp.toPx())
        )

        val animatedPath = PathMeasure()
        val length = animatedPath.length
        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(length, length),
            phase = length * (1 - progress)
        )

        drawPath(
            path = heartPath,
            color = Color.Green,
//            color = MaterialTheme.colorScheme.secondary,
            style = Stroke(
                8.dp.toPx(),
                pathEffect = pathEffect
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeartRateAnimationPreview() {
    GuardianTheme {
        HeartRateAnimation()
    }
}