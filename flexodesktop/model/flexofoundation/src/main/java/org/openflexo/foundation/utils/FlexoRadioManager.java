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
package org.openflexo.foundation.utils;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;

/**
 * @author gpolet Created on 12 sept. 2005
 */
public class FlexoRadioManager {

	private Hashtable<String, HashSet<IERadioButtonWidget>> table;

	/**
	 * @param component
	 * 
	 */
	public FlexoRadioManager(IEWOComponent component) {
		table = new Hashtable<String, HashSet<IERadioButtonWidget>>();
	}

	public HashSet<IERadioButtonWidget> registerButton(IERadioButtonWidget w, String groupName) {
		if (table.get(groupName) == null)
			table.put(groupName, new HashSet<IERadioButtonWidget>());
		HashSet<IERadioButtonWidget> v = table.get(groupName);
		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			HashSet<IERadioButtonWidget> radios = table.get(key);
			if (radios != v && radios.contains(w))
				radios.remove(w);
		}
		if (w.getValue()) {
			Iterator<IERadioButtonWidget> i = v.iterator();
			while (i.hasNext()) {
				if (i.next().getValue()) {
					w.setValue(false);
					break;
				}
			}
		}
		if (!v.contains(w))
			v.add(w);
		return v;
	}

	public void unRegisterButton(IERadioButtonWidget w, String groupName) {
		if (table.get(groupName) != null) {
			((HashSet) table.get(groupName)).remove(w);
		}
	}

	public HashSet<IERadioButtonWidget> getButtons(String groupName) {
		return table.get(groupName);
	}

	public String getUnusedGroupName(String base) {
		String attempt = base;
		int i = 0;
		while (table.get(attempt) != null) {
			i++;
			attempt = base + "-" + i;
		}
		return attempt;
	}
}
