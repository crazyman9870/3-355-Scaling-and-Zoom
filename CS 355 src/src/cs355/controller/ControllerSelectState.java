package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cs355.GUIFunctions;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Triangle;

public class ControllerSelectState implements IControllerState {

	private boolean rotating;
	private int currentShapeIndex;
	private Point2D.Double mouseDragStart;
	
	public ControllerSelectState() {
		this.rotating = false;
		this.currentShapeIndex = -1;
		this.mouseDragStart = null;
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
//		Point2D.Double startPoint = new Point2D.Double(arg0.getX(), arg0.getY());
//		AffineTransform viewToWorld = Controller.instance().viewToWorld();
//		viewToWorld.transform(startPoint, startPoint);
		if(Model.instance().mousePressedInRotationHandle(new Point2D.Double(arg0.getX(), arg0.getY()), 5))
			rotating = true;
		else {
			this.currentShapeIndex = Model.instance().selectShape(new Point2D.Double(arg0.getX(), arg0.getY()), 5);
			if(currentShapeIndex != -1) {
					this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
					AffineTransform viewToWorld = Controller.instance().viewToWorld();
					viewToWorld.transform(this.mouseDragStart, this.mouseDragStart);
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
			Point2D.Double movingPoint = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(movingPoint, movingPoint);
			if(rotating) {
				rotateShape(currentShapeIndex, movingPoint);
			}
			else {
				Shape.type type = Model.instance().getShape(currentShapeIndex).getShapeType();

				switch(type) {
				case LINE:
					this.handleLineTransformation(movingPoint);
					break;
				case SQUARE:
				case RECTANGLE:
				case CIRCLE:
				case ELLIPSE:
					this.handleShapeTransformation(movingPoint);
					break;
				case TRIANGLE:
					this.handleTriangleTransformation(movingPoint);
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
	
	public void handleLineTransformation(Point2D.Double pt) {

		Line line = (Line) Model.instance().getShape(currentShapeIndex);
		if(line.pointNearCenter(new Point2D.Double(pt.getX(), pt.getY()), 20)) {
			line.setCenter(new Point2D.Double(pt.getX(), pt.getY()));
		}
		else if(line.pointNearEnd(new Point2D.Double(pt.getX(), pt.getY()), 20)) {
			line.setEnd(new Point2D.Double(pt.getX(), pt.getY()));
		}
		else {
			double changeX = pt.getX() - mouseDragStart.getX();
			double changeY = pt.getY() - mouseDragStart.getY();
			
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
	
	public void handleShapeTransformation(Point2D.Double pt) {
		Shape shape = Model.instance().getShape(currentShapeIndex);
		double changeX = (pt.getX() - mouseDragStart.getX());
		double changeY = (pt.getY() - mouseDragStart.getY());
		shape.setCenter(new Point2D.Double((mouseDragStart.x + changeX), (mouseDragStart.y + changeY)));
		Model.instance().setShapeByIndex(currentShapeIndex, shape);
	}
	
	public void handleTriangleTransformation(Point2D.Double pt) {
		Triangle triangle = (Triangle) Model.instance().getShape(currentShapeIndex);
		double changeX = (pt.getX() - mouseDragStart.getX());
		double changeY = (pt.getY() - mouseDragStart.getY());
		
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
	
	public void rotateShape(int index, Point2D.Double pt)
	{ 
		Shape shape = Model.instance().getShape(currentShapeIndex);
		double xdelta = (shape.getCenter().getX()-pt.getX())/Controller.instance().getZoom();
		double ydelta = (shape.getCenter().getY()-pt.getY())/Controller.instance().getZoom();
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
