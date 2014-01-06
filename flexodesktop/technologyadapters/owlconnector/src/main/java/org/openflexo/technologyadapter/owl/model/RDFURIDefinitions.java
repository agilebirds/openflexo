package org.openflexo.technologyadapter.owl.model;

/**
 * Defines all RDF URI constants, as in http://www.w3.org/1999/02/22-rdf-syntax-ns
 * 
 * @author sylvain
 * 
 */
public interface RDFURIDefinitions {

	public static final String RDF_ONTOLOGY_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns";

	public static final String RDF_PROPERTY_URI = RDF_ONTOLOGY_URI + "#Property";

	public static final String TYPE_URI = RDF_ONTOLOGY_URI + "#type";
	public static final String VALUE_URI = RDF_ONTOLOGY_URI + "#value";
	public static final String PREDICATE_URI = RDF_ONTOLOGY_URI + "#predicate";
	public static final String OBJECT_URI = RDF_ONTOLOGY_URI + "#object";
	public static final String SUBJECT_URI = RDF_ONTOLOGY_URI + "#subject";
	public static final String REST_URI = RDF_ONTOLOGY_URI + "#rest";
	public static final String FIRST_URI = RDF_ONTOLOGY_URI + "#first";

}
