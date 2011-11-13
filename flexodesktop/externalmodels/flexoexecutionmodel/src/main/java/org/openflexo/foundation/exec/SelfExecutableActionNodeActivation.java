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
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;

public class SelfExecutableActionNodeActivation extends NodeActivation<SelfExecutableActionNode> {

	public SelfExecutableActionNodeActivation(SelfExecutableActionNode node, FlexoPreCondition pre) {
		super(node, pre);
	}

	public SelfExecutableActionNodeActivation(SelfExecutableActionNode node) {
		super(node);
	}

	@Override
	public ControlGraph makeSpecificControlGraph(boolean interprocedural) throws NotSupportedException, InvalidModelException {
		ControlGraph EXECUTE_PRIMITIVE = null;
		ControlGraph EXECUTE_ASSIGNMENTS = null;

		if (getNode().getExecutionPrimitive() != null) {
			EXECUTE_PRIMITIVE = makeControlGraphForExecutionPrimitive(getNode().getExecutionPrimitive());
			EXECUTE_PRIMITIVE.setHeaderComment("Call execution primitive for node " + getNode().getName());
		}

		if (getNode().getAssignments().size() > 0) {
			Vector<ControlGraph> allAssignments = new Vector<ControlGraph>();
			for (BindingAssignment assignment : getNode().getAssignments())
				allAssignments.add(makeControlGraphForAssignment(assignment));
			EXECUTE_ASSIGNMENTS = makeSequentialControlGraph(allAssignments);
			EXECUTE_ASSIGNMENTS.setHeaderComment("Perform assignments declared for execution of node " + getNode().getName());
		}

		return makeSequentialControlGraph(EXECUTE_PRIMITIVE, EXECUTE_ASSIGNMENTS);
	}

	@Override
	protected ControlGraph makeControlGraphCommonPostlude(boolean interprocedural) throws NotSupportedException, InvalidModelException {
		return makeSequentialControlGraph(super.makeControlGraphCommonPostlude(interprocedural),
				NodeDesactivation.desactivateNode(getNode(), interprocedural));
	}

}
