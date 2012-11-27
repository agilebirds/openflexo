package org.openflexo.foundation.ontology;

import java.util.List;

public interface IFlexoOntologyStructuralProperty extends IFlexoOntologyConcept {

	public List<? extends IFlexoOntologyStructuralProperty> getSuperProperties();

	public List<? extends IFlexoOntologyStructuralProperty> getSubProperties(IFlexoOntology context);

	public boolean isAnnotationProperty();

	public IFlexoOntologyConcept getDomain();
}
