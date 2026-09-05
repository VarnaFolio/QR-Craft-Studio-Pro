package com.example.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object SvgGenerator {
    fun generateSvg(content: String, size: Int = 512): String {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height

        val sb = StringBuilder()
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 $width $height\" width=\"100%\" height=\"100%\">\n")
        sb.append("<rect width=\"100%\" height=\"100%\" fill=\"#FFFFFF\" />\n")
        sb.append("<path d=\"")

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitMatrix[x, y]) {
                    sb.append("M$x,$y h1 v1 h-1 z ")
                }
            }
        }

        sb.append("\" fill=\"#000000\" />\n")
        sb.append("</svg>")
        return sb.toString()
    }
}
