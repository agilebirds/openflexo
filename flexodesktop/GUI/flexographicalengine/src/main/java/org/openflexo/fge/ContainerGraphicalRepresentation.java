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

import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.impl.ContainerGraphicalRepresentationImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * Represents a container in a diagram <br>
 * A container has a size (a width and an height), and may defines layout features<br>
 * Basic implementations of a container include drawing (root of a diagram), and shapes<br>
 * 
 * Note that this implementation is powered by PAMELA framework.
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(ContainerGraphicalRepresentationImpl.class)
public interface ContainerGraphicalRepresentation extends GraphicalRepresentation {

	// Property keys

	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	public static enum ContainerParameters implements GRParameter {
		width, height;
	}

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = WIDTH, defaultValue = "100")
	@XMLAttribute
	public abstract double getWidth();

	@Setter(value = WIDTH)
	public abstract void setWidth(double aValue);

	@Getter(value = HEIGHT, defaultValue = "100")
	@XMLAttribute
	public abstract double getHeight();

	@Setter(value = HEIGHT)
	public abstract void setHeight(double aValue);

	public FGEDimension getSize();

}
