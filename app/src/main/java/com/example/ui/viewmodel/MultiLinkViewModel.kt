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
        }
    }

    private fun startCloudSync() {
        viewModelScope.launch {
            profile.filterNotNull().collect { prof ->
                FirestoreSyncManager.syncProfileToFirestore(getApplication(), prof)
                FirestoreSyncManager.startWebEventListener(getApplication(), prof.handle) { webEvent ->
                    viewModelScope.launch {
                        repository.recordExternalClick(webEvent)
                        if (webEvent.revenueAmount > 0.0 && webEvent.category == LinkCategory.TIP.name) {
                            val curr = profile.value
                            if (curr != null) {
                                repository.saveProfile(curr.copy(tipGoalCurrent = curr.tipGoalCurrent + webEvent.revenueAmount))
                            }
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            allLinks.collect { links ->
                val handle = profile.value?.handle ?: "alexa_aura"
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
