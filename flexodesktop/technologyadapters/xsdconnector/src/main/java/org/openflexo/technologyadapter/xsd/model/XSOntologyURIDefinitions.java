package org.openflexo.technologyadapter.xsd.model;

public interface XSOntologyURIDefinitions {

	public static final String XS_ONTOLOGY_URI = "http://www.openflexo.org/xsd.owl";

	public static final String XS_DATAPROPERTY_NAMEPREFIX = "hasValue";

	public static final String XS_ATTRIBUTEPROPERTY_NAMEPREFIX = "hasAttribute";

	public static final String XS_HASCHILD_PROPERTY_NAME = "hasChild";

	public static final String XS_HASELEMENT_PROPERTY_URI = XS_ONTOLOGY_URI + "#" + XS_HASCHILD_PROPERTY_NAME;

	public static final String XS_HASPARENT_PROPERTY_NAME = "hasParent";

	public static final String XS_ISPARTOFELEMENT_PORPERTY_URI = XS_ONTOLOGY_URI + "#" + XS_HASPARENT_PROPERTY_NAME;

	public static final String XS_THING_URI = XS_ONTOLOGY_URI + "#Thing";

}
