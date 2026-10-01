package com.paisen.kurolauncher.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JapaneseSearchHelperTest {

    @Test
    fun testKanaToRomajiConversion() {
        val mercari = JapaneseSearchHelper.kanaToRomaji("メルカリ")
        assertTrue("Expected 'merukari' in $mercari", mercari.contains("merukari"))

        val camera = JapaneseSearchHelper.kanaToRomaji("カメラ")
        assertTrue("Expected 'kamera' in $camera", camera.contains("kamera"))

        val map = JapaneseSearchHelper.kanaToRomaji("マップ")
        assertTrue("Expected 'mappu' in $map", map.contains("mappu"))

        val ramen = JapaneseSearchHelper.kanaToRomaji("ラーメン")
        assertTrue("Expected 'ramen' or 'raamen' in $ramen", ramen.any { it == "ramen" || it == "raamen" })

        val densha = JapaneseSearchHelper.kanaToRomaji("でんしゃ")
        assertTrue("Expected 'densha' in $densha", densha.contains("densha"))
    }

    @Test
    fun testRomajiToKanaConversion() {
        val (hira1, kata1) = JapaneseSearchHelper.romajiToKana("merukari")
        assertEquals("めるかり", hira1)
        assertEquals("メルカリ", kata1)

        val (hira2, kata2) = JapaneseSearchHelper.romajiToKana("kamera")
        assertEquals("かめら", hira2)
        assertEquals("カメラ", kata2)

        val (hira3, kata3) = JapaneseSearchHelper.romajiToKana("tokei")
        assertEquals("とけい", hira3)
        assertEquals("トケイ", kata3)

        val (hira4, kata4) = JapaneseSearchHelper.romajiToKana("mappu")
        assertEquals("まっぷ", hira4)
        assertEquals("マップ", kata4)

        val (hira5, kata5) = JapaneseSearchHelper.romajiToKana("norikae")
        assertEquals("のりかえ", hira5)
        assertEquals("ノリカエ", kata5)
    }

    @Test
    fun testGenerateSearchTermsForKanaApp() {
        val terms = JapaneseSearchHelper.generateSearchTerms("メルカリ", "com.kouzoh.mercari")
        assertTrue("Expected 'merukari' in search terms", terms.contains("merukari"))
        assertTrue("Expected 'mercari' from package in search terms", terms.contains("mercari"))
    }

    @Test
    fun testGenerateSearchTermsForKanjiApp() {
        val terms = JapaneseSearchHelper.generateSearchTerms("設定", "com.android.settings")
        assertTrue("Expected 'settei' in search terms", terms.contains("settei"))
        assertTrue("Expected 'settings' in search terms", terms.contains("settings"))

        val clockTerms = JapaneseSearchHelper.generateSearchTerms("時計", "com.google.android.deskclock")
        assertTrue("Expected 'tokei' in clock terms", clockTerms.contains("tokei"))
        assertTrue("Expected 'clock' in clock terms", clockTerms.contains("clock"))
        assertTrue("Expected 'deskclock' in clock terms", clockTerms.contains("deskclock"))
    }

    @Test
    fun testJapaneseMatchScoreRomajiQuery() {
        val appLabel = "メルカリ"
        val terms = JapaneseSearchHelper.generateSearchTerms(appLabel, "com.kouzoh.mercari")

        // Search full romaji
        val scoreFull = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "merukari")
        assertNotNull("Score should not be null for 'merukari'", scoreFull)
        assertEquals(5, scoreFull)

        // Search partial prefix romaji
        val scorePrefix = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "meru")
        assertNotNull("Score should not be null for 'meru'", scorePrefix)
        assertTrue("Score should indicate high match", scorePrefix!! <= 15)

        // Search package english name
        val scoreEn = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "mercari")
        assertNotNull("Score should not be null for 'mercari'", scoreEn)
    }

    @Test
    fun testJapaneseMatchScoreKanjiAppWithEnglishAndRomaji() {
        val appLabel = "設定"
        val terms = JapaneseSearchHelper.generateSearchTerms(appLabel, "com.android.settings")

        // English search
        val scoreEn = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "settings")
        assertNotNull("Score should not be null for 'settings'", scoreEn)
        assertEquals(5, scoreEn)

        // Romaji search
        val scoreRomaji = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "settei")
        assertNotNull("Score should not be null for 'settei'", scoreRomaji)
        assertEquals(5, scoreRomaji)

        // Partial English search
        val scoreEnPart = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "sett")
        assertNotNull("Score should not be null for 'sett'", scoreEnPart)
        assertTrue(scoreEnPart!! <= 15)

        // Single letter Romaji search
        val scoreSingle = JapaneseSearchHelper.getJapaneseMatchScore(appLabel, terms, "s")
        assertNotNull("Score should not be null for 's'", scoreSingle)
        assertTrue(scoreSingle!! <= 15)
    }

    @Test
    fun testCommonSystemAppsAndTransit() {
        // Camera
        val cameraTerms = JapaneseSearchHelper.generateSearchTerms("カメラ", "com.google.android.GoogleCamera")
        val camScore = JapaneseSearchHelper.getJapaneseMatchScore("カメラ", cameraTerms, "camera")
        assertNotNull("Score should not be null for 'camera'", camScore)
        val kameScore = JapaneseSearchHelper.getJapaneseMatchScore("カメラ", cameraTerms, "kame")
        assertNotNull("Score should not be null for 'kame'", kameScore)

        // Transit / Norikae
        val transitTerms = JapaneseSearchHelper.generateSearchTerms("乗換案内", "jp.co.jorudan.nrkj")
        val norikaeScore = JapaneseSearchHelper.getJapaneseMatchScore("乗換案内", transitTerms, "norikae")
        assertNotNull("Score should not be null for 'norikae'", norikaeScore)
        val transitScore = JapaneseSearchHelper.getJapaneseMatchScore("乗換案内", transitTerms, "transit")
        assertNotNull("Score should not be null for 'transit'", transitScore)

        // Calculator
        val calcTerms = JapaneseSearchHelper.generateSearchTerms("電卓", "com.google.android.calculator")
        val calcScore = JapaneseSearchHelper.getJapaneseMatchScore("電卓", calcTerms, "calc")
        assertNotNull("Score should not be null for 'calc'", calcScore)
        val dentakuScore = JapaneseSearchHelper.getJapaneseMatchScore("電卓", calcTerms, "dentaku")
        assertNotNull("Score should not be null for 'dentaku'", dentakuScore)

        // Weather
        val weatherTerms = JapaneseSearchHelper.generateSearchTerms("天気", "com.example.weather")
        val tenkiScore = JapaneseSearchHelper.getJapaneseMatchScore("天気", weatherTerms, "tenki")
        assertNotNull("Score should not be null for 'tenki'", tenkiScore)
        val weatherScore = JapaneseSearchHelper.getJapaneseMatchScore("天気", weatherTerms, "weather")
        assertNotNull("Score should not be null for 'weather'", weatherScore)
    }
}
