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
package org.openflexo.foundation.ie.widget;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Please comment this class
 * 
 * @author bmangez
 */
public class IECSSClassCollection {

	/**
     * 
     */
	public IECSSClassCollection() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void registerCSSClass(IECSSClass cls) {
		cssClasses.put(cls.name, cls);
		Vector v = getAllClassNameForTarget(cls.target);
		v.add(cls.name);
	}

	public static IECSSClass get(String name) {
		return (IECSSClass) cssClasses.get(name);
	}

	public static Vector getAllClassNameForTarget(String target) {
		if (target == null || target.equals("")) {
			target = "all";
		}
		Vector answer = (Vector) targets.get(target);
		if (answer == null) {
			return new Vector();
		}
		return answer;
	}

	public static final Hashtable targets = new Hashtable();

	public static final Hashtable cssClasses = new Hashtable();
}
