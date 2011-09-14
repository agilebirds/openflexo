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
package org.openflexo.ced.view.widget;

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.ced.view.widget.EditionPatternPreviewRepresentation.ConnectorToArtifact;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;


public class EditionPatternConnectorToArtefactGR extends ShapeGraphicalRepresentation<ConnectorToArtifact> implements GraphicalFlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatternConnectorToArtefactGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * DO NOT use it
	 */
	public EditionPatternConnectorToArtefactGR()
	{
		this(null,null);
	}

	public EditionPatternConnectorToArtefactGR(ConnectorToArtifact anArtefact, EditionPatternPreviewRepresentation aDrawing) 
	{
		super(ShapeType.CIRCLE, anArtefact, aDrawing);

		setX(350);
		setY(80);
		
		setWidth(20);
		setHeight(20);

		/*addToMouseClickControls(new CalcPaletteController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
			addToMouseClickControls(new CalcPaletteController.ShowContextualMenuControl(true));
		}*/
		//addToMouseDragControls(new DrawEdgeControl());

		//if (anArtefact != null) anArtefact.addObserver(this);

		setForeground(ForegroundStyle.makeStyle(new Color(255,204,0)));
		setBackground(BackgroundStyle.makeColoredBackground(new Color(255,255,204)));
		
		setIsFocusable(true);
		setIsSelectable(false);

	}


	@Override
	public EditionPatternPreviewRepresentation getDrawing() 
	{
		return (EditionPatternPreviewRepresentation)super.getDrawing();
	}
	
	public ConnectorToArtifact getConnectorFromArtifact()
	{
		return getDrawable();
	}


	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		/*if (observable == getConnectorFromArtifact()) {
			//logger.info("Notified "+dataModification);
			if (dataModification instanceof ShapeInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof ShapeDeleted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof ConnectorInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}*/
	}

	/*@Override
	public String getText()
	{
		if (getCalcPaletteElement() != null)
			return getCalcPaletteElement().getName();
		return null;
	}

	@Override
	public void setTextNoNotification(String text) 
	{
		if (getCalcPaletteElement() != null)
			getCalcPaletteElement().setName(text);
	}*/


}
