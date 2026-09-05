package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

import android.graphics.LinearGradient
import android.graphics.Shader

enum class QRModuleStyle {
    SQUARE,
    DOTS,
    ROUNDED,
    EXTRA_ROUNDED
}

enum class QREyeStyle {
    SQUARE,
    CIRCLE,
    ROUNDED
}

data class QRStylingConfig(
    val moduleStyle: QRModuleStyle = QRModuleStyle.SQUARE,
    val eyeFrameStyle: QREyeStyle = QREyeStyle.SQUARE,
    val eyeBallStyle: QREyeStyle = QREyeStyle.SQUARE,
    val colorStart: Color = Color(0xFF1B1429),
    val colorEnd: Color? = null,
    val backgroundColor: Color = Color.White,
    val logoBitmap: Bitmap? = null
)

object QRCodeGenerator {

    fun generateQRCode(
        text: String,
        width: Int = 512,
        height: Int = 512,
        config: QRStylingConfig = QRStylingConfig()
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(
                EncodeHintType.ERROR_CORRECTION,
                if (config.logoBitmap != null) ErrorCorrectionLevel.H else ErrorCorrectionLevel.M
            )
            put(EncodeHintType.MARGIN, 1)
        }

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            if (text.isBlank()) "https://qrcraft.studio/pro" else text,
            BarcodeFormat.QR_CODE,
            0, 0, // minimal size to get modules
            hints
        )

        val bmpWidth = width
        val bmpHeight = height
        val bmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        
        // Draw background
        canvas.drawColor(config.backgroundColor.toArgb())

        val modulesCount = bitMatrix.width
        val moduleSizeW = bmpWidth.toFloat() / modulesCount
        val moduleSizeH = bmpHeight.toFloat() / modulesCount

        val paint = Paint().apply {
            isAntiAlias = true
            if (config.colorEnd != null) {
                shader = LinearGradient(
                    0f, 0f, bmpWidth.toFloat(), bmpHeight.toFloat(),
                    config.colorStart.toArgb(), config.colorEnd.toArgb(),
                    Shader.TileMode.CLAMP
                )
            } else {
                color = config.colorStart.toArgb()
            }
        }

        for (x in 0 until modulesCount) {
            for (y in 0 until modulesCount) {
                if (bitMatrix[x, y]) {
                    val left = x * moduleSizeW
                    val top = y * moduleSizeH
                    val right = left + moduleSizeW
                    val bottom = top + moduleSizeH

                    // Check if module is part of an eye
                    val eyePart = getEyePart(x, y, modulesCount)
                    if (eyePart != null) {
                        drawEyePart(canvas, eyePart, left, top, right, bottom, paint, config)
                    } else {
                        // Regular module
                        drawModule(canvas, left, top, right, bottom, paint, config.moduleStyle)
                    }
                }
            }
        }

        // If logo is provided, draw it in the center
        if (config.logoBitmap != null) {
            val logoSize = bmpWidth / 5 
            val left = (bmpWidth - logoSize) / 2f
            val top = (bmpHeight - logoSize) / 2f
            
            val bgPaint = Paint().apply {
                color = AndroidColor.WHITE
                isAntiAlias = true
            }
            canvas.drawRoundRect(
                left - 8f, top - 8f, left + logoSize + 8f, top + logoSize + 8f,
                16f, 16f, bgPaint
            )

            val scaledLogo = Bitmap.createScaledBitmap(config.logoBitmap, logoSize, logoSize, true)
            canvas.drawBitmap(scaledLogo, left, top, null)
        }

        return bmp
    }

    private enum class EyePart { FRAME, BALL }

    private fun getEyePart(x: Int, y: Int, modulesCount: Int): EyePart? {
        val relX: Int
        val relY: Int

        if (x < 7 && y < 7) { // Top-left
            relX = x; relY = y
        } else if (x >= modulesCount - 7 && y < 7) { // Top-right
            relX = x - (modulesCount - 7); relY = y
        } else if (x < 7 && y >= modulesCount - 7) { // Bottom-left
            relX = x; relY = y - (modulesCount - 7)
        } else {
            return null
        }

        if (relX == 0 || relX == 6 || relY == 0 || relY == 6) return EyePart.FRAME
        if (relX in 2..4 && relY in 2..4) return EyePart.BALL
        return null
    }

    private fun drawEyePart(
        canvas: Canvas, part: EyePart,
        l: Float, t: Float, r: Float, b: Float,
        paint: Paint, config: QRStylingConfig
    ) {
        val style = if (part == EyePart.FRAME) config.eyeFrameStyle else config.eyeBallStyle
        val size = r - l
        
        when (style) {
            QREyeStyle.SQUARE -> canvas.drawRect(l, t, r, b, paint)
            QREyeStyle.CIRCLE -> canvas.drawCircle(l + size/2f, t + size/2f, size/2f, paint)
            QREyeStyle.ROUNDED -> canvas.drawRoundRect(l, t, r, b, size/3f, size/3f, paint)
        }
    }

    private fun drawModule(
        canvas: Canvas, l: Float, t: Float, r: Float, b: Float,
        paint: Paint, style: QRModuleStyle
    ) {
        val size = r - l
        when (style) {
            QRModuleStyle.SQUARE -> canvas.drawRect(l, t, r, b, paint)
            QRModuleStyle.DOTS -> canvas.drawCircle(l + size/2f, t + size/2f, size/2.2f, paint)
            QRModuleStyle.ROUNDED -> canvas.drawRoundRect(l + 0.5f, t + 0.5f, r - 0.5f, b - 0.5f, size/3f, size/3f, paint)
            QRModuleStyle.EXTRA_ROUNDED -> canvas.drawRoundRect(l, t, r, b, size/2f, size/2f, paint)
        }
    }
}
