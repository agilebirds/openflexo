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
package org.openflexo.foundation.exec;

import java.util.Vector;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Flow;
import org.openflexo.antar.Nop;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.Sequence;
import org.openflexo.antar.expr.Expression;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.exec.expr.ConditionPrimitiveExpression;
import org.openflexo.foundation.exec.inst.CodeCall;
import org.openflexo.foundation.exec.inst.FlexoAssignment;

public abstract class ControlGraphBuilder {

	protected abstract ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException;

	protected abstract String getProcedureName();

	/**
	 * Override this when required
	 * 
	 * @return
	 */
	protected String getProcedureComment() {
		return null;
	}

	protected Procedure makeProcedure() throws InvalidModelException, NotSupportedException {
		return new Procedure(getProcedureName(), makeControlGraph(true), getProcedureComment());
	}

	protected ControlGraph makeControlGraphForExecutionPrimitive(BindingValue executionPrimitive) {
		return new CodeCall(executionPrimitive);
	}

	protected ControlGraph makeControlGraphForAssignment(BindingAssignment assignment) {
		return new FlexoAssignment(assignment);
	}

	protected Expression makeCondition(AbstractBinding conditionPrimitive) {
		return new ConditionPrimitiveExpression(conditionPrimitive);
	}

	protected ControlGraph makeSequentialControlGraph(ControlGraph... statements) {
		Vector<ControlGraph> listOfStatements = new Vector<ControlGraph>();
		for (ControlGraph statement : statements) {
			if (statement != null && !(statement instanceof Nop && !statement.hasComment()))
				listOfStatements.add(statement);
		}
		return makeSequentialControlGraph(listOfStatements);
	}

	protected ControlGraph makeSequentialControlGraph(Vector<ControlGraph> listOfStatements) {
		if (listOfStatements == null || listOfStatements.size() == 0)
			return new Nop();

		if (listOfStatements.size() == 1) {
			return listOfStatements.firstElement();
		}

		else {
			Sequence returned = new Sequence();
			for (ControlGraph statement : listOfStatements)
				returned.addToStatements(statement);
			returned.normalize();
			return returned.normalize();
		}
	}

	protected ControlGraph makeFlowControlGraph(Vector<ControlGraph> listOfStatements) {
		if (listOfStatements == null || listOfStatements.size() == 0)
			return new Nop();

		if (listOfStatements.size() == 1) {
			return listOfStatements.firstElement();
		}

		else {
			Flow returned = new Flow();
			for (ControlGraph statement : listOfStatements)
				returned.addToStatements(statement);
			returned.normalize();
			return returned.normalize();
		}
	}

}
