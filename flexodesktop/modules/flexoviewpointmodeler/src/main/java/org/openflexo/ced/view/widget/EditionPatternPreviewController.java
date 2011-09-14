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

import java.util.logging.Logger;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionManagingDrawingController;


public class EditionPatternPreviewController extends SelectionManagingDrawingController<EditionPatternPreviewRepresentation> {

	private static final Logger logger = Logger.getLogger(EditionPatternPreviewController.class.getPackage().getName());

	public EditionPatternPreviewController(EditionPattern editionPattern, SelectionManager sm)
	{
		super(new EditionPatternPreviewRepresentation(editionPattern),sm);
	}

	@Override
	public void delete() 
	{
		getDrawing().delete();
		super.delete();
	}
	
	@Override
	public DrawingView<EditionPatternPreviewRepresentation> makeDrawingView(EditionPatternPreviewRepresentation drawing) 
	{
		return new EditionPatterPreviewDrawingView(drawing,this);
	}

	@Override
	public EditionPatterPreviewDrawingView getDrawingView() 
	{
		return (EditionPatterPreviewDrawingView)super.getDrawingView();
	}
	
	public EditionPattern getEditionPattern()
	{
		return getDrawing().getEditionPattern();
	}
	
}
