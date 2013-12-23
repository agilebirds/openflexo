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
package org.openflexo.technologyadapter.diagram.model.dm;

import org.openflexo.foundation.viewpoint.dm.ViewPointDataModification;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;

/**
 * Notify that a new element has been added to palette
 * 
 * @author sguerin
 * 
 */
public class DiagramPaletteElementInserted extends ViewPointDataModification {

	private final DiagramPalette _parent;

	public DiagramPaletteElementInserted(DiagramPaletteElement element, DiagramPalette parent) {
		// super(null, element);
		super("elements", null, element);
		_parent = parent;
	}

	@Override
	public DiagramPaletteElement newValue() {
		return (DiagramPaletteElement) super.newValue();
	}

	public DiagramPalette getParent() {
		return _parent;
	}

}
