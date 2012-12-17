package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class DataPropertyPatternRole<P extends IFlexoOntologyDataProperty> extends PropertyPatternRole<P> {

	private BuiltInDataType dataType;

	public DataPropertyPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.DataProperty;
	}

	@Override
	public String getPreciseType() {
		if (getParentProperty() != null) {
			return getParentProperty().getName();
		}
		return "";
	}

	@Override
	public IFlexoOntologyDataProperty getParentProperty() {
		return (IFlexoOntologyDataProperty) super.getParentProperty();
	}

	public void setParentProperty(IFlexoOntologyDataProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public BuiltInDataType getDataType() {
		return dataType;
	}

	public void setDataType(BuiltInDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public ActorReference<P> makeActorReference(P object, EditionPatternReference epRef) {
		return new ConceptActorReference<P>(object, this, epRef);
	}
}
