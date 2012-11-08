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
package org.openflexo.dm.view.erdiagram;

import java.awt.Color;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.dm.EntityAddedToDiagram;
import org.openflexo.foundation.dm.dm.EntityRemovedFromDiagram;

public class ERDiagramRepresentation extends DefaultDrawing<ERDiagram> implements GraphicalFlexoObserver, ERDiagramConstants {

	private static final Logger logger = Logger.getLogger(ERDiagramRepresentation.class.getPackage().getName());

	private DrawingGraphicalRepresentation<ERDiagram> graphicalRepresentation;

	public ERDiagramRepresentation(ERDiagram aDiagram) {
		super(aDiagram);
		graphicalRepresentation = new DrawingGraphicalRepresentationImpl<ERDiagram>(this);
		graphicalRepresentation.setBackgroundColor(new Color(255, 255, 204));
		graphicalRepresentation.addToMouseClickControls(new ERDiagramController.ShowContextualMenuControl());

		aDiagram.addObserver(this);

		updateGraphicalObjectsHierarchy();

	}

	@Override
	public void delete() {
		getDiagram().deleteObserver(this);
		super.delete();
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		for (DMEntity entity : getDiagram().getEntities()) {
			addDrawable(entity, getDiagram());
			for (DMProperty property : entity.getOrderedProperties()) {
				addDrawable(property, entity);
			}
		}
		for (DMEntity entity : getDiagram().getEntities()) {
			for (DMProperty property : entity.getOrderedProperties()) {
				if (isRelationship(property) && !relationshipForProperty(property).isInverseDeclaration(property)) {
					addDrawable(relationshipForProperty(property), getDiagram());
				}
			}
			if (isSpecializationRepresentable(entity, entity.getParentType())) {
				addDrawable(specializationForEntity(entity, entity.getParentType()), getDiagram());
			}
			for (DMType t : entity.getImplementedTypes()) {
				if (isSpecializationRepresentable(entity, t)) {
					addDrawable(specializationForEntity(entity, t), getDiagram());
				}
			}
		}
	}

	public ERDiagram getDiagram() {
		return getModel();
	}

	@Override
	public DrawingGraphicalRepresentation<ERDiagram> getDrawingGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable) {
		return (GraphicalRepresentation<O>) buildGraphicalRepresentation(aDrawable);
	}

	private boolean isRelationship(DMProperty property) {
		if (property.getType() != null && property.getType().getBaseEntity() != null
				&& getDiagram().getEntities().contains(property.getType().getBaseEntity())) {
			// This is a relation !
			return true;
		} else {
			return false;
		}
	}

	private boolean isSpecializationRepresentable(DMEntity entity, DMType type) {
		return entity != null && type != null && type.getBaseEntity() != null && getDiagram().getEntities().contains(type.getBaseEntity());
	}

	private GraphicalRepresentation<?> buildGraphicalRepresentation(Object aDrawable) {
		if (aDrawable instanceof DMEntity) {
			return new DMEntityGR((DMEntity) aDrawable, this);
		} else if (aDrawable instanceof DMProperty) {
			return new DMPropertyGR((DMProperty) aDrawable, this);
		} else if (aDrawable instanceof RelationshipRepresentation) {
			return new DMRelationshipGR((RelationshipRepresentation) aDrawable, this);
		} else if (aDrawable instanceof EntitySpecialization) {
			return new EntitySpecializationGR((EntitySpecialization) aDrawable, this);
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDiagram()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof EntityAddedToDiagram) {
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof EntityRemovedFromDiagram) {
				updateGraphicalObjectsHierarchy();
			}
		}
	}

	private Hashtable<DMProperty, RelationshipRepresentation> relationships = new Hashtable<DMProperty, RelationshipRepresentation>();

	protected RelationshipRepresentation relationshipForProperty(DMProperty property) {
		RelationshipRepresentation returned = relationships.get(property);
		if (returned == null) {
			returned = new RelationshipRepresentation(property);
			relationships.put(property, returned);
			if (returned.getInverseProperty() != null) {
				// Also register inverse
				relationships.put(returned.getInverseProperty(), returned);
			}
		}
		return returned;
	}

	private Hashtable<DMEntity, Hashtable<DMType, EntitySpecialization>> specializations = new Hashtable<DMEntity, Hashtable<DMType, EntitySpecialization>>();

	protected EntitySpecialization specializationForEntity(DMEntity entity, DMType type) {
		Hashtable<DMType, EntitySpecialization> specializationsForEntity = specializations.get(entity);
		if (specializationsForEntity == null) {
			specializationsForEntity = new Hashtable<DMType, EntitySpecialization>();
			specializations.put(entity, specializationsForEntity);
		}

		EntitySpecialization returned = specializationsForEntity.get(type);
		if (returned == null) {
			returned = new EntitySpecialization(entity, type);
			specializationsForEntity.put(type, returned);
		}
		return returned;
	}

}
