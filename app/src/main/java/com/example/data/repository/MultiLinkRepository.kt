package com.example.data.repository

import com.example.data.local.MultiLinkDao
import com.example.data.model.ClickEvent
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class MultiLinkRepository(private val dao: MultiLinkDao) {

    val profile: Flow<CreatorProfile?> = dao.getProfile()
    val allLinks: Flow<List<LinkItem>> = dao.getAllLinks()
    val activeLinks: Flow<List<LinkItem>> = dao.getActiveLinks()
    val clickEvents: Flow<List<ClickEvent>> = dao.getAllClickEvents()
    val recentClicks: Flow<List<ClickEvent>> = dao.getRecentClickEvents(15)
    val totalClickCount: Flow<Int> = dao.getTotalClickCount()
    val totalRevenue: Flow<Double> = dao.getTotalRevenue()

    fun getLinksByCategory(category: LinkCategory): Flow<List<LinkItem>> =
        dao.getLinksByCategory(category)

    suspend fun saveProfile(profile: CreatorProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addLink(link: LinkItem): Long {
        return dao.insertLink(link)
    }

    suspend fun updateLink(link: LinkItem) {
        dao.updateLink(link)
    }

    suspend fun deleteLink(link: LinkItem) {
        dao.deleteLink(link)
    }

    suspend fun deleteLinkById(id: Long) {
        dao.deleteLinkById(id)
    }

    suspend fun toggleLinkActive(linkId: Long, isActive: Boolean) {
        dao.toggleLinkActive(linkId, isActive)
    }

    suspend fun recordClick(
        link: LinkItem,
        source: String = "Landing Page",
        country: String = "United States",
        device: String = "Mobile",
        revenue: Double = 0.0
    ) {
        dao.incrementLinkClicks(link.id)
        val event = ClickEvent(
            linkId = link.id,
            linkTitle = link.title,
            category = link.category.name,
            timestamp = System.currentTimeMillis(),
            sourcePlatform = source,
            country = country,
            device = device,
            revenueAmount = revenue
        )
        dao.insertClickEvent(event)
    }

    suspend fun recordExternalClick(event: ClickEvent) {
        if (event.linkId > 0) {
            dao.incrementLinkClicks(event.linkId)
        }
        dao.insertClickEvent(event)
    }

    suspend fun simulateLiveVisitorClick() {
        val links = dao.getAllLinks().firstOrNull() ?: return
        if (links.isEmpty()) return
        val randomLink = links.random()

        val platforms = listOf("Instagram Bio", "TikTok Bio", "Twitter / X", "Direct / QR Scan", "YouTube Description")
        val countries = listOf("United States", "Japan", "Germany", "United Kingdom", "Canada", "France", "Australia", "Brazil")
        val devices = listOf("Mobile (Android)", "Mobile (iOS)", "Desktop (Mac)", "Desktop (Windows)")

        var rev = 0.0
        if (randomLink.category == LinkCategory.PRODUCT) {
            rev = if (Random.nextBoolean()) 39.0 else 19.0
        } else if (randomLink.category == LinkCategory.TIP) {
            rev = listOf(3.0, 5.0, 10.0, 25.0).random()
        }

        recordClick(
            link = randomLink,
            source = platforms.random(),
            country = countries.random(),
            device = devices.random(),
            revenue = rev
        )
    }

    suspend fun ensureInitialDataSeeded() {
        val existingProfile = dao.getProfile().firstOrNull()
        if (existingProfile == null || existingProfile.handle == "alexa_aura" || existingProfile.displayName == "Alexa Rivers") {
            dao.insertOrUpdateProfile(
                CreatorProfile(
                    id = 1,
                    handle = "nabeeljutt",
                    displayName = "Nabeel Jutt",
                    headline = "Digital Creator & Builder",
                    bio = "Welcome to my official MultiLink hub! Explore my projects, tools, socials, and direct support.",
                    avatarDrawableName = "creator_avatar_default_1793630325308",
                    location = "Global",
                    isVerified = true,
                    themeId = "CYBER_NEON",
                    customDomain = "gen-lang-client-0468598937.web.app/?handle=nabeeljutt",
                    tipGoalTitle = "Creator Studio & Production Setup",
                    tipGoalCurrent = 0.0,
                    tipGoalTarget = 1000.0,
                    stripeConnected = false,
                    stripeAccountId = "",
                    paypalHandle = "nabeeljuttm424",
                    cashAppTag = "",
                    buyMeACoffeeUrl = "",
                    preferredTipMethod = "PayPal"
                )
            )

            val existingLinks = dao.getAllLinks().firstOrNull()
            if (existingLinks.isNullOrEmpty() || existingLinks.any { it.url.contains("alexa") }) {
                dao.deleteAllLinks()
                val seedLinks = listOf(
                    // Business: Digital Product
                    LinkItem(
                        title = "Cyber Frontier Cinematic 3D LUT Pack",
                        url = "https://gumroad.com",
                        category = LinkCategory.PRODUCT,
                        iconKey = "book",
                        subtitle = "Professional 3D LUT color grading presets, .cube files & config",
                        price = "$39.00",
                        badge = "POPULAR",
                        coverImageName = "digital_product_mockup_1793630338547",
                        isFeatured = true,
                        isActive = true,
                        orderIndex = 0,
                        totalClicks = 0,
                        isProProduct = true,
                        downloadFileName = "cyber_frontier_lut.cube",
                        fileSizeBytes = "12 MB",
                        licenseType = "Commercial & Studio License"
                    ),
                    // Tip Jar
                    LinkItem(
                        title = "Support My Work • Direct Creator Tip",
                        url = "https://www.paypal.me/nabeeljuttm424/5",
                        category = LinkCategory.TIP,
                        iconKey = "coffee",
                        subtitle = "Directly support content creation and open tools",
                        price = "Pay what you want",
                        badge = "SUPPORT",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 1,
                        totalClicks = 0
                    ),
                    // Social Links
                    LinkItem(
                        title = "YouTube Channel",
                        url = "https://youtube.com",
                        category = LinkCategory.SOCIAL,
                        iconKey = "youtube",
                        subtitle = "Official YouTube Videos & Creator Content",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 2,
                        totalClicks = 0
                    ),
                    LinkItem(
                        title = "Instagram",
                        url = "https://instagram.com",
                        category = LinkCategory.SOCIAL,
                        iconKey = "instagram",
                        subtitle = "Daily Updates, Stories & Highlights",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 3,
                        totalClicks = 0
                    ),
                    LinkItem(
                        title = "TikTok",
                        url = "https://tiktok.com",
                        category = LinkCategory.SOCIAL,
                        iconKey = "tiktok",
                        subtitle = "Shorts, Reels & Creative Highlights",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 4,
                        totalClicks = 0
                    ),
                    LinkItem(
                        title = "X / Twitter",
                        url = "https://x.com",
                        category = LinkCategory.SOCIAL,
                        iconKey = "twitter",
                        subtitle = "Updates, Threads & Announcements",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 5,
                        totalClicks = 0
                    ),
                    LinkItem(
                        title = "GitHub",
                        url = "https://github.com",
                        category = LinkCategory.SOCIAL,
                        iconKey = "github",
                        subtitle = "Open Source Code, Repositories & Tools",
                        isFeatured = false,
                        isActive = true,
                        orderIndex = 6,
                        totalClicks = 0
                    )
                )

                dao.insertAllLinks(seedLinks)
            }
        }
    }
}
