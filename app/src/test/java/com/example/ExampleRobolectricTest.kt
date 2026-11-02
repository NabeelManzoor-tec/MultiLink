package com.example

import com.example.ui.components.QrCodeHelper
import com.example.ui.components.QrEyeStyle
import com.example.ui.components.QrRenderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun qrCodeMatrixGenerationProducesValidMatrix() {
        val matrix = QrCodeHelper.generateBitMatrix("https://multilink.bio/@alexa_aura", 25)
        assertNotNull(matrix)
        assertTrue(matrix!!.isNotEmpty())
        assertTrue(matrix[0].isNotEmpty())
    }

    @Test
    fun qrCodeBitmapGenerationSucceeds() {
        val bitmap = QrCodeHelper.generateBitmap(
            "https://multilink.bio/@alexa_aura",
            QrRenderConfig(eyeStyle = QrEyeStyle.ROUNDED),
            120
        )
        assertNotNull(bitmap)
        assertEquals(120, bitmap!!.width)
        assertEquals(120, bitmap.height)
    }

    @Test
    fun avatarPresetsArePopulated() {
        val presets = com.example.ui.components.AvatarPresets.presets
        assertTrue(presets.isNotEmpty())
        assertEquals(3, presets.size)
    }
}
