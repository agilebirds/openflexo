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
import org.jdom.Text;

public class MergeElementAction extends MergeAction{

	private Content _operatedElement;
	private Element _parentElement;
	private Element _existingChild;
	private int _insertionIndex;
	public MergeElementAction(int actionIndex,MergeActionType actionType,Content operatedElement, Element parentElement, int insertionIndex){
		this(actionIndex,actionType, operatedElement, parentElement, null,insertionIndex);
	}
	
	public MergeElementAction(int actionIndex,MergeActionType actionType,Content operatedElement, Element parentElement, Element existingChild, int insertionIndex){
		super(actionIndex,actionType);
		_insertionIndex = insertionIndex;
		_parentElement = parentElement;
		_operatedElement = operatedElement;
		_existingChild = existingChild;
	}

	@Override
	public void execute() {
		switch (_actionType) {
		case DELETE:
			_parentElement.removeContent(_operatedElement);
			break;
		
		case INSERT:
			if(_insertionIndex>-1){
				_parentElement.addContent(Math.min(_insertionIndex,_parentElement.getContentSize()), _operatedElement.detach());
			}else
				_parentElement.addContent(_operatedElement);
			break;
		case SWAP:
			_parentElement.removeContent(_existingChild);
			_parentElement.addContent(_operatedElement);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void undo() {
		switch (_actionType) {
		case DELETE:
			if(_insertionIndex>-1){
				_parentElement.addContent(Math.min(_insertionIndex,_parentElement.getContentSize()), _operatedElement);
			}else
				_parentElement.addContent(_operatedElement);
			break;
		case INSERT:
			_parentElement.removeContent(_operatedElement);
			break;
		case SWAP:
			_parentElement.addContent(_existingChild);
			_parentElement.removeContent(_operatedElement);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public String getLabel(){
		switch (_actionType) {
		case DELETE:
			return "delete "+(_operatedElement instanceof Element?((Element)_operatedElement).getName():((Text)_operatedElement).getText());
		case INSERT:
			return "insert "+(_operatedElement instanceof Element?((Element)_operatedElement).getName():((Text)_operatedElement).getText());
		case SWAP:
			return "replace existing child by it's concurrent";
		}
		return "error : action type "+_actionType+" is not supposed to be set on this conflict";
	}
}
