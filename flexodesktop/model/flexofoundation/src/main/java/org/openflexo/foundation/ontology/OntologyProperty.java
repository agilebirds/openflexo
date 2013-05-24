package org.openflexo.foundation.ontology;

import java.util.List;

public interface OntologyProperty extends OntologyObject {

	public List<? extends OntologyProperty> getSuperProperties();

	public List<? extends OntologyProperty> getSubProperties(FlexoOntology context);

	public boolean isAnnotationProperty();

	public OntologyObject getDomain();
}
