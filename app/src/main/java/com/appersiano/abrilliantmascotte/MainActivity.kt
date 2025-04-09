package com.appersiano.abrilliantmascotte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appersiano.abrilliantmascotte.ui.theme.ABrilliantMascotteTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ABrilliantMascotteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Koji()
                    }
                }
            }
        }
    }
}

@Composable
fun Koji() {
    Box {
        GreenBody(
            Modifier
                .size(400.dp)
                .align(Alignment.Center)
        )
        Eye(
            Modifier
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun PreviewKoji() {
    Koji()
}

private fun Offset.normalize(): Offset {
    val len = sqrt(this.x * this.x + this.y * this.y)
    return if (len != 0f) Offset(this.x / len, this.y / len) else Offset.Zero
}

@Composable
fun GreenBody(modifier: Modifier = Modifier.size(200.dp)) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)

        val vertices = listOf(
            Offset(width / 2, 0f),         // Top
            Offset(width, height / 2),       // Right
            Offset(width / 2, height),       // Bottom
            Offset(0f, height / 2)           // Left
        )
        val n = vertices.size

        val cornerRadius = minOf(width, height) * 0.15f

        val concavityFactor = 0.2f

        val pIns = mutableListOf<Offset>()
        val pOuts = mutableListOf<Offset>()
        for (i in 0 until n) {
            val current = vertices[i]
            val prev = vertices[(i - 1 + n) % n]
            val next = vertices[(i + 1) % n]

            val dirIn = (current - prev).normalize()
            val dirOut = (next - current).normalize()

            val pIn = current - dirIn * cornerRadius
            val pOut = current + dirOut * cornerRadius

            pIns.add(pIn)
            pOuts.add(pOut)
        }

        val path = Path().apply {
            moveTo(pOuts[0].x, pOuts[0].y)
            for (i in 0 until n) {
                val currentIndex = i
                val nextIndex = (i + 1) % n

                val startEdge = pOuts[currentIndex]
                val endEdge = pIns[nextIndex]
                val midEdge =
                    Offset((startEdge.x + endEdge.x) / 2, (startEdge.y + endEdge.y) / 2)
                val concaveControl = midEdge + (center - midEdge) * concavityFactor

                quadraticBezierTo(
                    concaveControl.x, concaveControl.y,
                    endEdge.x, endEdge.y
                )


                val vertex = vertices[nextIndex]
                val roundedEnd = pOuts[nextIndex]
                quadraticBezierTo(
                    vertex.x, vertex.y,
                    roundedEnd.x, roundedEnd.y
                )
            }
            close()
        }

        drawPath(path = path, color = Color(0xFF28cc56))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBody() {
    GreenBody()
}

@Composable
fun Eye(modifier: Modifier) {
    Box(modifier) {
        WhiteOfTheEye(
            Modifier
                .align(Alignment.Center)
        )
        Pupil(
            Modifier
                .size(70.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun WhiteOfTheEye(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 170.dp, height = 120.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            )
    )
}

@Composable
fun Pupil(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(50.dp)
            .background(
                color = Color.Black,
            )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEye() {
    GreenBody()
}
