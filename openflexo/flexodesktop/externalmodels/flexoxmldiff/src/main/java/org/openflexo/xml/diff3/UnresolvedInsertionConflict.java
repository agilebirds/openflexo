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
package org.openflexo.xml.diff3;

import org.jdom.Element;
/**
 * This conflict occurs when 2 Elements are inserted under the same parent
 * and that the parent cannot accept more than one child of this type.
 * This is checked against xml mappings.
 * @author bmangez
 *
 */
public class UnresolvedInsertionConflict extends UnresolvedConflict {

	private Element _parent;
	private Element _existingChild;
	private Element _rejectedChild;
	
	public UnresolvedInsertionConflict(XMLDiff3 merge,int index,Element parent,Element existingChild,Element rejectedChild){
		super(merge,index);
		_parent = parent;
		_existingChild = existingChild;
		_rejectedChild = rejectedChild;
	}
	
	@Override
	public String toString(){
		return "Element "+_parent.getName()+" (id="+_parent.getAttributeValue("id")+") can only accept one child named "+_existingChild.getName()+"\n"+
		"\texisting child :"+getExistingChildName()+"\n"+
		"\trejected child :"+getRejectedChildName();
	}
	public String getExistingChildName(){
		return _existingChild.getName()+"(id="+_existingChild.getAttributeValue("id")+")";
	}
	public String getRejectedChildName(){
		return _rejectedChild.getName()+"(id="+_rejectedChild.getAttributeValue("id")+")";
	}

	@Override
	public MergeAction buildDiscardYourChangeAction() {
		return new MergeElementAction(getConflictIndex(),MergeActionType.DONOTHING,_parent,_existingChild,_rejectedChild,-1);
	}

	@Override
	public MergeAction buildKeepYourChangeAction() {
		return new InsertionSwapAction(getConflictIndex(),_parent,_rejectedChild,_existingChild);
	}
}
