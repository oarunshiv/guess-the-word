package com.oarunshiv.guess.client

import com.oarunshiv.guess.AuthenticateResponse
import com.oarunshiv.guess.GuessResponse
import com.oarunshiv.guess.GuessTheWordGrpcKt
import com.oarunshiv.guess.authenticateRequest
import com.oarunshiv.guess.guessRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import mu.KotlinLogging
import java.io.Closeable
import java.util.concurrent.TimeUnit

class GuessTheWordClient(private val channel: ManagedChannel) : Closeable {
    private val stub = GuessTheWordGrpcKt.GuessTheWordCoroutineStub(channel)
    private lateinit var currentSessionId: String

    suspend fun authenticate() {
        val request = authenticateRequest {
            userId = "testUser"
            password = "testPassword"
        }
        val response = stub.authenticate(request)
        response.takeIf { it.status == AuthenticateResponse.Status.SUCCESS }
            ?.let {
                logger.info { "Got valid sessionId: ${it.sessionId}" }
                currentSessionId = it.sessionId
            }
            ?: throw IllegalStateException("Couldn't authenticate with the server $response")
    }

    suspend fun guess(wordToGuess: String): List<GuessResponse.Color>? {
        val request = guessRequest {
            sessionId = currentSessionId
            guess = wordToGuess
        }
        val response = stub.guess(request)
        logger.info { "Received: { ${response.colorsList} }" }
        return response.takeIf { it.status == GuessResponse.Status.VALID_REQUEST }?.colorsList
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}

suspend fun main(args: Array<String>) {

    val port = System.getenv("PORT")?.toInt() ?: 50051

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val wordGuesser = OarunshivWordGuesser("five_letter_words.txt")
    val client = GuessTheWordClient(channel)
    client.authenticate()
    var colors: Array<GuessResponse.Color>? = emptyArray()
    while (colors != null) {
        val wordToGuess = wordGuesser.nextBestGuess()
        colors = client.guess(wordToGuess)?.toTypedArray()
        if (colors == null || colors.count { it == GuessResponse.Color.GREEN } == 5) {
            GuessTheWordClient.logger.info { "Found the word: $wordToGuess!!" }
            GuessTheWordClient.logger.info { "Number of guesses: ${wordGuesser.guessedWords.size + 1} " }
            colors = null
        } else {
            wordGuesser.updateGuessResponse(wordToGuess, colors)
        }
    }
}
