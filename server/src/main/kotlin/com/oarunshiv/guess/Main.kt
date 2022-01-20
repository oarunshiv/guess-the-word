package com.oarunshiv.guess

import org.koin.core.context.startKoin

fun main() {
    val koin = startKoin {
        printLogger()
        modules(wordGuesserModule("five_letter_words.txt"), applicationModule)
    }.koin

    val server = koin.get<GuessTheWordServer>()
    server.start()
    server.blockUntilShutdown()
}
