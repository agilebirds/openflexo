/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;

import org.openflexo.xmlcode.StringEncoder;

public class DefaultBindingFactory extends StringEncoder.Converter<AbstractBinding> implements BindingFactory
{

	boolean warnOnFailure = true;

	private BindingValueFactory bindingValueFactory;
	private BindingExpressionFactory bindingExpressionFactory;
	private StaticBindingFactory staticBindingFactory;

	public DefaultBindingFactory()
	{
		this(new BindingValueFactory(),
				new BindingExpressionFactory(),
				new StaticBindingFactory());
		bindingValueFactory.setBindingFactory(this);
	}
	
	public DefaultBindingFactory(BindingValueFactory bindingValueFactory,
			BindingExpressionFactory bindingExpressionFactory,
			StaticBindingFactory staticBindingFactory) {
		super(AbstractBinding.class);
		this.bindingValueFactory = bindingValueFactory;
		this.bindingExpressionFactory = bindingExpressionFactory;
		this.staticBindingFactory = staticBindingFactory;
	}

	public void setBindable(Bindable bindable)
	{
		bindingValueFactory.setBindable(bindable);
		bindingExpressionFactory.setBindable(bindable);
		staticBindingFactory.setBindable(bindable);
	}

	public void setWarnOnFailure(boolean aFlag)
	{
		warnOnFailure = aFlag;
			bindingValueFactory.setWarnOnFailure(aFlag);
			bindingExpressionFactory.setWarnOnFailure(aFlag);
			staticBindingFactory.setWarnOnFailure(aFlag);
	}

	@Override
	public AbstractBinding convertFromString(String value) 
	{
		/*boolean debug = (value.startsWith("data.dateBindingDefinition"));
		if (debug) {
			System.out.println("Identified AbstractBinding "+value);
		}*/
		
		if (AbstractBinding.logger.isLoggable(Level.FINE))
			AbstractBinding.logger.fine("Decoding abstract binding: "+value);

		if ("null".equals(value))
			return null;
		StaticBinding decodedStringAsStaticBinding = staticBindingFactory.convertFromString(value);
		if (decodedStringAsStaticBinding != null) {
			if (AbstractBinding.logger.isLoggable(Level.FINE))
				AbstractBinding.logger.fine("Succeeded to decode as a StaticBinding");
			//if (debug) System.out.println("Succeeded to decode as a StaticBinding");
			//if (debug) System.exit(-1);
			return decodedStringAsStaticBinding;
		}
		else {
			// Lets try as a binding value
			bindingValueFactory.setWarnOnFailure(false);
			BindingValue decodedStringAsBindingValue = bindingValueFactory.convertFromString(value);
			bindingValueFactory.setWarnOnFailure(true);
			if (decodedStringAsBindingValue != null) {
				if (AbstractBinding.logger.isLoggable(Level.FINE))
					AbstractBinding.logger.fine("Succeeded to decode as a BindingValue");
				//if (debug) System.out.println("Succeeded to decode as a BindingValue");
				//if (debug) System.exit(-1);
				return decodedStringAsBindingValue;
			}				
			else {
				// Lets try as an expression
				BindingExpression decodedStringAsBindingExpression = bindingExpressionFactory.convertFromString(value);
				if (AbstractBinding.logger.isLoggable(Level.FINE))
					AbstractBinding.logger.fine("Could not decode as a BindingValue, trying as an expression");
				//if (debug) System.out.println("Could not decode as a BindingValue, trying as an expression");
				//if (debug) System.exit(-1);
				return decodedStringAsBindingExpression;
			}
		}
	}

	@Override
	public String convertToString(AbstractBinding value) 
	{
		if (value instanceof BindingValue) return bindingValueFactory.convertToString((BindingValue)value);
		if (value instanceof BindingExpression) return bindingExpressionFactory.convertToString((BindingExpression)value);
		if (value instanceof StaticBinding) return staticBindingFactory.convertToString((StaticBinding)value);
		return "???";
	}

	public BindingValueFactory getBindingValueFactory() 
	{
		return bindingValueFactory;
	}

	public void setBindingValueFactory(BindingValueFactory bindingValueFactory) 
	{
		this.bindingValueFactory = bindingValueFactory;
		bindingValueFactory.setBindingFactory(this);
	}

	public BindingExpressionFactory getBindingExpressionFactory() 
	{
		return bindingExpressionFactory;
	}

	public void setBindingExpressionFactory(
			BindingExpressionFactory bindingExpressionFactory) 
	{
		this.bindingExpressionFactory = bindingExpressionFactory;
	}

	public StaticBindingFactory getStaticBindingFactory() 
	{
		return staticBindingFactory;
	}

	public void setStaticBindingFactory(StaticBindingFactory staticBindingFactory) 
	{
		this.staticBindingFactory = staticBindingFactory;
	}

	public BindingVariable makeBindingVariable(Bindable container, String variableName, Type type)
	{
		return new BindingVariableImpl(container, variableName, type);
	}

	/*public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName)
	{
		if (father.getType() != null) {
			if (TypeUtils.getBaseClass(father.getType()) == null) {
				return null;
			}               
			Type currentType = father.getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getKeyValueProperty(currentType, propertyName);			
		}
		return null;
	}

	public List<? extends BindingPathElement> getAccessibleBindingPathElements(BindingPathElement father)
	{
		if (father.getType() != null) {
			if (TypeUtils.getBaseClass(father.getType()) == null) {
				return null;
			}               
			Type currentType = father.getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getAccessibleProperties(currentType);			
		}
		return null;
		
	}

	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements(BindingPathElement father)
	{
		if (father.getType() != null) {
			if (TypeUtils.getBaseClass(father.getType()) == null) {
				return null;
			}               
			Type currentType = father.getType();
			if (currentType instanceof Class
					&& ((Class)currentType).isPrimitive()) {
				currentType = TypeUtils.fromPrimitive((Class)currentType);
			}
			return KeyValueLibrary.getAccessibleMethods(currentType);			
		}
		return null;
		
	}*/

}