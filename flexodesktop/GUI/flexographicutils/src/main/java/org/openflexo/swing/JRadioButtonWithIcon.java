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
package org.openflexo.swing;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JRadioButton;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class JRadioButtonWithIcon extends JRadioButton {

	public JRadioButtonWithIcon(String text, File iconFile, boolean selected) {
		super();
		// Define an HTML fragment with an icon on the left and text on the
		// right.
		// The elements are embedded in a 3-column table.
		String label = null;
		try {
			label = "<html><table cellpadding=0><tr><td><img src=\""
			// The location of the icon
					+ iconFile.toURI().toURL() + "\"></td><td width="

					// The gap, in pixels, between icon and text
					+ 3 + "><td>"

					// Retrieve the current label text
					+ text + "</td></tr></table></html>";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setText(label);
		setSelected(selected);
	}

	public JRadioButtonWithIcon(String text, File iconFile) {
		this(text, iconFile, false);
	}
}
