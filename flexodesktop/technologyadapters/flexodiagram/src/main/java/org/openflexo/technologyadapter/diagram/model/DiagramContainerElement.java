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
package org.openflexo.technologyadapter.diagram.model;

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
 * Implements containment in Openflexo Diagram built-in technology: a container may contains some shapes and connectors
 * 
 * @author sylvain
 * 
 * @param <G>
 *            type of underlying graphical representation (sub-class of {@link GraphicalRepresentation})
 */
@ModelEntity
@ImplementationClass(DiagramContainerElementImpl.class)
public interface DiagramContainerElement<G extends GraphicalRepresentation> extends DiagramElement<G> {

	public static final String SHAPES = "shapes";
	public static final String CONNECTORS = "connectors";

	@Getter(value = SHAPES, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<DiagramShape> getShapes();

	@Setter(SHAPES)
	public void setShapes(List<DiagramShape> someShapes);

	@Adder(SHAPES)
	@PastingPoint
	public void addToShapes(DiagramShape aShape);

	@Remover(SHAPES)
	public void removeFromShapes(DiagramShape aShape);

	@Getter(value = CONNECTORS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<DiagramConnector> getConnectors();

	@Setter(CONNECTORS)
	public void setConnectors(List<DiagramConnector> someConnectors);

	@Adder(CONNECTORS)
	@PastingPoint
	public void addToConnectors(DiagramConnector aConnector);

	@Remover(CONNECTORS)
	public void removeFromConnectors(DiagramConnector aConnector);

	/**
	 * Return all descendants of this {@link DiagramElement} (recursive method)
	 * 
	 * @return
	 */
	public List<DiagramElement<?>> getDescendants();

}
