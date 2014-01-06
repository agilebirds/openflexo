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

import java.awt.Point;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControlAction;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.view.FGEView;

public abstract class MouseControlActionImpl<E extends AbstractDianaEditor<?, ?, ?>> implements MouseControlAction<E> {

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context) {
		return true;
	}

	protected Point getPointInView(DrawingTreeNode<?, ?> node, E editor, MouseControlContext context) {
		FGEView<?, ?> view = editor.getDrawingView().viewForNode(node);
		return editor.getDelegate().getPointInView(context.getSource(), context.getPoint(), view);

	}

	protected Point getPointInDrawingView(E editor, MouseControlContext context) {
		return editor.getDelegate().getPointInView(context.getSource(), context.getPoint(), editor.getDrawingView());
	}

}
