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

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a Shape in Openflexo build-in diagram technology<br>
 * A shape may be a container of other shapes and connectors
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DiagramShapeImpl.class)
@XMLElement(xmlTag = "Shape")
public interface DiagramShape extends DiagramContainerElement<ShapeGraphicalRepresentation> {

	// TODO: comment this when method clash in PAMELA will be solved
	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation();

	// TODO: comment this when method clash in PAMELA will be solved
	@Setter(value = GRAPHICAL_REPRESENTATION)
	@Override
	public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation);

	/**
	 * Return parent of this diagram element
	 * 
	 * @return
	 */
	@Override
	@Getter(value = PARENT, inverse = DiagramContainerElement.SHAPES)
	@XMLAttribute
	public DiagramContainerElement<?> getParent();

}