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
 * This Interface is used to manage all of the objects that Main uses.
 * 
 * @author Will
 *
 */
public interface DormGUI {
    /**
     * Calls the object's update method.
     */
    public void update();

    /**
     * Calls the object's mouseDown method.
     * 
     * @param furniture
     */
    public void mouseDown(Furniture[] furniture);

    /**
     * Calls the object's mouseUp method.
     */
    public void mouseUp();

    /**
     * Calls the objects isMouseOver method.
     * 
     * @return
     */
    public boolean isMouseOver();
}
