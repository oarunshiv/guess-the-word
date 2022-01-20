package com.oarunshiv.guess

import org.koin.core.qualifier.named
import org.koin.dsl.module

fun wordGuesserModule(dictionaryFile: String) = module {
    single { Dictionary(dictionaryFile) }
    single { GuessEvaluator(get()) }
}

val applicationModule =
    module {
        single(named("portNumber")) { System.getenv("PORT")?.toInt() ?: 50051 }
        single { GuessTheWordGameService(get(), get()) }
        single { GuessTheWordServer() }
    }