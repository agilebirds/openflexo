/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

public class MouseResizer implements MouseListener, MouseMotionListener {

	private static final int RESIZE_ZONE_WIDTH = 3;
	
	private Component component;
	private MouseResizerDelegate delegate;
	private Cursor previousCursor;
	private ResizeMode mode;
	
	private Point pressedPoint;
	private Point lastDraggedPoint;

	private ResizeMode[] modesToUse;
	
	public enum ResizeMode {
		NORTH,SOUTH,WEST,EAST,NORTH_EAST,NORTH_WEST,SOUTH_WEST,SOUTH_EAST, NONE;
	}
	
	public interface MouseResizerDelegate {
		/**
		 * This method is invoked as the mouse is being dragged.
		 * @param deltaX
		 * @param deltaY
		 */
		public void resizeDirectlyBy(int deltaX,int deltaY);
		/**
		 * This method is invoked when the mouse is released.
		 * @param deltaX the total distance on the X-axis performed by the mouse
		 * @param deltaY the total distance on the Y-axis performed by the mouse
		 */
		public void resizeBy(int deltaX,int deltaY);
	}
	
	public MouseResizer(Component component, MouseResizerDelegate delegate) {
		this(component, delegate, (ResizeMode[])null);
	}
	
	public MouseResizer(Component component, MouseResizerDelegate delegate, ResizeMode... modesToUse) {
		this.component = component;
		this.delegate = delegate;
		this.mode = ResizeMode.NONE;
		this.modesToUse = modesToUse;
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing to do
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (mode==ResizeMode.NONE)
			updateCursor(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (mode==ResizeMode.NONE)
			resetCursor();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isWithinResizeZone(e)) {
			pressedPoint = e.getPoint();
			if (isInNorthZone(e))
				mode = ResizeMode.NORTH;
			else if (isInSouthZone(e))
				mode = ResizeMode.SOUTH;
			else if (isInWestZone(e))
				mode = ResizeMode.WEST;
			else if (isInEastZone(e))
				mode = ResizeMode.EAST;
			else if (isInNorthWestZone(e))
				mode = ResizeMode.NORTH_WEST;
			else if (isInNorthEastZone(e))
				mode = ResizeMode.NORTH_EAST;
			else if (isInSouthWestZone(e))
				mode = ResizeMode.SOUTH_WEST;
			else if (isInSouthEastZone(e))
				mode = ResizeMode.SOUTH_EAST;
			e.consume();
		} else {
			mode = ResizeMode.NONE;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mode==ResizeMode.NONE)
			return;
		if (lastDraggedPoint==null)
			notifyResizeDirectlyBy(e.getX()-pressedPoint.x, e.getY()-pressedPoint.y);
		else
			notifyResizeDirectlyBy(e.getX()-lastDraggedPoint.x, e.getY()-lastDraggedPoint.y);
		notifyResizeBy(e.getX()-pressedPoint.x, e.getY()-pressedPoint.y);
		mode = ResizeMode.NONE;
		if (e.getComponent()!=component)
			resetCursor();
		pressedPoint = null;
		lastDraggedPoint = null;
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mode==ResizeMode.NONE)
			return;
		if (lastDraggedPoint==null)
			notifyResizeDirectlyBy(e.getX()-pressedPoint.x, e.getY()-pressedPoint.y);
		else
			notifyResizeDirectlyBy(e.getX()-lastDraggedPoint.x, e.getY()-lastDraggedPoint.y);
		lastDraggedPoint = e.getPoint();
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (mode==ResizeMode.NONE)
			updateCursor(e);
	}
	
	private void notifyResizeDirectlyBy(int deltaX, int deltaY) {
		switch (mode) {
		case NORTH:
			delegate.resizeDirectlyBy(0, -deltaY);
			break;
		case SOUTH:
			delegate.resizeDirectlyBy(0, deltaY);
			break;
		case WEST:
			delegate.resizeDirectlyBy(-deltaX, 0);
			break;
		case EAST:
			delegate.resizeDirectlyBy(deltaX, 0);
			break;
		case NORTH_EAST:
			delegate.resizeDirectlyBy(deltaX, -deltaY);
			break;
		case NORTH_WEST:
			delegate.resizeDirectlyBy(-deltaX, -deltaY);			
			break;
		case SOUTH_EAST:
			delegate.resizeDirectlyBy(deltaX, deltaY);
			break;
		case SOUTH_WEST:
			delegate.resizeDirectlyBy(-deltaX, deltaY);
			break;
		default:
			break;
		}
	}
	
	private void notifyResizeBy(int deltaX, int deltaY) {
		switch (mode) {
		case NORTH:
			delegate.resizeBy(0, -deltaY);
			break;
		case SOUTH:
			delegate.resizeBy(0, deltaY);
			break;
		case WEST:
			delegate.resizeBy(-deltaX, 0);
			break;
		case EAST:
			delegate.resizeBy(deltaX, 0);
			break;
		case NORTH_EAST:
			delegate.resizeBy(deltaX, -deltaY);
			break;
		case NORTH_WEST:
			delegate.resizeBy(-deltaX, -deltaY);			
			break;
		case SOUTH_EAST:
			delegate.resizeBy(deltaX, deltaY);
			break;
		case SOUTH_WEST:
			delegate.resizeBy(-deltaX, deltaY);
			break;
		default:
			break;
		}
	}
	
	private void resetCursor() {
		if (previousCursor!=null && mode==ResizeMode.NONE) {
			setCursorForComponentAndHierarchy(component, previousCursor);
			previousCursor = null;
		}
	}
	
	private void updateCursor(MouseEvent e) {
		if (isWithinResizeZone(e)) {
			previousCursor = Cursor.getDefaultCursor();
			Cursor cursor = null;
			if (isInNorthZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			} else if (isInSouthZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			} else if (isInWestZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			} else if (isInEastZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			} else if (isInNorthWestZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			} else if (isInNorthEastZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			} else if (isInSouthWestZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			} else if (isInSouthEastZone(e)) {
				cursor=Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			}
			if (cursor!=null) {
				setCursorForComponentAndHierarchy(component,cursor);
			}
		} else
			resetCursor();
	}

	private void setCursorForComponentAndHierarchy(Component component2, Cursor cursor) {
		SwingUtilities.getAncestorOfClass(Window.class, component2).setCursor(cursor);
	}

	private boolean isWithinResizeZone(MouseEvent e) {
		return (e.getX()>=-RESIZE_ZONE_WIDTH && e.getX()<=RESIZE_ZONE_WIDTH)
		||(e.getY()>=-RESIZE_ZONE_WIDTH && e.getY()<=RESIZE_ZONE_WIDTH)
		||(e.getX()<=getComponentWidth()+RESIZE_ZONE_WIDTH && e.getX()>=getComponentWidth()-RESIZE_ZONE_WIDTH) 
		||(e.getY()<=getComponentHeight()+RESIZE_ZONE_WIDTH && e.getY()>=getComponentHeight()-RESIZE_ZONE_WIDTH); 
	}

	/**
	 * @return
	 */
	protected int getComponentHeight() {
		return component.getHeight();
	}

	/**
	 * @return
	 */
	protected int getComponentWidth() {
		return component.getWidth();
	}
	
	private boolean isInNorthZone(MouseEvent e) {
		return e.getY()>=-RESIZE_ZONE_WIDTH && e.getY()<=RESIZE_ZONE_WIDTH && e.getX()>RESIZE_ZONE_WIDTH && e.getX()<getComponentWidth()-RESIZE_ZONE_WIDTH && isUsable(ResizeMode.NORTH);
	}
	private boolean isInSouthZone(MouseEvent e) {
		return e.getY()>=getComponentHeight()-RESIZE_ZONE_WIDTH && e.getY()<=getComponentHeight()+RESIZE_ZONE_WIDTH && e.getX()>RESIZE_ZONE_WIDTH && e.getX()<getComponentWidth()-RESIZE_ZONE_WIDTH && isUsable(ResizeMode.SOUTH);
	}
	
	private boolean isInWestZone(MouseEvent e) {
		return e.getX()>=-RESIZE_ZONE_WIDTH && e.getX()<=RESIZE_ZONE_WIDTH && e.getY()>RESIZE_ZONE_WIDTH && e.getY()<getComponentHeight()-RESIZE_ZONE_WIDTH  && isUsable(ResizeMode.WEST);
	}
	private boolean isInEastZone(MouseEvent e) {
		return e.getX()>=getComponentWidth()-RESIZE_ZONE_WIDTH && e.getX()<=getComponentWidth()+RESIZE_ZONE_WIDTH && e.getY()>RESIZE_ZONE_WIDTH && e.getY()<getComponentHeight()-RESIZE_ZONE_WIDTH && isUsable(ResizeMode.EAST);
	}
	
	private boolean isInNorthWestZone(MouseEvent e) {
		return e.getX()>=-RESIZE_ZONE_WIDTH && e.getX()<=RESIZE_ZONE_WIDTH && e.getY()>=-RESIZE_ZONE_WIDTH && e.getY()<=RESIZE_ZONE_WIDTH  && isUsable(ResizeMode.NORTH_WEST); 
	}
	private boolean isInNorthEastZone(MouseEvent e) {
		return e.getX()>=getComponentWidth()-RESIZE_ZONE_WIDTH && e.getX()<=getComponentWidth()+RESIZE_ZONE_WIDTH && e.getY()>=-RESIZE_ZONE_WIDTH && e.getY()<=RESIZE_ZONE_WIDTH  && isUsable(ResizeMode.NORTH_EAST); 
	}
	
	private boolean isInSouthWestZone(MouseEvent e) {
		return e.getY()>=getComponentHeight()-RESIZE_ZONE_WIDTH && e.getY()<=getComponentHeight()+RESIZE_ZONE_WIDTH && e.getX()>=-RESIZE_ZONE_WIDTH && e.getX()<=RESIZE_ZONE_WIDTH  && isUsable(ResizeMode.SOUTH_WEST); 
	}
	private boolean isInSouthEastZone(MouseEvent e) {
		return e.getY()>=getComponentHeight()-RESIZE_ZONE_WIDTH && e.getY()<=getComponentHeight()+RESIZE_ZONE_WIDTH && e.getX()>=getComponentWidth()-RESIZE_ZONE_WIDTH && e.getX()<=getComponentWidth()+RESIZE_ZONE_WIDTH && isUsable(ResizeMode.SOUTH_EAST);
	}

	private boolean isUsable(ResizeMode mode) {
		if (modesToUse==null)
			return true;
		for (int i = 0; i < modesToUse.length; i++) {
			if (mode==modesToUse[i])
				return true;
		}
		return false;
	}
	
	public ResizeMode getMode() {
		return mode;
	}
}
