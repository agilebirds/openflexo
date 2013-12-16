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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.action.GRConnectorTemplate;

@ModelEntity
@ImplementationClass(DiagramConnectorImpl.class)
@XMLElement(xmlTag = "Connector")
public interface DiagramConnector extends DiagramElement<ConnectorGraphicalRepresentation>, GRConnectorTemplate {

	public static final String START_SHAPE = "startShape";
	public static final String END_SHAPE = "endShape";

	/**
	 * Returns the start shape of this connector
	 * 
	 * @return
	 */
	@Getter(START_SHAPE)
	@XMLElement(context = "Start")
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public DiagramShape getStartShape();

	/**
	 * Sets the start shape of this connector
	 * 
	 * @return
	 */
	@Setter(START_SHAPE)
	public void setStartShape(DiagramShape startShape);

	/**
	 * Returns the end shape of this connector
	 * 
	 * @return
	 */
	@Getter(END_SHAPE)
	@XMLElement(context = "End")
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public abstract DiagramShape getEndShape();

	/**
	 * Sets the end shape of this connector
	 * 
	 * @return
	 */
	@Setter(END_SHAPE)
	public abstract void setEndShape(DiagramShape endShape);

	/**
	 * Return parent of this diagram element
	 * 
	 * @return
	 */
	@Override
	@Getter(value = PARENT, inverse = DiagramContainerElement.CONNECTORS)
	@XMLAttribute
	public DiagramContainerElement<?> getParent();

	// TODO: comment this when method clash in PAMELA will be solved
	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation();

	// TODO: comment this when method clash in PAMELA will be solved
	@Setter(value = GRAPHICAL_REPRESENTATION)
	@Override
	public void setGraphicalRepresentation(ConnectorGraphicalRepresentation graphicalRepresentation);

}