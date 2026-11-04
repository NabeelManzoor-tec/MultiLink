package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClickEvent
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.example.ui.components.IconResolver
import com.example.ui.components.MetricCard
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
fun AnalyticsScreen(
    totalClicks: Int,
    totalRevenue: Double,
    topLinks: List<LinkItem>,
    recentEvents: List<ClickEvent>,
    onSimulateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("All Time") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Live Stream Pulse Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_live_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandSuccess.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(BrandSuccess.copy(alpha = 0.3f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandSuccess)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "REAL-TIME TELEMETRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSuccess,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Audience click & engagement stream active",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onSimulateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .testTag("simulate_traffic_button")
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = BrandDarkBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Test Click",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDarkBackground
                        )
                    }
                }
            }
        }

        // Timeframe selector
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(listOf("Today", "7 Days", "30 Days", "All Time")) { period ->
                    FilterChip(
                        selected = selectedTimeframe == period,
                        onClick = { selectedTimeframe = period },
                        label = { Text(period, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary.copy(alpha = 0.25f),
                            selectedLabelColor = BrandPrimary,
                            containerColor = BrandDarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Primary KPI 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total Clicks",
                        value = "${totalClicks.coerceAtLeast(1)}",
                        subtitle = "+38% vs prev period",
                        icon = Icons.Default.AdsClick,
                        accentColor = BrandSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Gross Revenue",
                        value = "$${String.format("%.2f", totalRevenue)}",
                        subtitle = "From products & tips",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = BrandSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Conversion Rate",
                        value = "19.4%",
                        subtitle = "High intent traffic",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = BrandWarning,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Global Reach",
                        value = "12 Countries",
                        subtitle = "Direct & QR traffic",
                        icon = Icons.Default.Public,
                        accentColor = BrandPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Top Performing Links
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_performing_links_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Top Performing Links",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Most clicked destinations on your MultiLink page",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val maxClicks = (topLinks.maxOfOrNull { it.totalClicks } ?: 1).coerceAtLeast(1)

                    topLinks.forEachIndexed { index, link ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary,
                                        modifier = Modifier.width(24.dp)
                                    )
                                    Text(
                                        text = link.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = "${link.totalClicks} clicks",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            val ratio = (link.totalClicks.toFloat() / maxClicks).coerceIn(0.05f, 1f)
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when (link.category) {
                                    LinkCategory.PRODUCT -> BrandWarning
                                    LinkCategory.SERVICE -> BrandPrimary
                                    LinkCategory.TIP -> BrandSuccess
                                    else -> BrandSecondary
                                },
                                trackColor = BrandDarkSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        // Live Real-Time Activity Feed Header
        item {
            Text(
                text = "Live Audience Activity Feed (${recentEvents.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Events Stream
        items(recentEvents, key = { it.id }) { event ->
            RecentActivityItem(event = event)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun RecentActivityItem(event: ClickEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_event_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrandDarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (event.category) {
                        "PRODUCT" -> Icons.Default.MonetizationOn
                        "TIP" -> Icons.Default.Bolt
                        "SERVICE" -> Icons.Default.People
                        else -> Icons.Default.AdsClick
                    },
                    contentDescription = null,
                    tint = when (event.category) {
                        "PRODUCT" -> BrandWarning
                        "TIP" -> BrandSuccess
                        "SERVICE" -> BrandPrimary
                        else -> BrandSecondary
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.linkTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    if (event.revenueAmount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+$${event.revenueAmount.toInt()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandSuccess,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BrandSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "${event.sourcePlatform} • ${event.country}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = formatTimeAgo(event.timestamp),
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        else -> "${hours / 24}d ago"
    }
}
