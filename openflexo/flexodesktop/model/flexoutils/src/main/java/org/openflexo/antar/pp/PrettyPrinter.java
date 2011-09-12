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
package org.openflexo.antar.pp;

import java.io.PrintStream;

import org.openflexo.antar.AlgorithmicUnit;
import org.openflexo.antar.Class;
import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Flow;
import org.openflexo.antar.Instruction;
import org.openflexo.antar.Loop;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.Sequence;
import org.openflexo.antar.expr.Expression;


public abstract class PrettyPrinter {

	private ExpressionPrettyPrinter expressionPrettyPrinter;
	
	public PrettyPrinter(ExpressionPrettyPrinter expressionPrettyPrinter)
	{
		super();
		this.expressionPrettyPrinter = expressionPrettyPrinter;
	}

   public void print(ControlGraph expression, PrintStream out)
    {
    	out.print(getStringRepresentation(expression));
    }
    
    public String getStringRepresentation(AlgorithmicUnit algorithmicUnit)
    {
       	if (algorithmicUnit == null) {
    		return "null";
    	}
    	if (algorithmicUnit instanceof ControlGraph)
    		return makeStringRepresentation((ControlGraph)algorithmicUnit);
       	if (algorithmicUnit instanceof Procedure)
    		return makeStringRepresentation((Procedure)algorithmicUnit);
       	if (algorithmicUnit instanceof Class)
    		return makeStringRepresentation((Class)algorithmicUnit);
       	return algorithmicUnit.toString();
    }

    public String makeStringRepresentation(ControlGraph statement)
    {
       	if (statement == null) {
    		return "null";
    	}
    	if (statement instanceof Conditional)
    		return makeStringRepresentation((Conditional)statement);
       	if (statement instanceof Instruction)
    		return makeStringRepresentation((Instruction)statement);
       	if (statement instanceof Loop)
    		return makeStringRepresentation((Loop)statement);
       	if (statement instanceof Sequence)
    		return makeStringRepresentation((Sequence)statement);
       	if (statement instanceof Flow)
    		return makeStringRepresentation((Flow)statement);
        	return statement.toString();
    }

    public void print(Procedure procedure, PrintStream out)
    {
    	out.print(makeStringRepresentation(procedure));
    }
    
    public abstract String makeStringRepresentation(Class aClass);

    public abstract String makeStringRepresentation(Procedure procedure);

	public abstract String makeStringRepresentation(Conditional conditional);

	public abstract String makeStringRepresentation(Loop loop);

	public abstract String makeStringRepresentation(Sequence sequence);
	
	public abstract String makeStringRepresentation(Flow sequence);

	public abstract String makeStringRepresentation(Instruction instruction);
	
	public String getStringRepresentation(Expression expression) {
		return expressionPrettyPrinter.getStringRepresentation(expression);
	}


}
