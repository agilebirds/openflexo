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
package org.openflexo.foundation.rm;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;

/**
 * 
 */

public class FlexoResourceTreeImplementation implements FlexoResourceTree
{
	private FlexoResource _root;
	private Vector<FlexoResourceTree> _childs;
	
	public FlexoResourceTreeImplementation(FlexoResource resource)
	{
		_root = resource;
		_childs = new Vector<FlexoResourceTree>();
	}

	@Override
	public Vector<FlexoResourceTree> getChildNodes() 
	{
		return _childs;
	}

	public void add(FlexoResourceTree tree)
	{
		_childs.add(tree);
	}
	
	@Override
	public FlexoResource getRootNode() 
	{
		return _root;
	}

	@Override
	public boolean isEmpty() {
		return _childs.size() == 0;
	}
	
	@Override
	public String toString()
	{
		return toString(0, false);
	}

	@Override
	public String toString(int offset, boolean isLastChild) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0;i<offset;i++){
			if (i+1==offset)
				sb.append("|---");
			else
				if(isLastChild)
					sb.append("    ");
				else
					sb.append("|   ");
		}
		sb.append(_root).append("(").append(new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(_root.getLastUpdate())).append(")\n");
		Iterator<FlexoResourceTree> i = _childs.iterator();
		while (i.hasNext()) {
			FlexoResourceTree child = i.next();
			sb.append(child.toString(offset+1,!i.hasNext()));
		}
		return sb.toString();
	}
}