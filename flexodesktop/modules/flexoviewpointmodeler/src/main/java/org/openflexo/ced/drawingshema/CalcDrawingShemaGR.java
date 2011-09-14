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
package org.openflexo.ced.drawingshema;

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingConnectorInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingConnectorRemoved;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShapeInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShapeRemoved;


public class CalcDrawingShemaGR extends DrawingGraphicalRepresentation<CalcDrawingShema> implements GraphicalFlexoObserver, CalcDrawingShemaConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CalcDrawingShemaGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * DO NOT use it
	 */
	public CalcDrawingShemaGR()
	{
		this(null);
	}

	public CalcDrawingShemaGR(CalcDrawingShemaRepresentation aDrawing) 
	{
		super(aDrawing);

		if (aDrawing != null 
				&& aDrawing.getShema() != null 
				&& aDrawing.getShema().getGraphicalRepresentation() != null) {
			setsWith((GraphicalRepresentation<?>)aDrawing.getShema().getGraphicalRepresentation());
		}

		addToMouseClickControls(new CalcDrawingShemaController.ShowContextualMenuControl());

		if (aDrawing != null 
				&& aDrawing.getShema() != null) { 	
			aDrawing.getShema().setGraphicalRepresentation(this);
			aDrawing.getShema().addObserver(this);
		}

	}


	@Override
	public CalcDrawingShemaRepresentation getDrawing() 
	{
		return (CalcDrawingShemaRepresentation)super.getDrawing();
	}
	
	public CalcDrawingShema getShema()
	{
		if (getDrawing() != null)
			return getDrawing().getShema();
		return null;
	}


	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getShema()) {
			//logger.info("Notified "+dataModification);
			if ((dataModification instanceof CalcDrawingShapeInserted)
					|| (dataModification instanceof CalcDrawingShapeRemoved)
					|| (dataModification instanceof CalcDrawingConnectorInserted)
					|| (dataModification instanceof CalcDrawingConnectorRemoved)) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

	@Override
	public void notifyObservers(Object arg)
	{
		super.notifyObservers(arg);
		if (arg instanceof FGENotification
				&& ((FGENotification)arg).isModelNotification()
				&& getDrawing() != null 
				&& !getDrawing().ignoreNotifications()
				&& getShema() != null
				&& !getShema().ignoreNotifications())
			getShema().setChanged();
	}
	
	
}
