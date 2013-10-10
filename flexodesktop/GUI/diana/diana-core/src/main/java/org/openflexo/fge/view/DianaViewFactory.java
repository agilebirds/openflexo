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

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.DNDInfo;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.fge.view.widget.ShapePreviewPanel;

/**
 * Represent the view factory for a given technology (eg. Swing)
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components build by this tool factory (eg JComponent for Swing)
 */
public interface DianaViewFactory<F extends DianaViewFactory<F, C>, C> {

	/**
	 * Instantiate a new DrawingView<br>
	 * 
	 * @return
	 */
	public <M> DrawingView<M, ? extends C> makeDrawingView(AbstractDianaEditor<M, F, C> controller);

	/**
	 * Instantiate a new ShapeView for a shape node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ShapeView<O, ? extends C> makeShapeView(ShapeNode<O> shapeNode, AbstractDianaEditor<?, F, C> controller);

	/**
	 * Instantiate a new ConnectorView for a connector node<br>
	 * You might override this method for a custom view managing
	 * 
	 * @param shapeNode
	 * @return
	 */
	public <O> ConnectorView<O, ? extends C> makeConnectorView(ConnectorNode<O> connectorNode, AbstractDianaEditor<?, F, C> controller);

	public FIBBackgroundStyleSelector<? extends C> makeFIBBackgroundStyleSelector(BackgroundStyle backgroundStyle);

	public FIBForegroundStyleSelector<? extends C> makeFIBForegroundStyleSelector(ForegroundStyle foregroundStyle);

	public FIBTextStyleSelector<? extends C> makeFIBTextStyleSelector(TextStyle textStyle);

	public FIBShadowStyleSelector<? extends C> makeFIBShadowStyleSelector(ShadowStyle shadowStyle);

	public FIBShapeSelector<? extends C> makeFIBShapeSelector(ShapeSpecification shapeSpecification);

	public ShapePreviewPanel<? extends C> makeShapePreviewPanel(ShapeSpecification shapeSpecification);

	public DNDInfo makeDNDInfo(MoveAction moveAction, ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller,
			final MouseControlContext initialContext);
}
