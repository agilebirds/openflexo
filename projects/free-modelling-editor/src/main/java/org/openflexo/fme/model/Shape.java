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
package org.openflexo.fme.model;

import java.util.List;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

/**
 * A shape element in a diagram
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "Shape")
public interface Shape extends DiagramElement<Shape, ShapeGraphicalRepresentation> {

	public static final String START_CONNECTORS = "start_connectors";
	public static final String END_CONNECTORS = "end_connectors";
	
	/*@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	@Override
	public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation);*/
	
	
	@Getter(value = START_CONNECTORS, cardinality = Cardinality.LIST, inverse = Connector.START_SHAPE)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.IGNORE)
	@Embedded
	public List<Connector> getStartConnectors();

	@Setter(START_CONNECTORS)
	public void setStartConnectors(List<Connector> someConnectors);

	@Adder(START_CONNECTORS)
	@PastingPoint
	public void addToStartConnectors(Connector aConnector);

	@Remover(START_CONNECTORS)
	public void removeFromStartConnectors(Connector aConnector);
	
	@Getter(value = END_CONNECTORS, cardinality = Cardinality.LIST, inverse = Connector.END_SHAPE)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.IGNORE)
	public List<Connector> getEndConnectors();

	@Setter(END_CONNECTORS)
	public void setEndConnectors(List<Connector> someConnectors);

	@Adder(END_CONNECTORS)
	@PastingPoint
	public void addToEndConnectors(Connector aConnector);

	@Remover(END_CONNECTORS)
	public void removeFromEndConnectors(Connector aConnector);
	
}