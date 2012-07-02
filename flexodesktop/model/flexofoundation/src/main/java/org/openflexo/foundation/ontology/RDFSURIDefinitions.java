package org.openflexo.foundation.ontology;

/**
 * Defines all RDFS URI constants, as in http://www.w3.org/2000/01/rdf-schema
 * 
 * @author sylvain
 * 
 */
public interface RDFSURIDefinitions {

	public static final String RDFS_ONTOLOGY_URI = "http://www.w3.org/2000/01/rdf-schema";

	public static final String RDFS_LITERAL_URI = RDFS_ONTOLOGY_URI + "#Literal";
	public static final String RDFS_RESOURCE_URI = RDFS_ONTOLOGY_URI + "#Resource";
	public static final String RDFS_CLASS_URI = RDFS_ONTOLOGY_URI + "#Class";
	public static final String RDFS_DATATYPE_URI = RDFS_ONTOLOGY_URI + "#Datatype";
	public static final String RDFS_CONTAINER_URI = RDFS_ONTOLOGY_URI + "#Container";

	public static final String RDFS_DOMAIN_URI = RDFS_ONTOLOGY_URI + "#domain";
	public static final String RDFS_RANGE_URI = RDFS_ONTOLOGY_URI + "#range";
	public static final String RDFS_SUB_CLASS_URI = RDFS_ONTOLOGY_URI + "#subClassOf";
	public static final String RDFS_SUB_PROPERTY_URI = RDFS_ONTOLOGY_URI + "#subPropertyOf";

	public static final String RDFS_MEMBER_URI = RDFS_ONTOLOGY_URI + "#member";

	// Annotations
	public static final String RDFS_SEE_ALSO_URI = RDFS_ONTOLOGY_URI + "#seeAlso";
	public static final String RDFS_IS_DEFINED_BY_URI = RDFS_ONTOLOGY_URI + "#isDefinedBy";
	public static final String RDFS_LABEL_URI = RDFS_ONTOLOGY_URI + "#label";
	public static final String RDFS_COMMENT_URI = RDFS_ONTOLOGY_URI + "#comment";

}
