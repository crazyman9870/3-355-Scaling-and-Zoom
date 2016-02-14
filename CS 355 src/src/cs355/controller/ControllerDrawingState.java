package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import cs355.GUIFunctions;
import cs355.model.drawing.Circle;
import cs355.model.drawing.Ellipse;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Square;
import cs355.model.drawing.Triangle;

public class ControllerDrawingState implements IControllerState {

	private double zoom;
	private Point2D.Double mouseDragStart;
	private ArrayList<Point2D> triangleCoordinates;
	
	public ControllerDrawingState(double zoom) {
		this.zoom = zoom;
		this.mouseDragStart = null;
		this.triangleCoordinates = new ArrayList<>();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(Model.instance().getCurrentShape() == Shape.type.TRIANGLE) {
			Point2D.Double point = new Point2D.Double(arg0.getX(), arg0.getY());
			this.triangleCoordinates.add(point);
			
			if (this.triangleCoordinates.size() == 3) 
			{
				Point2D.Double point1 = new Point2D.Double(this.triangleCoordinates.get(0).getX(), this.triangleCoordinates.get(0).getY());
				Point2D.Double point2 = new Point2D.Double(this.triangleCoordinates.get(1).getX(), this.triangleCoordinates.get(1).getY());
				Point2D.Double point3 = new Point2D.Double(this.triangleCoordinates.get(2).getX(), this.triangleCoordinates.get(2).getY());
								
				Point2D.Double center = new Point2D.Double(Controller.instance().calculateCenterTriangle(point1.getX(), point2.getX(), point3.getX()),
						Controller.instance().calculateCenterTriangle(point1.getY(), point2.getY(), point3.getY()));
				
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
			default:
				break;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(Model.instance().getCurrentShape() != Shape.type.TRIANGLE) {
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
	}

	@Override
	public stateType getType() {
		return stateType.DRAWING;
	}
	
	/* Shape Handlers */
	
	private void handleActiveLine(MouseEvent arg0) {
		
		Line line = (Line) Model.instance().getLastShape();
		line.setEnd(new Point2D.Double(arg0.getX(), arg0.getY()));
		
		Model.instance().setLastShape(line);
	}
	
	private void handleActiveSquare(MouseEvent arg0)	{
		
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
	
	private void handleActiveRectangle(MouseEvent arg0) {
		
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
	
	private void handleActiveCircle(MouseEvent arg0) {
		
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
	
	private void handleActiveEllipse(MouseEvent arg0) {
		
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
}