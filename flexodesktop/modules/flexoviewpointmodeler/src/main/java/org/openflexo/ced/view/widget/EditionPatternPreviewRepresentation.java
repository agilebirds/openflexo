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
package org.openflexo.ced.view.widget;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.calc.ConnectorPatternRole;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.foundation.ontology.calc.GraphicalRepresentationChanged;
import org.openflexo.foundation.ontology.calc.PatternRole;
import org.openflexo.foundation.ontology.calc.ShapePatternRole;
import org.openflexo.foundation.ontology.calc.PatternRole.PatternRoleType;


public class EditionPatternPreviewRepresentation extends DefaultDrawing<EditionPattern> implements GraphicalFlexoObserver, EditionPatternPreviewConstants {

	private static final Logger logger = Logger.getLogger(EditionPatternPreviewRepresentation.class.getPackage().getName());
	
	private EditionPatternPreviewShemaGR graphicalRepresentation;
	
	private Boolean ignoreNotifications = true;

	private Hashtable<PatternRole,EditionPatternPreviewShapeGR> shapesGR;
	private Hashtable<PatternRole,EditionPatternPreviewConnectorGR> connectorsGR;

	public EditionPatternPreviewRepresentation(EditionPattern anEditionPattern)
	{
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
	public void delete()
	{
		if (graphicalRepresentation != null) graphicalRepresentation.delete();
		if (getEditionPattern() != null) getEditionPattern().deleteObserver(this);
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			role.deleteObserver(this);
		}
	}
	
	@Override
	protected void beginUpdateObjectHierarchy()
	{
		ignoreNotifications = true;
		super.beginUpdateObjectHierarchy();
	}
	
	@Override
	protected void endUpdateObjectHierarchy()
	{
		super.endUpdateObjectHierarchy();
		ignoreNotifications = false;
	}
	
