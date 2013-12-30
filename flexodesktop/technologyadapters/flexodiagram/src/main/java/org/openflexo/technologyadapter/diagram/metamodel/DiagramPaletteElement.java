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
package org.openflexo.technologyadapter.diagram.metamodel;

import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.NameChanged;
import org.openflexo.technologyadapter.diagram.fml.DiagramPaletteFactory;
import org.openflexo.technologyadapter.diagram.fml.DiagramPaletteObject;

public class DiagramPaletteElement extends DiagramPaletteObject {

	private static final Logger logger = Logger.getLogger(DiagramPaletteElement.class.getPackage().getName());

	// Represent graphical representation to be used as representation in the palette
	private ShapeGraphicalRepresentation graphicalRepresentation;

	private final DiagramPaletteElement parent = null;

	private String name;
	private DiagramPalette _palette;

	public DiagramPaletteElement() {
		super();
	}

	@Override
	public FlexoServiceManager getServiceManager() {
		return getPalette().getServiceManager();
	}

	public DiagramPaletteFactory getFactory() {
		return _palette.getFactory();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (requireChange(this.name, name)) {
			String oldName = this.name;
			this.name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName, name));
		}
	}

	public String getURI() {
		return getPalette().getURI() + "/" + getName();
	}

	@Override
	public DiagramSpecification getDiagramSpecification() {
		if (getPalette() != null) {
			return getPalette().getDiagramSpecification();
		}
		return null;
	}

	@Override
	public DiagramPalette getPalette() {
		return _palette;
	}

	public void setPalette(DiagramPalette palette) {
		_palette = palette;
	}

	public DiagramPaletteElement getParent() {
		return parent;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getPalette() != null) {
			getPalette().setIsModified();
		}
	}

	@Override
	public boolean delete() {
		if (getPalette() != null) {
			getPalette().removeFromElements(this);
		}
		super.delete();
		deleteObservers();
		return true;
	}

	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

}
