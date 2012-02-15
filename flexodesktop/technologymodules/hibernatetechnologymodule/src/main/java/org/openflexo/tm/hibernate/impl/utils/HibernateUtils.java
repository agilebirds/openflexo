/**
 * 
 */
package org.openflexo.tm.hibernate.impl.utils;

import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.tm.hibernate.impl.enums.HibernateAttributeType;

/**
 * @author Nicolas Daniels
 * 
 */
public class HibernateUtils {

	/**
	 * Transform a DMType into its HibernateAttributeType equivalent. If no equivalence is found, null is returned.
	 * 
	 * @param dmType
	 * @return the HibernateAttributeType associated to this DMTYpe.
	 */
	public static HibernateAttributeType getHibernateAttributeTypeFromDMType(DMType dmType) {

		if (dmType == null)
			return null;

		if (dmType.isString())
			return HibernateAttributeType.STRING;
		if (dmType.isBoolean() || dmType.isBooleanPrimitive())
			return HibernateAttributeType.BOOLEAN;
		if (dmType.isInteger() || dmType.isIntegerPrimitive())
			return HibernateAttributeType.INTEGER;
		if (dmType.isDouble() || dmType.isDoublePrimitive() || dmType.isFloat() || dmType.isFloatPrimitive())
			return HibernateAttributeType.DOUBLE;
		if (dmType.isLong() || dmType.isLong())
			return HibernateAttributeType.LONG;
		if (dmType.isByte() || dmType.isBytePrimitive())
			return HibernateAttributeType.BYTES;
		if (dmType.isDate())
			return HibernateAttributeType.DATE;

		if (dmType.getBaseEntity() != null && dmType.getBaseEntity().getIsEnumeration())
			return HibernateAttributeType.ENUM;
        if (dmType.isCollection()){
            if(dmType.getParameters().get(0).getBaseEntity().getIsEnumeration()){
                return HibernateAttributeType.ENUM_SET;
            }
        }
		return null;
	}

	/**
	 * Find if this DMProperty should be transformed to a Hibernate Attribute or to a Hibernate Relationship.
	 * 
	 * @param property
	 * @return true if this property should be represented as a Hibernate Attribute, false if it should be reprensented as a Hibernate Relationship.
	 */
	public static boolean getIsHibernateAttributeRepresented(DMProperty property) {

		if (property.getType() == null)
			return true;
        if(property.getType().isCollection()){
           if(property.getType().getParameters().get(0).getBaseEntity().getIsEnumeration()){
               return true;
           }
        }
		return getHibernateAttributeTypeFromDMType(property.getType()) != null;
	}
}
