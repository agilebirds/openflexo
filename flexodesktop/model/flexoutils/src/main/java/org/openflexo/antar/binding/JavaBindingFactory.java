/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class JavaBindingFactory implements BindingFactory {
	static final Logger logger = Logger.getLogger(JavaBindingFactory.class.getPackage().getName());

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		if (parent instanceof FinalBindingPathElement) {
			return Collections.emptyList();
		}
		if (parent.getType() != null) {
			if (TypeUtils.getBaseClass(parent.getType()) == null) {
				return null;
			}
			Type currentType = parent.getType();
			if (currentType instanceof Class && ((Class) currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class) currentType);
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
		if (parent instanceof FinalBindingPathElement) {
			return Collections.emptyList();
		}
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

}