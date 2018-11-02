//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: Wa-Tor Population Dynamics Simulation
// Files: WaTor.java, Config.java
// Course: CS 200 Spring 2018
//
// Author: William Bauer
// Email: wbauer2@wisc.edu
// Lecturer's Name: Jim Williams
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name: name of your pair programming partner
// Partner Email: email address of your programming partner
// Lecturer's Name: name of your partner's lecturer
//
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
// ___ Write-up states that pair programming is allowed for this assignment.
// ___ We have both read and understand the course Pair Programming Policy.
// ___ We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates
// strangers, etc do. If you received no outside help from either type of
// source, then please explicitly indicate NONE.
//
// Persons:
// Online Sources: (identify each URL and describe their assistance in detail)
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This is the Class for the WaTor simulation. This class runs a life-cycle simulation of fish and
 * sharks in the wild. It simulates the predator an prey dynamic of the two where sharks must eat
 * fish to survive and ends the simulation when either there are no more sharks remaining or all of
 * the fish have been eaten.
 * 
 * @author Will
 *
 */

public class WaTor {

    /**
     * This is the main method for WaTor simulation. Based on:
     * http://home.cc.gatech.edu/biocs1/uploads/2/wator_dewdney.pdf This method contains the main
     * simulation loop. In main the Scanner for System.in is allocated and used to interact with the
     * user.
     * 
     * @param args (unused)
     */
    public static void main(String[] args) {

        // scanner and random number generator for use throughout
        Scanner input = new Scanner(System.in);
        Random randGen = new Random();

        // values at the same index in these parallel arrays correspond to the
        // same creature

        // a value equal or greater than 0 at a location indicates a fish of
        // that age at that location.
        int[][] fish = null;

        // true at a location indicates that the fish moved during the current
        // chronon
        boolean[][] fishMoved = null;

        // a value equal or greater than 0 at a location indicates a shark of
        // that age at that location
        int[][] sharks = null;

        // true at a location indicates that the shark moved during the current
        // chronon
        boolean[][] sharksMoved = null;

        // a value equal or greater than 0 indicates the number of chronon
        // since the shark last ate.
        int[][] starve = null;

        // an array for simulation parameters
        // to be used when saving or loading parameters in Milestone 3
        int[] simulationParameters = null;

        // Make the history ArrayList
        ArrayList<int[]> history = new ArrayList<int[]>();


        // welcome message
        System.out.println("Welcome to Wa-Tor");

        // Ask user if they would like to load simulation parameters from a file.
        // If the user enters a y or Y as the only non-whitespace characters
        // then prompt for filename and call loadSimulationParameters
        boolean filesLoaded = false;
        String fileName = null;

        System.out.print("Do you want to load simulation parameters from a file (y/n): ");
        String response = input.nextLine();

        if (response.equalsIgnoreCase("y")) {
            System.out.print("Enter filename to load: ");
            fileName = input.nextLine();
            simulationParameters = loadSimulationParameters(fileName);
        }
        // if simulationParameters is filled by loadSimulationParameters set filesLoaded to true
        if (simulationParameters != null) {
            filesLoaded = true;
        }
        // prompts the user to enter the simulation parameters
        if (simulationParameters == null) {
            simulationParameters = new int[Config.SIM_PARAMS.length];
            for (int i = 0; i < Config.SIM_PARAMS.length; i++) {
                System.out.print("Enter " + Config.SIM_PARAMS[i] + ": ");
                simulationParameters[i] = input.nextInt();
            }
            input.nextLine(); // read and ignore remaining newline
        }

        // if seed is > 0 then set the random number generator to seed
        if (simulationParameters[indexForParam("seed")] > 0) {
            randGen.setSeed(simulationParameters[indexForParam("seed")]);
        }

        // save simulation parameters in local variables to help make code
        // more readable.
        int oceanWidth = simulationParameters[indexForParam("ocean_width")];
        int oceanHeight = simulationParameters[indexForParam("ocean_height")];
        int startingFish = simulationParameters[indexForParam("starting_fish")];
        int startingSharks = simulationParameters[indexForParam("starting_sharks")];
        int fishBreed = simulationParameters[indexForParam("fish_breed")];
        int sharksBreed = simulationParameters[indexForParam("sharks_breed")];
        int sharksStarve = simulationParameters[indexForParam("sharks_starve")];

        // create parallel arrays to track fish and sharks
        fish = new int[oceanHeight][oceanWidth];
        fishMoved = new boolean[oceanHeight][oceanWidth];
        sharks = new int[oceanHeight][oceanWidth];
        sharksMoved = new boolean[oceanHeight][oceanWidth];
        starve = new int[oceanHeight][oceanWidth];

        // make sure fish, sharks and starve arrays are empty (call emptyArray)
        emptyArray(fish);
        emptyArray(sharks);
        emptyArray(starve);

        // place the initial fish and sharks and print out the number
        // placed
        int numFish = placeFish(fish, startingFish, fishBreed, randGen);
        int numSharks = placeSharks(fish, sharks, startingSharks, sharksBreed, randGen);
        System.out.println("Placed " + numFish + " fish.");
        System.out.println("Placed " + numSharks + " sharks.");

        int currentChronon = 1;

        // Adds this Chronon's information to the History ArrayList
        // int[] historyElement = new int[3];
        // historyElement[0] = currentChronon;
        // historyElement[1] = numFish;
        // historyElement[2] = numSharks;
        // history.add(historyElement);

        history.add(new int[] {currentChronon, numFish, numSharks});

        // simulation ends when no more sharks or fish remain
        boolean simulationEnd = numFish <= 0 || numSharks <= 0;

        while (!simulationEnd) {
            int fishSuccess = 0;
            int sharkSuccess = 0;
            // print out the locations of the fish and sharks
            showFishAndSharks(currentChronon, fish, sharks);

            // prompt user for Enter, # of chronon, or 'end'
            // Enter advances to next chronon, a number
            // entered means run that many chronon,
            // 'end' will end the simulation
            System.out.print("Press Enter, # of chronon, or 'end': ");
            response = input.nextLine().trim();
            if (response.equalsIgnoreCase("end")) {
                break; // leave simulation loop
            } else if (response.length() > 0) {
                int numChrononAdvanced = Integer.parseInt(response);

                for (int i = 0; i < numChrononAdvanced; ++i) {
                    // clear fishMoved and sharksMoved from previous chronon
                    clearMoves(fishMoved);
                    clearMoves(sharksMoved);


                    // call fishSwimAndBreed
                    fishSuccess = fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);

                    // call sharksHuntAndBreed
                    sharkSuccess = sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved,
                        sharksBreed, starve, sharksStarve, randGen);

                    // increment current chronon and count the current number of fish and sharks
                    ++currentChronon;
                    numFish = countCreatures(fish);
                    numSharks = countCreatures(sharks);
                    // Adds this Chronon's information to the History ArrayList
                    int[] newHistoryElement = new int[3];
                    newHistoryElement[0] = currentChronon;
                    newHistoryElement[1] = numFish;
                    newHistoryElement[2] = numSharks;

                    history.add(newHistoryElement);
                    // history.add(new int[]{currentChronon, numFish, numSharks});

                    // if all the fish or sharks are gone then end simulation
                    if (simulationEnd = numFish <= 0 || numSharks <= 0) {
                        break;
                    }
                }
            } else {

                // clear fishMoved and sharksMoved from previous chronon
                clearMoves(fishMoved);
                clearMoves(sharksMoved);


                // call fishSwimAndBreed
                fishSuccess = fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);

