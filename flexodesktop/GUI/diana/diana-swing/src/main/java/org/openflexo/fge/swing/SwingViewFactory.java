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
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.DNDInfo;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.control.tools.BackgroundStyleFactory;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.PaletteController;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.swing.control.JDNDInfo;
import org.openflexo.fge.swing.control.JMouseControlContext;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.view.FGEViewMouseListener;
import org.openflexo.fge.swing.view.JConnectorView;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.fge.swing.view.JFGEView;
import org.openflexo.fge.swing.view.JShapeView;
import org.openflexo.fge.swing.widget.JFIBBackgroundStyleSelector;
import org.openflexo.fge.swing.widget.JFIBForegroundStyleSelector;
import org.openflexo.fge.swing.widget.JFIBShadowStyleSelector;
import org.openflexo.fge.swing.widget.JFIBShapeSelector;
import org.openflexo.fge.swing.widget.JFIBTextStyleSelector;
import org.openflexo.fge.swing.widget.JShapePreviewPanel;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingViewFactory implements DianaViewFactory<SwingViewFactory, JComponent> {

	public static SwingViewFactory INSTANCE = new SwingViewFactory();

	protected SwingViewFactory() {
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
			AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		return new FGEViewMouseListener(node, (JFGEView<O, ? extends JComponent>) view);
	}

	/**
	 * Instantiate a new JDrawingView<br>
	 * You might override this method for a custom view managing
	 * 
	 * @return
	 */
	public <M> JDrawingView<M> makeDrawingView(AbstractDianaEditor<M, SwingViewFactory, JComponent> controller) {
		return new JDrawingView<M>(controller);
	}

	/**
	 * Instantiate a new JShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> JShapeView<O> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
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
			AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		return new JConnectorView<O>(connectorNode, controller);
	}

	@Override
	public PaletteController<SwingViewFactory, JComponent> makePaletteController(DianaPalette<?, ?> palette) {
		return new JPaletteController((JDianaPalette) palette);
	}

	@Override
	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			MouseControlContext initialContext) {
		return new JDNDInfo(moveAction, shapeNode, controller, (JMouseControlContext) initialContext);
	}

	@Override
	public JFIBBackgroundStyleSelector makeFIBBackgroundStyleSelector(BackgroundStyleFactory backgroundStyleFactory) {
		return new JFIBBackgroundStyleSelector(backgroundStyleFactory);
	}

	@Override
	public JFIBForegroundStyleSelector makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle) {
		return new JFIBForegroundStyleSelector(foregroundStyle);
	}

	@Override
	public JFIBTextStyleSelector makeFIBTextStyleSelector(TextStyle textStyle) {
		return new JFIBTextStyleSelector(textStyle);
	}

	@Override
	public JFIBShadowStyleSelector makeFIBShadowStyleSelector(ShadowStyle shadowStyle) {
		return new JFIBShadowStyleSelector(shadowStyle);
	}

	@Override
	public JFIBShapeSelector makeFIBShapeSelector(ShapeFactory shapeFactory) {
		return new JFIBShapeSelector(shapeFactory);
	}

	@Override
	public JShapePreviewPanel makeShapePreviewPanel(ShapeSpecification shapeSpecification) {
		return new JShapePreviewPanel(shapeSpecification);
	}
	/*
		public JFGEDrawingGraphics makeDrawingGraphics(RootNode<?> rootNode) {
			return new JFGEDrawingGraphics(rootNode);
		}

		public JFGEShapeGraphics makeShapeGraphics(ShapeNode<?> shapeNode) {
			return new JFGEShapeGraphics(shapeNode);
		}

		public JFGEConnectorGraphics makeConnectorGraphics(ConnectorNode<?> connectorNode) {
			return new JFGEConnectorGraphics(connectorNode);
		}

		public JFGEGeometricGraphics makeGeometricGraphics(GeometricNode<?> geometricNode) {
			return new JFGEGeometricGraphics(geometricNode);
		}

		public JFGEDrawingDecorationGraphics makeDrawingDecorationGraphics(RootNode<?> rootNode) {
			return new JFGEDrawingDecorationGraphics(rootNode);
		}

		public JFGEShapeDecorationGraphics makeShapeDecorationGraphics(ShapeNode<?> shapeNode) {
			return new JFGEShapeDecorationGraphics(shapeNode);
		}

		public JFGESymbolGraphics makeSymbolGraphics(ConnectorNode<?> connectorNode) {
			return new JFGESymbolGraphics(connectorNode);
		}
	*/
}
