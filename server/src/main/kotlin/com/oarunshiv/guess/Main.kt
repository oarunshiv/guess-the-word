package com.oarunshiv.guess

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Main method that runs the [GuessTheWordServer].
 */
fun main(args: Array<String>) {
    val argParser = ArgParser("guess-the-word-server")
    val port by argParser.option(
        ArgType.Int,
        shortName = "p",
        description = "The port number where the server connections are accepted."
    ).default(50051)
    argParser.parse(args)

    val koin = startKoin {
        printLogger(Level.ERROR)
        modules(wordGuesserModule("five_letter_words.txt"), applicationModule(port))
    }.koin

    val server = koin.get<GuessTheWordServer>()
    server.start()
    server.blockUntilShutdown()
}
