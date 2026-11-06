package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.local.MultiLinkDatabase
import com.example.data.model.ClickEvent
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.example.data.model.UserRole
import com.example.data.repository.MultiLinkRepository
import com.example.ui.components.QrEyeStyle
import com.example.ui.components.QrRenderConfig
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandDarkBackground
import com.example.data.billing.BillingStatus
import com.example.data.billing.PlayBillingManager
import com.example.data.firebase.FirestoreSyncManager
import com.example.data.update.AppUpdateManager
import com.example.data.update.AppUpdateState
import com.example.data.update.AppUpdateData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    LINKS,
    MONETIZE,
    ANALYTICS,
    QR_STUDIO
}

class MultiLinkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MultiLinkRepository

    val currentUser: StateFlow<FirebaseUser?> = AuthManager.currentUser
    val userRole: StateFlow<UserRole> = AuthManager.userRole

    private val _isGuestMode = MutableStateFlow(true)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _showAuthScreen = MutableStateFlow(false)
    val showAuthScreen: StateFlow<Boolean> = _showAuthScreen.asStateFlow()

    val playBillingManager = PlayBillingManager(application, viewModelScope)
    val appUpdateManager = AppUpdateManager(application, viewModelScope)
    val appUpdateState: StateFlow<AppUpdateState> = appUpdateManager.updateState
    val isUpdateDialogVisible: StateFlow<Boolean> = appUpdateManager.isUpdateDialogVisible

    init {
        val db = MultiLinkDatabase.getDatabase(application)
        repository = MultiLinkRepository(db.multiLinkDao())
        AuthManager.initAuthListener(application, viewModelScope)
        viewModelScope.launch {
            repository.ensureInitialDataSeeded()
            startCloudSync()
            appUpdateManager.checkForUpdates(isUserInitiated = false)
        }
        viewModelScope.launch {
            playBillingManager.billingStatus.collect { status ->
                if (status is BillingStatus.SubscriptionActive) {
                    val cal = java.util.Calendar.getInstance()
                    if (status.planTier == "PRO_ANNUAL") {
                        cal.add(java.util.Calendar.YEAR, 1)
                    } else {
                        cal.add(java.util.Calendar.DAY_OF_MONTH, 30)
                    }
                    val renewalDate = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US).format(cal.time)
                    upgradeToPro(
                        tier = status.planTier,
                        paymentMethod = "Google Play In-App Billing",
                        renewalDate = renewalDate,
                        purchaseToken = status.purchaseToken,
                        orderId = status.orderId
                    )
                }
            }
        }
    }

    val profile: StateFlow<CreatorProfile?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLinks: StateFlow<List<LinkItem>> = repository.allLinks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLinks: StateFlow<List<LinkItem>> = repository.activeLinks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clickEvents: StateFlow<List<ClickEvent>> = repository.clickEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentClicks: StateFlow<List<ClickEvent>> = repository.recentClicks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalClicks: StateFlow<Int> = repository.totalClickCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue: StateFlow<Double> = repository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _currentTab = MutableStateFlow(AppTab.LINKS)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isPublicVisitorMode = MutableStateFlow(false)
    val isPublicVisitorMode: StateFlow<Boolean> = _isPublicVisitorMode.asStateFlow()

    private val _qrConfig = MutableStateFlow(
        QrRenderConfig(
            foregroundColor = BrandSecondary,
            backgroundColor = BrandDarkBackground,
            eyeStyle = QrEyeStyle.ROUNDED,
            showCenterAvatar = true
        )
    )
    val qrConfig: StateFlow<QrRenderConfig> = _qrConfig.asStateFlow()

    // Analytics computation
    val topPerformingLinks: StateFlow<List<LinkItem>> = allLinks.combine(clickEvents) { links, _ ->
        links.sortedByDescending { it.totalClicks }.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setPublicVisitorMode(enabled: Boolean) {
        _isPublicVisitorMode.value = enabled
    }

    fun setUserRole(role: UserRole) {
        AuthManager.setUserRole(role)
    }

    fun setShowAuthScreen(show: Boolean) {
        _showAuthScreen.value = show
    }

    fun setGuestMode(isGuest: Boolean, role: UserRole = UserRole.CREATOR) {
        _isGuestMode.value = isGuest
        AuthManager.setUserRole(role)
        _showAuthScreen.value = false
    }

    fun onSignedIn(user: FirebaseUser, role: UserRole) {
        _isGuestMode.value = false
        AuthManager.setUserRole(role)
        _showAuthScreen.value = false
    }

    fun updateProfile(newProfile: CreatorProfile) {
        viewModelScope.launch {
            repository.saveProfile(newProfile)
        }
    }

    fun updateTheme(themeId: String) {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            repository.saveProfile(current.copy(themeId = themeId))
        }
    }

    fun addLink(link: LinkItem) {
        viewModelScope.launch {
            repository.addLink(link)
        }
    }

    fun updateLink(link: LinkItem) {
        viewModelScope.launch {
            repository.updateLink(link)
        }
    }

    fun deleteLink(link: LinkItem) {
        viewModelScope.launch {
            repository.deleteLink(link)
        }
    }

    fun toggleLinkActive(linkId: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleLinkActive(linkId, isActive)
        }
    }

    fun recordVisitorClick(
        link: LinkItem,
        source: String = "Landing Page",
        country: String = "United States",
        device: String = "Mobile",
        revenue: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.recordClick(
                link = link,
                source = source,
                country = country,
                device = device,
                revenue = revenue
            )
            // If visitor made a tip contribution, automatically reflect in tipGoalCurrent
            if (revenue > 0.0 && link.category == LinkCategory.TIP) {
                val current = profile.value
                if (current != null) {
                    val updated = current.copy(tipGoalCurrent = current.tipGoalCurrent + revenue)
                    repository.saveProfile(updated)
                }
            }
            if (revenue > 0.0) {
                val current = profile.value
                val handle = current?.handle ?: "nabeeljutt"
                val name = current?.displayName ?: "Nabeel Jutt"
                val txType = when (link.category) {
                    LinkCategory.TIP -> "TIP"
                    LinkCategory.PRODUCT -> "PRODUCT_PURCHASE"
                    LinkCategory.SERVICE -> "SERVICE_BOOKING"
                    else -> "PURCHASE"
                }
                val txId = "tx_${System.currentTimeMillis()}"
                FirestoreSyncManager.recordTransaction(
                    context = getApplication(),
                    transactionId = txId,
                    type = txType,
                    itemTitle = link.title,
                    amount = revenue,
                    currency = "USD",
                    paymentMethod = source,
                    creatorHandle = handle,
                    creatorName = name,
                    customerEmail = "nabeeljuttm424@gmail.com",
                    customerName = "Verified Supporter",
                    donorNote = "Recorded from $source ($device)"
                )
                FirestoreSyncManager.syncUserProfile(
                    context = getApplication(),
                    email = "nabeeljuttm424@gmail.com",
                    displayName = name,
                    handle = handle,
                    role = "creator"
                )
            }
        }
    }

    fun recordProductPurchase(
        product: LinkItem,
        amount: Double,
        paymentMethod: String,
        country: String = "United States",
        device: String = "Mobile (Android)"
    ) {
        viewModelScope.launch {
            repository.recordClick(
                link = product,
                source = "Product Checkout ($paymentMethod)",
                country = country,
                device = device,
                revenue = amount
            )
            val current = profile.value
            val handle = current?.handle ?: "nabeeljutt"
            val name = current?.displayName ?: "Nabeel Jutt"
            val txId = "tx_ord_${System.currentTimeMillis()}"
            FirestoreSyncManager.recordTransaction(
                context = getApplication(),
                transactionId = txId,
                type = if (product.category == LinkCategory.SERVICE) "SERVICE_BOOKING" else "PRODUCT_PURCHASE",
                itemTitle = product.title,
                amount = amount,
                currency = "USD",
                paymentMethod = paymentMethod,
                creatorHandle = handle,
                creatorName = name,
                customerEmail = "nabeeljuttm424@gmail.com",
                customerName = "Verified Customer",
                status = "COMPLETED"
            )
            FirestoreSyncManager.syncUserProfile(
                context = getApplication(),
                email = "nabeeljuttm424@gmail.com",
                displayName = name,
                handle = handle,
                role = "creator"
            )
        }
    }

    fun simulateLiveTraffic() {
        viewModelScope.launch {
            repository.simulateLiveVisitorClick()
        }
    }

    fun updateQrConfig(config: QrRenderConfig) {
        _qrConfig.value = config
    }

    fun updatePayoutSettings(
        stripeConnected: Boolean,
        paypalHandle: String,
        cashAppTag: String,
        buyMeACoffeeUrl: String,
        preferredTipMethod: String
    ) {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            val updated = current.copy(
                stripeConnected = stripeConnected,
                paypalHandle = paypalHandle,
                cashAppTag = cashAppTag,
                buyMeACoffeeUrl = buyMeACoffeeUrl,
                preferredTipMethod = preferredTipMethod
            )
            repository.saveProfile(updated)
            FirestoreSyncManager.syncProfileToFirestore(getApplication(), updated)
            FirestoreSyncManager.syncUserProfile(
                context = getApplication(),
                email = "nabeeljuttm424@gmail.com",
                displayName = updated.displayName,
                handle = updated.handle,
                role = "creator"
            )
        }
    }

    fun upgradeToPro(
        tier: String,
        paymentMethod: String = "Google Play In-App Billing",
        renewalDate: String = "Nov 3, 2027",
        purchaseToken: String = "",
        orderId: String = ""
    ) {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            val updated = current.copy(
                isProUser = true,
                proPlanTier = tier,
                proPaymentMethod = paymentMethod,
                proSubscriptionRenewalDate = renewalDate,
                proPurchaseToken = purchaseToken,
                proOrderId = orderId
            )
            repository.saveProfile(updated)
            val txId = if (orderId.isNotBlank()) orderId else "tx_pro_${System.currentTimeMillis()}"
            val proAmount = if (tier == "PRO_ANNUAL") 69.99 else 9.99
            FirestoreSyncManager.recordTransaction(
                context = getApplication(),
                transactionId = txId,
                type = "PRO_SUBSCRIPTION",
                itemTitle = "MultiLink PRO ($tier)",
                amount = proAmount,
                currency = "USD",
                paymentMethod = paymentMethod,
                creatorHandle = current.handle,
                creatorName = current.displayName,
                customerEmail = "nabeeljuttm424@gmail.com",
                customerName = current.displayName,
                status = "ACTIVE"
            )
            FirestoreSyncManager.syncUserProfile(
                context = getApplication(),
                email = "nabeeljuttm424@gmail.com",
                displayName = current.displayName,
                handle = current.handle,
                role = "creator"
            )
        }
    }

    fun cancelProSubscription() {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            val updated = current.copy(
                isProUser = false,
                proPlanTier = "FREE",
                proPaymentMethod = "",
                proSubscriptionRenewalDate = "",
                proPurchaseToken = "",
                proOrderId = ""
            )
            repository.saveProfile(updated)
        }
    }

    fun recordTipWithPayment(amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            val current = profile.value ?: return@launch
            val updatedProfile = current.copy(
                tipGoalCurrent = current.tipGoalCurrent + amount
            )
            repository.saveProfile(updatedProfile)

            // Also record click/conversion for tip item
            val tipLink = allLinks.value.find { it.category == LinkCategory.TIP } ?: allLinks.value.firstOrNull()
            if (tipLink != null) {
                repository.recordClick(
                    link = tipLink,
                    source = "Tip via $paymentMethod",
                    revenue = amount
                )
            }

            val txId = "tx_tip_${System.currentTimeMillis()}"
            FirestoreSyncManager.recordTransaction(
                context = getApplication(),
                transactionId = txId,
                type = "TIP",
                itemTitle = "Fan Tip Support",
                amount = amount,
                currency = "USD",
                paymentMethod = paymentMethod,
                creatorHandle = current.handle,
                creatorName = current.displayName,
                customerEmail = "nabeeljuttm424@gmail.com",
                customerName = "Supporter",
                donorNote = "Direct Tip via $paymentMethod"
            )
            FirestoreSyncManager.syncUserProfile(
                context = getApplication(),
                email = "nabeeljuttm424@gmail.com",
                displayName = current.displayName,
                handle = current.handle,
                role = "creator"
            )
        }
    }

    private fun startCloudSync() {
        viewModelScope.launch {
            profile.filterNotNull().collect { prof ->
                FirestoreSyncManager.syncProfileToFirestore(getApplication(), prof)
                FirestoreSyncManager.syncUserProfile(
                    context = getApplication(),
                    email = "nabeeljuttm424@gmail.com",
                    displayName = prof.displayName,
                    handle = prof.handle,
                    role = "creator"
                )
                FirestoreSyncManager.startWebEventListener(getApplication(), prof.handle) { webEvent ->
                    viewModelScope.launch {
                        repository.recordExternalClick(webEvent)
                        if (webEvent.revenueAmount > 0.0) {
                            if (webEvent.category == LinkCategory.TIP.name) {
                                val curr = profile.value
                                if (curr != null) {
                                    repository.saveProfile(curr.copy(tipGoalCurrent = curr.tipGoalCurrent + webEvent.revenueAmount))
                                }
                            }
                            val txId = "tx_web_${webEvent.timestamp}"
                            FirestoreSyncManager.recordTransaction(
                                context = getApplication(),
                                transactionId = txId,
                                type = webEvent.category,
                                itemTitle = webEvent.linkTitle,
                                amount = webEvent.revenueAmount,
                                currency = "USD",
                                paymentMethod = "Web Portal (${webEvent.sourcePlatform})",
                                creatorHandle = prof.handle,
                                creatorName = prof.displayName,
                                customerEmail = "nabeeljuttm424@gmail.com",
                                customerName = "Web Customer"
                            )
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            allLinks.collect { links ->
                val handle = profile.value?.handle ?: "nabeeljutt"
                if (links.isNotEmpty()) {
                    FirestoreSyncManager.syncLinksToFirestore(getApplication(), handle, links)
                }
            }
        }
    }

    fun syncNowToWebCloud() {
        viewModelScope.launch {
            val prof = profile.value ?: return@launch
            FirestoreSyncManager.syncProfileToFirestore(getApplication(), prof)
            val links = allLinks.value
            if (links.isNotEmpty()) {
                FirestoreSyncManager.syncLinksToFirestore(getApplication(), prof.handle, links)
            }
        }
    }

    fun checkForUpdatesManually() {
        appUpdateManager.checkForUpdates(isUserInitiated = true)
    }

    fun dismissUpdateDialog() {
        appUpdateManager.dismissUpdateDialog()
    }

    fun showUpdateDialog() {
        appUpdateManager.showUpdateDialog()
    }

    fun launchPlayStoreUpdate(activity: android.app.Activity? = null) {
        appUpdateManager.launchPlayStoreUpdate(activity)
    }

    override fun onCleared() {
        super.onCleared()
        FirestoreSyncManager.stopWebEventListener()
        playBillingManager.destroy()
    }
}
