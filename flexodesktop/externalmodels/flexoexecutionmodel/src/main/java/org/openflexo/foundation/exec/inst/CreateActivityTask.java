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
package org.openflexo.foundation.exec.inst;

import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public class CreateActivityTask extends CustomInstruction {

	private AbstractActivityNode activity;
	
	public CreateActivityTask (AbstractActivityNode activity)
	{
		super();
		this.activity = activity;
		setInlineComment("Create new ActivityTask for node "+(getActivity()!=null?getActivity().getName():null));
	}

	public AbstractActivityNode getActivity() {
		return activity;
	}

	@Override
	public String toString()
	{
		return "[CreateActivityTask:"+activity+"]";
	}

	@Override
	public String getJavaStringRepresentation() 
	{
		return "createActivityTask("+getActivity().getFlexoID()+");";
	}
	
	@Override
	public CreateActivityTask clone()
	{
		CreateActivityTask returned = new CreateActivityTask(activity);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}


}
