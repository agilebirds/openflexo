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
package org.openflexo.fge.geomedit.gr;

import java.awt.Color;

import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.ShowContextualMenuControl;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.Texture.TextureType;
import org.openflexo.xmlcode.XMLSerializable;

public class GeometricObjectGraphicalRepresentation<A extends FGEArea, G extends GeometricObject<A>> extends
		GeometricGraphicalRepresentation<G> implements XMLSerializable {
	// Called for LOAD
	public GeometricObjectGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public GeometricObjectGraphicalRepresentation(G object, GeometricDrawing aDrawing) {
		super(/*object.getGeometricObject()*/null, object, aDrawing);
		setBackground(BackgroundStyle.makeTexturedBackground(TextureType.TEXTURE1, Color.RED, Color.WHITE));
		addToMouseClickControls(new ShowContextualMenuControl());
	}

	@Override
	public String getInspectorName() {
		return getDrawable().getInspectorName();
	}

	/*@Override
	public G getDrawable() {
		// TODO Auto-generated method stub
		return super.getDrawable();
	}*/

	@Override
	public A getGeometricObject() {
		if (getDrawable() != null) {
			return getDrawable().getGeometricObject();
		}
		return null;
	}

	@Override
	public String getText() {
		if (!getDisplayLabel()) {
			return null;
		}
		if (getDrawable() != null) {
			return getDrawable().name;
		}
		return super.getText();
	}

	private boolean displayLabel;

	public boolean getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(boolean aFlag) {
		displayLabel = aFlag;
	}

}