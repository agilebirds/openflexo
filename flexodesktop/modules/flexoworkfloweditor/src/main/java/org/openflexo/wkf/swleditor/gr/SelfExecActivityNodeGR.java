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
package org.openflexo.wkf.swleditor.gr;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomClickControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundImage;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class SelfExecActivityNodeGR extends AbstractActivityNodeGR<SelfExecutableActivityNode> {

	private static final Logger logger = Logger.getLogger(SelfExecActivityNodeGR.class.getPackage().getName());

	private final ForegroundStyle foreground;
	private final BackgroundImage background;

	public SelfExecActivityNodeGR(SelfExecutableActivityNode activityNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(activityNode, ShapeType.CIRCLE, aDrawing, isInPalet);

		setWidth(30);
		setHeight(30);

		// setText(getActivityNode().getName());
		setIsFloatingLabel(true);

		foreground = ForegroundStyle.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);

		background = BackgroundStyle.makeImageBackground(WKFIconLibrary.SELF_EXECUTABLE_IMAGE);
		background.setScaleX(1);
		background.setScaleY(1);
		background.setDeltaX(-4);
		background.setDeltaY(-3);

		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);

		setForeground(foreground);
		setBackground(background);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		addToMouseClickControls(new ExecutionPetriGraphOpener(), true);
	}

	public SelfExecutableActivityNode getActivityNode() {
		return getDrawable();
	}

	@Override
	public Circle getShape() {
		return (Circle) super.getShape();
	}

	public class ExecutionPetriGraphOpener extends MouseClickControl {

		public ExecutionPetriGraphOpener() {
			super("ExecutionPetriGraphOpener", MouseButton.LEFT, 2, new CustomClickControlAction() {
				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						java.awt.event.MouseEvent event) {
					logger.info("Opening Execution petri graph by double-clicking");
					OpenExecutionPetriGraph.actionType.makeNewAction(getAbstractActivityNode(), null, getDrawing().getEditor()).doAction();
					getDrawing().updateGraphicalObjectsHierarchy();
					return true;
				}
			}, false, false, false, false);
		}

	}

	@Override
	public double getDefaultLabelX() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getX();
		}
		return getLeftBorder() + 15;
	}

	@Override
	public double getDefaultLabelY() {
		if (getModel().hasLabelLocationForContext(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
			return getModel().getLabelLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR).getY();
		}
		return getTopBorder() + getHeight() + getTextStyle().getFont().getSize() + 5;
	}

}
