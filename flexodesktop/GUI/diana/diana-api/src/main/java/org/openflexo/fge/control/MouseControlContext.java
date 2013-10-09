/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.fge.control;

import java.awt.Point;

import org.openflexo.fge.control.MouseControl.MouseButton;

/**
 * This is the abstraction of run-time context of mouse control handling.<br>
 * A possible implementation of this would be for example the {@link MouseEvent} for the Swing Technology Diana Implementation
 * 
 * @author sylvain
 */
public interface MouseControlContext {

	/**
	 * Return a flag indicating if context is still relevant<br>
	 * (Usefull to dismiss consumed events is Swing for example)
	 * 
	 * @return
	 */
	public boolean isConsumed();

	/**
	 * Returns which, if any, of the mouse buttons has changed state.
	 * 
	 * @return
	 */
	public MouseButton getButton();

	public boolean isShiftDown();

	public boolean isControlDown();

	public boolean isMetaDown();

	public boolean isAltDown();

	public int getClickCount();

	public void consume();

	public Object getSource();

	public Point getPoint();

}