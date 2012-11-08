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
package org.openflexo.fge.connectors;

import org.openflexo.fge.connectors.impl.LineConnectorImpl;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(LineConnectorImpl.class)
@XMLElement(xmlTag = "LineConnector")
public interface LineConnector extends Connector {

	// Property keys

	public static final String LINE_CONNECTOR_TYPE = "lineConnectorType";
	public static final String CP1_RELATIVE_TO_START_OBJECT = "cp1RelativeToStartObject";
	public static final String CP2_RELATIVE_TO_END_OBJECT = "cp2RelativeToEndObject";

	public static enum LineConnectorType {
		CENTER_TO_CENTER, MINIMAL_LENGTH, FUNNY, ADJUSTABLE
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = LINE_CONNECTOR_TYPE)
	@XMLAttribute
	public LineConnectorType getLineConnectorType();

	@Setter(value = LINE_CONNECTOR_TYPE)
	public void setLineConnectorType(LineConnectorType aLineConnectorType);

	@Getter(value = CP1_RELATIVE_TO_START_OBJECT)
	@XMLAttribute
	public FGEPoint _getCp1RelativeToStartObject();

	@Setter(value = CP1_RELATIVE_TO_START_OBJECT)
	public void _setCp1RelativeToStartObject(FGEPoint aPoint);

	@Getter(value = CP2_RELATIVE_TO_END_OBJECT)
	@XMLAttribute
	public FGEPoint _getCp2RelativeToEndObject();

	@Setter(value = CP2_RELATIVE_TO_END_OBJECT)
	public void _setCp2RelativeToEndObject(FGEPoint aPoint);

}
