package com.oarunshiv.guess

import com.oarunshiv.guess.GuessResponse.Color.BLACK
import com.oarunshiv.guess.GuessResponse.Color.GREEN
import com.oarunshiv.guess.GuessResponse.Color.YELLOW

class GuessEvaluator(private val dictionary: Dictionary) {
    fun guess(actualWord: String, guessedWord: String): GuessResponse {
        validateWord(actualWord, guessedWord).let { if (it != null) { return it } }
        val answer = mutableListOf(BLACK, BLACK, BLACK, BLACK, BLACK)
        val mappedWord = mutableMapOf<Char, MutableSet<Int>>()
        actualWord.forEachIndexed { i, c ->
            mappedWord.compute(c) { _, indices -> indices?.apply { add(i) } ?: mutableSetOf(i) }
        }
        val evaluatedPositions = mutableSetOf<Int>()
        val evaluatedChars = mutableSetOf<Char>()
        guessedWord.forEachIndexed { i, c ->
            if (i in mappedWord.getOrDefault(c, emptySet())) {
                answer[i] = GREEN
                evaluatedPositions.add(i)
                mappedWord[c]?.remove(i)
            }
        }
        guessedWord.forEachIndexed { i, c ->
            if (i !in evaluatedPositions && c !in evaluatedChars && !mappedWord[c].isNullOrEmpty()) {
                answer[i] = YELLOW
                evaluatedChars.add(c)
            }
        }
        return guessResponse {
            status = GuessResponse.Status.VALID_REQUEST
            this.guessedWord = guessedWord
            colors.addAll(answer)
        }
    }

    private fun validateWord(actual: String, guessed: String): GuessResponse? {
        if (actual.length != guessed.length) {
            return guessResponse {
                status = GuessResponse.Status.INPUT_WORD_SIZE_MISMATCH
                exceptionMessage = guessed + SIZE_MISMATCH_MESSAGE
                guessedWord = guessed
            }
        }
        if (!dictionary.isValidWord(guessed)) {
            return guessResponse {
                status = GuessResponse.Status.NON_DICTIONARY_WORD
                exceptionMessage = guessed + NON_DICTIONARY_WORD_MESSAGE
                guessedWord = guessed
            }
        }
        return null
    }

    companion object {
        internal const val SIZE_MISMATCH_MESSAGE = "'s size is incorrect."
        internal const val NON_DICTIONARY_WORD_MESSAGE = " is not a valid dictionary word."
    }
}
