package org.openflexo.foundation.ontology;

public interface W3URIDefinitions {

	public static final String W3_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	public static final String W3_URI = W3_NAMESPACE;
	// TODO check if correct, namespace doesn't have same format restrictions as URI

	public static final String W3_ANYTYPE_URI = W3_URI + "#anyType";

	public static final String W3_STRING_DATATYPE_URI = W3_URI + "#string";

	public static final String W3_INTEGER_DATATYPE_URI = W3_URI + "#integer";

	public static final String W3_INT_DATATYPE_URI = W3_URI + "#int";

	public static final String W3_SHORT_DATATYPE_URI = W3_URI + "#short";

	public static final String W3_LONG_DATATYPE_URI = W3_URI + "#long";

	public static final String W3_BYTE_DATATYPE_URI = W3_URI + "#byte";

	public static final String W3_FLOAT_DATATYPE_URI = W3_URI + "#float";

	public static final String W3_DOUBLE_DATATYPE_URI = W3_URI + "#double";

	public static final String W3_BOOLEAN_DATATYPE_URI = W3_URI + "#boolean";

}
