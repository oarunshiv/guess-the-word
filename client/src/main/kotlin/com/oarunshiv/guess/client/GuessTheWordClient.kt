package com.oarunshiv.guess.client

import com.oarunshiv.guess.AuthenticateResponse
import com.oarunshiv.guess.GuessResponse
import com.oarunshiv.guess.GuessTheWordGrpcKt
import com.oarunshiv.guess.authenticateRequest
import com.oarunshiv.guess.guessRequest
import io.grpc.ManagedChannel
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * Client used to send request to the GuessTheWordServer.
 */
class GuessTheWordClient(private val channel: ManagedChannel) : Closeable {
    private val stub = GuessTheWordGrpcKt.GuessTheWordCoroutineStub(channel)
    private val currentSessionId: String by lazy { runBlocking { getAuthenticatedSessionId() } }

    /**
     * Makes a [GuessTheWordGrpcKt.GuessTheWordCoroutineImplBase.authenticate] request to the server
     * and returns sessionId if the request succeeds.
     */
    private suspend fun getAuthenticatedSessionId(): String {
        val request = authenticateRequest {
            userId = "testUser"
            password = "testPassword"
        }
        val response = stub.authenticate(request)
        if (response.status == AuthenticateResponse.Status.SUCCESS) {
            logger.info { "Got valid sessionId: ${response.sessionId}" }
            return response.sessionId
        } else {
            throw IllegalStateException("Couldn't authenticate with the server $response")
        }
    }

    /**
     * Sends a [com.oarunshiv.guess.GuessRequest] to the server with the provided guess word.
     * @param wordToGuess The guess word which is to be sent to the server.
     * @return List of colors. Each color represents the correctness of the letter in corresponding
     * index of the guessed word.
     */
    suspend fun guess(wordToGuess: String): GuessResponse {
        val request = guessRequest {
            sessionId = currentSessionId
            guess = wordToGuess
        }
        return stub.guess(request)
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}
