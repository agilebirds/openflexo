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
package org.openflexo.fge.control.tools;

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.view.DrawingView;

public abstract class DrawConnectorToolController<ME> extends ToolController<ME> {

	private static final Logger logger = Logger.getLogger(DrawConnectorToolController.class.getPackage().getName());

	protected ShapeNode<?> startNode = null;
	protected ShapeNode<?> endNode = null;

	private ConnectorGraphicalRepresentation connectorGR;

	private FGEConnectorGraphics graphics;

	public DrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawConnectorAction toolAction) {
		super(controller, toolAction);
	}

	public abstract FGEConnectorGraphics makeGraphics(ForegroundStyle foregroundStyle);

	public FGEConnectorGraphics getGraphics() {
		return graphics;
	}

	@Override
	public DrawConnectorAction getToolAction() {
		return (DrawConnectorAction) super.getToolAction();
	}

	/**
	 * Return the DrawingView of the controller this tool is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getController() != null) {
			return getController().getDrawingView();
		}
		return null;
	}

	protected void startMouseEdition(ME e) {
		super.startMouseEdition(e);
	}

	protected void stopMouseEdition() {
		super.stopMouseEdition();
	}

	public void delete() {
		logger.warning("Please implement deletion for DrawCustomShapeToolController");
		super.delete();
	}

	public void mouseClicked(ME e) {
		System.out.println("mouseClicked() on " + getPoint(e));
	}

}
