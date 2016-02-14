package cs355.model.drawing;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs355.GUIFunctions;

public class Model extends CS355Drawing {

	//Use a singleton so that the model can be accessed by the view when repainting
	private static Model _instance;
	
	private Shape.type currentShape;
	private Color selectedColor;
	private int selectedShapeIndex;
	private ArrayList<Shape> shapes;


	//If the model had not been initialized, it will be.
	public static Model instance() {
		if (_instance == null) 
			_instance = new Model();
		return _instance;
	}
	
	private Model() {
		currentShape = Shape.type.NONE;
		selectedColor = Color.WHITE;
		selectedShapeIndex = -1;
		shapes = new ArrayList<Shape>();
	}
	
	//Notifies the observers
	public void notifyObservers() {
		super.notifyObservers();
	}
	
	public void setColor(Color c) {	
		selectedColor = c;
		if(selectedShapeIndex != -1)
			shapes.get(selectedShapeIndex).setColor(c);
		changeMade();
	}
	
	public Color getColor()	{
		return selectedColor;
	}
		
	public int getSelectedShapeIndex() {
		return selectedShapeIndex;
	}

	public void setSelectedShapeIndex(int selectedShapeIndex) {
		this.selectedShapeIndex = selectedShapeIndex;
		changeMade();
	}
	
	public int selectShape(Point2D.Double pt, double tolerance) {
		for(int i = shapes.size() - 1; i >= 0; i--) {
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());
			Shape s = shapes.get(i);
			if(s.pointInShape(ptCopy, tolerance)) {
				selectedShapeIndex = i;
				selectedColor = s.getColor();
				GUIFunctions.changeSelectedColor(selectedColor);
				changeMade();
				return selectedShapeIndex;
			}
		}
		selectedShapeIndex = -1;
		changeMade();
		return selectedShapeIndex;
	}
	
	public boolean mousePressedInRotationHandle(Point2D.Double pt, double tolerance)
	{
		if(selectedShapeIndex == -1)
			return false;
		
		Shape shape = shapes.get(selectedShapeIndex);
		double height = -1;
		switch(shape.getShapeType())
		{
			case ELLIPSE:
				height = ((Ellipse)shape).getHeight();
				break;
			case RECTANGLE:
				height = ((Rectangle)shape).getHeight();
				break;
			case CIRCLE:
				height = 2*((Circle)shape).getRadius();
				break;
			case SQUARE:
				height = ((Square)shape).getSize();
				break;
			default:
				break;
		}
		if(height!=-1)
		{
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());
			AffineTransform worldToObj = new AffineTransform();
			worldToObj.rotate(-shape.getRotation());
			worldToObj.translate(-shape.getCenter().getX(),-shape.getCenter().getY());
			worldToObj.transform(ptCopy, ptCopy);
			double yDiff = ptCopy.getY()+((height/2) + 9);
			
			double distance = Math.sqrt(Math.pow(ptCopy.getX(), 2) + Math.pow(yDiff, 2));
			return (6>=distance);
		}
		if(shape.getShapeType().equals(Shape.type.TRIANGLE))
		{
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());
			AffineTransform worldToObj = new AffineTransform();
			worldToObj.rotate(-shape.getRotation());
			worldToObj.translate(-shape.getCenter().getX(),-shape.getCenter().getY());
			worldToObj.transform(ptCopy, ptCopy); //transform pt to object coordinates
			
			Triangle triangle = (Triangle)shape;
			double ax = triangle.getA().getX()-triangle.getCenter().getX();
			double bx = triangle.getB().getX()-triangle.getCenter().getX();
			double cx = triangle.getC().getX()-triangle.getCenter().getX();
			
			double ay = triangle.getA().getY()-triangle.getCenter().getY();
			double by = triangle.getB().getY()-triangle.getCenter().getY();
			double cy = triangle.getC().getY()-triangle.getCenter().getY();
			
			double distance = 7;
			if(ay <= by && ay <= cy)
			{
				distance = Math.sqrt(Math.pow(ax-ptCopy.getX(), 2) + Math.pow(ay-ptCopy.getY()-9, 2));
			}
			else if(by <= ay && by <= cy)
			{
				distance = Math.sqrt(Math.pow(bx-ptCopy.getX(), 2) + Math.pow(by-ptCopy.getY()-9, 2));
			}
			else if(cy <= by && cy <= ay)
			{
				distance = Math.sqrt(Math.pow(cx-ptCopy.getX(), 2) + Math.pow(cy-ptCopy.getY()-9, 2));
			}
			return (6>=distance); 
		}
		return false;
	}
	
	@Override
	public Shape getShape(int index) {
		return shapes.get(index);
	}
	
	public void setShapeByIndex(int index, Shape newShape) {
		shapes.remove(index);
		shapes.add(index, newShape);
	}

	@Override
	public int addShape(Shape s) {
		shapes.add(s);
		System.out.println(shapes.size());
		return shapes.size();
	}

	@Override
	public void deleteShape(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		shapes.remove(index);
		selectedShapeIndex = -1;
	}

	@Override
	public void moveToFront(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(s);
		selectedShapeIndex = shapes.size() - 1;
	}

	@Override
	public void moveToBack(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(0, s);
		selectedShapeIndex = 0;
	}

	@Override
	public void moveForward(int index) {
		if(shapes.size() - 1 <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(index + 1, s);
		selectedShapeIndex = index + 1;
	}

	@Override
	public void moveBackward(int index) {
		if(shapes.size() <= index || index <= 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(index - 1, s);
		selectedShapeIndex = index - 1; 
	}

	@Override
	public List<Shape> getShapes() {
		return shapes;
	}

	@Override
	public List<Shape> getShapesReversed() {
		ArrayList<Shape> copy = new ArrayList<>();
		Collections.reverse(copy);
		return null;
	}

	@Override
	public void setShapes(List<Shape> shapes) {
		this.shapes = (ArrayList<Shape>) shapes;
	}

	public Shape.type getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(Shape.type currentMode) {
		this.currentShape = currentMode;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public void setShapes(ArrayList<Shape> shapes) {
		this.shapes = shapes;
	}
	
	public Shape getLastShape()
	{	return shapes.get(shapes.size() - 1);	}
	
	public void setLastShape(Shape newShape) {	
		shapes.remove(shapes.size() - 1);
		shapes.add(newShape);
	}
	
	public void deleteLastShape() {
		shapes.remove(shapes.size()-1);
	}
	
	public void changeMade() {
		setChanged();
		notifyObservers();
	}
}
