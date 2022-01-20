package com.oarunshiv.guess

import io.grpc.Server
import io.grpc.ServerBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class GuessTheWordServer : KoinComponent {
    private val port: Int by inject(qualifier = named("portNumber"))
    private val service: GuessTheWordService = get()

    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(service)
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
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
