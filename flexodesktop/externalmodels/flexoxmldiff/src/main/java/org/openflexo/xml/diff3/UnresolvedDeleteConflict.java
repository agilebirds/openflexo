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

public class UnresolvedDeleteConflict extends UnresolvedConflict {

	private Element _src;
	private Element _e1;
	private Element _e2;
	private Element _parentInTarget;
	private int _index1;
	private int _index2;

	public UnresolvedDeleteConflict(XMLDiff3 merge, int index, Element src, Element potentialParent, Element element1, Element element2,
			int index1, int index2) {
		super(merge, index);
		_src = src;
		_e1 = element1;
		_e2 = element2;
		_parentInTarget = potentialParent;
		_index1 = index1;
		_index2 = index2;

	}

	public boolean isMyDeletion() {
		return _e2 == null;
	}

	public boolean isThrirdPartyDeletion() {
		return _e1 == null;
	}

	public String getDeletedElementName() {
		return _src.getName() + "(id=" + _src.getAttributeValue("id") + ")";
	}

	public Element getElement2() {
		return _e2;
	}

	public Element getElement1() {
		return _e1;
	}

	@Override
	public MergeAction buildDiscardYourChangeAction() {
		if (_e1 == null) {
			return new MergeElementAction(getConflictIndex(), MergeActionType.DONOTHING, _e2, _parentInTarget, null, -1);
		} else {
			return new MergeElementAction(getConflictIndex(), MergeActionType.INSERT, _e1, _parentInTarget, null, _index1);
		}
	}

	@Override
	public MergeAction buildKeepYourChangeAction() {
		if (_e1 == null) {
			return new MergeElementAction(getConflictIndex(), MergeActionType.INSERT, _e2, _parentInTarget, null, _index2);
		} else {
			return new MergeElementAction(getConflictIndex(), MergeActionType.DONOTHING, _e2, _parentInTarget, null, -1);
		}
	}

}
