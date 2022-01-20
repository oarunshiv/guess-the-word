package com.oarunshiv.guess.client

import com.oarunshiv.guess.AuthenticateResponse
import com.oarunshiv.guess.GuessResponse
import com.oarunshiv.guess.GuessTheWordGrpcKt
import com.oarunshiv.guess.authenticateRequest
import com.oarunshiv.guess.guessRequest
import io.grpc.ManagedChannel
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
        logger.debug { "Received: { ${response.colorsList} }" }
        if (response.status != GuessResponse.Status.VALID_REQUEST) {
            logger.warn { "Got an exception from the server.!" }
            throw IllegalArgumentException("Invalid input passed. Response obtained: $response")
        }
        return response.takeIf { it.status == GuessResponse.Status.VALID_REQUEST }?.colorsList
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}
