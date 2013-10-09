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
package org.openflexo.fge.control.actions;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;

public class MultipleSelectionAction extends MouseClickControlActionImpl {

	@Override
	public boolean handleClick(DrawingTreeNode<?, ?> node, DianaEditor<?> editor, MouseControlContext context) {
		if (editor instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) editor;
			if (node.getGraphicalRepresentation().getIsSelectable()) {
				controller.toggleSelection(node);
				return true;
			}
		}
		return false;
	}

}