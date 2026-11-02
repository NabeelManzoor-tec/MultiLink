package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

enum class QrEyeStyle {
    ROUNDED,
    SQUARE,
    DOTS
}

data class QrRenderConfig(
    val foregroundColor: Color = Color(0xFF06B6D4),
    val backgroundColor: Color = Color(0xFF0B0E14),
    val eyeStyle: QrEyeStyle = QrEyeStyle.ROUNDED,
    val showCenterAvatar: Boolean = true
)

object QrCodeHelper {

    fun generateBitMatrix(text: String, size: Int = 300): Array<BooleanArray>? {
        return try {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val width = matrix.width
            val height = matrix.height
            val result = Array(height) { BooleanArray(width) }
            for (y in 0 until height) {
                for (x in 0 until width) {
                    result[y][x] = matrix.get(x, y)
                }
            }
            result
        } catch (e: Exception) {
            null
        }
    }

    fun generateBitmap(
        text: String,
        config: QrRenderConfig,
        size: Int = 600
    ): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 2,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
            val width = matrix.width
            val height = matrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val fg = config.foregroundColor.toArgb()
            val bg = config.backgroundColor.toArgb()

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (matrix.get(x, y)) fg else bg)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun StylizedQrCode(
    content: String,
    modifier: Modifier = Modifier,
    config: QrRenderConfig = QrRenderConfig(),
    avatarPath: String = ""
) {
    val matrix = remember(content) {
        QrCodeHelper.generateBitMatrix(content, 35)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(config.backgroundColor)
            .border(2.dp, config.foregroundColor.copy(alpha = 0.35f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (matrix != null) {
            val rows = matrix.size
            val cols = if (rows > 0) matrix[0].size else 0

            Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
                if (rows > 0 && cols > 0) {
                    val cellWidth = size.width / cols
                    val cellHeight = size.height / rows
                    val centerBoxRadius = cols * 0.18f
                    val centerCol = cols / 2f
                    val centerRow = rows / 2f

                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            if (matrix[r][c]) {
                                // Skip modules under center avatar if enabled
                                if (config.showCenterAvatar) {
                                    val dist = kotlin.math.hypot(c - centerCol, r - centerRow)
                                    if (dist < centerBoxRadius) continue
                                }

                                val left = c * cellWidth
                                val top = r * cellHeight

                                when (config.eyeStyle) {
                                    QrEyeStyle.ROUNDED -> {
                                        drawRoundRect(
                                            color = config.foregroundColor,
                                            topLeft = Offset(left, top),
                                            size = Size(cellWidth * 0.95f, cellHeight * 0.95f),
                                            cornerRadius = CornerRadius(cellWidth * 0.35f, cellHeight * 0.35f)
                                        )
                                    }
                                    QrEyeStyle.DOTS -> {
                                        val radius = (cellWidth.coerceAtMost(cellHeight) * 0.45f)
                                        drawCircle(
                                            color = config.foregroundColor,
                                            radius = radius,
                                            center = Offset(left + cellWidth / 2, top + cellHeight / 2)
                                        )
                                    }
                                    QrEyeStyle.SQUARE -> {
                                        drawRect(
                                            color = config.foregroundColor,
                                            topLeft = Offset(left, top),
                                            size = Size(cellWidth, cellHeight)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Center Avatar / Badge overlay
        if (config.showCenterAvatar) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.24f)
                    .clip(CircleShape)
                    .background(config.backgroundColor)
                    .border(2.5.dp, config.foregroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CreatorAvatarView(
                    avatarPath = avatarPath,
                    contentDescription = "QR Center Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}
