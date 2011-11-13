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
package org.openflexo.fps.view;

import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSFile.FileContentEditor;
import org.openflexo.fps.controller.FPSController;

public class CodeEditor extends CodeDisplayer implements FileContentEditor {

	public CodeEditor(CVSFile cvsFile, FPSController controller) {
		super(cvsFile, controller);
		_component.setEditable(true);
		setEditedContent(getCVSFile());
	}

	@Override
	public String getEditedContent() {
		return _component.getEditedContent();
	}

	@Override
	public void setEditedContent(CVSFile file) {
		_component.setEditedContent(file);
	}

}
