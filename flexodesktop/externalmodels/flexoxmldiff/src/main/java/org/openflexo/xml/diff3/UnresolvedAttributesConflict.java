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

import org.jdom.Attribute;
import org.jdom.Element;

public class UnresolvedAttributesConflict extends UnresolvedConflict {

	private Attribute _attribute1;
	private Attribute _attribute2;
	private Element _sourceElement;
	private Element _mergedElement;

	public UnresolvedAttributesConflict(XMLDiff3 merge, Element element, Attribute attribute1, Attribute attribute2, Element mergedElement) {
		super(merge, 0);
		_sourceElement = element;
		_mergedElement = mergedElement;
		_attribute1 = attribute1;
		_attribute2 = attribute2;
	}

	public Element getMergedElement() {
		return _mergedElement;
	}

	public String elementName() {
		return _sourceElement.getName();
	}

	public Element getRootSourceElement() {
		return _sourceElement.getDocument().getRootElement();
	}

	public String getRootElementName() {
		return getRootSourceElement().getName();
	}

	public String attributeName() {
		return _attribute1 != null ? _attribute1.getName() : _attribute2.getName();
	}

	public String originalValue() {
		return _sourceElement.getAttributeValue(attributeName());
	}

	public String value1() {
		if (_attribute1 == null)
			return null;
		return _attribute1.getValue();
	}

	public String value2() {
		if (_attribute2 == null)
			return null;
		return _attribute2.getValue();
	}

	@Override
	public String toString() {
		return "Unresolved conflict on attribute " + attributeName() + " of element " + _sourceElement.getName() + "(id="
				+ _sourceElement.getAttributeValue("id") + ")\n" + "\toriginalValue = '" + originalValue() + "'\n" + "\tfirst update = '"
				+ value1() + "'\n" + "\tsecond update = '" + value2() + "'";
	}

	@Override
	protected MergeAction buildDiscardYourChangeAction() {
		return new MergeAttributeAction(getConflictIndex(), MergeActionType.INSERT, attributeName(), value1(), _mergedElement);
	}

	@Override
	protected MergeAction buildKeepYourChangeAction() {
		return new MergeAttributeAction(getConflictIndex(), MergeActionType.INSERT, attributeName(), value2(), _mergedElement);
	}
}
