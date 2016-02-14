package cs355.solution;

import java.awt.Color;

import cs355.GUIFunctions;
import cs355.controller.Controller;
import cs355.model.drawing.Model;
import cs355.view.View;

/**
 * This is the main class. The program starts here.
 * Make you add code below to initialize your model,
 * view, and controller and give them to the app.
 */
public class CS355 {

	public static final int SCREEN_SIZE = 2048;
	public static final int SCROLL_START = 512;
	public static final int STARTING_POSITION = 1024 - SCROLL_START/2;
	/**
	 * This is where it starts.
	 * @param args = the command line arguments
	 */
	public static void main(String[] args) {

		Controller controller = Controller.instance();
		View view = new View();
		Model.instance().addObserver(view);

		GUIFunctions.createCS355Frame(controller, view);
		GUIFunctions.changeSelectedColor(Color.WHITE);

		GUIFunctions.setHScrollBarMax(SCREEN_SIZE);
		GUIFunctions.setVScrollBarMax(SCREEN_SIZE);

		GUIFunctions.setVScrollBarPosit(STARTING_POSITION);
		GUIFunctions.setHScrollBarPosit(STARTING_POSITION);

		GUIFunctions.setHScrollBarKnob(SCROLL_START);
		GUIFunctions.setVScrollBarKnob(SCROLL_START);
		
		GUIFunctions.refresh();
	}
}
