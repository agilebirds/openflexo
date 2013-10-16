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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ConnectorImpl.class)
@XMLElement(xmlTag = "Connector")
public interface Connector extends DiagramElement<Connector, ConnectorGraphicalRepresentation> {

	public static final String START_SHAPE = "startShape";
	public static final String END_SHAPE = "endShape";

	@Getter(START_SHAPE)
	@XMLElement(context = "Start")
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public Shape getStartShape();

	@Setter(START_SHAPE)
	public void setStartShape(Shape startShape);

	@Getter(END_SHAPE)
	@XMLElement(context = "End")
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public abstract Shape getEndShape();

	@Setter(END_SHAPE)
	public abstract void setEndShape(Shape endShape);

}