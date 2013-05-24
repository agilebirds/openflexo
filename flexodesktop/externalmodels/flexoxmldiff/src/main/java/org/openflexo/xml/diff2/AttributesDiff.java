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
package org.openflexo.xml.diff2;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class AttributesDiff {

	private Map<String, Attribute> deletedAttributes;
	private Map<String, Attribute> addedAttributes;
	private Map<Attribute, Attribute> updatedAttributes;
	private boolean _noDiff = true;
	private DocumentsMapping _documentMapping;
	private Element _sourceElement;

	public AttributesDiff(Element src, Element target, DocumentsMapping docMapping) {
		super();
		_sourceElement = src;
		_documentMapping = docMapping;
		deletedAttributes = new Hashtable<String, Attribute>();
		addedAttributes = new Hashtable<String, Attribute>();
		updatedAttributes = new Hashtable<Attribute, Attribute>();
		Iterator<Attribute> it = src.getAttributes().iterator();
		Attribute item = null;
		while (it.hasNext()) {
			item = it.next();
			if (target.getAttribute(item.getName()) == null) {
				deletedAttributes.put(item.getName(), item);
				_noDiff = false;
			} else if (!item.getValue().equals(target.getAttributeValue(item.getName()))) {
				updatedAttributes.put(item, target.getAttribute(item.getName()));
				_noDiff = false;
			}
		}
		it = target.getAttributes().iterator();
		while (it.hasNext()) {
			item = it.next();
			if (src.getAttribute(item.getName()) == null) {
				addedAttributes.put(item.getName(), item);
				_noDiff = false;
			}
		}
	}

	public Element getSourceElement() {
		return _sourceElement;
	}

	public DocumentsMapping getDocumentMapping() {
		return _documentMapping;
	}

	public boolean noDiff() {
		return _noDiff;
	}

	public Map<String, Attribute> getDeletedAttributes() {
		return deletedAttributes;
	}

	public Map<String, Attribute> getAddedAttributes() {
		return addedAttributes;
	}

	public Map<Attribute, Attribute> getUpdatedAttributes() {
		return updatedAttributes;
	}

	public boolean isUnchanged() {
		return getDeletedAttributes().size() == 0 && getAddedAttributes().size() == 0 && getUpdatedAttributes().size() == 0;
	}

}
