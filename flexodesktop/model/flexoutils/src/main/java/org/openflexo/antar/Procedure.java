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
package org.openflexo.antar;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.expr.Variable;
import org.openflexo.logging.FlexoLogger;

public class Procedure implements AlgorithmicUnit {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(Procedure.class.getPackage().getName());

	private String procedureName;
	private Vector<ProcedureParameter> parameters;
	private ControlGraph controlGraph;
	private String comment;
	private String returnType;
	
	public Procedure(String procedureName,ControlGraph controlGraph) 
	{
		super();
		this.procedureName = procedureName;
		this.controlGraph = controlGraph;
		this.parameters = new Vector<ProcedureParameter>();
	}

	public Procedure(String procedureName,ControlGraph controlGraph,ProcedureParameter... parameters) 
	{
		this(procedureName,controlGraph);
		for (ProcedureParameter p : parameters) addParameter(p);
	}

	public Procedure(String procedureName,ControlGraph controlGraph,String comment, ProcedureParameter... parameters) 
	{
		this(procedureName,controlGraph,parameters);
		setComment(comment);
	}

	public Procedure(String procedureName,Vector<ProcedureParameter> parameters,ControlGraph controlGraph) 
	{
		this(procedureName,controlGraph);
		for (ProcedureParameter p : parameters) addParameter(p);
	}

	public Procedure(String procedureName,Vector<ProcedureParameter> parameters,ControlGraph controlGraph,String comment) 
	{
		this(procedureName,parameters,controlGraph);
		setComment(comment);
	}

	public ControlGraph getControlGraph()
	{
		return controlGraph;
	}
	
	public void setControlGraph(ControlGraph controlGraph)
	{
		this.controlGraph = controlGraph;
	}
	
	public Vector<ProcedureParameter> getParameters() 
	{
		return parameters;
	}
	
	public void setParameters(Vector<ProcedureParameter> parameters) 
	{
		this.parameters = parameters;
	}
	
	public void addParameter(ProcedureParameter parameter)
	{
		parameters.add(parameter);
	}
	
	public void removeParameter(ProcedureParameter parameter)
	{
		parameters.remove(parameter);
	}
	
	public String getProcedureName()
	{
		return procedureName;
	}
	
	public void setProcedureName(String procedureName)
	{
		this.procedureName = procedureName;
	}

	public String getComment() 
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getReturnType() 
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
	

	public static class ProcedureParameter
	{
		private Variable variable;
		private Type type;

		public ProcedureParameter(Variable variable, Type type) 
		{
			super();
			this.variable = variable;
			this.type = type;
		}

		public Type getType() 
		{
			return type;
		}
		
		public void setType(Type type) 
		{
			this.type = type;
		}
		
		public Variable getVariable() 
		{
			return variable;
		}
		
		public void setVariable(Variable variable) 
		{
			this.variable = variable;
		}
	}

}
