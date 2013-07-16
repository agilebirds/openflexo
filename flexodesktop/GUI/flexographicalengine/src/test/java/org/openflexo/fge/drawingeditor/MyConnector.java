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
package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(MyConnectorImpl.class)
@XMLElement(xmlTag = "MyConnector")
public interface MyConnector extends MyDrawingElement<MyConnector, ConnectorGraphicalRepresentation> {

	public static final String START_SHAPE = "startShape";
	public static final String END_SHAPE = "endShape";

	@Getter(START_SHAPE)
	public MyShape getStartShape();

	@Setter(START_SHAPE)
	public void setStartShape(MyShape startShape);

	@Getter(END_SHAPE)
	public abstract MyShape getEndShape();

	@Setter(END_SHAPE)
	public abstract void setEndShape(MyShape endShape);

}