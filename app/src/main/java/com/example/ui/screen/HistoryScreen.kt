package com.example.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.QRCodeEntity
import com.example.viewmodel.QRCodeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: QRCodeViewModel,
    onNavigateCreate: () -> Unit = {},
    onNavigateScanner: () -> Unit = {},
    onNavigatePro: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val qrList by viewModel.qrCodes.collectAsStateWithLifecycle()

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

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
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
                        label = { Text("HISTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "History & Archive",
                        color = Color(0xFFF3F0F9),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${qrList.size} items stored offline (Room DB)",
                        color = Color(0xFFB07AFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (qrList.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAll() }) {
                        Text("Clear All", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (qrList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFF1B1429), CircleShape)
                                .border(1.dp, Color(0xFF3B2859), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = null,
                                tint = Color(0xFF7E709C),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No QR codes in history yet",
                            color = Color(0xFFF3F0F9),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Generated and scanned codes will appear here automatically.",
                            color = Color(0xFF7E709C),
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(qrList, key = { it.id }) { item ->
                        QRCodeHistoryItemCard(
                            item = item,
                            dateFormat = dateFormat,
                            onFavoriteClick = { viewModel.toggleFavorite(item.id, item.isFavorite) },
                            onDeleteClick = { viewModel.delete(item) },
                            onCopyClick = {
                                clipboardManager.setText(AnnotatedString(item.content))
                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onShareClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, item.content)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QRCodeHistoryItemCard(
    item: QRCodeEntity,
    dateFormat: SimpleDateFormat,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1429)),
        border = BorderStroke(1.dp, Color(0xFF3B2859))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (item.type == "Generated") Color(0xFF7C3AED) else Color(0xFF0284C7),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.type.uppercase(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.category,
                        color = Color(0xFFB07AFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) Color(0xFFFFD700) else Color(0xFF7E709C)
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.content,
                color = Color(0xFFF3F0F9),
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(item.timestamp)),
                    color = Color(0xFF7E709C),
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onCopyClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFB07AFF))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", color = Color(0xFFB07AFF), fontSize = 11.sp)
                    }

                    TextButton(
                        onClick = onShareClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
