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
package org.openflexo.ie.view.widget;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TransparentMouseListener implements MouseListener, MouseMotionListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseMotionListeners().length; i++) {
			_target.getMouseMotionListeners()[i].mouseDragged(convertedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseMotionListeners().length; i++) {
			_target.getMouseMotionListeners()[i].mouseMoved(convertedEvent);
		}
	}

	public TransparentMouseListener(Component src, Component target) {
		super();
		_src = src;
		_target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseListeners().length; i++) {
			_target.getMouseListeners()[i].mouseClicked(convertedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseListeners().length; i++) {
			_target.getMouseListeners()[i].mousePressed(convertedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseListeners().length; i++) {
			_target.getMouseListeners()[i].mouseReleased(convertedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseListeners().length; i++) {
			_target.getMouseListeners()[i].mouseEntered(convertedEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		if (_target == null) {
			_target = _src.getParent().getParent();
		}
		MouseEvent convertedEvent = SwingUtilities.convertMouseEvent(_src, arg0, _target);
		for (int i = 0; i < _target.getMouseListeners().length; i++) {
			_target.getMouseListeners()[i].mouseExited(convertedEvent);
		}
	}

	Component _src;

	Component _target;
}
