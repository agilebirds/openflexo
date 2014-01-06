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
package org.openflexo.fme;

import java.awt.Point;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseClickControlActionImpl;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fme.model.DiagramFactory;

public class ShowContextualMenuControl extends MouseClickControlImpl<DianaDrawingEditor> {

	public ShowContextualMenuControl(DiagramFactory factory) {
		super("Show contextual menu", MouseButton.RIGHT, 1, new MouseClickControlActionImpl<DianaDrawingEditor>() {
			@Override
			public boolean handleClick(DrawingTreeNode<?, ?> dtn, DianaDrawingEditor controller, MouseControlContext context) {
				FGEView view = controller.getDrawingView().viewForNode(dtn);
				Point newPoint = getPointInView(dtn, controller, context);
				((DianaDrawingEditor) controller).showContextualMenu(dtn, view, newPoint);
				return false;
			}
		}, false, false, false, false, factory);
	}
}
