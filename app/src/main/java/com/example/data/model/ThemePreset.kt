package com.example.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class LandingThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val borderColor: Color,
    val accentColor: Color,
    val textColor: Color,
    val textMutedColor: Color,
    val gradientBrush: Brush
)

object LandingThemes {
    val presets = listOf(
        LandingThemePreset(
            id = "CYBER_NEON",
            name = "Cyber Neon",
            description = "High-voltage violet & cyan glow on deep midnight",
            backgroundColor = Color(0xFF090D16),
            surfaceColor = Color(0xFF131926),
            borderColor = Color(0xFF283654),
            accentColor = Color(0xFF06B6D4),
            textColor = Color(0xFFF8FAFC),
            textMutedColor = Color(0xFF94A3B8),
            gradientBrush = Brush.horizontalGradient(
                listOf(Color(0xFF6366F1), Color(0xFF06B6D4))
            )
        ),
        LandingThemePreset(
            id = "LUXURY_DARK",
            name = "Obsidian Gold",
            description = "Brushed champagne gold & ultra-matte obsidian",
            backgroundColor = Color(0xFF08080A),
            surfaceColor = Color(0xFF141418),
            borderColor = Color(0xFF383220),
            accentColor = Color(0xFFF59E0B),
            textColor = Color(0xFFFFFBEB),
            textMutedColor = Color(0xFFA8A29E),
            gradientBrush = Brush.horizontalGradient(
                listOf(Color(0xFFD97706), Color(0xFFFBBF24))
            )
        ),
        LandingThemePreset(
            id = "SUNSET_BLAZE",
            name = "Sunset Radiant",
            description = "Warm coral rose to radiant golden amber",
            backgroundColor = Color(0xFF110A14),
            surfaceColor = Color(0xFF1F1226),
            borderColor = Color(0xFF4A2542),
            accentColor = Color(0xFFF43F5E),
            textColor = Color(0xFFFFF1F2),
            textMutedColor = Color(0xFFFDA4AF),
            gradientBrush = Brush.horizontalGradient(
                listOf(Color(0xFFF43F5E), Color(0xFFF59E0B))
            )
        ),
        LandingThemePreset(
            id = "EMERALD_MINT",
            name = "Emerald Matrix",
            description = "Deep dark jade with electric mint highlights",
            backgroundColor = Color(0xFF061311),
            surfaceColor = Color(0xFF0D2420),
            borderColor = Color(0xFF1A453D),
            accentColor = Color(0xFF10B981),
            textColor = Color(0xFFECFDF5),
            textMutedColor = Color(0xFF6EE7B7),
            gradientBrush = Brush.horizontalGradient(
                listOf(Color(0xFF059669), Color(0xFF10B981))
            )
        ),
        LandingThemePreset(
            id = "MINIMAL_CLEAN",
            name = "Titanium Clean",
            description = "Monochrome minimalism with stark high contrast",
            backgroundColor = Color(0xFF0F1115),
            surfaceColor = Color(0xFF1A1D24),
            borderColor = Color(0xFF2E3440),
            accentColor = Color(0xFFE2E8F0),
            textColor = Color(0xFFFFFFFF),
            textMutedColor = Color(0xFF94A3B8),
            gradientBrush = Brush.horizontalGradient(
                listOf(Color(0xFF475569), Color(0xFF94A3B8))
            )
        )
    )

    fun getById(id: String): LandingThemePreset {
        return presets.find { it.id == id } ?: presets.first()
    }
}
