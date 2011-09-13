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
package org.openflexo.foundation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.toolbox.ToolBox;


/**
 * @author sguerin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FlexoUtils
{

	public static String exceptionStackTrace(Throwable tr)
	{
		StringWriter stw = new StringWriter();
		PrintWriter pw = new PrintWriter(stw);
		tr.printStackTrace(pw);
		pw.flush();
		String st = stw.toString();
		pw.close();
		return st;
	}

	public static String getItemName(IEWidget _widget, BindingValue itemVariable)
	{
		String itemName = null;
		if (itemVariable != null) {
			itemName = itemVariable.getCodeStringRepresentation();
		}
		if (itemName == null || itemName.trim().equals("")) {
			itemName = "item_" + _widget.getFlexoID();
		}
		return ToolBox.getJavaName(itemName);
	}

	public static <T> Vector<T> asVector(T... object) {
		Vector<T> vector = new Vector<T>();
		for (T t : object) {
			vector.add(t);
		}
		return vector;
	}
}
