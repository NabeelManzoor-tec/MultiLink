package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CreatorProfile
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import android.net.Uri
import android.webkit.JavascriptInterface
import com.example.data.firebase.FirestoreSyncManager
import com.example.data.model.LinkItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class AndroidMultiLinkBridge(
    private val context: android.content.Context,
    private val profile: CreatorProfile,
    private val links: List<LinkItem>,
    private val onRecordClick: (Long, String, String, Double) -> Unit
) {
    @JavascriptInterface
    fun downloadProductAssets(productTitle: String) {
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            com.example.util.LocalAssetDownloader.downloadProductAssets(context, productTitle)
        }
    }

    @JavascriptInterface
    fun getProfileJson(): String {
        return try {
            JSONObject().apply {
                put("handle", profile.handle)
                put("displayName", profile.displayName)
                put("headline", profile.headline)
                put("bio", profile.bio)
                put("avatarUrl", "images/${profile.avatarDrawableName}.jpg")
                put("isVerified", profile.isVerified)
                put("isProUser", profile.isProUser)
                put("themeId", profile.themeId)
                put("tipGoalTitle", profile.tipGoalTitle)
                put("tipGoalCurrent", profile.tipGoalCurrent)
                put("tipGoalTarget", profile.tipGoalTarget)
                put("paypalHandle", profile.paypalHandle)
                put("cashAppTag", profile.cashAppTag)
                put("stripeConnected", profile.stripeConnected)
                put("stripeAccountId", profile.stripeAccountId)
                put("buyMeACoffeeUrl", profile.buyMeACoffeeUrl)
                put("preferredTipMethod", profile.preferredTipMethod)
            }.toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    @JavascriptInterface
    fun getLinksJson(): String {
        return try {
            val arr = JSONArray()
            links.forEach { link ->
                val obj = JSONObject().apply {
                    put("id", link.id)
                    put("title", link.title)
                    put("subtitle", link.subtitle)
                    put("url", link.url)
                    put("category", link.category.name)
                    put("iconKey", link.iconKey)
                    put("price", link.price)
                    put("badge", link.badge)
                    put("isFeatured", link.isFeatured)
                    put("isActive", link.isActive)
                    put("coverImageName", if (link.coverImageName.isNotEmpty()) "images/${link.coverImageName}.jpg" else "")
                }
                arr.put(obj)
            }
            arr.toString()
        } catch (e: Exception) {
            "[]"
        }
    }

    @JavascriptInterface
    fun recordClick(linkId: Long, linkTitle: String, category: String, revenue: Double) {
        onRecordClick(linkId, linkTitle, category, revenue)
    }

    @JavascriptInterface
    fun recordTransaction(
        txId: String,
        type: String,
        itemTitle: String,
        amount: Double,
        paymentMethod: String,
        customerEmail: String,
        customerName: String,
        note: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreSyncManager.recordTransaction(
                context = context,
                transactionId = txId.ifEmpty { "tx_web_${System.currentTimeMillis()}" },
                type = type,
                itemTitle = itemTitle,
                amount = amount,
                currency = "USD",
                paymentMethod = paymentMethod,
                creatorHandle = profile.handle,
                creatorName = profile.displayName,
                customerEmail = customerEmail.ifEmpty { "nabeeljuttm424@gmail.com" },
                customerName = customerName.ifEmpty { "Customer" },
                donorNote = note
            )
        }
    }

    @JavascriptInterface
    fun syncUserProfile(
        email: String,
        displayName: String,
        handle: String,
        role: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            FirestoreSyncManager.syncUserProfile(
                context = context,
                email = email.ifEmpty { "nabeeljuttm424@gmail.com" },
                displayName = displayName.ifEmpty { "Verified User" },
                handle = handle.ifEmpty { profile.handle },
                role = role.ifEmpty { "customer" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullscreenWebViewerScreen(
    profile: CreatorProfile,
    links: List<LinkItem> = emptyList(),
    onRecordClick: (Long, String, String, Double) -> Unit = { _, _, _, _ -> },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cleanHandle = profile.handle.trim().removePrefix("@").lowercase().ifEmpty { "nabeeljutt" }
    val publicWebUrl = "https://gen-lang-client-0468598937.web.app/?handle=$cleanHandle"
    val localAssetUrl = "file:///android_asset/web/index.html?handle=$cleanHandle&theme=${profile.themeId}"

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableIntStateOf(0) }

    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandDarkBackground,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "@$cleanHandle",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(BrandSuccess)
                                    )
                                }
                                Text(
                                    text = "gen-lang-client-0468598937.web.app",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("web_viewer_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Studio",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        // Refresh button
                        IconButton(
                            onClick = {
                                webViewInstance?.reload()
                                Toast.makeText(context, "Refreshing page...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("web_viewer_refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = TextSecondary
                            )
                        }

                        // Copy Link button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MultiLink Live URL", publicWebUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied live link: $publicWebUrl", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.testTag("web_viewer_copy_link_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Link",
                                tint = BrandSecondary
                            )
                        }

                        // Share Link button
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${profile.displayName} on MultiLink")
                                    putExtra(Intent.EXTRA_TEXT, "Check out my MultiLink page: $publicWebUrl")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Public Link"))
                            },
                            modifier = Modifier.testTag("web_viewer_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrandDarkBackground
                    )
                )

                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = BrandPrimary,
                        trackColor = BrandDarkBackground
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BrandDarkBackground)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.allowFileAccessFromFileURLs = true
                        settings.allowUniversalAccessFromFileURLs = true

                        addJavascriptInterface(
                            AndroidMultiLinkBridge(ctx, profile, links) { linkId, title, category, revenue ->
                                onRecordClick(linkId, title, category, revenue)
                            },
                            "AndroidMultiLink"
                        )

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress
                                if (newProgress >= 100) {
                                    isLoading = false
                                }
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: return false
                                if (url.startsWith("https://paypal.me") ||
                                    url.startsWith("https://www.paypal.me") ||
                                    url.startsWith("https://cash.app") ||
                                    url.startsWith("https://buy.stripe.com") ||
                                    url.startsWith("https://buymeacoffee.com") ||
                                    url.startsWith("https://gumroad.com") ||
                                    (!url.startsWith("file://") && !url.contains("web.app"))
                                ) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                        return true
                                    } catch (e: Exception) {
                                        // fallback
                                    }
                                }
                                return false
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                if (request?.url?.toString() != localAssetUrl) {
                                    view?.loadUrl(localAssetUrl)
                                }
                            }
                        }

                        // Load the bundled real-time connected Web app
                        loadUrl(localAssetUrl)
                        webViewInstance = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fullscreen_webview")
            )
        }
    }
}
