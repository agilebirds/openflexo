package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

public abstract class DataPropertyPatternRole<P extends IFlexoOntologyDataProperty> extends PropertyPatternRole<P> {

	private BuiltInDataType dataType;

	public DataPropertyPatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getParentProperty() == null) {
			return IFlexoOntologyDataProperty.class;
		}
		return super.getType();
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
