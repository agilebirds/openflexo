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
package org.openflexo.fge;

import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a connector linking two shapes in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ConnectorGraphicalRepresentation")
public interface ConnectorGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	// public static final String START_OBJECT = "startObject";
	// public static final String END_OBJECT = "endObject";
	@PropertyIdentifier(type = ConnectorSpecification.class)
	public static final String CONNECTOR_SPECIFICATION_KEY = "connectorSpecification";

	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOREGROUND_KEY = "foreground";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String SELECTED_FOREGROUND_KEY = "selectedForeground";
	@PropertyIdentifier(type = ForegroundStyle.class)
	public static final String FOCUSED_FOREGROUND_KEY = "focusedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_SELECTED_FOREGROUND_KEY = "hasSelectedForeground";
	@PropertyIdentifier(type = Boolean.class)
	public static final String HAS_FOCUSED_FOREGROUND_KEY = "hasFocusedForeground";

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
	@PropertyIdentifier(type = Boolean.class)
	public static final String APPLY_FOREGROUND_TO_SYMBOLS_KEY = "applyForegroundToSymbols";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DEBUG_COVERING_AREA_KEY = "debugCoveringArea";

	public static GRParameter<ConnectorSpecification> CONNECTOR = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.CONNECTOR_SPECIFICATION_KEY, ConnectorSpecification.class);

	public static GRParameter<ForegroundStyle> FOREGROUND = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.FOREGROUND_KEY, ForegroundStyle.class);
	public static GRParameter<ForegroundStyle> SELECTED_FOREGROUND = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.SELECTED_FOREGROUND_KEY, ForegroundStyle.class);
	public static GRParameter<ForegroundStyle> FOCUSED_FOREGROUND = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.FOCUSED_FOREGROUND_KEY, ForegroundStyle.class);

	public static GRParameter<StartSymbolType> START_SYMBOL = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.START_SYMBOL_KEY, StartSymbolType.class);
	public static GRParameter<Double> START_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.START_SYMBOL_SIZE_KEY, Double.class);
	public static GRParameter<MiddleSymbolType> MIDDLE_SYMBOL = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.MIDDLE_SYMBOL_KEY, MiddleSymbolType.class);
	public static GRParameter<Double> MIDDLE_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.MIDDLE_SYMBOL_SIZE_KEY, Double.class);
	public static GRParameter<EndSymbolType> END_SYMBOL = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.END_SYMBOL_KEY, EndSymbolType.class);
	public static GRParameter<Double> END_SYMBOL_SIZE = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.END_SYMBOL_SIZE_KEY, Double.class);

	public static GRParameter<Double> RELATIVE_MIDDLE_SYMBOL_LOCATION = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.RELATIVE_MIDDLE_SYMBOL_LOCATION_KEY, Double.class);
	public static GRParameter<Boolean> APPLY_FOREGROUND_TO_SYMBOLS = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.APPLY_FOREGROUND_TO_SYMBOLS_KEY, Boolean.class);
	public static GRParameter<Boolean> DEBUG_COVERING_AREA = GRParameter.getGRParameter(ConnectorGraphicalRepresentation.class,
			ConnectorGraphicalRepresentation.DEBUG_COVERING_AREA_KEY, Boolean.class);

	/*public static enum ConnectorParameters implements GRParameter {
		connector,
		foreground,
		selectedForeground,
		focusedForeground,
		hasSelectedForeground,
		hasFocusedForeground,
		startSymbol,
		endSymbol,
		middleSymbol,
		startSymbolSize,
		endSymbolSize,
		middleSymbolSize,
		relativeMiddleSymbolLocation,
		applyForegroundToSymbols,
		debugCoveringArea
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	/*@Getter(value = START_OBJECT)
	@XMLElement(context = "Start")
	public ShapeGraphicalRepresentation getStartObject();

	@Setter(value = START_OBJECT)
	public void setStartObject(ShapeGraphicalRepresentation aStartObject);

	@Getter(value = END_OBJECT)
	@XMLElement(context = "End")
	public ShapeGraphicalRepresentation getEndObject();

	@Setter(value = END_OBJECT)
	public void setEndObject(ShapeGraphicalRepresentation anEndObject);*/

	@Getter(value = CONNECTOR_SPECIFICATION_KEY)
	@XMLElement
	public ConnectorSpecification getConnectorSpecification();

	@Setter(value = CONNECTOR_SPECIFICATION_KEY)
	public void setConnectorSpecification(ConnectorSpecification aConnector);

	@Getter(value = FOREGROUND_KEY)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND_KEY)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND_KEY)
	@XMLElement(context = "Selected")
	public ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND_KEY)
	public void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND_KEY)
	public void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND_KEY)
	@XMLElement(context = "Focused")
	public ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND_KEY)
	public void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND_KEY)
	public void setHasFocusedForeground(boolean aFlag);

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

	@Getter(value = APPLY_FOREGROUND_TO_SYMBOLS_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getApplyForegroundToSymbols();

	@Setter(value = APPLY_FOREGROUND_TO_SYMBOLS_KEY)
	public void setApplyForegroundToSymbols(boolean applyForegroundToSymbols);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	// public void notifyConnectorChanged();

	public void notifyConnectorModified();

	public void notifyConnectorNeedsToBeRedrawn();

	public ConnectorType getConnectorType();

	public void setConnectorType(ConnectorType connectorType);

	// public void observeRelevantObjects();

	// public int getExtendedX(double scale);

	// public int getExtendedY(double scale);

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	// public Rectangle getNormalizedBounds(double scale);

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	// public double distanceToConnector(FGEPoint aPoint, double scale, ConnectorNode<?> connectorNode);

	// public boolean isConnectorConsistent();

	// public void refreshConnector();

	// Override for a custom view management
	// public ConnectorView makeConnectorView(DianaEditor controller);

	public boolean getDebugCoveringArea();

	public void setDebugCoveringArea(boolean debugCoveringArea);

	// public FGEConnectorGraphics getGraphics();

	// public List<? extends ControlArea<?>> getControlAreas();

	// public ConnectorGraphicalRepresentation clone();

}
