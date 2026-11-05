package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreatorProfile
import com.example.ui.components.CreatorAvatarView
import com.example.ui.components.QrEyeStyle
import com.example.ui.components.QrRenderConfig
import com.example.ui.components.StylizedQrCode
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandTertiary
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QrStudioScreen(
    profile: CreatorProfile?,
    qrConfig: QrRenderConfig,
    onUpdateConfig: (QrRenderConfig) -> Unit,
    onTestScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showStandeeDialog by remember { mutableStateOf(false) }

    val cleanHandle = profile?.handle?.trim()?.removePrefix("@")?.lowercase()?.ifEmpty { "alexa_aura" } ?: "alexa_aura"
    val qrContent = "https://gen-lang-client-0468598937.web.app/?handle=$cleanHandle"

    val colorOptions = listOf(
        Pair("Cyan", BrandSecondary),
        Pair("Violet", BrandPrimary),
        Pair("Amber", BrandWarning),
        Pair("Emerald", BrandSuccess),
        Pair("Rose", BrandTertiary),
        Pair("White", Color.White)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "QR Code Physical Studio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Custom-branded QR codes for physical events, popups & business cards",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        // Live QR Code Display Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qr_preview_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Stylized QR View
                    StylizedQrCode(
                        content = qrContent,
                        modifier = Modifier.size(220.dp),
                        config = qrConfig,
                        avatarPath = profile?.avatarDrawableName ?: ""
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "gen-lang-client-0468598937.web.app/?handle=$cleanHandle",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandSecondary
                    )

                    Text(
                        text = "Point any phone camera to instantly load profile & store",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Quick Sharing Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Scan my MultiLink QR Code or visit: $qrContent"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share MultiLink QR"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 38.dp)
                        .testTag("share_qr_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }

                Button(
                    onClick = onTestScan,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 38.dp)
                        .testTag("test_qr_scan_button")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(15.dp), tint = BrandDarkBackground)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground, maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("MultiLink URL", qrContent))
                        Toast.makeText(context, "Bio URL copied!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 38.dp)
                        .testTag("copy_bio_url_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp, maxLines = 1)
                }
            }
        }

        // Customizer Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qr_customizer_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Customize QR Styling",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Color swatches
                    Column {
                        Text(
                            text = "Brand Accent Color",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            colorOptions.forEach { (name, color) ->
                                val isSelected = qrConfig.foregroundColor == color
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) TextPrimary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onUpdateConfig(qrConfig.copy(foregroundColor = color))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (color == Color.White) BrandDarkBackground else TextPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Eye Shape
                    Column {
                        Text(
                            text = "Module Eye Style",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = qrConfig.eyeStyle == QrEyeStyle.ROUNDED,
                                onClick = { onUpdateConfig(qrConfig.copy(eyeStyle = QrEyeStyle.ROUNDED)) },
                                label = { Text("Smooth Rounded", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = qrConfig.eyeStyle == QrEyeStyle.DOTS,
                                onClick = { onUpdateConfig(qrConfig.copy(eyeStyle = QrEyeStyle.DOTS)) },
                                label = { Text("Modern Dots", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = qrConfig.eyeStyle == QrEyeStyle.SQUARE,
                                onClick = { onUpdateConfig(qrConfig.copy(eyeStyle = QrEyeStyle.SQUARE)) },
                                label = { Text("Classic", fontSize = 11.sp) }
                            )
                        }
                    }

                    // Center Avatar Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Embed Creator Avatar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Display profile portrait in center of QR code",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = qrConfig.showCenterAvatar,
                            onCheckedChange = { onUpdateConfig(qrConfig.copy(showCenterAvatar = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextPrimary,
                                checkedTrackColor = BrandPrimary
                            )
                        )
                    }
                }
            }
        }

        // Physical Booth Standee Preview Trigger
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStandeeDialog = true }
                    .testTag("open_standee_preview_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Event Standee & Flyer Preview",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Preview printable table tent for conferences & booth tables",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        Icons.Default.Print,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Physical Event Standee Mockup Dialog
    if (showStandeeDialog && profile != null) {
        StandeeMockupDialog(
            profile = profile,
            qrConfig = qrConfig,
            qrContent = qrContent,
            onDismiss = { showStandeeDialog = false }
        )
    }
}

@Composable
fun StandeeMockupDialog(
    profile: CreatorProfile,
    qrConfig: QrRenderConfig,
    qrContent: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Event Standee Mockup",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Printable acrylic tent card mockup
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, BrandPrimary, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CONNECT & SUPPORT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandSecondary,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, BrandSecondary, CircleShape)
                        ) {
                            CreatorAvatarView(
                                avatarPath = profile.avatarDrawableName,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = profile.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "@${profile.handle}",
                            fontSize = 12.sp,
                            color = BrandPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        StylizedQrCode(
                            content = qrContent,
                            modifier = Modifier.size(160.dp),
                            config = qrConfig,
                            avatarPath = profile.avatarDrawableName
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Scan with your camera for digital products, shaders & 1-on-1 bookings",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Powered by MultiLink",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Mockup saved for high-res print export!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Print / Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = BrandDarkSurface
    )
}
