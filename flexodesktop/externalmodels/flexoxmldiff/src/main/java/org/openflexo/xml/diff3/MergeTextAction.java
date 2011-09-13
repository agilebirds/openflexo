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

public class MergeTextAction extends MergeAction {

	private Text _text1;
	private Text _text2;
	private Element _parentInMergedDocument;
	public MergeTextAction(int actionIndex,MergeActionType actionType,Element parentInMergedDocument,Text text1, Text text2){
		super(actionIndex,actionType);
		_text1 = text1!=null?(Text)text1.clone():null;
		_text2 = text2!=null?(Text)text2.clone():null;
		_parentInMergedDocument = parentInMergedDocument;
	}

	@Override
	public void execute() {
		switch (_actionType) {
		case CHOOSE1:
			if(_text1!=null)_parentInMergedDocument.addContent(_text1);
			break;
		case CHOOSE2:
			if(_text2!=null)_parentInMergedDocument.addContent(_text2);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void undo() {
		switch (_actionType) {
		case CHOOSE1:
			if(_text1!=null)_parentInMergedDocument.removeContent(_text1);
			break;
		case CHOOSE2:
			if(_text2!=null)_parentInMergedDocument.removeContent(_text2);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public String getLabel(){
		switch (_actionType) {
		case CHOOSE1:
			return "choose "+_text1.getText();
		case CHOOSE2:
			return "choose "+_text2.getText();
		}
		return "error : action type "+_actionType+" is not supposed to be set on this conflict";
	}
}

