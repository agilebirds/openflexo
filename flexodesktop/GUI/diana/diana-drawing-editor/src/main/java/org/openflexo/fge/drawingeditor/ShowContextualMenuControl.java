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
package org.openflexo.fge.drawingeditor;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.actions.CustomClickControlAction;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.view.FGEView;

public class ShowContextualMenuControl extends MouseClickControlImpl {

	public ShowContextualMenuControl(DiagramFactory factory) {
		super("Show contextual menu", MouseButton.RIGHT, 1, new CustomClickControlAction() {
			@Override
			public boolean handleClick(DrawingTreeNode<?, ?> dtn, DianaInteractiveViewer<?> controller, java.awt.event.MouseEvent event) {
				FGEView view = controller.getDrawingView().viewForNode(dtn);
				Point newPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
				((DianaEditor) controller).showContextualMenu(dtn, view, newPoint);
				return false;
			}
		}, false, false, false, false, factory);
	}
}
