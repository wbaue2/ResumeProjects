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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The SaveButton class is a subclass of the Button class. It is used to save the positions of the
 * furniture currently on the screen to be loaded back later.
 * 
 * @author Will
 *
 */
public class SaveButton extends Button {

    File saveRoom;
    PrintWriter printWriter;

    /**
     * The constructor of the SaveButton class calls the super constructor and sets the label.
     * 
     * @param x
     * @param y
     * @param processing
     */
    public SaveButton(float x, float y, PApplet processing) {
        super(x, y, processing);
        label = "Save room";

    }

    /**
     * mouseDown overrides the mouseDown method of the super class and when called writes the
     * informations of the furniture objects to a file.
     */
    @Override
    public void mouseDown(Furniture[] furniture) {
        boolean mouseOver = isMouseOver();
        // The furniturePosition array, string type, and int rotations are used to store the
        // information of the furniture objects.
        float[] furniturePosition = new float[2];
        String type;
        int rotations;
        if (mouseOver) {
            // Creates a new file to write to
            saveRoom = new File("RoomData.ddd");
            try {
                // Opens the file for writing
                printWriter = new PrintWriter(saveRoom);
            } catch (FileNotFoundException e) {
                System.out.println("WARNING: Could not save room contents to file RoomData.ddd.");
            }
            // Loops through the passed in furniture array
            for (int i = 0; i < furniture.length; ++i) {
                // If the index in the furniture array id not null get the information and write it
                // to the file
                if (furniture[i] != null) {
                    type = furniture[i].getType();
                    furniturePosition = furniture[i].getPosition();
                    rotations = furniture[i].getRotaions();
                    printWriter.println(type + ':' + furniturePosition[0] + ','
                        + furniturePosition[1] + ',' + rotations);
                    printWriter.flush();
                }
            }
        }
        System.out.println("File saved");
    }
}
