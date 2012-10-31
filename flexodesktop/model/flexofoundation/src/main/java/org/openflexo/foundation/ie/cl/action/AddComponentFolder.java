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
package org.openflexo.foundation.ie.cl.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.IECLObject;

public class AddComponentFolder extends FlexoAction<AddComponentFolder, IECLObject, IECLObject> {

	private static final Logger logger = Logger.getLogger(AddComponentFolder.class.getPackage().getName());

	public static FlexoActionType<AddComponentFolder, IECLObject, IECLObject> actionType = new FlexoActionType<AddComponentFolder, IECLObject, IECLObject>(
			"add_new_component_folder", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddComponentFolder makeNewAction(IECLObject focusedObject, Vector<IECLObject> globalSelection, FlexoEditor editor) {
			return new AddComponentFolder(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(IECLObject object, Vector<IECLObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(IECLObject object, Vector<IECLObject> globalSelection) {
			return object != null
					&& (object instanceof FlexoComponentFolder || object instanceof FlexoComponentLibrary || object instanceof ComponentDefinition);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoComponentLibrary.class);
		FlexoModelObject.addActionForClass(actionType, FlexoComponentFolder.class);
		FlexoModelObject.addActionForClass(actionType, ComponentDefinition.class);
	}

	private FlexoComponentFolder _newFolder;
	private FlexoComponentFolder _parentFolder;
	private String _newFolderName;

	AddComponentFolder(IECLObject focusedObject, Vector<IECLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Add component folder");
		if (getFocusedObject() != null) {
			if (getParentFolder() != null) {
				_newFolder = FlexoComponentFolder.createNewFolder(getParentFolder().getComponentLibrary(), getParentFolder(),
						getNewFolderName());
			} else {
				if (!getFocusedObject().getProject().getFlexoComponentLibrary().hasRootFolder()) {
					_parentFolder = FlexoComponentFolder.createNewRootFolder(getFocusedObject().getProject().getFlexoComponentLibrary());
				}
				_parentFolder = getFocusedObject().getProject().getFlexoComponentLibrary().getRootFolder();
				_newFolder = FlexoComponentFolder.createNewFolder(getParentFolder().getComponentLibrary(), getParentFolder(),
						getNewFolderName());
			}
		} else {
			throw new InvalidParametersException("unable to create component folder: no focused object supplied");
		}
	}

	public String getNewFolderName() {
		return _newFolderName;
	}

	public void setNewFolderName(String newFolderName) {
		_newFolderName = newFolderName;
	}

	public FlexoComponentFolder getParentFolder() {
		return _parentFolder;
	}

	public void setParentFolder(FlexoComponentFolder parentFolder) {
		_parentFolder = parentFolder;
	}

	public FlexoComponentFolder getNewFolder() {
		return _newFolder;
	}

}
