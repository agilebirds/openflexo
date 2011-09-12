package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

public class ConnectorPatternRole extends GraphicalElementPatternRole {

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.ConnectorGraphicalRepresentation.
	private Object _graphicalRepresentation;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation here are a org.openflexo.fge.ShapeGraphicalRepresentation.
	private Object artifactFromGraphicalRepresentation;
	private Object artifactToGraphicalRepresentation;


    @Override
	public PatternRoleType getType()
	{
		return PatternRoleType.Connector;
	}


	@Override
	public String getPreciseType()
	{
		return FlexoLocalization.localizedForKey("connector");
	}

	@Override
	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}
	
	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
	}
	
	public Object getArtifactFromGraphicalRepresentation() 
	{
		return artifactFromGraphicalRepresentation;
	}

	public void setArtifactFromGraphicalRepresentation(
			Object artifactFromGraphicalRepresentation) 
	{
		this.artifactFromGraphicalRepresentation = artifactFromGraphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, artifactFromGraphicalRepresentation));
	}

	public Object getArtifactToGraphicalRepresentation()
	{
		return artifactToGraphicalRepresentation;
	}

	public void setArtifactToGraphicalRepresentation(
			Object artifactToGraphicalRepresentation) 
	{
		this.artifactToGraphicalRepresentation = artifactToGraphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, artifactToGraphicalRepresentation));
	}
	
	public ShapePatternRole getStartShape()
	{
		for (EditionScheme es : getEditionPattern().getEditionSchemes()) {
			for (EditionAction action : es.getActions()) {
				if ((action.getPatternRole() == this) && (action instanceof AddConnector)) {
					AddConnector addConnector = (AddConnector)action;
					for (PatternRole r : getEditionPattern().getPatternRoles()) {
						if ((r instanceof ShapePatternRole)
								&& (addConnector._getFromShape() != null) 
								&& addConnector._getFromShape().equals(r.getPatternRoleName())) {
							return (ShapePatternRole)r;
						}
					}
				}
			}
		}
			
		return null;
	}

	public ShapePatternRole getEndShape()
	{
		for (EditionScheme es : getEditionPattern().getEditionSchemes()) {
			for (EditionAction action : es.getActions()) {
				if ((action.getPatternRole() == this) && (action instanceof AddConnector)) {
					AddConnector addConnector = (AddConnector)action;
					for (PatternRole r : getEditionPattern().getPatternRoles()) {
						if ((r instanceof ShapePatternRole)
								&& (addConnector._getToShape() != null) 
								&& addConnector._getToShape().equals(r.getPatternRoleName())) {
							return (ShapePatternRole)r;
						}
					}
				}
			}
		}

		return null;
	}
	

}
