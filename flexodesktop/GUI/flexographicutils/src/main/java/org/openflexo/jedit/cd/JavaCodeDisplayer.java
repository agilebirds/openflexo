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
package org.openflexo.jedit.cd;

import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JavaTokenMarker;
import org.openflexo.toolbox.FontCst;

public class JavaCodeDisplayer extends JEditTextArea {

	public JavaCodeDisplayer(String text) {
		super();
		setTokenMarker(new JavaTokenMarker());
		painter.setEOLMarkersPainted(false);
		painter.setInvalidLinesPainted(false);
		setFont(FontCst.JAVA_CODE_FONT);
		setEditable(false);
		// setElectricScroll(1000);
		setFirstLine(0);
		setText(text);
	}
}
