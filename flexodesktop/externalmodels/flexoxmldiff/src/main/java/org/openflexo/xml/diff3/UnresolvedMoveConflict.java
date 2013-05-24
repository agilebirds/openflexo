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

import org.jdom2.Element;

public class UnresolvedMoveConflict extends UnresolvedConflict {

	private Element _srcContent;
	private Element _parent1InMergedDocument;
	private Element _parent2InMergedDocument;
	private int _insertion1;
	private int _insertion2;

	public UnresolvedMoveConflict(XMLDiff3 merge, int index, Element srcContent, Element parent1InMergedDocument,
			Element parent2InMergedDocument, int insertionIndex1, int insertionIndex2) {
		super(merge, index);
		_srcContent = srcContent;
		_parent1InMergedDocument = parent1InMergedDocument;
		_parent2InMergedDocument = parent2InMergedDocument;
		_insertion1 = insertionIndex1;
		_insertion2 = insertionIndex2;
	}

	public String getMovedContentName() {
		return _srcContent.getName();
	}

	public String getParent1Name() {
		return _parent1InMergedDocument.getName() + "(id=" + _parent1InMergedDocument.getAttributeValue("id") + ")";
	}

	public String getParent2Name() {
		return _parent2InMergedDocument.getName() + "(id=" + _parent2InMergedDocument.getAttributeValue("id") + ")";
	}

	@Override
	public MergeAction buildDiscardYourChangeAction() {
		return new MergeElementAction(getConflictIndex(), MergeActionType.INSERT, _srcContent, _parent1InMergedDocument, null, _insertion1);
	}

	@Override
	public MergeAction buildKeepYourChangeAction() {
		return new MergeElementAction(getConflictIndex(), MergeActionType.INSERT, _srcContent, _parent2InMergedDocument, null, _insertion2);
	}

}
