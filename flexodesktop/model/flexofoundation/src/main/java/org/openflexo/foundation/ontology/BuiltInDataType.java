package org.openflexo.foundation.ontology;

public enum BuiltInDataType implements W3URIDefinitions {
	// TODO: see http://www.w3.org/TR/xmlschema-2/ to complete list

	String {
		@Override
		public Class<?> getAccessedType() {
			return String.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_STRING_DATATYPE_URI;
		}

	},
	Integer {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_INTEGER_DATATYPE_URI;
		}

	},
	Int {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_INT_DATATYPE_URI;
		}

	},
	Short {
		@Override
		public Class<?> getAccessedType() {
			return Short.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_SHORT_DATATYPE_URI;
		}

	},
	Long {
		@Override
		public Class<?> getAccessedType() {
			return Long.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_LONG_DATATYPE_URI;
		}

	},
	Byte {
		@Override
		public Class<?> getAccessedType() {
			return Byte.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_BYTE_DATATYPE_URI;
		}

	},
	Float {
		@Override
		public Class<?> getAccessedType() {
			return Float.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_FLOAT_DATATYPE_URI;
		}

	},
	Double {
		@Override
		public Class<?> getAccessedType() {
			return Double.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_DOUBLE_DATATYPE_URI;
		}

	},
	Boolean {
		@Override
		public Class<?> getAccessedType() {
			return Boolean.class;
		}

		@Override
		public java.lang.String getURI() {
			return W3_BOOLEAN_DATATYPE_URI;
		}

	};

	public abstract Class<?> getAccessedType();

	public abstract String getURI();

	public static BuiltInDataType fromURI(String uri) {
		for (BuiltInDataType dt : values()) {
			if (dt.getURI().equals(uri)) {
				return dt;
			}
		}
		return null;
	}
}