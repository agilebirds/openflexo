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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.graphics.BackgroundImageBackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class SelfExecActionNodeGR extends AbstractActionNodeGR {

	private static final Logger logger = Logger.getLogger(SelfExecActionNodeGR.class.getPackage().getName());

	private final ForegroundStyle foreground;
	private final BackgroundImageBackgroundStyle background;

	public SelfExecActionNodeGR(SelfExecutableActionNode actionNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(actionNode, aDrawing, isInPalet);

		setWidth(30);
		setHeight(30);

		// setText(getActionNode().getName());
		setIsFloatingLabel(true);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);

		background = BackgroundStyle.makeImageBackground(WKFIconLibrary.SELF_EXECUTABLE_IMAGE);
		background.setScaleX(1);
		background.setScaleY(1);
		background.setDeltaX(-4);
		background.setDeltaY(-3);

		setForeground(foreground);
		setBackground(background);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		addToMouseClickControls(new ExecutionPetriGraphOpener(), true);

	}

	@Override
	public SelfExecutableActionNode getActionNode() {
		return (SelfExecutableActionNode) super.getActionNode();
	}

	public class ExecutionPetriGraphOpener extends MouseClickControl {

		public ExecutionPetriGraphOpener() {
			super("ExecutionPetriGraphOpener", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					logger.info("Opening Execution petri graph by double-clicking");
					OpenExecutionPetriGraph.actionType.makeNewAction(getActionNode(), null, getDrawing().getEditor()).doAction();
					// getDrawing().updateGraphicalObjectsHierarchy();
					return true;
				}
			}, false, false, false, false);
		}

	}

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
			return getModel().getLabelLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR).getY();
		}
		return getTopBorder() + getHeight() + getTextStyle().getFont().getSize() + 5;
	}

}
