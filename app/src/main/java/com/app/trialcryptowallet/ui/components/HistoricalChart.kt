package com.app.trialcryptowallet.ui.components

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartData
import com.app.trialcryptowallet.utils.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun PriceChart(
    modifier: Modifier = Modifier,
    data: List<ItemHistoricalChartData> = listOf(
        ItemHistoricalChartData(unixTime = 1609459200, price = 100.0),
        ItemHistoricalChartData(unixTime = 1609545600, price = 150.0),
        ItemHistoricalChartData(unixTime = 1609632000, price = 120.0),
        ItemHistoricalChartData(unixTime = 1609718400, price = 180.0)
    ),
    isProfitable: Boolean = true,
    isDateWithTime: Boolean = true
) {
    val scale = remember { mutableFloatStateOf(50f) }
    val offsetX = remember { mutableFloatStateOf(0f) }

    val maxPrice = data.maxOfOrNull { it.price } ?: 0.0
    val minPrice = data.minOfOrNull { it.price } ?: 0.0

    val maxTime = data.maxOfOrNull { it.unixTime } ?: 0L
    val minTime = data.minOfOrNull { it.unixTime } ?: 0L

    // Инициализируем начальное смещение для скроллинга к правой границе
    val initialOffsetX = remember {
        mutableFloatStateOf(-Float.MAX_VALUE)
    }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            scale.floatValue *= zoom
            if (scale.floatValue < 1f) {
                scale.floatValue = 1f
            } else if (scale.floatValue > 50f) {
                scale.floatValue = 50f
            }
            offsetX.floatValue += pan.x

            // Ограничиваем скроллинг
            val maxOffsetX = 0f
            val minOffsetX = -(size.width * (scale.floatValue - 1))
            offsetX.floatValue = offsetX.floatValue.coerceIn(minOffsetX, maxOffsetX)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .then(gestureModifier)
    ) {
        val width = size.width
        val height = size.height

        val xScale = (width / (maxTime - minTime)) * scale.floatValue

        // Инициализируем начальное смещение только один раз
        if (initialOffsetX.floatValue == -Float.MAX_VALUE) {
            offsetX.floatValue = -(width * (scale.floatValue - 1))
            initialOffsetX.floatValue = offsetX.floatValue
        }

        // Вычисление видимых границ времени
        val visibleMinTime = minTime + (-offsetX.floatValue / xScale).toLong()
        val visibleMaxTime = minTime + ((width - offsetX.floatValue) / xScale).toLong()

        // Индексы видимых данных с дополнительной точкой слева и справа
        val visibleStartIndex = data.indexOfFirst { it.unixTime >= visibleMinTime }.coerceAtLeast(1) - 1
        val visibleEndIndex = data.indexOfLast { it.unixTime <= visibleMaxTime }.coerceAtMost(data.size - 2) + 1

        // Фильтрация видимых данных
        val visibleData = data.subList(visibleStartIndex, visibleEndIndex + 1)

        // Пересчет вертикального масштаба и оффсета
        val visibleMaxPrice = visibleData.maxOfOrNull { it.price } ?: maxPrice
        val visibleMinPrice = visibleData.minOfOrNull { it.price } ?: minPrice
        val yScale = (height / (visibleMaxPrice - visibleMinPrice))

        clipRect(0f, 0f, width, height) {
            val path = Path().apply {
                visibleData.forEachIndexed { index, item ->
                    val x = (item.unixTime - minTime) * xScale + offsetX.floatValue
                    val y = height - ((item.price - visibleMinPrice) * yScale)
                    if (index == 0) moveTo(x, y.toFloat()) else lineTo(x, y.toFloat())
                }
            }

            drawPath(
                path = path,
                color = if (isProfitable) Color.Green else Color.Red,
                style = Stroke(width = 4f)
            )

            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 24f
            }
            val bounds = Rect()

            // Отрисовка правой границы цен
            visibleData.forEach {
                val x = width - 10
                val y = height - ((it.price - visibleMinPrice) * yScale)
                val price = formatCurrency(it.price)

                drawLine(
                    color = if (it.price == data.last().price) {
                        if (isProfitable) Color.Green else Color.Red
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    },
                    strokeWidth = 1.dp.value,
                    start = Offset(0f, y.toFloat()),
                    end = Offset(width, y.toFloat()),
                    pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                )

                textPaint.getTextBounds(price, 0, price.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()
                drawContext.canvas.nativeCanvas.drawText(
                    price,
                    x + 10 - textWidth,
                    y.toFloat() + textHeight / 2f,
                    textPaint
                )
            }

            // Отрисовка нижней границы дат
            visibleData.forEach {
                val x = (it.unixTime - minTime) * xScale + offsetX.floatValue
                val y = height - 10
                val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.unixTime))
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.unixTime))

                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    strokeWidth = 1.dp.value,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                )

                textPaint.getTextBounds(date, 0, date.length, bounds)
                var textWidth = bounds.width()
                drawContext.canvas.nativeCanvas.drawText(
                    date,
                    x - textWidth / 2f,
                    if (isDateWithTime) y - textPaint.textSize else y,
                    textPaint
                )
                if (isDateWithTime) {
                    textPaint.getTextBounds(time, 0, time.length, bounds)
                    textWidth = bounds.width()
                    drawContext.canvas.nativeCanvas.drawText(
                        time,
                        x - textWidth / 2f,
                        y,
                        textPaint
                    )
                }
            }
        }
    }
}