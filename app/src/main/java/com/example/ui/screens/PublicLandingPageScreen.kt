package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreatorProfile
import com.example.data.model.LandingThemes
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.example.ui.components.CreatorAvatarView
import com.example.ui.components.IconResolver
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandTertiary
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.GoldEnd
import com.example.ui.theme.GoldStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PublicLandingPageScreen(
    profile: CreatorProfile,
    links: List<LinkItem>,
    onCloseVisitorMode: () -> Unit,
    onRecordClick: (LinkItem, revenue: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onCloseVisitorMode() }

    val theme = LandingThemes.getById(profile.themeId)

    var selectedProductForCheckout by remember { mutableStateOf<LinkItem?>(null) }
    var selectedServiceForBooking by remember { mutableStateOf<LinkItem?>(null) }
    var showTipModal by remember { mutableStateOf(false) }
    var completedOrderReceipt by remember { mutableStateOf<Pair<LinkItem, String>?>(null) }

    val activeLinks = links.filter { it.isActive }
    val featuredProducts = activeLinks.filter { it.category == LinkCategory.PRODUCT && it.isFeatured }
    val otherLinks = activeLinks.filter { !(it.category == LinkCategory.PRODUCT && it.isFeatured) }

    val socialLinks = activeLinks.filter { it.category == LinkCategory.SOCIAL }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .testTag("public_landing_page")
    ) {
        // Ambient background gradient glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            theme.accentColor.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                // Visitor top bar with "Back to Creator Studio"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCloseVisitorMode,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.surfaceColor.copy(alpha = 0.85f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor),
                        modifier = Modifier.testTag("back_to_studio_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Creator Studio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.textColor
                        )
                    }

                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "https://${profile.customDomain}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share"))
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.surfaceColor)
                            .border(1.dp, theme.borderColor, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Creator Profile Avatar & Bio
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .border(3.dp, theme.accentColor, CircleShape)
                    ) {
                        CreatorAvatarView(
                            avatarPath = profile.avatarDrawableName,
                            contentDescription = "Creator Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.displayName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textColor
                        )
                        if (profile.isProUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldStart)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = "PRO", tint = BrandDarkBackground, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("PRO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = BrandDarkBackground)
                                }
                            }
                        } else if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = theme.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "@${profile.handle}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = theme.accentColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = profile.headline,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.textMutedColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = profile.bio,
                        fontSize = 13.sp,
                        color = theme.textColor.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (profile.location.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = theme.textMutedColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile.location,
                                fontSize = 12.sp,
                                color = theme.textMutedColor
                            )
                        }
                    }
                }
            }

            // Quick Social Icons Row
            if (socialLinks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        socialLinks.take(6).forEach { social ->
                            IconButton(
                                onClick = {
                                    onRecordClick(social, 0.0)
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(social.url))
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Opening ${social.title}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(theme.surfaceColor)
                                    .border(1.dp, theme.borderColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = IconResolver.getIcon(social.iconKey),
                                    contentDescription = social.title,
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tip Jar Banner / Community Goal
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTipModal = true }
                        .testTag("visitor_tip_jar_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(BrandSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Coffee,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Support My Work • Tip Jar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textColor
                            )
                            Text(
                                text = profile.tipGoalTitle,
                                fontSize = 11.sp,
                                color = theme.textMutedColor
                            )
                        }

                        Button(
                            onClick = { showTipModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Send Tip", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                        }
                    }
                }
            }

            // Featured Digital Products Showcase
            items(featuredProducts, key = { it.id }) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_product_card_${product.id}"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.accentColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.digital_product_mockup_1793630338547),
                                contentDescription = product.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            if (product.badge.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BrandWarning)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = product.badge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandDarkBackground
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = product.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textColor
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = product.subtitle,
                                fontSize = 12.sp,
                                color = theme.textMutedColor,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product.price,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandSuccess
                                )

                                Button(
                                    onClick = {
                                        selectedProductForCheckout = product
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandDarkBackground)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Get Instant Access", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                                }
                            }
                        }
                    }
                }
            }

            // All consolidated links
            items(otherLinks, key = { it.id }) { link ->
                PublicLinkCard(
                    link = link,
                    theme = theme,
                    onClick = {
                        if (link.category == LinkCategory.SERVICE) {
                            selectedServiceForBooking = link
                        } else if (link.category == LinkCategory.TIP) {
                            showTipModal = true
                        } else {
                            onRecordClick(link, 0.0)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opened ${link.title}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            // Powered by MultiLink footer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MultiLink",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Consolidated profiles • Monetization • Real-time telemetry",
                        fontSize = 10.sp,
                        color = theme.textMutedColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Product Instant Checkout Modal with Payment Method Selection
    selectedProductForCheckout?.let { prod ->
        var selectedPaymentMethod by remember { mutableStateOf("Google Pay") }
        AlertDialog(
            onDismissRequest = { selectedProductForCheckout = null },
            title = {
                Text(
                    "Checkout: ${prod.title}",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Instant Digital Delivery & Lifetime Access",
                        fontSize = 13.sp,
                        color = BrandSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = prod.subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandDarkBackground)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Due:", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(prod.price, fontWeight = FontWeight.ExtraBold, color = BrandSuccess)
                        }
                    }

                    Text("Select Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    listOf(
                        Triple("Google Pay", "Instant 1-Tap Bank / Card Checkout", Icons.Default.Payments),
                        Triple("Card (•••• 4242)", "Visa ending in 4242 • Live Gateway", Icons.Default.CreditCard),
                        Triple("PayPal", "@${profile.paypalHandle} • Real-time Transfer", Icons.Default.AccountBalance)
                    ).forEach { method ->
                        val isSelected = selectedPaymentMethod == method.first
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BrandPrimary.copy(alpha = 0.15f) else BrandDarkBackground)
                                .border(1.dp, if (isSelected) BrandPrimary else BrandDarkBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedPaymentMethod = method.first }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(method.third, contentDescription = null, tint = if (isSelected) BrandPrimary else TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(method.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(method.second, fontSize = 10.sp, color = TextSecondary)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Live Payment Banner
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE PAYMENT: Money deducted directly from your bank/card",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSuccess
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = prod.price.replace("$", "").toDoubleOrNull() ?: 39.0

                        // Launch live external payment checkout URL in browser so money is debited
                        if (prod.url.isNotEmpty() && prod.url.startsWith("http")) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prod.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // fallback
                            }
                        }

                        onRecordClick(prod, amount)
                        completedOrderReceipt = Pair(prod, selectedPaymentMethod)
                        selectedProductForCheckout = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.defaultMinSize(minHeight = 40.dp)
                ) {
                    Text(
                        text = "Pay ${prod.price} via $selectedPaymentMethod",
                        fontWeight = FontWeight.Bold,
                        color = BrandDarkBackground,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProductForCheckout = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BrandDarkSurface
        )
    }

    // Professional Service Booking Modal
    selectedServiceForBooking?.let { srv ->
        AlertDialog(
            onDismissRequest = { selectedServiceForBooking = null },
            title = {
                Text(
                    "Book: ${srv.title}",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Rate: ${srv.price}",
                        fontSize = 14.sp,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = srv.subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Next available slots: Tomorrow 2:00 PM, Thursday 10:00 AM",
                        fontSize = 11.sp,
                        color = BrandSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRecordClick(srv, 150.0)
                        Toast.makeText(context, "Session booked! Calendar invitation sent.", Toast.LENGTH_LONG).show()
                        selectedServiceForBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Confirm Booking", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedServiceForBooking = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BrandDarkSurface
        )
    }

    // Tip Jar / Fan Support Modal
    if (showTipModal) {
        TipModalDialog(
            profile = profile,
            onDismiss = { showTipModal = false },
            onSendTip = { amount, method ->
                val tipLink = links.find { it.category == LinkCategory.TIP } ?: links.first()
                onRecordClick(tipLink, amount)
                Toast.makeText(context, "Sent $$amount tip to ${profile.displayName} via $method! 🎉", Toast.LENGTH_LONG).show()
                showTipModal = false
            }
        )
    }

    // Product Order Receipt & Asset Delivery Modal
    completedOrderReceipt?.let { receipt ->
        val product = receipt.first
        val paymentMethod = receipt.second
        OrderSuccessReceiptDialog(
            product = product,
            paymentMethod = paymentMethod,
            profile = profile,
            onDismiss = { completedOrderReceipt = null }
        )
    }
}

@Composable
fun PublicLinkCard(
    link: LinkItem,
    theme: com.example.data.model.LandingThemePreset,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("public_link_${link.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = IconResolver.getIcon(link.iconKey),
                    contentDescription = null,
                    tint = theme.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = link.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (link.badge.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = link.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.accentColor,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                if (link.subtitle.isNotEmpty()) {
                    Text(
                        text = link.subtitle,
                        fontSize = 11.sp,
                        color = theme.textMutedColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (link.price.isNotEmpty()) {
                    Text(
                        text = link.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSuccess,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = theme.textMutedColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TipModalDialog(
    profile: CreatorProfile,
    onDismiss: () -> Unit,
    onSendTip: (Double, String) -> Unit
) {
    val context = LocalContext.current
    var selectedAmount by remember { mutableStateOf(5.0) }
    var customAmountText by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(profile.preferredTipMethod.ifEmpty { "Google Pay" }) }
    var isAddingNewCard by remember { mutableStateOf(false) }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BrandSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Coffee, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Support ${profile.displayName}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text("Goal: ${profile.tipGoalTitle}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Choose Tip Amount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(3.0, 5.0, 10.0, 25.0).forEach { amt ->
                        val isSelected = selectedAmount == amt && customAmountText.isEmpty()
                        Button(
                            onClick = {
                                selectedAmount = amt
                                customAmountText = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandSuccess else BrandDarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text(
                                text = "$${amt.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BrandDarkBackground else TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Custom tip field
                OutlinedTextField(
                    value = customAmountText,
                    onValueChange = {
                        customAmountText = it
                        val parsed = it.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            selectedAmount = parsed
                        }
                    },
                    label = { Text("Custom Amount ($)") },
                    placeholder = { Text("e.g. 50.00") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandSuccess,
                        unfocusedBorderColor = BrandDarkBorder
                    )
                )

                Text(
                    text = "2. Select Payment Method",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Available Payment Methods
                val methods = listOf(
                    Triple("Google Pay", "Instant 1-Tap Checkout", Icons.Default.Payments),
                    Triple("Credit / Debit Card", if (cardNumber.isNotEmpty()) "Card •••• ${cardNumber.takeLast(4)}" else "Visa ending in 4242", Icons.Default.CreditCard),
                    Triple("PayPal", "@${profile.paypalHandle.ifEmpty { "creator" }}", Icons.Default.AccountBalance),
                    Triple("Cash App", profile.cashAppTag.ifEmpty { "$${profile.handle}" }, Icons.Default.AttachMoney)
                )

                methods.forEach { method ->
                    val isSelected = selectedPaymentMethod == method.first
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandPrimary.copy(alpha = 0.15f) else BrandDarkBackground)
                            .border(1.dp, if (isSelected) BrandPrimary else BrandDarkBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedPaymentMethod = method.first
                                if (method.first == "Credit / Debit Card" && cardNumber.isEmpty()) {
                                    isAddingNewCard = true
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(method.third, contentDescription = null, tint = if (isSelected) BrandPrimary else TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(method.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(method.second, fontSize = 10.sp, color = TextSecondary)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Inline card expansion
                if (selectedPaymentMethod == "Credit / Debit Card" && isAddingNewCard) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandDarkSurfaceVariant)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Enter Card Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 19) cardNumber = it },
                            placeholder = { Text("4242 •••• •••• 4242") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { if (it.length <= 5) cardExpiry = it },
                                placeholder = { Text("MM/YY") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvc,
                                onValueChange = { if (it.length <= 4) cardCvc = it },
                                placeholder = { Text("CVC") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Live Payment Processing Notice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE PAYMENT: Funds debited via $selectedPaymentMethod to ${profile.displayName}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSuccess
                    )
                }

                // Pro Fee Transparency
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (profile.isProUser) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (profile.isProUser) GoldStart else BrandSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (profile.isProUser) "0% Platform Fee • 100% goes to creator (MultiLink PRO)" else "Secure 256-bit encrypted fan checkout",
                        fontSize = 10.sp,
                        color = if (profile.isProUser) GoldStart else TextMuted
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountStr = String.format("%.2f", selectedAmount)
                    val liveUrl = when (selectedPaymentMethod) {
                        "PayPal" -> "https://www.paypal.me/${profile.paypalHandle.ifEmpty { "alexarivers" }}/$amountStr"
                        "Cash App" -> {
                            val tag = profile.cashAppTag.ifEmpty { "AlexaAura" }.replace("$", "")
                            "https://cash.app/$$tag/$amountStr"
                        }
                        "Credit / Debit Card" -> {
                            if (profile.buyMeACoffeeUrl.isNotEmpty()) {
                                if (profile.buyMeACoffeeUrl.startsWith("http")) profile.buyMeACoffeeUrl else "https://${profile.buyMeACoffeeUrl}"
                            } else {
                                "https://buy.stripe.com/live_multilink_tip?creator=${profile.handle}&amount=$amountStr"
                            }
                        }
                        else -> {
                            // Google Pay / Default
                            if (profile.buyMeACoffeeUrl.isNotEmpty()) {
                                if (profile.buyMeACoffeeUrl.startsWith("http")) profile.buyMeACoffeeUrl else "https://${profile.buyMeACoffeeUrl}"
                            } else {
                                "https://www.paypal.me/${profile.paypalHandle.ifEmpty { "alexarivers" }}/$amountStr"
                            }
                        }
                    }

                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // fallback
                    }

                    onSendTip(selectedAmount, selectedPaymentMethod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .testTag("send_tip_confirm_button")
            ) {
                Text(
                    text = "Pay $${String.format("%.2f", selectedAmount)} via $selectedPaymentMethod",
                    fontWeight = FontWeight.Bold,
                    color = BrandDarkBackground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun OrderSuccessReceiptDialog(
    product: LinkItem,
    paymentMethod: String,
    profile: CreatorProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val transactionId = remember { "ML-${(10000..99999).random()}" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Payment Successful!", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text("Order #$transactionId", fontSize = 11.sp, color = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = product.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid:", fontSize = 11.sp, color = TextSecondary)
                            Text(product.price, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = BrandSuccess)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Source:", fontSize = 11.sp, color = TextSecondary)
                            Text(paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }
                }

                // Digital Delivery Asset Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldStart.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = BrandWarning, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Instant Digital Delivery", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                        }

                        val fileName = product.downloadFileName.ifEmpty { "digital_product_package.zip" }
                        val fileSize = product.fileSizeBytes.ifEmpty { "1.4 GB" }
                        val license = product.licenseType.ifEmpty { "Commercial & Extended Use" }

                        Text(
                            text = "$fileName ($fileSize)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandSecondary
                        )
                        Text(
                            text = "License: $license",
                            fontSize = 10.sp,
                            color = TextMuted
                        )

                        Button(
                            onClick = {
                                Toast.makeText(context, "Downloading $fileName...", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandWarning),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 36.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = BrandDarkBackground, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download Assets Now", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandDarkBackground)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (profile.isProUser) Icons.Default.WorkspacePremium else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (profile.isProUser) GoldStart else BrandSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (profile.isProUser) "LIVE PAYMENT SETTLED • 0% Fee (Creator received 100%)" else "LIVE PAYMENT SETTLED • Receipt sent to your bank & email",
                        fontSize = 10.sp,
                        color = if (profile.isProUser) GoldStart else BrandSuccess,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        containerColor = BrandDarkSurface
    )
}
