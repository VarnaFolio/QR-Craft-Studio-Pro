package com.example.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object SvgGenerator {
    fun generateSvg(
        content: String,
        config: QRStylingConfig = QRStylingConfig(),
        size: Int = 512
    ): String {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = if (config.logoBitmap != null) ErrorCorrectionLevel.H else ErrorCorrectionLevel.M

        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)
        val modulesCount = bitMatrix.width
        
        val colorStartHex = String.format("#%06X", (0xFFFFFF and config.colorStart.toArgb()))
        val bgColorHex = String.format("#%06X", (0xFFFFFF and config.backgroundColor.toArgb()))
        val bgOpacity = config.backgroundColor.alpha

        val sb = StringBuilder()
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 $modulesCount $modulesCount\" width=\"100%\" height=\"100%\">\n")
        
        // Background
        if (config.backgroundColor != Color.Transparent) {
            sb.append("<rect width=\"$modulesCount\" height=\"$modulesCount\" fill=\"$bgColorHex\" fill-opacity=\"$bgOpacity\" />\n")
        }

        // Gradient definition
        if (config.colorEnd != null) {
            val colorEndHex = String.format("#%06X", (0xFFFFFF and config.colorEnd.toArgb()))
            sb.append("<defs>\n")
            sb.append("<linearGradient id=\"qrGrad\" x1=\"0%\" y1=\"0%\" x2=\"100%\" y2=\"100%\">\n")
            sb.append("<stop offset=\"0%\" stop-color=\"$colorStartHex\" />\n")
            sb.append("<stop offset=\"100%\" stop-color=\"$colorEndHex\" />\n")
            sb.append("</linearGradient>\n")
            sb.append("</defs>\n")
        }

        val fill = if (config.colorEnd != null) "url(#qrGrad)" else colorStartHex

        for (y in 0 until modulesCount) {
            for (x in 0 until modulesCount) {
                if (bitMatrix[x, y]) {
                    val eyePart = getEyePart(x, y, modulesCount)
                    if (eyePart != null) {
                        appendEyePath(sb, eyePart, x, y, fill, config)
                    } else {
                        appendModulePath(sb, x, y, fill, config.moduleStyle)
                    }
                }
            }
        }

        // If logo is provided, embed it as base64 in SVG
        if (config.logoBitmap != null) {
            val logoSize = modulesCount * 0.2f
            val offset = (modulesCount - logoSize) / 2f
            
            // Draw white background for logo contrast
            sb.append("<rect x=\"${offset - 0.2}\" y=\"${offset - 0.2}\" width=\"${logoSize + 0.4}\" height=\"${logoSize + 0.4}\" rx=\"0.4\" ry=\"0.4\" fill=\"#FFFFFF\" />\n")
            
            val outputStream = ByteArrayOutputStream()
            config.logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val base64Logo = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            
            sb.append("<image x=\"$offset\" y=\"$offset\" width=\"$logoSize\" height=\"$logoSize\" href=\"data:image/png;base64,$base64Logo\" />\n")
        }

        sb.append("</svg>")
        return sb.toString()
    }

    private enum class EyePart { FRAME, BALL }

    private fun getEyePart(x: Int, y: Int, modulesCount: Int): EyePart? {
        val relX: Int; val relY: Int
        if (x < 7 && y < 7) { relX = x; relY = y }
        else if (x >= modulesCount - 7 && y < 7) { relX = x - (modulesCount - 7); relY = y }
        else if (x < 7 && y >= modulesCount - 7) { relX = x; relY = y - (modulesCount - 7) }
        else return null

        if (relX == 0 || relX == 6 || relY == 0 || relY == 6) return EyePart.FRAME
        if (relX in 2..4 && relY in 2..4) return EyePart.BALL
        return null
    }

    private fun appendEyePath(sb: StringBuilder, part: EyePart, x: Int, y: Int, fill: String, config: QRStylingConfig) {
        val style = if (part == EyePart.FRAME) config.eyeFrameStyle else config.eyeBallStyle
        when (style) {
            QREyeStyle.SQUARE -> sb.append("<rect x=\"$x\" y=\"$y\" width=\"1\" height=\"1\" fill=\"$fill\" />\n")
            QREyeStyle.CIRCLE -> sb.append("<circle cx=\"${x + 0.5}\" cy=\"${y + 0.5}\" r=\"0.5\" fill=\"$fill\" />\n")
            QREyeStyle.ROUNDED -> sb.append("<rect x=\"$x\" y=\"$y\" width=\"1\" height=\"1\" rx=\"0.3\" ry=\"0.3\" fill=\"$fill\" />\n")
        }
    }

    private fun appendModulePath(sb: StringBuilder, x: Int, y: Int, fill: String, style: QRModuleStyle) {
        when (style) {
            QRModuleStyle.SQUARE -> sb.append("<rect x=\"$x\" y=\"$y\" width=\"1\" height=\"1\" fill=\"$fill\" />\n")
            QRModuleStyle.DOTS -> sb.append("<circle cx=\"${x + 0.5}\" cy=\"${y + 0.5}\" r=\"0.4\" fill=\"$fill\" />\n")
            QRModuleStyle.ROUNDED -> sb.append("<rect x=\"$x\" y=\"$y\" width=\"1\" height=\"1\" rx=\"0.3\" ry=\"0.3\" fill=\"$fill\" />\n")
            QRModuleStyle.EXTRA_ROUNDED -> sb.append("<circle cx=\"${x + 0.5}\" cy=\"${y + 0.5}\" r=\"0.5\" fill=\"$fill\" />\n")
        }
    }
}
