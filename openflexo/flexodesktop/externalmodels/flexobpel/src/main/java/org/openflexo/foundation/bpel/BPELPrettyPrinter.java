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
package org.openflexo.foundation.bpel;

import java.util.Vector;

import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.OperatorNotSupportedException;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingExpression.BindingValueVariable;
import org.openflexo.foundation.bindings.BindingValue.BindingPath;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.dm.WSDLRepository;


public class BPELPrettyPrinter extends DefaultExpressionPrettyPrinter {

	private static BPELPrettyPrinter printer;
	public static BPELPrettyPrinter getInstance(BPELWriter writer) {
		if (printer==null) {
			printer=new BPELPrettyPrinter(writer);
		}
		return printer;
	}
	public static BPELPrettyPrinter getInstance() {
		// this will return null if it has not been initialised
		return printer;
	}
	
	
	
	private BPELWriter writer;
	
	public BPELPrettyPrinter(BPELWriter w) {
		super(new BPELGrammar());
		writer=w;
	}

	 @Override
	public String getStringRepresentation(Expression expression)
	    {
	    	if (expression == null) {
	    		return "null";
	    	}
	    	if (expression instanceof Variable) {
	       		return makeStringRepresentation((Variable)expression);
	    	}
	       	if (expression instanceof Constant)
	    		return makeStringRepresentation((Constant)expression);
	       	if (expression instanceof Function)
	    		return makeStringRepresentation((Function)expression);
	       	if (expression instanceof UnaryOperatorExpression)
	    		return makeStringRepresentation((UnaryOperatorExpression)expression);
	       	if (expression instanceof BinaryOperatorExpression)
	    		return makeStringRepresentation((BinaryOperatorExpression)expression);
	       	if (expression instanceof BindingValueVariable)
	       		return makeStringRepresentation(((BindingValueVariable)expression));
	      
	       	
	       	//return "<unknown "+expression.getClass().getSimpleName()+">";
	       	return expression.toString();
	    }

	@Override
	protected String makeStringRepresentation (BinaryOperatorExpression expression){
		
		 try {
			 if (expression.getLeftArgument().toString().equals("BINDING_EXPRESSION")) {
				 return getStringRepresentation(expression.getRightArgument());
			 }
			 else 
			 return "("
			 +getStringRepresentation(expression.getLeftArgument())
			 +" "
			 +getSymbol(expression.getOperator())
			 +" "
			 +getStringRepresentation(expression.getRightArgument())
			 +")";
		 } catch (OperatorNotSupportedException e) {
			 return "<unsupported>";
		 }
	 }
	 
	
	/*
	 * Takes as input a FlexoVariable and returned the corresponding BPEL, whith qualified elements
	 */
	public String makeStringRepresentation(BindingValue var) {
		if (var==null) return null;
		System.out.println(" * * * * * * * Looking in variables for : "+var.getStringRepresentation());
	   		
		String[] returned=writer.getBPELMessagePartFromFlexoVariable(var.getStringRepresentation());
   		
   		String toReturn=new String();
  
		int numberToRemove=0;
   		if (returned!=null && returned[0]!=null && returned[1]!=null) {
   			String replaced=returned[1];
   			System.out.println("Replaced : "+replaced);
   		
   			// from replaced, we check how many elements must be removed from the path
   			String[] replacedEl=replaced.split("\\.");
   			numberToRemove+=replacedEl.length-1;
   		}	
   		
   		
		BindingPath path=var.getBindingPath();
   		Vector<BindingPathElement> vect=new Vector<BindingPathElement>();
   		System.out.println("Number to remove : "+numberToRemove);
   		System.out.println("Path size : "+path.size());
   		for (int i=numberToRemove;i<path.size();i++) {
   			vect.add(path.get(i));
   			System.out.println("Adding to path : "+path.get(i).getSerializationRepresentation());
   		}
   			
		String stringPath=new String();
		
   		// for every element in the path, we need to declare its namespace.
   		// except for the processInstance/businessData
   		for (int i=0;i<vect.size();i++) {
   			if (i!=0)stringPath+=".";
   			BindingPathElement currentEl=vect.get(i);
   			String pack=currentEl.getEntity().getPackage().getName();
   			String ns=null;
   			if ((currentEl.getEntity().getRepository() instanceof WSDLRepository)) {
   				WSDLRepository rep=(WSDLRepository) currentEl.getEntity().getRepository();
   			
   				System.out.println("registered mappings : "+rep.getPackageToNamespace());
  
   				ns=(rep.getPackageToNamespace().get(pack));
   			}
   			
   			// this is wrong, but we should add a namespace declaration for the exported package...
   			else {
   				ns=pack;
   			}

   			System.out.println("NS for :"+ currentEl.getSerializationRepresentation()+" ("+ pack +")resolved to : "+ns);
   			
   			if (ns!=null) {
   				BPELNamespacePrefixMapperFactory.addNamespaceAndPrefix(ns, null);
   				String prefix=BPELNamespacePrefixMapperFactory.getInstance().getPreferredPrefix(ns, null, false);
   				stringPath+=prefix+":";
   			}
   			stringPath+=currentEl.getSerializationRepresentation();
   		}
   		if (returned!=null) {
   			toReturn=returned[0]+"/";
   		}
   		toReturn+=stringPath.replaceAll("\\.", "/");
   		
   		System.out.println("returning :"+toReturn);
   		return toReturn;
   		
	}
	
	
	protected String makeStringRepresentation(BindingValueVariable v) {
		BindingValue var=v.getBindingValue();
   		return makeStringRepresentation(var);
		
	}
	
	@Override
	protected String makeStringRepresentation(Variable variable) {
		return variable.getName();
		//return writer.getBPELMessagePartFromFlexoVariable(variable.getName())[0];
	}
	
	@Override
 	protected String makeStringRepresentation(BooleanConstant constant) {
		if (constant == BooleanConstant.FALSE) return "false()";
		else if (constant == BooleanConstant.TRUE) return "true()";
		return "???";
	}
}
