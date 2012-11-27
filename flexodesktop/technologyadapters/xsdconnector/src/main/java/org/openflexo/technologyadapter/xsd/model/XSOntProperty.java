package org.openflexo.technologyadapter.xsd.model;

import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

public abstract class XSOntProperty extends AbstractXSOntObject implements IFlexoOntologyStructuralProperty, XSOntologyURIDefinitions {

	private AbstractXSOntObject domain;
	private boolean noDomainFoundYet = true;

	protected XSOntProperty(XSOntology ontology, String name, String uri) {
		super(ontology, name, uri);
		domain = ontology.getThingConcept();
	}

	@Override
	public boolean isAnnotationProperty() {
		return false;
	}

	@Override
	public IFlexoOntologyConcept getDomain() {
		return domain;
	}

	public void newDomainFound(AbstractXSOntObject domain) {
		if (noDomainFoundYet) {
			this.domain = domain;
			noDomainFoundYet = false;
		} else {
			this.domain = getOntology().getThingConcept();
		}
	}

	public void resetDomain() {
		this.domain = getOntology().getThingConcept();
		noDomainFoundYet = true;
	}

}
