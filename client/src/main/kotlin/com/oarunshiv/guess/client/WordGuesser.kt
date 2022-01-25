package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse

/**
 * Interface that is used to generate the guess word and update state based on results from the server.
 */
interface WordGuesser {
    /**
     * Provide next word to guess.
     */
    fun nextBestGuess(): String

    /**
     * Updates any state based on the response from the server.
     * @param guessedWord The word guessed by the client.
     * @param result The result list obtained from the server which indicates the correctness of the
     * letters in the corresponding index of the guessedWord.
     */
    fun updateGuessResponse(guessedWord: String, result: List<GuessResponse.Color>)
}
