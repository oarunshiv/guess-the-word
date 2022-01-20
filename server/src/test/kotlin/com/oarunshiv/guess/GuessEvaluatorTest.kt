package com.oarunshiv.guess

import com.oarunshiv.guess.GuessEvaluator.Companion.NON_DICTIONARY_WORD_MESSAGE
import com.oarunshiv.guess.GuessEvaluator.Companion.SIZE_MISMATCH_MESSAGE
import com.oarunshiv.guess.GuessResponse.Color
import com.oarunshiv.guess.GuessResponse.Color.BLACK
import com.oarunshiv.guess.GuessResponse.Color.GREEN
import com.oarunshiv.guess.GuessResponse.Color.YELLOW
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuessEvaluatorTest : KoinTest {
    private val guessEvaluator: GuessEvaluator by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create { modules(wordGuesserModule("five_letter_words_sample.txt")) }

    private fun validResponseWith(guessedWord: String, vararg colors: Color) = GuessResponse
        .newBuilder()
        .setStatus(GuessResponse.Status.VALID_REQUEST)
        .addAllColors(colors.asList())
        .setGuessedWord(guessedWord)
        .build()

    private fun guessInputsProvider(): Array<Arguments> {
        val letterWithIncorrectSize = "four"
        val invalidWord = "blahh"
        val invalidWordResponse = GuessResponse.newBuilder()
            .setStatus(GuessResponse.Status.NON_DICTIONARY_WORD)
            .setExceptionMessage(invalidWord + NON_DICTIONARY_WORD_MESSAGE)
            .setGuessedWord(invalidWord)
            .build()
        val wordLengthMismatchResponse = GuessResponse.newBuilder()
            .setStatus(GuessResponse.Status.INPUT_WORD_SIZE_MISMATCH)
            .setExceptionMessage(letterWithIncorrectSize + SIZE_MISMATCH_MESSAGE)
            .setGuessedWord(letterWithIncorrectSize)
            .build()
        val aback = "aback"

        val tiger = "tiger"
        val score = "score"
        val pinch = "pinch"
        val check = "check"
        val blank = "blank"
        val khaki = "khaki"
        val koala = "koala"
        val dared = "dared"
        val eared = "eared"
        return arrayOf(
            arguments(aback, letterWithIncorrectSize, wordLengthMismatchResponse),
            arguments(aback, invalidWord, invalidWordResponse),
            arguments(aback, tiger, validResponseWith(tiger, BLACK, BLACK, BLACK, BLACK, BLACK)),
            arguments(aback, score, validResponseWith(score, BLACK, YELLOW, BLACK, BLACK, BLACK)),
            arguments(aback, pinch, validResponseWith(pinch, BLACK, BLACK, BLACK, GREEN, BLACK)),
            arguments(aback, check, validResponseWith(check, BLACK, BLACK, BLACK, GREEN, GREEN)),
            arguments(aback, blank, validResponseWith(blank, YELLOW, BLACK, GREEN, BLACK, GREEN)),
            arguments(aback, khaki, validResponseWith(khaki, YELLOW, BLACK, GREEN, BLACK, BLACK)),
            arguments(aback, koala, validResponseWith(koala, YELLOW, BLACK, GREEN, BLACK, YELLOW)),
            arguments(eared, dared, validResponseWith(dared, BLACK, GREEN, GREEN, GREEN, GREEN)),
        )
    }

    @ParameterizedTest
    @MethodSource("guessInputsProvider")
    fun guess(
        actualWord: String,
        guessedWord: String,
        expectedResponse: GuessResponse
    ) {
        val response = guessEvaluator.guess(actualWord, guessedWord)
        assertEquals(expectedResponse, response)
    }
}
