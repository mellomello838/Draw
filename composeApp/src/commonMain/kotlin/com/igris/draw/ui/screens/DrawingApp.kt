package com.igris.draw.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun DrawingApp() {
    var paths by remember { mutableStateOf(listOf<SmoothPath>()) }
    var currentPath by remember { mutableStateOf<SmoothPath?>(null) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Start a new path
                        currentPath = SmoothPath(mutableListOf(offset))
                    },
                    onDrag = { change, _ ->
                        // Update the current path with the new point
                        currentPath = currentPath?.apply {
                            points.add(change.position)
                        }
                    },
                    onDragEnd = {
                        // Add the current path to the list of paths
                        currentPath?.let { paths = paths + it }
                        currentPath = null
                    }
                )
            }
    ) {
        // Draw all saved paths
        for (path in paths) {
            drawSmoothPath(path.points, Color.Black, 4f)
        }
        // Draw the currently active path
        currentPath?.let { drawSmoothPath(it.points, Color.Black, 4f) }
    }
}

// Data class to represent a path with a list of points
data class SmoothPath(val points: MutableList<Offset>)

// Extension function to draw a smooth path
fun DrawScope.drawSmoothPath(points: List<Offset>, color: Color, strokeWidth: Float) {
    if (points.size < 2) return // Not enough points to draw
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size - 1) {
            // Calculate the midpoint between the current and next point
            val midPoint = Offset(
                (points[i].x + points[i + 1].x) / 2,
                (points[i].y + points[i + 1].y) / 2
            )
            quadraticBezierTo(
                points[i].x, points[i].y,
                midPoint.x, midPoint.y
            )
        }
        // Connect the last point
        val last = points.last()
        lineTo(last.x, last.y)
    }
    drawPath(path, color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth))
}