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
import java.util.ArrayList;

/**
 * This class contains methods utilized by the Dorm Designer application. While running the
 * application calls these methods to allow the user to arrange the furniture in their dorm room.
 */
public class Main {

    private PApplet processing;
    private PImage backgroundImage;
    // An ArrayList that will contain all of the objects that Main uses.
    private ArrayList<DormGUI> guiObjects;
    // Max number of furniture that LoadButton will be allowed to load at once.
    private final static int MAX_LOAD_FURNITURE = 100;


    /**
     * This is the constructor for the main object. It initializes the Main class' private fields
     * each time a main object is created.
     */
    public Main(PApplet processing) {

        this.processing = processing;
        // Sets the color of the application's background
        this.processing.background(95, 158, 160);
        // Loads the background image and draws it centered in the display window
        this.backgroundImage = processing.loadImage("images/background.png");
        this.processing.image(this.backgroundImage, this.processing.width / 2,
            this.processing.height / 2);
        guiObjects = new ArrayList<DormGUI>();

        // These lines create a new instance of a button and add it to the guiObjects ArrayList
        CreateFurnitureButton bedButton = new CreateFurnitureButton("bed", 50, 24, this.processing);
        guiObjects.add(bedButton);

        CreateFurnitureButton sofaButton =
            new CreateFurnitureButton("sofa", 150, 24, this.processing);
        guiObjects.add(sofaButton);

        CreateFurnitureButton dresserButton =
            new CreateFurnitureButton("dresser", 250, 24, this.processing);
        guiObjects.add(dresserButton);

        CreateFurnitureButton deskButton =
            new CreateFurnitureButton("desk", 350, 24, this.processing);
        guiObjects.add(deskButton);

        CreateFurnitureButton sinkButton =
            new CreateFurnitureButton("sink", 450, 24, this.processing);
        guiObjects.add(sinkButton);

        ClearButton clearButton = new ClearButton(550, 24, this.processing);
        guiObjects.add(clearButton);

        SaveButton saveButton = new SaveButton(650, 24, this.processing);
        guiObjects.add(saveButton);

        LoadButton loadButton = new LoadButton(750, 24, this.processing);
        guiObjects.add(loadButton);
        // This iterates through the guiObjects ArrayList and calls the update method for each of
        // the objects it contains
        for (int i = 0; i < guiObjects.size(); ++i) {
            guiObjects.get(i).update();
        }

    }

    /**
     * This method creates a new Furniture[] for the old mouseDown() methods to make use of. It does
     * so by copying all Furniture references from this.guiObjects into a temporary array of size
     * MAX_LOAD_FURNITURE.
     * 
     * @return that array of Furniture references.
     */
    private Furniture[] extractFurnitureFromGUIObjects() {
        Furniture[] furniture = new Furniture[MAX_LOAD_FURNITURE];
        int nextFreeIndex = 0;
        for (int i = 0; i < guiObjects.size() && nextFreeIndex < furniture.length; i++)
            if (guiObjects.get(i) instanceof Furniture)
                furniture[nextFreeIndex++] = (Furniture) guiObjects.get(i);
        return furniture;
    }

    /**
     * This method first removes all Furniture references from this.guiObjects, and then adds back
     * in all of the non-null references from it's parameter.
     * 
     * @param furniture contains the only furniture that will be left in this.guiObjects after this
     *        method is invoked (null references ignored).
     */
    private void replaceFurnitureInGUIObjects(Furniture[] furniture) {
        for (int i = 0; i < guiObjects.size(); i++)
            if (guiObjects.get(i) instanceof Furniture)
                guiObjects.remove(i--);
        for (int i = 0; i < furniture.length; i++)
            if (furniture[i] != null)
                guiObjects.add(furniture[i]);
    }

    /**
     * The update method will redraw our display window each time an event registered occurs. This
     * allows the user to move the furniture around as well as add, delete, or rotate more
     * furniture.
     * 
     */
    public void update() {

        // Redraws the background color and images
        this.processing.background(95, 158, 160);
        this.backgroundImage = this.processing.loadImage("images/background.png");
        this.processing.image(this.backgroundImage, this.processing.width / 2,
            this.processing.height / 2);
        // This iterates through the guiObjects ArrayList and calls the update method for each of
        // the objects it contains
        for (int i = 0; i < guiObjects.size(); ++i) {
            guiObjects.get(i).update();
        }
    }

    /**
     * The Utility class automatically calls mouseDown() whenever the mouse button is pressed down.
     * This method checks to see if the mouse is over any of the furniture or buttons when it is
     * pressed so that the user is able interact with the display.
     */
    public void mouseDown() {
        // Creates the furniture array to be passed into the object's mouseDown methods.
        Furniture[] furniture = extractFurnitureFromGUIObjects();
        // This iterates through the ArrayList backwards in order to grab the piece of furniture
        // that is visually on top if multiple objects are stacked.
        for (int i = (guiObjects.size() - 1); i >= 0; i--) {
            if (guiObjects.get(i).isMouseOver()) {
                guiObjects.get(i).mouseDown(furniture);
                break;
            }
        }

        replaceFurnitureInGUIObjects(furniture);
    }


    /**
     * This method is automatically called by the Utility class when the mouse button is released
     * and calls the mouse up method for a specific object.
     */
    public void mouseUp() {
        // Iterates through the arrayList and calls mouseUp for each of the objects it contains.
        for (int i = 0; i < guiObjects.size(); i++) {
            guiObjects.get(i).mouseUp();
        }
    }

    /**
     * Each time the user presses any key, the keyPressed method will be executed and will check to
     * see if the key pressed matches one of the commands.
     */
    public void keyPressed() {
        // Extracts the furniture array for keyPrressed to use
        Furniture[] furniture = extractFurnitureFromGUIObjects();
        // Checks to see if the key pressed is 'd' or 'D' and if the mouse is over a piece of
        // furniture and if so deletes it
        if (this.processing.key == 'd' || this.processing.key == 'D') {
            for (int i = (furniture.length - 1); i >= 0; i--) {
                if (furniture[i] != null && furniture[i].isMouseOver()) {
                    furniture[i] = null;
                    break;
                }
            }
        }
        // Checks to see if the key pressed is 'r' or 'R' and if the mouse is over a piece of
        // furniture and if so rotates it
        if (this.processing.key == 'r' || this.processing.key == 'R') {
            for (int i = (furniture.length - 1); i >= 0; i--) {
                if (furniture[i] != null && furniture[i].isMouseOver()) {
                    furniture[i].rotate();
                    break;
                }
            }
        }
        replaceFurnitureInGUIObjects(furniture);
    }

    /**
     * The main method initiates the application when the program is run. It creates the display
     * window, sets its dimension, and checks for callback methods.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Utility.startApplication();

    }
}
