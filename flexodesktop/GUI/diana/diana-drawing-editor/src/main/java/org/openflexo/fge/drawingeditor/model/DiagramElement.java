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
package org.openflexo.fge.drawingeditor.model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.xmlcode.XMLSerializable;

@ModelEntity
@ImplementationClass(DiagramElementImpl.class)
public interface DiagramElement<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> extends XMLSerializable, Cloneable,
		Observer, AccessibleProxyObject, CloneableProxyObject {

	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	public static final String DIAGRAM = "diagram";
	public static final String SHAPES = "shapes";
	public static final String CONNECTORS = "connectors";
	public static final String NAME = "name";

	@Getter(value = SHAPES, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<Shape> getShapes();

	@Setter(SHAPES)
	public void setShapes(List<Shape> someShapes);

	@Adder(SHAPES)
	@PastingPoint
	public void addToShapes(Shape aShape);

	@Remover(SHAPES)
	public void removeFromShapes(Shape aShape);

	@Getter(value = CONNECTORS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<Connector> getConnectors();

	@Setter(CONNECTORS)
	public void setConnectors(List<Connector> someConnectors);

	@Adder(CONNECTORS)
	@PastingPoint
	public void addToConnectors(Connector aConnector);

	@Remover(CONNECTORS)
	public void removeFromConnectors(Connector aConnector);

	@Getter(value = NAME)
	@XMLAttribute
	public String getName();

	@Setter(value = NAME)
	public void setName(String aName);

	@Getter(value = DIAGRAM)
	public Diagram getDiagram();

	@Setter(value = DIAGRAM)
	public void setDiagram(Diagram diagram);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public G getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(G graphicalRepresentation);

	// public void initializeDeserialization();

	// public void finalizeDeserialization();

	public DiagramElement<M, G> clone();

	@Override
	public void update(Observable o, Object arg);

	public void setChanged();

	public boolean hasChanged();
}
