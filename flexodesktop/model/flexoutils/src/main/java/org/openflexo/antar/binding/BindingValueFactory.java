/**
 * 
 */
package org.openflexo.antar.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.antar.binding.BindingValue.DecodingPreProcessor;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.xmlcode.StringEncoder;

public class BindingValueFactory extends StringEncoder.Converter<BindingValue>
{
	boolean warnOnFailure = true;

	private Bindable _bindable;

	private DecodingPreProcessor _preProcessor = null;

	private BindingFactory _bindingFactory;

	public BindingValueFactory()
	{
		super(BindingValue.class);
	}

	public BindingValueFactory(BindingFactory bindingStringFactory)
	{
		this();
		setBindingFactory(bindingStringFactory);
	}

	public BindingFactory getBindingFactory() 
	{
		return _bindingFactory;
	}
	
	public void setBindingFactory(BindingFactory bindingStringFactory) 
	{
		_bindingFactory = bindingStringFactory;
	}

	public void setWarnOnFailure(boolean aFlag)
	{
		warnOnFailure = aFlag;
	}
	private MethodCall tryToDecodeAsMethodCall(BindingValue owner, Type currentType, String aValue)
	{
		/*boolean debug = (aValue.indexOf("evaluateSelectableCondition") > -1);
		if (debug) {
			System.out.println("OK, dans tryToDecodeAsMethodCall "+aValue);
			System.out.println("currentType="+currentType);
			System.out.println("owner="+owner);
		}*/
		if (BindingValue.logger.isLoggable(Level.FINE))
			BindingValue.logger.fine("tryToDecodeAsMethodCall " + aValue);

		String callName;
		Vector<String> paramsAsString;

		try {
			ExpressionParser parser = new DefaultExpressionParser();
			Expression parsedExpression = parser.parse(aValue);
			//if (debug) System.out.println("parsedExpression="+parsedExpression);
			if (parsedExpression instanceof Function) {
				callName = ((Function)parsedExpression).getName();
				paramsAsString = new Vector<String>();
				for (Expression e : ((Function)parsedExpression).getArgs()) {
					paramsAsString.add(e.toString());
				}
				//if (debug) System.out.println("methodName="+callName);
			}
			else {
				if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
					BindingValue.logger.warning("Could not decode BindingValue : trying to find method call matching '" + aValue + " this is not a function call");
				return null;
			}
		}
		catch (ParseException e) {
			if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
				BindingValue.logger.warning("Could not decode BindingValue : parse error while trying to find method call matching '" + aValue);
			return null;
		}

		Class typeClass = TypeUtils.getBaseClass(currentType);

		Method[] allMethods = typeClass.getMethods();

		if (BindingValue.logger.isLoggable(Level.FINE))
			BindingValue.logger.fine("allMethods=" + allMethods);
		if (BindingValue.logger.isLoggable(Level.FINE))
			BindingValue.logger.fine("paramsAsString=" + paramsAsString);
		
		String propertyNameWithFirstCharToUpperCase = callName.substring(0, 1).toUpperCase() + callName.substring(1, callName.length());

		String[] tries = new String[4];

		tries[0]="get" + propertyNameWithFirstCharToUpperCase;
		tries[1]=callName;
		tries[2]="_" + callName;
		tries[3]="_get" + propertyNameWithFirstCharToUpperCase;

		Vector<Method> possiblyMatchingMethods = new Vector<Method>();
		for (Method method : allMethods) {
			if (method.getGenericParameterTypes().length == paramsAsString.size()) {
				for (int i=0; i<4; i++) {
					if (method.getName().equals(tries[i])) {
						possiblyMatchingMethods.add(method);
					}
				}
			}
		}
		BindingValue.logger.fine("possiblyMatchingMethods=" + possiblyMatchingMethods);
		//if (debug) System.out.println("possiblyMatchingMethods=" + possiblyMatchingMethods);
		Vector<MethodCall> results = new Vector<MethodCall>();
		for (Method method : possiblyMatchingMethods) {
			boolean successfull = true;
			MethodCall methodCall = new MethodCall(owner, method,currentType);
			int i = 0;
			for (Type genericType : method.getGenericParameterTypes()) {
				Type type = TypeUtils.makeInstantiatedType(genericType, currentType);
				//if (debug) System.out.println("Instead of considering "+genericType+" consider "+type);
				String bindingAsString = paramsAsString.elementAt(i);
				String paramName = "arg"+i;
				i++;
				if (BindingValue.logger.isLoggable(Level.FINE)) BindingValue.logger.fine("Attempt to parse: "+bindingAsString);
				//if (debug) System.out.println("Attempt to parse: "+bindingAsString);
				AbstractBinding paramBindingValue = _bindingFactory.convertFromString(bindingAsString);
				if ((paramBindingValue != null)) {
					paramBindingValue.setOwner(_bindable);
					if (BindingValue.logger.isLoggable(Level.FINE))
						BindingValue.logger.fine("paramBindingValue="+paramBindingValue+" of "+paramBindingValue.getAccessedType());
					//if (debug) System.out.println("paramBindingValue="+paramBindingValue+" of "+paramBindingValue.getAccessedType());
					if (paramBindingValue.isStaticValue()) {
						paramBindingValue.setBindingDefinition(methodCall.new MethodCallParamBindingDefinition(paramName,type));
					}
					if (type != null 
							&& paramBindingValue.getAccessedType() != null 
							&& TypeUtils.isTypeAssignableFrom(type, paramBindingValue.getAccessedType(), true)) {
						if (BindingValue.logger.isLoggable(Level.FINE))
							BindingValue.logger.fine("Lookup on " + type + " succeded: " + paramBindingValue.getStringRepresentation());
						//if (debug) System.out.println("Lookup on " + type + " succeded: " + paramBindingValue.getStringRepresentation());
						methodCall.setBindingValueForParam(paramBindingValue,paramName);
					} else {
						if (BindingValue.logger.isLoggable(Level.FINE))
							BindingValue.logger.fine("Lookup on type " + type + " failed (wrong type): "
									+ paramBindingValue.getStringRepresentation() + "types: "
									+"looking "+type+" found "+paramBindingValue.getAccessedType());
						/*if (debug) System.out.println("Lookup on type " + type + " failed (wrong type): "
									+ paramBindingValue.getStringRepresentation() + "types: "
									+"looking "+type+" found "+paramBindingValue.getAccessedType());*/
						successfull = false;
					}
				} else {
					if (BindingValue.logger.isLoggable(Level.FINE))
						BindingValue.logger.fine("Lookup on " + type + " failed (cannot analysing): " + bindingAsString);
					//if (debug) System.out.println("Lookup on " + type + " failed (cannot analysing): " + bindingAsString);
					successfull = false;
				}
			}
			if (successfull) {
				results.add(methodCall);
			}
		}
		if (results.size() == 1) {
			return results.firstElement();
		} else if (results.size() > 1) {
			if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
				BindingValue.logger.warning(("While decoding BindingValue '" + aValue + "' : found ambigous methods " + callName));
			return results.firstElement();
		}
		if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
			BindingValue.logger.warning("Could not decode BindingValue : cannot find method call matching '" + aValue);
		return null;
	}

