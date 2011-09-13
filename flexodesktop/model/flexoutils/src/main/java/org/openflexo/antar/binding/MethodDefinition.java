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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.toolbox.ToolBox;

public class MethodDefinition extends KeyValueBindingImpl implements ComplexPathElement {

	private static final Logger logger = Logger.getLogger(MethodDefinition.class.getPackage().getName());

	private Type declaringType;
	private Method method;
	private static Hashtable<Method,MethodDefinition> cache = new Hashtable<Method, MethodDefinition>();

	public static MethodDefinition getMethodDefinition(Type aDeclaringType, Method method)
	{
		MethodDefinition returned = cache.get(method);
		if (returned == null) {
			returned = new MethodDefinition(aDeclaringType,method);
			cache.put(method,returned);
		}
		return returned;
	}

	private MethodDefinition(Type aDeclaringType, Method method)
	{
		super();
		this.method = method;
		this.declaringType = aDeclaringType;
	}

	public Method getMethod() 
	{
		return method;
	}

	@Override
	public Type getType() 
	{
		return TypeUtils.makeInstantiatedType(getMethod().getGenericReturnType(),declaringType);
	}

	private String _signatureNFQ;
	private String _signatureFQ;
	private String _parameterListAsStringFQ;
	private String _parameterListAsString;

	public String getSimplifiedSignature()
	{
		if (_signatureNFQ == null) {
			StringBuffer signature = new StringBuffer();
			signature.append(method.getName());
			signature.append("(");
			signature.append(getParameterListAsString(false));
			signature.append(")");
			_signatureNFQ = signature.toString();
		}
		return _signatureNFQ;
	}

	public String getSignature()
	{
		if (_signatureFQ == null) {
			//try {
				StringBuffer signature = new StringBuffer();
				signature.append(method.getName());
				signature.append("(");
				signature.append(getParameterListAsString(true));
				signature.append(")");
				_signatureFQ = signature.toString();
			/*}
			catch (InvalidKeyValuePropertyException e) {
				logger.warning("While computing getSignature() for "+method+" and "+declaringType+" message:"+e.getMessage());
				e.printStackTrace();
				return null;
			}*/

		}
		return _signatureFQ;
	}

	/*public String getSimplifiedSignatureInContext(Type context)
	{
		StringBuffer signature = new StringBuffer();
		signature.append(method.getName());
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, false));
		signature.append(")");
		return signature.toString();
	}

	public String getSignatureInContext(Type context)
	{
		StringBuffer signature = new StringBuffer();
		signature.append(method.getName());
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, true));
		signature.append(")");
		return signature.toString();
	}*/


	private String getParameterListAsString(boolean fullyQualified)
	{
		String _searched = (fullyQualified?_parameterListAsStringFQ:_parameterListAsString);
		if (_searched == null) {
			StringBuffer returned = new StringBuffer();
			boolean isFirst = true;
			for (Type p : method.getGenericParameterTypes()) {
				Type contextualParamType = TypeUtils.makeInstantiatedType(p,declaringType);
				returned.append((isFirst?"":",")
						+(fullyQualified?TypeUtils.fullQualifiedRepresentation(contextualParamType):TypeUtils.simpleRepresentation(contextualParamType)));
				isFirst = false;          	
			}
			if (fullyQualified) {
				_parameterListAsStringFQ =returned.toString();
			}
			else {
				_parameterListAsString =returned.toString();
			}
		}
		return (fullyQualified?_parameterListAsStringFQ:_parameterListAsString);
	}

	// Warning: no cache for this method
	/*String getParameterListAsStringInContext(Type context, boolean fullyQualified)
	{
		StringBuffer returned = new StringBuffer();
		boolean isFirst = true;
		for (Type p : method.getGenericParameterTypes()) {
			Type typeInContext = TypeUtils.makeInstantiatedType(p, context);
			returned.append((isFirst?"":",")+(fullyQualified?TypeUtils.fullQualifiedRepresentation(typeInContext):TypeUtils.simpleRepresentation(typeInContext)));
			isFirst = false;          	
		}
		return returned.toString();
	}*/

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MethodDefinition) {
			//System.out.println("Compare "+getMethod()+" and "+((MethodDefinition)obj).getMethod());
			return getMethod().equals(((MethodDefinition)obj).getMethod());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return getMethod().hashCode();
	}
	
	@Override
	public String toString()
	{
		return "MethodDefinition["+getSimplifiedSignature()+"]";
	}

	@Override
	public Class getDeclaringClass()
	{
		return TypeUtils.getBaseClass(declaringType);
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return toString();
	}

	@Override
	public boolean isBindingValid() 
	{
			return true;
	}

    public String getLabel()
    {
    	return getSimplifiedSignature();
    }
    
	public String getTooltipText(Type resultingType)
	{
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType!=null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>"+resultingTypeAsString+" "+getSimplifiedSignature()+"</b></p>";
		//returned += "<p><i>"+(method.getDescription()!=null?method.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

    @Override
    public boolean isSettable()
    {
    	return false;
    }
 
    @Override
    public Object evaluateBinding(Object target, BindingEvaluationContext context)
    {
     	return null;
    }
}
