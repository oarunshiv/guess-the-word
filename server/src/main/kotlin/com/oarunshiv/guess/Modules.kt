package com.oarunshiv.guess

import org.koin.core.qualifier.named
import org.koin.dsl.module

fun wordGuesserModule(dictionaryFile: String) = module {
    single { Dictionary(dictionaryFile) }
    single { GuessEvaluator(get()) }
}

fun applicationModule(port: Int) =
    module {
        single(named("portNumber")) { port }
        single { GuessTheWordService(get(), get()) }
        single { GuessTheWordServer(get(qualifier = named("portNumber")), get()) }
    }
