package com.oarunshiv.guess

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DictionaryTest : KoinTest {
    private val dictionary: Dictionary by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create { modules(wordGuesserModule("five_letter_words_sample.txt")) }

    @Test
    fun generateWord() {
        val words = (0..24).map { dictionary.generateWord() }.toSet()
        assert(words.size > 5)
    }

    @Test
    fun isValidWord() {
        repeat(10) { assertTrue { dictionary.isValidWord(dictionary.generateWord()) } }
        assertFalse { dictionary.isValidWord("blahh") }
    }
}
