package com.example.ui.screen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.QRCodeGenerator
import com.example.util.QRModuleStyle
import com.example.util.QREyeStyle
import com.example.util.QRStylingConfig
import com.example.util.SvgGenerator
import com.example.viewmodel.QRCodeViewModel
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    viewModel: QRCodeViewModel? = null,
    onNavigateHistory: () -> Unit = {},
    onNavigateScanner: () -> Unit = {},
    onNavigatePro: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("URL Link") }
    var qrInputText by remember { mutableStateOf("https://qrcraft.studio/pro") }
    var selectedColorIndex by remember { mutableStateOf(0) }
    var stylingConfig by remember { mutableStateOf(QRStylingConfig()) }
    var showProBanner by remember { mutableStateOf<String?>(null) }

    val logoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it)) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                stylingConfig = stylingConfig.copy(logoBitmap = bitmap)
                Toast.makeText(context, "Logo Loaded (ECL H Enabled)", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading logo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Generate QR Bitmap
    val qrBitmap = remember(qrInputText, stylingConfig) {
        QRCodeGenerator.generateQRCode(
            text = qrInputText.ifBlank { "https://qrcraft.studio/pro" },
            config = stylingConfig
        )
    }

    Scaffold(
        containerColor = Color(0xFF120C1F),
        bottomBar = {
            Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A122B))
            ) {
                // AdMob Banner Placeholder
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(50.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF3B2859),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(Color(0xFF221736), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GOOGLE ADMOB BANNER PLACEHOLDER",
                        color = Color(0xFF8C7DAE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color(0xFF1A122B),
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Create") },
                        label = { Text("CREATE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFB07AFF),
                            selectedTextColor = Color(0xFFB07AFF),
                            unselectedIconColor = Color(0xFF7E709C),
                            unselectedTextColor = Color(0xFF7E709C),
                            indicatorColor = Color(0xFF2E204A)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateHistory,
                        icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
                        label = { Text("HISTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF7E709C),
                            unselectedTextColor = Color(0xFF7E709C)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateScanner,
                        icon = { Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scanner") },
                        label = { Text("SCANNER", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF7E709C),
                            unselectedTextColor = Color(0xFF7E709C)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigatePro,
                        icon = { Icon(Icons.Filled.Star, contentDescription = "Pro", tint = Color(0xFFFFD700)) },
                        label = { Text("PRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFFFFD700),
                            unselectedTextColor = Color(0xFFFFD700)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showProBanner != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigatePro() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221736)),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF3B2859), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Достъпно само при PRO версия",
                                color = Color(0xFFFFD700),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = showProBanner!!,
                                color = Color(0xFFABA1BF),
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = { showProBanner = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFFABA1BF))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Top App Bar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF9C6ADE), Color(0xFF38BDF8))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "QR Craft Studio",
                            color = Color(0xFFF3F0F9),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PHASE 1: MVP ENGINE",
                            color = Color(0xFFB07AFF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                IconButton(
                    onClick = onNavigatePro,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF221736), CircleShape)
                        .border(1.dp, Color(0xFF3B2859), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFFDCD6EE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Chips (URL Link, Wi-Fi, Free Text, Email)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("URL Link", "Wi-Fi", "Free Text", "Email")
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = category
                            if (category == "URL Link") qrInputText = "https://qrcraft.studio/pro"
                            else if (category == "Wi-Fi") qrInputText = "WIFI:S:MyHomeWiFi;T:WPA;P:secret123;;"
                            else if (category == "Free Text") qrInputText = "Welcome to QR Craft Studio Pro!"
                            else if (category == "Email") qrInputText = "mailto:support@qrcraft.studio?subject=Inquiry"
                        },
                        label = { Text(category, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E142E),
                            labelColor = Color(0xFFABA1BF),
                            selectedContainerColor = Color(0xFF7C3AED),
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFF3B2859),
                            selectedBorderColor = Color(0xFF9C6ADE)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Input Field Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1429)),
                border = BorderStroke(1.dp, Color(0xFF3B2859))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Target $selectedCategory",
                        color = Color(0xFFB07AFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = qrInputText,
                        onValueChange = { qrInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF9C6ADE),
                            unfocusedBorderColor = Color(0xFF3B2859),
                            focusedContainerColor = Color(0xFF120C1F),
                            unfocusedContainerColor = Color(0xFF120C1F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // QR Preview Container Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181026)),
                border = BorderStroke(1.dp, Color(0xFF2D2042))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // QR Code Image Preview Box
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Customization Tool Buttons (Colors, Style, Logo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Colors Tool
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    selectedColorIndex = (selectedColorIndex + 1) % 6
                                    val newConfig = when(selectedColorIndex) {
                                        0 -> stylingConfig.copy(colorStart = Color(0xFF1B1429), colorEnd = null)
                                        1 -> stylingConfig.copy(colorStart = Color(0xFF7C3AED), colorEnd = null)
                                        2 -> stylingConfig.copy(colorStart = Color(0xFF0284C7), colorEnd = null)
                                        3 -> stylingConfig.copy(colorStart = Color(0xFF9C6ADE), colorEnd = Color(0xFF38BDF8)) // Gradient 1
                                        4 -> stylingConfig.copy(colorStart = Color(0xFFF43F5E), colorEnd = Color(0xFFFB923C)) // Gradient 2
                                        else -> stylingConfig.copy(colorStart = Color(0xFF059669), colorEnd = null)
                                    }
                                    stylingConfig = newConfig
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF221736), CircleShape)
                                    .border(1.dp, Color(0xFF9C6ADE), CircleShape)
                            ) {
                                Icon(Icons.Outlined.Palette, contentDescription = "Colors", tint = Color(0xFFB07AFF))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("COLORS", color = Color(0xFFABA1BF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Style Tool
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    val prefs = context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                                    val isPro = prefs.getBoolean("is_pro", false)
                                    if (!isPro) {
                                        showProBanner = "Кликнете тук, за да отключите персонализираните стилове и всички PRO функции!"
                                    } else {
                                        // Cycle through styling presets
                                        val nextConfig = when {
                                            stylingConfig.moduleStyle == QRModuleStyle.SQUARE -> stylingConfig.copy(
                                                moduleStyle = QRModuleStyle.DOTS,
                                                eyeFrameStyle = QREyeStyle.CIRCLE,
                                                eyeBallStyle = QREyeStyle.CIRCLE
                                            )
                                            stylingConfig.moduleStyle == QRModuleStyle.DOTS -> stylingConfig.copy(
                                                moduleStyle = QRModuleStyle.ROUNDED,
                                                eyeFrameStyle = QREyeStyle.ROUNDED,
                                                eyeBallStyle = QREyeStyle.SQUARE
                                            )
                                            stylingConfig.moduleStyle == QRModuleStyle.ROUNDED -> stylingConfig.copy(
                                                moduleStyle = QRModuleStyle.EXTRA_ROUNDED,
                                                eyeFrameStyle = QREyeStyle.ROUNDED,
                                                eyeBallStyle = QREyeStyle.CIRCLE
                                            )
                                            else -> stylingConfig.copy(
                                                moduleStyle = QRModuleStyle.SQUARE,
                                                eyeFrameStyle = QREyeStyle.SQUARE,
                                                eyeBallStyle = QREyeStyle.SQUARE
                                            )
                                        }
                                        stylingConfig = nextConfig
                                        Toast.makeText(context, "Style Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF221736), CircleShape)
                                    .border(1.dp, if (stylingConfig.moduleStyle != QRModuleStyle.SQUARE) Color(0xFF9C6ADE) else Color(0xFF3B2859), CircleShape)
                            ) {
                                Icon(Icons.Outlined.GridOn, contentDescription = "Style", tint = if (stylingConfig.moduleStyle != QRModuleStyle.SQUARE) Color(0xFFB07AFF) else Color(0xFFABA1BF))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("STYLE", color = if (stylingConfig.moduleStyle != QRModuleStyle.SQUARE) Color(0xFFB07AFF) else Color(0xFFABA1BF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Logo Tool
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    val prefs = context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                                    val isPro = prefs.getBoolean("is_pro", false)
                                    if (!isPro) {
                                        showProBanner = "Кликнете тук, за да отключите вграждането на лого и всички PRO функции!"
                                    } else {
                                        if (stylingConfig.logoBitmap == null) {
                                            logoLauncher.launch("image/*")
                                        } else {
                                            stylingConfig = stylingConfig.copy(logoBitmap = null)
                                            Toast.makeText(context, "Logo Removed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF221736), CircleShape)
                                    .border(1.dp, if (stylingConfig.logoBitmap != null) Color(0xFF9C6ADE) else Color(0xFF3B2859), CircleShape)
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = "Logo", tint = if (stylingConfig.logoBitmap != null) Color(0xFFB07AFF) else Color(0xFFABA1BF))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("LOGO", color = if (stylingConfig.logoBitmap != null) Color(0xFFB07AFF) else Color(0xFFABA1BF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save to Gallery (PNG) Prominent Button
            Button(
                onClick = {
                    try {
                        val filename = "QRCode_${System.currentTimeMillis()}.png"
                        var fos: OutputStream? = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val resolver = context.contentResolver
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/QRCraftStudio")
                            }
                            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            if (imageUri != null) {
                                fos = resolver.openOutputStream(imageUri)
                            }
                        } else {
                            // For older versions or fallback
                            val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
                            val imageFile = java.io.File(imagesDir, filename)
                            fos = java.io.FileOutputStream(imageFile)
                        }

                        fos?.use {
                            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            viewModel?.insert(qrInputText, "Generated", selectedCategory)
                            Toast.makeText(context, "Successfully saved to Gallery & History!", Toast.LENGTH_LONG).show()
                        } ?: run {
                            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error saving: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED)
                )
            ) {
                Text(
                    text = "Save to Gallery (PNG)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save as SVG (Pro Vector) Button
            OutlinedButton(
                onClick = {
                    val prefs = context.getSharedPreferences("qr_craft_prefs", android.content.Context.MODE_PRIVATE)
                    val isPro = prefs.getBoolean("is_pro", false)
                    if (!isPro) {
                        showProBanner = "Кликнете тук, за да отключите векторния SVG експорт и всички PRO функции!"
                        return@OutlinedButton
                    }

                    try {
                        val svgContent = SvgGenerator.generateSvg(qrInputText, stylingConfig)
                        val filename = "QRCode_${System.currentTimeMillis()}.svg"
                        var fos: OutputStream? = null

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val resolver = context.contentResolver
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/svg+xml")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/QRCraftStudio")
                            }
                            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                            if (uri != null) {
                                fos = resolver.openOutputStream(uri)
                            }
                        } else {
                            val docsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
                            if (!docsDir.exists()) docsDir.mkdirs()
                            val file = java.io.File(docsDir, filename)
                            fos = java.io.FileOutputStream(file)
                        }

                        fos?.use {
                            it.write(svgContent.toByteArray())
                            Toast.makeText(context, "Successfully saved SVG Vector to Documents!", Toast.LENGTH_LONG).show()
                        } ?: run {
                            Toast.makeText(context, "Failed to save SVG file", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error saving SVG: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFFFFD700)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700))
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export as SVG (Pro Vector)",
                    color = Color(0xFFFFD700),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
