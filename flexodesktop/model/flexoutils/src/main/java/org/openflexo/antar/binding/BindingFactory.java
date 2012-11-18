/**
 * 
 */
package org.openflexo.antar.binding;

import java.util.List;

public interface BindingFactory {

	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent);

	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent);

	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName);

	public FunctionPathElement makeFunctionPathElement(BindingPathElement father, String functionName, List<DataBinding<?>> args);

}