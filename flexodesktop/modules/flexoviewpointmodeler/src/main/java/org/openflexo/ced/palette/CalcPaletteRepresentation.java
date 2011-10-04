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

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;


public class CalcPaletteRepresentation extends DefaultDrawing<ViewPointPalette> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(CalcPaletteRepresentation.class.getPackage().getName());
	
	private PaletteGR paletteGR;

	private Hashtable<ViewPointPaletteElement,PaletteElementGR> paletteElementsGR;
	
	private Boolean ignoreNotifications = true;

	private boolean readOnly = false;

	public CalcPaletteRepresentation(ViewPointPalette aPalette, boolean readOnly)
	{
		super(aPalette);
		
		paletteElementsGR = new Hashtable<ViewPointPaletteElement, PaletteElementGR>();
		
		this.readOnly = readOnly;
		//graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		//graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());
		
		aPalette.addObserver(this);
		
		updateGraphicalObjectsHierarchy();

		ignoreNotifications = false;

	}
	
	@Override
	protected void beginUpdateObjectHierarchy()
	{
		ignoreNotifications = true;
		super.beginUpdateObjectHierarchy();
	}
	
	@Override
	protected void endUpdateObjectHierarchy()
	{
		super.endUpdateObjectHierarchy();
		ignoreNotifications = false;
	}
	
	protected boolean ignoreNotifications()
	{
		if (ignoreNotifications == null) return true;
		return ignoreNotifications;
	}
	
	
	@Override
	public void delete()
	{
		if (paletteGR != null) paletteGR.delete();
		if (getPalette() != null) getPalette().deleteObserver(this);
		paletteElementsGR.clear();
	}
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
 		buildGraphicalObjectsHierarchyFor(getPalette());
	}

	private void buildGraphicalObjectsHierarchyFor(ViewPointPalette parent)
	{
		for (ViewPointPaletteElement child : parent.getElements()) {
			addDrawable(child, parent);
		}
	}

	public ViewPointPalette getPalette()
	{
		return getModel();
	}

	@Override
	public PaletteGR getDrawingGraphicalRepresentation()
	{
		if (paletteGR == null) {
			paletteGR = new PaletteGR(this);
		}
		return paletteGR;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		if (aDrawable instanceof ViewPointPaletteElement) {
			ViewPointPaletteElement element = (ViewPointPaletteElement)aDrawable;
			PaletteElementGR returned = paletteElementsGR.get(element);
			if (returned == null) {
				returned = buildGraphicalRepresentation(element);
				paletteElementsGR.put(element, returned);
				return (GraphicalRepresentation<O>)returned;
			}
			return (GraphicalRepresentation<O>)returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}
	
	private PaletteElementGR buildGraphicalRepresentation(ViewPointPaletteElement element)
	{
			if (element.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				PaletteElementGR graphicalRepresentation = new PaletteElementGR(element,this);
				graphicalRepresentation.setsWith(
						(ShapeGraphicalRepresentation)element.getGraphicalRepresentation(),
						GraphicalRepresentation.Parameters.text);
				if (!readOnly) element.setGraphicalRepresentation(graphicalRepresentation);
				return graphicalRepresentation;
			}
			PaletteElementGR graphicalRepresentation = new PaletteElementGR(element,this);
			if (!readOnly) element.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;

			
			
			/*if (element.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				logger.info("TODO: faire ici comme dans CalcDrawingShemaRepresentation");
				PaletteElementGR graphicalRepresentation = new PaletteElementGR(element,this);
				graphicalRepresentation.setsWith(
						(ShapeGraphicalRepresentation)element.getGraphicalRepresentation(),
						GraphicalRepresentation.Parameters.text);
				element.setGraphicalRepresentation(graphicalRepresentation);
				return graphicalRepresentation;
			}*/
	}
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
	}
	
}
