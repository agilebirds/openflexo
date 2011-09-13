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

public class InsertionSwapAction extends MergeAction{

	private Element _toInsert;
	private Element _parentElement;
	private Element _existing;
	
	public InsertionSwapAction(int actionIndex, Element parentElement,Element toInsert, Element existingChild){
		super(actionIndex,MergeActionType.SWAP);
		_parentElement = parentElement;
		_toInsert = toInsert;
		_existing = existingChild;
	}

	@Override
	public void execute() {
		switch (_actionType) {
		case SWAP:
			System.out.println("Execute SWAP : remove "+_existing.getAttributeValue("id")+" inserting : "+_toInsert.getAttributeValue("id"));
			_parentElement.removeContent(_parentElement.getChild(_existing.getName()));
			_parentElement.addContent(_toInsert);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void undo() {
		switch (_actionType) {
		case SWAP:
			System.out.println("Undo SWAP : remove "+_toInsert.getAttributeValue("id")+" inserting : "+_existing.getAttributeValue("id"));
			_parentElement.removeContent(_parentElement.getChild(_toInsert.getName()));
			_parentElement.addContent(_existing);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public String getLabel(){
		switch (_actionType) {
		case SWAP:
			return "replace existing child by it's concurrent";
		}
		return "error : action type "+_actionType+" is not supposed to be set on this conflict";
	}
}