                // call sharksHuntAndBreed
                sharkSuccess = sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved, sharksBreed,
                    starve, sharksStarve, randGen);

                // increment current chronon and count the current number of fish and sharks
                ++currentChronon;
                numFish = countCreatures(fish);
                numSharks = countCreatures(sharks);
                // Adds this Chronon's information to the History ArrayList
                int[] newHistoryElement = new int[3];
                newHistoryElement[0] = currentChronon;
                newHistoryElement[1] = numFish;
                newHistoryElement[2] = numSharks;

                history.add(newHistoryElement);
                // history.add(new int[]{currentChronon, numFish, numSharks});

                // if all the fish or sharks are gone then end simulation
                simulationEnd = numFish <= 0 || numSharks <= 0;
            } ;
        }
        // print the final ocean contents
        showFishAndSharks(currentChronon, fish, sharks);

        // Print out why the simulation ended.
        if (numSharks <= 0) {
            System.out.println("Wa-Tor simulation ended since no sharks remain.");
        } else if (numFish <= 0) {
            System.out.println("Wa-Tor simulation ended since no fish remain.");
        } else {
            System.out.println("Wa-Tor simulation ended at user request.");
        }

        // If the user was prompted to enter simulation parameters
        // then prompt the user to see if they would like to save them.
        // If the user enters a y or Y as the only non-whitespace characters
        // then prompt for filename and save, otherwise don't save parameters.
        // call saveSimulationParameters to actually save the parameters to the file.
        // If saveSimulationParameters throws an IOException then catch it and
        // repeat the code to prompt asking the user if they want to save
        // the simulation parameters.

        boolean saved = false;
        String SPSaveFile = null;

        if (!filesLoaded) {
            while (!saved) {
                System.out.print("Save simulation parameters (y/n): ");
                response = input.nextLine().trim();
                if (response.equalsIgnoreCase("y")) {
                    System.out.print("Enter filename: ");
                    SPSaveFile = input.nextLine();
                    try {
                        saveSimulationParameters(simulationParameters, SPSaveFile);
                        saved = true;
                    } catch (IOException e) {
                        saved = false;
                    }
                }
                saved = true;
            }
        }


        // Always prompt the user to see if they would like to save a
        // population chart of the simulation.
        // If the user enters a y or Y as the only non-whitespace characters
        // then prompt for filename and save, otherwise don't save chart.
        // call savePopulationChart to save the parameters to the file.
        // If savePopulationChart throws an IOException then catch it and
        // repeat the code to prompt asking the user if they want to save
        // the population chart.

        saved = false;
        while (!saved) {
            System.out.print("Save population chart (y/n): ");
            response = input.nextLine().trim();
            if (response.equalsIgnoreCase("y")) {

                String PCSaveFile = null;
                System.out.print("Enter filename: ");
                PCSaveFile = input.nextLine();
                try {
                    savePopulationChart(simulationParameters, history, oceanWidth, oceanHeight,
                        PCSaveFile);
                    saved = true;
                } catch (IOException e) {
                    System.out.print("Unable to save to: " + PCSaveFile);
                    saved = false;
                }
            }
        }

        input.close();
    }

    /**
     * This is called when a fish cannot move. This increments the fish's age and notes in the
     * fishMove array that it has been updated this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param row The row of the fish that is staying.
     * @param col The col of the fish that is staying.
     */
    public static void aFishStays(int[][] fish, boolean[][] fishMove, int row, int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish %d,%d stays\n", row, col);
        }
        fish[row][col]++; // increment age of fish
        fishMove[row][col] = true;
    }

    /**
     * The fish moves from fromRow,fromCol to toRow,toCol. The age of the fish is incremented. The
     * fishMove array records that this fish has moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow The row the fish is moving from.
     * @param fromCol The column the fish is moving from.
     * @param toRow The row the fish is moving to.
     * @param toCol The column the fish is moving to.
     */
    public static void aFishMoves(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol,
        int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow,
                toCol);
        }
        // just move fish
        fish[toRow][toCol] = fish[fromRow][fromCol] + 1; // increment age
        fishMove[toRow][toCol] = true;

        // clear previous location
        fish[fromRow][fromCol] = Config.EMPTY;
        fishMove[fromRow][fromCol] = false;
    }

    /**
     * The fish moves from fromRow,fromCol to toRow,toCol. This fish breeds so its age is reset to
     * 0. The new fish is put in the fromRow,fromCol with an age of 0. The fishMove array records
     * that both fish moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow The row the fish is moving from and where the new fish is located.
     * @param fromCol The column the fish is moving from and where the new fish is located.
     * @param toRow The row the fish is moving to.
     * @param toCol The column the fish is moving to.
     */
    public static void aFishMovesAndBreeds(int[][] fish, boolean[][] fishMove, int fromRow,
        int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d and breed\n", fromRow, fromCol,
                toRow, toCol);
        }
        // move fish, resetting age in new location
        fish[toRow][toCol] = 0;
        fishMove[toRow][toCol] = true;

        // breed
        fish[fromRow][fromCol] = 0; // new fish in previous location
        fishMove[fromRow][fromCol] = true;
    }

    /**
     * This removes the shark from the sharks, sharksMove and starve arrays.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStarves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row,
        int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d starves\n", row, col);
        }
        sharks[row][col] = Config.EMPTY;
        starve[row][col] = Config.EMPTY;
        sharksMove[row][col] = false;
    }

    /**
     * This is called when a shark cannot move. This increments the shark's age and time since the
     * shark last ate and notes in the sharkMove array that it has been updated this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStays(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row,
        int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d can't move\n", row, col);
        }
        sharks[row][col]++; // increment age of shark
        starve[row][col]++; // increment time since last ate
        sharksMove[row][col] = true;
    }

    /**
     * This moves a shark from fromRow,fromCol to toRow,toCol. This increments the age and time
     * since the shark last ate and notes that this shark has moved this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow The row the shark is moving to.
     * @param toCol The column the shark is moving to.
     */
    public static void sharkMoves(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow,
                toCol);
        }
        // just move shark
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] + 1;

        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = false;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark moves from fromRow,fromCol to toRow,toCol. This shark breeds so its age is reset to
     * 0 but its time since last ate is incremented. The new shark is put in the fromRow,fromCol
     * with an age of 0 and 0 time since last ate. The fishMove array records that both fish moved
     * this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow The row the shark is moving to.
     * @param toCol The column the shark is moving to.
     */
    public static void sharkMovesAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int fromRow, int fromCol, int toRow, int toCol) {

        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d and breeds\n", fromRow,
                fromCol, toRow, toCol);
        }
        sharks[toRow][toCol] = 0; // reset age in new location
        sharks[fromRow][fromCol] = 0; // new fish in previous location

        sharksMove[toRow][toCol] = true;
        sharksMove[fromRow][fromCol] = true;

        starve[toRow][toCol] = starve[fromRow][fromCol] + 1;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The sharks age is
     * incremented, time since it last ate and that this shark moved this chronon are noted. The
     * fish is now gone.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow The row the shark is moving to.
     * @param toCol The column the shark is moving to.
     */
    public static void sharkEatsFish(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d\n", fromRow, fromCol,
                toRow, toCol);
        }
        // eat fish
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        // move shark
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] = 0;

        // clear old location
        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The fish is now gone.
     * This shark breeds so its age is reset to 0 and its time since last ate is incremented. The
     * new shark is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. That
     * these sharks moved this chronon is noted.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fromRow The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow The row the shark is moving to.
     * @param toCol The column the shark is moving to.
     */
    public static void sharkEatsFishAndBreeds(int[][] sharks, boolean[][] sharksMove,
        int[][] starve, int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow,
        int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d and breed\n",
                fromRow, fromCol, toRow, toCol);
        }
        // shark eats fish and may breed
        // eat fish
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        // move to new location
        sharks[toRow][toCol] = 0; // reset age in new location
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = 0;

        // breed
        sharks[fromRow][fromCol] = 0; // new shark in previous location
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * This sets all elements within the array to Config.EMPTY. This does not assume any array size
     * but uses the .length attribute of the array. If arr is null the method prints an error
     * message and returns.
     * 
     * @param arr The array that only has EMPTY elements when method has executed.
     */
    public static void emptyArray(int[][] arr) {
        if (arr == null) {
            System.out.println("emptyArray arr is null");
            return;
        }
        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = Config.EMPTY;
            }
        }
    }

    /**
     * This sets all elements within the array to false, indicating not moved this chronon. This
     * does not assume any array size but uses the .length attribute of the array. If arr is null
     * the method prints a message and returns.
     * 
     * @param arr The array will have only false elements when method completes.
     */
    public static void clearMoves(boolean[][] arr) {
        if (arr == null) {
            System.out.println("clearMoves arr is null");
            return;
        }
        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = false;
            }
        }
    }

    /**
     * Shows the locations of all the fish and sharks noting a fish with Config.FISH_MARK, a shark
     * with Config.SHARK_MARK and empty water with Config.WATER_MARK. At the top is a title
     * "Chronon: " with the current chronon and at the bottom is a count of the number of fish and
     * sharks. Example of a 3 row, 5 column ocean. Note every mark is also followed by a space.
     * Chronon: 1 O . . O . . . . . O fish:7 sharks:3
     * 
     * @param chronon The current chronon.
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     */
    public static void showFishAndSharks(int chronon, int[][] fish, int[][] sharks) {
        System.out.println("Chronon: " + chronon);
        int numFish = 0;
        int numSharks = 0;

        for (int i = 0; i < fish.length; ++i) {
            for (int j = 0; j < fish[0].length; ++j) {
                if (fish[i][j] != -1) {
                    System.out.print(Config.FISH_MARK + " ");
                    ++numFish;
                } else if (sharks[i][j] != -1) {
                    System.out.print(Config.SHARK_MARK + " ");
                    ++numSharks;
                } else {
                    System.out.print(Config.WATER_MARK + " ");
                }
            }
            System.out.println();
        }
        System.out.print("fish:" + numFish + " ");
        System.out.println("sharks:" + numSharks);
    }

    /**
     * This places up to startingFish fish in the fish array. This randomly chooses a location and
     * age for each fish. Algorithm: For each fish this tries to place reset the attempts to place
     * the particular fish to 0. Try to place a single fish up to Config.MAX_PLACE_ATTEMPTS times
     * Randomly choose a row, then column using randGen.nextInt( ) with the appropriate fish array
     * dimension as the parameter. Increment the number of attempts to place the fish. If the
     * location is empty in the fish array then place the fish in that location, randomly choosing
     * its age from 0 up to and including fishBreed. If the location is already occupied, generate
     * another location and try again. On the Config.MAX_PLACE_ATTEMPTS try, whether or not the fish
     * is successfully placed, stop trying to place additional fish. Return the number of fish
     * actually placed.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param startingFish The number of fish to attempt to place in the fish array.
     * @param fishBreed The age at which fish breed.
     * @param randGen The random number generator.
     * @return the number of fish actually placed.
     */
    public static int placeFish(int[][] fish, int startingFish, int fishBreed, Random randGen) {
        int numFishPlaced = 0;

        // For each fish we try to place
        for (int i = 1; i <= startingFish; ++i) {

            boolean placed = false;
            // Reset attempts
            int placeAttempts = 0;
            // While not yet placed and place attempts is less than the max place attempts
            while (!placed && placeAttempts < Config.MAX_PLACE_ATTEMPTS) {

                // Randomly creates the row and col to attempt to place the fish
                int row = randGen.nextInt(fish.length);
                int column = randGen.nextInt(fish[0].length);
                ++placeAttempts;// increments place attempts

                // checks to see if can place fish, if can, does
                if (fish[row][column] == -1) {
                    int fishAge = randGen.nextInt(fishBreed + 1);
                    fish[row][column] = fishAge;
                    ++numFishPlaced;
                    placed = true;
                }

            }
            // If not placed or placeAttempts equals the max place attempts break
            if (!placed || (placeAttempts == Config.MAX_PLACE_ATTEMPTS)) {
                break;
            }

        } // End of startingFish for loop
        return numFishPlaced;

    }

    /**
     * This places up to startingSharks sharks in the sharks array. This randomly chooses a location
     * and age for each shark. Algorithm: For each shark this tries to place, reset the attempts to
     * place the particular shark to 0. Try to place a single shark up to Config.MAX_PLACE_ATTEMPTS
     * times. Randomly choose a row, then column using randGen.nextInt( ) with the appropriate shark
     * array dimension as the parameter. Increment the number of attempts to place the shark. If the
     * location is empty in both the fish array and sharks array then place the shark in that
     * location, randomly choosing its age from 0 up to and including sharkBreed. If the location is
     * already occupied, generate another location and try again. On the Config.MAX_PLACE_ATTEMPTS
     * try, whether or not the shark is successfully placed, stop trying to place additional sharks.
     * Return the number of sharks actually placed. *
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param startingSharks The number of sharks to attempt to place in the sharks array.
     * @param sharksBreed The age at which sharks breed.
     * @param randGen The random number generator.
     * @return the number of sharks actually placed.
     */
    public static int placeSharks(int[][] fish, int[][] sharks, int startingSharks, int sharksBreed,
        Random randGen) {
        int numSharksPlaced = 0;


        // For every shark we should attempt to place
        for (int i = 1; i <= startingSharks; ++i) {


            // The number of times we've attempted to place this specific shark
            int placeAttempts = 0;
            boolean placed = false;
            // While we haven't yet placed this shark...
            while (!placed && placeAttempts < Config.MAX_PLACE_ATTEMPTS) {



                // Generate a row and column for this shark
                int row = randGen.nextInt(sharks.length);
                int column = randGen.nextInt(sharks[0].length);
                ++placeAttempts;


                // checks to see if can place Shark, if can, does
                if (fish[row][column] == -1 && sharks[row][column] == -1) {
                    int sharkAge = randGen.nextInt(sharksBreed + 1);
                    sharks[row][column] = sharkAge;
                    ++numSharksPlaced;

                    placed = true;
                }

            }

            // If we failed, don't place any more sharks
            if (!placed || (placeAttempts == Config.MAX_PLACE_ATTEMPTS)) {
                break;
            }

        } // End of the shark attempt for loop

        return numSharksPlaced;
    }

    /**
     * This counts the number of fish or the number of sharks depending on the array passed in.
     * 
     * @param fishOrSharks Either an array containing the ages of all the fish or an array
     *        containing the ages of all the sharks.
     * @return The number of fish or number of sharks, depending on the array passed in.
     */
    public static int countCreatures(int[][] fishOrSharks) {
        int numCreatures = 0;
        for (int i = 0; i < fishOrSharks.length; ++i) {
            for (int j = 0; j < fishOrSharks[0].length; ++j) {
                if (fishOrSharks[i][j] != -1) {
                    ++numCreatures;
                }
            }
        }
        return numCreatures;
    }

    /**
     * This returns a list of the coordinates (row,col) of positions around the row, col parameters
     * that do not contain a fish or shark. The positions that are considered are directly above,
     * below, left and right of row, col and IN THAT ORDER. Where 0,0 is the upper left corner when
     * fish and sharks arrays are printed out. Remember that creatures moving off one side of the
     * array appear on the opposite side. For example, those moving left off the array appear on the
     * right side and those moving down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying the location.
     * @param sharks A non-Config.EMPTY value indicates the age of the shark occupying the location.
     * @param row The row of a creature trying to move.
     * @param col The column of a creature trying to move.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of
     *         unoccupied locations. In each coordinate array the 0 index is the row, the 1 index is
     *         the column.
     */
    public static ArrayList<int[]> unoccupiedPositions(int[][] fish, int[][] sharks, int row,
        int col) {

        ArrayList<int[]> unoccupied = new ArrayList<>();
        // Above, if the space above does not go off the top of the array
        if ((row - 1) >= 0) {
            // if the space above is empty in both the fish and shark arrays add it to the
            // unoccupied ArrayList
            if (fish[row - 1][col] == Config.EMPTY && sharks[row - 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {(row - 1), col});
            }
        } else {
            // if the space above does go off the top of the array check the space at the bottom of
            // the array and add it to the unoccupied ArrayList if it is empty
            if (fish[fish.length - 1][col] == Config.EMPTY
                && sharks[fish.length - 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {(fish.length - 1), col});
            }
        }
        // Below, if the space below does not go off the bottom of the array
        if ((row + 1) <= (fish.length - 1)) {
            // if the space below is empty in both the fish and shark arrays add it to the
            // unoccupied ArrayList
            if (fish[row + 1][col] == Config.EMPTY && sharks[row + 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {(row + 1), col});
            }
        } else {
            // if the space below does go off the bottom of the array check the space at the top of
            // the array and add it to the unoccupied ArrayList if it is empty
            if (fish[0][col] == Config.EMPTY && sharks[0][col] == Config.EMPTY) {
                unoccupied.add(new int[] {0, col});
            }
        }
        // Left, if the space left does not go off the left of the array
        if ((col - 1) >= 0) {
            // if the space left is empty in both the fish and shark arrays add it to the unoccupied
            // ArrayList
            if (fish[row][col - 1] == Config.EMPTY && sharks[row][col - 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, (col - 1)});
            }
        } else {
            // if the space left does go off the left of the array check the space at the right of
            // the array and add it to the unoccupied ArrayList if it is empty
            if (fish[row][fish[0].length - 1] == Config.EMPTY
                && sharks[row][fish[0].length - 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, (fish[0].length - 1)});
            }
        }
        // Right, if the space right does not go off the right of the array
        if ((col + 1) <= (fish[0].length - 1)) {
            // if the space right is empty in both the fish and shark arrays add it to the
            // unoccupied ArrayList
            if (fish[row][col + 1] == Config.EMPTY && sharks[row][col + 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, (col + 1)});
            }
        } else {
            // if the space right does go off the right of the array check the space at the left of
            // the array and add it to the unoccupied ArrayList if it is empty
            if (fish[row][0] == Config.EMPTY && sharks[row][0] == Config.EMPTY) {
                unoccupied.add(new int[] {row, 0});
            }
        }

        return unoccupied;
    }


    /**
     * This randomly selects, with the Random number generator passed as a parameter, one of
     * elements (array of int) in the neighbors list. If the size of neighbors is 0 (empty) then
     * null is returned. If neighbors contains 1 element then that element is returned. The randGen
     * parameter is only used to select 1 element from a neighbors list containing more than 1
     * element. If neighbors or randGen is null then an error message is printed to System.err and
     * null is returned.
     * 
     * @param neighbors A list of potential neighbors to choose from.
     * @param randGen The random number generator used throughout the simulation.
     * @return A int[] containing the coordinates of a creatures move or null as specified above.
     */
    public static int[] chooseMove(ArrayList<int[]> neighbors, Random randGen) {
        // Creates the array to track the chosen move
        int[] moveChosen = null;
        // If neighbors is null or randGen is null print an error message and return null
        if (neighbors == null || randGen == null) {
            System.err.println("ArrayList neighbors = null or randGen == null");
            return null;
        } else { // Otherwise
            // If neighbors is empty return null
            if (neighbors.isEmpty()) {
                return null;
            } else if (neighbors.size() == 1) { // Else if neighbors is of size 1 choose that move
                moveChosen = neighbors.get(0);
            } else { // Otherwise
                // Randomly choose the location to move to from the neighbors
                int chosenElement = randGen.nextInt(neighbors.size());
                moveChosen = neighbors.get(chosenElement);
            }
        }
        // Return the chosen move
        return moveChosen;
    }

    /**
     * This attempts to move each fish each chronon.
     * 
     * This is a key method with a number of parameters. Check that the parameters are valid prior
     * to writing the code to move a fish. The parameters are checked in the order they appear in
     * the parameter list. If any of the array parameters are null or not at least 1 element in size
     * then a helpful error message is printed out and -1 is returned. An example message for an
     * invalid fish array is "fishSwimAndBreed Invalid fish array: Null or not at least 1 in each
     * dimension.". Testing will not check the content of the message but will check whether the
     * correct number is returned for the situation. Passing this test means we know fish[0] exists
     * and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same
     * length). If fishBreed is less than zero a helpful error message is printed out and -2 is
     * returned. If randGen is null then a helpful error message is printed out and -3 is returned.
     * 
     *
     * Algorithm: for each fish that has not moved this chronon get the available unoccupied
     * positions for the fish to move (call unoccupiedPositions) choose a move from those positions
     * (call chooseMove) Based on the move chosen, either the fish stays (call aFishStays) fish
     * moves (call aFishMoves) or fish moves and breeds (call aFishMovesAndBreeds)
     * 
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param fishBreed The age in chronon that a fish must be to breed.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     *         fish 0 is returned indicating success.
     */
    public static int fishSwimAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove,
        int fishBreed, Random randGen) {
        // If the fish array is null or not at least 1 in each dimension print an error message and
        // return -1
        if (fish == null || fish.length < 1 || fish[0].length < 1) {
            System.out.println(
                "fishSwimAndBreed Invalid fish array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the sharks array is null or not at least 1 in each dimension print an error message
        // and return -1
        if (sharks == null || sharks.length < 1 || sharks[0].length < 1) {
            System.out.println(
                "fishSwimAndBreed Invalid sharks array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the fishMove array is null or not at least 1 in each dimension print an error message
        // and return -1
        if (fishMove == null || fishMove.length < 1 || fishMove[0].length < 1) {
            System.out.println(
                "fishSwimAndBreed Invalid fishMove array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the value for fishBreed is not greater than 0, print an error message and return -2
        if (fishBreed < 0) {
            System.out
                .println("fishSwinAndBreed Invalid fishBreed: Must be greater than or equal to 0.");
            return -2;
        }
        // If randGen is null, print an error message and return -3
        if (randGen == null) {
            System.out.println("fishSwinAndBreed Invalid randGen: Null.");
            return -3;
        }
        // Iterates through the fish array
        for (int i = 0; i < fish.length; ++i) {
            for (int j = 0; j < fish[0].length; ++j) {
                // If the fish array at [i][j] is not empty and hasn't yet moved
                if (fish[i][j] != Config.EMPTY && fishMove[i][j] == false) {
                    // Fill ArrayList possibleMoves by calling unoccupiedPositions
                    ArrayList<int[]> possibleMoves = unoccupiedPositions(fish, sharks, i, j);
                    // Fill the array for the chosen move by calling chooseMove
                    int[] moveChosen = chooseMove(possibleMoves, randGen);
                    // If moveChosen is null, call aFishStays
                    if (moveChosen == null) {
                        aFishStays(fish, fishMove, i, j);
                    } else if (moveChosen != null && fish[i][j] >= fishBreed) { // else if there is
                                                                                // a place to move
                                                                                // and the fish can
                                                                                // breed
                        aFishMovesAndBreeds(fish, fishMove, i, j, moveChosen[0], moveChosen[1]);
                    } else { // else call aFishMoves
                        aFishMoves(fish, fishMove, i, j, moveChosen[0], moveChosen[1]);
                    }
                }
            }
        }
        // Return 0 is successful
        return 0;
    }

    /**
     * This returns a list of the coordinates (row,col) of positions around the row, col parameters
     * that contain a fish. The positions that are considered are directly above, below, left and
     * right of row, col and IN THAT ORDER. Where 0,0 is the upper left corner when fish array is
     * printed out. Remember that sharks moving off one side of the array appear on the opposite
     * side. For example, those moving left off the array appear on the right side and those moving
     * down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying a location.
     * @param row The row of a hungry shark.
     * @param col The column of a hungry shark.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of fish
     *         locations. In each coordinate array the 0 index is the row, the 1 index is the
     *         column.
     */
    public static ArrayList<int[]> fishPositions(int[][] fish, int row, int col) {
        ArrayList<int[]> fishPositions = new ArrayList<>();

        // Above, if the space above does not go off the top of the array
        if ((row - 1) >= 0) {
            // if the space above is not empty in the fish array add it to the
            // fishPositions ArrayList
            if (fish[row - 1][col] != Config.EMPTY) {
                fishPositions.add(new int[] {(row - 1), col});

            }
        } else {
            // if the space above does go off the top of the array check the space at the bottom of
            // the array and add it to the fishPositions ArrayList if it is not empty
            if (fish[fish.length - 1][col] != Config.EMPTY) {
                fishPositions.add(new int[] {(fish.length - 1), col});

            }
        }
        // Below, if the space above does not go off the bottom of the array
        if ((row + 1) <= (fish.length - 1)) {
            // if the space below is not empty in the fish array add it to the
            // fishPositions ArrayList
            if (fish[row + 1][col] != Config.EMPTY) {
                fishPositions.add(new int[] {(row + 1), col});

            }
        } else {
            // if the space below does go off the bottom of the array check the space at the top of
            // the array and add it to the fishPositions ArrayList if it is not empty
            if (fish[0][col] != Config.EMPTY) {
                fishPositions.add(new int[] {0, col});

            }
        }
        // Left, if the space above does not go off the left of the array
        if ((col - 1) >= 0) {
            // if the space left is not empty in the fish array add it to the
            // fishPositions ArrayList
            if (fish[row][col - 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, (col - 1)});
            }
        } else {
            // if the space left does go off the left of the array check the space at the right of
            // the array and add it to the fishPositions ArrayList if it is not empty
            if (fish[row][fish[0].length - 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, (fish[0].length - 1)});
            }
        }
        // Right, if the space above does not go off the right of the array
        if ((col + 1) <= (fish[0].length - 1)) {
            // if the space right is not empty in the fish array add it to the
            // fishPositions ArrayList
            if (fish[row][col + 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, (col + 1)});
            }
        } else {
            // if the space right does go off the right of the array check the space at the left of
            // the array and add it to the fishPositions ArrayList if it is not empty
            if (fish[row][0] != Config.EMPTY) {
                fishPositions.add(new int[] {row, 0});
            }
        }

        // Return the fishPositions arrayList
        return fishPositions;
    }

    /**
     * This attempts to move each shark each chronon.
     *
     * This is a key method with a number of parameters. Check that the parameters are valid prior
     * to writing the code to move a shark. The parameters are checked in the order they appear in
     * the parameter list. If any of the array parameters are null or not at least 1 element in size
     * then a helpful error message is printed out and -1 is returned. An example message for an
     * invalid fish array is "sharksHuntAndBreed Invalid fish array: Null or not at least 1 in each
     * dimension.". Testing will not check the content of the message but will check whether the
     * correct number is returned for the situation. Passing this test means we know fish[0] exists
     * and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same
     * length). If sharksBreed or sharksStarve are less than zero a helpful error message is printed
     * out and -2 is returned. If randGen is null then a helpful error message is printed out and -3
     * is returned.
     * 
     * Algorithm to move a shark: for each shark that has not moved this chronon check to see if the
     * shark has starved, if so call sharkStarves otherwise get the available positions of
     * neighboring fish (call fishPositions) if there are no neighboring fish to eat then determine
     * available positions (call unoccupiedPositions) choose a move (call chooseMove) and based on
     * the move chosen call sharkStays, sharkMoves or sharkMovesAndBreeds appropriately, using the
     * sharkBreed parameter to see if a shark breeds. else if there are neighboring fish then choose
     * the move (call chooseMove), eat the fish (call sharkEatsFish or sharkEatsFishAndBreeds)
     * appropriately. return 0, meaning success.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this chronon.
     * @param sharksMove The array containing the indicator of whether each shark moved this
     *        chronon.
     * @param sharksBreed The age the sharks must be in order to breed.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param sharksStarve The time in chronon since the sharks last ate that results in them
     *        starving to death.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     *         sharks 0 is returned indicating success.
     */
    public static int sharksHuntAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove,
        boolean[][] sharksMove, int sharksBreed, int[][] starve, int sharksStarve, Random randGen) {
        // If the fish array is null or not at least 1 in each dimension print an error message and
        // return -1
        if (fish == null || fish.length < 1 || fish[0].length < 1) {
            System.out.println(
                "sharksSwimAndBreed Invalid fish array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the sharks array is null or not at least 1 in each dimension print an error message
        // and
        // return -1
        if (sharks == null || sharks.length < 1 || sharks[0].length < 1) {
            System.out.println(
                "sharksSwimAndBreed Invalid sharks array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the fishMove array is null or not at least 1 in each dimension print an error message
        // and
        // return -1
        if (fishMove == null || fishMove.length < 1 || fishMove[0].length < 1) {
            System.out.println(
                "sharksSwimAndBreed Invalid fishMove array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If the SharksMove array is null or not at least 1 in each dimension print an error
        // message and
        // return -1
        if (sharksMove == null || sharksMove.length < 1 || sharksMove[0].length < 1) {
            System.out.println(
                "sharksSwimAndBreed Invalid sharksMove array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If sharksBreed is less than 0, print an error message and return -2
        if (sharksBreed < 0) {
            System.out.println(
                "sharksSwinAndBreed Invalid sharksBreed: Must be greater than or equal to 0.");
            return -2;
        }
        // If the starve array is null or not at least 1 in each dimension print an error message
        // and
        // return -1
        if (starve == null || starve.length < 1 || starve[0].length < 1) {
            System.out.println(
                "sharksSwimAndBreed Invalid starve array: Null or not at least 1 in each dimension.");
            return -1;
        }
        // If sharksStarve is less than 0, print an error message and return -2
        if (sharksStarve < 0) {
            System.out.println(
                "sharksSwinAndBreed Invalid sharksStarve: Must be greater than or equal to 0.");
            return -2;
        }
        // If randGen is null, print an error message and return -3
        if (randGen == null) {
            System.out.println("sharksSwimAndBreed Invalid randGen: Null.");
            return -3;
        }
        if (Config.DEBUG) {
            System.out.println("sharks age:");
            for (int i = 0; i < sharks.length; ++i) {
                for (int j = 0; j < sharks[0].length; ++j) {
                    System.out.println(i + "," + j + " :" + sharks[i][j]);
                }
            }
            System.out.println("sharks starve:");
            for (int i = 0; i < starve.length; ++i) {
                for (int j = 0; j < starve[0].length; ++j) {
                    System.out.println(i + "," + j + " :" + starve[i][j]);
                }
            }
        }
        // Iterate through the sharks array
        for (int i = 0; i < sharks.length; ++i) {
            for (int j = 0; j < sharks[0].length; ++j) {
                // If the shark array at [i][j] is not empty, and starve at [i][j] is equal to
                // sharksStarve, and the shark has not yet moved
                if (sharks[i][j] != Config.EMPTY && starve[i][j] == sharksStarve
                    && sharksMove[i][j] == false) {
                    // Call sharkStarves
                    sharkStarves(sharks, sharksMove, starve, i, j);

                }
                // else if sharks [i][j] is not empty and the shark has not yet moved
                else if (sharks[i][j] != Config.EMPTY && sharksMove[i][j] == false) {
                    ArrayList<int[]> fishLocations = fishPositions(fish, i, j);

                    // If the fishLocations ArrayList is empty
                    if (fishLocations.size() == 0) {
                        // Fill the arrayList potentialMoves by calling unoccupied positions
                        ArrayList<int[]> potentialMoves = unoccupiedPositions(fish, sharks, i, j);
                        // Fill moveSelected by calling chooseMove
                        int[] moveSelected = chooseMove(potentialMoves, randGen);
                        // If moveSelected is null, call sharkStays
                        if (moveSelected == null) {
                            sharkStays(sharks, sharksMove, starve, i, j);

                        }
                        // else if moveSelected is not null and the shark can breed
                        else if (moveSelected != null && sharks[i][j] >= sharksBreed) {
                            // Call sharkMovesAndBreeds
                            sharkMovesAndBreeds(sharks, sharksMove, starve, i, j, moveSelected[0],
                                moveSelected[1]);

                        } else { // else call sharkMoves
                            sharkMoves(sharks, sharksMove, starve, i, j, moveSelected[0],
                                moveSelected[1]);

                        }
                    } else if (fishLocations.size() > 0) { // else if fishLocations is not empty
                        // Fill huntFish by calling chooseMove
                        int[] huntFish = chooseMove(fishLocations, randGen);
                        // If the shark can breed call sharkEatsFishAndBreeds
                        if (sharks[i][j] >= sharksBreed) {
                            sharkEatsFishAndBreeds(sharks, sharksMove, starve, fish, fishMove, i, j,
                                huntFish[0], huntFish[1]);

                        } else { // Else call sharkEAtsFish
                            sharkEatsFish(sharks, sharksMove, starve, fish, fishMove, i, j,
                                huntFish[0], huntFish[1]);
                        }
                    }
                }
            }
        }
        // Return 0 is successful
        return 0;
    }

    /**
     * This looks up the specified paramName in this Config.SIM_PARAMS array, ignoring case. If
     * found then the array index is returned.
     * 
     * @param paramName The parameter name to look for, ignoring case.
     * @return The index of the parameter name if found, otherwise returns -1.
     */
    public static int indexForParam(String paramName) {
        for (int i = 0; i < Config.SIM_PARAMS.length; i++) {
            if (paramName.equalsIgnoreCase(Config.SIM_PARAMS[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Writes the simulationParameters to the file named filename. The format of the file is the
     * name of the parameter and value on one line separated by =. The order of the lines does not
     * matter. Algorithm: Open the file named filename for writing. Any IOExceptions should be
     * handled with a throws clause and not a try-catch block. For each of the simulation parameters
     * whose names are found in Config.SIM_PARAMS Write out the name of the parameter, =, the
     * parameter value and then newline. Close the file.
     * 
     * Example contents of file: seed=233 ocean_width=20 ocean_height=10 starting_fish=100
     * starting_sharks=10 fish_breed=3 sharks_breed=10 sharks_starve=4
     * 
     * @param simulationParameters The values of the parameters to write out.
     * @param filename The name of the file to write the parameters to.
     */
    public static void saveSimulationParameters(int[] simulationParameters, String filename)
        throws IOException {
        // Create a new file simParameters with name filename
        File simParameters = new File(filename);
        // Open it for writing with PrintWriter
        PrintWriter writeSimParams = new PrintWriter(simParameters);
        // Write the simulation parameters into the file
        writeSimParams.println("seed=" + simulationParameters[indexForParam("seed")]);
        writeSimParams.println("ocean_width=" + simulationParameters[indexForParam("ocean_width")]);
        writeSimParams
            .println("ocean_height=" + simulationParameters[indexForParam("ocean_height")]);
        writeSimParams
            .println("starting_fish=" + simulationParameters[indexForParam("starting_fish")]);
        writeSimParams
            .println("starting_sharks=" + simulationParameters[indexForParam("starting_sharks")]);
        writeSimParams.println("fish_breed=" + simulationParameters[indexForParam("fish_breed")]);
        writeSimParams
            .println("sharks_breed=" + simulationParameters[indexForParam("sharks_breed")]);
        writeSimParams
            .println("sharks_starve=" + simulationParameters[indexForParam("sharks_starve")]);
        // Close the PrintWriter
        writeSimParams.close();
    }


    /**
     * This loads the simulation parameters from the file named filename. The names of the
     * parameters are in the Config.SIM_PARAMS array and the array returned from this method is a
     * parallel array containing the parameter values. The name corresponds to the value with the
     * same index. Algorithm: Try to open filename for reading. If the FileNotFoundException is
     * thrown print the message printing out the filename without < > and return null;
     * 
     * File not found: <filename>
     * 
     * Read lines from the file as long as each line contains "=". As soon as a line does not
     * contain "=" then stop reading from the file. The order of the lines in the file is not
     * significant. In a line the part before "=" is the name and the part after is the value. The
     * separate method you wrote in P7 is helpful here. Find the index of the name within
     * Config.SIM_PARAMS (call indexForParam). If the index is found then convert the value into an
     * int and store in the corresponding index in the array of int that will be returned from this
     * method. If the index is not found then print out the message followed by the entire line
     * without the < >.
     * 
     * Unrecognized: <line>
     * 
     * @param filename The name of the from which to read simulation parameters.
     * @return The array of parameters.
     */
    public static int[] loadSimulationParameters(String filename) {

        int[] params = new int[Config.SIM_PARAMS.length];

        try {
            // Create a new file paramFile
            File paramFile = new File(filename);
            // Create a scanner to read from the file
            Scanner paramScanner = new Scanner(paramFile);


            // For every line in the file
            while (paramScanner.hasNextLine()) {

                // Get the next line in the file
                String paramLine = paramScanner.nextLine();


                if (!paramLine.contains("=")) {
                    // Return null or return the params we've parsed so far?
                    // Returning the existing params we've parsed so far
                    return params;
                }

                // Get the param name and value (parse value string for an int)
                String paramName = paramLine.split("=")[0];
                String paramValueStr = paramLine.split("=")[1];
                int paramValue = Integer.parseInt(paramValueStr);

                // Check to see if the parameter string is a valid string
                if (indexForParam(paramName) == -1) {
                    // return null
                    return null;
                } else {// else find the index of the parameter and set the parameter value to the
                        // corresponding index in params
                    int paramIndex = indexForParam(paramName);
                    params[paramIndex] = paramValue;
                }
            }

            // Close the file
            paramScanner.close();

        } catch (FileNotFoundException e) { // Catch the thrown exception and print out an error
                                            // message then return null
            System.out.println("File not found: " + filename);
            return null;
        }

        // Return the Array params containing the loaded parameters
        return params;
    }

    /**
     * This writes the simulation parameters and the chart of the simulation to a file. If
     * simulationParameters is null or history is null then print an error message and leave the
     * method before any output. If filename cannot be written to then this method should throw an
     * IOException. *
     * 
     * Parameters are written first, 1 per line in the file. Use an = to separate the name from the
     * value. Then write a blank line and then the Population Chart. Example file contents are:
     * seed=111 ocean_width=5 ocean_height=2 starting_fish=6 starting_sharks=2 fish_breed=3
     * sharks_breed=3 sharks_starve=3
     * 
     * Population Chart Numbers of fish(.) and sharks(O) in units of 1. F 6,S 2 1)OO.... F 4,S 2
     * 2)OO.. F 2,S 4 3)..OO F 1,S 4 4).OOO F 0,S 4 5)OOOO
     * 
     * Looking at one line in detail F 6,S 2 1)OO.... ^^^^^^ 6 fish (the larger of sharks or fish is
     * in the background) ^^ 2 sharks ^^^^^ chronon 1 ^^^^ the number of sharks ^^^^ the number of
     * fish
     * 
     * The unit size is determined by dividing the maximum possible number of a creature (oceanWidth
     * * oceanHeight) by Config.POPULATION_CHART_WIDTH. Then iterate through the history printing
     * out the number of fish and sharks. PrintWriter has a printf method that is helpful for
     * formatting. printf("F%3d", 5) prints "F 5", a 5 right justified in a field of size 3.
     * 
     * @param simulationParameters The array of simulation parameter values.
     * @param history Each element in the ArrayList is an array with information about a specific
     *        chronon. The array has 3 elements: chronon, number of fish, and number of sharks, in
     *        that order.
     * @param oceanWidth The width of the ocean.
     * @param oceanHeight The height of the ocean.
     * @param filename The name of the file to write the parameters and chart to.
     */
    public static void savePopulationChart(int[] simulationParameters, ArrayList<int[]> history,
        int oceanWidth, int oceanHeight, String filename) throws IOException {
        // If simulationParameters is null or the history ArrayList is null, print an error message
        // and return
        if (simulationParameters == null || history == null) {
            System.out.println("Error: simulationParameters or history is null");
            return;
        }
        // Create a new file popChart
        File popChart = new File(filename);

        // Open the file for writing
        PrintWriter writePopChart = new PrintWriter(popChart);
        // If you can't write in popChart throw an exception
        if (!popChart.canWrite()) {

            throw new IOException();
        }

        // Write the simulation parameters into the file
        writePopChart.println("seed=" + simulationParameters[indexForParam("seed")]);
        writePopChart.println("ocean_width=" + simulationParameters[indexForParam("ocean_width")]);
        writePopChart
            .println("ocean_height=" + simulationParameters[indexForParam("ocean_height")]);
        writePopChart
            .println("starting_fish=" + simulationParameters[indexForParam("starting_fish")]);
        writePopChart
            .println("starting_sharks=" + simulationParameters[indexForParam("starting_sharks")]);
        writePopChart.println("fish_breed=" + simulationParameters[indexForParam("fish_breed")]);
        writePopChart
            .println("sharks_breed=" + simulationParameters[indexForParam("sharks_breed")]);
        writePopChart
            .println("sharks_starve=" + simulationParameters[indexForParam("sharks_starve")]);
        // Write a blank line
        writePopChart.println();
        // Maximum number of creatures is the oceanWidth * oceanHeight
        int maxNumOfCreature = oceanWidth * oceanHeight;

        // Unit Size is the Max number of creatures divided by the population chart width
        int unitSize = maxNumOfCreature / Config.POPULATION_CHART_WIDTH;
        // If the unitSize is less than one set it equal to one
        if (unitSize == 0) {
            unitSize = 1;
        }
        // Write a header to the Chart and a key
        writePopChart.println("Population Chart");
        writePopChart.println("Numbers of fish(" + Config.FISH_MARK + ") and sharks("
            + Config.SHARK_MARK + ") in units of " + (int) unitSize + ".");

        // For each history
        for (int i = 0; i < history.size(); ++i) {

            int[] historyElement = new int[3];
            historyElement = history.get(i);
            int chronon = historyElement[0];
            int numfish = historyElement[1];
            int numSharks = historyElement[2];


            // Fish in a field of 4, numFish right aligned
            writePopChart.printf("F%3d", numfish);
            writePopChart.print(",");

            // Sharks in a field of 4, numSharks right aligned
            writePopChart.printf("S%3d", numSharks);

            // Chronon in a field of 5, chronon right aligned
            writePopChart.printf(" %4d", chronon);
            writePopChart.print(")");

            int totalMarks = 0;
            // The number of fish and sharks it needs to print
            int numPrintFish = (int) Math.ceil(numfish / unitSize);
            int numPrintSharks = (int) Math.ceil(numSharks / unitSize);
            // if numFish is less than numSharks
            if (numfish < numSharks) {

                String popString = "";
                // for numPrintFish times print the Fish mark
                for (int j = 0; j < numPrintFish; ++j) {
                    popString += Config.FISH_MARK;
                    ++totalMarks;
                }
                // For numPrintSharks times, minus the fish marks printed, print the shark mark
                for (int k = 0; k < (numPrintSharks - totalMarks); ++k) {
                    popString += Config.SHARK_MARK;
                }
                writePopChart.printf("%-50s", popString);

            } else {

                String popString = "";
                // For numPrintSharks times print the shark mark
                for (int k = 0; k < numPrintSharks; ++k) {
                    popString += Config.SHARK_MARK;
                    ++totalMarks;
                }
                // For numPrintFish times, minus the shark marks printed, print the fish mark
                for (int j = 0; j < (numPrintFish - totalMarks); ++j) {
                    popString += Config.FISH_MARK;
                }

                writePopChart.printf("%-50s", popString);

            }
            // Make a new line after each iteration
            writePopChart.println();
        }
        // Close the PrintWriter
        writePopChart.close();
    }
}
