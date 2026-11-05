package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import com.example.data.billing.BillingStatus
import com.example.data.billing.PlayBillingManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.example.ui.components.MetricCard
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandInfo
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
fun MonetizeScreen(
    profile: CreatorProfile?,
    links: List<LinkItem>,
    totalRevenue: Double,
    playBillingManager: PlayBillingManager? = null,
    onAddLink: (LinkItem) -> Unit,
    onDeleteLink: (LinkItem) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    onSimulateTip: () -> Unit,
    onUpdatePayouts: (Boolean, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpgradeToPro: (tier: String, paymentMethod: String, renewalDate: String, token: String, orderId: String) -> Unit = { _, _, _, _, _ -> },
    onCancelPro: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showPayoutConfigDialog by remember { mutableStateOf(false) }
    var showProUpgradeDialog by remember { mutableStateOf(false) }
    var showProSuccessReceiptDialog by remember { mutableStateOf(false) }
    var lastSubscribedPlanTier by remember { mutableStateOf("PRO_ANNUAL") }
    var lastSubscribedPaymentMethod by remember { mutableStateOf("") }
    var lastSubscribedAmount by remember { mutableStateOf(79.00) }
    var lastSubscribedRenewalDate by remember { mutableStateOf("") }
    var lastTransactionRef by remember { mutableStateOf("") }
    var showPayoutHistoryDialog by remember { mutableStateOf(false) }

    val products = remember(links) { links.filter { it.category == LinkCategory.PRODUCT } }
    val services = remember(links) { links.filter { it.category == LinkCategory.SERVICE } }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Monetization KPI Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Gross Revenue",
                        value = "$${String.format("%.2f", totalRevenue)}",
                        subtitle = if (profile?.isProUser == true) "0% Platform Fee (Pro)" else "5% Platform Fee",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = BrandSuccess,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Monetized Offers",
                        value = "${products.size + services.size}",
                        subtitle = "Products & Calls",
                        icon = Icons.Default.LocalMall,
                        accentColor = BrandWarning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // MultiLink PRO Tier Banner
            item {
                if (profile?.isProUser == true) {
                    ProActiveStatusCard(
                        profile = profile,
                        onManage = { showProUpgradeDialog = true }
                    )
                } else {
                    ProUpgradeBannerCard(
                        onUpgradeClick = { showProUpgradeDialog = true }
                    )
                }
            }

            // Creator Payout & Tip Collection Methods
            item {
                profile?.let { prof ->
                    CreatorPayoutMethodsCard(
                        profile = prof,
                        onConfigureClick = { showPayoutConfigDialog = true },
                        onViewSchedule = { showPayoutHistoryDialog = true }
                    )
                }
            }

            // Tip Jar / Fan Support Goal Card
            item {
                profile?.let { prof ->
                    TipGoalCard(
                        profile = prof,
                        onSendTestTip = {
                            onSimulateTip()
                            Toast.makeText(context, "Simulated +$10.00 Tip received via ${prof.preferredTipMethod}!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Digital Products Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Digital Products (${products.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Ebooks, 3D Assets, Presets & Notion Packs",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { showAddProductDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandWarning),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_product_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandDarkBackground)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                    }
                }
            }

            // Products List
            items(products, key = { it.id }) { product ->
                ProductMonetizeCard(
                    product = product,
                    onToggleActive = { onToggleActive(product.id, it) },
                    onDelete = { onDeleteLink(product) }
                )
            }

            // Professional Services Section Header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Professional Services (${services.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Mentorship, Consulting, 1-on-1 Calls",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { showAddServiceDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_service_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Service", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Services List
            items(services, key = { it.id }) { service ->
                ServiceMonetizeCard(
                    service = service,
                    onToggleActive = { onToggleActive(service.id, it) },
                    onDelete = { onDeleteLink(service) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            isProUser = profile?.isProUser == true,
            onUpgradeToPro = { showProUpgradeDialog = true },
            onDismiss = { showAddProductDialog = false },
            onConfirm = {
                onAddLink(it)
                showAddProductDialog = false
            }
        )
    }

    if (showAddServiceDialog) {
        AddServiceDialog(
            onDismiss = { showAddServiceDialog = false },
            onConfirm = {
                onAddLink(it)
                showAddServiceDialog = false
            }
        )
    }

    if (showPayoutConfigDialog && profile != null) {
        CreatorPayoutConfigDialog(
            profile = profile,
            onDismiss = { showPayoutConfigDialog = false },
            onSave = { stripeOk, paypal, cashapp, bmac, preferred ->
                onUpdatePayouts(stripeOk, paypal, cashapp, bmac, preferred)
                showPayoutConfigDialog = false
                Toast.makeText(context, "Payout & tip methods updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showProUpgradeDialog) {
        ProUpgradeDialog(
            isCurrentlyPro = profile?.isProUser == true,
            currentTier = profile?.proPlanTier ?: "FREE",
            currentPaymentMethod = profile?.proPaymentMethod ?: "",
            currentRenewalDate = profile?.proSubscriptionRenewalDate ?: "",
            proOrderId = profile?.proOrderId ?: "",
            playBillingManager = playBillingManager,
            onDismiss = { showProUpgradeDialog = false },
            onSubscriptionPurchased = { tier, paymentMethod, renewalDate, amount, token, orderId ->
                onUpgradeToPro(tier, paymentMethod, renewalDate, token, orderId)
                showProUpgradeDialog = false
                lastSubscribedPlanTier = tier
                lastSubscribedPaymentMethod = paymentMethod
                lastSubscribedAmount = amount
                lastSubscribedRenewalDate = renewalDate
                lastTransactionRef = orderId
                showProSuccessReceiptDialog = true
            },
            onCancelSubscription = {
                onCancelPro()
                showProUpgradeDialog = false
                Toast.makeText(context, "Subscription canceled", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showProSuccessReceiptDialog) {
        ProSubscriptionSuccessDialog(
            tier = lastSubscribedPlanTier,
            paymentMethod = lastSubscribedPaymentMethod,
            amount = lastSubscribedAmount,
            renewalDate = lastSubscribedRenewalDate,
            transactionRef = lastTransactionRef,
            onDismiss = { showProSuccessReceiptDialog = false }
        )
    }

    if (showPayoutHistoryDialog && profile != null) {
        PayoutHistoryScheduleDialog(
            profile = profile,
            totalRevenue = totalRevenue,
            onDismiss = { showPayoutHistoryDialog = false }
        )
    }
}

@Composable
fun ProUpgradeBannerCard(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUpgradeClick() }
            .testTag("pro_upgrade_banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(GoldStart, BrandPrimary, BrandSecondary))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(GoldStart, GoldEnd))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = BrandDarkBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MultiLink PRO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldStart)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "0% FEES",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandDarkBackground
                                )
                            }
                        }
                        Text(
                            text = "Keep 100% of tips, Gold checkmark & custom domains",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("0% Fee on Tips", "Gold Badge", "Vector QR Codes").forEach { perk ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandDarkSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = GoldStart, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = perk, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldStart),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("upgrade_to_pro_button")
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = BrandDarkBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Upgrade to PRO (from $6.58/mo)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDarkBackground
                )
            }
        }
    }
}

@Composable
fun ProActiveStatusCard(
    profile: CreatorProfile,
    onManage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pro_active_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldStart)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GoldStart, GoldEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = BrandDarkBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MultiLink PRO Active",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldStart
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PAID & VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSuccess
                            )
                        }
                    }
                    Text(
                        text = if (profile.proPlanTier == "PRO_ANNUAL") "Annual Membership ($79.00/yr)" else "Monthly Membership ($9.99/mo)",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onManage,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldStart),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Manage", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldStart)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Paid With:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = profile.proPaymentMethod.ifBlank { "Google Play In-App Billing" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Next Renewal:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = profile.proSubscriptionRenewalDate.ifBlank { "Nov 3, 2027" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldStart
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Platform Fee Rate:", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "0% (Pro Exemption Active)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorPayoutMethodsCard(
    profile: CreatorProfile,
    onConfigureClick: () -> Unit,
    onViewSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("creator_payout_methods_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Payout & Tip Methods",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "How you collect earnings & tips",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onConfigureClick, modifier = Modifier.testTag("configure_payouts_button")) {
                    Icon(Icons.Default.Settings, contentDescription = "Configure", tint = BrandSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Method 1: Stripe Connect
            PayoutMethodRow(
                title = "Stripe Connect",
                subtitle = if (profile.stripeConnected) "Direct Bank Deposits (${profile.stripeAccountId})" else "Not connected",
                badge = if (profile.stripeConnected) "CONNECTED" else "SETUP NEEDED",
                badgeColor = if (profile.stripeConnected) BrandSuccess else BrandWarning,
                isPreferred = profile.preferredTipMethod == "Stripe",
                icon = Icons.Default.AccountBalance
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Method 2: PayPal
            PayoutMethodRow(
                title = "PayPal",
                subtitle = if (profile.paypalHandle.isNotEmpty()) "@${profile.paypalHandle}" else "Not configured",
                badge = if (profile.paypalHandle.isNotEmpty()) "ACTIVE" else "OFF",
                badgeColor = if (profile.paypalHandle.isNotEmpty()) BrandInfo else TextMuted,
                isPreferred = profile.preferredTipMethod == "PayPal",
                icon = Icons.Default.CreditCard
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Method 3: Cash App
            PayoutMethodRow(
                title = "Cash App",
                subtitle = if (profile.cashAppTag.isNotEmpty()) profile.cashAppTag else "Not configured",
                badge = if (profile.cashAppTag.isNotEmpty()) "ACTIVE" else "OFF",
                badgeColor = if (profile.cashAppTag.isNotEmpty()) BrandSuccess else TextMuted,
                isPreferred = profile.preferredTipMethod == "CashApp",
                icon = Icons.Default.AttachMoney
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onConfigureClick,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit Accounts",
                        fontSize = 11.sp,
                        color = BrandSecondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onViewSchedule,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkSurfaceVariant),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Payout Schedule",
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PayoutMethodRow(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    isPreferred: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BrandDarkSurfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (isPreferred) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEFAULT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrandPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }

        Text(
            text = badge,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = badgeColor,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TipGoalCard(
    profile: CreatorProfile,
    onSendTestTip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (profile.tipGoalCurrent / profile.tipGoalTarget).toFloat().coerceIn(0f, 1f)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tip_goal_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = null,
                            tint = BrandSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Community Tip Jar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = profile.tipGoalTitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Button(
                    onClick = onSendTestTip,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .testTag("simulate_tip_button")
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = BrandDarkBackground, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send $10 Tip", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${profile.tipGoalCurrent.toInt()} raised",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandSuccess
                )
                Text(
                    text = "Goal: $${profile.tipGoalTarget.toInt()}",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = BrandSuccess,
                trackColor = BrandDarkSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrandSuccess.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandSuccess)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE PAYMENT GATEWAY: Bank settlements via Stripe, PayPal & Cash App",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandSuccess
                )
            }
        }
    }
}

@Composable
fun ProductMonetizeCard(
    product: LinkItem,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (product.coverImageName.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.digital_product_mockup_1793630338547),
                        contentDescription = "Product Mockup",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (product.badge.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandWarning)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = product.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDarkBackground
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.price,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandSuccess
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${product.totalClicks} visits",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    if (product.isProProduct || product.downloadFileName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldStart.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldStart, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PRO ASSET • 0% COMMISSION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldStart
                            )
                        }

                        if (product.downloadFileName.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = BrandWarning, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.downloadFileName} (${product.fileSizeBytes.ifEmpty { "Direct Delivery" }})",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (product.licenseType.isNotEmpty()) {
                            Text(
                                text = "License: ${product.licenseType}",
                                fontSize = 10.sp,
                                color = BrandSecondary,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                    }
                    Switch(
                        checked = product.isActive,
                        onCheckedChange = onToggleActive,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextPrimary,
                            checkedTrackColor = BrandWarning
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceMonetizeCard(
    service: LinkItem,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = service.subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.price,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                    if (service.badge.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = service.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BrandSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                }
                Switch(
                    checked = service.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = BrandPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun CreatorPayoutConfigDialog(
    profile: CreatorProfile,
    onDismiss: () -> Unit,
    onSave: (Boolean, String, String, String, String) -> Unit
) {
    var stripeConnected by remember { mutableStateOf(profile.stripeConnected) }
    var paypalHandle by remember { mutableStateOf(profile.paypalHandle) }
    var cashAppTag by remember { mutableStateOf(profile.cashAppTag) }
    var buyMeACoffeeUrl by remember { mutableStateOf(profile.buyMeACoffeeUrl) }
    var preferredTipMethod by remember { mutableStateOf(profile.preferredTipMethod) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = BrandSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configure Payout & Tip Receivers", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Configure accounts where fan tips and digital product earnings are deposited.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Stripe Connect Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandDarkBackground)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stripe Direct Deposit", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text("Instant bank deposits & Google Pay", fontSize = 11.sp, color = TextMuted)
                    }
                    Switch(
                        checked = stripeConnected,
                        onCheckedChange = { stripeConnected = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = BrandSuccess)
                    )
                }

                // PayPal
                OutlinedTextField(
                    value = paypalHandle,
                    onValueChange = { paypalHandle = it },
                    label = { Text("PayPal Username / Handle") },
                    placeholder = { Text("username (e.g. alexa.rivers)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandInfo,
                        unfocusedBorderColor = BrandDarkBorder
                    )
                )

                // Cash App
                OutlinedTextField(
                    value = cashAppTag,
                    onValueChange = { cashAppTag = it },
                    label = { Text("Cash App \$Cashtag") },
                    placeholder = { Text("\$AlexaAura") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandSuccess,
                        unfocusedBorderColor = BrandDarkBorder
                    )
                )

                // Buy Me a Coffee
                OutlinedTextField(
                    value = buyMeACoffeeUrl,
                    onValueChange = { buyMeACoffeeUrl = it },
                    label = { Text("Buy Me A Coffee Link") },
                    placeholder = { Text("buymeacoffee.com/creator") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Default Tip Landing Method:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Stripe", "PayPal", "CashApp").forEach { method ->
                        val isSelected = preferredTipMethod == method
                        Button(
                            onClick = { preferredTipMethod = method },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) BrandPrimary else BrandDarkBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = method,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(stripeConnected, paypalHandle, cashAppTag, buyMeACoffeeUrl, preferredTipMethod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary)
            ) {
                Text("Save Accounts", fontWeight = FontWeight.Bold, color = BrandDarkBackground)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun ProUpgradeDialog(
    isCurrentlyPro: Boolean,
    currentTier: String,
    currentPaymentMethod: String = "",
    currentRenewalDate: String = "",
    proOrderId: String = "",
    playBillingManager: PlayBillingManager?,
    onDismiss: () -> Unit,
    onSubscriptionPurchased: (tier: String, paymentMethod: String, renewalDate: String, amount: Double, token: String, orderId: String) -> Unit,
    onCancelSubscription: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(if (currentTier == "PRO_MONTHLY") "PRO_MONTHLY" else "PRO_ANNUAL") }
    var isProcessing by remember { mutableStateOf(false) }
    var billingErrorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity
    val billingStatus by (playBillingManager?.billingStatus ?: remember { kotlinx.coroutines.flow.MutableStateFlow(BillingStatus.Idle) }).collectAsState()

    LaunchedEffect(billingStatus) {
        when (val status = billingStatus) {
            is BillingStatus.SubscriptionActive -> {
                isProcessing = false
                val cal = java.util.Calendar.getInstance()
                if (status.tier == "PRO_ANNUAL") {
                    cal.add(java.util.Calendar.YEAR, 1)
                } else {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 30)
                }
                val renewalDateStr = SimpleDateFormat("MMM d, yyyy", Locale.US).format(cal.time)
                onSubscriptionPurchased(
                    status.tier,
                    "Google Play In-App Billing",
                    renewalDateStr,
                    if (status.tier == "PRO_ANNUAL") 79.00 else 9.99,
                    status.purchaseToken,
                    status.orderId
                )
                onDismiss()
            }
            is BillingStatus.Error -> {
                isProcessing = false
                billingErrorMessage = status.message
            }
            is BillingStatus.Cancelled -> {
                isProcessing = false
                billingErrorMessage = "Transaction was cancelled. Subscription was not activated."
            }
            is BillingStatus.Connecting -> {
                isProcessing = true
            }
            else -> {}
        }
    }

    val planAmount = if (selectedPlan == "PRO_ANNUAL") 79.00 else 9.99
    val planPriceText = if (selectedPlan == "PRO_ANNUAL") "$79.00/yr" else "$9.99/mo"
    val targetProductId = if (selectedPlan == "PRO_ANNUAL") PlayBillingManager.SUB_ANNUAL else PlayBillingManager.SUB_MONTHLY

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GoldStart, GoldEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = BrandDarkBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isCurrentlyPro) "Manage PRO Subscription" else "MultiLink PRO Subscription",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Real-time payment authorization required",
                        fontSize = 11.sp,
                        color = GoldStart
                    )
                }
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Feature Comparison Highlights
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProFeatureRow("0% Platform Fees", "Keep 100% of all product sales & tips (0% vs 5% fee)")
                        ProFeatureRow("Unlimited Products", "Upload unlimited digital products & instant downloads")
                        ProFeatureRow("Gold Verified Badge", "Golden checkmark displayed on your public page")
                        ProFeatureRow("Custom Domain", "yourproject.web.app or own custom domain")
                        ProFeatureRow("High-Res Vector QR", "Print-ready QR standees for physical events")
                    }
                }

                Text(
                    text = "1. Choose Membership Plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                // Plan 1: Annual
                PlanOptionCard(
                    title = "Annual Membership",
                    price = "$79.00 / year",
                    subtitle = "$6.58 / mo • Save 34% (Billed annually)",
                    badge = "BEST VALUE",
                    isSelected = selectedPlan == "PRO_ANNUAL",
                    onClick = { selectedPlan = "PRO_ANNUAL" }
                )

                // Plan 2: Monthly
                PlanOptionCard(
                    title = "Monthly Membership",
                    price = "$9.99 / month",
                    subtitle = "Billed monthly • Cancel anytime",
                    badge = "FLEXIBLE",
                    isSelected = selectedPlan == "PRO_MONTHLY",
                    onClick = { selectedPlan = "PRO_MONTHLY" }
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "2. Google Play Payment Processing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                // Google Play Billing Card (Mandatory Policy)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldStart)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = GoldStart,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Play In-App Billing",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSuccess,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandSuccess.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mandatory for digital subscriptions under Google Play Policy. Purchases are securely billed to your Google Play account or linked payment method.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Play Product ID: $targetProductId",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldStart
                        )
                    }
                }

                // Diagnostic/Error banner if billing returned an error
                if (billingErrorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWarning.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandWarning.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = BrandWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Play Notice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandWarning
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = billingErrorMessage ?: "",
                                fontSize = 10.sp,
                                color = TextPrimary
                            )
                            if (billingErrorMessage?.contains("Unavailable", ignoreCase = true) == true ||
                                billingErrorMessage?.contains("not configured", ignoreCase = true) == true ||
                                billingErrorMessage?.contains("Error code: 4", ignoreCase = true) == true) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Developer Action: Add product ID '$targetProductId' in your Google Play Console under Monetize > Subscriptions and set state to Active.",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Security & Auto-renewal note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Encrypted live transaction • Manage or cancel anytime in Google Play Store",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isProcessing) return@Button
                    val act = activity
                    if (act != null && playBillingManager != null) {
                        isProcessing = true
                        billingErrorMessage = null
                        playBillingManager.launchSubscriptionPurchase(act, selectedPlan)
                    } else {
                        billingErrorMessage = "Play Billing Client is connecting. Please ensure Google Play Store is available."
                    }
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldStart,
                    disabledContainerColor = BrandDarkBorder
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_pro_plan_button")
            ) {
                if (isProcessing) {
                    Text("Connecting to Google Play...", fontWeight = FontWeight.Bold, color = BrandDarkBackground, fontSize = 12.sp)
                } else {
                    Text(
                        text = if (isCurrentlyPro) "Switch Plan ($planPriceText)" else "Pay with Google Play ($planPriceText)",
                        fontWeight = FontWeight.Bold,
                        color = BrandDarkBackground,
                        fontSize = 12.sp
                    )
                }
            }
        },
        dismissButton = {
            if (isCurrentlyPro) {
                TextButton(onClick = onCancelSubscription) {
                    Text("Cancel Plan", color = Color(0xFFEF4444))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun PaymentMethodOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("payment_method_${title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.12f) else BrandDarkBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) GoldStart else BrandDarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) GoldStart else TextMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldStart)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GoldStart.copy(alpha = 0.2f) else BrandDarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) GoldStart else TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ProSubscriptionSuccessDialog(
    tier: String,
    paymentMethod: String,
    amount: Double,
    renewalDate: String,
    transactionRef: String,
    onDismiss: () -> Unit
) {
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
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandSuccess,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Payment Successful!", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text("MultiLink PRO subscription activated", fontSize = 11.sp, color = BrandSuccess)
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
                    colors = CardDefaults.cardColors(containerColor = BrandDarkBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Transaction ID", fontSize = 11.sp, color = TextSecondary)
                            Text(transactionRef, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Plan Selected", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                if (tier == "PRO_ANNUAL") "Annual Membership" else "Monthly Membership",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldStart
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Amount Charged", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                "$${String.format(Locale.US, "%.2f", amount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSuccess
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Method", fontSize = 11.sp, color = TextSecondary)
                            Text(paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Next Renewal Date", fontSize = 11.sp, color = TextSecondary)
                            Text(renewalDate, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldStart.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldStart.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "🎉 What's now unlocked:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldStart
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• 0% Platform Fee on all tip jar & product sales\n• Golden Verified Checkmark on your public page\n• Unlimited digital files with automatic delivery\n• High-res Vector SVG QR standee downloads",
                            fontSize = 10.sp,
                            color = TextPrimary,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldStart),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dismiss_pro_receipt_button")
            ) {
                Text("Start Monetizing as PRO", fontWeight = FontWeight.Bold, color = BrandDarkBackground)
            }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun PlanOptionCard(
    title: String,
    price: String,
    subtitle: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.12f) else BrandDarkBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) GoldStart else BrandDarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) GoldStart else TextMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldStart)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldStart,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GoldStart.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }

            Text(text = price, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextPrimary)
        }
    }
}

@Composable
fun ProFeatureRow(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldStart, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun PayoutHistoryScheduleDialog(
    profile: CreatorProfile,
    totalRevenue: Double,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = BrandSuccess)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Payout Schedule & Transfers", fontWeight = FontWeight.Bold, color = TextPrimary)
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
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Next Estimated Transfer:", fontSize = 11.sp, color = TextSecondary)
                        Text("$${String.format("%.2f", totalRevenue)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrandSuccess)
                        Text("Scheduled: Tomorrow at 6:00 AM via Stripe Connect", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Text("Recent Bank Transfers:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)

                listOf(
                    Triple("Mar 28, 2026", "$450.00", "Direct Deposit (Chase •••• 9214)"),
                    Triple("Mar 21, 2026", "$680.00", "Direct Deposit (Chase •••• 9214)"),
                    Triple("Mar 14, 2026", "$290.00", "Direct Deposit (Chase •••• 9214)")
                ).forEach { transfer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandDarkBackground)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(transfer.first, fontSize = 11.sp, color = TextSecondary)
                            Text(transfer.third, fontSize = 10.sp, color = TextMuted)
                        }
                        Text(transfer.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandSuccess)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun AddProductDialog(
    isProUser: Boolean,
    onUpgradeToPro: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (LinkItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("$29.00") }
    var url by remember { mutableStateOf("https://gumroad.com/l/") }
    var subtitle by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("NEW") }

    // PRO Product Delivery Options
    var isProProduct by remember { mutableStateOf(isProUser) }
    var downloadFileName by remember { mutableStateOf("asset_toolkit_2026.zip") }
    var fileSizeBytes by remember { mutableStateOf("450 MB") }
    var licenseType by remember { mutableStateOf("Commercial & Extended Use") }

    val licenseOptions = listOf("Personal Use", "Commercial & Extended Use", "Unlimited Resale")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BrandWarning.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = BrandWarning, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Add Digital Product", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text("Sell downloads, presets, eBooks & 3D assets", fontSize = 11.sp, color = TextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Name") },
                        placeholder = { Text("e.g. Cyberpunk 3D Shader Pack") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (e.g. $29.00)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Product Link / Checkout URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Short Description") },
                        placeholder = { Text("50+ Shaders, textures & UE5 materials") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Badge selector
                    Text("Product Badge:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("NEW", "BESTSELLER", "LIMITED", "HOT").forEach { b ->
                            val isSelected = badge == b
                            Button(
                                onClick = { badge = b },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandWarning else BrandDarkBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 32.dp)
                            ) {
                                Text(
                                    text = b,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BrandDarkBackground else TextPrimary
                                )
                            }
                        }
                    }
                }

                // PRO Product Options Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkBackground),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isProProduct) GoldStart else BrandDarkBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldStart, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("PRO Product Delivery", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                        Text("Instant file delivery & 0% fee", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }

                                Switch(
                                    checked = isProProduct,
                                    onCheckedChange = { isProProduct = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = GoldStart)
                                )
                            }

                            if (isProProduct) {
                                OutlinedTextField(
                                    value = downloadFileName,
                                    onValueChange = { downloadFileName = it },
                                    label = { Text("Download Asset Filename") },
                                    placeholder = { Text("e.g. cyberpunk_bundle_2026.zip") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = fileSizeBytes,
                                    onValueChange = { fileSizeBytes = it },
                                    label = { Text("File Size / Assets Info") },
                                    placeholder = { Text("e.g. 1.4 GB or 500 MB") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text("Usage Rights / License Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    licenseOptions.forEach { lic ->
                                        val isSelected = licenseType == lic
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) BrandPrimary.copy(alpha = 0.15f) else BrandDarkSurfaceVariant)
                                                .clickable { licenseType = lic }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (isSelected) GoldStart else TextMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(lic, fontSize = 11.sp, color = if (isSelected) TextPrimary else TextSecondary, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                // Commission info
                                if (isProUser) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GoldStart.copy(alpha = 0.15f))
                                            .padding(8.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = GoldStart, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "0% Platform Fee: You keep 100% of this sale ($price)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldStart
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BrandDarkSurfaceVariant)
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Free Plan: 5% platform fee applies. Upgrade to PRO to keep 100% and enable instant asset cloud delivery.",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                        Button(
                                            onClick = onUpgradeToPro,
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldStart),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                                        ) {
                                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = BrandDarkBackground, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Upgrade to PRO (0% Fee)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            LinkItem(
                                title = title,
                                url = url,
                                category = LinkCategory.PRODUCT,
                                iconKey = "store",
                                subtitle = subtitle,
                                price = price,
                                badge = badge,
                                coverImageName = "digital_product_mockup_1793630338547",
                                isFeatured = true,
                                isActive = true,
                                isProProduct = isProProduct,
                                downloadFileName = if (isProProduct) downloadFileName else "",
                                fileSizeBytes = if (isProProduct) fileSizeBytes else "",
                                licenseType = if (isProProduct) licenseType else ""
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandWarning),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.defaultMinSize(minHeight = 38.dp)
            ) {
                Text("Add Product", color = BrandDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun AddServiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (LinkItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("$100 / session") }
    var url by remember { mutableStateOf("https://cal.com/") }
    var subtitle by remember { mutableStateOf("45-min strategy & consultation call") }
    var badge by remember { mutableStateOf("AVAILABLE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Professional Service", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Service Title (e.g. Portfolio Review)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate (e.g. $150 / session or $85/hr)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Calendar / Booking URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Duration & Details") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            LinkItem(
                                title = title,
                                url = url,
                                category = LinkCategory.SERVICE,
                                iconKey = "calendar",
                                subtitle = subtitle,
                                price = rate,
                                badge = badge,
                                isFeatured = false,
                                isActive = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Add Service", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = BrandDarkSurface
    )
}
