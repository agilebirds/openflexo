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
import org.jdom.Text;

public class UnresolvedTextConflict extends UnresolvedConflict{

	private Text _text1;
	private Text _text2;
	private Element _parentMergedElement;
	
	public UnresolvedTextConflict (XMLDiff3 merge,int index,Element element, Text text1, Text text2) {
		super(merge,index);
		_parentMergedElement = element;
		_text1 = text1;
		_text2 = text2;
	}
	
	public Element getParentMergedElement(){
		return _parentMergedElement;
	}
	public String getParentMergedElementName(){
		return _parentMergedElement.getName();
	}
	public Text getText1(){
		return _text1;
	}
	public Text getText2(){
		return _text2;
	}
	
	public String getText1Value(){
		return _text1==null?"":_text1.getTextTrim();
	}
	public String getText2Value(){
		return _text2==null?"":_text2.getTextTrim();
	}

	@Override
	public MergeAction buildDiscardYourChangeAction() {
		return new MergeTextAction(getConflictIndex(),MergeActionType.CHOOSE1,_parentMergedElement,_text1,_text2);
	}

	@Override
	public MergeAction buildKeepYourChangeAction() {
		return new MergeTextAction(getConflictIndex(),MergeActionType.CHOOSE2,_parentMergedElement,_text1,_text2);
	}
	
}
