package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ontology.OntologicDataType;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public class DataPropertyPatternRole extends PropertyPatternRole<OntologyDataProperty> {

	private OntologicDataType dataType;

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
	public Class<OntologyDataProperty> getAccessedClass() {
		return OntologyDataProperty.class;
	}

	@Override
	public OntologyDataProperty getParentProperty() {
		return (OntologyDataProperty) super.getParentProperty();
	}

	public void setParentProperty(OntologyDataProperty ontologyProperty) {
		super.setParentProperty(ontologyProperty);
	}

	public OntologicDataType getDataType() {
		return dataType;
	}

	public void setDataType(OntologicDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public ActorReference<OntologyDataProperty> makeActorReference(OntologyDataProperty object, EditionPatternReference epRef) {
		return new ConceptActorReference<OntologyDataProperty>(object, this, epRef);
	}
}
