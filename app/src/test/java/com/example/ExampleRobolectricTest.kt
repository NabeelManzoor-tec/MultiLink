package com.example

import com.example.ui.components.QrCodeHelper
import com.example.ui.components.QrEyeStyle
import com.example.ui.components.QrRenderConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
class ExampleRobolectricTest {

    @Test
    fun qrCodeMatrixGenerationProducesValidMatrix() {
        val matrix = QrCodeHelper.generateBitMatrix("https://gen-lang-client-0468598937.web.app/?handle=alexa_aura", 25)
        assertNotNull(matrix)
        assertTrue(matrix!!.isNotEmpty())
        assertTrue(matrix[0].isNotEmpty())
    }

    @Test
    fun avatarPresetsArePopulated() {
        val presets = com.example.ui.components.AvatarPresets.presets
        assertTrue(presets.isNotEmpty())
        assertEquals(3, presets.size)
    }
}
