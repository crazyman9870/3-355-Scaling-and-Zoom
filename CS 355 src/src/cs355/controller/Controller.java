package cs355.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.model.drawing.*;

public class Controller implements CS355Controller {

	private static Controller _instance;
	
	private int currentShapeIndex;
	private double zoom;
	private boolean rotating;
	private Point2D.Double mouseDragStart;
	private ArrayList<Point2D> triangleCoordinates;
	private Mode controllerMode;

	//If the model had not been initialized, it will be.
	public static Controller instance() {
		if (_instance == null) 
			_instance = new Controller();
		return _instance;
	}
	
	private Controller() {
		this.currentShapeIndex = -1;
		this.zoom = 1.0;
		this.rotating = false;
		this.mouseDragStart = null;
		triangleCoordinates = new ArrayList<>();
		controllerMode = Mode.NONE;
	}
	
	public enum Mode {
		SHAPE, SELECT, NONE
	}
	
	public int getCurrentShapeIndex() {
		return currentShapeIndex;
	}

	public void setCurrentShapeIndex(int currentShapeIndex) {
		this.currentShapeIndex = currentShapeIndex;
	}

	public Mode getControllerMode() {
		return controllerMode;
	}

	public void setControllerMode(Mode controllerMode) {
		this.controllerMode = controllerMode;
	}
	
