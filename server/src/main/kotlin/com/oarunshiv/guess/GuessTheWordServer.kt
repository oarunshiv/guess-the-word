package com.oarunshiv.guess

import io.grpc.Server
import io.grpc.ServerBuilder
import org.koin.core.component.get

/**
 * Server which authenticates request and evaluates guess requests from authenticated clients.
 */
class GuessTheWordServer(port: Int, service: GuessTheWordService) {
    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(service)
        .build()

    /**
     * Starts [GuessTheWordService] on the specified service port.
     */
    fun start() {
        server.start()
        println("Server started, listening on ${server.port}")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@GuessTheWordServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
