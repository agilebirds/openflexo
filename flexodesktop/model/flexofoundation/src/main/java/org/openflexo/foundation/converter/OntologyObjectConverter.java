package org.openflexo.foundation.converter;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

@Deprecated
public class OntologyObjectConverter extends Converter<IFlexoOntologyConcept> {
	private final IFlexoOntology ontology;

	public OntologyObjectConverter(IFlexoOntology ontology) {
		super(IFlexoOntologyConcept.class);
		this.ontology = ontology;
	}

	@Override
	public IFlexoOntologyConcept convertFromString(String value, ModelFactory factory) {
		return ontology.getOntologyObject(value);
	}

	@Override
	public String convertToString(IFlexoOntologyConcept value) {
		return value.getURI();
	};
}