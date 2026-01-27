package com.capivv.sdk.l10n

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for localization.
 */
class LocalizationTest {

    @Test
    fun `default language is English`() {
        assertEquals("Restore Purchases", CapivvL10n.get("restore_purchases", "en"))
        assertEquals("Continue", CapivvL10n.get("continue", "en"))
        assertEquals("Subscribe Now", CapivvL10n.get("subscribe_now", "en"))
    }

    @Test
    fun `Spanish translations are available`() {
        assertEquals("Restaurar Compras", CapivvL10n.get("restore_purchases", "es"))
        assertEquals("Continuar", CapivvL10n.get("continue", "es"))
        assertEquals("Suscribirse Ahora", CapivvL10n.get("subscribe_now", "es"))
    }

    @Test
    fun `French translations are available`() {
        assertEquals("Restaurer les Achats", CapivvL10n.get("restore_purchases", "fr"))
        assertEquals("Continuer", CapivvL10n.get("continue", "fr"))
        assertEquals("S'abonner Maintenant", CapivvL10n.get("subscribe_now", "fr"))
    }

    @Test
    fun `German translations are available`() {
        assertEquals("Käufe Wiederherstellen", CapivvL10n.get("restore_purchases", "de"))
        assertEquals("Weiter", CapivvL10n.get("continue", "de"))
        assertEquals("Jetzt Abonnieren", CapivvL10n.get("subscribe_now", "de"))
    }

    @Test
    fun `Japanese translations are available`() {
        assertEquals("購入を復元", CapivvL10n.get("restore_purchases", "ja"))
        assertEquals("続ける", CapivvL10n.get("continue", "ja"))
        assertEquals("今すぐ購読", CapivvL10n.get("subscribe_now", "ja"))
    }

    @Test
    fun `Chinese translations are available`() {
        assertEquals("恢复购买", CapivvL10n.get("restore_purchases", "zh"))
        assertEquals("继续", CapivvL10n.get("continue", "zh"))
        assertEquals("立即订阅", CapivvL10n.get("subscribe_now", "zh"))
    }

    @Test
    fun `unsupported language falls back to English`() {
        assertEquals("Restore Purchases", CapivvL10n.get("restore_purchases", "xyz"))
    }

    @Test
    fun `format function replaces placeholders`() {
        val formatted = CapivvL10n.format("trial_period", "en", "7 days")
        assertTrue(formatted.contains("7 days"))
    }

    @Test
    fun `supported locales includes all 8 languages`() {
        val locales = CapivvL10n.supportedLocales
        assertEquals(8, locales.size)
        assertTrue(locales.contains("en"))
        assertTrue(locales.contains("es"))
        assertTrue(locales.contains("fr"))
        assertTrue(locales.contains("de"))
        assertTrue(locales.contains("ja"))
        assertTrue(locales.contains("zh"))
        assertTrue(locales.contains("pt"))
        assertTrue(locales.contains("it"))
    }
}
