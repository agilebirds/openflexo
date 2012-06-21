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

package org.openflexo.foundation.toc.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.DuplicateSectionException;
import org.openflexo.foundation.cg.action.InvalidLevelException;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;

public class RepairTOCEntry extends FlexoAction<RepairTOCEntry, TOCEntry, TOCObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RepairTOCEntry.class.getPackage().getName());

	public static FlexoActionType<RepairTOCEntry, TOCEntry, TOCObject> actionType = new FlexoActionType<RepairTOCEntry, TOCEntry, TOCObject>(
			"repair_toc_entry", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public RepairTOCEntry makeNewAction(TOCEntry focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
			return new RepairTOCEntry(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(TOCEntry object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(TOCEntry object, Vector<TOCObject> globalSelection) {
			return object.isReadOnly() && object.getIdentifier() == null && object.getObject(true) == null;
		}

	};

	private FixProposal choice;
	private FlexoModelObject modelObject;

	protected RepairTOCEntry(TOCEntry focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public enum FixProposal {
		DELETE, CHOOSE_OTHER_OBJECT, MAKE_NORMAL_SECTION
	}

	public TOCRepository getRepository() {
		return getFocusedObject().getRepository();
	}

	@Override
	protected void doAction(Object context) throws DuplicateSectionException, InvalidLevelException {
		switch (choice) {
		case DELETE:
			getFocusedObject().delete();
			break;
		case MAKE_NORMAL_SECTION:
			getFocusedObject().setIsReadOnly(false);
			getFocusedObject().setObjectReference(null);
			break;
		case CHOOSE_OTHER_OBJECT:
			getFocusedObject().setObject(getModelObject());
			break;
		default:
			break;
		}
	}

	public FixProposal getChoice() {
		return choice;
	}

	public void setChoice(FixProposal choice) {
		this.choice = choice;
	}

	public FlexoModelObject getModelObject() {
		return modelObject;
	}

	public void setModelObject(FlexoModelObject modelObject) {
		this.modelObject = modelObject;
	}

}
