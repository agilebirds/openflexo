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
package org.openflexo.ced.palette;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;
import org.openflexo.toolbox.ToolBox;


public class PaletteElementGR extends ShapeGraphicalRepresentation<ViewPointPaletteElement> implements GraphicalFlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PaletteElementGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * DO NOT use it
	 */
	public PaletteElementGR()
	{
		super(ShapeType.RECTANGLE, null,null);
	}

	public PaletteElementGR(ViewPointPaletteElement aShape, Drawing<?> aDrawing) 
	{
		super(ShapeType.RECTANGLE, aShape, aDrawing);

		addToMouseClickControls(new CalcPaletteController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
			addToMouseClickControls(new CalcPaletteController.ShowContextualMenuControl(true));
		}

		if (aShape != null) aShape.addObserver(this);

	}

	
	@Override
	public void delete()
	{
		//logger.info("Delete PaletteElementGR");
		if (getDrawable() != null) getDrawable().deleteObserver(this);
		super.delete();
	}


	@Override
	public CalcPaletteRepresentation getDrawing() 
	{
		return (CalcPaletteRepresentation)super.getDrawing();
	}
	
	public ViewPointPaletteElement getCalcPaletteElement()
	{
		return getDrawable();
	}


	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getCalcPaletteElement()) {
			//logger.info("Notified "+dataModification);
			if (dataModification instanceof CalcPaletteElementInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof CalcPaletteElementRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

	@Override
	public boolean getAllowToLeaveBounds()
	{
		return false;
	}
	
	@Override
	public String getText()
	{
		if (getCalcPaletteElement() != null)
			return getCalcPaletteElement().getName();
		return super.getText();
	}

	@Override
	public void setTextNoNotification(String text) 
	{
		if (getCalcPaletteElement() != null)
			getCalcPaletteElement().setName(text);
		else super.setTextNoNotification(text);
	}

	@Override
	public void notifyObservers(Object arg)
	{
		super.notifyObservers(arg);
		if (arg instanceof FGENotification
				&& ((FGENotification)arg).isModelNotification()
				&& getDrawing() != null 
				&& !getDrawing().ignoreNotifications()
				&& getCalcPaletteElement() != null
				&& !getCalcPaletteElement().getPalette().ignoreNotifications())
			getCalcPaletteElement().setChanged();
	}


}
