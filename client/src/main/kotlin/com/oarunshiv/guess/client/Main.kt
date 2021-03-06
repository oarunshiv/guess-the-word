package com.oarunshiv.guess.client

import com.oarunshiv.guess.GuessResponse
import io.grpc.ManagedChannelBuilder
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

/**
 * Main function that is used runs [GuessTheWordClient] to guess and find the guess-word set by the
 * server.
 */
suspend fun main(args: Array<String>) {
    val argParser = ArgParser("guess-the-word-client")
    val port by argParser.option(
        ArgType.Int,
        shortName = "p",
        description = "The port number where the server connections are accepted."
    ).default(50051)
    argParser.parse(args)

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    // Use your custom WordGuesserHere.
    val wordGuesser = SampleWordGuesser("exhaustive_five_letter_words.txt")
    val client = GuessTheWordClient(channel)
    var colors: List<GuessResponse.Color>? = emptyList()
    while (colors != null) {
        val wordToGuess = wordGuesser.nextBestGuess()
        val response = client.guess(wordToGuess)
        if (response.status == GuessResponse.Status.VALID_REQUEST) {
            colors = response.colorsList
        } else {
            GuessTheWordClient.logger.warn { "Server couldn't process '$wordToGuess' properly." }
            throw IllegalArgumentException("Invalid input passed. Response obtained: $response")
        }
        if (colors.count { it == GuessResponse.Color.GREEN } == 5) {
            GuessTheWordClient.logger.info { "Found the word: $wordToGuess!!" }
            GuessTheWordClient.logger.info { "Number of guesses: ${response.numberOfGuesses} " }
            colors = null
        } else {
            wordGuesser.updateGuessResponse(wordToGuess, colors)
        }
    }
}
