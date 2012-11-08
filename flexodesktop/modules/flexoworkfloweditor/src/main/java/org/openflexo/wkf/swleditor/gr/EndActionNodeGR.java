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

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class EndActionNodeGR extends AbstractActionNodeGR {

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private ForegroundStyle painterForeground;
	private BackgroundStyle painterBackground;

	public EndActionNodeGR(ActionNode actionNode, SwimmingLaneRepresentation aDrawing, boolean isInPalet) {
		super(actionNode, aDrawing, isInPalet);

		// Important: width is different from height here to avoid connector blinking when editing layout
		// This little difference allows a kind of hysteresis favourizing horizontal layout
		setWidth(31);
		setHeight(30);

		setIsFloatingLabel(true);

		foreground = ForegroundStyleImpl.makeStyle(Color.BLACK);
		foreground.setLineWidth(0.2);
		background = BackgroundStyleImpl.makeColoredBackground(getMainBgColor());
		setForeground(foreground);
		setBackground(background);
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		painterForeground = ForegroundStyleImpl.makeStyle(Color.DARK_GRAY);
		painterForeground.setLineWidth(2.0);
		painterBackground = BackgroundStyleImpl.makeColoredBackground(Color.DARK_GRAY);

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useBackgroundStyle(painterBackground);
				g.fillCircle(0.2, 0.2, 0.6, 0.6);
			}
		});
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultX() {
		return 250;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double _getDefaultY() {
		return (getActionNode().getParentPetriGraph().getIndexForEndNode(getActionNode()) * 50) + DEFAULT_BEGIN_Y_OFFSET;
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
		return getTopBorder() + 40;
	}

}
