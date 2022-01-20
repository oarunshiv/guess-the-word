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

    private val sessionIdMap = ConcurrentHashMap<String, String>()
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
                sessionIdMap[sha256hex] = dictionary.generateWord()
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
        val currentWord = sessionIdMap[request.sessionId]
            ?: throw StatusException(PERMISSION_DENIED.withDescription("Invalid sessionid"))
        println("[Server] Using $currentWord for ${request.sessionId}")
        return guessEvaluator.guess(currentWord, request.guess).copy { sessionId = request.sessionId }
    }
}