	protected boolean ignoreNotifications()
	{
		if (ignoreNotifications == null) return true;
		return ignoreNotifications;
	}
	
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			if (role instanceof ShapePatternRole) {
				addDrawable(role, getEditionPattern());
			}
		}
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			if (role instanceof ConnectorPatternRole) {
				if (((ConnectorPatternRole)role).getStartShape() == null) addDrawable(getFromArtifact((ConnectorPatternRole)role), getEditionPattern());
				if (((ConnectorPatternRole)role).getEndShape() == null) addDrawable(getToArtifact((ConnectorPatternRole)role), getEditionPattern());
				addDrawable(role, getEditionPattern());
			}
		}
	}
	
	public EditionPattern getEditionPattern()
	{
		return getModel();
	}

	@Override
	public EditionPatternPreviewShemaGR getDrawingGraphicalRepresentation()
	{
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new EditionPatternPreviewShemaGR(this);
		}
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		if (aDrawable instanceof PatternRole) {
			PatternRole patternRole = (PatternRole)aDrawable;
			if (patternRole instanceof ShapePatternRole) {
				EditionPatternPreviewShapeGR returned = shapesGR.get(patternRole);
				if (returned == null) {
					returned = buildGraphicalRepresentationForShape((ShapePatternRole)patternRole);
					shapesGR.put(patternRole, returned);
					return (GraphicalRepresentation<O>)returned;
				}
				return (GraphicalRepresentation<O>)returned;
			}
			else if (patternRole instanceof ConnectorPatternRole) {
				EditionPatternPreviewConnectorGR returned = connectorsGR.get(patternRole);
				if (returned == null) {
					returned = buildGraphicalRepresentation((ConnectorPatternRole)patternRole);
					connectorsGR.put(patternRole, returned);
					return (GraphicalRepresentation<O>)returned;
				}
				return (GraphicalRepresentation<O>)returned;
			}
		}
		else if (aDrawable instanceof ConnectorFromArtifact) {
			ConnectorFromArtifact connector = (ConnectorFromArtifact)aDrawable;
			return (GraphicalRepresentation<O>) connector.getGraphicalRepresentation();
		}
		else if (aDrawable instanceof ConnectorToArtifact) {
			ConnectorToArtifact connector = (ConnectorToArtifact)aDrawable;
			return (GraphicalRepresentation<O>) connector.getGraphicalRepresentation();
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}

	private EditionPatternPreviewShapeGR buildGraphicalRepresentationForShape(ShapePatternRole patternRole)
	{
		patternRole.addObserver(this);
		if (patternRole.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			EditionPatternPreviewShapeGR graphicalRepresentation = new EditionPatternPreviewShapeGR(patternRole,this);
			graphicalRepresentation.setsWith(
					(ShapeGraphicalRepresentation)patternRole.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
			return graphicalRepresentation;
		}
		EditionPatternPreviewShapeGR graphicalRepresentation = new EditionPatternPreviewShapeGR(patternRole,this);
		patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private EditionPatternPreviewConnectorGR buildGraphicalRepresentation(ConnectorPatternRole patternRole)
	{
		patternRole.addObserver(this);
		if (patternRole.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			EditionPatternPreviewConnectorGR graphicalRepresentation = new EditionPatternPreviewConnectorGR(patternRole,this);
			graphicalRepresentation.setsWith(
					(ConnectorGraphicalRepresentation)patternRole.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
			return graphicalRepresentation;
		}
		EditionPatternPreviewConnectorGR graphicalRepresentation = new EditionPatternPreviewConnectorGR(patternRole,this);
		patternRole._setGraphicalRepresentationNoNotification(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
		System.out.println("Hop, je recois la notification "+dataModification);
		if (dataModification instanceof GraphicalRepresentationChanged) {
			PatternRole patternRole = ((GraphicalRepresentationChanged)dataModification).getPatternRole();
			if (patternRole.getType() == PatternRoleType.Shape) {
				System.out.println("Je remplace la shape");
				EditionPatternPreviewShapeGR returned = shapesGR.get(patternRole);
				if (returned != null) returned.delete();
				shapesGR.remove(patternRole);
			}
			else if (patternRole.getType() == PatternRoleType.Connector) {
				System.out.println("Je remplace le connecteur");
				EditionPatternPreviewConnectorGR returned = connectorsGR.get(patternRole);
				if (returned != null) returned.delete();
				connectorsGR.remove(patternRole);
			}
			//invalidateGraphicalObjectsHierarchy(getEditionPattern());
			updateDrawable(patternRole);
			//updateGraphicalObjectsHierarchy();
			getDrawingGraphicalRepresentation().notifyDrawingNeedsToBeRedrawn();
		}
	}
	
	protected ConnectorFromArtifact getFromArtifact(ConnectorPatternRole connector)
	{
		ConnectorFromArtifact returned = fromArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorFromArtifact(connector);
			fromArtifacts.put(connector, returned);
		}
		return returned;
	}
	
	protected ConnectorToArtifact getToArtifact(ConnectorPatternRole connector)
	{
		ConnectorToArtifact returned = toArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorToArtifact(connector);
			toArtifacts.put(connector, returned);
		}
		return returned;
	}
	
	private Hashtable<PatternRole,ConnectorFromArtifact> fromArtifacts;
	private Hashtable<PatternRole,ConnectorToArtifact> toArtifacts;
	
	protected class ConnectorFromArtifact {
		
		private EditionPatternConnectorFromArtefactGR gr;
		private ConnectorPatternRole connector;
		
		protected ConnectorFromArtifact(ConnectorPatternRole aConnector)
		{
			connector = aConnector;
			gr = null;
		}
		
		protected EditionPatternConnectorFromArtefactGR getGraphicalRepresentation()
		{
			if (gr == null) {
				gr = new EditionPatternConnectorFromArtefactGR(this,EditionPatternPreviewRepresentation.this);
				if (connector.getArtifactFromGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					ShapeGraphicalRepresentation storedGR = (ShapeGraphicalRepresentation)connector.getArtifactFromGraphicalRepresentation();
					gr.setsWith(
							storedGR,
							GraphicalRepresentation.Parameters.text);
				}
				connector.setArtifactFromGraphicalRepresentation(gr);
			}
			return gr;			
		}
 	}
	
	protected class ConnectorToArtifact {
		
		private EditionPatternConnectorToArtefactGR gr;
		private ConnectorPatternRole connector;
		
		protected ConnectorToArtifact(ConnectorPatternRole aConnector)
		{
			connector = aConnector;
			gr = null;
		}
		
		protected EditionPatternConnectorToArtefactGR getGraphicalRepresentation()
		{
			if (gr == null) {
				gr = new EditionPatternConnectorToArtefactGR(this,EditionPatternPreviewRepresentation.this);
				if (connector.getArtifactToGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
					gr.setsWith(
							(ShapeGraphicalRepresentation)connector.getArtifactToGraphicalRepresentation(),
							GraphicalRepresentation.Parameters.text);
				}
				connector.setArtifactToGraphicalRepresentation(gr);
			}
			return gr;			
		}
		
	}
	
	protected ShapeGraphicalRepresentation<?> getStartShape(ConnectorPatternRole connector)
	{
		Object startShape = connector.getStartShape();
		if (startShape == null) startShape = getFromArtifact(connector);
		return (ShapeGraphicalRepresentation<?>)getGraphicalRepresentation(startShape);
	}

	protected ShapeGraphicalRepresentation<?> getEndShape(ConnectorPatternRole connector)
	{
		Object endShape = connector.getEndShape();
		if (endShape == null) endShape = getToArtifact(connector);
		return (ShapeGraphicalRepresentation<?>)getGraphicalRepresentation(endShape);
	}
	
}
