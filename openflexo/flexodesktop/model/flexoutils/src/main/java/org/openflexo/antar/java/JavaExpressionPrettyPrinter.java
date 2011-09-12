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
package org.openflexo.antar.java;

import java.util.Date;

import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.SymbolicConstant;
import org.openflexo.antar.expr.Constant.DateSymbolicConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.FloatSymbolicConstant;
import org.openflexo.toolbox.Duration;


public class JavaExpressionPrettyPrinter extends DefaultExpressionPrettyPrinter {

	public JavaExpressionPrettyPrinter()
	{
		super(new JavaGrammar());
	}

    @Override
	public String getStringRepresentation(Expression expression)
    {
     	if (expression instanceof JavaPrettyPrintable)
    		return ((JavaPrettyPrintable)expression).getJavaStringRepresentation();
     	return super.getStringRepresentation(expression);
    }

    @Override
    protected String makeStringRepresentation(SymbolicConstant constant) 
    {
    	if (constant == FloatSymbolicConstant.E) {
    		return "Math.E";
    	}
    	else if (constant == FloatSymbolicConstant.PI) {
    		return "Math.PI";
    	}
    	else if (constant == DateSymbolicConstant.NOW) {
    		// TODO not implemented
    		return "new Date() /* NOW */";
    	}
    	else if (constant == DateSymbolicConstant.TODAY) {
    		// TODO not implemented
    		return "new Date() /* TODAY */";
    	}
    	return super.makeStringRepresentation(constant);
    }

    @Override
    protected String makeStringRepresentation(DurationConstant constant)
    {
    	return getJavaStringRepresentation(constant.getDuration());
     }
    
    public static String getJavaStringRepresentation(Duration aDuration)
    {
    	return "new Duration("+aDuration.getValue()+",Duration.DurationUnit."+aDuration.getUnit().toString()+")";  	
    }

    public static String getJavaStringRepresentation(Date aDate)
    {
    	// TODO not implemented
    	return "new Date() /* Please implement date representation for "+aDate+" */";  	
    }
}
