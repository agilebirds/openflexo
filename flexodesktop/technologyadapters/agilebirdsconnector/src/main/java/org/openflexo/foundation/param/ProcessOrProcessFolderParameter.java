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
package org.openflexo.foundation.param;

import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.inspector.widget.DenaliWidget;

public class ProcessOrProcessFolderParameter extends ParameterDefinition<AgileBirdsObject> {

	private ProcessOrProcessFolderSelectingConditional _processSelectingConditional;

	public ProcessOrProcessFolderParameter(String name, String label) {
		this(name, label, (AgileBirdsObject) null);
	}

	public ProcessOrProcessFolderParameter(String name, String label, FlexoProcess defaultValue) {
		this(name, label, (AgileBirdsObject) defaultValue);
	}

	public ProcessOrProcessFolderParameter(String name, String label, ProcessFolder defaultValue) {
		this(name, label, (AgileBirdsObject) defaultValue);
	}

	private ProcessOrProcessFolderParameter(String name, String label, AgileBirdsObject defaultValue) {
		super(name, label, defaultValue);
		addParameter("className", "org.openflexo.components.widget.ProcessOrProcessFolderInspectorWidget");
		_processSelectingConditional = null;
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.CUSTOM;
	}

	public boolean isAcceptableProcessOrProcessFolder(AgileBirdsObject aProcess) {

		if (aProcess instanceof FlexoProcess) {
			if (_processSelectingConditional != null) {
				return _processSelectingConditional.isSelectable((FlexoProcess) aProcess);
			}
			return true;
		}

		if (aProcess instanceof ProcessFolder) {
			if (_processSelectingConditional != null) {
				return _processSelectingConditional.isSelectable((ProcessFolder) aProcess);
			}
			return true;
		}

		return false;
	}

	public void setProcessSelectingConditional(ProcessOrProcessFolderSelectingConditional processSelectingConditional) {
		_processSelectingConditional = processSelectingConditional;
		addParameter("isSelectable", "params." + getName() + ".isAcceptableProcessOrProcessFolder");
	}

	public abstract static class ProcessOrProcessFolderSelectingConditional {
		public abstract boolean isSelectable(FlexoProcess process);

		public abstract boolean isSelectable(ProcessFolder processFolder);
	}

}
