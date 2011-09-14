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
package org.openflexo.foundation.bpel;

import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class BPELNamespacePrefixMapper extends NamespacePrefixMapper{
	private Hashtable<String,String> ht;
	private int currentPrefixIndex=0;

	public BPELNamespacePrefixMapper() {
		ht=new Hashtable<String, String>();
	}
	@Override
	public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
		if (ht.containsKey(arg0)) {
			return ht.get(arg0);
		}
		return null;
	}

	@Override
	public String[] getPreDeclaredNamespaceUris() {
		int i=0;
		String[] toReturn=new String[ht.size()];
		Enumeration<String> en=ht.keys();
		while (en.hasMoreElements()) {
			toReturn[i++]=en.nextElement();
		}
		return toReturn;
	}

	public void addNamespaceAndPrefix(String namespace, String prefix) {
		if (ht.containsKey(namespace)) {
			return;
		}

		if (prefix != null) {
			ht.put(namespace, prefix);
		} else {
			ht.put(namespace,"ns"+currentPrefixIndex++);
		}
	}

	@Override
	public String toString() {
		return ht.toString();
	}

}
