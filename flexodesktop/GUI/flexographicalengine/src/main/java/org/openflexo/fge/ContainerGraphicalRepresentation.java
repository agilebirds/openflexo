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

import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.fge.impl.ContainerGraphicalRepresentationImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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

	@PropertyIdentifier(type = Double.class)
	public static final String WIDTH_KEY = "width";
	@PropertyIdentifier(type = Double.class)
	public static final String HEIGHT_KEY = "height";

	@PropertyIdentifier(type = Double.class)
	public static final String MINIMAL_WIDTH_KEY = "minimalWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String MINIMAL_HEIGHT_KEY = "minimalHeight";
	@PropertyIdentifier(type = Double.class)
	public static final String MAXIMAL_WIDTH_KEY = "maximalWidth";
	@PropertyIdentifier(type = Double.class)
	public static final String MAXIMAL_HEIGHT_KEY = "maximalHeight";
	@PropertyIdentifier(type = FGESteppedDimensionConstraint.class)
	public static final String DIMENSION_CONSTRAINT_STEP_KEY = "dimensionConstraintStep";
	@PropertyIdentifier(type = DimensionConstraints.class)
	public static final String DIMENSION_CONSTRAINTS_KEY = "dimensionConstraints";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY = "adjustMinimalWidthToLabelWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY = "adjustMinimalHeightToLabelHeight";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY = "adjustMaximalWidthToLabelWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY = "adjustMaximalHeightToLabelHeight";

	public static GRParameter<Double> WIDTH = GRParameter.getGRParameter(ContainerGraphicalRepresentation.class, WIDTH_KEY, Double.class);
	public static GRParameter<Double> HEIGHT = GRParameter.getGRParameter(ContainerGraphicalRepresentation.class, HEIGHT_KEY, Double.class);

	public static GRParameter<Boolean> ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT = GRParameter.getGRParameter(
			ShapeGraphicalRepresentation.class, ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, Boolean.class);
	public static GRParameter<Boolean> ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY, Boolean.class);
	public static GRParameter<Boolean> ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT = GRParameter.getGRParameter(
			ShapeGraphicalRepresentation.class, ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, Boolean.class);
	public static GRParameter<Boolean> ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY, Boolean.class);
	public static GRParameter<Double> MINIMAL_WIDTH = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, MINIMAL_WIDTH_KEY,
			Double.class);
	public static GRParameter<Double> MINIMAL_HEIGHT = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, MINIMAL_HEIGHT_KEY,
			Double.class);
	public static GRParameter<Double> MAXIMAL_WIDTH = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, MAXIMAL_WIDTH_KEY,
			Double.class);
	public static GRParameter<Double> MAXIMAL_HEIGHT = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class, MAXIMAL_HEIGHT_KEY,
			Double.class);
	public static GRParameter<DimensionConstraints> DIMENSION_CONSTRAINTS = GRParameter.getGRParameter(ShapeGraphicalRepresentation.class,
			DIMENSION_CONSTRAINTS_KEY, DimensionConstraints.class);
	public static GRParameter<FGESteppedDimensionConstraint> DIMENSION_CONSTRAINT_STEP = GRParameter.getGRParameter(
			ShapeGraphicalRepresentation.class, DIMENSION_CONSTRAINT_STEP_KEY, FGESteppedDimensionConstraint.class);

	/*public static enum ContainerParameters implements GRParameter {
		width, height;
	}*/

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = WIDTH_KEY, defaultValue = "100")
	@XMLAttribute
	public abstract double getWidth();

	@Setter(value = WIDTH_KEY)
	public abstract void setWidth(double aValue);

	@Getter(value = HEIGHT_KEY, defaultValue = "100")
	@XMLAttribute
	public abstract double getHeight();

	@Setter(value = HEIGHT_KEY)
	public abstract void setHeight(double aValue);

	public FGEDimension getSize();

	@Getter(value = MINIMAL_WIDTH_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalWidth();

	@Setter(value = MINIMAL_WIDTH_KEY)
	public void setMinimalWidth(double minimalWidth);

	@Getter(value = MINIMAL_HEIGHT_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getMinimalHeight();

	@Setter(value = MINIMAL_HEIGHT_KEY)
	public void setMinimalHeight(double minimalHeight);

	@Getter(value = MAXIMAL_HEIGHT_KEY, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalHeight();

	@Setter(value = MAXIMAL_HEIGHT_KEY)
	public void setMaximalHeight(double maximalHeight);

	@Getter(value = MAXIMAL_WIDTH_KEY, defaultValue = "POSITIVE_INFINITY")
	@XMLAttribute
	public double getMaximalWidth();

	@Setter(value = MAXIMAL_WIDTH_KEY)
	public void setMaximalWidth(double maximalWidth);

	@Getter(value = DIMENSION_CONSTRAINT_STEP_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGESteppedDimensionConstraint getDimensionConstraintStep();

	@Setter(value = DIMENSION_CONSTRAINT_STEP_KEY)
	public void setDimensionConstraintStep(FGESteppedDimensionConstraint dimensionConstraintStep);

	@Getter(value = DIMENSION_CONSTRAINTS_KEY)
	@XMLAttribute
	public DimensionConstraints getDimensionConstraints();

	@Setter(value = DIMENSION_CONSTRAINTS_KEY)
	public void setDimensionConstraints(DimensionConstraints dimensionConstraints);

	@Getter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalWidthToLabelWidth();

	@Setter(value = ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH_KEY)
	public void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth);

	@Getter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getAdjustMinimalHeightToLabelHeight();

	@Setter(value = ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY)
	public void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight);

	@Getter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalWidthToLabelWidth();

	@Setter(value = ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH_KEY)
	public void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth);

	@Getter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAdjustMaximalHeightToLabelHeight();

	@Setter(value = ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT_KEY)
	public void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight);

}
