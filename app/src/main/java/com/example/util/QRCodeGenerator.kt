package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object QRCodeGenerator {

    fun generateQRCode(
        text: String,
        width: Int = 512,
        height: Int = 512,
        foregroundColor: Color = Color(0xFF1B1429),
        backgroundColor: Color = Color.White,
        logoBitmap: Bitmap? = null
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            if (logoBitmap != null) {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            } else {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            }
            put(EncodeHintType.MARGIN, 1)
        }

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(
            if (text.isBlank()) "https://qrcraft.studio/pro" else text,
            BarcodeFormat.QR_CODE,
            width,
            height,
            hints
        )

        val bmpWidth = bitMatrix.width
        val bmpHeight = bitMatrix.height
        val bmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)

        val fgColor = foregroundColor.toArgb()
        val bgColor = backgroundColor.toArgb()

        for (x in 0 until bmpWidth) {
            for (y in 0 until bmpHeight) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) fgColor else bgColor)
            }
        }

        // If logo is provided, draw it in the center
        if (logoBitmap != null) {
            val canvas = Canvas(bmp)
            val logoSize = bmpWidth / 5 // 20% of QR size
            val left = (bmpWidth - logoSize) / 2f
            val top = (bmpHeight - logoSize) / 2f
            
            // Draw white background circle/square for logo contrast
            val paint = android.graphics.Paint().apply {
                color = AndroidColor.WHITE
                isAntiAlias = true
            }
            canvas.drawRoundRect(
                left - 8f,
                top - 8f,
                left + logoSize + 8f,
                top + logoSize + 8f,
                16f,
                16f,
                paint
            )

            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoSize, logoSize, true)
            canvas.drawBitmap(scaledLogo, left, top, null)
        }

        return bmp
    }
}
