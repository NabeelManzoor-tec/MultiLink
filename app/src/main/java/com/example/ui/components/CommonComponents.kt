package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LandingThemes
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class AvatarPreset(
    val id: String,
    val title: String,
    val resId: Int
)

object AvatarPresets {
    val presets = listOf(
        AvatarPreset(
            id = "creator_avatar_default_1793630325308",
            title = "Digital Artist",
            resId = R.drawable.creator_avatar_default_1793630325308
        ),
        AvatarPreset(
            id = "avatar_preset_tech_1793633084131",
            title = "Tech Founder",
            resId = R.drawable.avatar_preset_tech_1793633084131
        ),
        AvatarPreset(
            id = "avatar_preset_music_1793633099465",
            title = "Sound Producer",
            resId = R.drawable.avatar_preset_music_1793633099465
        )
    )
}

@Composable
fun CreatorAvatarView(
    avatarPath: String,
    contentDescription: String? = "Creator Avatar",
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (avatarPath.startsWith("content://") || avatarPath.startsWith("file://")) {
        AsyncImage(
            model = avatarPath,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        val resId = when {
            avatarPath.contains("tech") -> R.drawable.avatar_preset_tech_1793633084131
            avatarPath.contains("music") -> R.drawable.avatar_preset_music_1793633099465
            else -> R.drawable.creator_avatar_default_1793630325308
        }
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

object IconResolver {
    fun getIcon(key: String): ImageVector {
        return when (key.lowercase()) {
            "youtube", "video" -> Icons.Default.PlayArrow
            "instagram", "photo" -> Icons.Default.AutoAwesome
            "tiktok" -> Icons.Default.PlayArrow
            "twitter", "x" -> Icons.AutoMirrored.Filled.TrendingUp
            "github", "code" -> Icons.Default.Code
            "discord" -> Icons.Default.Headphones
            "spotify", "music" -> Icons.Default.Headphones
            "book", "product", "store" -> Icons.Default.ShoppingBag
            "calendar", "service" -> Icons.Default.CalendarMonth
            "coffee", "tip" -> Icons.Default.Coffee
            "sparkles", "affiliate" -> Icons.Default.Star
            else -> Icons.Default.Link
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = BrandSecondary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("metric_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = title,
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ThemeSelectorRow(
    selectedThemeId: String,
    onSelectTheme: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Landing Page Theme",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LandingThemes.presets.forEach { preset ->
                val isSelected = preset.id == selectedThemeId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(preset.backgroundColor)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) preset.accentColor else BrandDarkBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectTheme(preset.id) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(preset.gradientBrush)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = preset.name.split(" ").first(),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) preset.textColor else TextMuted
                        )
                    }
                }
            }
        }
    }
}
