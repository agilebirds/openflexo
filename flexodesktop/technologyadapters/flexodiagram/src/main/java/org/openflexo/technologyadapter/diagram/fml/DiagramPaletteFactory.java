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
package org.openflexo.technologyadapter.diagram.fml;

import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.model.converter.RelativePathFileConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;

/**
 * Diagram factory<br>
 * Only one instance of this class should be used
 * 
 * @author sylvain
 * 
 */
public class DiagramPaletteFactory extends FGEModelFactoryImpl {

	public DiagramPaletteFactory(DiagramPaletteResource paletteResource) throws ModelDefinitionException {
		super(DiagramPalette.class, DiagramPaletteElement.class);
		addConverter(new RelativePathFileConverter(paletteResource.getFile().getParentFile()));
	}

	public ShapeGraphicalRepresentation makeNewShapeGR(ShapeGraphicalRepresentation aGR) {
		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		returned.setsWith(aGR);
		returned.setIsFocusable(true);
		returned.setIsSelectable(true);
		returned.setIsReadOnly(false);
		returned.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
		return returned;
	}

}
