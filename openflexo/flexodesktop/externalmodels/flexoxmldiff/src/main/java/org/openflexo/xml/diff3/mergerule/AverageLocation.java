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

import java.awt.Point;

import org.openflexo.xml.diff3.MergeActionType;
import org.openflexo.xml.diff3.MergeAttributeAction;
import org.openflexo.xml.diff3.UnresolvedAttributesConflict;


public class AverageLocation extends MergeAttributeRule {

	public AverageLocation(UnresolvedAttributesConflict conflict){
		super(conflict);
	}
	
	
	@Override
	public boolean canBeApplyed() {
		return "deducedLocation".equals(_conflict.attributeName()) || "inducedDeducedLocation".equals(_conflict.attributeName());
	}

	@Override
	public MergeAttributeAction getAction() {
		if(_action==null){
			Point p1 = MergeUtils.pointFromString(_conflict.value1());
			Point p2 = MergeUtils.pointFromString(_conflict.value2());
			Point average = new Point((p1.x+p2.x)/2,(p1.y+p2.y)/2);
			_action = new MergeAttributeAction(_conflict.getConflictIndex(),MergeActionType.UPDATE,_conflict.attributeName(),MergeUtils.pointToString(average),_conflict.getMergedElement());
		}
		return _action;
	}

}
