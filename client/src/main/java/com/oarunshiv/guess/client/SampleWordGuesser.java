package com.oarunshiv.guess.client;

import com.oarunshiv.guess.GuessResponse.Color;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Sample class demonstrating how to implement WordGuesser in Java.
 */
public class SampleWordGuesser implements WordGuesser {
  public SampleWordGuesser(String dictionaryFile) {
    this.dictionaryFile = dictionaryFile;
    this.words = getWords();
  }

  private final String dictionaryFile;
  private final ArrayList<String> words;// = getWords();
  private int count = 0;

  @NotNull @Override public String nextBestGuess() {
    Random rand = new Random(); //instance of random class
    //generate random values from 0-24
    int int_random = rand.nextInt(words.size());
    count++;
    return words.remove(int_random);
  }

  @NotNull private ArrayList<String> getWords() {
    ArrayList<String> words = new ArrayList<>();
    try {
      File myObj = new File(getClass().getClassLoader().getResource(dictionaryFile).toURI());
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        words.add(myReader.nextLine());
      }
      myReader.close();
    } catch (FileNotFoundException | URISyntaxException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return words;
  }

  public int numberOfGuesses() {
    return count;
  }

  @Override public void updateGuessResponse(@NotNull String guessedWord, @NotNull List<? extends Color> result) {}
}
