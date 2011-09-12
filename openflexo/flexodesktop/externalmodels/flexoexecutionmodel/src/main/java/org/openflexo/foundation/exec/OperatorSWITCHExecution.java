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

import java.util.logging.Logger;


import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Nop;
import org.openflexo.foundation.wkf.node.SWITCHOperator;
import org.openflexo.logging.FlexoLogger;

public class OperatorSWITCHExecution extends OperatorNodeExecution {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(OperatorSWITCHExecution.class.getPackage().getName());

	protected OperatorSWITCHExecution(SWITCHOperator operatorNode)
	{
		super(operatorNode);
	}
	

	@Override
	protected final ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException,NotSupportedException
	{
		Nop returned = new Nop();
		returned.setInlineComment("Manage SWITCH");
		return returned;
	}

	@Override
	public SWITCHOperator getOperatorNode() 
	{
		return (SWITCHOperator)super.getOperatorNode();
	}


}
