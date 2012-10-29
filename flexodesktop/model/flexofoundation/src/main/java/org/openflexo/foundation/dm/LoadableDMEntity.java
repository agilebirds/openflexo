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
package org.openflexo.foundation.dm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.MethodReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.PropertyReference;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a class and its definition for objects accessible from a Class object. That could be classes accessible in current ClassLoader
 * or classes imported from an external jar-file (see ExternalRepository).
 * 
 * @author sguerin
 * 
 */
public class LoadableDMEntity extends DMEntity {

	private static final Logger logger = Logger.getLogger(LoadableDMEntity.class.getPackage().getName());

	private Class<?> javaType;

	/**
	 * Constructor used during deserialization
	 */
	public LoadableDMEntity(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public LoadableDMEntity(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * Constructor used for dynamic creation
	 */
	private LoadableDMEntity(DMRepository repository, Class<?> aClass, boolean includesGetOnlyProperties, boolean includesMethods) {
		this(repository.getDMModel());
		setRepository(repository);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Build new LoadableDMEntity for " + aClass.getName());
		}
		javaType = aClass;
		if (javaType.isPrimitive()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Supplied class " + javaType + " is a primitive. Please use JDKPrimitive instead !");
			}
			return;
		}
		if (javaType.isArray()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Supplied class " + javaType + " is an array which is not permitted !");
			}
			return;
		}
		initializeFromClass(includesGetOnlyProperties, includesMethods);
		repository.registerEntity(this);
		List<String> excludedSignatures = registerProperties(includesGetOnlyProperties);
		if (includesMethods) {
			registerMethods(excludedSignatures);
		}
	}

	/**
	 * Static builder used for dynamic creation
	 */
	public static LoadableDMEntity createLoadableDMEntity(DMRepository repository, Class<?> aClass) {
		return createLoadableDMEntity(repository, aClass, false, false);
	}

	/**
	 * Static builder used for dynamic creation
	 */
	public static LoadableDMEntity createLoadableDMEntity(DMRepository repository, Class<?> aClass, boolean includesGetOnlyProperties,
			boolean includesMethods) {

		DMEntity alreadyExistingEntity = repository.getDMModel().getDMEntity(aClass);

		// logger.info("createLoadableDMEntity() for "+aClass+" repository="+repository+" cl="+aClass.getClassLoader());

		if (alreadyExistingEntity != null) {
			// This entity is already registered somewhere !
			// Just unregister it from its old location and register to the new
			// one.
			DMRepository oldRepository = alreadyExistingEntity.getRepository();
			oldRepository.unregisterEntity(alreadyExistingEntity);
			repository.registerEntity(alreadyExistingEntity);
			return (LoadableDMEntity) alreadyExistingEntity;
		}
		if (aClass.isPrimitive()) {
			return new JDKPrimitive(repository, aClass);
		} else {
			return new LoadableDMEntity(repository, aClass, includesGetOnlyProperties, includesMethods);
		}
	}

	@Override
	public void delete() {
		getProject().getJarClassLoader().unloadClass(javaType);
		super.delete();
	};

	@Override
	public boolean getIsReadOnly() {
		return true;
	}

	@Override
	public void setIsReadOnly(boolean readOnly) {
		// Not authorized
	}

	public Class<?> getJavaType() {
		if (javaType == null) {
			javaType = retrieveJavaType();
		}
		return javaType;
	}

	public Class<?> retrieveJavaType() {
		if (getRepository() == getDMModel().getJDKRepository()) {
			try {
				javaType = Class.forName(getFullyQualifiedName());
			} catch (ClassNotFoundException e) {
				// Warns about the exception
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				e.printStackTrace();
			}
		} else if (getRepository() instanceof ExternalRepository) {
			JarLoader jarLoader = ((ExternalRepository) getRepository()).getJarLoader();
			if (jarLoader == null) {
				logger.warning("Could not reload JAR !!!");
			} else {
				javaType = jarLoader.getClassForName(getFullyQualifiedName());
				if (javaType == null) {
					logger.warning("JarLoader has been succesfully loaded, but class " + getFullyQualifiedName() + " was no found !");
				}
			}
		}
		return javaType;
	}

	@Override
	public boolean isAncestorOf(DMEntity entity) {
		if (entity == null) {
			return false;
		}
		if (entityPackageName.equals("java.lang")) {
			if (entityClassName.equals("Boolean") && entity.getName().equals("boolean")) {
				return true;
			}
			if (entityClassName.equals("Integer") && entity.getName().equals("int")) {
				return true;
			}
			if (entityClassName.equals("Long") && entity.getName().equals("long")) {
				return true;
			}
			if (entityClassName.equals("Short") && entity.getName().equals("short")) {
				return true;
			}
			if (entityClassName.equals("Float") && entity.getName().equals("float")) {
				return true;
			}
			if (entityClassName.equals("Double") && entity.getName().equals("double")) {
				return true;
			}
			if (entityClassName.equals("Character") && entity.getName().equals("char")) {
				return true;
			}
			if (entityClassName.equals("Byte") && entity.getName().equals("byte")) {
				return true;
			}
		}
		return super.isAncestorOf(entity);
	}

	protected static String packageNameForClass(Class<?> aClass) {
		return packageNameForClassName(aClass.getName());
	}

	protected static String packageNameForClassName(String className) {
		String packageName = null;
		StringTokenizer st = new StringTokenizer(className, ".");
		while (st.hasMoreTokens()) {
			String nextToken = st.nextToken();
			if (st.hasMoreTokens()) {
				if (packageName == null) {
					packageName = nextToken;
				} else {
					packageName += "." + nextToken;
				}
			}
		}
		return packageName;
	}

	/**
	 * Update from data in ClassLoader
	 */
	private void initializeFromClass(boolean includesGetOnlyProperties, boolean includesMethods) {
		if (javaType == null) {
			logger.warning("Type could not be determined !");
			return;
		}
		// logger.info("type="+type);
		// logger.info("packageNameForClass(type)="+packageNameForClass(type));
		if (packageNameForClass(javaType) != null) {
			setEntityPackageName(packageNameForClass(javaType));
			// entityPackageName = type.getPackage().getName();
		}
		StringTokenizer st = new StringTokenizer(javaType.getName(), ".");
		while (st.hasMoreTokens()) {
			name = st.nextToken();
		}
		try {
			setEntityClassName(name);
		} catch (DuplicateClassNameException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		} catch (InvalidNameException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}
		// entityClassName = name;

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Registering " + getFullyQualifiedName());
		}

		// setParentEntity(null);

		// Sets the parent entity if relevant
		// Register by the way all non-registered intermediate DMEntity in same
		// repositery
		Class<?> current = javaType;
		while (current.getSuperclass() != null) {
			current = current.getSuperclass();
			DMEntity currentParentEntity = getDMModel().getDMEntity(current);
			if (currentParentEntity == null) {
				// This entity is not yet known
				LoadableDMEntity.createLoadableDMEntity(getRepository(), current, includesGetOnlyProperties, includesMethods);
				currentParentEntity = getDMModel().getDMEntity(current);
				if (logger.isLoggable(Level.FINE)) {
					logger.finer("Force register " + current.getName() + " in DataModel: " + currentParentEntity);
				}
			}
			if (currentParentEntity == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not register " + current.getName() + " in DataModel");
				}
			}
			if (current == javaType.getSuperclass()) {
				setParentType(makeType(getRepository(), javaType.getGenericSuperclass(), true), true);
				// setParentEntity(currentParentEntity);
			}
		}

		setIsInterface(javaType.isInterface());
		setIsEnumeration(javaType.isEnum());

		// implements clauses
		int implementedTypesIndex = 0;
		for (Type t : javaType.getGenericInterfaces()) {
			DMType implementsType = makeType(getRepository(), t, true);
			implementsType.setOwner(this);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Implements " + implementsType);
			}
			if (getImplementedTypes().size() > implementedTypesIndex) {
				DMType existingTypeAsThisIndex = getImplementedTypes().elementAt(implementedTypesIndex);
				if (!existingTypeAsThisIndex.equals(implementsType)) {
					getImplementedTypes().setElementAt(implementsType, implementedTypesIndex);
				}
			} else {
				addToImplementedTypes(implementsType);
			}
			implementedTypesIndex++;
		}
		int implementedTypesToRemove = getImplementedTypes().size() - javaType.getGenericInterfaces().length;
		for (int j = 0; j < implementedTypesToRemove; j++) {
			getImplementedTypes().removeElementAt(getTypeVariables().size() - 1);
		}

		// type variable
		int typeVariableIndex = 0;
		for (TypeVariable tv : javaType.getTypeParameters()) {
			DMTypeVariable typeVariable;
			if (getTypeVariables().size() > typeVariableIndex) {
				DMTypeVariable existingTV = getTypeVariables().elementAt(typeVariableIndex);
				if (!existingTV.getName().equals(tv.getName())) {
					existingTV.setName(tv.getName());
				}
				typeVariable = existingTV;
			} else {
				typeVariable = new DMTypeVariable(getDMModel(), this);
				typeVariable.setName(tv.getName());
				addToTypeVariables(typeVariable);
			}
			if (tv.getBounds().length > 0) {
				StringBuffer sb = new StringBuffer();
				boolean isFirst = true;
				for (Type t : tv.getBounds()) {
					DMType type = makeType(getRepository(), t, false);
					type.setOwner(this);
					sb.append((isFirst ? "" : ",") + type.getStringRepresentation());
					isFirst = false;
				}
				String boundsAsString = sb.toString();
				if (!boundsAsString.equals(typeVariable.getBounds())) {
					typeVariable.setBounds(boundsAsString);
				}
			}
			typeVariableIndex++;
		}
		int elementsToRemove = getTypeVariables().size() - javaType.getTypeParameters().length;
		for (int j = 0; j < elementsToRemove; j++) {
			getTypeVariables().removeElementAt(getTypeVariables().size() - 1);
		}
	}

	private void registerMethods(List<String> excludedSignatures) {
		for (DMMethod next : searchForMethods(javaType, getDMModel(), getRepository(), true, excludedSignatures)) {
			registerMethod(next);
		}
	}

	/**
	 * Build a new Vector containing unregistered DMMethod instances, given flags includesGetOnlyProperties and includesMethods
	 */
	public static List<DMMethod> searchForMethods(Class<?> type, DMModel dmModel, DMRepository repository,
			boolean importReferencedEntities, List<String> excludedSignatures) {
		List<DMMethod> returned = new ArrayList<DMMethod>();

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("searchForMethods()");
		}
		for (String excludedSignature : excludedSignatures) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Excluded: " + excludedSignature);
			}
		}

		try {
			Method[] declaredMethods = type.getDeclaredMethods();
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];

				DMMethod newMethod = makeMethod(method, dmModel, repository, importReferencedEntities);

				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Examine: " + newMethod.getSignature());
				}
				if (!excludedSignatures.contains(newMethod.getSignature())) {
					returned.add(newMethod);
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Exclude " + DMMethod.signatureForMethod(method, true));
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

		return returned;
	}

	/**
	 * Build a new DMMethod
	 */
	private static DMMethod makeMethod(Class<?> type, String signature, DMModel dmModel, DMRepository repository,
			boolean importReferencedEntities) {
		Method method = searchMatchingMethod(type, signature);
		if (method != null) {
			return makeMethod(method, dmModel, repository, importReferencedEntities);
		}
		return null;
	}

	private static DMEntity obtainDMEntity(DMRepository repository, Class<?> aClass, boolean importReferencedEntities) {
		DMModel dmModel = repository.getDMModel();
		DMEntity returned = dmModel.getDMEntity(aClass);
		if (returned == null && importReferencedEntities) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Force register " + aClass.getName() + " in DataModel");
			}
			LoadableDMEntity.createLoadableDMEntity(repository, aClass, false, false);
			returned = dmModel.getDMEntity(aClass);
			if (returned == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not register " + aClass.getName() + " in DataModel");
				}
			}
		}
		return returned;
	}

	private static DMType makeType(DMRepository repository, Type type, boolean importReferencedEntities) {
		DMModel dmModel = repository.getDMModel();
		if (type instanceof Class) {
			Class<?> typeAsClass = (Class<?>) type;
			Class<?> baseType;
			if (typeAsClass.isArray()) {
				baseType = typeAsClass.getComponentType();
				while (baseType.isArray()) {
					baseType = baseType.getComponentType();
				}
			} else {
				baseType = typeAsClass;
			}
			DMEntity returnTypeBaseEntity = obtainDMEntity(repository, baseType, importReferencedEntities);

			/*
			 * if ((returnTypeBaseEntity == null) && (importReferencedEntities)) { if (logger.isLoggable(Level.FINE))
			 * logger.finer("Force register " + baseType.getName() + " in DataModel"); LoadableDMEntity.createLoadableDMEntity(baseType,
			 * dmModel, false, false); returnTypeBaseEntity = dmModel.getDMEntity(baseType); if (returnTypeBaseEntity == null) { if
			 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not register " + baseType.getName() + " in DataModel"); } }
			 */

			if (returnTypeBaseEntity != null) {
				return DMType.makeResolvedDMType(returnTypeBaseEntity, DMType.arrayDepth(typeAsClass));
			} else {
				return DMType.makeUnresolvedDMType(baseType.getCanonicalName(), DMType.arrayDepth(typeAsClass));
			}
		} else if (type instanceof TypeVariable) {
			TypeVariable typeAsTypeVariable = (TypeVariable) type;
			GenericDeclaration gd = typeAsTypeVariable.getGenericDeclaration();
			if (gd instanceof Class) {
				DMEntity owner = obtainDMEntity(repository, (Class<?>) gd, importReferencedEntities);
				if (owner != null) {
					DMTypeVariable typeVariable = null;
					for (DMTypeVariable tv : owner.getTypeVariables()) {
						if (tv.getName().equals(typeAsTypeVariable.getName())) {
							typeVariable = tv;
						}
					}
					if (typeVariable == null) {
						typeVariable = new DMTypeVariable(dmModel, owner);
						typeVariable.setName(typeAsTypeVariable.getName());
						if (typeAsTypeVariable.getBounds().length > 0) {
							typeVariable.setBounds(typeAsTypeVariable.getBounds()[0].toString());
						}
						owner.addToTypeVariables(typeVariable);
					}
					return DMType.makeTypeVariableDMType(typeVariable);
				} else {
					logger.warning("Could not find entity for class " + gd);
					return DMType.makeUnresolvedDMType(type.toString());
				}
			} else if (gd instanceof Method || gd instanceof Constructor<?>) {
				logger.warning("GenericDeclaration for method and constructors not implemented yet for " + gd);
				return DMType.makeResolvedDMType(dmModel.getDMEntity(Object.class));
			} else {
				logger.warning("Unexpected type " + gd);
				return DMType.makeUnresolvedDMType(type.toString());
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType typeAsParameterizedType = (ParameterizedType) type;
			if (typeAsParameterizedType.getRawType() instanceof Class) {
				DMEntity baseEntity = obtainDMEntity(repository, (Class<?>) typeAsParameterizedType.getRawType(), importReferencedEntities);
				if (baseEntity != null) {
					Vector<DMType> parameters = new Vector<DMType>();
					for (Type t : typeAsParameterizedType.getActualTypeArguments()) {
						parameters.add(makeType(repository, t, importReferencedEntities));
					}
					return DMType.makeResolvedDMType(baseEntity, 0, parameters);
				} else {
					logger.info("Cannot find entity for " + typeAsParameterizedType.getRawType());
					return DMType.makeUnresolvedDMType(type.toString());

				}
			} else {
				logger.warning("Unexpected type " + typeAsParameterizedType.getRawType());
				return DMType.makeUnresolvedDMType(type.toString());
			}
		} else if (type instanceof GenericArrayType) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found GenericArrayType: " + type);
			}
			GenericArrayType typeAsGenericArrayType = (GenericArrayType) type;
			int dimensions = 1;
			Type baseType = typeAsGenericArrayType.getGenericComponentType();
			while (baseType instanceof GenericArrayType) {
				baseType = ((GenericArrayType) baseType).getGenericComponentType();
				dimensions++;
			}
			DMType returned = makeType(repository, baseType, importReferencedEntities);
			returned.setDimensions(dimensions);
			return returned;
		} else if (type instanceof WildcardType) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found wildcard: " + type);
			}
			WildcardType typeAsWildcardType = (WildcardType) type;
			Vector<DMType> upperBounds = new Vector<DMType>();
			for (Type t : typeAsWildcardType.getUpperBounds()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Upper: " + t);
				}
				upperBounds.add(makeType(repository, t, importReferencedEntities));
			}
			Vector<DMType> lowerBounds = new Vector<DMType>();
			for (Type t : typeAsWildcardType.getLowerBounds()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Lower: " + t);
				}
				lowerBounds.add(makeType(repository, t, importReferencedEntities));
			}
			DMType returned = DMType.makeWildcardDMType(upperBounds, lowerBounds);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returning wildcard: " + returned.getStringRepresentation());
			}
			return returned;
		}
		logger.warning("Unexpected type " + type.getClass());
		return DMType.makeUnresolvedDMType(type.toString());
	}

	/**
	 * Build a new DMMethod from a Method object
	 */
	private static DMMethod makeMethod(Method method, DMModel dmModel, DMRepository repository, boolean importReferencedEntities) {
		Type returnType = method.getGenericReturnType();
		Type[] parameters = method.getGenericParameterTypes();
		String methodName = method.getName();

		DMMethod newMethod = new DMMethod(dmModel, methodName);
		newMethod.setEntity(obtainDMEntity(repository, method.getDeclaringClass(), true));

		// Visibility
		DMVisibilityType visibility;
		if (Modifier.isPublic(method.getModifiers())) {
			visibility = DMVisibilityType.PUBLIC;
		} else if (Modifier.isProtected(method.getModifiers())) {
			visibility = DMVisibilityType.PROTECTED;
		} else if (Modifier.isPrivate(method.getModifiers())) {
			visibility = DMVisibilityType.PRIVATE;
		} else {
			visibility = DMVisibilityType.NONE;
		}
		newMethod.setVisibilityModifier(visibility);

		// Static
		newMethod.setIsStatic(Modifier.isStatic(method.getModifiers()));

		// Abstract
		newMethod.setIsAbstract(Modifier.isAbstract(method.getModifiers()));

		// Synchronized
		newMethod.setIsSynchronized(Modifier.isSynchronized(method.getModifiers()));

		// lookup return type
		DMType returnedDMType = makeType(repository, returnType, importReferencedEntities);
		newMethod.setReturnType(returnedDMType, true);

		/*
		 * DMEntity returnTypeEntity = dmModel.getDMEntity(returnType); if ((returnTypeEntity == null) && (importReferencedEntities)) { if
		 * (logger.isLoggable(Level.FINE)) logger.finer("Force register " + returnType.getName() + " in DataModel");
		 * LoadableDMEntity.createLoadableDMEntity(returnType, dmModel, false, false); returnTypeEntity = dmModel.getDMEntity(returnType);
		 * if (returnTypeEntity == null) { if (logger.isLoggable(Level.WARNING)) logger.warning("Could not register " + returnType.getName()
		 * + " in DataModel"); } }
		 * 
		 * if ((!importReferencedEntities) && (returnTypeEntity == null)) { newMethod.setReturnType(new DMType(returnType)); } else {
		 * newMethod.setReturnType(new DMType(returnTypeEntity)); }
		 */

		if (parameters != null) {
			for (int j = 0; j < parameters.length; j++) {
				Type parameter = parameters[j];

				DMType paramDMType = makeType(repository, parameter, importReferencedEntities);
				DMMethodParameter param = new DMMethodParameter(dmModel, newMethod);
				String name = "arg" + j;
				if (paramDMType.getBaseEntity() != null) {
					name = paramDMType.getBaseEntity().getNameAsMethodArgument();
				} else if (paramDMType.getTypeVariable() != null) {
					name = paramDMType.getTypeVariable().getName().toLowerCase();
				}
				name = newMethod.getNextDefautParameterName(name);
				param.setName(name);
				param.setType(paramDMType);

				/*
				 * DMEntity paramTypeEntity = dmModel.getDMEntity(parameter); if ((paramTypeEntity == null) && (importReferencedEntities)) {
				 * LoadableDMEntity.createLoadableDMEntity(parameter, dmModel, false, false); paramTypeEntity =
				 * dmModel.getDMEntity(parameter); if (logger.isLoggable(Level.FINE)) logger.finer("Force register " + parameter.getName() +
				 * " in DataModel: " + paramTypeEntity); if (paramTypeEntity == null) { if (logger.isLoggable(Level.WARNING))
				 * logger.warning("Could not register " + parameter.getName() + " in DataModel"); } } DMMethodParameter param = new
				 * DMMethodParameter(dmModel,newMethod); if (paramTypeEntity != null) {
				 * param.setName(paramTypeEntity.getNameAsMethodArgument()); param.setType(new DMType(paramTypeEntity)); } else {
				 * param.setName("???"); param.setType(new DMType(parameter)); //param.setUnresolvedTypeClass(parameter); }
				 */

				newMethod.addToParametersNoCheck(param);
			}
		}

		newMethod.updateCode();

		return newMethod;
	}

	/**
	 * 
	 * @param includesGetOnlyProperties
	 * @return a Vector of signature of all methods used to build properties
	 */
	private List<String> registerProperties(boolean includesGetOnlyProperties) {
		List<String> excludedSignatures = new ArrayList<String>();
		List<DMProperty> properties = searchForProperties(javaType, getDMModel(), getRepository(), includesGetOnlyProperties, true,
				excludedSignatures);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Properties to register: " + properties);
		}
		for (DMProperty next : properties) {
			registerProperty(next, false);
		}
		return excludedSignatures;
	}

	/**
	 * Build a new Vector containing unregistered DMProperty instances, given flags includesGetOnlyProperties and includesMethods
	 */
	public static List<DMProperty> searchForProperties(Class<?> type, DMModel dmModel, DMRepository repository,
			boolean includesGetOnlyProperties, boolean importReferencedEntities, List<String> excludedSignatures) {
		List<DMProperty> returned = new ArrayList<DMProperty>();

		try {
			Method[] declaredMethods = type.getDeclaredMethods();
			for (int i = 0; i < declaredMethods.length; i++) {
				Method method = declaredMethods[i];
				DMProperty newProperty = makeProperty(method, dmModel, repository, includesGetOnlyProperties, importReferencedEntities,
						excludedSignatures);
				if (newProperty != null && !containsAPropertyNamed(returned, newProperty.getName())) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Make property from method: " + method);
					}
					returned.add(newProperty);
				}
			}

			Field[] declaredFields = type.getDeclaredFields();

			for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];

				DMProperty newProperty = makeProperty(field, dmModel, repository, importReferencedEntities);
				if (newProperty != null && !containsAPropertyNamed(returned, newProperty.getName())) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Make property from field: " + field);
					}
					returned.add(newProperty);
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
		return returned;
	}

	private static boolean containsAPropertyNamed(List<DMProperty> properties, String aName) {
		for (DMProperty p : properties) {
			if (p.getName().equals(aName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Build a new DMProperty
	 */
	private static DMProperty makeProperty(Class<?> type, String propertyName, DMModel dmModel, DMRepository repository,
			boolean includesGetOnlyProperties, boolean importReferencedEntities, Vector<String> excludedSignatures) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeProperty() " + propertyName + " for " + type);
		}
		Method method = searchMatchingGetMethod(type, propertyName);
		if (method != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found method " + method);
			}
			return makeProperty(method, dmModel, repository, includesGetOnlyProperties, importReferencedEntities, excludedSignatures);
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Not found method, try field");
			}
			Field f = searchMatchingField(type, propertyName);
			if (f != null) {
				return makeProperty(f, dmModel, repository, importReferencedEntities);
			}
		}
		return null;
	}

	/**
	 * Build a new DMProperty
	 */
	private static DMProperty makeProperty(Method method, DMModel dmModel, DMRepository repository, boolean includesGetOnlyProperties,
			boolean importReferencedEntities, List<String> excludedSignatures) {
		Class<?> type = method.getDeclaringClass();
		Type returnType = method.getGenericReturnType();
		Type[] parameters = method.getGenericParameterTypes();
		if (returnType != Void.TYPE && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
				&& parameters.length == 0) {
			// This signature matches a GET property, lets continue !

			// Look for name
			String propertyName = method.getName();

			// Exclude it from methods
			if (excludedSignatures != null) {
				excludedSignatures.add(DMMethod.signatureForMethod(method, true));
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
			Method setMethod = searchMatchingSetMethod(type, propertyName, returnType);
			boolean isSettable = setMethod != null;
			if (setMethod != null && excludedSignatures != null) {
				excludedSignatures.add(DMMethod.signatureForMethod(setMethod, true));
			}

			// OK, we have the name, let's look at the cardinality
			DMCardinality cardinality = DMCardinality.get(returnType);

			if (cardinality == DMCardinality.VECTOR) {
				TreeSet<AccessorMethod> addToMethods = searchMatchingAddToMethods(type, propertyName);
				TreeSet<AccessorMethod> removeFromMethods = searchMatchingRemoveFromMethods(type, propertyName);
				if (excludedSignatures != null) {
					for (AccessorMethod next : addToMethods) {
						excludedSignatures.add(DMMethod.signatureForMethod(next.getMethod(), true));
					}
					for (AccessorMethod next : removeFromMethods) {
						excludedSignatures.add(DMMethod.signatureForMethod(next.getMethod(), true));
					}
					if (addToMethods.size() == 0 || removeFromMethods.size() == 0) {
						isSettable = false;
					}
				}
			}

			if (cardinality == DMCardinality.HASHTABLE) {
				TreeSet<AccessorMethod> setMethods = searchMatchingSetForKeyMethods(type, propertyName);
				TreeSet<AccessorMethod> removeMethods = searchMatchingRemoveWithKeyMethods(type, propertyName);
				if (excludedSignatures != null) {
					for (AccessorMethod next : setMethods) {
						excludedSignatures.add(DMMethod.signatureForMethod(next.getMethod(), true));
					}
					for (AccessorMethod next : removeMethods) {
						excludedSignatures.add(DMMethod.signatureForMethod(next.getMethod(), true));
					}
					if (setMethods.size() == 0 || removeMethods.size() == 0) {
						isSettable = false;
					}
				}
			}

			// Creates and register the property

			if (includesGetOnlyProperties || isSettable) {

				// lookup type
				DMType propertyType = makeType(repository, returnType, importReferencedEntities);

				/*
				 * DMEntity returnTypeEntity = dmModel.getDMEntity(returnType); if ((returnTypeEntity == null) &&
				 * (importReferencedEntities)) { if (logger.isLoggable(Level.FINE)) logger.finer("Force register " + type.getName() +
				 * " in DataModel: " + returnTypeEntity); LoadableDMEntity.createLoadableDMEntity(returnType, dmModel, false, false);
				 * returnTypeEntity = dmModel.getDMEntity(returnType); if (returnTypeEntity == null) { if (logger.isLoggable(Level.WARNING))
				 * logger.warning("Could not register " + type.getName() + " in DataModel"); } } DMType propertyType; if (returnTypeEntity
				 * != null) { propertyType = new DMType(returnTypeEntity); } else { propertyType = new DMType(returnType); }
				 */

				DMProperty newProperty = new DMProperty(dmModel, propertyName, propertyType, cardinality, true, isSettable,
						DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
				newProperty.setEntity(obtainDMEntity(repository, method.getDeclaringClass(), true));

				// if ((!importReferencedEntities) && (returnTypeEntity == null))
				// newProperty.setUnresolvedTypeName(returnType.getCanonicalName());

				return newProperty;
			}

		}
		return null;
	}

	/**
	 * Build a new DMProperty
	 */
	private static DMProperty makeProperty(Field field, DMModel dmModel, DMRepository repository, /*
																								 * boolean includesGetOnlyProperties,
																								 * boolean includesMethods,
																								 */
			boolean importReferencedEntities/* , Vector excludedSignatures */) {
		// Class type = field.getDeclaringClass();
		Type fieldType = field.getGenericType();
		if (fieldType != Void.TYPE && Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
			// This signature matches a GET property, lets continue !

			// lookup type
			DMType propertyType = makeType(repository, fieldType, importReferencedEntities);

			/*
			 * DMEntity typeEntity = dmModel.getDMEntity(fieldType); if ((typeEntity == null) && (importReferencedEntities)) { if
			 * (logger.isLoggable(Level.FINE)) logger.finer("Force register " + type.getName() + " in DataModel: " + typeEntity);
			 * LoadableDMEntity.createLoadableDMEntity(fieldType, dmModel, false, false); typeEntity = dmModel.getDMEntity(fieldType); if
			 * (typeEntity == null) { if (logger.isLoggable(Level.WARNING)) logger.warning("Could not register " + type.getName() +
			 * " in DataModel"); } }
			 */

			// Look for name
			String propertyName = field.getName();
			// OK, we have the name, let's look at the cardinality
			DMCardinality cardinality = DMCardinality.get(fieldType);

			/*
			 * DMType propertyType = null; if (typeEntity != null) { propertyType = new DMType(typeEntity); } else { propertyType = new
			 * DMType(fieldType); }
			 */

			DMProperty newProperty = new DMProperty(dmModel, propertyName, propertyType, cardinality, true, true,
					DMPropertyImplementationType.PUBLIC_FIELD);
			newProperty.setEntity(obtainDMEntity(repository, field.getDeclaringClass(), true));

			/*
			 * if ((!importReferencedEntities) && (typeEntity == null)) newProperty.setUnresolvedTypeName(fieldType.getCanonicalName());
			 */

			return newProperty;
		}

		return null;
	}

	/**
	 * Try to find a method matching supplied signature
	 * 
	 * Returns corresponding method, null if no such method exist
	 */
	private static Method searchMatchingMethod(Class<?> type, String signature) {
		Method[] methods = type.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (DMMethod.signatureForMethod(method, true).equals(signature)) {
				return method;
			}
			if (DMMethod.signatureForMethod(method, false).equals(signature)) {
				return method;
			}
		}
		return null;
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
	private static Method searchMatchingGetMethod(Class<?> type, String propertyName) {

		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		Vector<String> tries = new Vector<String>();

		tries.add(propertyName);
		tries.add("_" + propertyName);
		tries.add("get" + propertyNameWithFirstCharToUpperCase);
		tries.add("_get" + propertyNameWithFirstCharToUpperCase);

		for (Enumeration<String> e = tries.elements(); e.hasMoreElements();) {
			try {
				String methodName = e.nextElement();
				return getMethod(type, methodName, (Type[]) null);
				// return type.getMethod((String) e.nextElement(), (Class[])null);
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchMethodException err) {
				// we continue
			}
		}

		return null;

	}

	/**
	 * Try to find a matching field, such as (in order):
	 * <ul>
	 * <li>propertyName</li>
	 * <li>_propertyName</li>
	 * </ul>
	 * Returns corresponding field, null if no such method exist
	 */
	private static Field searchMatchingField(Class<?> type, String propertyName) {
		Vector<String> tries = new Vector<String>();

		tries.add(propertyName);
		tries.add("_" + propertyName);

		for (Enumeration<String> e = tries.elements(); e.hasMoreElements();) {
			try {
				String fieldName = e.nextElement();
				return type.getField(fieldName);
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchFieldException err) {
				// we continue
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
	private static Method searchMatchingSetMethod(Class<?> type, String propertyName, Type aType) {

		String propertyNameWithFirstCharToUpperCase = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1, propertyName.length());

		Vector<String> tries = new Vector<String>();

		Type params[] = new Type[1];
		params[0] = aType;
		/*
		 * if (aType instanceof Class) params[0] = (Class)aType; else if (aType instanceof ParameterizedType) params[0] =
		 * (Class)((ParameterizedType)aType).getRawType(); else if (aType instanceof TypeVariable){ logger.warning
		 * ("Pas tres bien gere pour le moment "+aType.getClass()); params[0] = Object.class; }
		 */

		tries.add("set" + propertyNameWithFirstCharToUpperCase);
		tries.add("_set" + propertyNameWithFirstCharToUpperCase);

		for (Enumeration<String> e = tries.elements(); e.hasMoreElements();) {
			try {
				String methodName = e.nextElement();
				// Method returned = type.getMethod(methodName, params);
				Method returned = getMethod(type, methodName, params);
				return returned;
			} catch (SecurityException err) {
				// we continue
			} catch (NoSuchMethodException err) {
				// we continue
			}
		}

		return null;

	}

	private static Method getMethod(Class<?> type, String methodName, Type... params) throws NoSuchMethodException {
		if (params == null) {
			params = new Type[0];
		}
		StringBuffer sb = null;
		if (logger.isLoggable(Level.FINE)) {
			sb = new StringBuffer();
			for (Type t : params) {
				sb.append(" " + t.toString());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Looking for " + methodName + " with" + sb.toString());
			}
		}
		for (Method m : type.getMethods()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Examining " + m);
			}
			if (m.getName().equals(methodName) && m.getGenericParameterTypes().length == params.length) {
				boolean paramMatches = true;
				for (int i = 0; i < params.length; i++) {
					if (!params[i].equals(m.getGenericParameterTypes()[i])) {
						paramMatches = false;
					}
				}
				if (paramMatches) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Looking for " + methodName + " with" + sb.toString() + ": found");
					}
					return m;
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Looking for " + methodName + " with" + sb.toString() + ": NOT found");
		}
		throw new NoSuchMethodException();
	}

	/**
	 * Search and return matching "setForKey" methods<br>
	 * NB: 'setForKey...' methods are methods with general form: <code>setXXXForKey(Class anObject, Class aKey)</code> or
	 * <code>_setXXXForKey(Class anObject, Class aKey)</code>, where XXX is the property name (try with or without a terminal 's'
	 * character), and Class could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingSetForKeyMethods(Class<?> type, String propertyName) {

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

		return searchMethodsWithNameAndParamsNumber(type, methodNameCondidates, 2);
	}

	/**
	 * Search and return matching "removeWithKey" methods<br>
	 * NB: 'removeWithKey...' methods are methods with general form: <code>removeXXXWithKey(Class aKey)</code> or
	 * <code>_removeXXXWithKey(Class aKey)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingRemoveWithKeyMethods(Class<?> type, String propertyName) {

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

		return searchMethodsWithNameAndParamsNumber(type, methodNameCondidates, 1);
	}

	/**
	 * Search and return matching "addTo" methods<br>
	 * NB: 'addTo...' methods are methods with general form: <code>addToXXX(Class anObject)</code> or <code>_addToXXX(Class anObject)</code>
	 * , where XXX is the property name (try with or without a terminal 's' character), and Class could be anything... Returns an ordered
	 * TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingAddToMethods(Class<?> type, String propertyName) {

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

		return searchMethodsWithNameAndParamsNumber(type, methodNameCondidates, 1);
	}

	/**
	 * Search and return matching "removeFrom" methods<br>
	 * NB: 'removeFrom...' methods are methods with general form: <code>removeFromXXX(Class anObject)</code> or
	 * <code>_removeFromXXX(Class anObject)</code>, where XXX is the property name (try with or without a terminal 's' character), and Class
	 * could be anything... Returns an ordered TreeSet of {@link AccessorMethod} objects
	 */
	private static TreeSet<AccessorMethod> searchMatchingRemoveFromMethods(Class<?> type, String propertyName) {

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

		return searchMethodsWithNameAndParamsNumber(type, methodNameCondidates, 1);
	}

	/**
	 * Search and returns all methods (as {@link AccessorMethod} objects) of related class whose names is in the specified string list, with
	 * exactly the specified number of parameters, ascendant ordered regarding parameters specialization.
	 * 
	 * @see AccessorMethod
	 */
	private static TreeSet<AccessorMethod> searchMethodsWithNameAndParamsNumber(Class<?> type, String[] searchedNames, int paramNumber) {

		TreeSet<AccessorMethod> returnedTreeSet = new TreeSet<AccessorMethod>();
		Method[] allMethods = type.getMethods();

		for (int i = 0; i < allMethods.length; i++) {
			Method tempMethod = allMethods[i];
			for (int j = 0; j < searchedNames.length; j++) {
				if (tempMethod.getName().equalsIgnoreCase(searchedNames[j]) && tempMethod.getParameterTypes().length == paramNumber) {
					// This is a good candidate
					returnedTreeSet.add(new AccessorMethod(tempMethod));
				}
			}
		}
		return returnedTreeSet;
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
	public static class AccessorMethod implements Comparable<AccessorMethod> {

		/** Stores the related <code>Method</code> */
		protected Method method;

		/**
		 * Creates a new <code>AccessorMethod</code> instance.
		 * 
		 * @param aKeyValueProperty
		 *            a <code>KeyValueProperty</code> value
		 * @param aMethod
		 *            a <code>Method</code> value
		 */
		public AccessorMethod(Method aMethod) {

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
		public int compareTo(AccessorMethod comparedAccessorMethod) throws ClassCastException {
			if (getMethod().getParameterTypes().length != comparedAccessorMethod.getMethod().getParameterTypes().length) {

				// Those objects could not be compared and should be treated
				// as equals
				// regarding the specialization of their parameters
				return 2;
			} else {

				for (int i = 0; i < getMethod().getParameterTypes().length; i++) {
					Class<?> localParameterType = getMethod().getParameterTypes()[i];
					Class<?> comparedParameterType = comparedAccessorMethod.getMethod().getParameterTypes()[i];
					if (!localParameterType.equals(comparedParameterType)) {
						boolean localParamIsParentOfComparedParam = localParameterType.isAssignableFrom(comparedParameterType);
						boolean localParamIsChildOfComparedParam = comparedParameterType.isAssignableFrom(localParameterType);
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

		/**
		 * Return the related <code>Method</code>
		 * 
		 * @return
		 */
		public Method getMethod() {

			return method;
		}

	}

	/**
	 * Update property Delete supplied property when not found in Class
	 * 
	 * @param property
	 * @throws InvalidNameException
	 * @throws DuplicatePropertyNameException
	 */
	private void updateProperty(DMProperty property) throws InvalidNameException, DuplicatePropertyNameException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update property " + property);
		}
		DMProperty updatedProperty = makeProperty(getJavaType(), property.getName(), getDMModel(), getRepository(), true, true, null);
		if (updatedProperty == null) {
			logger.info("Delete property: " + property.getName());
			property.delete();
		} else {
			property.update(updatedProperty, false);
		}
	}

	/**
	 * Create property using property reference
	 * 
	 * @param property
	 */
	private DMProperty createProperty(PropertyReference propertyReference) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Create property " + propertyReference.getName());
		}
		DMProperty returnedProperty = makeProperty(getJavaType(), propertyReference.getName(), getDMModel(), getRepository(), true, true,
				null);
		registerProperty(returnedProperty, false);
		return returnedProperty;
	}

	/**
	 * Update method using defined type as Class Delete supplied method when not found in Class
	 * 
	 * @param method
	 * @throws DuplicateMethodSignatureException
	 */
	private void updateMethod(DMMethod method) throws DuplicateMethodSignatureException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update method " + method);
		}
		DMMethod updatedMethod = makeMethod(getJavaType(), method.getSignature(), getDMModel(), getRepository(), true);
		if (updatedMethod == null) {
			method.delete();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update method " + method.getSignature() + " with " + updatedMethod.getSignature());
			}
			method.update(updatedMethod, false);
		}
	}

	/**
	 * Create method using method reference
	 * 
	 * @param property
	 */
	private DMMethod createMethod(MethodReference methodReference) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Create method " + methodReference.getSignature());
		}
		DMMethod returnedMethod = makeMethod(getJavaType(), methodReference.getSignature(), getDMModel(), getRepository(), true);
		if (returnedMethod == null) {
			logger.warning("Could not retrieve method " + methodReference.getSignature());
			return null;
		}
		registerMethod(returnedMethod);
		return returnedMethod;
	}

	/**
	 * Update this entity given a supplied set of properties and methods to include
	 * 
	 * @param aClassReference
	 */
	public void update(ClassReference aClassReference) {
		if (getJavaType() == null) {
			logger.warning("Could not update: could not find class !");
		} else {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update " + getName() + " with " + aClassReference.getName());
			}

			// First update package and class name, and parent class if different
			initializeFromClass(false, false);

			// Then what's about properties ?
			List<DMProperty> propertiesToDelete = new ArrayList<DMProperty>(getProperties().values());
			for (Enumeration<PropertyReference> en = aClassReference.getPropertiesEnumeration(); en.hasMoreElements();) {
				PropertyReference nextPropertyRef = en.nextElement();
				if (nextPropertyRef.isSelected()) {
					DMProperty property = getDMProperty(nextPropertyRef.getName());
					// logger.info("Next selected: "+nextPropertyRef.getName()+" existing="+property);
					if (property != null) {
						// A selected property already declared, update it
						try {
							updateProperty(property);
						} catch (InvalidNameException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (DuplicatePropertyNameException e) {
							e.printStackTrace();
							logger.warning(e.getMessage());
						}
						propertiesToDelete.remove(property);
					} else {
						// This is a newly imported property, creates it
						createProperty(nextPropertyRef);
					}
				}
			}
			for (DMProperty toDelete : propertiesToDelete) {
				logger.info("Delete property: " + toDelete.getName());
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Delete property " + toDelete);
				}
				toDelete.delete();
			}

			// Then what's about methods ?
			List<DMMethod> methodsToDelete = new ArrayList<DMMethod>(getMethods().values());
			for (Enumeration<MethodReference> en = aClassReference.getMethodsEnumeration(); en.hasMoreElements();) {
				MethodReference nextMethodRef = en.nextElement();
				if (nextMethodRef.isSelected()) {
					DMMethod method = getDeclaredMethod(nextMethodRef.getSignature());
					if (method != null) {
						// A selected method already declared, update it
						try {
							updateMethod(method);
						} catch (DuplicateMethodSignatureException e) {
							e.printStackTrace();
						}
						methodsToDelete.remove(method);
					} else {
						// This is a newly imported property, creates it
						createMethod(nextMethodRef);
					}
				}
			}
			for (DMMethod toDelete : methodsToDelete) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Delete method " + toDelete);
				}
				toDelete.delete();
			}

		}
	}

	public void update(boolean includeGetOnlyProperties, boolean includeMethods) {
		if (getJavaType() == null) {
			logger.warning("Could not update: could not find class !");
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update " + this + " with " + includeGetOnlyProperties + " and " + includeMethods);
			}

			// First update package and class name, and parent class if different
			initializeFromClass(false, false);

			// Look after all required properties

			List<String> excludedSignatures = new Vector<String>();
			List<DMProperty> allRequiredProperties = LoadableDMEntity.searchForProperties(getJavaType(), getDMModel(), getRepository(),
					includeGetOnlyProperties, false, excludedSignatures);
			List<DMMethod> allRequiredMethods;
			if (includeMethods) {
				allRequiredMethods = LoadableDMEntity.searchForMethods(getJavaType(), getDMModel(), getRepository(), false,
						excludedSignatures);
			} else {
				allRequiredMethods = new Vector<DMMethod>();
			}

			// Then what's about properties ?

			List<DMProperty> properties = new ArrayList<DMProperty>(getProperties().values());

			for (DMProperty nextProperty : properties) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update property " + nextProperty);
				}
				try {
					updateProperty(nextProperty);
				} catch (InvalidNameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicatePropertyNameException e) {
					e.printStackTrace();
					logger.warning(e.getMessage());
				}

				for (DMProperty next : new ArrayList<DMProperty>(allRequiredProperties)) {
					if (next.getName().equals(nextProperty.getName())) {
						allRequiredProperties.remove(next);
					}
				}
			}

			// Those properties still required, add them:

			for (DMProperty nextProperty : allRequiredProperties) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Create property " + nextProperty);
				}
				registerProperty(nextProperty, false);
			}

			// Then what's about methods ?

			List<DMMethod> methods = new ArrayList<DMMethod>(getMethods().values());

			for (DMMethod nextMethod : methods) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update method " + nextMethod);
				}
				try {
					updateMethod(nextMethod);
				} catch (DuplicateMethodSignatureException e) {
					e.printStackTrace();
				}
				for (DMMethod next : new ArrayList<DMMethod>(allRequiredMethods)) {
					if (next.getSignature().equals(nextMethod.getSignature())) {
						allRequiredMethods.remove(next);
					}
				}
			}

			// Those methods still required, add them:

			for (DMMethod nextMethod : allRequiredMethods) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Create method " + nextMethod);
				}
				registerMethod(nextMethod);
			}

		}
	}

	/**
	 * Tells if code generation is applicable for related DMEntity Always false for compiled classes
	 * 
	 * @return
	 */
	@Override
	public boolean isCodeGenerationApplicable() {
		return false;
	}

}
