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
package org.openflexo.wsdl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SchemaStructure {
	/*
	 * This class memorises 2 things :
	 *   -> all the types defined in a given namespace. Types are representer by their XML Element Object
	 *   -> the dependencies between one namespace and another. This is needed to know which namespace declaration must be spcified. 
	 */
	private Hashtable<String, Vector<Element>> ht = new Hashtable<String, Vector<Element>>();
	private Hashtable<String, Vector<String>> dependencies = new Hashtable<String, Vector<String>>();

	public void addElementInNamespace(String namespace, Element el) {
		if (!ht.containsKey(namespace)) {
			ht.put(namespace, new Vector<Element>());
		}
		ht.get(namespace).add(el);
	}

	public Vector<Element> getSchemaDefinition(Document doc) {
		Vector<Element> toReturn = new Vector<Element>();

		Enumeration<String> en = ht.keys();
		while (en.hasMoreElements()) {
			String currentNS = en.nextElement();
			Vector<Element> currentVector = ht.get(currentNS);
			Element el = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "schema");
			for (Element currentEl : currentVector) {
				el.appendChild(currentEl);
			}
			el.setAttribute("targetNamespace", currentNS);
			el.setAttribute("xmlns", "http://www.w3.org/2001/XMLSchema");
			toReturn.add(el);
		}

		return toReturn;
	}

	public void addNamespaceDependency(String dependant, String target) {
		if (dependant.equals(target))
			return;
		if (!dependencies.containsKey(dependant)) {
			dependencies.put(dependant, new Vector<String>());
		}
		dependencies.get(dependant).add(target);
	}

	public Vector<String> getNamespaceDependency(String namespace) {
		if (dependencies.containsKey(namespace))
			return dependencies.get(namespace);
		return new Vector<String>();
	}

}
