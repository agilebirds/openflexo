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
package org.openflexo.fme.model;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEObject;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.shapes.ShapeSpecification;

/**
 * Default implementation of {@link DiagramElement}
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <G>
 */
public abstract class DiagramElementImpl<M extends DiagramElement<M, G>, G extends GraphicalRepresentation> implements DiagramElement<M, G> {

	public DiagramElementImpl() {
	}

	@Override
	public Diagram getDiagram() {
		if (this instanceof Diagram) {
			return (Diagram) this;
		}
		if (getContainer() != null) {
			return getContainer().getDiagram();
		}
		return null;
	}
	
	@Override
	public void setGraphicalRepresentation(G graphicalRepresentation) {
		if (getGraphicalRepresentation() != null && getGraphicalRepresentation().getPropertyChangeSupport() != null) {
			getGraphicalRepresentation().getPropertyChangeSupport().removePropertyChangeListener(this);	
		}
		performSuperSetter(GRAPHICAL_REPRESENTATION, graphicalRepresentation);
		if (graphicalRepresentation != null && graphicalRepresentation.getPropertyChangeSupport() != null) {
			graphicalRepresentation.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(GraphicalRepresentation.TRANSPARENCY_KEY)
				|| (evt.getPropertyName().equals(BackgroundStyle.USE_TRANSPARENCY_KEY))
				|| (evt.getPropertyName().equals(BackgroundStyle.TRANSPARENCY_LEVEL_KEY))
				|| (evt.getPropertyName().equals(ForegroundStyle.USE_TRANSPARENCY_KEY))
				|| (evt.getPropertyName().equals(ForegroundStyle.TRANSPARENCY_LEVEL_KEY))) {
			return;
		}

		// Detected that a graphical property value has changed
		GRParameter<?> p = GRParameter.getGRParameter(evt.getSource().getClass(), evt.getPropertyName());
		if (p != null && evt.getSource() instanceof FGEObject) {
			if ((evt.getSource() == getGraphicalRepresentation().getTextStyle())
					|| ((getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) && (evt.getSource() == ((ShapeGraphicalRepresentation) getGraphicalRepresentation())
							.getForeground()))
					|| ((getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) && (evt.getSource() == ((ShapeGraphicalRepresentation) getGraphicalRepresentation())
							.getBackground()))
					|| ((getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) && (evt.getSource() == ((ShapeGraphicalRepresentation) getGraphicalRepresentation())
							.getShapeSpecification()))
					|| ((getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) && (evt.getSource() == ((ConnectorGraphicalRepresentation) getGraphicalRepresentation())
							.getForeground()))
					|| ((getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) && (evt.getSource() == ((ConnectorGraphicalRepresentation) getGraphicalRepresentation())
							.getConnectorSpecification()))) {
				handleGraphicalPropertyChanged((FGEObject) evt.getSource(), p, evt.getOldValue(), evt.getNewValue());
			}
		}
	}

	private <T> void handleGraphicalPropertyChanged(FGEObject source, GRParameter<?> p, Object oldValue, Object newValue) {
		List<DiagramElement<?, ?>> allElementsSharingSameAssociation = getDiagram().getElementsWithAssociation(getAssociation());

		if (allElementsSharingSameAssociation.size() == 1) {
			propagateModificationToAssociation(source, p, oldValue, newValue, false);
		}

		else { // allElementsSharingSameAssociation.size() > 1
				// Create new Association
			setAssociation((ConceptGRAssociation) getAssociation().cloneObject());
			getDiagram().addToAssociations(getAssociation());
			propagateModificationToAssociation(source, p, oldValue, newValue, false);
		}
	}

	private <T> void propagateModificationToAssociation(FGEObject source, GRParameter<?> p, Object oldValue, Object newValue,
			boolean alsoPropagateToSiblings) {

		List<DiagramElement<?, ?>> allElementsSharingSameAssociation = getDiagram().getElementsWithAssociation(getAssociation());

		if (source instanceof TextStyle) {
			((ShapeGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getTextStyle().setObjectForKey(newValue,
					p.getName());
			if (alsoPropagateToSiblings) {
				for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
					if (e != this) {
						((ShapeGraphicalRepresentation) e.getGraphicalRepresentation()).getTextStyle().setObjectForKey(newValue,
								p.getName());
					}
				}
			}
		}

		if (getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			if (source instanceof BackgroundStyle) {
				((ShapeGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getBackground().setObjectForKey(newValue,
						p.getName());
				if (alsoPropagateToSiblings) {
					for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
						if (e != this) {
							((ShapeGraphicalRepresentation) e.getGraphicalRepresentation()).getBackground().setObjectForKey(newValue,
									p.getName());
						}
					}
				}
			}
			if (source instanceof ForegroundStyle) {
				((ShapeGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getForeground().setObjectForKey(newValue,
						p.getName());
				if (alsoPropagateToSiblings) {
					for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
						if (e != this) {
							((ShapeGraphicalRepresentation) e.getGraphicalRepresentation()).getForeground().setObjectForKey(newValue,
									p.getName());
						}
					}
				}
			}
			if (source instanceof ShapeSpecification) {
				((ShapeGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getShapeSpecification().setObjectForKey(
						newValue, p.getName());
				if (alsoPropagateToSiblings) {
					for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
						if (e != this) {
							((ShapeGraphicalRepresentation) e.getGraphicalRepresentation()).getShapeSpecification().setObjectForKey(
									newValue, p.getName());
						}
					}
				}
			}
		}

		if (getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			if (source instanceof ForegroundStyle) {
				((ConnectorGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getForeground().setObjectForKey(
						newValue, p.getName());
				if (alsoPropagateToSiblings) {
					for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
						if (e != this) {
							((ConnectorGraphicalRepresentation) e.getGraphicalRepresentation()).getForeground().setObjectForKey(newValue,
									p.getName());
						}
					}
				}
			}
			if (source instanceof ConnectorSpecification) {
				((ConnectorGraphicalRepresentation) getAssociation().getGraphicalRepresentation()).getConnectorSpecification()
						.setObjectForKey(newValue, p.getName());
				if (alsoPropagateToSiblings) {
					for (DiagramElement<?, ?> e : allElementsSharingSameAssociation) {
						if (e != this) {
							((ConnectorGraphicalRepresentation) e.getGraphicalRepresentation()).getConnectorSpecification()
									.setObjectForKey(newValue, p.getName());
						}
					}
				}
			}
		}

	}
	
	

	public List<DiagramElement<?, ?>> getElementsWithAssociation(ConceptGRAssociation association) {
		List<DiagramElement<?, ?>> returned = new ArrayList<DiagramElement<?, ?>>();
		if (getAssociation() == association) {
			returned.add(this);
		}
		for (Shape s : getShapes()) {
			returned.addAll(s.getElementsWithAssociation(association));
		}
		for (Connector c : getConnectors()) {
			returned.addAll(c.getElementsWithAssociation(association));
		}
		return returned;
	}

	public List<DiagramElement<?, ?>> getElementsRepresentingInstance(Instance instance) {
		List<DiagramElement<?, ?>> returned = new ArrayList<DiagramElement<?, ?>>();
		if (getInstance() == instance) {
			returned.add(this);
		}
		for (Shape s : getShapes()) {
			returned.addAll(s.getElementsRepresentingInstance(instance));
		}
		for (Connector c : getConnectors()) {
			returned.addAll(c.getElementsRepresentingInstance(instance));
		}
		return returned;
	}

	

	
}
