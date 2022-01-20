package com.oarunshiv.guess.client;

import com.oarunshiv.guess.GuessResponse.Color;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Sample class demonstrating how to implement WordGuesser.
 */
public class SampleWordGuesser implements WordGuesser {
  public SampleWordGuesser(String dictionaryFile) {
    this.dictionaryFile = dictionaryFile;
  }

  private final String dictionaryFile;
  private int count = 0;

  @NotNull @Override public String nextBestGuess() {
    ArrayList<String> words = new ArrayList<>();
    try {
      File myObj = new File(dictionaryFile);
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        words.add(myReader.nextLine());
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    Random rand = new Random(); //instance of random class
    //generate random values from 0-24
    int int_random = rand.nextInt(words.size());
    count++;
    return words.remove(int_random);
  }

  @Override public int numberOfGuesses() {
    return count;
  }

  @Override public void updateGuessResponse(@NotNull String guessedWord, @NotNull Color[] result) {
  }
}
