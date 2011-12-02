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

public class MergeAttributeAction extends MergeAction {

	private String _attributeName;
	private String _attributeValue;
	private Element _element;
	private String _oldValue;

	public MergeAttributeAction(int actionIndex, MergeActionType actionType, String attributeName, String attributeValue, Element element) {
		super(actionIndex, actionType);
		_element = element;
		_attributeName = attributeName;
		_attributeValue = attributeValue;
	}

	@Override
	public String getLabel() {
		switch (_actionType) {
		case DELETE:
			return "delete " + _attributeName;
		case UPDATE:
			return _attributeName + " = " + _attributeValue;
		case INSERT:
			return "add " + _attributeName + " = " + _attributeValue;
		}
		return "error : action type " + _actionType + " is not supposed to be set on this conflict";
	}

	@Override
	public void execute() {
		switch (_actionType) {
		case DELETE:
			_element.removeAttribute(_attributeName);
			break;
		case UPDATE:
			_oldValue = _element.getAttributeValue(_attributeName);
			_element.setAttribute(_attributeName, _attributeValue);
			break;
		case INSERT:
			_element.setAttribute(_attributeName, _attributeValue);
			break;

		default:
			break;
		}
	}

	@Override
	public void undo() {
		switch (_actionType) {
		case DELETE:
			_element.setAttribute(_attributeName, _attributeValue);
			break;
		case UPDATE:
			_element.setAttribute(_attributeName, _oldValue);
			break;
		case INSERT:
			_element.removeAttribute(_attributeName);

			break;

		default:
			break;
		}
	}
}