	protected class PathTokenizer
	{
		private final Vector<String> _tokens;

		private final Enumeration<String> enumeration;

		protected PathTokenizer(String value)
		{
			super();
			_tokens = new Vector<String>();
			StringTokenizer st = new StringTokenizer(value, ".()", true);
			String current = "";
			int level = 0;
			while (st.hasMoreElements()) {
				String next = st.nextToken();
				if ((next.equals(".")) && (current.trim().length() > 0) && (level == 0)) {
					_tokens.add(current);
					current = "";
				} else if (next.equals("(")) {
					current += next;
					level++;
				} else if (next.equals(")")) {
					current += next;
					level--;
				} else {
					current += next;
				}
			}
			if ((current.trim().length() > 0) && (level == 0)) {
				_tokens.add(current);
				current = "";
			}
			enumeration = _tokens.elements();
		}

		public boolean hasMoreTokens()
		{
			return enumeration.hasMoreElements();
		}

		public String nextToken()
		{
			String returned = enumeration.nextElement();
			if (BindingValue.logger.isLoggable(Level.FINE))
				BindingValue.logger.fine("> " + returned);
			return returned;
		}
	}

	@Override
	public BindingValue convertFromString(String aValue)
	{

		//boolean debug = (aValue.startsWith("data.dateBindingDefinition"));
		/*if (debug) {
			System.out.println("OK, decoding BindingValue "+aValue);
			System.out.println("_bindable="+_bindable);
			System.out.println("binding model ="+_bindable.getBindingModel());
		}*/
		if (BindingValue.logger.isLoggable(Level.FINE))
			BindingValue.logger.fine("Decode " + aValue);

		if (_bindable == null) {
			if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
				BindingValue.logger.warning(("Could not decode BindingValue '" + aValue + "' : bindable not set !"));
			return null;
		} else {
			String value;
			if (_preProcessor != null) {
				value = _preProcessor.preProcessString(aValue);
			} else {
				value = aValue;
			}
			if ("null".equals(value))
				return null;

			BindingValue returned = new BindingValue();

			PathTokenizer st = new PathTokenizer(value);
			if (st.hasMoreTokens()) {
				String bindingVariableName = st.nextToken();
				if (_bindable == null) {
					if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
						BindingValue.logger.warning(("Could not decode BindingValue '" + value + "' : no declared bindable !"));
					return null;
				}
				if (_bindable.getBindingModel() == null) {
					if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
						BindingValue.logger
						.warning(("Could not decode BindingValue '" + value + "' : declared bindable has a null binding model !"));
					return null;
				}
				BindingVariable bv = _bindable.getBindingModel().bindingVariableNamed(bindingVariableName);
				if (bv == null) {
					if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
						BindingValue.logger
						.warning(("Could not decode BindingValue '" + value + "' : variable " + bindingVariableName + " not found in binding model !"));
					if (BindingValue.logger.isLoggable(Level.FINE))
						BindingValue.logger.fine("NOT Found binding variable "+bv);
					return null;
				} else {
					if (BindingValue.logger.isLoggable(Level.FINE))
						BindingValue.logger.fine("Found binding variable "+bv);
					BindingPathElement currentElement = bv;
					Type currentType = bv.getType();
					if (currentType == null) {
						if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
							BindingValue.logger
							.warning(("Could not decode BindingValue '" + value + "' : variable " + bindingVariableName + " doesn't implement any type !"));
						return null;
					}
					returned.setBindingVariable(bv);
					while (st.hasMoreTokens()) {
						String nextTokenName = st.nextToken();
						if (BindingValue.logger.isLoggable(Level.FINE))
							BindingValue.logger.fine("nextTokenName="+nextTokenName+" currentType="+currentType);
						//if (debug) System.out.println("nextTokenName="+nextTokenName+" currentType="+currentType);
						BindingPathElement nextElement;
						if (TypeUtils.getBaseClass(currentType) == null) {
							if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
								BindingValue.logger.warning(("Could not decode BindingValue '" + value
										+ "' : cannot find base entity for type '" + currentType));
							return null;
						}                           
						//nextElement = KeyValueLibrary.getKeyValueProperty(currentType, nextTokenName);
						//nextElement = _bindingFactory.getBindingPathElement(currentElement, nextTokenName);
						nextElement = currentElement.getBindingPathElement(nextTokenName);
						/*if (currentType instanceof Class
								&& ((Class)currentType).isPrimitive()) {
							currentType = TypeUtils.fromPrimitive((Class)currentType);
							//nextElement = KeyValueLibrary.getKeyValueProperty(currentType, nextTokenName);
							nextElement = _bindingFactory.getBindingPathElement(currentElement, nextTokenName);
						}*/
						if (nextElement == null) {
							// OK, may be this is a MethodCall
							//if (debug) System.out.println("is it a method call ? "+nextTokenName);
							nextElement = tryToDecodeAsMethodCall(returned, currentType, nextTokenName);
						}
						if (nextElement == null) {
							if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
								BindingValue.logger.warning(("Could not decode BindingValue '" + value
										+ "' : cannot find property nor method matching '" + nextTokenName + "' for type "
										+ currentType + " !"));
							return null;
						} else {
							currentType = returned.addBindingPathElement(nextElement);
							//returned.addBindingPathElement(nextElement);
							//currentType = nextElement.getType();
							if (currentType == null) {
								if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
									BindingValue.logger
									.warning(("Could not decode BindingValue '" + value + "' : token " + nextTokenName + " doesn't implement any type !"));
								return null;
							}
						}
						currentElement = nextElement;
					}
					// Before to receive its owner, we set to knonwn bindable, in order to check validity
					returned.setOwner(_bindable);
					if (returned.isBindingValidWithoutBindingDefinition()) {
						returned._isConnected = true;
						return returned;
					} else {
						if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
							BindingValue.logger.warning(("Could not decode BindingValue '" + value + "' : invalid binding !"));
						return null;
					}
				}
			} else {
				if (BindingValue.logger.isLoggable(Level.WARNING) && warnOnFailure)
					BindingValue.logger.warning(("Could not decode BindingValue '" + value + "' : variable not set !"));
				return null;
			}
		}
	}

	@Override
	public String convertToString(BindingValue value)
	{
		return value.getStringRepresentation();
	}

	public Bindable getBindable()
	{
		return _bindable;
	}

	public void setBindable(Bindable bindable)
	{
		_bindable = bindable;
	}

	public DecodingPreProcessor getPreProcessor()
	{
		return _preProcessor;
	}

	public void setPreProcessor(DecodingPreProcessor preProcessor)
	{
		_preProcessor = preProcessor;
	}
	
}