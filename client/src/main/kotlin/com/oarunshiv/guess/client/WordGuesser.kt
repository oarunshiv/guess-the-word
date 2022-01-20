package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse

interface WordGuesser {
    fun nextBestGuess(): String
    fun updateGuessResponse(guessedWord: String, result: Array<GuessResponse.Color>)
    fun numberOfGuesses(): Int
}
