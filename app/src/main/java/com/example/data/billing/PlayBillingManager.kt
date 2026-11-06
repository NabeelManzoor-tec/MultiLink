package com.example.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BillingStatus {
    object Idle : BillingStatus()
    object Connecting : BillingStatus()
    data class Connected(val availablePlans: List<ProductDetails>) : BillingStatus()
    data class Error(val message: String) : BillingStatus()
    object Cancelled : BillingStatus()
    data class SubscriptionActive(
        val purchaseToken: String,
        val orderId: String,
        val planTier: String
    ) : BillingStatus() {
        val tier: String get() = planTier
    }
}

class PlayBillingManager(
    private val context: Context,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        private const val TAG = "PlayBilling"

        // Primary underscore product IDs starting with numbers
        const val SUB_MONTHLY = "1_pro_monthly"
        const val SUB_ANNUAL = "2_pro_annual"

        // Compatibility aliases (both hyphenated and legacy)
        const val SUB_MONTHLY_HYPHEN = "1-pro-monthly"
        const val SUB_MONTHLY_LEGACY = "multilink_pro_monthly"
        const val SUB_ANNUAL_HYPHEN = "2-pro-annual"
        const val SUB_ANNUAL_LEGACY = "multilink_pro_annual"

        // In-App Products (Tips & LUT Pack)
        const val INAPP_TIP_5 = "1_tip_5"
        const val INAPP_TIP_10 = "2_tip_10"
        const val INAPP_TIP_25 = "3_tip_25"
        const val INAPP_PRODUCT_LUT = "4_product_lut"

        const val INAPP_TIP_5_HYPHEN = "1-tip-5"
        const val INAPP_TIP_10_HYPHEN = "2-tip-10"
        const val INAPP_TIP_25_HYPHEN = "3-tip-25"
        const val INAPP_PRODUCT_LUT_HYPHEN = "4-product-lut"

        val ALL_SUB_ANNUAL_IDS = setOf(SUB_ANNUAL, SUB_ANNUAL_HYPHEN, SUB_ANNUAL_LEGACY)
        val ALL_SUB_MONTHLY_IDS = setOf(SUB_MONTHLY, SUB_MONTHLY_HYPHEN, SUB_MONTHLY_LEGACY)
        val ALL_TIP_IDS = setOf(INAPP_TIP_5, INAPP_TIP_5_HYPHEN, INAPP_TIP_10, INAPP_TIP_10_HYPHEN, INAPP_TIP_25, INAPP_TIP_25_HYPHEN)
    }

    private val _billingStatus = MutableStateFlow<BillingStatus>(BillingStatus.Idle)
    val billingStatus: StateFlow<BillingStatus> = _billingStatus.asStateFlow()

    private val _productDetailsMap = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetailsMap: StateFlow<Map<String, ProductDetails>> = _productDetailsMap.asStateFlow()

    private var onPurchaseSuccessCallback: ((tier: String, token: String, orderId: String) -> Unit)? = null

    val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    init {
        startConnection()
    }

    fun startConnection() {
        if (!billingClient.isReady) {
            _billingStatus.value = BillingStatus.Connecting
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "BillingClient connected successfully")
            querySubscriptionProducts()
            restorePurchases()
        } else {
            Log.w(TAG, "Billing setup failed with code: ${billingResult.responseCode} - ${billingResult.debugMessage}")
            _billingStatus.value = BillingStatus.Error(
                "Google Play Store disconnected (Code: ${billingResult.responseCode}). ${billingResult.debugMessage}"
            )
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.w(TAG, "Billing service disconnected. Retrying connection...")
        _billingStatus.value = BillingStatus.Error("Google Play Store connection lost. Reconnecting...")
    }

    private fun querySubscriptionProducts() {
        val subProductIds = listOf(
            SUB_MONTHLY, SUB_MONTHLY_HYPHEN, SUB_MONTHLY_LEGACY,
            SUB_ANNUAL, SUB_ANNUAL_HYPHEN, SUB_ANNUAL_LEGACY
        ).distinct()

        val subList = subProductIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val inappProductIds = listOf(
            INAPP_TIP_5, INAPP_TIP_5_HYPHEN,
            INAPP_TIP_10, INAPP_TIP_10_HYPHEN,
            INAPP_TIP_25, INAPP_TIP_25_HYPHEN,
            INAPP_PRODUCT_LUT, INAPP_PRODUCT_LUT_HYPHEN
        ).distinct()

        val inappList = inappProductIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(subList).build()
        ) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val list = queryProductDetailsResult.productDetailsList
                val current = _productDetailsMap.value.toMutableMap()
                list.forEach { current[it.productId] = it }
                _productDetailsMap.value = current
                _billingStatus.value = BillingStatus.Connected(current.values.toList())
                Log.d(TAG, "Loaded ${list.size} subscriptions from Google Play")
            } else {
                Log.w(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
                _billingStatus.value = BillingStatus.Error(
                    "Play Console In-App Subscriptions pending (Code: ${billingResult.responseCode}). " +
                    "Make sure '$SUB_MONTHLY' ($9.99) and '$SUB_ANNUAL' ($69.99) are active in Play Console."
                )
            }
        }

        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(inappList).build()
        ) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val list = queryProductDetailsResult.productDetailsList
                val current = _productDetailsMap.value.toMutableMap()
                list.forEach { current[it.productId] = it }
                _productDetailsMap.value = current
                Log.d(TAG, "Loaded ${list.size} in-app products from Google Play")
            }
        }
    }

    fun launchSubscriptionPurchase(
        activity: Activity,
        planTier: String, // "PRO_MONTHLY" or "PRO_ANNUAL"
        onSuccess: (tier: String, token: String, orderId: String) -> Unit = { _, _, _ -> }
    ) {
        this.onPurchaseSuccessCallback = onSuccess

        val targetCandidates = if (planTier == "PRO_ANNUAL") {
            listOf(SUB_ANNUAL, SUB_ANNUAL_HYPHEN, SUB_ANNUAL_LEGACY)
        } else {
            listOf(SUB_MONTHLY, SUB_MONTHLY_HYPHEN, SUB_MONTHLY_LEGACY)
        }

        val details = targetCandidates.firstNotNullOfOrNull { _productDetailsMap.value[it] }

        if (details == null) {
            val primaryId = if (planTier == "PRO_ANNUAL") SUB_ANNUAL else SUB_MONTHLY
            Log.w(TAG, "ProductDetails for $primaryId not found in Play Store response")
            _billingStatus.value = BillingStatus.Error(
                "Subscription product '$primaryId' not yet active on Google Play. " +
                "Verify your Play Console Merchant account and In-App Subscriptions setup."
            )
            return
        }

        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            Log.w(TAG, "No valid subscription offer token found for ${details.productId}")
            _billingStatus.value = BillingStatus.Error("No valid pricing offer available from Google Play.")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val response = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (response.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "Failed to launch billing flow: ${response.debugMessage}")
            _billingStatus.value = BillingStatus.Error("Could not launch Google Play sheet: ${response.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handleVerifiedPurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User canceled Google Play purchase flow")
            _billingStatus.value = BillingStatus.Cancelled
        } else {
            Log.w(TAG, "Purchase failed: Code ${billingResult.responseCode} - ${billingResult.debugMessage}")
            _billingStatus.value = BillingStatus.Error(
                "Payment could not be completed via Google Play (Code: ${billingResult.responseCode})."
            )
        }
    }

    private fun handleVerifiedPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val isTip = purchase.products.any { ALL_TIP_IDS.contains(it) }

            if (isTip) {
                // Consume tip so supporter can send tips repeatedly
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.consumeAsync(consumeParams) { result, _ ->
                    Log.d(TAG, "Tip consumed: ${result.debugMessage}")
                }
            } else if (!purchase.isAcknowledged) {
                // Acknowledge subscription or non-consumable digital product
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged successfully: ${purchase.orderId}")
                    } else {
                        Log.w(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                    }
                }
            }

            val isAnnual = purchase.products.any { ALL_SUB_ANNUAL_IDS.contains(it) }
            val tier = if (isAnnual) "PRO_ANNUAL" else "PRO_MONTHLY"
            val orderId = purchase.orderId ?: "GPA.PLAY-${System.currentTimeMillis()}"

            _billingStatus.value = BillingStatus.SubscriptionActive(
                purchaseToken = purchase.purchaseToken,
                orderId = orderId,
                planTier = tier
            )

            onPurchaseSuccessCallback?.invoke(tier, purchase.purchaseToken, orderId)
        }
    }

    fun restorePurchases() {
        if (!billingClient.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val active = purchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                if (active != null) {
                    val isAnnual = active.products.any { ALL_SUB_ANNUAL_IDS.contains(it) }
                    val tier = if (isAnnual) "PRO_ANNUAL" else "PRO_MONTHLY"
                    _billingStatus.value = BillingStatus.SubscriptionActive(
                        purchaseToken = active.purchaseToken,
                        orderId = active.orderId ?: "GPA.EXISTING",
                        planTier = tier
                    )
                }
            }
        }
    }

    fun destroy() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }
}
