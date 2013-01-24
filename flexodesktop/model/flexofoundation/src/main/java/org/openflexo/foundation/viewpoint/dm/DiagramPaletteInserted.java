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
package org.openflexo.foundation.viewpoint.dm;

import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPoint;

/**
 * Notify that a new palette has been added
 * 
 * @author sguerin
 * 
 */
public class DiagramPaletteInserted extends ViewPointDataModification {

	private ViewPoint _parent;

	public DiagramPaletteInserted(DiagramPalette palette, ViewPoint parent) {
		super(null, palette);
		_parent = parent;
	}

	@Override
	public DiagramPalette newValue() {
		return (DiagramPalette) super.newValue();
	}

	public ViewPoint getParent() {
		return _parent;
	}

}
