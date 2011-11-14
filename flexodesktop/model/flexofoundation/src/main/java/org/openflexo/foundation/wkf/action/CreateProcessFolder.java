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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoFolderContainerNode;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.ProcessFolder;

public class CreateProcessFolder extends FlexoAction<CreateProcessFolder, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(CreateProcessFolder.class.getPackage().getName());

	public static FlexoActionType<CreateProcessFolder, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<CreateProcessFolder, FlexoModelObject, FlexoModelObject>(
			"create_process_folder", FlexoActionType.newMenu, FlexoActionType.newMenuGroup1, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateProcessFolder makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new CreateProcessFolder(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null && !object.isImported();
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return (object instanceof FlexoFolderContainerNode || object instanceof FlexoProcess) && !object.isImported();
		}

	};

	CreateProcessFolder(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
		FlexoModelObject.addActionForClass(actionType, FlexoFolderContainerNode.class);
	}

	private ProcessFolder newFolder;

	private String name;
	private String description;

	@Override
	protected void doAction(Object context) {
		newFolder = new ProcessFolder(getParent().getWorkflow(), getParent());
		try {
			newFolder.setName(getName());
			if (description != null) {
				newFolder.setDescription(description);
			}
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Invalid name " + getName(), e);
			}
		}
	}

	private FlexoFolderContainerNode getParent() {
		if (getFocusedObject() instanceof FlexoProcessNode) {
			return (FlexoProcessNode) getFocusedObject();
		} else if (getFocusedObject() instanceof FlexoProcess) {
			return ((FlexoProcess) getFocusedObject()).getProcessNode();
		} else if (getFocusedObject() instanceof ProcessFolder) {
			return ((ProcessFolder) getFocusedObject());
		}
		return null;
	}

	public ProcessFolder getNewFolder() {
		return newFolder;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (name == null) {
			return name = getParent().getNewFolderName();
		}
		return name;
	}

	public void setDescription(String value) {
		this.description = value;
	}

}
