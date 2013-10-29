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

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Implementation of {@link ShapeSpecification}, as a mutable container over {@link ShapeSpecification} class hierarchy.<br>
 * It presents graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedShapeSpecification extends InspectedStyleUsingFactory<ShapeSpecificationFactory, ShapeSpecification, ShapeType> {

	public InspectedShapeSpecification(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, new ShapeSpecificationFactory(controller));
	}

	@Override
	public List<ShapeNode<?>> getSelection() {
		return getController().getSelectedShapes();
	}

	@Override
	public ShapeSpecification getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			return ((ShapeNode<?>) node).getShapeSpecification();
		}
		return null;
	}

	@Override
	protected ShapeType getStyleType(ShapeSpecification style) {
		if (style != null) {
			return style.getShapeType();
		}
		return null;
	}

	protected void applyNewStyle(ShapeType newShapeType, DrawingTreeNode<?, ?> node) {
		ShapeNode<?> n = (ShapeNode<?>) node;
		ShapeSpecification oldShapeSpecification = n.getShapeSpecification();
		CompoundEdit setValueEdit = startRecordEdit("Set ShapeType to " + newShapeType);
		ShapeSpecification newShapeSpecification = getStyleFactory().makeNewStyle(oldShapeSpecification);
		n.setShapeSpecification(newShapeSpecification);
		// n.getPropertyChangeSupport().firePropertyChange(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
		// oldShapeSpecification.getShapeType(), newShapeType);
		stopRecordEdit(setValueEdit);
	}
}
