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

import java.beans.PropertyChangeListener;
import java.util.List;

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
import org.openflexo.model.annotations.XMLElement;

/**
 * Abstraction of any graphical element at logic level, involved in a {@link Diagram}
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <G>
 */
@ModelEntity
@ImplementationClass(DiagramElementImpl.class)
public interface DiagramElement<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> extends FMEModelObject,
		PropertyChangeListener {

	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	public static final String SHAPES = "shapes";
	public static final String CONNECTORS = "connectors";
	public static final String ASSOCIATION = "association";
	public static final String CONTAINER = "container";
	public static final String INSTANCE = "instance";

	@Getter(value = SHAPES, cardinality = Cardinality.LIST, inverse = CONTAINER)
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

	@Getter(value = CONNECTORS, cardinality = Cardinality.LIST, inverse = CONTAINER)
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

	@Getter(value = CONTAINER)
	public DiagramElement<?, ?> getContainer();

	@Setter(value = CONTAINER)
	public void setContainer(DiagramElement<?, ?> aContainer);

	public Diagram getDiagram();

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public G getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(G graphicalRepresentation);

	@Getter(value = ASSOCIATION)
	@XMLElement
	public ConceptGRAssociation getAssociation();

	@Setter(value = ASSOCIATION)
	public void setAssociation(ConceptGRAssociation anAssociation);

	@Getter(value = INSTANCE)
	@XMLElement
	@Embedded
	public Instance getInstance();

	@Setter(INSTANCE)
	public void setInstance(Instance instance);

	public List<DiagramElement<?, ?>> getElementsWithAssociation(ConceptGRAssociation association);

	public List<DiagramElement<?, ?>> getElementsRepresentingInstance(Instance instance);

	public void setChanged();

	public boolean hasChanged();

}
