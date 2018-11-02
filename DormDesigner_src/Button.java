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
 * This class is the super class for all of the button classes and contains their important fields
 * and methods to be used by main.
 * 
 * @author Will
 *
 */
public class Button implements DormGUI {

    private static final int WIDTH = 96;
    private static final int HEIGHT = 32;

    protected PApplet processing;
    protected float[] position;
    protected String label;

    /**
     * This is the constructor for the Button Class. It initializes its protected fields.
     * 
     * @param x
     * @param y
     * @param processing
     */
    public Button(float x, float y, PApplet processing) {
        this.processing = processing;
        // Initializes the positions array with the passed in coordinates
        position = new float[] {x, y};
        label = "Button";
    }

    /**
     * The update method redraws the buttons to the screen and changes their color if the mouse is
     * over them.
     */
    public void update() {
        boolean mouseOver = isMouseOver();
        // If the mouse is over the button set its color to dark grey, else set it to light grey
        if (mouseOver) {
            this.processing.fill(100);
        } else {
            this.processing.fill(200);
        }
        // Draws the button onto the display
        this.processing.rect((position[0] - (WIDTH / 2)), (position[1] + (HEIGHT / 2)),
            (position[0] + (WIDTH / 2)), (position[1] - (HEIGHT / 2)));
        // Sets the color of the text in the button and draws it onto the display
        this.processing.fill(0);
        this.processing.text(label, position[0], position[1]);
    }

    /**
     * mouseDown gets overridden by the subclasses' mousDown methods.
     */
    @Override
    public void mouseDown(Furniture[] furniture) {
        // If the mouse is pressed over the button print a message
        if (isMouseOver())
            System.out.println("A button was pressed.");
    }

    /**
     * The mouseUp method is called when the mouse is released.
     */
    @Override
    public void mouseUp() {}

    /**
     * This method determines if the mouse is currently over the button and returns a boolean.
     * 
     */
    public boolean isMouseOver() {
        boolean mouseOver = false;
        float buttonLeftEnd;
        float buttonRightEnd;
        float buttonTop;
        float buttonBottom;
        // Sets the dimensions of the button
        buttonLeftEnd = (this.position[0] - (WIDTH / 2));
        buttonRightEnd = (this.position[0] + (WIDTH / 2));
        buttonTop = (this.position[1] + (HEIGHT / 2));
        buttonBottom = (this.position[1] - (HEIGHT / 2));
        // If the mouse is within the dimensions of the button set mouseOver to be true
        if (buttonLeftEnd <= this.processing.mouseX && buttonRightEnd >= this.processing.mouseX
            && buttonBottom <= this.processing.mouseY && buttonTop >= this.processing.mouseY) {
            mouseOver = true;
        }
        return mouseOver;
    }
}
