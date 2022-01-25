package com.oarunshiv.guess

import io.grpc.Status.PERMISSION_DENIED
import io.grpc.StatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class GuessTheWordService(
    private val guessEvaluator: GuessEvaluator,
    private val dictionary: Dictionary
) : GuessTheWordGrpcKt.GuessTheWordCoroutineImplBase() {

    data class GuessTracker(val assignedWord: String, var guessCount: Int = 0)
    private val activeSessions = ConcurrentHashMap<String, GuessTracker>()
    private val userIdMap = mapOf("testUser" to "testPassword")
    private val salt = Instant.now().epochSecond

    // TODO Use a custom scoped injection to get a random word.

    override suspend fun authenticate(request: AuthenticateRequest): AuthenticateResponse {
        println("[Server]Received request: $request")
        when (userIdMap[request.userId]) {
            request.password -> {
                val saltedString = request.userId + request.password + salt
                val sha256hex = MessageDigest.getInstance("SHA3-256")
                    .digest(saltedString.toByteArray())
                    .joinToString("") { "%02x".format(it) }
                activeSessions[sha256hex] = GuessTracker(dictionary.generateWord())
                return authenticateResponse {
                    status = AuthenticateResponse.Status.SUCCESS
                    sessionId = sha256hex
                }
            }
            null -> return authenticateResponse {
                status = AuthenticateResponse.Status.INVALID_USER
                exceptionMessage = "Unknown user ${request.userId}"
            }
            else -> return authenticateResponse {
                status = AuthenticateResponse.Status.AUTHENTICATION_ERROR; exceptionMessage =
                    "Provided password for ${request.userId} is incorrect."
            }
        }
    }

    override suspend fun guess(request: GuessRequest): GuessResponse {
        val sessionId = request.sessionId
        val guessTracker = activeSessions[sessionId]
            ?: throw StatusException(PERMISSION_DENIED.withDescription("Invalid sessionid"))

        val currentWord = guessTracker.assignedWord
        println("$sessionId guessed $currentWord")
        val guessResponse = guessEvaluator.evaluate(currentWord, request)
        if (guessResponse.status == GuessResponse.Status.VALID_REQUEST) {
            guessTracker.guessCount++
            if (guessResponse.colorsList.count { it == GuessResponse.Color.GREEN } == 5) {
                println(
                    "------> " +
                        "$sessionId found the word $currentWord in ${guessTracker.guessCount} attempts." +
                        " <------"
                )
                activeSessions.remove(sessionId)
            }
            return guessResponse.copy { numberOfGuesses = guessTracker.guessCount }
        }
        return guessResponse
    }
}
