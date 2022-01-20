package com.oarunshiv.guess
import com.oarunshiv.guess.GuessResponse.Color.BLACK
import com.oarunshiv.guess.GuessResponse.Color.GREEN
import com.oarunshiv.guess.GuessResponse.Color.YELLOW

class GuessEvaluator(private val dictionary: Dictionary) {
    fun guess(actualWord: String, guessedWord: String): GuessResponse {
        validateWord(actualWord, guessedWord).let { if (it != null) return it }
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
        return GuessResponse.newBuilder()
            .setStatus(GuessResponse.Status.VALID_REQUEST)
            .setGuessedWord(guessedWord)
            .addAllColors(answer)
            .build()
    }

    private fun validateWord(actualWord: String, guessedWord: String): GuessResponse? {
        if (actualWord.length != guessedWord.length) {
            return GuessResponse.newBuilder()
                .setStatus(GuessResponse.Status.INPUT_WORD_SIZE_MISMATCH)
                .setExceptionMessage(guessedWord + SIZE_MISMATCH_MESSAGE)
                .setGuessedWord(guessedWord)
                .build()
        }
        if (!dictionary.isValidWord(guessedWord)) {
            return GuessResponse.newBuilder()
                .setStatus(GuessResponse.Status.NON_DICTIONARY_WORD)
                .setExceptionMessage(guessedWord + NON_DICTIONARY_WORD_MESSGAGE)
                .setGuessedWord(guessedWord)
                .build()
        }
        return null
    }

    companion object {
        internal const val SIZE_MISMATCH_MESSAGE = "'s size is incorrect."
        internal const val NON_DICTIONARY_WORD_MESSGAGE = " is not a valid dictionary word."
    }
}
