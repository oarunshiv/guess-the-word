package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse
import mu.KotlinLogging

/**
 * This utility executor can be used to run [WordGuesser] implementation to test with custom words.
 * The program prints the guess word and then waits for the response to be readfrom the
 * Standard input. Enter the result with either 'b','y' or 'g' corresponding to each of the guess
 * letter without any punctuations.
 */
fun main() {
    val logger = KotlinLogging.logger {}
    while (true) {
        val wordGuesser = SampleWordGuesser("exhaustive_five_letter_words.txt")
        while (true) {
            val wordToGuess = wordGuesser.nextBestGuess()
            println(wordToGuess)
            print("Enter the response: ")
            val input = readLine()!!
            if (input == "exit") break
            val colors = input.map {
                when (it) {
                    'b' -> GuessResponse.Color.BLACK
                    'y' -> GuessResponse.Color.YELLOW
                    'g' -> GuessResponse.Color.GREEN
                    else -> error("Invalid input")
                }
            }
            if (colors.count { it == GuessResponse.Color.GREEN } == 5) {
                logger.info { "Found the word: $wordToGuess!!" }
                logger.info { "Number of guesses: ${wordGuesser.numberOfGuesses()} " }
                return
            } else {
                wordGuesser.updateGuessResponse(wordToGuess, colors)
            }
        }
    }
}
