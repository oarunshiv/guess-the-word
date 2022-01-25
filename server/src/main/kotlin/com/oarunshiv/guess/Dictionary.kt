package com.oarunshiv.guess

/**
 * This class loads up all the 5 letter english words and is used to validate words and
 * generate random words.
 */
class Dictionary(dictionaryFile: String) {
    private val validWords =
        this::class.java.classLoader.getResourceAsStream(dictionaryFile)
            ?.bufferedReader()
            ?.readLines()
            ?.toSet()
            ?: throw IllegalStateException("Couldn't retrieve words from file.")

    /**
     * Indicates whether the provided word is a valid 5-letter English word.
     */
    fun isValidWord(word: String) = word in validWords

    /**
     * Generates a random word.
     */
    fun generateWord() = validWords.random()
}
