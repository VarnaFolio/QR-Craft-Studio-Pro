package com.example.ui.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.viewmodel.QRCodeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: QRCodeViewModel,
    onNavigateCreate: () -> Unit = {},
    onNavigateHistory: () -> Unit = {},
    onNavigatePro: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var scannedResult by remember { mutableStateOf<String?>(null) }
    var isScanningActive by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        containerColor = Color(0xFF120C1F),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A122B))
            ) {
                // AdMob Banner Placeholder
                Box(
                    modifier = Modifier
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
                        selected = false,
                        onClick = onNavigateCreate,
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Create") },
                        label = { Text("CREATE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFF7E709C),
                            unselectedTextColor = Color(0xFF7E709C)
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
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scanner") },
                        label = { Text("SCANNER", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cameraPermissionState.status.isGranted) {
                if (isScanningActive) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            val executor = ContextCompat.getMainExecutor(ctx)

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val barcodeScanner = BarcodeScanning.getClient()
                                val analysisExecutor = Executors.newSingleThreadExecutor()

                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()

                                imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                for (barcode in barcodes) {
                                                    barcode.rawValue?.let { value ->
                                                        if (isScanningActive) {
                                                            isScanningActive = false
                                                            scannedResult = value
                                                            viewModel.insert(
                                                                content = value,
                                                                type = "Scanned",
                                                                category = if (value.startsWith("http")) "URL Link" else "Free Text"
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                // Handle failure if needed
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        ctx as androidx.lifecycle.LifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (exc: Exception) {
                                    // Handle binding failure
                                }
                            }, executor)

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay viewfinder frame
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .border(2.dp, Color(0xFFB07AFF), RoundedCornerShape(24.dp))
                        )
                    }

                    // Instruction Banner at Top
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xCC1B1429)),
                        border = BorderStroke(1.dp, Color(0xFF3B2859))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color(0xFFB07AFF)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Align QR code inside the frame to scan automatically",
                                color = Color(0xFFF3F0F9),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // Result Bottom Card / Dialog view when scan completes
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xE6120C1F))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1429)),
                            border = BorderStroke(1.dp, Color(0xFF9C6ADE))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color(0xFF2E204A), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color(0xFFB07AFF),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "QR Code Scanned!",
                                    color = Color(0xFFF3F0F9),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF120C1F)),
                                    border = BorderStroke(1.dp, Color(0xFF3B2859))
                                ) {
                                    Text(
                                        text = scannedResult ?: "",
                                        color = Color(0xFFABA1BF),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            scannedResult?.let {
                                                clipboardManager.setText(AnnotatedString(it))
                                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E204A))
                                    ) {
                                        Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Copy", fontWeight = FontWeight.Bold)
                                    }

                                    if (scannedResult?.startsWith("http") == true) {
                                        Button(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedResult))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                                        ) {
                                            Icon(Icons.Outlined.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Open", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        isScanningActive = true
                                        scannedResult = null
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF9C6ADE))
                                ) {
                                    Text("Scan Another", color = Color(0xFFB07AFF), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Permission request screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Camera Permission Required",
                        color = Color(0xFFF3F0F9),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "QR Craft Studio needs camera access to scan QR codes seamlessly.",
                        color = Color(0xFFABA1BF),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Grant Permission", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
