package com.oarunshiv.guess

class Dictionary(dictionaryFile: String) {
    private val validWords =
        this::class.java.classLoader.getResourceAsStream(dictionaryFile)
            ?.bufferedReader()
            ?.readLines()
            ?.toSet()
            ?: throw IllegalStateException("Couldn't retrieve words from file.")

    fun isValidWord(word: String) = word in validWords
    fun generateWord() = validWords.random()
    fun printSize() {
        println(validWords.size)
    }
}
