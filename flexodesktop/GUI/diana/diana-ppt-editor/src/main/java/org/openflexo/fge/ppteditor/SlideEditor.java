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
package org.openflexo.fge.ppteditor;

import java.util.logging.Logger;

import javax.swing.JPopupMenu;

import org.apache.poi.hslf.model.Slide;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.control.SwingToolFactory;

public class SlideEditor extends JDianaInteractiveEditor<Slide> {

	private static final Logger logger = Logger.getLogger(SlideEditor.class.getPackage().getName());

	private JPopupMenu contextualMenu;

	// private DrawingTreeNode<?, ?> contextualMenuInvoker;
	// private Point contextualMenuClickedPoint;

	// private Shape copiedShape;

	public SlideEditor(final SlideDrawing aDrawing) {
		super(aDrawing, FGECoreUtils.TOOLS_FACTORY, SwingToolFactory.DEFAULT);

		DrawShapeAction drawShapeAction = new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);
			}
		};

		DrawConnectorAction drawConnectorAction = new DrawConnectorAction() {

			@Override
			public void performedDrawNewConnector(ConnectorGraphicalRepresentation graphicalRepresentation, ShapeNode<?> startNode,
					ShapeNode<?> endNode) {
				System.out.println("OK, perform draw new connector with " + graphicalRepresentation + " start: " + startNode + " end: "
						+ endNode);

			}
		};

		setDrawCustomShapeAction(drawShapeAction);
		setDrawShapeAction(drawShapeAction);
		setDrawConnectorAction(drawConnectorAction);
	}

	@Override
	public SlideDrawing getDrawing() {
		return (SlideDrawing) super.getDrawing();
	}

	public Slide getSlide() {
		return getDrawing().getModel();
	}
}
