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
package org.openflexo.xml.diff3.mergerule;

import org.openflexo.xml.diff3.MergeActionType;
import org.openflexo.xml.diff3.MergeAttributeAction;
import org.openflexo.xml.diff3.UnresolvedAttributesConflict;

public class MaxValue extends MergeAttributeRule {

	public MaxValue(UnresolvedAttributesConflict conflict){
		super(conflict);
	}
	
	
	@Override
	public boolean canBeApplyed() {
		return "height".equals(_conflict.attributeName()) 
		|| "width".equals(_conflict.attributeName())
		|| "lastUniqueID".equals(_conflict.attributeName())
		|| "resourcesCount".equals(_conflict.attributeName());
	}

	@Override
	public MergeAttributeAction getAction() {
		if(_action==null){
			int v1 = Integer.valueOf(_conflict.value1());
			int v2 = Integer.valueOf(_conflict.value2());
			Integer max = new Integer(Math.max(v1, v2));
			_action = new MergeAttributeAction(_conflict.getConflictIndex(),MergeActionType.UPDATE,_conflict.attributeName(),max.toString(),_conflict.getMergedElement());
		}
		return _action;
	}

}
