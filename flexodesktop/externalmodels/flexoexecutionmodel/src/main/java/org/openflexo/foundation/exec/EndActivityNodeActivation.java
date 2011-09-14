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
import org.openflexo.foundation.exec.inst.DeleteProcessInstance;
import org.openflexo.foundation.exec.inst.DestroyRemainingTokensForProcessInstance;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;


public class EndActivityNodeActivation extends NodeActivation<ActivityNode> {

	public EndActivityNodeActivation(ActivityNode node, FlexoPreCondition pre)
	{
		super(node,pre);
	}
	
	public EndActivityNodeActivation(ActivityNode node)
	{
		super(node);
	}
	
	@Override
	public ControlGraph makeSpecificControlGraph(boolean interprocedural) throws NotSupportedException, InvalidModelException 
	{
		ControlGraph DESTROY_REMAINING_TOKEN = new DestroyRemainingTokensForProcessInstance();
		ControlGraph DELETE_PROCESS_INSTANCE = new DeleteProcessInstance();
		
		return makeSequentialControlGraph(
				DESTROY_REMAINING_TOKEN,
				DELETE_PROCESS_INSTANCE);
	}
	
	/**
	 * Override parent method: we don't try here to activate a node above
	 */
	@Override
	protected ControlGraph makeControlGraphCommonPostlude(boolean interprocedural) throws NotSupportedException, InvalidModelException
	{
		return NodeDesactivation.desactivateNode(getNode(),interprocedural);
	}

}
