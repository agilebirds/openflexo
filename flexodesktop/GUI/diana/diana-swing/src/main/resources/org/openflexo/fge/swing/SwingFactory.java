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

package org.openflexo.fge.swing;

import javax.swing.JComponent;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.DNDInfo;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.swing.control.JDNDInfo;
import org.openflexo.fge.swing.control.JMouseControlContext;
import org.openflexo.fge.swing.view.FGEViewMouseListener;
import org.openflexo.fge.swing.view.JConnectorView;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.fge.swing.view.JShapeView;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.FGEView;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingFactory implements DianaViewFactory<SwingFactory, JComponent> {

	public static SwingFactory INSTANCE = new SwingFactory();

	private SwingFactory() {
	}

	/**
	 * Build and return a MouseListener for supplied node and view<br>
	 * Here return null as a simple viewer doesn't allow any editing facility
	 * 
	 * @param node
	 * @param view
	 * @return
	 */
	public <O> FGEViewMouseListener makeViewMouseListener(DrawingTreeNode<O, ?> node, FGEView<O, ? extends JComponent> view,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new FGEViewMouseListener(node, view);
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public <M> JDrawingView<M> makeDrawingView(AbstractDianaEditor<M, SwingFactory, JComponent> controller) {
		return new JDrawingView<M>(controller);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new JShapeView<O>(shapeNode, controller);
	}

	/**
	 * Instantiate a new JConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> JConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode,
			AbstractDianaEditor<?, SwingFactory, JComponent> controller) {
		return new JConnectorView<O>(connectorNode, controller);
	}

	@Override
	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			MouseControlContext initialContext) {
		return new JDNDInfo(moveAction, shapeNode, controller, (JMouseControlContext) initialContext);
	}
}
