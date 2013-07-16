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
package org.openflexo.vpm.view.widget;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternPreviewRepresentation extends DrawingImpl<EditionPattern> implements GraphicalFlexoObserver,
		EditionPatternPreviewConstants {

	private static final Logger logger = Logger.getLogger(EditionPatternPreviewRepresentation.class.getPackage().getName());

	private EditionPatternPreviewShemaGR graphicalRepresentation;

	private Boolean ignoreNotifications = true;

	private Hashtable<PatternRole, EditionPatternPreviewShapeGR> shapesGR;
	private Hashtable<PatternRole, EditionPatternPreviewConnectorGR> connectorsGR;

	public EditionPatternPreviewRepresentation(EditionPattern anEditionPattern) {
		super(anEditionPattern);

		shapesGR = new Hashtable<PatternRole, EditionPatternPreviewShapeGR>();
		connectorsGR = new Hashtable<PatternRole, EditionPatternPreviewConnectorGR>();

		fromArtifacts = new Hashtable<PatternRole, ConnectorFromArtifact>();
		toArtifacts = new Hashtable<PatternRole, ConnectorToArtifact>();

		anEditionPattern.addObserver(this);
		updateGraphicalObjectsHierarchy();
		ignoreNotifications = false;
	}

	@Override
	public void delete() {

		if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}
		if (getEditionPattern() != null) {
			getEditionPattern().deleteObserver(this);
		}
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			role.deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void beginUpdateObjectHierarchy() {
		ignoreNotifications = true;
		super.beginUpdateObjectHierarchy();
	}

	@Override
	protected void endUpdateObjectHierarchy() {
		super.endUpdateObjectHierarchy();
		ignoreNotifications = false;
	}

	protected boolean ignoreNotifications() {
		if (ignoreNotifications == null) {
			return true;
		}
		return ignoreNotifications;
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			if (role instanceof ShapePatternRole) {
				if (((ShapePatternRole) role).getParentShapeAsDefinedInAction()) {
					addDrawable(role, getEditionPattern());
					// System.out.println("Add shape " + role.getPatternRoleName() + " under EditionPattern");
				} else {
					addDrawable(role, ((ShapePatternRole) role).getParentShapePatternRole());
					// System.out.println("Add shape " + role.getPatternRoleName() + " under "
					// + ((ShapePatternRole) role).getParentShapePatternRole().getPatternRoleName());
				}
			}
		}
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			if (role instanceof ConnectorPatternRole) {
				if (((ConnectorPatternRole) role).getStartShapePatternRole() == null) {
					addDrawable(getFromArtifact((ConnectorPatternRole) role), getEditionPattern());
					// System.out.println("Add From artifact under EditionPattern");
				}
				if (((ConnectorPatternRole) role).getEndShapePatternRole() == null) {
					addDrawable(getToArtifact((ConnectorPatternRole) role), getEditionPattern());
					// System.out.println("Add To artifact under EditionPattern");
				}
				// System.out.println("Add connector " + role.getPatternRoleName() + " under EditionPattern");
				addDrawable(role, getEditionPattern());
			}
		}
	}

	public EditionPattern getEditionPattern() {
		return getModel();
	}

	@Override
	public EditionPatternPreviewShemaGR getDrawingGraphicalRepresentation() {
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new EditionPatternPreviewShemaGR(this);
		}
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation retrieveGraphicalRepresentation(O aDrawable) {
		if (aDrawable instanceof PatternRole) {
			PatternRole patternRole = (PatternRole) aDrawable;
			if (patternRole instanceof ShapePatternRole) {
				EditionPatternPreviewShapeGR returned = shapesGR.get(patternRole);
				if (returned == null || returned.isDeleted()) {
					returned = buildGraphicalRepresentationForShape((ShapePatternRole) patternRole);
					shapesGR.put(patternRole, returned);
					return (GraphicalRepresentation) returned;
				}
				return (GraphicalRepresentation) returned;
			} else if (patternRole instanceof ConnectorPatternRole) {
				EditionPatternPreviewConnectorGR returned = connectorsGR.get(patternRole);
				if (returned == null || returned.isDeleted()) {
					returned = buildGraphicalRepresentationForConnector((ConnectorPatternRole) patternRole);
					connectorsGR.put(patternRole, returned);
					return (GraphicalRepresentation) returned;
				}
				return (GraphicalRepresentation) returned;
			}
		} else if (aDrawable instanceof ConnectorFromArtifact) {
			ConnectorFromArtifact connector = (ConnectorFromArtifact) aDrawable;
			return (GraphicalRepresentation) connector.getGraphicalRepresentation();
		} else if (aDrawable instanceof ConnectorToArtifact) {
			ConnectorToArtifact connector = (ConnectorToArtifact) aDrawable;
			return (GraphicalRepresentation) connector.getGraphicalRepresentation();
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	private EditionPatternPreviewShapeGR buildGraphicalRepresentationForShape(ShapePatternRole patternRole) {
		// System.out.println("Build new EditionPatternPreviewShapeGR for shape " + patternRole.getPatternRoleName());
		patternRole.addObserver(this);
		if (patternRole.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			EditionPatternPreviewShapeGR graphicalRepresentation = new EditionPatternPreviewShapeGR(patternRole, this);
			((ShapeGraphicalRepresentation) patternRole.getGraphicalRepresentation()).setValidated(false);
			graphicalRepresentation.setsWith((GraphicalRepresentation) patternRole.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
			return graphicalRepresentation;
		}
		EditionPatternPreviewShapeGR graphicalRepresentation = new EditionPatternPreviewShapeGR(patternRole, this);
		patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private EditionPatternPreviewConnectorGR buildGraphicalRepresentationForConnector(ConnectorPatternRole patternRole) {
		// System.out.println("Build new EditionPatternPreviewConnectorGR for connector " + patternRole.getPatternRoleName());
		// System.out.println("start shape = " + getStartShape(patternRole));
		// System.out.println("end shape = " + getEndShape(patternRole));
		patternRole.addObserver(this);
		if (patternRole.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			EditionPatternPreviewConnectorGR graphicalRepresentation = new EditionPatternPreviewConnectorGR(patternRole, this);
			((ConnectorGraphicalRepresentation) patternRole.getGraphicalRepresentation()).setValidated(false);
			graphicalRepresentation.setsWith((ConnectorGraphicalRepresentation) patternRole.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
			return graphicalRepresentation;
		}
		EditionPatternPreviewConnectorGR graphicalRepresentation = new EditionPatternPreviewConnectorGR(patternRole, this);
		patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (ignoreNotifications()) {
			return;
		}
		updateGraphicalObjectsHierarchy();

	}

	protected ConnectorFromArtifact getFromArtifact(ConnectorPatternRole connector) {
		ConnectorFromArtifact returned = fromArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorFromArtifact(connector);
			fromArtifacts.put(connector, returned);
		}
		return returned;
	}

	protected ConnectorToArtifact getToArtifact(ConnectorPatternRole connector) {
		ConnectorToArtifact returned = toArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorToArtifact(connector);
			toArtifacts.put(connector, returned);
		}
		return returned;
	}

	private Hashtable<PatternRole, ConnectorFromArtifact> fromArtifacts;
	private Hashtable<PatternRole, ConnectorToArtifact> toArtifacts;

	protected class ConnectorFromArtifact {

		private EditionPatternConnectorFromArtefactGR gr;
		private ConnectorPatternRole connector;

		protected ConnectorFromArtifact(ConnectorPatternRole aConnector) {
			connector = aConnector;
			gr = null;
		}

		protected EditionPatternConnectorFromArtefactGR getGraphicalRepresentation() {
			if (gr == null || gr.isDeleted()) {
				gr = new EditionPatternConnectorFromArtefactGR(this, EditionPatternPreviewRepresentation.this);
				if (connector.getArtifactFromGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					GraphicalRepresentation storedGR = (GraphicalRepresentation) connector.getArtifactFromGraphicalRepresentation();
					gr.setsWith(storedGR, GraphicalRepresentation.Parameters.text);
				}
				connector.setArtifactFromGraphicalRepresentation(gr);
			}
			return gr;
		}
	}

	protected class ConnectorToArtifact {

		private EditionPatternConnectorToArtefactGR gr;
		private ConnectorPatternRole connector;

		protected ConnectorToArtifact(ConnectorPatternRole aConnector) {
			connector = aConnector;
			gr = null;
		}

		protected EditionPatternConnectorToArtefactGR getGraphicalRepresentation() {
			if (gr == null || gr.isDeleted()) {
				gr = new EditionPatternConnectorToArtefactGR(this, EditionPatternPreviewRepresentation.this);
				if (connector.getArtifactToGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					gr.setsWith((GraphicalRepresentation) connector.getArtifactToGraphicalRepresentation(),
							GraphicalRepresentation.Parameters.text);
				}
				connector.setArtifactToGraphicalRepresentation(gr);
			}
			return gr;
		}

	}

	protected ShapeGraphicalRepresentation getStartShape(ConnectorPatternRole connector) {
		Object startShape = connector.getStartShapePatternRole();
		if (startShape == null) {
			startShape = getFromArtifact(connector);
		}
		return (ShapeGraphicalRepresentation) getGraphicalRepresentation(startShape);
	}

	protected ShapeGraphicalRepresentation getEndShape(ConnectorPatternRole connector) {
		Object endShape = connector.getEndShapePatternRole();
		if (endShape == null) {
			endShape = getToArtifact(connector);
		}
		return (ShapeGraphicalRepresentation) getGraphicalRepresentation(endShape);
	}

}
