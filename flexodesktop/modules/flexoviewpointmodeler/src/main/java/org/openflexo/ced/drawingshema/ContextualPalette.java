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

import java.util.Vector;
import java.util.logging.Logger;


import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.view.OEShape;
import org.openflexo.foundation.viewpoint.CalcDrawingObject;
import org.openflexo.foundation.viewpoint.CalcPalette;
import org.openflexo.foundation.viewpoint.CalcPaletteElement;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.action.AddCalcDrawingShape;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;
import org.openflexo.localization.FlexoLocalization;

public class ContextualPalette extends DrawingPalette implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ContextualPalette.class.getPackage().getName());

	private CalcPalette _calcPalette;
	
	public ContextualPalette(CalcPalette calcPalette)
	{
		super((int)((DrawingGraphicalRepresentation)calcPalette.getGraphicalRepresentation()).getWidth(),
				(int)((DrawingGraphicalRepresentation)calcPalette.getGraphicalRepresentation()).getHeight(),
				calcPalette.getName());
		
		_calcPalette = calcPalette;
		
		for (CalcPaletteElement element : calcPalette.getElements()) {
			addElement(makePaletteElement(element));
		}
		
		makePalettePanel();
		getPaletteView().revalidate();
		
		calcPalette.addObserver(this);
	}
	
	@Override
	public void update(FlexoObservable observable,
			DataModification dataModification)
	{
		if (observable == _calcPalette) {
			if (dataModification instanceof CalcPaletteElementInserted) {
				logger.info("Notified new Palette Element added");
				CalcPaletteElementInserted dm = (CalcPaletteElementInserted)dataModification;
				ContextualPaletteElement e = makePaletteElement(dm.newValue());
				addElement(e);
				e.getGraphicalRepresentation().notifyObjectHierarchyHasBeenUpdated();
				DrawingView<PaletteDrawing> oldPaletteView = getPaletteView();
				updatePalette();
				getController().updatePalette(_calcPalette,oldPaletteView);
			}
			else if (dataModification instanceof CalcPaletteElementRemoved) {
				logger.info("Notified new Palette Element removed");
				CalcPaletteElementRemoved dm = (CalcPaletteElementRemoved)dataModification;
				ContextualPaletteElement e = getContextualPaletteElement(dm.oldValue());
				removeElement(e);
				DrawingView<PaletteDrawing> oldPaletteView = getPaletteView();
				updatePalette();
				getController().updatePalette(_calcPalette,oldPaletteView);
			}
		}
	}
	
	protected ContextualPaletteElement getContextualPaletteElement(CalcPaletteElement element)
	{
		for (PaletteElement e : elements) {
			if (e instanceof ContextualPaletteElement 
					&& ((ContextualPaletteElement)e).calcPaletteElement == element) 
				return (ContextualPaletteElement)e;
		}
		return null;

	}
	
	@Override
	public CalcDrawingShemaController getController()
	{
		return (CalcDrawingShemaController)super.getController();
	}
	
	private Vector<DropScheme> getAvailableDropSchemes(EditionPattern pattern, GraphicalRepresentation target) 
	{
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (DropScheme dropScheme : pattern.getDropSchemes()) {
			if (dropScheme.isTopTarget() && target instanceof DrawingGraphicalRepresentation) {
				returned.add(dropScheme);
			}
			if (target.getDrawable() instanceof OEShape) {
				OEShape targetShape = (OEShape)target.getDrawable();
				AbstractOntologyObject targetObject = targetShape.getLinkedConcept();
				if (targetObject instanceof OntologyObject && dropScheme.isValidTarget((OntologyObject)targetObject)) {
					returned.add(dropScheme);
				}
			}
		}
		return returned;
	}

	private ContextualPaletteElement makePaletteElement(final CalcPaletteElement element) 
	{
		return new ContextualPaletteElement(element);
	}

	protected class ContextualPaletteElement implements PaletteElement
	{
		private CalcPaletteElement calcPaletteElement;
		private PaletteElementGraphicalRepresentation gr;
		
		public ContextualPaletteElement(CalcPaletteElement aPaletteElement)
		{
			calcPaletteElement = aPaletteElement;
			gr  = new PaletteElementGraphicalRepresentation((ShapeGraphicalRepresentation)calcPaletteElement.getGraphicalRepresentation(),null,getPaletteDrawing());
			gr.setText(aPaletteElement.getName());
			gr.setDrawable(this);
		}
		
		@Override
		public boolean acceptDragging(GraphicalRepresentation target)
		{
			return target instanceof CalcDrawingShemaGR || target instanceof CalcDrawingShapeGR;
		}

		@Override
		public boolean elementDragged(GraphicalRepresentation containerGR, FGEPoint dropLocation)
		{
			if (containerGR.getDrawable() instanceof CalcDrawingObject) {

				CalcDrawingObject container = (CalcDrawingObject)containerGR.getDrawable();

				ShapeGraphicalRepresentation<?> shapeGR = getGraphicalRepresentation().clone();
				shapeGR.setIsSelectable(true);
				shapeGR.setIsFocusable(true);
				shapeGR.setIsReadOnly(false);
				shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
				shapeGR.setLocation(dropLocation);
				shapeGR.setLayer(containerGR.getLayer()+1);
				shapeGR.setAllowToLeaveBounds(true);

				AddCalcDrawingShape action = AddCalcDrawingShape.actionType.makeNewAction(container, null, getController().getCEDController().getEditor());
				action.graphicalRepresentation = shapeGR;
				action.newShapeName = shapeGR.getText();
				if (action.newShapeName == null) action.newShapeName = FlexoLocalization.localizedForKey("shape");
				//action.nameSetToNull = true;
				//action.setNewShapeName(FlexoLocalization.localizedForKey("unnamed"));

				action.doAction();
				return action.hasActionExecutionSucceeded();
			}

			return false;
		}

		@Override
		public PaletteElementGraphicalRepresentation getGraphicalRepresentation()
		{
			return gr;
		}		
		
		@Override
		public DrawingPalette getPalette()
		{
			return ContextualPalette.this;
		}

	}
	
}
