/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;

public interface BindingFactory {

	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent);

	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent);

	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName);

	public Function retrieveFunction(Type parentType, String functionName, List<DataBinding<?>> args);

	public FunctionPathElement makeFunctionPathElement(BindingPathElement father, Function function, List<DataBinding<?>> args);

}