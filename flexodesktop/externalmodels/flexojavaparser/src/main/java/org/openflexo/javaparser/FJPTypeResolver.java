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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.LoadableDMEntity;
import org.openflexo.javaparser.FJPJavaSource.FJPImportDeclarations.FJPImportDeclaration;

import com.thoughtworks.qdox.model.Type;

/**
 * Utility class used to resolve types in the context of Flexo Data Model (mapping between Type and DMEntity)
 * 
 * @author sylvain
 * 
 */
public class FJPTypeResolver {

	private static final Logger logger = Logger.getLogger(FJPTypeResolver.class.getPackage().getName());

	private static DMEntity entityForType(Type type, DMModel dataModel) {
		DMEntity returned = dataModel.getCachedEntitiesForTypes().get(type.getValue());
		return returned;
	}

	private static void storeEntityForType(DMEntity entity, Type type, DMModel dataModel) {
		dataModel.getCachedEntitiesForTypes().put(type.getValue(), entity);
	}

	private static enum ClassResolvingMethod {
		Cache, DataModel, ImportDeclarations, Context, ClassPath
	}

	/*public static long timeSpendResolvingTypes = 0;
	public static Vector<Type> searchedTypes;*/

	/*public static void startDebug()
	{
		timeSpendResolvingTypes = 0;
		searchedTypes = new Vector<Type>();
	}
	
	public static void stopDebug()
	{
		logger.info("Searched "+searchedTypes.size()+" types");
		Date date1 = new Date();
		for (Type t : searchedTypes) {
			t.getValue();
		}
		Date date2 = new Date();
		logger.info("Time getValue() "+(date2.getTime()-date1.getTime())+" ms");
		Date date3 = new Date();
		for (Type t : searchedTypes) {
			t.toString();
		}
		Date date4 = new Date();
		logger.info("Time toString() "+(date4.getTime()-date3.getTime())+" ms");
		Date date5 = new Date();
		for (Type t : searchedTypes) {
			t.hashCode();
		}
		Date date6 = new Date();
		logger.info("Time hashCode() "+(date6.getTime()-date5.getTime())+" ms");
		for (Type t : searchedTypes) {
			//logger.info("Type "+t.hashCode()+" library "+t.getJavaClassParent().getClassLibrary());
		}
	}*/

	protected static class TypeLookupInfo {
		DMEntity foundEntity = null;
		boolean resolvedButEntityNotAvailableYet = false;
	}

