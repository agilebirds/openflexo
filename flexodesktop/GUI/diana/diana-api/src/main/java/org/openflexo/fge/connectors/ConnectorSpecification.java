/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * This is the specification of a Connector<br>
 * Contains all the properties required to manage a Connector
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(LineConnectorSpecification.class), @Import(CurveConnectorSpecification.class),
		@Import(RectPolylinConnectorSpecification.class), @Import(CurvedPolylinConnectorSpecification.class) })
public interface ConnectorSpecification extends FGEObject {

	@PropertyIdentifier(type = StartSymbolType.class)
	public static final String START_SYMBOL_KEY = "startSymbol";
	@PropertyIdentifier(type = EndSymbolType.class)
	public static final String END_SYMBOL_KEY = "endSymbol";
	@PropertyIdentifier(type = MiddleSymbolType.class)
	public static final String MIDDLE_SYMBOL_KEY = "middleSymbol";
	@PropertyIdentifier(type = Double.class)
	public static final String START_SYMBOL_SIZE_KEY = "startSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String END_SYMBOL_SIZE_KEY = "endSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String MIDDLE_SYMBOL_SIZE_KEY = "middleSymbolSize";
	@PropertyIdentifier(type = Double.class)
	public static final String RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY = "relativeMiddleSymbolLocation";

	public static GRParameter<StartSymbolType> START_SYMBOL = GRParameter.getGRParameter(ConnectorSpecification.class, START_SYMBOL_KEY,
			StartSymbolType.class);
	public static GRParameter<Double> START_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorSpecification.class, START_SYMBOL_SIZE_KEY,
			Double.class);
	public static GRParameter<MiddleSymbolType> MIDDLE_SYMBOL = GRParameter.getGRParameter(ConnectorSpecification.class, MIDDLE_SYMBOL_KEY,
			MiddleSymbolType.class);
	public static GRParameter<Double> MIDDLE_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorSpecification.class, MIDDLE_SYMBOL_SIZE_KEY,
			Double.class);
	public static GRParameter<EndSymbolType> END_SYMBOL = GRParameter.getGRParameter(ConnectorSpecification.class, END_SYMBOL_KEY,
			EndSymbolType.class);
	public static GRParameter<Double> END_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorSpecification.class, END_SYMBOL_SIZE_KEY,
			Double.class);

	public static GRParameter<Double> RELATIVE_MIDDLE_SYMBOL_LOCATION = GRParameter.getGRParameter(ConnectorSpecification.class,
			RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY, Double.class);

	public static enum ConnectorType {
		LINE, RECT_POLYLIN, CURVE, CURVED_POLYLIN;

		public ImageIcon getIcon() {
			if (this == RECT_POLYLIN) {
				return FGEIconLibrary.RECT_POLYLIN_CONNECTOR_ICON;
			} else if (this == CURVE) {
				return FGEIconLibrary.CURVE_CONNECTOR_ICON;
			} else if (this == LINE) {
				return FGEIconLibrary.LINE_CONNECTOR_ICON;
			}
			return null;
		}

	}

	public ConnectorType getConnectorType();

	@Getter(value = START_SYMBOL_KEY)
	@XMLAttribute
	public StartSymbolType getStartSymbol();

	@Setter(value = START_SYMBOL_KEY)
	public void setStartSymbol(StartSymbolType startSymbol);

	@Getter(value = END_SYMBOL_KEY)
	@XMLAttribute
	public EndSymbolType getEndSymbol();

	@Setter(value = END_SYMBOL_KEY)
	public void setEndSymbol(EndSymbolType endSymbol);

	@Getter(value = MIDDLE_SYMBOL_KEY)
	@XMLAttribute
	public MiddleSymbolType getMiddleSymbol();

	@Setter(value = MIDDLE_SYMBOL_KEY)
	public void setMiddleSymbol(MiddleSymbolType middleSymbol);

	@Getter(value = START_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getStartSymbolSize();

	@Setter(value = START_SYMBOL_SIZE_KEY)
	public void setStartSymbolSize(double startSymbolSize);

	@Getter(value = END_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getEndSymbolSize();

	@Setter(value = END_SYMBOL_SIZE_KEY)
	public void setEndSymbolSize(double endSymbolSize);

	@Getter(value = MIDDLE_SYMBOL_SIZE_KEY, defaultValue = "10.0")
	@XMLAttribute
	public double getMiddleSymbolSize();

	@Setter(value = MIDDLE_SYMBOL_SIZE_KEY)
	public void setMiddleSymbolSize(double middleSymbolSize);

	@Getter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY, defaultValue = "0.5")
	@XMLAttribute
	public double getRelativeMiddleSymbolLocation();

	@Setter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY)
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation);

	public Connector<?> makeConnector(ConnectorNode<?> connectorNode);
}
