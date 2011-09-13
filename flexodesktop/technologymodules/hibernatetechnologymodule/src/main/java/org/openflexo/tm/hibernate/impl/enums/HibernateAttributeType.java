/**
 * 
 */
package org.openflexo.tm.hibernate.impl.enums;

import java.util.Date;
import java.util.Enumeration;

/**
 * Defines all hibernate attribute types available
 * 
 * @author Nicolas Daniels
 */
public enum HibernateAttributeType {
	/** Limited length text type. */
	STRING,

	/** Unlimited length text type. */
	TEXT,

	/** Enumeration type. */
	ENUM,

	/** Boolean type. */
	BOOLEAN,

	/** Date type. */
	DATE,

	/** Integer type. */
	INTEGER,

	/** Decimal number type. */
	DOUBLE,

	/** Big integer type. */
	LONG,

	/** Bytes type. */
	BYTES;


	/**
	 * Return the java class link to this type
	 * 
	 * @return the java class link to this type
	 */
	public Class<? extends Object> getAsClass() {
		switch (this) {
			case DOUBLE:
				return Double.class;
			case LONG:
				return Long.class;
			case INTEGER:
				return Integer.class;
			case BOOLEAN:
				return Boolean.class;
			case ENUM:
				return Enumeration.class;
			case STRING:
			case TEXT:
				return String.class;
			case DATE:
				return Date.class;
			default:
				return Object.class;
		}
	}
}
