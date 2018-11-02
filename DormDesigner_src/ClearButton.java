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
/**
 * This class which extends Button overrides the mouseDown method in order to clear all of the
 * furniture objects when it is pressed by the user.
 * 
 * @author Will
 *
 */
public class ClearButton extends Button {

    public ClearButton(float x, float y, PApplet processing) {
        super(x, y, processing);
        label = "Clear room";
    }

    /**
     * When the mouse is pressed over this button it clears all of the furniture objects from the
     * passed in array.
     */
    @Override
    public void mouseDown(Furniture[] furniture) {
        boolean mouseOver = isMouseOver();
        // If the mouse is over the button when it is pressed iterate through the array and replace
        // all of the references to the furniture objects with null.
        if (mouseOver) {
            System.out.println("Room cleared");
            for (int i = 0; i < furniture.length; ++i) {
                furniture[i] = null;
            }
        }
    }
}