	private static TypeLookupInfo lookupEntity(Type type, DMModel dataModel, FJPDMSet context, FJPJavaSource source, boolean resolveNow)
			throws CrossReferencedEntitiesException {
		// Date date1 = new Date();

		TypeLookupInfo returned = new TypeLookupInfo();
		returned.foundEntity = null;
		returned.resolvedButEntityNotAvailableYet = false;

		ClassResolvingMethod m = ClassResolvingMethod.Cache;

		while (returned.foundEntity == null && m != null && !returned.resolvedButEntityNotAvailableYet) {
			if (m == ClassResolvingMethod.Cache) {
				returned.foundEntity = entityForType(type, dataModel);
			} else if (m == ClassResolvingMethod.DataModel) {
				returned.foundEntity = dataModel.getDMEntity(type.getValue());
				if (returned.foundEntity == null && source.getPackage() == null) {
					returned.foundEntity = dataModel.getDMEntity(DMPackage.DEFAULT_PACKAGE_NAME, type.getValue());
				}
			} else if (m == ClassResolvingMethod.ImportDeclarations) {
				if (source != null) {
					for (FJPImportDeclaration importDeclaration : source.getImportDeclarations().getImportDeclarations()) {
						if (importDeclaration.getImportDeclaration().endsWith(".*")) {
							String importPrefix = importDeclaration.getImportDeclaration().substring(0,
									importDeclaration.getImportDeclaration().lastIndexOf(".*"));
							returned.foundEntity = dataModel.getDMEntity(importPrefix + "." + type.getValue());
							if (returned.foundEntity != null) {
								source.getQDSource().getClassLibrary().add(importPrefix + "." + type.getValue());
								break;
							}
						} else if (importDeclaration.getImportDeclaration().endsWith(type.getValue())) {
							returned.foundEntity = dataModel.getDMEntity(importDeclaration.getImportDeclaration());
							if (returned.foundEntity != null) {
								source.getQDSource().getClassLibrary().add(importDeclaration.getImportDeclaration());
								break;
							}
						}
					}
				}
			} else if (m == ClassResolvingMethod.Context) {
				if (context != null) {
					ClassReference classRef = context.getClassReference(type.getValue());
					if (classRef != null) {
						if (resolveNow) {
							returned.foundEntity = context.justResolvedEntity(classRef);
							if (returned.foundEntity == null) {
								throw new CrossReferencedEntitiesException();
							}
						} else {
							returned.resolvedButEntityNotAvailableYet = true;
						}
					}
				}
			} else if (m == ClassResolvingMethod.ClassPath) {
				try {
					Class<?> searchedClass = Class.forName(type.getValue());
					if (resolveNow) {
						returned.foundEntity = LoadableDMEntity.createLoadableDMEntity(dataModel.getJDKRepository(), searchedClass, false,
								false);
					} else {
						returned.resolvedButEntityNotAvailableYet = true;
					}
				} catch (ClassNotFoundException e) {
				}
			}
			if (returned.foundEntity == null) {
				if (m.ordinal() + 1 < ClassResolvingMethod.values().length) {
					m = ClassResolvingMethod.values()[m.ordinal() + 1];
				} else {
					m = null;
				}
			}
		}

		if (returned.foundEntity != null) {
			storeEntityForType(returned.foundEntity, type, dataModel);
		}

		/*Date date2 = new Date();
		long timeToResolveThisType = (date2.getTime()-date1.getTime());
		timeSpendResolvingTypes += timeToResolveThisType;
		logger.info("Spent "+timeToResolveThisType+" ms to resolve "+type.getValue()+" with method "+m);*/

		return returned;

	}

	public static class CrossReferencedEntitiesException extends Exception {
	}

	public static class UnresolvedTypeException extends FlexoException {
		private Type _unresolvedType;

		public UnresolvedTypeException(Type unresolvedType) {
			super("Cannot resolve " + unresolvedType);
			_unresolvedType = unresolvedType;
		}

		public Type getUnresolvedType() {
			return _unresolvedType;
		}
	}

	public static boolean isResolvable(Type type, DMModel dataModel, FJPDMSet context, FJPJavaSource source) {
		if (type == null) {
			logger.warning("isResolvable() called for null type");
			return false;
		}

		if (!type.isResolved()) {
			return false;
		}

		TypeLookupInfo lookupInfo;
		try {
			lookupInfo = lookupEntity(type, dataModel, context, source, false);
		} catch (CrossReferencedEntitiesException e1) {
			lookupInfo = new TypeLookupInfo();
		}

		if (lookupInfo.foundEntity != null) {
			return true;
		} else if (lookupInfo.resolvedButEntityNotAvailableYet) {
			return true;
		} else {
			logger.warning("Type: " + type + " is not resolvable");
			return false;
		}
	}

	public static DMEntity resolveEntity(Type type, DMModel dataModel, FJPDMSet context, FJPJavaSource source,
			boolean importReferencedEntities) throws CrossReferencedEntitiesException {
		if (!type.isResolved()) {
			return null;
		}

		TypeLookupInfo lookupInfo = lookupEntity(type, dataModel, context, source, true);

		// Warn if not found
		if (lookupInfo.foundEntity == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not resolve " + type.getValue());
			}
		} else {
			if (type instanceof DMType) {
				((DMType) type).resolveAs(lookupInfo.foundEntity);
			}
		}

		return lookupInfo.foundEntity;
	}

}
