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

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Implementation of {@link ConnectorSpecification}, as a mutable container over {@link ConnectorSpecification} class hierarchy.<br>
 * It presents graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedConnectorSpecification extends
		InspectedStyleUsingFactory<ConnectorSpecificationFactory, ConnectorSpecification, ConnectorType> {

	public InspectedConnectorSpecification(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, new ConnectorSpecificationFactory(controller));
	}

	@Override
	public List<ConnectorNode<?>> getSelection() {
		return getController().getSelectedConnectors();
	}

	@Override
	public ConnectorSpecification getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ConnectorNode) {
			return ((ConnectorNode<?>) node).getConnectorSpecification();
		}
		return null;
	}

	@Override
	protected ConnectorType getStyleType(ConnectorSpecification style) {
		if (style != null) {
			return style.getConnectorType();
		}
		return null;
	}

	protected void applyNewStyleTypeToSelection(ConnectorType newStyleType) {
		System.out.println("OK, je dis a tout le monde que c'est un " + newStyleType);
		for (DrawingTreeNode<?, ?> n : getSelection()) {
			ConnectorSpecification nodeStyle = getStyle(n);
			System.out.println("> le noeud " + n + " est a " + nodeStyle + " type=" + getStyleType(nodeStyle));
			if (getStyleType(nodeStyle) != newStyleType) {
				applyNewStyle(newStyleType, n);
			}
		}
	}

	protected void applyNewStyle(ConnectorType newConnectorType, DrawingTreeNode<?, ?> node) {
		ConnectorNode<?> n = (ConnectorNode<?>) node;
		ConnectorSpecification oldConnectorSpecification = n.getConnectorSpecification();
		CompoundEdit setValueEdit = startRecordEdit("Set ConnectorType to " + newConnectorType);
		System.out.println("set ConnectorSpec with " + newConnectorType);
		ConnectorSpecification newConnectorSpecification = getStyleFactory().makeNewStyle(oldConnectorSpecification);
		System.out.println("set ConnectorSpec with " + newConnectorSpecification);
		n.setConnectorSpecification(newConnectorSpecification);
		// n.getPropertyChangeSupport().firePropertyChange(ShapeGraphicalRepresentation.BACKGROUND_STYLE_TYPE_KEY,
		// oldShapeSpecification.getShapeType(), newShapeType);
		stopRecordEdit(setValueEdit);
	}
}
