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

import org.jdom.Content;
import org.jdom.Element;

public class UnresolvedElementConflict extends UnresolvedConflict {

	private Content _insertedElement;
	private Element _deletedElement;

	public UnresolvedElementConflict(XMLDiff3 merge, int index, Content inserted, Element deleted) {
		super(merge, index);
		_insertedElement = inserted;
		_deletedElement = deleted;
	}

	@Override
	public MergeAction buildDiscardYourChangeAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MergeAction buildKeepYourChangeAction() {
		// TODO Auto-generated method stub
		return null;
	}

}