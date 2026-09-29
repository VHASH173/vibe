package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.supabase.SupabaseEdgeFunctions
import com.example.model.GiftCatalog
import com.example.model.ReportReason
import com.example.ui.theme.AppThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VibeStream", appName)
    }

    @Test
    fun `verify 75 percent creator split calculation`() {
        val rocketGift = com.example.model.GiftCatalog.allGifts.first { it.id == "gift_rocket" }
        val split = SupabaseEdgeFunctions.calculateSplit(rocketGift)

        // $10.00 total: $7.50 to Creator (75%), $2.50 to Platform (25%)
        assertEquals(7.50, split.creatorUsd, 0.01)
        assertEquals(2.50, split.platformUsd, 0.01)
        assertEquals(75, split.creatorPercent)
    }

    @Test
    fun `verify report categories exist for app store compliance`() {
        val reasons = ReportReason.values().map { it.displayName }
        assertTrue(reasons.any { it.contains("inapropiado", ignoreCase = true) })
        assertTrue(reasons.any { it.contains("spam", ignoreCase = true) })
        assertTrue(reasons.any { it.contains("acoso", ignoreCase = true) })
        assertTrue(reasons.any { it.contains("menor", ignoreCase = true) })
    }

    @Test
    fun `verify theme modes are available`() {
        val modes = AppThemeMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(AppThemeMode.DARK))
        assertTrue(modes.contains(AppThemeMode.LIGHT))
        assertTrue(modes.contains(AppThemeMode.SYSTEM))
    }
}
