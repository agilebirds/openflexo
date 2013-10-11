package org.openflexo.fge.swing.control;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;

/**
 * Implements {@link MouseControlContext} for SWING technology<br>
 * This implementation provides a wrapper of a {@link MouseEvent} object
 * 
 * @author sylvain
 * 
 */
public class JMouseControlContext implements MouseControlContext {

	private MouseEvent event;

	public JMouseControlContext(MouseEvent event) {
		super();
		this.event = event;
	}

	public MouseEvent getMouseEvent() {
		return event;
	}

	@Override
	public MouseButton getButton() {

		/*if (ToolBox.getPLATFORM() == ToolBox.MACOS && event.getButton() == MouseEvent.BUTTON1 && event.isControlDown()) {
			return MouseButton.RIGHT;
		}*/

		switch (event.getButton()) {
		case MouseEvent.BUTTON1:
			return MouseButton.LEFT;
		case MouseEvent.BUTTON2:
			return MouseButton.CENTER;
		case MouseEvent.BUTTON3:
			return MouseButton.RIGHT;
		default:
			return null;
		}
	}

	@Override
	public int getClickCount() {
		return event.getClickCount();
	}

	@Override
	public boolean isConsumed() {
		return event.isConsumed();
	}

	@Override
	public void consume() {
		event.consume();
	}

	@Override
	public boolean isShiftDown() {
		return event.isShiftDown();
	}

	@Override
	public boolean isControlDown() {
		return event.isControlDown();
	}

	@Override
	public boolean isMetaDown() {
		return event.isMetaDown();
	}

	@Override
	public boolean isAltDown() {
		return event.isAltDown();
	}

	@Override
	public Object getSource() {
		return event.getSource();
	}

	@Override
	public Point getPoint() {
		return event.getPoint();
	}

}
