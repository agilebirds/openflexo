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
package org.openflexo.javaparser;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMVisibilityType;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.javaparser.FJPTypeResolver.CrossReferencedEntitiesException;

import com.thoughtworks.qdox.model.Type;

/**
 * Utility class used to perform mapping between FJP model and Flexo DM model (maps source code on DM-model, ie DMEntity, DMProperty, ...)
 * 
 * @author sylvain
 * 
 */
public class FJPDMMapper {

	private static final Logger logger = Logger.getLogger(FJPDMMapper.class.getPackage().getName());

	public static Vector<DMMethod> searchForMethods(FJPJavaClass aClass, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean importReferencedEntities, Vector<String> excludedSignatures) {
		Vector<DMMethod> returned = new Vector<DMMethod>();

		try {
			FJPJavaMethod[] declaredMethods = aClass.getMethods();
			for (FJPJavaMethod method : declaredMethods) {
				DMMethod newMethod = makeMethod(method, dataModel, context, source, importReferencedEntities);
				if (newMethod != null && (excludedSignatures == null || !excludedSignatures.contains(newMethod.getSignature()))) {
					returned.add(newMethod);
					// logger.info("Add "+newMethod.getSignature());
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Exclude " + method.getCallSignature());
					}
				}
			}
		} catch (NoClassDefFoundError e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find class: " + e.getMessage());
			}
		} catch (Throwable e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected exception raised " + e);
			}
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("searchForMethods() for " + aClass.getName());
			logger.fine("Return " + returned);
		}

		return returned;
	}

	public static Vector<DMProperty> searchForProperties(FJPJavaClass aClass, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean includesGetOnlyProperties, boolean importReferencedEntities, Vector<String> excludedSignatures) {
		Vector<DMProperty> returned = new Vector<DMProperty>();

		FJPJavaMethod[] declaredMethods = aClass.getMethods();
		for (FJPJavaMethod method : declaredMethods) {
			DMProperty newProperty = null;
			try {
				newProperty = makeProperty(method, dataModel, context, source, includesGetOnlyProperties, importReferencedEntities,
						excludedSignatures);
			} catch (FJPTypeResolver.CrossReferencedEntitiesException e) {
			}
			if (newProperty != null) {
				returned.add(newProperty);
				// logger.info("add property "+newProperty+" for method "+method);
			}
		}

		FJPJavaField[] declaredFields = aClass.getFields();

		for (int i = 0; i < declaredFields.length; i++) {
			FJPJavaField field = declaredFields[i];
			DMProperty newProperty = null;
			try {
				newProperty = makeProperty(field, dataModel, context, source, importReferencedEntities);
			} catch (FJPTypeResolver.CrossReferencedEntitiesException e) {
			}
			if (newProperty != null) {
				returned.add(newProperty);
				// logger.info("add property "+newProperty);
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("searchForProperties() for " + aClass.getName());
			logger.fine("Return " + returned);
		}

		return returned;
	}

	static DMMethod makeMethod(FJPJavaClass aClass, String signature, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean importReferencedEntities) throws FJPTypeResolver.CrossReferencedEntitiesException {
		FJPJavaMethod method = searchMatchingMethod(aClass, signature, dataModel, context, source);
		if (method != null) {
			return makeMethod(method, dataModel, context, source, importReferencedEntities);
		}
		return null;
	}

	static DMMethod makeMethod(FJPJavaMethod method, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean importReferencedEntities) throws FJPTypeResolver.CrossReferencedEntitiesException {
		if (method.isConstructor()) {
			return null;
		}

		DMType returnType = method.getReturns();
		Vector<FJPJavaParameter> parameters = method.getParameters();
		String methodName = method.getName();

		DMMethod newMethod = new DMMethod(dataModel.getDMModel(), methodName);
		newMethod.preventFromModifiedPropagation();

		// Visibility
		DMVisibilityType visibility;
		if (method.isPublic()) {
			visibility = DMVisibilityType.PUBLIC;
		} else if (method.isProtected()) {
			visibility = DMVisibilityType.PROTECTED;
		} else if (method.isPrivate()) {
			visibility = DMVisibilityType.PRIVATE;
		} else {
			visibility = DMVisibilityType.NONE;
		}
		newMethod.setVisibilityModifier(visibility);

		// Static
		newMethod.setIsStatic(method.isStatic());

		// Abstract
		newMethod.setIsAbstract(method.isAbstract());

		// Synchronized
		newMethod.setIsSynchronized(method.isSynchronized());

		// Lookup return type
		// DMEntity returnTypeEntity = null;
		if (returnType != null) { // May happen with constructors
			if (FJPTypeResolver.isResolvable(returnType, dataModel, context, source)) {
				/*returnTypeEntity =*/FJPTypeResolver.resolveEntity(returnType, dataModel, context, source, importReferencedEntities);
				// newMethod.setReturnType(returnTypeEntity);
			}
			newMethod.setReturnType(returnType, true);
			/*if (returnTypeEntity == null && returnType instanceof DMType) {
				newMethod.addToUnresolvedTypes((DMType)returnType);
				newMethod.setUnresolvedReturnType((DMType)returnType);
			}*/
		}

		if (parameters != null) {
			for (FJPJavaParameter parameter : parameters) {
				DMMethodParameter param = new DMMethodParameter(dataModel, newMethod);
				param.preventFromModifiedPropagation();
				param.setName(parameter.getName());
				// Lookup type
				// DMEntity typeEntity = null;
				if (FJPTypeResolver.isResolvable(parameter.getType(), dataModel, context, source)) {
					/*typeEntity =*/FJPTypeResolver.resolveEntity(parameter.getType(), dataModel, context, source,
							importReferencedEntities);
					// param.setType(typeEntity);
				}
				param.setType(parameter.getType());
				/*if (typeEntity == null && parameter.getType() instanceof DMType) {
					param.setUnresolvedType((DMType)parameter.getType());
					newMethod.addToUnresolvedTypes((DMType)parameter.getType());
				}*/
				newMethod.addToParametersNoCheck(param);
			}
		}

		try {
			newMethod.getSourceCode().setCode((method.getJavadoc() != null ? method.getJavadoc().toString() : "") + method.getSourceCode(),
					false);
		} catch (ParserNotInstalledException e) {
			e.printStackTrace();
		} catch (DuplicateMethodSignatureException e) {
			e.printStackTrace();
		}

		JavadocItem javadoc = method.getJavadoc();

		if (javadoc != null) {

			newMethod.setDescription(javadoc.getComment());

			for (ParsedJavadocItem tag : javadoc.getTagsByName("doc")) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Handle DOC tag " + tag.getParameterName() + " with " + tag.getParameterValue());
				}
				newMethod.setSpecificDescriptionsForKey(tag.getParameterValue(), tag.getParameterName());
			}

			for (ParsedJavadocItem tag : javadoc.getTagsByName("param")) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Handle PARAM tag " + tag.getParameterName() + " with " + tag.getParameterValue());
				}
				DMMethodParameter param = newMethod.getDMParameter(tag.getParameterName());
				if (param != null) {
					if (!tag.getParameterValue().equals(tag.getParameterName())) {
						param.setDescription(tag.getParameterValue());
					} else {
						param.setDescription(null);
					}
				}
			}
		}

		return newMethod;
	}

	/**
	 * Build a new DMProperty
	 * 
	 * @throws CrossReferencedEntitiesException
	 */
	static DMProperty makeProperty(FJPJavaClass aClass, String propertyName, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean includesGetOnlyProperties, boolean importReferencedEntities, Vector<String> excludedSignatures)
			throws FJPTypeResolver.CrossReferencedEntitiesException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Looking for " + propertyName);
		}
		FJPJavaMethod method = searchMatchingGetMethod(aClass, propertyName);
		if (method != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found method " + method);
			}
			return makeProperty(method, dataModel, context, source, includesGetOnlyProperties, importReferencedEntities, excludedSignatures);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("No method found, looking for field ");
		}
		FJPJavaField field = aClass.getFieldByName(propertyName);
		if (field == null) {
			field = aClass.getFieldByName("_" + propertyName);
		}
		if (field != null) {
			return makeProperty(field, dataModel, context, source, importReferencedEntities);
		}
		return null;
	}

	/**
	 * Build a new DMProperty
	 * 
	 * @throws CrossReferencedEntitiesException
	 */
	static DMProperty makeProperty(FJPJavaMethod method, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean includesGetOnlyProperties, boolean importReferencedEntities, Vector<String> excludedSignatures)
			throws FJPTypeResolver.CrossReferencedEntitiesException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Analyse method " + method.getCallSignature() + " is it a property ?");
		}

		FJPJavaClass parentClass = method.getParentClass();
		DMType returnType = method.getReturns();
		if (returnType == null) {
			// This is a constructor, ignore it and return null
			return null;
		}

		Vector<FJPJavaParameter> parameters = method.getParameters();

		if (!returnType.isVoid() && method.isPublic() && !method.isStatic() && parameters.size() == 0) {
			// This signature matches a GET property, lets continue !

			// Look for name
			String propertyName = method.getName();

			// Exclude it from methods
			if (excludedSignatures != null) {
				excludedSignatures.add(method.getCallSignature());
			}

			// Beautify property name

			if (propertyName.length() > 3 && propertyName.substring(0, 3).equalsIgnoreCase("get")) {
				propertyName = propertyName.substring(3);
			}
			if (propertyName.length() > 1 && propertyName.substring(0, 1).equals("_")) {
				propertyName = propertyName.substring(1);
			}

			// First char always to lower case
			propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1, propertyName.length());

			// Is there a SET method ?
			FJPJavaMethod setMethod = searchMatchingSetMethod(parentClass, propertyName, returnType);
			boolean isSettable = setMethod != null;
			if (setMethod != null && excludedSignatures != null) {
				excludedSignatures.add(setMethod.getCallSignature());
			}

			// OK, we have the name, let's look at the cardinality
			DMCardinality cardinality = getCardinality(returnType);

			DMType propertyType = null;
			DMType keyType = null;

			FJPJavaMethod additionMethod = null;
			FJPJavaMethod removalMethod = null;

			if (cardinality == DMCardinality.VECTOR) {
				TreeSet<AccessorMethod> addToMethods = searchMatchingAddToMethods(parentClass, propertyName);
				TreeSet<AccessorMethod> removeFromMethods = searchMatchingRemoveFromMethods(parentClass, propertyName);
				for (Iterator it = addToMethods.iterator(); it.hasNext();) {
					AccessorMethod next = (AccessorMethod) it.next();
					if (excludedSignatures != null) {
						excludedSignatures.add(next.getMethod().getCallSignature());
					}
					if (propertyType == null) {
						propertyType = next.getMethod().getParameters().firstElement().getType();
					} else if (!propertyType.equals(next.getMethod().getParameters().firstElement().getType())) {
						isSettable = false;
					}
				}
				for (Iterator it = removeFromMethods.iterator(); it.hasNext();) {
					AccessorMethod next = (AccessorMethod) it.next();
					if (excludedSignatures != null) {
						excludedSignatures.add(next.getMethod().getCallSignature());
					}
					if (!propertyType.equals(next.getMethod().getParameters().firstElement().getType())) {
						isSettable = false;
					}
				}
				if (addToMethods.size() == 0 || removeFromMethods.size() == 0) {
					isSettable = false;
				}
				if (addToMethods.size() > 0) {
					additionMethod = addToMethods.first().method;
				}
				if (removeFromMethods.size() > 0) {
					removalMethod = removeFromMethods.first().method;
				}
				returnType = propertyType;
				if (returnType == null) {
					returnType = DMType.makeResolvedDMType(dataModel.getDMEntity(Object.class));
				}
			}

			if (cardinality == DMCardinality.HASHTABLE) {
				TreeSet<AccessorMethod> setMethods = searchMatchingSetForKeyMethods(parentClass, propertyName);
				TreeSet<AccessorMethod> removeMethods = searchMatchingRemoveWithKeyMethods(parentClass, propertyName);
				for (Iterator it = setMethods.iterator(); it.hasNext();) {
					AccessorMethod next = (AccessorMethod) it.next();
					if (excludedSignatures != null) {
						excludedSignatures.add(next.getMethod().getCallSignature());
					}
					if (propertyType == null) {
						propertyType = next.getMethod().getParameters().firstElement().getType();
					} else if (!propertyType.equals(next.getMethod().getParameters().firstElement().getType())) {
						isSettable = false;
					}
					if (keyType == null) {
						keyType = next.getMethod().getParameters().elementAt(1).getType();
					}
					logger.info("Method " + next.getMethod().getCallSignature() + " ketType=" + keyType);
				}
				for (Iterator it = removeMethods.iterator(); it.hasNext();) {
					AccessorMethod next = (AccessorMethod) it.next();
					if (excludedSignatures != null) {
						excludedSignatures.add(next.getMethod().getCallSignature());
					}
				}
				if (setMethods.size() == 0 || removeMethods.size() == 0) {
					isSettable = false;
				}
				if (setMethods.size() > 0) {
					additionMethod = setMethods.first().method;
				}
				if (removeMethods.size() > 0) {
					removalMethod = removeMethods.first().method;
				}
				returnType = propertyType;
				if (returnType == null) {
					returnType = DMType.makeResolvedDMType(dataModel.getDMEntity(Object.class));
				}
			}

			// Creates and register the property

			if (includesGetOnlyProperties || isSettable) {

				if (FJPTypeResolver.isResolvable(returnType, dataModel, context, source)) {
					FJPTypeResolver.resolveEntity(returnType, dataModel, context, source, importReferencedEntities);
				}

				// logger.info("Make new property "+propertyName+" type="+returnType+" cardinality="+cardinality+" isSettable="+isSettable);

				DMProperty newProperty = new DMProperty(dataModel, propertyName, returnType, cardinality, false, isSettable,
						DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
				newProperty.preventFromModifiedPropagation();
				if (cardinality == DMCardinality.HASHTABLE) {
					if (keyType != null && FJPTypeResolver.isResolvable(keyType, dataModel, context, source)) {
						FJPTypeResolver.resolveEntity(keyType, dataModel, context, source, importReferencedEntities);
					}
					newProperty.setKeyType(keyType, true);
				}

				try {
					newProperty.getGetterSourceCode().setCode(
							(method.getJavadoc() != null ? method.getJavadoc().toString() : "") + method.getSourceCode(), false);
					if (setMethod != null) {
						newProperty.getSetterSourceCode().setCode(
								(setMethod.getJavadoc() != null ? setMethod.getJavadoc().toString() : "") + setMethod.getSourceCode(),
								false);
					}
					if (additionMethod != null) {
						newProperty.getAdditionSourceCode().setCode(
								(additionMethod.getJavadoc() != null ? additionMethod.getJavadoc().toString() : "")
										+ additionMethod.getSourceCode(), false);
					}
					if (removalMethod != null) {
						newProperty.getRemovalSourceCode().setCode(
								(removalMethod.getJavadoc() != null ? removalMethod.getJavadoc().toString() : "")
										+ removalMethod.getSourceCode(), false);
					}
				} catch (ParserNotInstalledException e) {
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					e.printStackTrace();
				}

				if (isSettable) {
					if (setMethod != null && setMethod.getParameters() != null && setMethod.getParameters().size() == 1) {
						newProperty.setSetterParamName(setMethod.getParameters().firstElement().getName());
					}
				}

				if (cardinality.isMultiple()) {
					if (additionMethod != null && additionMethod.getParameters() != null && additionMethod.getParameters().size() == 1) {
						newProperty.setAdditionAccessorParamName(additionMethod.getParameters().firstElement().getName());
					}
					if (removalMethod != null && removalMethod.getParameters() != null && removalMethod.getParameters().size() == 1) {
						newProperty.setRemovalAccessorParamName(removalMethod.getParameters().firstElement().getName());
					}
				}

				JavadocItem javadoc = method.getJavadoc();

				if (javadoc != null) {

					newProperty.setDescription(javadoc.getComment());

					for (ParsedJavadocItem tag : javadoc.getTagsByName("doc")) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Handle DOC tag " + tag.getParameterName() + " with " + tag.getParameterValue());
						}
						newProperty.setSpecificDescriptionsForKey(tag.getParameterValue(), tag.getParameterName());
					}

				}

				return newProperty;
			}

		}
		return null;
	}

	/**
	 * Build a new DMProperty
	 * 
	 * @throws CrossReferencedEntitiesException
	 */
	static DMProperty makeProperty(FJPJavaField field, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean importReferencedEntities) throws FJPTypeResolver.CrossReferencedEntitiesException {
		DMType fieldType = field.getType();
		if (!fieldType.isVoid() && field.isPublic() && !field.isStatic()) {
			// This signature matches a GET property, lets continue !

			// Look for name
			String propertyName = field.getName();
			// OK, we have the name, let's look at the cardinality
			DMCardinality cardinality = getCardinality(fieldType);

			// Lookup return type
			// DMEntity typeEntity = null;
			if (FJPTypeResolver.isResolvable(fieldType, dataModel, context, source)) {
				/*typeEntity =*/FJPTypeResolver.resolveEntity(fieldType, dataModel, context, source, importReferencedEntities);
			}
			DMProperty newProperty = new DMProperty(dataModel, propertyName, fieldType, cardinality, false, true,
					DMPropertyImplementationType.PUBLIC_FIELD);
			newProperty.preventFromModifiedPropagation();

			/*if (typeEntity == null) {
				newProperty.setUnresolvedTypeName(fieldType.getValue());
			}*/

			return newProperty;
		}

		return null;
	}

	/**
	 * Try to find a method matching supplied signature
	 * 
	 * Returns corresponding method, null if no such method exist
	 */
	private static FJPJavaMethod searchMatchingMethod(FJPJavaClass aClass, String signature, DMModel dataModel, FJPDMSet context,
			FJPJavaSource source) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Searching " + signature);
		}
		for (FJPJavaMethod method : aClass.getMethods()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Try " + getFullQualifiedCallSignature(method, dataModel, context, source));
			}
			if (getFullQualifiedCallSignature(method, dataModel, context, source).equals(signature)) {
				return method;
			}
		}
		return null;
	}

	private static String getFullQualifiedCallSignature(FJPJavaMethod method, DMModel dataModel, FJPDMSet context, FJPJavaSource source) {
		String returned = method.getName() + "(" + getParametersTypesAsString(method, dataModel, context, source) + ")";
		return returned;
	}

	private static String getParametersTypesAsString(FJPJavaMethod method, DMModel dataModel, FJPDMSet context, FJPJavaSource source) {
		boolean isFirst = true;
		StringBuffer sb = new StringBuffer();
		for (FJPJavaParameter p : method.getParameters()) {
			DMType t = p.getType();
			/*String typeName = t.toString();
			try {
				DMEntity resolvedEntity = FJPTypeResolver.resolveEntity(t,dataModel,context,source,false);
				if (resolvedEntity != null)
					typeName = resolvedEntity.getFullQualifiedName();
			} catch (FJPTypeResolver.CrossReferencedEntitiesException e) {
				e.printStackTrace();
			}
			sb.append((isFirst?"":",")+typeName+(p.isVarArgs()?"...":""));
			 */

			try {
				FJPTypeResolver.resolveEntity(t, dataModel, context, source, false);
			} catch (CrossReferencedEntitiesException e) {
				e.printStackTrace();
			}
			String typeName = t.getStringRepresentation() + (p.isVarArgs() ? "..." : "");
			sb.append((isFirst ? "" : ",") + typeName);
			isFirst = false;
		}
		return sb.toString();
	}

	/**
	 * Try to find a matching "get" method, such as (in order):
	 * <ul>
	 * <li>propertyName()</li>
	 * <li>_propertyName()</li>
	 * <li>getPropertyName()</li>
	 * <li>_getPropertyName()</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	private static FJPJavaMethod searchMatchingGetMethod(FJPJavaClass aClass, String propertyName) {

		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		Vector<String> tries = new Vector<String>();

		tries.add(propertyName + "()");
		tries.add("_" + propertyName + "()");
		tries.add("get" + propertyNameWithFirstCharToUpperCase + "()");
		tries.add("_get" + propertyNameWithFirstCharToUpperCase + "()");

		for (String tryThis : tries) {
			FJPJavaMethod returned = aClass.getMethodBySignature(tryThis);
			if (returned != null) {
				return returned;
			}
		}
		return null;

	}

	/**
	 * Try to find a matching "set" method, such as (in order):
	 * <ul>
	 * <li>setPropertyName(Type)</li>
	 * <li>_setPropertyName(Type)</li>
	 * </ul>
	 * Returns corresponding method, null if no such method exist
	 */
	private static FJPJavaMethod searchMatchingSetMethod(FJPJavaClass aClass, String propertyName, DMType aType) {

		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		Vector<String> tries = new Vector<String>();

		tries.add("set" + propertyNameWithFirstCharToUpperCase);
		tries.add("_set" + propertyNameWithFirstCharToUpperCase);

		for (String tryThis : tries) {
			FJPJavaMethod returned = aClass.getMethodBySignature(tryThis, aType);
			if (returned != null) {
				return returned;
			}
		}

		// Little hask to handle properly NSArray as VECTOR-cardinality properties
		// TODO: implement this better later
		if (aType.getStringRepresentation().equals("com.webobjects.foundation.NSArray")) {
			return searchMatchingSetMethod(aClass, propertyName, DMType.makeUnresolvedDMType("com.webobjects.foundation.NSMutableArray"));
		}

		return null;
	}

	/**
	 * Search and return matching "setForKey" methods<br>
	 * NB: 'setForKey...' methods are methods with general form: <code>setXXXForKey(Class anObject, Class aKey)</code> or
	 * <code>_setXXXForKey(Class anObject, Class aKey)</code>, where XXX is the property name (try with or without a terminal 's'
	 * character), and Class could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingSetForKeyMethods(FJPJavaClass aClass, String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("ies")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 3) + "y";
			pluralPropertyName = propertyName;
		} else if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "set" + singularPropertyName + "ForKey";
		methodNameCondidates[1] = "_set" + singularPropertyName + "ForKey";
		methodNameCondidates[2] = "set" + pluralPropertyName + "ForKey";
		methodNameCondidates[3] = "_set" + pluralPropertyName + "ForKey";

		return searchMethodsWithNameAndParamsNumber(aClass, methodNameCondidates, 2);
	}

	/**
	 * Search and return matching "removeWithKey" methods<br>
	 * NB: 'removeWithKey...' methods are methods with general form: <code>removeXXXWithKey(Class aKey)</code> or
	 * <code>_removeXXXWithKey(Class aKey)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingRemoveWithKeyMethods(FJPJavaClass aClass, String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("ies")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 3) + "y";
			pluralPropertyName = propertyName;
		} else if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "remove" + singularPropertyName + "WithKey";
		methodNameCondidates[1] = "_remove" + singularPropertyName + "WithKey";
		methodNameCondidates[2] = "remove" + pluralPropertyName + "WithKey";
		methodNameCondidates[3] = "_remove" + pluralPropertyName + "WithKey";

		return searchMethodsWithNameAndParamsNumber(aClass, methodNameCondidates, 1);
	}

	/**
	 * Search and return matching "addTo" methods<br>
	 * NB: 'addTo...' methods are methods with general form: <code>addToXXX(Class anObject)</code> or <code>_addToXXX(Class anObject)</code>
	 * , where XXX is the property name (try with or without a terminal 's' character), and Class could be anything... Returns an ordered
	 * TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingAddToMethods(FJPJavaClass aClass, String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "addTo" + singularPropertyName;
		methodNameCondidates[1] = "_addTo" + singularPropertyName;
		methodNameCondidates[2] = "addTo" + pluralPropertyName;
		methodNameCondidates[3] = "_addTo" + pluralPropertyName;

		return searchMethodsWithNameAndParamsNumber(aClass, methodNameCondidates, 1);
	}

	/**
	 * Search and return matching "removeFrom" methods<br>
	 * NB: 'removeFrom...' methods are methods with general form: <code>removeFromXXX(Class anObject)</code> or
	 * <code>_removeFromXXX(Class anObject)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingRemoveFromMethods(FJPJavaClass aClass, String propertyName) {

		String singularPropertyName;
		String pluralPropertyName;

		if (propertyName.endsWith("s") || propertyName.endsWith("S")) {
			singularPropertyName = propertyName.substring(0, propertyName.length() - 1);
			pluralPropertyName = propertyName;
		} else {
			singularPropertyName = propertyName;
			pluralPropertyName = propertyName + "s";
		} // end of else

		String[] methodNameCondidates = new String[4];
		methodNameCondidates[0] = "removeFrom" + singularPropertyName;
		methodNameCondidates[1] = "_removeFrom" + singularPropertyName;
		methodNameCondidates[2] = "removeFrom" + pluralPropertyName;
		methodNameCondidates[3] = "_removeFrom" + pluralPropertyName;

		return searchMethodsWithNameAndParamsNumber(aClass, methodNameCondidates, 1);
	}

	/**
	 * Search and returns all methods (as {@link AccessorMethod} objects) of related class whose names is in the specified string list, with
	 * exactly the specified number of parameters, ascendant ordered regarding parameters specialization.
	 * 
	 * @see AccessorMethod
	 */
	private static TreeSet<AccessorMethod> searchMethodsWithNameAndParamsNumber(FJPJavaClass aClass, String[] searchedNames, int paramNumber) {

		TreeSet<AccessorMethod> returnedTreeSet = new TreeSet<AccessorMethod>();
		FJPJavaMethod[] allMethods = aClass.getMethods();

		for (int i = 0; i < allMethods.length; i++) {
			FJPJavaMethod tempMethod = allMethods[i];
			for (int j = 0; j < searchedNames.length; j++) {
				if (tempMethod.getName().equalsIgnoreCase(searchedNames[j]) && tempMethod.getParameters().size() == paramNumber) {
					// This is a good candidate
					returnedTreeSet.add(new AccessorMethod(tempMethod));
				}
			}
		}
		return returnedTreeSet;
	}

	private static DMCardinality getCardinality(Type aType) {
		// Little hask to handle properly NSArray as VECTOR-cardinality properties
		// TODO: implement this better later
		Type NSARRAY_TYPE = new Type("NSArray");
		if (aType.isA(NSARRAY_TYPE)) {
			return DMCardinality.VECTOR;
		}
		Type NSARRAY_TYPE_FQ = new Type("com.webobjects.foundation.NSArray");
		if (aType.isA(NSARRAY_TYPE_FQ)) {
			return DMCardinality.VECTOR;
		}

		Type VECTOR_TYPE = new Type(java.util.Vector.class.getCanonicalName());
		if (aType.isA(VECTOR_TYPE)) {
			return DMCardinality.VECTOR;
		}
		Type HASHTABLE_TYPE = new Type(java.util.Hashtable.class.getCanonicalName());
		if (aType.isA(HASHTABLE_TYPE)) {
			return DMCardinality.HASHTABLE;
		}
		return DMCardinality.SINGLE;
	}

	/**
	 * <p>
	 * <code>AccessorMethod</code> is a class representing a KeyValueProperty accessor method.
	 * </p>
	 * <p>
	 * Because many differents accessors could be defined in a class, all implementing different class-specific levels (more or less
	 * specialized, regarding parameters classes), we store these <code>AccessorMethods</code> in a particular order depending on the
	 * parameters specialization. This order is implemented in this class through {@link Comparable} interface implementation. Note: this
	 * class has a natural ordering that is inconsistent with equals, which means that <code>(x.compareTo(y)==0) == (x.equals(y))</code>
	 * condition is violated.
	 */
	private static class AccessorMethod implements Comparable {

		/** Stores the related <code>Method</code> */
		protected FJPJavaMethod method;

		/**
		 * Creates a new <code>AccessorMethod</code> instance.
		 * 
		 * @param aKeyValueProperty
		 *            a <code>KeyValueProperty</code> value
		 * @param aMethod
		 *            a <code>Method</code> value
		 */
		public AccessorMethod(FJPJavaMethod aMethod) {

			super();
			method = aMethod;
		}

		/**
		 * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object
		 * is less than, equal to, or greater than the specified object.
		 * 
		 * @param object
		 *            an <code>Object</code> value
		 * @return an <code>int</code> value
		 * @exception ClassCastException
		 *                if an error occurs
		 */
		@Override
		public int compareTo(Object object) throws ClassCastException {

			if (object instanceof AccessorMethod) {

				AccessorMethod comparedAccessorMethod = (AccessorMethod) object;

				if (getMethod().getParameters().size() != comparedAccessorMethod.getMethod().getParameters().size()) {

					// Those objects could not be compared and should be treated
					// as equals
					// regarding the specialization of their parameters
					return 2;
				}

				else {

					for (int i = 0; i < getMethod().getParameters().size(); i++) {

						Type localParameterType = getMethod().getParameters().get(i).getType();
						Type comparedParameterType = comparedAccessorMethod.getMethod().getParameters().get(i).getType();

						if (!localParameterType.equals(comparedParameterType)) {

							boolean localParamIsParentOfComparedParam = comparedParameterType.isA(localParameterType);

							boolean localParamIsChildOfComparedParam = localParameterType.isA(comparedParameterType);

							if (localParamIsParentOfComparedParam) {
								return 1;
							}
							if (localParamIsChildOfComparedParam) {
								return -1;
							}
							// Those objects could not be compared
							return 2;
						}

					} // end of for

					// Those objects are equals regarding the specialization of
					// their parameters
					return 0;
				}

			}

			else {
				throw new ClassCastException();
			}
		}

		/**
		 * Return the related <code>Method</code>
		 * 
		 * @return
		 */
		public FJPJavaMethod getMethod() {

			return method;
		}

	}

}
