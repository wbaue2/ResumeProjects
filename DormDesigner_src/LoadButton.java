//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: Dorm Designer 3000
// Files: DormDesigner.jar, Main.java, Furniture.java, Button.java, CreateFurnitureButton.java,
//////////////////// SaveButton.java, LoadButton.java, ClearButton.java, DormGUI.java
// Course: CS 300, Summer 2018
//
// Author: William Bauer
// Email: wbauer2@wisc.edu
// Lecturer's Name: Mouna Kacem
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name: None
// Partner Email:
// Partner Lecturer's Name:
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
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons:
// Online Sources:
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * The class LoadButton is a subclass of Button and is used to load the positions of furniture from
 * a file.
 * 
 * @author Will
 *
 */
public class LoadButton extends Button {

    FileReader loadFile;
    BufferedReader loadRoom;
    BufferedReader getLines;
    float furnitureXCoord;
    float furnitureYCoord;
    int furnitureRotations;
    String type;
    int lineNumber;

    /**
     * The loadButton constructor calls it's super constructor and sets the label.
     * 
     * @param x
     * @param y
     * @param processing
     */
    public LoadButton(float x, float y, PApplet processing) {
        super(x, y, processing);
        label = "Load room";
    }

    /**
     * This method overrides the super class' mouseDown and when called loads the positions of the
     * furniture to the furniture array that is passed in.
     */
    @Override
    public void mouseDown(Furniture[] furniture) {
        // This is used to track the number of furniture objects created.
        int furnitureCreated = 0;
        boolean mouseOver = isMouseOver();
        // If the mouse is pressed over the button
        if (mouseOver) {
            // Set all the positions in the furniture array to null.
            for (int i = 0; i < furniture.length; i++) {
                if (furniture[i] != null) {
                    furniture[i] = null;
                }
            }
            try {
                // Opens the file and wraps it in a bufferedReader
                this.loadFile = new FileReader("RoomData.ddd");
                this.getLines = new BufferedReader(loadFile);
                // Calls the countLines method to get the number of lines in the file, the file is
                // closed at the end of this method
                countLines(getLines);
                // The file is reopened in order to read its information.
                this.loadFile = new FileReader("RoomData.ddd");
                this.loadRoom = new BufferedReader(loadFile);

            } catch (FileNotFoundException e) {
                System.out.println("WARNING: Could not load room contents from file RoomData.ddd.");

            } catch (IOException e) {
                System.out.println("WARNING: Could not load room contents from file RoomData.ddd.");

            }
            // If countLines executed successfully
            if (lineNumber != -1) {
                // iterate through the lines of the file
                for (int i = 0; i < lineNumber; i++) {
                    // Calls the getFurnitureInfo helper method to parse a line of the file for
                    // information
                    getFurnitureInfo(loadRoom);
                    // If we have already created 6 furniture objects break the loop
                    if (furnitureCreated >= 6) {
                        System.out.println("WARNING: Unable to load more furniture.");
                        break;
                    } else if (type != null) {
                        // If the type of furniture is one of the accepted values create a new
                        // furniture object
                        if (type.equals("bed") || type.equals("sofa") || type.equals("dresser")
                            || type.equals("desk") || type.equals("sink")) {
                            Furniture newFurniture = new Furniture(type, furnitureXCoord,
                                furnitureYCoord, furnitureRotations, processing);
                            furniture[i] = newFurniture;
                            // Increments the count to keep track of the number of furniture objects
                            // created
                            furnitureCreated++;

                        } else {
                            System.out.println(
                                "WARNING: Could not find an image for a furniture object of type: "
                                    + type);
                        }
                    }

                }
            }
        }
    }

    /**
     * This is a helper method for mouseDown to read through the file and extract the information
     * about the furniture objects
     * 
     * @param loadRoom
     */
    private void getFurnitureInfo(BufferedReader loadRoom) {
        String line = null;
        try {
            // Read the next line from the file
            line = loadRoom.readLine();

        } catch (IOException e) {
            System.out.println("WARNING: Could not load room contents from file RoomData.ddd.");

        }
        // If the line was successfully read
        if (line != null) {
            // Checks to make sure the line is separated by a colon, if not prints an error message
            if (line.split(":").length != 2) {
                System.out.println("WARNING: Found incorrectly formatted line in file: " + line);
                type = null;
            } // Checks to make sure the second half of the line is separated by commas, if not
              // prints and error message
            else if (line.split(":")[1].split(",").length != 3) {
                System.out.println("WARNING: Found incorrectly formatted line in file: " + line);
                type = null;
            } else { // If the line is formatted correctly parse through it and extract the
                     // information
                type = line.split(":")[0].trim().toLowerCase();
                String xCoord = line.split(":")[1].split(",")[0].trim();
                furnitureXCoord = Float.parseFloat(xCoord);
                String yCoord = line.split(":")[1].split(",")[1].trim();
                furnitureYCoord = Float.parseFloat(yCoord);
                String rotations = line.split(":")[1].split(",")[2].trim();
                furnitureRotations = Integer.parseInt(rotations);
            }
        } else {
            type = null;
        }
    }

    /**
     * This method counts the number of lines in the file in order to be able to loop through the
     * correct number of times.
     * 
     * @param getLines
     * @throws IOException
     */
    private void countLines(BufferedReader getLines) throws IOException {
        LineNumberReader reader = null;
        try {
            // Creates a new LineNumberreader object
            reader = new LineNumberReader(getLines);
            // While the line is not null get the line number, the final value of lineNumber will be
            // the number of lines in the file.
            while ((reader.readLine()) != null);
            lineNumber = reader.getLineNumber();
        } catch (Exception ex) { // If it cannot successfully go through the file return -1.
            lineNumber = -1;
        } finally {
            // CLose the reader so it can be reopened at the top of the file
            if (reader != null)
                reader.close();
        }
    }
}
