package org.openflexo.foundation.ontology;

import org.openflexo.xmlcode.StringEncoder.Converter;

public class OntologyObjectConverter extends Converter<IFlexoOntologyConcept> {
	private IFlexoOntology ontology;

	public OntologyObjectConverter(IFlexoOntology ontology) {
		super(IFlexoOntologyConcept.class);
		this.ontology = ontology;
	}

	@Override
	public IFlexoOntologyConcept convertFromString(String value) {
		return ontology.getOntologyObject(value);
	}

	@Override
	public String convertToString(IFlexoOntologyConcept value) {
		return value.getURI();
	};
}