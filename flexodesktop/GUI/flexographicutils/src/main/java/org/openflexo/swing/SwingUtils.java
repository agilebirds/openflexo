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
import java.awt.Container;

import javax.swing.SwingUtilities;

public class SwingUtils {

	public static String getComponentPath(Component c) {
		StringBuffer sb = new StringBuffer();
		Component current = c;
		boolean isFirst = true;
		while (current.getParent() != null) {
			sb.insert(0, (isFirst ? "" : ".") + current.getClass().getSimpleName());
			isFirst = false;
			current = current.getParent();
		}
		return sb.toString();
	}

	/**
	 * Use appropriately {@link SwingUtilities#isDescendingFrom(Component, Component)} which is strictly equivalent
	 * 
	 * @param c
	 * @param container
	 * @return
	 */
	@Deprecated
	public static boolean isComponentContainedInContainer(Component c, Container container) {
		return SwingUtilities.isDescendingFrom(c, container);
	}

}
