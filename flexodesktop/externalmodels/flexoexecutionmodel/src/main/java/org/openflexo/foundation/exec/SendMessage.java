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
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.toolbox.ToolBox;


public class SendMessage extends ControlGraphBuilder {

	private ExternalMessageInEdge messageEdge;
	
	public static ControlGraph sendMessage (ExternalMessageInEdge messageEdge,boolean interprocedural)  throws InvalidModelException,NotSupportedException
	{
		return (new SendMessage(messageEdge)).makeControlGraph(interprocedural);
	}

	protected SendMessage(ExternalMessageInEdge messageEdge)
	{
		super();
		this.messageEdge = messageEdge;
	}
	
	@Override
	protected ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException,NotSupportedException
	{
		throw new NotSupportedException("SendMessage not supported yet");
	}


	@Override
	protected String getProcedureName()
	{
		return "sendMessageTo"+ToolBox.getJavaName((getServiceOperation() != null?getServiceOperation().getFullyQualifiedName()+"_"+getServiceOperation().getFlexoID():"Null"));
	}

	public ExternalMessageInEdge getMessageEdge() 
	{
		return messageEdge;
	}

	public FlexoPortMap getPortMap() 
	{
		if (getMessageEdge() != null)
			return getMessageEdge().getEndNode();
		return null;
	}

	public ServiceOperation getServiceOperation() 
	{
		if (getPortMap() != null) 
			return getPortMap().getOperation();
		return null;
	}

}
