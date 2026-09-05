package com.example.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.QRCodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProScreen(
    viewModel: QRCodeViewModel,
    onNavigateCreate: () -> Unit = {},
    onNavigateHistory: () -> Unit = {},
    onNavigateScanner: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("annual") } // "monthly" or "annual"
    var isProActive by remember {
        mutableStateOf(
            context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                .getBoolean("is_pro", false)
        )
    }

    Scaffold(
        containerColor = Color(0xFF120C1F),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A122B))
            ) {
                // If Pro is active, no ad banner. Otherwise show placeholder banner or ad info
                if (!isProActive) {
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
                            text = "GOOGLE ADMOB BANNER (REMOVED IN PRO)",
                            color = Color(0xFF8C7DAE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
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
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Filled.Star, contentDescription = "Pro", tint = Color(0xFFFFD700)) },
                        label = { Text("PRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFFD700),
                            selectedTextColor = Color(0xFFFFD700),
                            indicatorColor = Color(0xFF3B2859)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isProActive) "PRO VIP MEMBER ACTIVE" else "QR CRAFT VIP PRO",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isProActive) "Enjoy Your Unlimited Pro Perks!" else "Unlock Full Creative Power",
                color = Color(0xFFF3F0F9),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isProActive) "All features, templates, and exports are fully unlocked." else "Remove all ads, unlock unlimited styled QR codes, HD vector exports, and B2B tools.",
                color = Color(0xFFABA1BF),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Perks Checklist Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1429)),
                border = BorderStroke(1.dp, Color(0xFF3B2859))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PerkRow(text = "100% Ad-Free Experience (No Banners or Interstitials)")
                    PerkRow(text = "Unlimited Colored & Gradient QR Code Styles")
                    PerkRow(text = "Center Logo Embedding & Error Correction Level H")
                    PerkRow(text = "HD PNG, SVG & PDF Vector Exports")
                    PerkRow(text = "Dynamic QR Codes & Google Review Generator")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isProActive) {
                // Subscription Plan Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = "monthly" },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlan == "monthly") Color(0xFF2E204A) else Color(0xFF1B1429)
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (selectedPlan == "monthly") Color(0xFFB07AFF) else Color(0xFF3B2859)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Monthly", color = Color(0xFFABA1BF), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "$4.99", color = Color(0xFFF3F0F9), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "per month", color = Color(0xFF7E709C), fontSize = 11.sp)
                        }
                    }

                    // Annual Card (Best Value)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = "annual" },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlan == "annual") Color(0xFF2E204A) else Color(0xFF1B1429)
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (selectedPlan == "annual") Color(0xFFFFD700) else Color(0xFF3B2859)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFD700), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "BEST VALUE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "$29.99", color = Color(0xFFF3F0F9), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "per year ($2.50/mo)", color = Color(0xFFFFD700), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subscribe / Unlock Button (Play Billing integration simulation + live handler)
                Button(
                    onClick = {
                        context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_pro", true)
                            .apply()
                        isProActive = true
                        Toast.makeText(context, "🎉 Welcome to QR Craft Pro VIP!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedPlan == "annual") "Unlock Annual VIP ($29.99)" else "Unlock Monthly VIP ($4.99)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        Toast.makeText(context, "Checking Google Play Billing purchases...", Toast.LENGTH_SHORT).show()
                        // Simulate restore
                        val prefs = context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                        if (prefs.getBoolean("is_pro", false)) {
                            isProActive = true
                            Toast.makeText(context, "Pro subscription restored successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No active subscription found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(text = "Restore Purchases", color = Color(0xFFABA1BF), fontSize = 13.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        context.getSharedPreferences("qr_craft_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_pro", false)
                            .apply()
                        isProActive = false
                        Toast.makeText(context, "Pro status deactivated for testing.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text("Deactivate Pro (Reset for Testing)", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PerkRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color(0xFFF3F0F9),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
