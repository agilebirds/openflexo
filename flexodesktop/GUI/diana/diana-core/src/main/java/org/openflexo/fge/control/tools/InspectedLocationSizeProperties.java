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
package org.openflexo.fge.control.tools;

import java.util.List;

import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link ShadowStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedLocationSizeProperties extends InspectedStyle<GraphicalRepresentation> {

	public InspectedLocationSizeProperties(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects();
	}

	@Override
	public GraphicalRepresentation getStyle(DrawingTreeNode<?, ?> node) {
		return node.getGraphicalRepresentation();
	}

	public boolean areLocationPropertiesApplicable() {
		return getController().getSelectedShapes().size() > 0;
	}

	public Boolean getIsVisible() {
		return getPropertyValue(GraphicalRepresentation.IS_VISIBLE);
	}

	public void setIsVisible(Boolean value) {
		setPropertyValue(GraphicalRepresentation.IS_VISIBLE, value);
	}

	public Integer getLayer() {
		return getPropertyValue(GraphicalRepresentation.LAYER);
	}

	public void setLayer(Integer value) {
		setPropertyValue(GraphicalRepresentation.LAYER, value);
	}

	public Integer getIndexInLayer() {
		// TODO
		return 0;
	}

	public void setIndexInLayer(Integer value) {
		// TODO
	}

	public Double getX() {
		return getPropertyValue(ShapeGraphicalRepresentation.X);
	}

	public void setX(Double value) {
		setPropertyValue(ShapeGraphicalRepresentation.X, value);
	}

	public Double getY() {
		return getPropertyValue(ShapeGraphicalRepresentation.Y);
	}

	public void setY(Double value) {
		setPropertyValue(ShapeGraphicalRepresentation.Y, value);
	}

	public Double getWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.WIDTH);
	}

	public void setWidth(Double value) {
		setPropertyValue(ContainerGraphicalRepresentation.WIDTH, value);
	}

	public Double getHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.HEIGHT);
	}

	public void setHeight(Double value) {
		setPropertyValue(ContainerGraphicalRepresentation.HEIGHT, value);
	}

}
