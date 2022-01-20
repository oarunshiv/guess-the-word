package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse
import io.grpc.ManagedChannelBuilder

suspend fun main(args: Array<String>) {

    val port = System.getenv("PORT")?.toInt() ?: 50051
    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    // Use your custom WordGuesserHere.
    val wordGuesser = SampleWordGuesser("five_letter_words.txt")
    val client = GuessTheWordClient(channel)
    client.authenticate()
    var colors: Array<GuessResponse.Color>? = emptyArray()
    while (colors != null) {
        val wordToGuess = wordGuesser.nextBestGuess()
        colors = client.guess(wordToGuess)?.toTypedArray()
        if (colors == null || colors.count { it == GuessResponse.Color.GREEN } == 5) {
            GuessTheWordClient.logger.info { "Found the word: $wordToGuess!!" }
            GuessTheWordClient.logger.info { "Number of guesses: ${wordGuesser.numberOfGuesses() + 1} " }
            colors = null
        } else {
            wordGuesser.updateGuessResponse(wordToGuess, colors)
        }
    }
}
