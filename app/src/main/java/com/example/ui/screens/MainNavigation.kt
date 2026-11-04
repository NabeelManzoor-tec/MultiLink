package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.auth.AuthManager
import com.example.data.model.UserRole
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.ui.theme.GoldEnd
import com.example.ui.theme.GoldStart
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MultiLinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen(
    viewModel: MultiLinkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val allLinks by viewModel.allLinks.collectAsStateWithLifecycle()
    val recentClicks by viewModel.recentClicks.collectAsStateWithLifecycle()
    val totalClicks by viewModel.totalClicks.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val topLinks by viewModel.topPerformingLinks.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isPublicVisitorMode by viewModel.isPublicVisitorMode.collectAsStateWithLifecycle()
    val qrConfig by viewModel.qrConfig.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val showAuthScreen by viewModel.showAuthScreen.collectAsStateWithLifecycle()
    val isGuestMode by viewModel.isGuestMode.collectAsStateWithLifecycle()

    var showAccountDialog by remember { mutableStateOf(false) }

    if (showAuthScreen) {
        AuthScreen(
            onAuthSuccess = { user, role ->
                viewModel.onSignedIn(user, role)
                Toast.makeText(context, "Welcome ${user.displayName ?: "Creator"}!", Toast.LENGTH_SHORT).show()
            },
            onContinueAsGuest = { role ->
                viewModel.setGuestMode(true, role)
                Toast.makeText(context, "Browsing in guest mode", Toast.LENGTH_SHORT).show()
            }
        )
    } else if (isPublicVisitorMode && profile != null) {
        PublicLandingPageScreen(
            profile = profile!!,
            links = allLinks,
            onCloseVisitorMode = { viewModel.setPublicVisitorMode(false) },
            onRecordClick = { link, revenue ->
                viewModel.recordVisitorClick(link = link, source = "Landing Page", revenue = revenue)
            }
        )
    } else if (userRole == UserRole.CUSTOMER) {
        // Customer Hub Screen for Customers & Clients
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(BrandDarkBackground),
            containerColor = BrandDarkBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.5.dp, BrandSecondary, RoundedCornerShape(10.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_multilink_app_icon_1793630259886),
                                    contentDescription = "MultiLink Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "MultiLink",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CUSTOMER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandWarning,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BrandWarning.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = if (currentUser != null) currentUser!!.displayName ?: "Customer Account" else "Customer Guest",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    },
                    actions = {
                        // Switch to Creator Mode button
                        Button(
                            onClick = { viewModel.setUserRole(UserRole.CREATOR) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("switch_to_creator_topbar_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Creator Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { showAccountDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("customer_account_icon_button")
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Account Settings",
                                tint = BrandSecondary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandDarkBackground)
                )
            }
        ) { paddingValues ->
            CustomerHubScreen(
                userEmail = currentUser?.email ?: "guest@multilink.bio",
                userName = currentUser?.displayName ?: "Guest Customer",
                onViewCreatorLandingPage = { viewModel.setPublicVisitorMode(true) },
                onSwitchToCreatorMode = { viewModel.setUserRole(UserRole.CREATOR) },
                modifier = Modifier.padding(paddingValues)
            )
        }
    } else {
        // Creator Studio View
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(BrandDarkBackground),
            containerColor = BrandDarkBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showAccountDialog = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.5.dp, BrandSecondary, RoundedCornerShape(10.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_multilink_app_icon_1793630259886),
                                    contentDescription = "MultiLink Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "MultiLink",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CREATOR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandSecondary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BrandSecondary.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = "@${profile?.handle ?: "creator"}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    },
                    actions = {
                        if (profile?.isProUser == true) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldStart)
                                    .clickable { viewModel.selectTab(AppTab.MONETIZE) }
                                    .padding(horizontal = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = BrandDarkBackground, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("PRO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = BrandDarkBackground)
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.setPublicVisitorMode(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .padding(end = 2.dp)
                                .testTag("toggle_live_view_button")
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showAccountDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("account_menu_button")
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Account Settings",
                                tint = if (currentUser != null) BrandSuccess else TextSecondary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrandDarkBackground
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(width = 1.dp, color = BrandDarkBorder),
                    containerColor = BrandDarkSurface,
                    contentColor = TextSecondary
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.LINKS,
                        onClick = { viewModel.selectTab(AppTab.LINKS) },
                        icon = { Icon(Icons.Default.Link, contentDescription = "Links & Bio") },
                        label = { Text("Links", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            selectedTextColor = BrandPrimary,
                            indicatorColor = BrandPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_links_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.MONETIZE,
                        onClick = { viewModel.selectTab(AppTab.MONETIZE) },
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Monetize") },
                        label = { Text("Monetize", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandWarning,
                            selectedTextColor = BrandWarning,
                            indicatorColor = BrandWarning.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_monetize_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.ANALYTICS,
                        onClick = { viewModel.selectTab(AppTab.ANALYTICS) },
                        icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Analytics") },
                        label = { Text("Analytics", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandSecondary,
                            selectedTextColor = BrandSecondary,
                            indicatorColor = BrandSecondary.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_analytics_tab")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.QR_STUDIO,
                        onClick = { viewModel.selectTab(AppTab.QR_STUDIO) },
                        icon = { Icon(Icons.Default.QrCode, contentDescription = "QR Studio") },
                        label = { Text("QR Studio", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            selectedTextColor = BrandPrimary,
                            indicatorColor = BrandPrimary.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_qr_tab")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
                    when (tab) {
                        AppTab.LINKS -> {
                            LinksScreen(
                                profile = profile,
                                links = allLinks,
                                onUpdateProfile = { viewModel.updateProfile(it) },
                                onAddLink = { viewModel.addLink(it) },
                                onUpdateLink = { viewModel.updateLink(it) },
                                onDeleteLink = { viewModel.deleteLink(it) },
                                onToggleActive = { id, active -> viewModel.toggleLinkActive(id, active) },
                                onSelectTheme = { viewModel.updateTheme(it) }
                            )
                        }

                        AppTab.MONETIZE -> {
                            MonetizeScreen(
                                profile = profile,
                                links = allLinks,
                                totalRevenue = totalRevenue,
                                onAddLink = { viewModel.addLink(it) },
                                onDeleteLink = { viewModel.deleteLink(it) },
                                onToggleActive = { id, active -> viewModel.toggleLinkActive(id, active) },
                                onSimulateTip = { viewModel.simulateLiveTraffic() },
                                onUpdatePayouts = { stripeOk, paypal, cashapp, bmac, preferred ->
                                    viewModel.updatePayoutSettings(stripeOk, paypal, cashapp, bmac, preferred)
                                },
                                onUpgradeToPro = { tier, paymentMethod, renewalDate ->
                                    viewModel.upgradeToPro(tier, paymentMethod, renewalDate)
                                },
                                onCancelPro = {
                                    viewModel.cancelProSubscription()
                                }
                            )
                        }

                        AppTab.ANALYTICS -> {
                            AnalyticsScreen(
                                totalClicks = totalClicks,
                                totalRevenue = totalRevenue,
                                topLinks = topLinks,
                                recentEvents = recentClicks,
                                onSimulateClick = { viewModel.simulateLiveTraffic() }
                            )
                        }

                        AppTab.QR_STUDIO -> {
                            QrStudioScreen(
                                profile = profile,
                                qrConfig = qrConfig,
                                onUpdateConfig = { viewModel.updateQrConfig(it) },
                                onTestScan = { viewModel.setPublicVisitorMode(true) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Account & Role Switcher Dialog
    if (showAccountDialog) {
        AccountDetailsDialog(
            currentUser = currentUser,
            currentRole = userRole,
            profile = profile,
            onDismiss = { showAccountDialog = false },
            onSwitchRole = { newRole ->
                viewModel.setUserRole(newRole)
                showAccountDialog = false
                Toast.makeText(context, "Switched to ${newRole.name} Mode", Toast.LENGTH_SHORT).show()
            },
            onOpenSignIn = {
                showAccountDialog = false
                viewModel.setShowAuthScreen(true)
            },
            onUpgradeToPro = {
                showAccountDialog = false
                viewModel.selectTab(AppTab.MONETIZE)
            },
            onSignOut = {
                AuthManager.signOut(context, credentialManager, onComplete = {
                    viewModel.setGuestMode(true)
                    showAccountDialog = false
                    Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                }, scope = coroutineScope)
            }
        )
    }
}

@Composable
fun AccountDetailsDialog(
    currentUser: com.google.firebase.auth.FirebaseUser?,
    currentRole: UserRole,
    profile: com.example.data.model.CreatorProfile?,
    onDismiss: () -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onOpenSignIn: () -> Unit,
    onUpgradeToPro: () -> Unit,
    onSignOut: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "MultiLink Account",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // User Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (currentRole == UserRole.CREATOR) BrandPrimary else BrandSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentRole == UserRole.CREATOR) Icons.Default.AutoAwesome else Icons.Default.Store,
                                contentDescription = null,
                                tint = BrandDarkBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.displayName ?: "Guest User",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = currentUser?.email ?: "Not signed in to Google",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Current: ${currentRole.name} MODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole == UserRole.CREATOR) BrandPrimary else BrandWarning
                                )
                            }
                        }
                    }
                }

                // MultiLink Plan Tier Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (profile?.isProUser == true) GoldStart else BrandDarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (profile?.isProUser == true) GoldStart else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (profile?.isProUser == true) "MultiLink PRO Active" else "MultiLink Free Tier",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (profile?.isProUser == true) GoldStart else TextPrimary
                                )
                                Text(
                                    text = if (profile?.isProUser == true) "0% Platform Fee • Gold Verified Badge" else "5% fee on tips & products",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (profile?.isProUser != true && currentRole == UserRole.CREATOR) {
                            Button(
                                onClick = onUpgradeToPro,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldStart),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                            }
                        }
                    }
                }

                // Switch Mode Action
                val targetRole = if (currentRole == UserRole.CREATOR) UserRole.CUSTOMER else UserRole.CREATOR
                OutlinedButton(
                    onClick = { onSwitchRole(targetRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("switch_account_mode_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = BrandSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentRole == UserRole.CREATOR) "Switch to Customer Hub" else "Switch to Creator Studio",
                        fontWeight = FontWeight.SemiBold,
                        color = BrandSecondary,
                        fontSize = 12.sp
                    )
                }

                // Sign In or Sign Out Action
                if (currentUser != null) {
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sign_out_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onOpenSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_sign_in_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In / Sign Up with Google", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = BrandDarkSurface
    )
}
