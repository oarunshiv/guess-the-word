package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse

fun main() {
    while (true) {
        val wordGuesser = OarunshivWordGuesser("five_letter_words.txt")
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
                GuessTheWordClient.logger.info { "Found the word: $wordToGuess!!" }
                GuessTheWordClient.logger.info { "Number of guesses: ${wordGuesser.guessedWords.size + 1} " }
                return
            } else {
                wordGuesser.updateGuessResponse(wordToGuess, colors.toTypedArray())
            }
        }
    }
}
