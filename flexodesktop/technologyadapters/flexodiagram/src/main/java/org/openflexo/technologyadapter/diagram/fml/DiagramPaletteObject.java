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

import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;

public abstract class DiagramPaletteObject extends NamedViewPointObject {

	public DiagramPaletteObject(DiagramPaletteBuilder builder) {
		super(builder);
	}

	public abstract DiagramPalette getPalette();

	public abstract DiagramSpecification getVirtualModel();

	@Override
	public final ViewPoint getViewPoint() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getViewPoint();
		}
		return null;
	}

	public static class DiagramPaletteBuilder {
		public DiagramSpecification diagramSpecification;
		public DiagramPalette diagramPalette;
		public DiagramPaletteResource resource;
		private DiagramPaletteFactory factory;

		public DiagramPaletteBuilder(DiagramSpecification diagramSpecification, DiagramPaletteResource resource,
				DiagramPaletteFactory factory) {
			this.diagramSpecification = diagramSpecification;
			this.resource = resource;
			this.factory = factory;
		}

		public DiagramPaletteFactory getFactory() {
			return factory;
		}

	}

}
