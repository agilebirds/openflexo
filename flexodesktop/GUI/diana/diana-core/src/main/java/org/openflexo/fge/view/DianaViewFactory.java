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

package org.openflexo.fge.view;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.view.listener.FGEViewMouseListener;

/**
 * Represent the view factory for a given technology (eg. Swing)
 * 
 * @author sylvain
 * 
 * @param <C>
 */
public interface DianaViewFactory<C> {

	/**
	 * Instantiate a new DrawingView<br>
	 * 
	 * @return
	 */
	public <M> DrawingView<M> makeDrawingView(AbstractDianaEditor<?, ?, C> controller);

	/**
	 * Instantiate a new ShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, ?, C> controller);

	/**
	 * Instantiate a new ConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode, AbstractDianaEditor<?, ?, C> controller);

	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	public <O> FGEViewMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, FGEView<O, ? extends C> view,
			AbstractDianaEditor<?, ?, C> controller);

}
