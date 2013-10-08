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
package org.openflexo.fge.geomedit;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControlInfo;
import org.openflexo.fge.control.actions.CustomClickControlAction;
import org.openflexo.fge.swing.actions.JCustomMouseClickControl;

public class ShowContextualMenuControl extends JCustomMouseClickControl {

	public ShowContextualMenuControl() {
		super("Show contextual menu", MouseButton.RIGHT, 1, new CustomClickControlAction() {
			@Override
			public boolean handleClick(DrawingTreeNode<?, ?> node, AbstractDianaEditor<?> controller, MouseControlInfo controlInfo) {
				Point newPoint = SwingUtilities.convertPoint((Component) controlInfo.getSource(), controlInfo.getPoint(), controller.getDrawingView());
				((GeomEditController) controller).showContextualMenu(node, newPoint);
				return false;
			}
			
		}, false, false, false, false);
	}

}
