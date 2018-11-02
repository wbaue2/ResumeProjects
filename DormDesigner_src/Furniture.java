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
 * This is the class for the furniture object that can be either beds or sofas. Methods in this
 * class allow the user to drag and drop and rotate bed objects in the display window.
 */
public class Furniture implements DormGUI {

    private PApplet processing;
    private PImage image;
    private float[] position;
    private boolean isDragging;
    private int rotations;
    private String furnitureType;

    /**
     * This is the constructor for the furniture class. It initializes the fields of a new furniture
     * object positioned in the center of the display.
     * 
     * @param furnitureType
     * @param processing
     */
    public Furniture(String furnitureType, PApplet processing) {
        this.processing = processing;
        this.isDragging = false;
        this.furnitureType = furnitureType;
        // Loads the image of the object based on the passed in parameter
        this.image = processing.loadImage("images/" + this.furnitureType + ".png");
        // Initializes the position array for this object and gives it coordinates for the middle
        // of the display window.
        this.position = new float[2];
        this.position[0] = this.processing.width / 2;
        this.position[1] = this.processing.height / 2;
        this.rotations = 0;

    }

    /**
     * This overloaded constructor is used by the LoadButton class when reading information from a
     * file and creating new furniture objects with that information.
     * 
     * @param type
     * @param x
     * @param y
     * @param r
     * @param processing
     */
    public Furniture(String type, float x, float y, int r, PApplet processing) {
        this.processing = processing;
        this.isDragging = false;
        this.furnitureType = type;
        this.image = processing.loadImage("images/" + this.furnitureType + ".png");
        this.position = new float[2];
        this.position[0] = x;
        this.position[1] = y;
        this.rotations = r;
    }

    /**
     * Used by the SaveButton class to write this information into a file.
     * 
     * @return
     */
    public String getType() {
        return this.furnitureType;
    }

    /**
     * Used by the SaveButton class to write this information into a file.
     * 
     * @return
     */
    public float[] getPosition() {
        return this.position;
    }

    /**
     * Used by the SaveButton class to write this information into a file.
     * 
     * @return
     */
    public int getRotaions() {
        return this.rotations;
    }

    /**
     * Checks to see if the furniture object is being dragged and redraws the object at its current
     * position
     * 
     */
    public void update() {
        // If the object is being dragged set its coordinates to the coordinates of the mouse
        if (this.isDragging == true) {
            this.position[0] = this.processing.mouseX;
            this.position[1] = this.processing.mouseY;
        }
        // Draws the object on the display window
        this.processing.image(image, position[0], position[1], rotations * PApplet.PI / 2);
    }

    /**
     * Used to start dragging the object, when the mouse is over this object when it is pressed
     * 
     */
    public void mouseDown(Furniture[] furniture) {
        boolean mouseOver = isMouseOver();
        // If the mouse is over the object set isDragging equal to true
        if (mouseOver == true) {
            this.isDragging = true;
        }
    }


    /**
     * Used to indicate that the object is no longer being dragged when the mouse button is released
     * 
     */
    public void mouseUp() {
        this.isDragging = false;
    }

    /**
     * helper method to determine whether the mouse is currently over this object. Returns a
     * boolean, true indicates that the mouse is over the object and false that it is not.
     * 
     * @return
     */
    public boolean isMouseOver() {
        boolean mouseOver = false;
        float bedLeftEnd;
        float bedRightEnd;
        float bedTop;
        float bedBottom;
        // If the number of rotations is even set the object dimensions to correspond with its
        // orientation
        if ((rotations % 2) == 0) {
            bedLeftEnd = (this.position[0] - (this.image.width / 2));
            bedRightEnd = (this.position[0] + (this.image.width / 2));
            bedTop = (this.position[1] + (this.image.height / 2));
            bedBottom = (this.position[1] - (this.image.height / 2));
        } else { // else set it for the other orientation
            bedLeftEnd = (this.position[0] - (this.image.height / 2));
            bedRightEnd = (this.position[0] + (this.image.height / 2));
            bedTop = (this.position[1] + (this.image.width / 2));
            bedBottom = (this.position[1] - (this.image.width / 2));
        }
        // If the mouse is within the object dimensions set mouseOver to true
        if (bedLeftEnd <= this.processing.mouseX && bedRightEnd >= this.processing.mouseX
            && bedBottom <= this.processing.mouseY && bedTop >= this.processing.mouseY) {
            mouseOver = true;
        }
        return mouseOver;
    }

    /**
     * Rotates the object 90 degrees
     * 
     */
    public void rotate() {
        boolean mouseOver = isMouseOver();
        // If the mouse is over the object, rotate the object
        if (mouseOver) {
            rotations++;
        }
    }
}
