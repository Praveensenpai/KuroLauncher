package com.paisen.kurolauncher.helper

import java.util.Locale

/**
 * Provides bidirectional Romaji <-> Kana transliteration, Kanji app reading lookup,
 * English-Japanese alias matching, and package keyword extraction.
 */
object JapaneseSearchHelper {

    private val GENERIC_PACKAGE_PARTS = setOf(
        "com", "org", "net", "jp", "co", "ne", "android", "google", "app", "apps",
        "mobile", "client", "service", "main", "ui", "internal", "release", "debug"
    )

    fun isHiragana(c: Char): Boolean = c in '\u3041'..'\u3096'

    fun isKatakana(c: Char): Boolean = c in '\u30A1'..'\u30FA' || c == 'ー'

    fun isKana(c: Char): Boolean = isHiragana(c) || isKatakana(c)

    fun isKanji(c: Char): Boolean = c in '\u4E00'..'\u9FAF' || c in '\u3400'..'\u4DBF'

    fun containsJapanese(text: String): Boolean = text.any { isKana(it) || isKanji(it) }

    fun hiraganaToKatakana(text: String): String {
        val sb = StringBuilder(text.length)
        for (ch in text) {
            if (ch in '\u3041'..'\u3096') {
                sb.append((ch.code + 0x60).toChar())
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun katakanaToHiragana(text: String): String {
        val sb = StringBuilder(text.length)
        for (ch in text) {
            if (ch in '\u30A1'..'\u30F6') {
                sb.append((ch.code - 0x60).toChar())
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Katakana or Hiragana text into Romaji representations.
     * Generates both standard and long-vowel variations where applicable.
     */
    fun kanaToRomaji(input: String): List<String> {
        val text = hiraganaToKatakana(input)
        val primary = StringBuilder()
        val alternate = StringBuilder()
        var hasVowelExtension = false

        var i = 0
        while (i < text.length) {
            val c = text[i]

            // 1. Check for small tsu (sokuon: ッ or っ)
            if (c == 'ッ' || c == 'っ') {
                val nextConsonant = getNextConsonant(text, i + 1)
                if (nextConsonant != null) {
                    primary.append(nextConsonant)
                    alternate.append(nextConsonant)
                }
                i++
                continue
            }

            // 2. Check for long vowel marker (ー)
            if (c == 'ー') {
                hasVowelExtension = true
                val prevVowel = getLastVowel(primary)
                if (prevVowel != null) {
                    alternate.append(prevVowel)
                }
                i++
                continue
            }

            // 3. Check for 2-character compound (digraph / yōon)
            if (i + 1 < text.length) {
                val twoChar = text.substring(i, i + 2)
                val compound = JapaneseKanaData.KANA_COMPOUNDS[twoChar]
                if (compound != null) {
                    primary.append(compound)
                    alternate.append(compound)
                    i += 2
                    continue
                }
            }

            // 4. Single Kana character
            val single = JapaneseKanaData.SINGLE_KANA[c]
            if (single != null) {
                primary.append(single)
                alternate.append(single)
            } else {
                primary.append(c)
                alternate.append(c)
            }
            i++
        }

        val result = mutableListOf(primary.toString().lowercase(Locale.ROOT))
        if (hasVowelExtension && alternate.isNotEmpty() && alternate.toString() != primary.toString()) {
            result.add(alternate.toString().lowercase(Locale.ROOT))
        }
        return result
    }

    private fun getNextConsonant(text: String, nextIndex: Int): Char? {
        if (nextIndex >= text.length) return null
        if (nextIndex + 1 < text.length) {
            val two = text.substring(nextIndex, nextIndex + 2)
            val romaji = JapaneseKanaData.KANA_COMPOUNDS[two]
            if (!romaji.isNullOrEmpty()) return romaji[0]
        }
        val singleRomaji = JapaneseKanaData.SINGLE_KANA[text[nextIndex]]
        return if (!singleRomaji.isNullOrEmpty()) singleRomaji[0] else null
    }

    private fun getLastVowel(sb: StringBuilder): Char? {
        for (idx in sb.length - 1 downTo 0) {
            val ch = sb[idx]
            if (ch in "aeiou") return ch
        }
        return null
    }

    /**
     * Converts a Romaji query (Latin characters) into both Katakana and Hiragana.
     */
    fun romajiToKana(query: String): Pair<String, String> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return Pair("", "")

        val kata = StringBuilder()
        var i = 0

        while (i < q.length) {
            // Check for double consonant (small tsu: ッ)
            if (i + 1 < q.length && q[i] == q[i + 1] && q[i] !in "aeioun") {
                kata.append('ッ')
                i++
                continue
            }

            // Greedy match against ROMAJI_TO_KANA
            var matched = false
            for ((romajiToken, kanaToken) in JapaneseKanaData.ROMAJI_TO_KANA) {
                if (q.startsWith(romajiToken, i)) {
                    kata.append(kanaToken)
                    i += romajiToken.length
                    matched = true
                    break
                }
            }

            if (!matched) {
                // If 'n' followed by consonant or end of string, map to 'ン'
                if (q[i] == 'n' && (i + 1 == q.length || q[i + 1] !in "aeiouy")) {
                    kata.append('ン')
                } else {
                    kata.append(q[i])
                }
                i++
            }
        }

        val katakanaResult = kata.toString()
        val hiraganaResult = katakanaToHiragana(katakanaResult)
        return Pair(hiraganaResult, katakanaResult)
    }

    /**
     * Splits package name by dots and camelCase/numbers to extract meaningful tokens.
     */
    fun extractPackageKeywords(packageName: String): List<String> {
        if (packageName.isBlank()) return emptyList()
        val segments = packageName.split('.')
        val tokens = mutableSetOf<String>()

        for (segment in segments) {
            val lower = segment.lowercase(Locale.ROOT)
            if (lower !in GENERIC_PACKAGE_PARTS && lower.length >= 3) {
                tokens.add(lower)
            }
            val camelParts = segment.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
                .replace(Regex("(?<=[A-Za-z])(?=[0-9])"), " ")
                .split(Regex("[^a-zA-Z0-9]+"))
            for (part in camelParts) {
                val p = part.lowercase(Locale.ROOT)
                if (p !in GENERIC_PACKAGE_PARTS && p.length >= 3) {
                    tokens.add(p)
                }
            }
        }
        return tokens.toList()
    }

    /**
     * Generates a precomputed set of search terms for an application.
     */
    fun generateSearchTerms(label: String, packageName: String): List<String> {
        val terms = mutableSetOf<String>()
        val trimmedLabel = label.trim()

        // 1. Package tokens
        terms.addAll(extractPackageKeywords(packageName))

        // 2. If app label contains Japanese characters:
        if (containsJapanese(trimmedLabel)) {
            // Katakana <-> Hiragana conversions
            terms.add(hiraganaToKatakana(trimmedLabel).lowercase(Locale.ROOT))
            terms.add(katakanaToHiragana(trimmedLabel).lowercase(Locale.ROOT))

            // Kana -> Romaji
            terms.addAll(kanaToRomaji(trimmedLabel))

            // Check Kanji compound readings
            for ((kanjiKey, readings) in JapaneseKanaData.KANJI_APP_READINGS) {
                if (trimmedLabel.contains(kanjiKey)) {
                    for (reading in readings) {
                        terms.add(reading.lowercase(Locale.ROOT))
                        if (containsJapanese(reading)) {
                            terms.addAll(kanaToRomaji(reading))
                        }
                    }
                }
            }

            // Check Bilingual English aliases
            for ((jpKey, enAliases) in JapaneseKanaData.BILINGUAL_APP_ALIASES) {
                if (trimmedLabel.contains(jpKey)) {
                    for (alias in enAliases) {
                        terms.add(alias.lowercase(Locale.ROOT))
                    }
                }
            }
        }

        // Remove empty or identical to label
        terms.remove("")
        terms.remove(trimmedLabel.lowercase(Locale.ROOT))
        return terms.toList()
    }

    /**
     * Computes matching score against secondary search terms and phonetic query transliteration.
     */
    fun getJapaneseMatchScore(appLabel: String, searchTerms: List<String>, query: String): Int? {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return null

        // 1. Direct match against precomputed search terms (Romaji, English aliases, package keywords)
        for (term in searchTerms) {
            if (term == q) return 5
            if (term.startsWith(q)) return 15
        }

        // 2. Word boundary or sub-token match in search terms
        for (term in searchTerms) {
            val words = term.split(Regex("[-_+,.`'\\s\\p{Z}]+"))
            if (words.any { it.startsWith(q) }) return 25
            if (term.contains(q)) return 45
        }

        // 3. Phonetic query conversion: Latin Romaji query -> Kana match against appLabel
        val (hiraQuery, kataQuery) = romajiToKana(q)
        if (hiraQuery.isNotEmpty() && hiraQuery != q) {
            val hiraLabel = katakanaToHiragana(appLabel)
            val kataLabel = hiraganaToKatakana(appLabel)

            if (kataLabel.startsWith(kataQuery) || hiraLabel.startsWith(hiraQuery)) {
                return 12
            }
            if (kataLabel.contains(kataQuery) || hiraLabel.contains(hiraQuery)) {
                return 42
            }
        }

        return null
    }
}
