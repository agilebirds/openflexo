package org.openflexo.foundation.viewpoint;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.foundation.view.ViewConnector;
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
	public PatternRoleType getType() {
		return PatternRoleType.Connector;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("connector");
	}

	@Override
	public Object getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(Object graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}

	public void updateGraphicalRepresentation(Object graphicalRepresentation) {
		if (_graphicalRepresentation != null) {
			System.out.println("OK, i update, what about notification ?");
			((ConnectorGraphicalRepresentation) _graphicalRepresentation)
					.setsWith((ConnectorGraphicalRepresentation) graphicalRepresentation);
			setChanged();
			notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
		} else {
			setGraphicalRepresentation(graphicalRepresentation);
		}
	}

	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
	}

	public Object getArtifactFromGraphicalRepresentation() {
		return artifactFromGraphicalRepresentation;
	}

	public void setArtifactFromGraphicalRepresentation(Object artifactFromGraphicalRepresentation) {
		this.artifactFromGraphicalRepresentation = artifactFromGraphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, artifactFromGraphicalRepresentation));
	}

	public Object getArtifactToGraphicalRepresentation() {
		return artifactToGraphicalRepresentation;
	}

	public void setArtifactToGraphicalRepresentation(Object artifactToGraphicalRepresentation) {
		this.artifactToGraphicalRepresentation = artifactToGraphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, artifactToGraphicalRepresentation));
	}

	private ShapePatternRole startShapePatternRole;
	private ShapePatternRole endShapePatternRole;

	public ShapePatternRole getStartShapePatternRole() {
		return startShapePatternRole;
	}

	public void setStartShapePatternRole(ShapePatternRole startShapePatternRole) {
		this.startShapePatternRole = startShapePatternRole;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this,
				startShapePatternRole != null ? startShapePatternRole.getGraphicalRepresentation() : artifactFromGraphicalRepresentation));
	}

	public boolean getStartShapeAsDefinedInAction() {
		return getStartShapePatternRole() == null;
	}

	public void setStartShapeAsDefinedInAction(boolean flag) {
		if (!flag && getEditionPattern().getShapePatternRoles().size() > 0) {
			setStartShapePatternRole(getEditionPattern().getShapePatternRoles().get(0));
		} else {
			// System.out.println("setStartShapePatternRole with null");
			setStartShapePatternRole(null);
		}
	}

	public ShapePatternRole getEndShapePatternRole() {
		return endShapePatternRole;
	}

	public void setEndShapePatternRole(ShapePatternRole endShapePatternRole) {
		this.endShapePatternRole = endShapePatternRole;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this,
				endShapePatternRole != null ? endShapePatternRole.getGraphicalRepresentation() : artifactToGraphicalRepresentation));
	}

	public boolean getEndShapeAsDefinedInAction() {
		return getEndShapePatternRole() == null;
	}

	public void setEndShapeAsDefinedInAction(boolean flag) {
		if (!flag && getEditionPattern().getShapePatternRoles().size() > 0) {
			setEndShapePatternRole(getEditionPattern().getShapePatternRoles().get(0));
		} else {
			// System.out.println("setEndShapePatternRole with null");
			setEndShapePatternRole(null);
		}
	}

	/*public ShapePatternRole getStartShape() {
		for (EditionScheme es : getEditionPattern().getEditionSchemes()) {
			for (EditionAction action : es.getActions()) {
				if ((action.getPatternRole() == this) && (action instanceof AddConnector)) {
					AddConnector addConnector = (AddConnector) action;
					for (PatternRole r : getEditionPattern().getPatternRoles()) {
						if ((r instanceof ShapePatternRole) && (addConnector.getFromShape() != null)
								&& addConnector.getFromShape().toString().equals(r.getPatternRoleName())) {
							return (ShapePatternRole) r;
						}
					}
				}
			}
		}

		return null;
	}

	public ShapePatternRole getEndShape() {
		for (EditionScheme es : getEditionPattern().getEditionSchemes()) {
			for (EditionAction action : es.getActions()) {
				if ((action.getPatternRole() == this) && (action instanceof AddConnector)) {
					AddConnector addConnector = (AddConnector) action;
					for (PatternRole r : getEditionPattern().getPatternRoles()) {
						if ((r instanceof ShapePatternRole) && (addConnector.getToShape() != null)
								&& addConnector.getToShape().toString().equals(r.getPatternRoleName())) {
							return (ShapePatternRole) r;
						}
					}
				}
			}
		}

		return null;
	}*/

	@Override
	public Class<?> getAccessedClass() {
		return ViewConnector.class;
	}

}
