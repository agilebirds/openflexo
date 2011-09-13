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

import java.util.Hashtable;

public class NamespacePrefixMapper {
	private Hashtable<String,String> nameToPrefix=new Hashtable<String,String>();
	private int currentNS=0;
	
	public String getPrefixForNamespace(String namespace) {
		if (nameToPrefix.containsKey(namespace)) {
			return nameToPrefix.get(namespace);
		}
		else return null;
	}
	
	public void registerPrefixForNamespace(String namespace, String prefix) {
		nameToPrefix.put(namespace, prefix);
	}
	
	public void registerNamespace(String namespace) {
		if (! nameToPrefix.containsKey(namespace)) {
			System.out.println("registering namespace : "+namespace);
			registerPrefixForNamespace(namespace,"ns"+currentNS++);
		}
	}
	
	
}
