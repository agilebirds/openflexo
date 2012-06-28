package org.openflexo.antar.binding;

import java.lang.reflect.Type;

public interface CustomType extends Type {

	public String simpleRepresentation();

	public String fullQualifiedRepresentation();

	public Class getBaseClass();

	/**
	 * Determines if the class or interface represented by this <code>CustomType</code> object is either the same as, or is a superclass or
	 * superinterface of, the class or interface represented by the specified <code>anOtherType</code> parameter. It returns
	 * <code>true</code> if so; otherwise false<br>
	 * This method also tried to resolve generics before to perform the assignability test
	 * 
	 * @param aType
	 * @param anOtherType
	 * @param permissive
	 *            is a flag indicating if basic conversion between primitive types is allowed: for example, an int may be assign to a float
	 *            value after required conversion.
	 * @return
	 */
	public boolean isTypeAssignableFrom(Type aType, boolean permissive);
}
