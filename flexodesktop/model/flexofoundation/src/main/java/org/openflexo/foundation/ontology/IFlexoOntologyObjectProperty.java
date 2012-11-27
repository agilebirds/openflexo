package org.openflexo.foundation.ontology;

public interface IFlexoOntologyObjectProperty extends IFlexoOntologyStructuralProperty {

	public IFlexoOntologyConcept getRange();

	public boolean isLiteralRange();
}
