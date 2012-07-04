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
package org.openflexo.foundation.cg.action;

import java.util.Vector;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;

/**
 * @author gpolet
 * 
 */
public class AddDocType extends FlexoAction<AddDocType, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<AddDocType, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<AddDocType, FlexoModelObject, FlexoModelObject>(
			"add_doc_type", FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public AddDocType makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new AddDocType(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddDocType.actionType, TOCData.class);
		FlexoModelObject.addActionForClass(AddDocType.actionType, FlexoProject.class);
	}

	private String newName;
	private DocType newDocType;

	public DocType getNewDocType() {
		return newDocType;
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected AddDocType(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoModelObject mo = getFocusedObject();
		newDocType = new DocType(getNewName(), mo.getProject());
		mo.getProject().addToDocTypes(newDocType);
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

}
