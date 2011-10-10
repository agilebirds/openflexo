/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;

public interface BindingFactory
{

	public void setBindable(Bindable bindable);

	public void setWarnOnFailure(boolean aFlag);

	public AbstractBinding convertFromString(String value);
	
	public String convertToString(AbstractBinding value);

	public BindingValueFactory getBindingValueFactory();

	public void setBindingValueFactory(BindingValueFactory bindingValueFactory);

	public BindingExpressionFactory getBindingExpressionFactory();

	public void setBindingExpressionFactory(BindingExpressionFactory bindingExpressionFactory);
	
	public StaticBindingFactory getStaticBindingFactory() ;

	public void setStaticBindingFactory(StaticBindingFactory staticBindingFactory);

	public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName);

	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father);

	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father);

	public BindingVariable makeBindingVariable(Bindable container, String variableName, Type type);


}