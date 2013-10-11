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
package org.openflexo.fge.control;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * A {@link DrawingPalette} is the abstraction of a palette associated to a drawing<br>
 * A {@link DrawingPalette} is composed of {@link PaletteElement}
 * 
 * @author sylvain
 * 
 */
public class DrawingPalette {

	private static final Logger logger = Logger.getLogger(DrawingPalette.class.getPackage().getName());

	protected List<PaletteElement> elements;

	private final int width;
	private final int height;
	private final String title;

	/**
	 * This factory is the one used to build palettes, NOT THE ONE which is used in the related drawing editor
	 */
	public static FGEModelFactory FACTORY;

	static {
		try {
			FACTORY = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public DrawingPalette(int width, int height, String title) {
		try {
			FACTORY = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		this.width = width;
		this.height = height;
		this.title = title;
		elements = new ArrayList<PaletteElement>();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build palette " + title + " " + Integer.toHexString(hashCode()) + " of " + getClass().getName());
		}
	}

	public void delete() {
		for (PaletteElement element : elements) {
			element.delete();
		}
		elements = null;
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<PaletteElement> getElements() {
		return elements;
	}

	public void addElement(PaletteElement element) {
		elements.add(element);
		// Try to perform some checks and initialization of
		// expecting behaviour for a PaletteElement
		element.getGraphicalRepresentation().setIsFocusable(false);
		element.getGraphicalRepresentation().setIsSelectable(false);
		element.getGraphicalRepresentation().setIsReadOnly(true);
		element.getGraphicalRepresentation().setLocationConstraints(LocationConstraints.UNMOVABLE);
		// element.getGraphicalRepresentation().addToMouseDragControls(mouseDragControl)
	}

	public void removeElement(PaletteElement element) {
		elements.remove(element);
	}

}
