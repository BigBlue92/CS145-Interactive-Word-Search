/**
 * @author Ryan Mackenzie
 * CS 145 Section B
 * Assignment 1: Word Search generator
 */

import java.util.*;

public class WordSearch {


	public static void main(String[] args) {
		Random rand = new Random();
		Scanner s = new Scanner(System.in);
		String input = "";//Will be used to track the user's input words.
		String [] words = new String[1000];//Will contain the answers from the user.
		boolean searchGenerated = false;
		int numWords = 0;
		
		int puzzleSize = printIntro(s);//Introduces the game to the user, determines puzzle size.
		char [][] answer = new char[puzzleSize][puzzleSize];
		
		puzzleSize--;//makes the puzzle size entered by the user match 0 base indexes.
		
        //Below is the user menu, which loops until the user quits and will tell the user if they entered invalid input.
		while (!input.equalsIgnoreCase("q")) {
			System.out.print("Please select an option:\nGenerate a new word search (g)\nPrint out your word search (p)\nShow the solution to your word search (s)\nQuit the program (q)\n");
			input = s.next();
			
			if (input.equalsIgnoreCase("g")) {
				plusFill(answer);//Fills the answer array with 'X' chars.
				numWords = generate(s, answer, puzzleSize, rand, numWords, words);
				searchGenerated = true;
            
			} else if (input.equalsIgnoreCase("p") ) {
         
				if (searchGenerated) {
					print(answer, puzzleSize, rand, words, numWords);
				} else System.out.println("No word search has been generated.\n");
            
			} else if (input.equalsIgnoreCase("s") ) {
         
				if (searchGenerated) {
					showAnswer(answer, puzzleSize, words, numWords);
				} else System.out.println("No word search has been generated.\n");
            
			} else if (!input.equalsIgnoreCase("q")) {
				System.out.println("Invalid input entered.\n");
			}
		}
		s.close();
	}
	

	
	//Pre: Called from the main method automatically. Takes the scanner from the main.
	//Post: Introduces the program, returns an int for puzzle size to the main so that other methods may use it. 
	public static int printIntro(Scanner s) {
		System.out.print("Welcome to Ryan's word search generator!\nThis program will allow you to generate your own word search puzzle.\n\nWhat size would you like your word search to be? (should be greater than the amount of words).\n");
		int puzzleSize = s.nextInt();
		return puzzleSize;
	}
	
	
	
