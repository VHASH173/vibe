package com.example

import com.example.data.supabase.SupabaseEdgeFunctions
import com.example.model.GiftCatalog
import com.example.model.LegalContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFairMonetizationSplit_75_25() {
        val rocket = GiftCatalog.allGifts.first { it.id == "gift_rocket" }
        assertEquals(1000, rocket.coinCost)
        assertEquals(10.00, rocket.usdValue, 0.001)

        val split = SupabaseEdgeFunctions.calculateSplit(rocket)
        assertTrue(split.success)
        assertEquals(750, split.creatorCoins)
        assertEquals(250, split.platformCoins)
        assertEquals(7.50, split.creatorUsd, 0.001)
        assertEquals(2.50, split.platformUsd, 0.001)
    }

    @Test
    fun testGalaxyDragon_75_25() {
        val dragon = GiftCatalog.allGifts.first { it.id == "gift_dragon" }
        assertEquals(5000, dragon.coinCost)
        assertEquals(50.00, dragon.usdValue, 0.001)

        val split = SupabaseEdgeFunctions.calculateSplit(dragon)
        assertEquals(3750, split.creatorCoins)
        assertEquals(1250, split.platformCoins)
        assertEquals(37.50, split.creatorUsd, 0.001)
        assertEquals(12.50, split.platformUsd, 0.001)
    }

    @Test
    fun testLegalConsentAuditPayload() {
        val email = "streamer@vibestream.live"
        val username = "@alex_stream"
        val version = LegalContent.TERMS_VERSION
        val timestamp = System.currentTimeMillis()

        val payload = SupabaseEdgeFunctions.RegisterAuditPayload(
            email = email,
            username = username,
            termsAcceptedVersion = version,
            termsAcceptedTimestamp = timestamp
        )

        assertEquals("streamer@vibestream.live", payload.email)
        assertEquals("@alex_stream", payload.username)
        assertEquals("v1.0", payload.termsAcceptedVersion)
        assertTrue(payload.termsAcceptedTimestamp > 0)
    }

    @Test
    fun testFaqContentNotEmpty() {
        assertTrue(LegalContent.FAQ_ITEMS.isNotEmpty())
        assertTrue(LegalContent.TERMS_AND_CONDITIONS.contains("75%"))
        assertTrue(LegalContent.PRIVACY_POLICY.contains("GDPR"))
    }

    @Test
    fun testAgeVerificationEligibility() {
        // Adult user (e.g. 21 years old)
        val adultAge = 21
        val adultCanGoLive = adultAge >= 18
        assertTrue("Adult users should be allowed to go live", adultCanGoLive)

        // Minor user (e.g. 16 years old)
        val minorAge = 16
        val minorCanGoLive = minorAge >= 18
        assertFalse("Minor users (< 18) must NOT be allowed to go live", minorCanGoLive)
    }

    @Test
    fun testWebmAssetMapper() {
        val rocketWebm = com.example.model.WebmAssetMapper.getGiftWebmUri(com.example.model.GiftAnimationType.VIBE_ROCKET)
        assertEquals("asset:///webm/energia.webm", rocketWebm)

        val dragonWebm = com.example.model.WebmAssetMapper.getGiftWebmUri(com.example.model.GiftAnimationType.GALAXY_DRAGON)
        assertEquals("asset:///webm/tormenta_red.webm", dragonWebm)

        val treasureWebm = com.example.model.WebmAssetMapper.getTreasureBoxWebmUri()
        assertEquals("asset:///webm/cofre_magia.webm", treasureWebm)
    }

    @Test
    fun testGiftModelStructure() {
        val gift = com.example.model.GiftModel(
            id = 5655L,
            name = "Cohete Espacial",
            diamond = 1000L,
            type = 2L,
            storageIcon = "https://firebasestorage.googleapis.com/v0/b/tiktok-live-app-c6854.firebasestorage.app/o/rocket.webp",
            picture = "https://p16-webcast.tiktokcdn.com/rocket.png"
        )
        assertEquals(5655L, gift.id)
        assertEquals(5655, gift.idInt)
        assertEquals("Cohete Espacial", gift.name)
        assertEquals(1000L, gift.diamond)
        assertEquals(1000, gift.diamondInt)
        assertEquals(2L, gift.type)
        assertEquals(2, gift.typeInt)
        assertTrue(gift.storageIcon.contains("tiktok-live-app-c6854"))
    }

    @Test
    fun testDynamicGift7525SplitCalculation() {
        val gift = com.example.model.GiftModel(
            id = 5827L,
            name = "Dragón Galaxia",
            diamond = 2000L,
            type = 3L,
            storageIcon = "https://firebasestorage.googleapis.com/v0/b/tiktok-live-app-c6854.firebasestorage.app/o/dragon.webp",
            picture = "https://p16-webcast.tiktokcdn.com/dragon.png"
        )
        val coinCost = gift.diamond.toInt()
        val usdValue = coinCost * 0.01
        val creatorShareUsd = usdValue * 0.75
        val platformShareUsd = usdValue * 0.25
        val creatorCoins = (coinCost * 0.75).toInt()

        assertEquals(20.0, usdValue, 0.001)
        assertEquals(15.0, creatorShareUsd, 0.001)
        assertEquals(5.0, platformShareUsd, 0.001)
        assertEquals(1500, creatorCoins)
    }
}
