package org.openflexo.foundation.ontology;

public interface OntologyObjectProperty extends OntologyProperty {

	public OntologyObject getRange();

	public boolean isLiteralRange();
}
