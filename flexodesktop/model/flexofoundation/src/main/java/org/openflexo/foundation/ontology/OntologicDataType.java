package org.openflexo.foundation.ontology;

import com.hp.hpl.jena.rdf.model.Literal;

public enum OntologicDataType
{
	// TODO: see http://www.w3.org/TR/xmlschema-2/ to complete list

	String {
		@Override
		public Class<?> getAccessedType() {
			return String.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#string";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getString();
		}
	},
	Integer {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#integer";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getInt();
		}
	},
	Short {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#short";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getShort();
		}
	},
	Long {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#long";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getLong();
		}
	},
	Byte {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#byte";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getByte();
		}
	},
	Float {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#float";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getFloat();
		}
	},
	Double {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#double";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getDouble();
		}
	},
	Boolean {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}
		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#boolean";
		}
		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getBoolean();
		}
	};

	public abstract Class<?> getAccessedType();
	public abstract String getURI();
	public abstract Object valueFromLiteral(Literal literal);
	
	public static OntologicDataType fromURI(String uri)
	{
		for (OntologicDataType dt : values()) {
			if (dt.getURI().equals(uri)) return dt;
		}
		return null;
	}
}