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
import org.openflexo.xml.diff3.MergeTextAction;
import org.openflexo.xml.diff3.UnresolvedTextConflict;

public class ResourceCount extends MergeTextRule {

	public ResourceCount(UnresolvedTextConflict conflict) {
		super(conflict);
	}

	@Override
	public boolean canBeApplyed() {
		return "resourcesCount".equals(_conflict.getParentMergedElementName());
	}

	@Override
	public MergeTextAction getAction() {
		if(_action==null){
			int v1 = Integer.valueOf(_conflict.getText1().getText());
			int v2 = Integer.valueOf(_conflict.getText2().getText());
			if(v1>v2)
				_action = new MergeTextAction(_conflict.getConflictIndex(),MergeActionType.CHOOSE1,_conflict.getParentMergedElement(),_conflict.getText1(), _conflict.getText2());
			else
				_action = new MergeTextAction(_conflict.getConflictIndex(),MergeActionType.CHOOSE2,_conflict.getParentMergedElement(),_conflict.getText1(), _conflict.getText2());
		}
		return _action;
	}

}
