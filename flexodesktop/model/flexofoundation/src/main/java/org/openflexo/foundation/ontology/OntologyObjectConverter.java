package org.openflexo.foundation.ontology;

import org.openflexo.xmlcode.StringEncoder.Converter;

public class OntologyObjectConverter extends Converter<OntologyObject> {
	private FlexoOntology ontology;

	public OntologyObjectConverter(FlexoOntology ontology) {
		super(OntologyObject.class);
		this.ontology = ontology;
	}

	@Override
	public OntologyObject convertFromString(String value) {
		return ontology.getOntologyObject(value);
	}

	@Override
	public String convertToString(OntologyObject value) {
		return value.getURI();
	};
}