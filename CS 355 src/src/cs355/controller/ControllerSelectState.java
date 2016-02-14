package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import cs355.GUIFunctions;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Triangle;

public class ControllerSelectState implements IControllerState {

	private double zoom;
	private boolean rotating;
	private int currentShapeIndex;
	private Point2D.Double mouseDragStart;
	
	public ControllerSelectState(double zoom) {
		this.zoom = zoom;
		this.rotating = false;
		this.currentShapeIndex = -1;
		this.mouseDragStart = null;
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		if(Model.instance().mousePressedInRotationHandle(new Point2D.Double(arg0.getX(), arg0.getY()), 5))
			rotating = true;
		else {
			this.currentShapeIndex = Model.instance().selectShape(new Point2D.Double(arg0.getX(), arg0.getY()), 5);
			if(currentShapeIndex != -1) {
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(currentShapeIndex != -1) {
			rotating = false;
			this.mouseDragStart=null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(currentShapeIndex != -1) {
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
	public stateType getType() {
		return stateType.SELECT;
	}
	
	/* Transform Functions */
	
	public void handleLineTransformation(MouseEvent arg0) {

		Line line = (Line) Model.instance().getShape(currentShapeIndex);
		if(line.pointNearCenter(new Point2D.Double(arg0.getX(), arg0.getY()), 20)) {
			line.setCenter(new Point2D.Double(arg0.getX(), arg0.getY()));
		}
		else if(line.pointNearEnd(new Point2D.Double(arg0.getX(), arg0.getY()), 20)) {
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
		triangle.setCenter(new Point2D.Double(Controller.instance().calculateCenterTriangle(triangle.getA().getX(), triangle.getB().getX(), triangle.getC().getX()),
				Controller.instance().calculateCenterTriangle(triangle.getA().getY(), triangle.getB().getY(), triangle.getC().getY())));
		
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

	public int getCurrentShapeIndex() {
		return currentShapeIndex;
	}

	public void setCurrentShapeIndex(int currentShapeIndex) {
		this.currentShapeIndex = currentShapeIndex;
	}

	
}
