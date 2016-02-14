package cs355.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;

import cs355.GUIFunctions;
import cs355.model.drawing.*;

public class View implements ViewRefresher {

	@Override
	public void update(Observable arg0, Object arg1) {
		GUIFunctions.refresh();
	}

	@Override
	public void refreshView(Graphics2D g2d) {
		ArrayList<Shape> shapes = (ArrayList<Shape>) Model.instance().getShapes();
		int selectedShapeIndex = Model.instance().getSelectedShapeIndex();
		for(int i = 0; i < shapes.size(); i++) {
			Shape currentShape = shapes.get(i);
			
			//Sets the color for the graphics object
			g2d.setColor(currentShape.getColor());
			
			AffineTransform objToWorld = new AffineTransform();
			// translate, rotate and then sets the transform 
			objToWorld.translate(currentShape.getCenter().getX(), currentShape.getCenter().getY());
			objToWorld.rotate(currentShape.getRotation());
			g2d.setTransform(objToWorld);
			//Draw the object
			g2d.fill(shapeFactory(currentShape, g2d, false)); //Uses the factory to determine the current shape to set the fill.
			g2d.draw(shapeFactory(currentShape, g2d, selectedShapeIndex == i)); //Uses the factory to determine the current shape to draw the image
			g2d.setColor(currentShape.getColor());
		}
	}
	
	//Use a factory to determine what type is being dealt with
	public java.awt.Shape shapeFactory(Shape currentShape, Graphics2D g2d, boolean selected) {
		
		if(currentShape.getShapeType() == Shape.type.LINE) {
			Line line = (Line)currentShape;
			Point2D.Double start = new Point2D.Double(line.getCenter().x, line.getCenter().y);		
			Point2D.Double end = new Point2D.Double(line.getEnd().x, line.getEnd().y);
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
//				if(start.x < end.x && start.y < end.y)
//					g2d.drawRect((int)start.x/100, (int)start.y/100, (int)Math.abs(end.x - start.x), (int)Math.abs(end.y - start.y));
//				else if(start.x < end.x && start.y > end.y)
//					g2d.drawRect((int)start.x/100, (int)(start.y/100 - Math.abs(end.y - start.y) - 5), (int)Math.abs(end.x - start.x), (int)Math.abs(end.y - start.y));
//				else if(start.x > end.x && start.y < end.y)
//					g2d.drawRect((int)(start.x/100 - Math.abs(end.x - start.x) - 5), (int)(start.y/100), (int)Math.abs(end.x - start.x), (int)Math.abs(end.y - start.y));
//				else
//					g2d.drawRect((int)(start.x/100 - Math.abs(end.x - start.x) - 5), (int)(start.y/100 - Math.abs(end.y - start.y) - 5), (int)Math.abs(end.x - start.x), (int)Math.abs(end.y - start.y));
				g2d.drawOval(-6, -6, 11, 11); //center
				g2d.drawOval((int)(line.getEnd().getX()-line.getCenter().getX())-6, (int)(line.getEnd().getY()-line.getCenter().getY())-6, 11, 11); //end
				g2d.setColor(currentShape.getColor());
			}			
			return new Line2D.Double(0, 0, end.x - start.x, end.y - start.y);
		}

		if(currentShape.getShapeType() == Shape.type.SQUARE) {
			//create a Square from Rectangle2D object and return it
			double sideLength = ((Square) currentShape).getSize();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.drawRect((int)-sideLength/2, (int)-sideLength/2, (int)sideLength, (int)sideLength);
				g2d.drawOval(-6, (int)-sideLength/2 - 15, 11, 11);
			}
			return new Rectangle2D.Double(-sideLength/2, -sideLength/2, sideLength, sideLength);
		}
		
		if(currentShape.getShapeType() == Shape.type.RECTANGLE)	{
			//create a Rectangle2D object and return it
			double width = ((Rectangle) currentShape).getWidth();
			double height = ((Rectangle) currentShape).getHeight();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.drawRect((int)-width/2, (int)-height/2, (int)width, (int)height);
				g2d.drawOval(-6, (int)(-height/2 - 15), 11, 11);
			}
			return new Rectangle2D.Double(-width/2, -height/2, width, height);
		}
		
		if(currentShape.getShapeType() == Shape.type.CIRCLE) {
			//create a Circle2D object and return it
			double diameter = ((Circle) currentShape).getRadius() * 2;
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.drawRect((int)-diameter/2, (int)-diameter/2, (int)diameter, (int)diameter);
				g2d.drawOval(-6, (int)-diameter/2 - 15, 11, 11);
				g2d.setColor(currentShape.getColor());
			}
			return new Ellipse2D.Double(-diameter/2, -diameter/2, diameter, diameter);
		}
		
		if(currentShape.getShapeType() == Shape.type.ELLIPSE) {
			//create a Ellipse2D object and return it
			double width = ((Ellipse) currentShape).getWidth();
			double height = ((Ellipse) currentShape).getHeight();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.drawRect((int)-width/2, (int)-height/2, (int)width, (int)height);
				g2d.drawOval(-6, (int)(-height/2 - 15), 11, 11);
				g2d.setColor(currentShape.getColor());
			}
			return new Ellipse2D.Double(-width/2, -height/2, width, height);
		}
		
		if(currentShape.getShapeType() == Shape.type.TRIANGLE) {
			//create a triangle from a Polygon and return it
			int[] x = new int[3];
			int[] y = new int[3];
			
			x[0] = (int) (((Triangle) currentShape).getA().x - ((Triangle) currentShape).getCenter().x);
			x[1] = (int) (((Triangle) currentShape).getB().x - ((Triangle) currentShape).getCenter().x);
			x[2] = (int) (((Triangle) currentShape).getC().x - ((Triangle) currentShape).getCenter().x);
			
			y[0] = (int) (((Triangle) currentShape).getA().y - ((Triangle) currentShape).getCenter().y);
			y[1] = (int) (((Triangle) currentShape).getB().y - ((Triangle) currentShape).getCenter().y);
			y[2] = (int) (((Triangle) currentShape).getC().y - ((Triangle) currentShape).getCenter().y);
			
			Polygon tri = new Polygon();
			tri.addPoint(x[0], y[0]);
			tri.addPoint(x[1], y[1]);
			tri.addPoint(x[2], y[2]);
			
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.draw(tri);
				if(y[0] <= y[1] && y[0] <= y[2])
					g2d.drawOval(x[0]-6, y[0] - 15, 11, 11);
				else if(y[1] <= y[0] && y[1] <= y[2])
					g2d.drawOval(x[1]-6, y[1] - 15, 11, 11);
				else if(y[2] <= y[1] && y[2] <= y[0])
					g2d.drawOval(x[2]-6, y[2] - 15, 11, 11);
			}
			
			return tri;
		}
		
		return null;
	}
}