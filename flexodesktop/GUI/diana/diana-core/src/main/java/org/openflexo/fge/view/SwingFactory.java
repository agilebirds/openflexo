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

import javax.swing.JComponent;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.view.listener.FGEViewMouseListener;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingFactory implements DianaViewFactory<SwingFactory, JComponent> {

	public SwingFactory() {

	}

	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	@Override
	public <O> FGEViewMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, FGEView<O, ? extends JComponent> view,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new FGEViewMouseListener(node, view);
	}

	/**
	 * Instantiate a new DrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public <M> DrawingView<M> makeDrawingView(AbstractDianaEditor<M, SwingFactory, JComponent> controller) {
		return new DrawingView<M>(controller);
	}

	/**
	 * Instantiate a new ShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new ShapeView<O>(shapeNode, controller);
	}

	/**
	 * Instantiate a new ConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new ConnectorView<O>(connectorNode, controller);
	}

}
