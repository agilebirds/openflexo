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
package org.openflexo.fge.shapes.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.shapes.ShapeSpecification;

/**
 * Default implementation of {@link ShapeSpecification}
 * 
 * @author sylvain
 * 
 */
public abstract class ShapeSpecificationImpl extends FGEObjectImpl implements ShapeSpecification {

	private static final Logger logger = Logger.getLogger(ShapeSpecificationImpl.class.getPackage().getName());

	// private transient ShapeGraphicalRepresentation graphicalRepresentation;

	// private transient Vector<ControlPoint> _controlPoints = null;

	private static final FGEModelFactory SHADOW_FACTORY = FGEUtils.TOOLS_FACTORY;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeSpecificationImpl() {
		super();
	}

	/**
	 * Must be overriden when shape requires it
	 * 
	 * @return
	 */
	@Override
	public boolean areDimensionConstrained() {
		return false;
	}

	@Override
	public abstract ShapeType getShapeType();

	@Override
	public ShapeSpecificationImpl clone() {
		try {
			ShapeSpecificationImpl returned = (ShapeSpecificationImpl) super.clone();
			// returned._controlPoints = null;
			// returned.graphicalRepresentation = null;
			// returned.updateShape();
			// returned.rebuildControlPoints();
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object object) {
		// TODO
		/*if (object instanceof ShapeSpecificationImpl && getShape() != null) {
			return getShape().equals(((ShapeSpecificationImpl) object).getShape())
					&& areDimensionConstrained() == ((ShapeSpecificationImpl) object).areDimensionConstrained();
		}*/
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		// TODO
		/*if (getShape() != null) {
			return getShape().toString().hashCode();
		}*/
		return super.hashCode();
	}

	@Override
	public Shape<?> makeShape(ShapeNode<?> node) {
		Shape returned = new Shape(node);
		addObserver(returned);
		return returned;
	}

}
