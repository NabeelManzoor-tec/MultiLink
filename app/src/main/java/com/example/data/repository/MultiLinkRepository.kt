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
        if (existingProfile == null) {
            dao.insertOrUpdateProfile(
                CreatorProfile(
                    id = 1,
                    handle = "alexa_aura",
                    displayName = "Alexa Rivers",
                    headline = "Digital Artist & Creative Technologist",
                    bio = "Creating next-gen cyberpunk shaders, 3D toolkits & mentoring ambitious indie creators worldwide 🚀",
                    avatarDrawableName = "creator_avatar_default_1793630325308",
                    location = "Tokyo & San Francisco",
                    isVerified = true,
                    themeId = "CYBER_NEON",
                    customDomain = "multilink.bio/@alexa_aura",
                    tipGoalTitle = "4K Unreal 5 Render Cluster",
                    tipGoalCurrent = 1840.0,
                    tipGoalTarget = 2500.0
                )
            )

            val seedLinks = listOf(
                // Business: Digital Product (Featured)
                LinkItem(
                    title = "The Digital Frontier: Cyberpunk 3D Toolkit 2026",
                    url = "https://gumroad.com/l/cyber-frontier",
                    category = LinkCategory.PRODUCT,
                    iconKey = "book",
                    subtitle = "50+ Procedural Shaders, Unreal Engine 5 project & LUTs",
                    price = "$39.00",
                    badge = "BESTSELLER",
                    coverImageName = "digital_product_mockup_1793630338547",
                    isFeatured = true,
                    isActive = true,
                    orderIndex = 0,
                    totalClicks = 342,
                    isProProduct = true,
                    downloadFileName = "cyberpunk_toolkit_2026.zip",
                    fileSizeBytes = "1.4 GB",
                    licenseType = "Commercial & Extended Use"
                ),
                // Business: Service
                LinkItem(
                    title = "1-on-1 Creative Direction & Portfolio Review",
                    url = "https://cal.com/alexa-aura/mentorship",
                    category = LinkCategory.SERVICE,
                    iconKey = "calendar",
                    subtitle = "45-minute live screen-share strategy session & roadmap",
                    price = "$150 / session",
                    badge = "2 SLOTS LEFT",
                    isFeatured = true,
                    isActive = true,
                    orderIndex = 1,
                    totalClicks = 88
                ),
                // Tip Jar
                LinkItem(
                    title = "Fuel The Lab • Send a Coffee / Tip",
                    url = "https://buymeacoffee.com/alexaaura",
                    category = LinkCategory.TIP,
                    iconKey = "coffee",
                    subtitle = "Directly support free open-source shader tutorials",
                    price = "Pay what you want",
                    badge = "SUPPORT",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 2,
                    totalClicks = 156
                ),
                // Social Links
                LinkItem(
                    title = "YouTube Channel",
                    url = "https://youtube.com/@alexa_aura",
                    category = LinkCategory.SOCIAL,
                    iconKey = "youtube",
                    subtitle = "128k Subscribers • Weekly VFX & Creative Coding Tutorials",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 3,
                    totalClicks = 512
                ),
                LinkItem(
                    title = "Instagram",
                    url = "https://instagram.com/alexa.aura",
                    category = LinkCategory.SOCIAL,
                    iconKey = "instagram",
                    subtitle = "84.2k Followers • Daily 3D Renders & Aesthetic BTS",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 4,
                    totalClicks = 428
                ),
                LinkItem(
                    title = "TikTok",
                    url = "https://tiktok.com/@alexa_aura",
                    category = LinkCategory.SOCIAL,
                    iconKey = "tiktok",
                    subtitle = "210k Community • Viral Breakdown Reels",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 5,
                    totalClicks = 620
                ),
                LinkItem(
                    title = "X / Twitter",
                    url = "https://x.com/alexa_aura",
                    category = LinkCategory.SOCIAL,
                    iconKey = "twitter",
                    subtitle = "Indie Devlogs, Real-Time Graphics Discussions & Drops",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 6,
                    totalClicks = 289
                ),
                LinkItem(
                    title = "Discord Community",
                    url = "https://discord.gg/alexa-creators",
                    category = LinkCategory.SOCIAL,
                    iconKey = "discord",
                    subtitle = "The Cyber Studio Lounge • 4.8k Active Members",
                    badge = "ACTIVE",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 7,
                    totalClicks = 194
                ),
                LinkItem(
                    title = "GitHub",
                    url = "https://github.com/alexa-aura",
                    category = LinkCategory.SOCIAL,
                    iconKey = "github",
                    subtitle = "Free GLSL Shaders, ThreeJS boilerplates & Python tools",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 8,
                    totalClicks = 143
                ),
                LinkItem(
                    title = "Spotify Playlist: Late Night Cyber Beats",
                    url = "https://open.spotify.com/playlist/alexa-lofi",
                    category = LinkCategory.SOCIAL,
                    iconKey = "spotify",
                    subtitle = "Curated synthwave & ambient beats for deep focus",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 9,
                    totalClicks = 177
                ),
                LinkItem(
                    title = "My Minimalist Desk Setup & Gear",
                    url = "https://kit.co/alexa/creator-desk",
                    category = LinkCategory.AFFILIATE,
                    iconKey = "sparkles",
                    subtitle = "Ultra-wide OLED, Shure Mic & Custom Mechanical Keyboard",
                    badge = "15% OFF",
                    isFeatured = false,
                    isActive = true,
                    orderIndex = 10,
                    totalClicks = 95
                )
            )

            dao.insertAllLinks(seedLinks)

            // Seed sample telemetry events
            val now = System.currentTimeMillis()
            val sampleEvents = mutableListOf<ClickEvent>()
            val seedSources = listOf("Direct / QR Scan", "Instagram Bio", "TikTok Bio", "Twitter / X", "YouTube Description")
            val seedCountries = listOf("United States", "Japan", "Germany", "United Kingdom", "Canada", "Singapore")
            val seedDevices = listOf("Mobile (Android)", "Mobile (iOS)", "Desktop (Mac)")

            for (i in 0..24) {
                val link = seedLinks.random()
                val offset = (25 - i) * 1800000L + Random.nextLong(10000, 600000)
                var rev = 0.0
                if (link.category == LinkCategory.PRODUCT && Random.nextFloat() > 0.6f) {
                    rev = 39.0
                } else if (link.category == LinkCategory.TIP && Random.nextFloat() > 0.5f) {
                    rev = listOf(5.0, 10.0, 25.0).random()
                }

                sampleEvents.add(
                    ClickEvent(
                        linkId = (i % seedLinks.size + 1).toLong(),
                        linkTitle = link.title,
                        category = link.category.name,
                        timestamp = now - offset,
                        sourcePlatform = seedSources.random(),
                        country = seedCountries.random(),
                        device = seedDevices.random(),
                        revenueAmount = rev
                    )
                )
            }
            dao.insertAllClickEvents(sampleEvents)
        }
    }
}
