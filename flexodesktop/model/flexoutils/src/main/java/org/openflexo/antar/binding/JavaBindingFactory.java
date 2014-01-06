/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

public class JavaBindingFactory implements BindingFactory {
	static final Logger logger = Logger.getLogger(JavaBindingFactory.class.getPackage().getName());

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		if (parent.getType() != null) {
			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class) currentType);
			}
			if (currentType instanceof WildcardType) {
				Type[] upperBounds = ((WildcardType) currentType).getUpperBounds();
				if (upperBounds.length == 1) {
					currentType = upperBounds[0];
				}
			}
			List<JavaPropertyPathElement> returned = new ArrayList<JavaPropertyPathElement>();
			for (KeyValueProperty p : KeyValueLibrary.getAccessibleProperties(currentType)) {
				returned.add(new JavaPropertyPathElement(parent, p));
			}
			return returned;
		}
		return null;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		if (parent.getType() != null) {
			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class) currentType);
			}
			List<JavaMethodPathElement> returned = new ArrayList<JavaMethodPathElement>();
			for (MethodDefinition m : KeyValueLibrary.getAccessibleMethods(currentType)) {
				returned.add(new JavaMethodPathElement(parent, m, null));
			}
			return returned;
		}
		return null;
	}

	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName) {
		Type fatherType = father.getType();
		if (fatherType instanceof Class && ((Class) fatherType).isPrimitive()) {
			fatherType = TypeUtils.fromPrimitive((Class) fatherType);
		}
		KeyValueProperty keyValueProperty = KeyValueLibrary.getKeyValueProperty(fatherType, propertyName);
		if (keyValueProperty != null) {
			return new JavaPropertyPathElement(father, keyValueProperty);
		}
		return null;
	}

	@Override
	public FunctionPathElement makeFunctionPathElement(BindingPathElement father, Function function, List<DataBinding<?>> args) {
		if (function instanceof MethodDefinition) {
			return new JavaMethodPathElement(father, (MethodDefinition) function, args);
		}
		return null;
	}

	@Override
	public Function retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args) {
		Vector<Method> possiblyMatchingMethods = new Vector<Method>();
		Class<?> typeClass = TypeUtils.getBaseClass(parentType);
		if (typeClass == null) {
			System.out.println("Cannot find typeClass for " + parentType);
		}
		Method[] allMethods = typeClass.getMethods();
		// First attempt: we perform type checking on parameters
		for (Method method : allMethods) {
			if (method.getName().equals(functionName) && method.getGenericParameterTypes().length == args.size()) {
				boolean lookupFails = false;
				for (int i = 0; i < args.size(); i++) {
					DataBinding<?> suppliedArg = args.get(i);
					Type argType = method.getGenericParameterTypes()[i];
					if (!TypeUtils.isTypeAssignableFrom(argType, suppliedArg.getDeclaredType())) {
						lookupFails = true;
					}
				}
				if (!lookupFails) {
					possiblyMatchingMethods.add(method);
				}
			}
		}
		// Second attempt: we don't check the types of parameters
		if (possiblyMatchingMethods.size() == 0) {
			for (Method method : allMethods) {
				if (method.getName().equals(functionName) && method.getGenericParameterTypes().length == args.size()) {
					possiblyMatchingMethods.add(method);
				}
			}
		}
		if (possiblyMatchingMethods.size() > 1) {
			logger.warning("Please implement disambiguity here");
			/*for (DataBinding<?> arg : args) {
				System.out.println("arg " + arg + " of " + arg.getDeclaredType() + " / " + arg.getAnalyzedType());
			}*/
			// Return the first one
			// TODO: try to find the best one
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		} else if (possiblyMatchingMethods.size() == 1) {
			return MethodDefinition.getMethodDefinition(parentType, possiblyMatchingMethods.get(0));
		} else {
			logger.warning("Cannot find method named " + functionName + " with args=" + args + "(" + args.size() + ") for type "
					+ parentType);
			// Thread.dumpStack();
			return null;
		}
	}

}