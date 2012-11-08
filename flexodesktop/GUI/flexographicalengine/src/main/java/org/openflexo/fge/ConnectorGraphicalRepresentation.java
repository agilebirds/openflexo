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
package org.openflexo.fge;

import java.awt.Rectangle;
import java.util.List;

import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a connector linking two shapes in a diagram<br>
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 * @param <O>
 *            the represented type
 */
@ModelEntity
@ImplementationClass(ConnectorGraphicalRepresentationImpl.class)
@XMLElement(xmlTag = "ConnectorGraphicalRepresentation")
public interface ConnectorGraphicalRepresentation<O> extends GraphicalRepresentation<O> {

	// Property keys

	public static final String START_OBJECT = "startObject";
	public static final String END_OBJECT = "endObject";
	public static final String CONNECTOR = "connector";

	public static final String FOREGROUND = "foreground";
	public static final String SELECTED_FOREGROUND = "selectedForeground";
	public static final String FOCUSED_FOREGROUND = "focusedForeground";
	public static final String HAS_SELECTED_FOREGROUND = "hasSelectedForeground";
	public static final String HAS_FOCUSED_FOREGROUND = "hasFocusedForeground";

	public static final String START_SYMBOL = "startSymbol";
	public static final String END_SYMBOL = "endSymbol";
	public static final String MIDDLE_SYMBOL = "middleSymbol";
	public static final String START_SYMBOL_SIZE = "startSymbolSize";
	public static final String END_SYMBOL_SIZE = "endSymbolSize";
	public static final String MIDDLE_SYMBOL_SIZE = "middleSymbolSize";
	public static final String RELATIVE_MIDDLE_SYMBOL_LOCATION = "relativeMiddleSymbolLocation";
	public static final String APPLY_FOREGROUND_TO_SYMBOLS = "applyForegroundToSymbols";

	public static enum ConnectorParameters implements GRParameter {
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
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = START_OBJECT)
	@XMLElement(context = "Start")
	public ShapeGraphicalRepresentation<?> getStartObject();

	@Setter(value = START_OBJECT)
	public void setStartObject(ShapeGraphicalRepresentation<?> aStartObject);

	@Getter(value = END_OBJECT)
	@XMLElement(context = "End")
	public ShapeGraphicalRepresentation<?> getEndObject();

	@Setter(value = END_OBJECT)
	public void setEndObject(ShapeGraphicalRepresentation<?> anEndObject);

	@Getter(value = CONNECTOR)
	@XMLElement
	public Connector getConnector();

	@Setter(value = CONNECTOR)
	public void setConnector(Connector aConnector);

	@Getter(value = FOREGROUND)
	@XMLElement
	public ForegroundStyle getForeground();

	@Setter(value = FOREGROUND)
	public void setForeground(ForegroundStyle aForeground);

	@Getter(value = SELECTED_FOREGROUND)
	@XMLElement(context = "Selected")
	public ForegroundStyle getSelectedForeground();

	@Setter(value = SELECTED_FOREGROUND)
	public void setSelectedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_SELECTED_FOREGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasSelectedForeground();

	@Setter(value = HAS_SELECTED_FOREGROUND)
	public void setHasSelectedForeground(boolean aFlag);

	@Getter(value = FOCUSED_FOREGROUND)
	@XMLElement(context = "Focused")
	public ForegroundStyle getFocusedForeground();

	@Setter(value = FOCUSED_FOREGROUND)
	public void setFocusedForeground(ForegroundStyle aForeground);

	@Getter(value = HAS_FOCUSED_FOREGROUND, defaultValue = "false")
	@XMLAttribute
	public boolean getHasFocusedForeground();

	@Setter(value = HAS_FOCUSED_FOREGROUND)
	public void setHasFocusedForeground(boolean aFlag);

	@Getter(value = START_SYMBOL)
	@XMLAttribute
	public StartSymbolType getStartSymbol();

	@Setter(value = START_SYMBOL)
	public void setStartSymbol(StartSymbolType startSymbol);

	@Getter(value = END_SYMBOL)
	@XMLAttribute
	public EndSymbolType getEndSymbol();

	@Setter(value = END_SYMBOL)
	public void setEndSymbol(EndSymbolType endSymbol);

	@Getter(value = MIDDLE_SYMBOL)
	@XMLAttribute
	public MiddleSymbolType getMiddleSymbol();

	@Setter(value = MIDDLE_SYMBOL)
	public void setMiddleSymbol(MiddleSymbolType middleSymbol);

	@Getter(value = START_SYMBOL_SIZE, defaultValue = "10.0")
	@XMLAttribute
	public double getStartSymbolSize();

	@Setter(value = START_SYMBOL_SIZE)
	public void setStartSymbolSize(double startSymbolSize);

	@Getter(value = END_SYMBOL_SIZE, defaultValue = "10.0")
	@XMLAttribute
	public double getEndSymbolSize();

	@Setter(value = END_SYMBOL_SIZE)
	public void setEndSymbolSize(double endSymbolSize);

	@Getter(value = MIDDLE_SYMBOL_SIZE, defaultValue = "10.0")
	@XMLAttribute
	public double getMiddleSymbolSize();

	@Setter(value = MIDDLE_SYMBOL_SIZE)
	public void setMiddleSymbolSize(double middleSymbolSize);

	@Getter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION, defaultValue = "0.5")
	@XMLAttribute
	public double getRelativeMiddleSymbolLocation();

	@Setter(value = RELATIVE_MIDDLE_SYMBOL_LOCATION)
	public void setRelativeMiddleSymbolLocation(double relativeMiddleSymbolLocation);

	@Getter(value = APPLY_FOREGROUND_TO_SYMBOLS, defaultValue = "true")
	@XMLAttribute
	public boolean getApplyForegroundToSymbols();

	@Setter(value = APPLY_FOREGROUND_TO_SYMBOLS)
	public void setApplyForegroundToSymbols(boolean applyForegroundToSymbols);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public void notifyConnectorChanged();

	public ConnectorType getConnectorType();

	public void setConnectorType(ConnectorType connectorType);

	public void observeRelevantObjects();

	public int getExtendedX(double scale);

	public int getExtendedY(double scale);

	/**
	 * Return normalized bounds Those bounds corresponds to the normalized area defined as (0.0,0.0)-(1.0,1.0) enclosing EXACTELY the two
	 * related shape bounds. Those bounds should eventually be extended to contain connector contained outside this area.
	 */
	public Rectangle getNormalizedBounds(double scale);

	/**
	 * Return distance from point to connector representation with a given scale
	 * 
	 * @param aPoint
	 *            expressed in local normalized coordinates system
	 * @param scale
	 * @return
	 */
	public double distanceToConnector(FGEPoint aPoint, double scale);

	public boolean isConnectorConsistent();

	public void refreshConnector();

	// Override for a custom view management
	public ConnectorView<O> makeConnectorView(DrawingController<?> controller);

	public boolean getDebugCoveringArea();

	public void setDebugCoveringArea(boolean debugCoveringArea);

	public FGEConnectorGraphics getGraphics();

	public List<? extends ControlArea> getControlAreas();

	public ConnectorGraphicalRepresentation<O> clone();

}