	//Pre: Called from the main method if the user selects "g". Takes the scanner, the 2D answer char array, the puzzle size int, the random object, the number of words entered by the user, and the array containing the words all from the main.
	//Post: Gets the number of words, and then takes and stores each word in String[] words. Returns the number of words to the main method so that other methods may use it.
	public static int generate(Scanner s, char[][] answer, int puzzleSize, Random rand, int numWords, String[] words) {
		System.out.println("How many words would you like to include in your search?");
		numWords = s.nextInt();
		System.out.println("Please enter the words you wish to use, seperated by spaces.");
		
		for(int i = 0; i < numWords; i++) {
			words[i] = s.next();
		}

	   wordFill(rand, numWords, words, answer, puzzleSize);
	   return numWords;
	}


	
	//Pre: Called from the generate method. Parameters include the random object, the number of words as an int, the string array containing the answer words, the answer 2D char array, and the int puzzle size.
	//Post: This method handles the random start point and direction for each word. It calls one of 8 methods based on direction, and will also pick a new starting point and direction if the word will go off the edge of the puzzle.
	//This is the last line in generate, and will kick back to the main menu once this successfully places each word.
	public static void wordFill(Random rand, int numWords, String[] words, char[][] answer, int puzzleSize) {
   
		for (int wordsIndex = 0; wordsIndex < numWords; wordsIndex++) {
			int startY = rand.nextInt(puzzleSize);
			int startX = rand.nextInt(puzzleSize);
			int dir = rand.nextInt(7);//Determines direction of the word.
			char[] currentWord = words[wordsIndex].toCharArray();//Takes the current word, and splits each letter into a char and stores it in a char array.
				
			if (dir == 0 && (startX + currentWord.length) <= puzzleSize) { // Right
				wordsIndex = fillRight(answer, currentWord, startX, startY, wordsIndex);//returns either wordsIndex or wordsIndex - 1 if the word will overwrite another word.
				
			} else if (dir == 1 && startX + currentWord.length <= puzzleSize && startY + currentWord.length <= puzzleSize) { // down & right
				wordsIndex = fillDownRight(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 2 && startY + currentWord.length <= puzzleSize) { // down
				wordsIndex = fillDown(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 3 && startX - currentWord.length  >= 0 && startY + currentWord.length <= puzzleSize) { // down and left
				wordsIndex = fillDownLeft(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 4 && startX - currentWord.length >= 0) { //left
				wordsIndex = fillLeft(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 5 && startX - currentWord.length >= 0 && startY - currentWord.length >= 0) { // left and up 
				wordsIndex = fillUpLeft(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 6 && startY - currentWord.length >= 0) { // up 
				wordsIndex = fillUp(answer, currentWord, startX, startY, wordsIndex);
				
			} else if (dir == 7 && startX + currentWord.length <= puzzleSize && startY - currentWord.length >= 0) { // up and right
				wordsIndex = fillUpRight(answer, currentWord, startX, startY, wordsIndex);
						
			} else wordsIndex--;//If the word goes off the edge of the crossword, the loop will retry with a new starting location. This else line sets the for loop back by one.
			
		}
		
	}
   

	
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //Post: This method handles adding words to the answer array that print left to right. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillRight(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      
      //Checks to see if the word will fit.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         if (answer[startY][startX + xPos] != '+' &&  answer[startY][startX + xPos] != currentWord[xPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
      }
      
      //Adds the word once it knows it will fit.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         answer[startY][startX + xPos] = currentWord[xPos];
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }      


   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print top left to bottom right. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillDownRight(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      int yPos = 0;
      
      //Checks to see if the word will fit without overwriting another word. The if will only pass if the word runs into a letter that's not the one it needs.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         if (answer[startY + yPos][startX + xPos] != '+' &&  answer[startY + yPos][startX + xPos] != currentWord[xPos]) {
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the current word to try and fit it in somewhere else (randomly).
         }
         yPos++;
      }
  
      yPos = 0;//Resetting the variable that tracks Y movement from the starting point.
      
      //Adds the word once the program finds a place for it.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         answer[startY + yPos][startX + xPos] = currentWord[xPos];
         yPos++;
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print top to bottom. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillDown(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      
      //Checks to see if the word will fit without overwriting another word.
      for (int yPos = 0; yPos < currentWord.length; yPos++) {
         if (answer[startY +yPos][startX] != '+' &&  answer[startY + yPos][startX] != currentWord[yPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
      }
      
      //Adds the word once it knows it will fit.
      for (int yPos = 0; yPos < currentWord.length; yPos++) {
         answer[startY + yPos][startX] = currentWord[yPos];
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }          

   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print top right to bottom left. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillDownLeft(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      int yPos = 0;
      
      //Checks to see if the word will fit without overwriting another word.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         if (answer[startY + yPos][startX + xPos] != '+' &&  answer[startY + yPos][startX + xPos] != currentWord[yPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
         yPos++;
      }
  
      yPos = 0;//Resetting the variable that tracks Y movement from the starting point.
      
      //Adds the word once the program finds a place for it.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         answer[startY + yPos][startX + xPos] = currentWord[yPos];
         yPos++;
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print right to left. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added.
   public static int fillLeft(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      
      //Checks to see if the word will fit.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         if (answer[startY][startX + xPos] != '+' &&  answer[startY][startX + xPos] != currentWord[0 - xPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
      }
      
      //Adds the word once it knows it will fit.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         answer[startY][startX + xPos] = currentWord[0 - xPos];
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print bottom right to top left. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillUpLeft(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      int yPos = 0;
      
      //Checks to see if the word will fit without overwriting another word.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         if (answer[startY + yPos][startX + xPos] != '+' &&  answer[startY + yPos][startX + xPos] != currentWord[0 - xPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
         yPos--;
      }
  
      yPos = 0;//Resetting the variable that tracks Y movement from the starting point.
      
      //Adds the word once the program finds a place for it.
      for (int xPos = 0; xPos > 0 - currentWord.length; xPos--) {
         answer[startY + yPos][startX + xPos] = currentWord[0 - xPos];
         yPos--;
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print bottom to top. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillUp(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      
      //Checks to see if the word will fit without overwriting another word.
      for (int yPos = 0; yPos > 0 - currentWord.length; yPos--) {
         if (answer[startY +yPos][startX] != '+' &&  answer[startY + yPos][startX] != currentWord[0 - yPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
      }
      
      //Adds the word once it knows it will fit.
      for (int yPos = 0; yPos > 0 - currentWord.length; yPos--) {
         answer[startY + yPos][startX] = currentWord[0 - yPos];
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from wordFill if the random direction lands on it and if the word will fit on the puzzle. Takes the answer 2D char array, the char array of the current word, the starting x and y positions of the word, and the index of which of the users words we're on.
   //This method handles adding words to the answer array that print bottom left to top right. It will kick back to the previous method and try a new starting position if it runs into another word that's already been added, but only if it's not the same letter.
   public static int fillUpRight(char[][]answer, char[] currentWord, int startX, int startY, int wordsIndex) {
      int yPos = 0;
      
      //Checks to see if the word will fit without overwriting another word.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         if (answer[startY + yPos][startX + xPos] != '+' &&  answer[startY + yPos][startX + xPos] != currentWord[xPos]) { 
            return (wordsIndex - 1); //jumps back to the previous method, which will generate a new location for the word to try and fit it in somewhere else (randomly).
         }
         yPos--;
      }
  
      yPos = 0;//Resetting the variable that tracks Y movement from the starting point.
      
      //Adds the word once the program finds a place for it.
      for (int xPos = 0; xPos < currentWord.length; xPos++) {
         answer[startY + yPos][startX + xPos] = currentWord[xPos];
         yPos--;
      }
      return wordsIndex; //returns the value needed to move on to the next word that the user entered.
   }
   
   
   
   //Pre: Called from the main if the user selects "s". Takes the answer 2D char array, the puzzle size, the users word array, and the number of words in the array.
   //This method loops a bunch and will print out the answer array in a far more appealing format than a toString call. It also displays the words below the puzzle along with the puzzle border. 
   public static void showAnswer(char[][] answer, int puzzleSize, String[] words, int numWords) {
      for (int i = 0; i <= puzzleSize * 3 + 4; i++ ) {
		   System.out.print("|");//top border of answer
	  }
      System.out.println();
	   
	  for(int y = 0; y <= puzzleSize; y++) {
		  System.out.print("|");
		  for (int x = 0; x <= puzzleSize; x++) {
			  System.out.print(" " + answer[y][x] + " ");//Prints each array element.
		  }
		  System.out.println("|");
	  }
	  for (int i = 0; i <= puzzleSize * 3 + 4; i++ ) {
		  System.out.print("|");//bottom border of answer
	  }
      System.out.println("\nAnswers:");
      
      for (int i = 0; i < numWords; i++) {
    	  System.out.print(words[i] + " ");//Prints out words below answer key
      }
      System.out.println("\n");
   }
   
   
   //Pre: Called from the main if the user selects "p". Takes the answer 2D array, the puzzle size, the random number generator, the string array containing the answer words, and the number of words.
   //Post: This method generates a clone of the answer array, and then fills every '+' element with a random letter. It then prints it the same format as the answer array.
   public static void print(char[][] answer, int puzzleSize, Random rand, String[] words, int numWords) {
	   int letter = 0;
	   char [][] puzzle = new char[answer.length][];
	   for (int i = 0; i < answer.length; i++) {
		   puzzle[i] = answer[i].clone();
	   }
	   
	   for (int y = 0; y < puzzle.length; y++) {
		   for (int x = 0; x < puzzle.length; x++) {
			   if (puzzle[y][x] == '+') {
				   letter = rand.nextInt(25) + 1;
				   letter += 97;
				   puzzle[y][x] = (char)letter;	   
			   }
		   }
	   }
   
	   
	   for (int i = 0; i <= puzzleSize * 3 + 4; i++ ) {
		   System.out.print("|");//top border of answer
	   }
	   System.out.println();
		   
	   for (int y = 0; y <= puzzleSize; y++) {
	       System.out.print("|");
	       for (int x = 0; x <= puzzleSize; x++) {
			  System.out.print(" " + puzzle[y][x] + " ");
	       }
	       System.out.println("|");
	   }
	   for (int i = 0; i <= puzzleSize * 3 + 4; i++ ) {
		   System.out.print("|");//bottom border of answer
	   }
	   System.out.println("\nWords:");
		      
	   for (int i = 0; i < numWords; i++) {
		   System.out.print(words[i] + " ");
	   }
	   System.out.println("\n");      
   }//End print
   
   
   	//Pre: Called from the main every time a new puzzle is needed. Takes the array containing the answer from the main.
	//Post: Takes the main answer array and fills it with '+' chars whenever called. Overwrites everything so that a new puzzle can be made.
	public static void plusFill(char[][] answer) {
		for (char[] row: answer) {
			Arrays.fill(row, '+');
		}
	}
   
	
}//End Class WordSearchGenerator
