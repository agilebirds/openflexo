/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.ontology;

import com.hp.hpl.jena.rdf.model.Literal;

public enum OntologicDataType {
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
	Int {
		@Override
		public Class<?> getAccessedType() {
			return Integer.class;
		}

		@Override
		public java.lang.String getURI() {
			return "http://www.w3.org/2001/XMLSchema#int";
		}

		@Override
		public Object valueFromLiteral(Literal literal) {
			return literal.getInt();
		}
	},
	Short {
		@Override
		public Class<?> getAccessedType() {
			return Short.class;
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
			return Long.class;
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
			return Byte.class;
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
			return Float.class;
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
			return Double.class;
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
			return Boolean.class;
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

	public static OntologicDataType fromURI(String uri) {
		for (OntologicDataType dt : values()) {
			if (dt.getURI().equals(uri)) {
				return dt;
			}
		}
		return null;
	}
}