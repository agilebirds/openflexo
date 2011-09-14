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

import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessRepresentation;


public class PortGR extends AbstractNodeGR<FlexoPort> {

	private static final Logger logger = Logger.getLogger(PortGR.class.getPackage().getName());

	private final BackgroundStyle.BackgroundImage background;

	public PortGR(FlexoPort port, ProcessRepresentation aDrawing)
	{
		super(port, ShapeType.SQUARE, aDrawing);
		//setX(getPort().getPosX());
		//setY(getPort().getPosY());
		setWidth(30);
		setHeight(30);

		setLayer(EMBEDDED_ACTIVITY_LAYER);

		//setText(getPort().getName());
		//setAbsoluteTextX(getPort().getNodeLabelPosX());
		//setAbsoluteTextY(getPort().getNodeLabelPosY());
		setIsFloatingLabel(true);

		background = BackgroundStyle.makeImageBackground(WKFIconLibrary.getImageIconForFlexoPort(getPort()));

		//System.out.println("width="+getPort().getImageIcon().getIconWidth()+" height="+getPort().getImageIcon().getIconHeight());

		background.setScaleX(1);
		background.setScaleY(1);
		background.setDeltaX(0);
		background.setDeltaY(0);

		setForeground(ForegroundStyle.makeNone());
		setBackground(background);

		// TODO handle layer !!!
		// if (operatorNode.getParentPetriGraph())
		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		updatePropertiesFromWKFPreferences();

	}

	int getTopBorder()
	{
		return REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX;
	}

	int getBottomBorder()
	{
		return REQUIRED_SPACE_ON_BOTTOM;
	}

	int getLeftBorder()
	{
		return REQUIRED_SPACE_ON_LEFT;
	}

	int getRightBorder()
	{
		return REQUIRED_SPACE_ON_RIGHT;
	}


	@Override
	public void updatePropertiesFromWKFPreferences()
	{
		super.updatePropertiesFromWKFPreferences();
		setBorder(new ShapeBorder(getTopBorder(),getBottomBorder(),getLeftBorder(),getRightBorder()));
		setTextStyle(TextStyle.makeTextStyle(Color.BLACK, getWorkflow()!=null?getWorkflow().getActivityFont(WKFPreferences.getActivityNodeFont()).getFont():WKFPreferences.getActivityNodeFont().getFont()));
		getShadowStyle().setShadowDepth(1);
		getShadowStyle().setShadowBlur(3);
	}

	public FlexoPort getPort()
	{
		return getDrawable();
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultX()
	{
		return getPort().getIndex() * 80 + 20;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultY()
	{
		return 20;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultLabelX()
	{
		return 25;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultLabelY()
	{
		return 0;
	}


}
