package cs355.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.controller.IControllerState.stateType;
import cs355.model.drawing.*;

public class Controller implements CS355Controller {

	private static Controller _instance;
	
	private double zoom;
	private boolean rotating;
	private Point2D.Double mouseDragStart;
	IControllerState state;

	//If the model had not been initialized, it will be.
	public static Controller instance() {
		if (_instance == null) 
			_instance = new Controller();
		return _instance;
	}
	
	private Controller() {
		this.zoom = 1.0;
		this.rotating = false;
		this.mouseDragStart = null;
		this.state = new ControllerNothingState();
	}
	
	public double calculateCenterTriangle(double coord1, double coord2, double coord3) {
		return ((coord1 + coord2 + coord3) / 3);
	}

	/* Mouse Events */

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		state.mousePressed(arg0);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		state.mouseDragged(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		state.mouseReleased(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	/* Button Events */
	
	@Override
	public void colorButtonHit(Color c) {
		Model.instance().setColor(c);
		GUIFunctions.changeSelectedColor(c);
	}

	@Override
	public void lineButtonHit() {
		Model.instance().setCurrentShape(Shape.type.LINE);
		this.state = new ControllerDrawingState(zoom);
	}

	@Override
	public void squareButtonHit() {
		Model.instance().setCurrentShape(Shape.type.SQUARE);
		this.state = new ControllerDrawingState(zoom);
	}

	@Override
	public void rectangleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.RECTANGLE);
		this.state = new ControllerDrawingState(zoom);
	}

	@Override
	public void circleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.CIRCLE);
		this.state = new ControllerDrawingState(zoom);
	}

	@Override
	public void ellipseButtonHit() {
		Model.instance().setCurrentShape(Shape.type.ELLIPSE);
		this.state = new ControllerDrawingState(zoom);
	}

	@Override
	public void triangleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.TRIANGLE);
		this.state = new ControllerDrawingState(zoom);
	}
	
	@Override
	public void selectButtonHit() {
		this.state = new ControllerSelectState(zoom);
	}

	@Override
	public void zoomInButtonHit() {
		if(zoom < 4.0) {
			zoom = zoom * 2;
			GUIFunctions.refresh();
		}
	}

	@Override
	public void zoomOutButtonHit() {
		if(zoom > 0.25) {
			zoom = zoom/2;
			GUIFunctions.refresh();
		}
	}
	
	/* Menu Buttons */

	@Override
	public void saveDrawing(File file) {
		Model.instance().save(file);
	}

	@Override
	public void openDrawing(File file) {
		Model.instance().open(file);
		GUIFunctions.refresh();
	}
	

	@Override
	public void doMoveForward() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveForward(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doMoveBackward() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveBackward(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doSendToFront() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveToFront(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doSendtoBack() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveToBack(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doDeleteShape() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().deleteShape(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
				Model.instance().changeMade();
			}
		}
	}
	
	/* Implement Later */
	
	@Override
	public void hScrollbarChanged(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vScrollbarChanged(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openScene(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggle3DModelDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openImage(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveImage(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleBackgroundDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doEdgeDetection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSharpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMedianBlur() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doUniformBlur() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doGrayscale() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doChangeContrast(int contrastAmountNum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doChangeBrightness(int brightnessAmountNum) {
		// TODO Auto-generated method stub

	}
	
	/* Transforms */

	public AffineTransform object_world_view(Shape s) {
		AffineTransform transform = new AffineTransform();

		// World to View
        transform.concatenate(new AffineTransform(zoom, 0, 0, zoom, 0, 0)); //scale
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -256 + 256*(1/zoom), -256 + 256*(1/zoom))); //t

		// Object to World
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, s.getCenter().getX(), s.getCenter().getY()));
		transform.concatenate(new AffineTransform(Math.cos(s.getRotation()), Math.sin(s.getRotation()), -Math.sin(s.getRotation()), Math.cos(s.getRotation()), 0, 0));
		
		return transform;
	}
	
	public AffineTransform view_world_object(Shape s) {
		AffineTransform transform = new AffineTransform();

		// World to object
		transform.concatenate(new AffineTransform(Math.cos(s.getRotation()), -Math.sin(s.getRotation()), Math.sin(s.getRotation()), Math.cos(s.getRotation()), 0.0, 0.0));
		transform.concatenate(new AffineTransform(1.0, 0.0, 0.0, 1.0, -s.getCenter().getX(), -s.getCenter().getY()));
		
		// View to world
        transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -(-256 + 256*(1/zoom)), -(-256 + 256*(1/zoom)))); //t
        transform.concatenate(new AffineTransform(1/zoom, 0, 0, 1/zoom, 0, 0)); //scale
		
		return transform;
	}

}
