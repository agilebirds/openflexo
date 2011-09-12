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

import org.openflexo.antar.ControlGraph;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;


public class SendTokenOnTokenEdge extends SendToken<FlexoPostCondition<?, ?>> {

	protected SendTokenOnTokenEdge(FlexoPostCondition<?, ?> edge)
	{
		super(edge);
	}

	@Override
	protected ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException 
	{
		AbstractNode end = getEdge().getEndNode();
		
		if (end == null)
			throw new InvalidModelException("No end node defined on edge");
		if (end instanceof FlexoPreCondition) {
			return SendTokenToPrecondition.sendTokenToPrecondition((FlexoPreCondition) end,getEdge(), interprocedural);
		} else if (end instanceof OperatorNode) {
			return OperatorNodeExecution.executeNode((OperatorNode) end,getEdge(),interprocedural);
		} else if (end instanceof EventNode) {
			return EventNodeExecution.executeNode((EventNode) end, interprocedural);
		} else if (end instanceof FlexoNode) {
			return NodeActivation.activateNode((FlexoNode) end, interprocedural);
		} 
		throw new NotSupportedException("Don't know what to do with "+end.getClass().getSimpleName());
	}

}
