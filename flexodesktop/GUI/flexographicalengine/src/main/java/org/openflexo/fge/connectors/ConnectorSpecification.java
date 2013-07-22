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

import javax.swing.ImageIcon;

import org.openflexo.fge.FGEObject;
import org.openflexo.fge.connectors.impl.ConnectorSpecificationImpl;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * This is the specification of a Connector<br>
 * Contains all the properties required to manage a Connector
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(ConnectorSpecificationImpl.class)
@Imports({ @Import(LineConnectorSpecification.class), @Import(CurveConnectorSpecification.class),
		@Import(RectPolylinConnectorSpecification.class), @Import(CurvedPolylinConnectorSpecification.class) })
public interface ConnectorSpecification extends FGEObject {

	public static enum ConnectorType {
		LINE,
		// RECT_LINE,
		RECT_POLYLIN,
		CURVE,
		CURVED_POLYLIN,
		CUSTOM;

		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return org.openflexo.fge.FGEIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			} else if (this == CURVE) {
				return org.openflexo.fge.FGEIconLibrary.CURVE_CONNECTOR_ICON;
			} else if (this == LINE) {
				return org.openflexo.fge.FGEIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

	}

	public ConnectorType getConnectorType();

	public ConnectorSpecification clone();

}
