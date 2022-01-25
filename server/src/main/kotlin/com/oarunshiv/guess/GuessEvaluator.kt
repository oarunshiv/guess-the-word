package com.oarunshiv.guess

import com.oarunshiv.guess.GuessResponse.Color
import com.oarunshiv.guess.GuessResponse.Color.BLACK
import com.oarunshiv.guess.GuessResponse.Color.GREEN
import com.oarunshiv.guess.GuessResponse.Color.YELLOW

/**
 * Evaluates guesses made by clients.
 */
class GuessEvaluator(private val dictionary: Dictionary) {
    /**
     * Evaluates the similarity between [actualWord] and [guessedWord].
     * @param actualWord The word against which to evaluate the response.
     * @param guessedWord The guessedWord to evaluate the similarity against the actual word.
     * @return [GuessResponse]. If the word is a valid word, then a list with 5 [Color]s
     * is added to the object.
     */
    fun evaluate(actualWord: String, request: GuessRequest): GuessResponse {
        validateWord(actualWord, request).let { if (it != null) { return it } }
        val guessedWord = request.guess
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
            sessionId = request.sessionId
        }
    }

    private fun validateWord(actual: String, request: GuessRequest): GuessResponse? {
        val guessed = request.guess
        if (actual.length != guessed.length) {
            return guessResponse {
                status = GuessResponse.Status.INPUT_WORD_SIZE_MISMATCH
                exceptionMessage = guessed + SIZE_MISMATCH_MESSAGE
                guessedWord = guessed
                sessionId = request.sessionId
            }
        }
        if (!dictionary.isValidWord(guessed)) {
            return guessResponse {
                status = GuessResponse.Status.NON_DICTIONARY_WORD
                exceptionMessage = guessed + NON_DICTIONARY_WORD_MESSAGE
                guessedWord = guessed
                sessionId = request.sessionId
            }
        }
        return null
    }

    companion object {
        internal const val SIZE_MISMATCH_MESSAGE = "'s size is incorrect."
        internal const val NON_DICTIONARY_WORD_MESSAGE = " is not a valid dictionary word."
    }
}