	/* Mouse Events */

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}
	
	private double calculateCenterTriangle(double coord1, double coord2, double coord3) {
		return ((coord1 + coord2 + coord3) / 3);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		if(controllerMode == Mode.SHAPE) {
				
			if(Model.instance().getCurrentShape() == Shape.type.TRIANGLE) {
				Point2D.Double point = new Point2D.Double(arg0.getX(), arg0.getY());
				this.triangleCoordinates.add(point);
				
				if (this.triangleCoordinates.size() == 3) 
				{
					Point2D.Double point1 = new Point2D.Double(this.triangleCoordinates.get(0).getX(), this.triangleCoordinates.get(0).getY());
					Point2D.Double point2 = new Point2D.Double(this.triangleCoordinates.get(1).getX(), this.triangleCoordinates.get(1).getY());
					Point2D.Double point3 = new Point2D.Double(this.triangleCoordinates.get(2).getX(), this.triangleCoordinates.get(2).getY());
									
					Point2D.Double center = new Point2D.Double(this.calculateCenterTriangle(point1.getX(), point2.getX(), point3.getX()),
							this.calculateCenterTriangle(point1.getY(), point2.getY(), point3.getY()));
					
					Triangle triangle = new Triangle(Model.instance().getColor(), center, point1, point2, point3);
					Model.instance().addShape(triangle);
					this.triangleCoordinates.clear();
					Model.instance().changeMade();
				}
			}
			else {
			
				switch(Model.instance().getCurrentShape()) {
				case LINE:
					Model.instance().addShape(new Line(Model.instance().getColor(), new Point2D.Double(arg0.getX(), arg0.getY()), new Point2D.Double(arg0.getX(), arg0.getY())));
					break;
				case SQUARE: 
					Model.instance().addShape(new Square(Model.instance().getColor(), new Point2D.Double(arg0.getX(), arg0.getY()), 0));
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
					break;
				case RECTANGLE: 
					Model.instance().addShape(new Rectangle(Model.instance().getColor(), new Point2D.Double(arg0.getX(), arg0.getY()),0, 0));
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
					break;
				case CIRCLE: 
					Model.instance().addShape(new Circle(Model.instance().getColor(), new Point2D.Double(arg0.getX(), arg0.getY()),0));
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
					break;
				case ELLIPSE: 
					Model.instance().addShape(new Ellipse(Model.instance().getColor(), new Point2D.Double(arg0.getX(), arg0.getY()),0, 0));
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
					break;
				case TRIANGLE:
					break;
				case NONE:
					break;
				default:
					break;
				}
			}
		}
		if(controllerMode == Mode.SELECT) {
			if(Model.instance().mousePressedInRotationHandle(new Point2D.Double(arg0.getX(), arg0.getY()), 5))
				rotating = true;
			else {
				this.currentShapeIndex = Model.instance().selectShape(new Point2D.Double(arg0.getX(), arg0.getY()), 5);
				if(currentShapeIndex != -1) {
						this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(controllerMode == Mode.SHAPE && Model.instance().getCurrentShape() != Shape.type.TRIANGLE) {
			Shape currentShape = Model.instance().getLastShape();
			
			switch(currentShape.getShapeType()) {
			case LINE:
				handleActiveLine(arg0);
				break;
			case SQUARE:
				handleActiveSquare(arg0);
				break;
			case RECTANGLE:
				handleActiveRectangle(arg0);
				break;
			case CIRCLE:
				handleActiveCircle(arg0);
				break;
			case ELLIPSE:
				handleActiveEllipse(arg0);
				break;
			case TRIANGLE:
				break;
			case NONE:
				break;
			default:
				break;
			}
			GUIFunctions.refresh();
		}
		if(controllerMode == Mode.SELECT && currentShapeIndex != -1) {
			
			if(rotating) {
				rotateShape(currentShapeIndex, arg0);
			}
			else {
				Shape.type type = Model.instance().getShape(currentShapeIndex).getShapeType();
				
				switch(type) {
				case LINE:
					this.handleLineTransformation(arg0);
					break;
				case SQUARE:
				case RECTANGLE:
				case CIRCLE:
				case ELLIPSE:
					this.handleShapeTransformation(arg0);
					break;
				case TRIANGLE:
					this.handleTriangleTransformation(arg0);
					break;
				case NONE:
					break;
				default:
					break;
				}
			}
			GUIFunctions.refresh();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(controllerMode == Mode.SELECT && currentShapeIndex != -1) {
			rotating = false;
			this.mouseDragStart=null;
		}
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
		switchStates(Mode.SHAPE);
	}

	@Override
	public void squareButtonHit() {
		Model.instance().setCurrentShape(Shape.type.SQUARE);
		switchStates(Mode.SHAPE);
	}

	@Override
	public void rectangleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.RECTANGLE);
		switchStates(Mode.SHAPE);
	}

	@Override
	public void circleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.CIRCLE);
		switchStates(Mode.SHAPE);
	}

	@Override
	public void ellipseButtonHit() {
		Model.instance().setCurrentShape(Shape.type.ELLIPSE);
		switchStates(Mode.SHAPE);
	}

	@Override
	public void triangleButtonHit() {
		Model.instance().setCurrentShape(Shape.type.TRIANGLE);
		switchStates(Mode.SHAPE);
	}
	
	@Override
	public void selectButtonHit() {
		switchStates(Mode.SELECT);
	}

	@Override
	public void zoomInButtonHit() {
		zoom = zoom * 2;
	}

	@Override
	public void zoomOutButtonHit() {
		zoom = zoom/2;
	}
	
	public void switchStates(Mode m) {
		this.controllerMode = m;
		this.currentShapeIndex = -1;
		this.mouseDragStart = null;
		this.triangleCoordinates.clear();
		Model.instance().setSelectedShapeIndex(-1);
	}

	/* Shape Handlers */
		
	public void handleActiveLine(MouseEvent arg0) {
		
		Line line = (Line) Model.instance().getLastShape();
		line.setEnd(new Point2D.Double(arg0.getX(), arg0.getY()));
		
		Model.instance().setLastShape(line);
	}
	
	
	public void handleActiveSquare(MouseEvent arg0)	{
		
		Square square = (Square) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(arg0.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = arg0.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the bottom left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = arg0.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}
		}

		//if the cursor is moving above the upper left corner
		if(arg0.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - arg0.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				//change to set center of some sort 
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the upper left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = mouseDragStart.y - arg0.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				square.setSize(newcorner);
			}
		}
		Model.instance().setLastShape(square);
	}
	
	public void handleActiveRectangle(MouseEvent arg0) {
		
		Rectangle rectangle = (Rectangle) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(arg0.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = arg0.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the bottom left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = arg0.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}

		//if the cursor is moving above the upper left corner
		if(arg0.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - arg0.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the upper left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = mouseDragStart.y - arg0.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}
		Model.instance().setLastShape(rectangle);
	}
	
	
	public void handleActiveCircle(MouseEvent arg0) {
		
		Circle circle = (Circle) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(arg0.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = arg0.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the bottom left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = arg0.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}

		//if the cursor is moving above the upper left corner
		if(arg0.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - arg0.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the upper left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = mouseDragStart.y - arg0.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}
		Model.instance().setLastShape(circle);
	}
	
	
	public void handleActiveEllipse(MouseEvent arg0) {
		
		Ellipse ellipse = (Ellipse) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(arg0.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = arg0.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the bottom left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = arg0.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}

		//if the cursor is moving above the upper left corner
		if(arg0.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(arg0.getX() > mouseDragStart.x)
			{
				double lengthX = arg0.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - arg0.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the upper left quad
			if(arg0.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - arg0.getX();
				double lengthY = mouseDragStart.y - arg0.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}
		Model.instance().setLastShape(ellipse);
	}
	
	
	public void handleLineTransformation(MouseEvent arg0) {
		//TODO
		Line line = (Line) Model.instance().getShape(currentShapeIndex);
		if(line.pointNearCenter(new Point2D.Double(arg0.getX(), arg0.getY()), 10)) {
			line.setCenter(new Point2D.Double(arg0.getX(), arg0.getY()));
		}
		else if(line.pointNearEnd(new Point2D.Double(arg0.getX(), arg0.getY()), 10)) {
			line.setEnd(new Point2D.Double(arg0.getX(), arg0.getY()));
		}
		else {
			double changeX = arg0.getX() - mouseDragStart.getX();
			double changeY = arg0.getY() - mouseDragStart.getY();
			
			Point2D.Double center = line.getCenter();
			Point2D.Double end = line.getEnd();

			double trueCenterX = (center.x + end.x) / 2;
			double trueCenterY = (center.y + end.y) / 2;
			
			double centerXdelta = line.getCenter().getX() - trueCenterX;
			double endXdelta = line.getEnd().getX() - trueCenterX;
			double centerYdelta = line.getCenter().getY() - trueCenterY;
			double endYdelta = line.getEnd().getY() - trueCenterY;
			

			
			line.setCenter(new Point2D.Double(mouseDragStart.x + changeX + centerXdelta, mouseDragStart.y + changeY + centerYdelta));
			line.setEnd(new Point2D.Double(mouseDragStart.x + changeX + endXdelta, mouseDragStart.y + changeY + endYdelta));
			Model.instance().setShapeByIndex(currentShapeIndex, line);
		}
	}
	
	public void handleShapeTransformation(MouseEvent arg0) {
		Shape shape = Model.instance().getShape(currentShapeIndex);
		double changeX = arg0.getX() - mouseDragStart.getX();
		double changeY = arg0.getY() - mouseDragStart.getY();
		shape.setCenter(new Point2D.Double(mouseDragStart.x + changeX, mouseDragStart.y + changeY));
		Model.instance().setShapeByIndex(currentShapeIndex, shape);
	}
	
	public void handleTriangleTransformation(MouseEvent arg0) {
		Triangle triangle = (Triangle) Model.instance().getShape(currentShapeIndex);
		double changeX = arg0.getX() - mouseDragStart.getX();
		double changeY = arg0.getY() - mouseDragStart.getY();
		
		double aXdelta = triangle.getA().getX() - triangle.getCenter().getX();
		double bXdelta = triangle.getB().getX() - triangle.getCenter().getX();
		double cXdelta = triangle.getC().getX() - triangle.getCenter().getX();
		double aYdelta = triangle.getA().getY() - triangle.getCenter().getY();
		double bYdelta = triangle.getB().getY() - triangle.getCenter().getY();
		double cYdelta = triangle.getC().getY() - triangle.getCenter().getY();
		
		Point2D.Double newA = new Point2D.Double(mouseDragStart.x + changeX + aXdelta, mouseDragStart.y + changeY + aYdelta);
		Point2D.Double newB = new Point2D.Double(mouseDragStart.x + changeX + bXdelta, mouseDragStart.y + changeY + bYdelta);
		Point2D.Double newC = new Point2D.Double(mouseDragStart.x + changeX + cXdelta, mouseDragStart.y + changeY + cYdelta);

		triangle.setA(newA);
		triangle.setB(newB);
		triangle.setC(newC);
		triangle.setCenter(new Point2D.Double(this.calculateCenterTriangle(triangle.getA().getX(), triangle.getB().getX(), triangle.getC().getX()),
				this.calculateCenterTriangle(triangle.getA().getY(), triangle.getB().getY(), triangle.getC().getY())));
		
		Model.instance().setShapeByIndex(currentShapeIndex, triangle);
	}
	
	public void rotateShape(int index, MouseEvent arg0)
	{ 
		Shape shape = Model.instance().getShape(currentShapeIndex);
		double xdelta = shape.getCenter().getX()-arg0.getX();
		double ydelta = shape.getCenter().getY()-arg0.getY();
		double angle = Math.atan2(ydelta, xdelta) - Math.PI / 2;
		shape.setRotation(angle % (2*Math.PI));
		Model.instance().changeMade();
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
		if(this.currentShapeIndex != -1) {
			Model.instance().moveForward(currentShapeIndex);
			currentShapeIndex = Model.instance().getSelectedShapeIndex();
		}
	}

	@Override
	public void doMoveBackward() {
		if(this.currentShapeIndex != -1) {
			Model.instance().moveBackward(currentShapeIndex);
			currentShapeIndex = Model.instance().getSelectedShapeIndex();
		}
	}

	@Override
	public void doSendToFront() {
		if(this.currentShapeIndex != -1) {
			Model.instance().moveToFront(currentShapeIndex);
			currentShapeIndex = Model.instance().getSelectedShapeIndex();
		}
	}

	@Override
	public void doSendtoBack() {
		if(this.currentShapeIndex != -1) {
			Model.instance().moveToBack(currentShapeIndex);
			currentShapeIndex = Model.instance().getSelectedShapeIndex();
		}
	}
	
	@Override
	public void doDeleteShape() {
		if(this.currentShapeIndex != -1) {
			Model.instance().deleteShape(currentShapeIndex);
			currentShapeIndex = Model.instance().getSelectedShapeIndex();
			Model.instance().changeMade();
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

}
