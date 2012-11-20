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
package org.openflexo.antar.binding;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.expr.EvaluationType;

import com.google.common.primitives.Primitives;

public class TypeUtils {

	static final Logger logger = Logger.getLogger(TypeUtils.class.getPackage().getName());

	public static Class getBaseClass(Type aType) {
		if (aType == null) {
			return null;
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).getBaseClass();
		}
		if (isResolved(aType)) {
			if (aType instanceof Class) {
				return (Class) aType;
			} else if (aType instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) aType).getRawType();
				if (rawType instanceof Class) {
					return (Class) rawType;
				}
				logger.warning("Not handled: " + aType.getClass().getName());
				return null;
			} else {
				logger.warning("Not handled: " + aType.getClass().getName());
				return null;
			}
		}
		logger.warning("Not handled: " + aType.getClass().getName());
		return null;
	}

	public static boolean isClassAncestorOf(Class<?> parentClass, Class<?> childClass) {
		if (parentClass == null) {
			return false;
		}
		if (childClass == null) {
			return false;
		}

		if (isVoid(parentClass)) {
			return isVoid(childClass);
		}
		if (isVoid(childClass)) {
			return isVoid(parentClass);
		}

		// Special cases
		if (parentClass == Object.class && childClass.isPrimitive()) {
			return true;
		}
		if (parentClass.isPrimitive()) {
			return isClassAncestorOf(fromPrimitive(parentClass), childClass);
		}
		if (childClass.isPrimitive()) {
			return isClassAncestorOf(parentClass, fromPrimitive(childClass));
		}

		// Normal case
		return parentClass.isAssignableFrom(childClass);
	}

	public static Class toPrimitive(Class<?> aClass) {
		if (isDouble(aClass)) {
			return Double.TYPE;
		}
		if (isFloat(aClass)) {
			return Float.TYPE;
		}
		if (isLong(aClass)) {
			return Long.TYPE;
		}
		if (isInteger(aClass)) {
			return Integer.TYPE;
		}
		if (isShort(aClass)) {
			return Short.TYPE;
		}
		if (isByte(aClass)) {
			return Byte.TYPE;
		}
		if (isBoolean(aClass)) {
			return Boolean.TYPE;
		}
		if (isChar(aClass)) {
			return Character.TYPE;
		}
		return aClass;
	}

	public static Class fromPrimitive(Class<?> aClass) {
		if (isDouble(aClass)) {
			return Double.class;
		}
		if (isFloat(aClass)) {
			return Float.class;
		}
		if (isLong(aClass)) {
			return Long.class;
		}
		if (isInteger(aClass)) {
			return Integer.class;
		}
		if (isShort(aClass)) {
			return Short.class;
		}
		if (isByte(aClass)) {
			return Byte.class;
		}
		if (isBoolean(aClass)) {
			return Boolean.class;
		}
		if (isChar(aClass)) {
			return Character.class;
		}
		return aClass;
	}

	public static boolean isPrimitive(Type type) {
		return type != null && Primitives.allPrimitiveTypes().contains(type);
	}

	public static boolean isDouble(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Double.class) || type.equals(Double.TYPE);
	}

	public static boolean isFloat(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Float.class) || type.equals(Float.TYPE);
	}

	public static boolean isInteger(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Integer.class) || type.equals(Integer.TYPE);
	}

	public static boolean isLong(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Long.class) || type.equals(Long.TYPE);
	}

	public static boolean isObject(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Object.class);
	}

	public static boolean isShort(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Short.class) || type.equals(Short.TYPE);
	}

	public static boolean isString(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(String.class);
	}

	public static boolean isVoid(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Void.class) || type.equals(Void.TYPE);
	}

	public static boolean isBoolean(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Boolean.class) || type.equals(Boolean.TYPE);
	}

	public static boolean isByte(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Byte.class) || type.equals(Byte.TYPE);
	}

	public static boolean isChar(Type type) {
		if (type == null) {
			return false;
		}
		return type.equals(Character.class) || type.equals(Character.TYPE);
	}

	public static EvaluationType kindOfType(Type type) {
		if (isBoolean(type)) {
			return EvaluationType.BOOLEAN;
		} else if (isInteger(type) || isLong(type) || isShort(type) || isChar(type) || isByte(type)) {
			return EvaluationType.ARITHMETIC_INTEGER;
		} else if (isFloat(type) || isDouble(type)) {
			return EvaluationType.ARITHMETIC_FLOAT;
		} else if (isString(type)) {
			return EvaluationType.STRING;
		}
		return EvaluationType.LITERAL;
	}

	public static boolean isTypeAssignableFrom(Type aType, Type anOtherType) {
		return isTypeAssignableFrom(aType, anOtherType, true);
	}

	/**
	 * Determines if the class or interface represented by supplied <code>aType</code> object is either the same as, or is a superclass or
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
	public static boolean isTypeAssignableFrom(Type aType, Type anOtherType, boolean permissive) {
		// Test if anOtherType instanceof aType

		/*if (aType instanceof CustomType || anOtherType instanceof CustomType) {
			logger.info("Called " + aType + " isAssignableFrom(" + anOtherType + ")");
			logger.info("En gros je me demande si " + anOtherType + " est bien une instance de " + aType + " anOtherType est un "
					+ anOtherType.getClass().getSimpleName());
		}*/

		// If supplied type is null return false
		if (aType == null || anOtherType == null) {
			return false;
		}

		// Everything could be assigned to Object
		if (isObject(aType)) {
			return true;
		}

		// Special case for Custom types
		if (aType instanceof CustomType) {
			return ((CustomType) aType).isTypeAssignableFrom(anOtherType, permissive);
		}

		if (anOtherType instanceof CustomType && isTypeAssignableFrom(aType, ((CustomType) anOtherType).getBaseClass())) {
			return true;
		}

		if (isVoid(aType)) {
			return isVoid(anOtherType);
		}

		if (isBoolean(aType)) {
			return isBoolean(anOtherType);
		}

		if (isDouble(aType)) {
			return isDouble(anOtherType) || isFloat(anOtherType) || isLong(anOtherType) || isInteger(anOtherType) || isShort(anOtherType)
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (isFloat(aType)) {
			return isDouble(anOtherType) && permissive || isFloat(anOtherType) || isLong(anOtherType) || isInteger(anOtherType)
					|| isShort(anOtherType) || isChar(anOtherType) || isByte(anOtherType);
		}

		if (isLong(aType)) {
			return isLong(anOtherType) || isInteger(anOtherType) || isShort(anOtherType) || isChar(anOtherType) || isByte(anOtherType);
		}

		if (isInteger(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) || isShort(anOtherType) || isChar(anOtherType)
					|| isByte(anOtherType);
		}

		if (isShort(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) || isChar(anOtherType)
					|| isByte(anOtherType);
		}

		if (isChar(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) && permissive
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (isByte(aType)) {
			return isLong(anOtherType) && permissive || isInteger(anOtherType) && permissive || isShort(anOtherType) && permissive
					|| isChar(anOtherType) || isByte(anOtherType);
		}

		if (aType instanceof WildcardType) {
			if (anOtherType instanceof WildcardType) {
				// If two wildcards, perform check on both upper bounds
				return isTypeAssignableFrom(((WildcardType) aType).getUpperBounds()[0], ((WildcardType) anOtherType).getUpperBounds()[0],
						permissive);
			}
			// Perform check on first upper bound only
			return isTypeAssignableFrom(((WildcardType) aType).getUpperBounds()[0], anOtherType, permissive);
		}

		if (aType instanceof GenericArrayType) {
			// logger.info("Called "+aType+" isAssignableFrom("+anOtherType+")");
			// logger.info("anOtherType is a "+anOtherType.getClass());
			if (anOtherType instanceof GenericArrayType) {
				return isTypeAssignableFrom(((GenericArrayType) aType).getGenericComponentType(),
						((GenericArrayType) anOtherType).getGenericComponentType(), permissive);
			} else if (anOtherType instanceof Class && ((Class) anOtherType).isArray()) {
				return isTypeAssignableFrom(((GenericArrayType) aType).getGenericComponentType(), ((Class) anOtherType).getComponentType(),
						permissive);
			}
			return false;
		}

		// Look if we are on same class
		if (aType instanceof Class && anOtherType instanceof Class) {
			return isClassAncestorOf((Class) aType, (Class) anOtherType);
		}

		if (!isClassAncestorOf(getBaseClass(aType), getBaseClass(anOtherType))) {
			return false;
		}

		// logger.info(""+getBaseClass(aType)+" is ancestor of "+getBaseClass(anOtherType));

		if (aType instanceof Class || anOtherType instanceof Class) {
			// One of two types is not parameterized, we cannot check, return true
			return true;
		}

		if (aType instanceof ParameterizedType && anOtherType instanceof ParameterizedType) {
			ParameterizedType t1 = (ParameterizedType) aType;
			ParameterizedType t2 = (ParameterizedType) anOtherType;

			// Now check that parameters size are the same
			if (t1.getActualTypeArguments().length != t2.getActualTypeArguments().length) {
				return false;
			}

			// Now, we have to compare parameter per parameter
			for (int i = 0; i < t1.getActualTypeArguments().length; i++) {
				Type st1 = t1.getActualTypeArguments()[i];
				Type st2 = t2.getActualTypeArguments()[i];
				if (!isTypeAssignableFrom(st1, st2, true)) {
					return false;
				}
			}
			return true;
		}

		/*if (getBaseEntity() == type.getBaseEntity()) {
			// Base entities are the same, let's analyse parameters
		
			// If one of both paramters def is empty (parameters are not defined, as before java5)
			// accept it without performing a test which is impossible to perform
			if ((getParameters().size() == 0)
					|| (type.getParameters().size() == 0)) return true;
		
			// Now check that parameters size are the same
			if (getParameters().size() != type.getParameters().size()) return false;
		
			// Now, we have to compare parameter per parameter
			for (int i=0; i<getParameters().size(); i++) 
			{
				DMType localParam = getParameters().elementAt(i);
				DMType sourceParam = type.getParameters().elementAt(i);
		
				if (localParam.getKindOfType() == KindOfType.WILDCARD
						&& localParam.getUpperBounds().size()==1) {
					DMType resultingSourceParamType;
					if (sourceParam.getKindOfType() == KindOfType.WILDCARD
							&& sourceParam.getUpperBounds().size()==1) {
						resultingSourceParamType = sourceParam.getUpperBounds().firstElement().bound;
					}
					else {
						resultingSourceParamType = sourceParam;
					}
					if (!localParam.getUpperBounds().firstElement().bound.isAssignableFrom(resultingSourceParamType,permissive)) {
						return false; 
					}
						}
						else if (!localParam.equals(sourceParam)) {
							return false;
						}
					}
					return true;    			
				}
		
				// Else it's a true ancestor
				else {
					//DMType parentType = makeInstantiatedDMType(type.getBaseEntity().getParentType(),type);
					DMType parentType = makeInstantiatedDMType(getBaseEntity().getClosestAncestorOf(type.getBaseEntity()),type);
					return isAssignableFrom(parentType,permissive);
				}*/

		return false;
	}

	public static String simpleRepresentation(Type aType) {
		if (aType == null) {
			return "null";
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).simpleRepresentation();
		}
		if (aType instanceof Class) {
			return ((Class) aType).getSimpleName();
		} else if (aType instanceof ParameterizedType) {
			ParameterizedType t = (ParameterizedType) aType;
			StringBuilder sb = new StringBuilder();
			sb.append(simpleRepresentation(t.getRawType())).append("<");
			boolean isFirst = true;
			for (Type st : t.getActualTypeArguments()) {
				sb.append(isFirst ? "" : ",").append(simpleRepresentation(st));
				isFirst = false;
			}
			sb.append(">");
			return sb.toString();
		}
		return aType.toString();
	}

	public static String fullQualifiedRepresentation(Type aType) {
		if (aType == null) {
			return null;
		}
		if (aType instanceof CustomType) {
			return ((CustomType) aType).fullQualifiedRepresentation();
		}
		if (aType instanceof Class) {
			return ((Class) aType).getName();
		} else if (aType instanceof ParameterizedType) {
			ParameterizedType t = (ParameterizedType) aType;
			StringBuilder sb = new StringBuilder();
			sb.append(fullQualifiedRepresentation(t.getRawType())).append("<");
			boolean isFirst = true;
			for (Type st : t.getActualTypeArguments()) {
				sb.append(isFirst ? "" : ",").append(fullQualifiedRepresentation(st));
				isFirst = false;
			}
			sb.append(">");
			return sb.toString();
		}
		return aType.toString();
	}

	public static boolean isResolved(Type type) {
		return type instanceof Class || type instanceof GenericArrayType || type instanceof ParameterizedType || type instanceof CustomType;
	}

	/**
	 * Return flag indicating if this type is considered as generic A generic type is a type that is parameterized with type variable(s). If
	 * this type is resolved but contains a type in it definition containing itself a generic definition, then this type is also generic
	 * (this 'isGeneric' property is recursively transmissible).
	 * 
	 * @return a flag indicating whether this type is resolved or not
	 */
	public static boolean isGeneric(Type type) {
		if (type instanceof CustomType) {
			return false;
		}
		if (type instanceof Class) {
			return false;
		}
		if (type instanceof GenericArrayType) {
			return isGeneric(((GenericArrayType) type).getGenericComponentType());
		}
		if (type instanceof ParameterizedType) {
			for (Type t : ((ParameterizedType) type).getActualTypeArguments()) {
				if (isGeneric(t)) {
					return true;
				}
			}
			return false;
		}
		if (type instanceof TypeVariable) {
			return true;
		}
		if (type instanceof WildcardType) {
			WildcardType w = (WildcardType) type;
			if (w.getUpperBounds() != null && w.getUpperBounds().length > 0) {
				for (Type b : w.getUpperBounds()) {
					if (isGeneric(b)) {
						return true;
					}
				}
			}
			if (w.getLowerBounds() != null && w.getLowerBounds().length > 0) {
				for (Type b : w.getLowerBounds()) {
					if (isGeneric(b)) {
						return true;
					}
				}
			}
			return false;
		}
		logger.warning("Unexpected " + type);
		return false;
	}

	/**
	 * Build instanciated DMType considering supplied type is generic (contains TypeVariable definitions) Returns a clone of DMType where
	 * all references to TypeVariable are replaced by values defined in context type. For example, given type=Enumeration<E> and
	 * context=Vector<String>, returns Enumeration<String> If supplied type is not generic, return type value (without cloning!)
	 * 
	 * @param type
	 *            : type to instanciate
	 * @param context
	 *            : context used to instanciate type
	 * @return
	 */
	public static Type makeInstantiatedType(Type type, Type context) {
		if (type == null) {
			return null;
		}

		if (!isGeneric(type)) {
			return type;
		}

		if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = new Type[((ParameterizedType) type).getActualTypeArguments().length];
			for (int i = 0; i < ((ParameterizedType) type).getActualTypeArguments().length; i++) {
				actualTypeArguments[i] = makeInstantiatedType(((ParameterizedType) type).getActualTypeArguments()[i], context);
			}
			return new ParameterizedTypeImpl((Class) ((ParameterizedType) type).getRawType(), actualTypeArguments);
		}

		if (type instanceof GenericArrayType) {
			return new GenericArrayTypeImpl(makeInstantiatedType(((GenericArrayType) type).getGenericComponentType(), context));
		}

		if (type instanceof TypeVariable) {
			TypeVariable<GenericDeclaration> tv = (TypeVariable<GenericDeclaration>) type;
			GenericDeclaration gd = tv.getGenericDeclaration();
			// System.out.println("Found type variable "+tv+" name="+tv.getName()+" GD="+tv.getGenericDeclaration());
			if (gd instanceof Class) {
				if (context instanceof ParameterizedType) {
					for (int i = 0; i < gd.getTypeParameters().length; i++) {
						if (gd.getTypeParameters()[i].equals(tv)) {
							// Found matching parameterized type
							if (i < ((ParameterizedType) context).getActualTypeArguments().length) {
								// logger.info("********* return instantiatedType for "+type+" context="+context+" gd="+gd);
								if (!((ParameterizedType) context).getRawType().equals(gd)) {
									return makeInstantiatedType(type, getSuperType(context));
								}
								return ((ParameterizedType) context).getActualTypeArguments()[i];
							} else {
								logger.warning("Could not retrieve parameterized type " + tv + " with context "
										+ simpleRepresentation(context));
								return type;
							}
						}
					}
				} else if (context instanceof Class && ((Class) context).getGenericSuperclass() != null) {
					return makeInstantiatedType(type, ((Class) context).getGenericSuperclass());
				}
			} else if (gd instanceof Method) {
				return type;
			}
			logger.warning("Not found type variable " + tv + " in context " + context + " GenericDeclaration=" + tv.getGenericDeclaration());
			// throw new InvalidKeyValuePropertyException("Not found type variable "+tv+" in context "+context);
			return type;
		}

		if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] upperBounds = new Type[wt.getUpperBounds().length];
			for (int i = 0; i < wt.getUpperBounds().length; i++) {
				upperBounds[i] = makeInstantiatedType(wt.getUpperBounds()[i], context);
			}
			Type[] lowerBounds = new Type[wt.getLowerBounds().length];
			for (int i = 0; i < wt.getLowerBounds().length; i++) {
				lowerBounds[i] = makeInstantiatedType(wt.getLowerBounds()[i], context);
			}
			return new WilcardTypeImpl(upperBounds, lowerBounds);
		}

		logger.warning("Unexpected " + type);
		return type;

	}

	public static Type getSuperType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType myType = (ParameterizedType) type;
			Type superType = ((Class<?>) myType.getRawType()).getGenericSuperclass();
			if (superType instanceof ParameterizedType) {
				Type[] actualTypeArguments = new Type[((ParameterizedType) superType).getActualTypeArguments().length];
				for (int i = 0; i < ((ParameterizedType) superType).getActualTypeArguments().length; i++) {
					Type tv2 = ((ParameterizedType) superType).getActualTypeArguments()[i];
					actualTypeArguments[i] = makeInstantiatedType(tv2, type);
				}
				return new ParameterizedTypeImpl(((Class<?>) ((ParameterizedType) type).getRawType()).getSuperclass(), actualTypeArguments);
			}
		} else if (type instanceof Class) {
			return ((Class) type).getGenericSuperclass();
		}
		if (type instanceof CustomType) {
			return getSuperType(((CustomType) type).getBaseClass());
		}

		return null;
	}

	public static Object castTo(Object object, Type desiredType) {
		if (object == null) {
			return null;
		}

		// System.out.println("Object type: "+object.getClass());
		// System.out.println("desiredType: "+desiredType);
		if (object.getClass().equals(desiredType)) {
			return object;
		}

		if (object instanceof Number) {
			if (TypeUtils.isByte(desiredType)) {
				return ((Number) object).byteValue();
			}
			if (TypeUtils.isShort(desiredType)) {
				return ((Number) object).shortValue();
			}
			if (TypeUtils.isInteger(desiredType)) {
				return ((Number) object).intValue();
			}
			if (TypeUtils.isLong(desiredType)) {
				return ((Number) object).longValue();
			}
			if (TypeUtils.isDouble(desiredType)) {
				return ((Number) object).doubleValue();
			}
			if (TypeUtils.isFloat(desiredType)) {
				return ((Number) object).floatValue();
			}
		}
		return object;
	}

	// TESTS

	public static interface ShouldFail {
		public void test4(short t1, double t2);

		public void test10(Vector t1, List<String> t2);

		public void test14(Vector<String> t1, List<String> t2);
	}

	public static interface ShouldSucceed {
		public void test1(Object t1, Object t2);

		public void test2(int t1, Integer t2);

		public void test3(float t1, int t2);

		public void test11(List t1, Vector<String> t2);

		public void test12(Vector<String> t1, Vector<String> t2);

		public void test13(List<String> t1, Vector<String> t2);
	}

	public static interface TestSuperType {
		public void test20(MyClass2<Integer, Boolean> t1, MyClass1<Boolean> t2);

		public void test21(MyClass2<Integer, List<Boolean>> t1, MyClass1<List<Boolean>> t2);

		public void test22(MyClass3<Integer> t1, MyClass1<List<Integer>> t2);
	}

	private static boolean checkFail(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println("checkFail " + (isTypeAssignableFrom(t1, t2, true) ? "NOK " : "OK  ") + "Method " + m.getName() + " t1: " + t1
				+ " of " + t1.getClass().getSimpleName() + " t2: " + t2 + " of " + t2.getClass().getSimpleName());
		return isTypeAssignableFrom(t1, t2, true);
	}

	private static boolean checkSucceed(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println("checkSucceed " + (isTypeAssignableFrom(t1, t2, true) ? "OK  " : "NOK ") + "Method " + m.getName() + " t1: "
				+ t1 + " of " + t1.getClass().getSimpleName() + " t2: " + t2 + " of " + t2.getClass().getSimpleName());
		return isTypeAssignableFrom(t1, t2, true);
	}

	private static boolean checkSuperType(Method m) {
		Type t1 = m.getGenericParameterTypes()[0];
		Type t2 = m.getGenericParameterTypes()[1];
		System.out.println("checkSuperType " + (getSuperType(t1).equals(t2) ? "OK  " : "NOK ") + "Method " + m.getName() + " type: "
				+ simpleRepresentation(t1) + " super type: " + simpleRepresentation(t2));
		return true;
	}

	public static class MyClass1<A> {

	}

	public static class MyClass2<B, D> extends MyClass1<D> {

	}

	public static class MyClass3<C> extends MyClass1<List<C>> {

	}

	public static void main(String[] args) {
		Class shouldSucceed = ShouldSucceed.class;
		for (Method m : shouldSucceed.getMethods()) {
			checkSucceed(m);
		}
		Class shouldFail = ShouldFail.class;
		for (Method m : shouldFail.getMethods()) {
			checkFail(m);
		}
		Class testSuperType = TestSuperType.class;
		for (Method m : testSuperType.getMethods()) {
			checkSuperType(m);
		}
	}

}
