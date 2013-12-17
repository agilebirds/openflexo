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
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.toc.TOCData;

/**
 * @author gpolet
 * 
 */
public class AddDocType extends FlexoAction<AddDocType, AgileBirdsObject, AgileBirdsObject> {

	public static final FlexoActionType<AddDocType, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<AddDocType, AgileBirdsObject, AgileBirdsObject>(
			"add_doc_type", FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return true;
		}

		@Override
		public AddDocType makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
			return new AddDocType(focusedObject, globalSelection, editor);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(AddDocType.actionType, TOCData.class);
		AgileBirdsObject.addActionForClass(AddDocType.actionType, FlexoProject.class);
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
	protected AddDocType(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		AgileBirdsObject mo = getFocusedObject();
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
