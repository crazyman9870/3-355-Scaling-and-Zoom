package cs355.controller;

import java.awt.event.MouseEvent;

public class ControllerNothingState implements IControllerState {
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public stateType getType() {
		return stateType.NOTHING;
	}

}
