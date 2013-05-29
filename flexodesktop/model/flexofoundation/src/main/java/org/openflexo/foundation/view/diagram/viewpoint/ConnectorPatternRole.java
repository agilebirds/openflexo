package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.dm.GraphicalRepresentationChanged;
import org.openflexo.foundation.view.diagram.model.dm.GraphicalRepresentationModified;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.localization.FlexoLocalization;

public class ConnectorPatternRole extends GraphicalElementPatternRole<DiagramConnector> {

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.ConnectorGraphicalRepresentation.
	private ConnectorGraphicalRepresentation<?> _graphicalRepresentation;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation here are a org.openflexo.fge.ShapeGraphicalRepresentation.
	private Object artifactFromGraphicalRepresentation;
	private Object artifactToGraphicalRepresentation;

	public ConnectorPatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	protected void initDefaultSpecifications() {
		super.initDefaultSpecifications();
		for (GraphicalFeature<?, ?> GF : AVAILABLE_FEATURES) {
			grSpecifications.add(new GraphicalElementSpecification(this, GF, false, true));
		}
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("PatternRole " + getName() + " as Connector from " + getVirtualModel().getReflexiveModelSlot().getName() + ";", context);
		return out.toString();
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("connector");
	}

	@Override
	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(GraphicalRepresentation<?> graphicalRepresentation) {
		_graphicalRepresentation = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}

	public void updateGraphicalRepresentation(ConnectorGraphicalRepresentation<?> graphicalRepresentation) {
		if (_graphicalRepresentation != null) {
			((ConnectorGraphicalRepresentation<?>) _graphicalRepresentation).setsWith(graphicalRepresentation);
			setChanged();
			notifyObservers(new GraphicalRepresentationModified(this, graphicalRepresentation));
		} else {
			setGraphicalRepresentation(graphicalRepresentation);
		}
	}

	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(GraphicalRepresentation<?> graphicalRepresentation) {
		_graphicalRepresentation = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
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
	public Type getType() {
		return DiagramConnector.class;
	}

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = {};

	public List<ShapePatternRole> getShapePatternRoles() {
		return getEditionPattern().getShapePatternRoles();
	}
}
