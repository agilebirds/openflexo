package org.openflexo.technologyadapter.diagram.fml;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.dm.GraphicalRepresentationChanged;

@ModelEntity
@ImplementationClass(ConnectorPatternRole.ConnectorPatternRoleImpl.class)
@XMLElement
public interface ConnectorPatternRole extends GraphicalElementPatternRole<DiagramConnector, ConnectorGraphicalRepresentation>{

@PropertyIdentifier(type=GraphicalRepresentation.class)
public static final String GRAPHICAL_REPRESENTATION_KEY = "graphicalRepresentation";
@PropertyIdentifier(type=ShapeGraphicalRepresentation.class)
public static final String ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY = "artifactFromGraphicalRepresentation";
@PropertyIdentifier(type=ShapeGraphicalRepresentation.class)
public static final String ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY = "artifactToGraphicalRepresentation";
@PropertyIdentifier(type=ShapePatternRole.class)
public static final String START_SHAPE_PATTERN_ROLE_KEY = "startShapePatternRole";
@PropertyIdentifier(type=ShapePatternRole.class)
public static final String END_SHAPE_PATTERN_ROLE_KEY = "endShapePatternRole";

@Getter(value=GRAPHICAL_REPRESENTATION_KEY)
@XMLElement
public GraphicalRepresentation getGraphicalRepresentation();

@Setter(GRAPHICAL_REPRESENTATION_KEY)
public void setGraphicalRepresentation(GraphicalRepresentation graphicalRepresentation);


@Getter(value=ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY)
@XMLElement
public ShapeGraphicalRepresentation getArtifactFromGraphicalRepresentation();

@Setter(ARTIFACT_FROM_GRAPHICAL_REPRESENTATION_KEY)
public void setArtifactFromGraphicalRepresentation(ShapeGraphicalRepresentation artifactFromGraphicalRepresentation);


@Getter(value=ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY)
@XMLElement
public ShapeGraphicalRepresentation getArtifactToGraphicalRepresentation();

@Setter(ARTIFACT_TO_GRAPHICAL_REPRESENTATION_KEY)
public void setArtifactToGraphicalRepresentation(ShapeGraphicalRepresentation artifactToGraphicalRepresentation);


@Getter(value=START_SHAPE_PATTERN_ROLE_KEY)
@XMLElement
public ShapePatternRole getStartShapePatternRole();

@Setter(START_SHAPE_PATTERN_ROLE_KEY)
public void setStartShapePatternRole(ShapePatternRole startShapePatternRole);


@Getter(value=END_SHAPE_PATTERN_ROLE_KEY)
@XMLElement
public ShapePatternRole getEndShapePatternRole();

@Setter(END_SHAPE_PATTERN_ROLE_KEY)
public void setEndShapePatternRole(ShapePatternRole endShapePatternRole);


public static abstract  class ConnectorPatternRoleImpl extends GraphicalElementPatternRole<DiagramConnector, ConnectorGraphicalRepresentation>Impl implements ConnectorPatternRole
{

	private ShapeGraphicalRepresentation artifactFromGraphicalRepresentation;
	private ShapeGraphicalRepresentation artifactToGraphicalRepresentation;

	public ConnectorPatternRoleImpl() {
		super();
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
		out.append("PatternRole " + getName() + " as ConnectorSpecification from " + getVirtualModel().getReflexiveModelSlot().getName()
				+ ";", context);
		return out.toString();
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("connector");
	}

	public ShapeGraphicalRepresentation getArtifactFromGraphicalRepresentation() {
		return artifactFromGraphicalRepresentation;
	}

	public void setArtifactFromGraphicalRepresentation(ShapeGraphicalRepresentation artifactFromGraphicalRepresentation) {
		this.artifactFromGraphicalRepresentation = artifactFromGraphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, artifactFromGraphicalRepresentation));
	}

	public ShapeGraphicalRepresentation getArtifactToGraphicalRepresentation() {
		return artifactToGraphicalRepresentation;
	}

	public void setArtifactToGraphicalRepresentation(ShapeGraphicalRepresentation artifactToGraphicalRepresentation) {
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
		if (!flag && getEditionPattern().getPatternRoles(ShapePatternRole.class).size() > 0) {
			setStartShapePatternRole(getEditionPattern().getPatternRoles(ShapePatternRole.class).get(0));
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
		if (!flag && getEditionPattern().getPatternRoles(ShapePatternRole.class).size() > 0) {
			setEndShapePatternRole(getEditionPattern().getPatternRoles(ShapePatternRole.class).get(0));
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
		return getEditionPattern().getPatternRoles(ShapePatternRole.class);
	}
}
}
