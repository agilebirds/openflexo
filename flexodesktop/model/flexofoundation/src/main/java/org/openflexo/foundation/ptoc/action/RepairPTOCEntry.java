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

package org.openflexo.foundation.ptoc.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.DuplicateSectionException;
import org.openflexo.foundation.cg.action.InvalidLevelException;
import org.openflexo.foundation.ptoc.PTOCEntry;
import org.openflexo.foundation.ptoc.PTOCObject;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;


public class RepairPTOCEntry extends FlexoAction<RepairPTOCEntry, PTOCEntry,PTOCObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RepairPTOCEntry.class.getPackage().getName());

    public static FlexoActionType<RepairPTOCEntry,PTOCEntry, PTOCObject> actionType = new FlexoActionType<RepairPTOCEntry, PTOCEntry, PTOCObject>(
            "repair_toc_entry", FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
        public RepairPTOCEntry makeNewAction(PTOCEntry focusedObject, Vector<PTOCObject> globalSelection, FlexoEditor editor)
        {
            return new RepairPTOCEntry(focusedObject, globalSelection, editor);
        }

        @Override
        protected boolean isVisibleForSelection(PTOCEntry object, Vector<PTOCObject> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isEnabledForSelection(PTOCEntry object, Vector<PTOCObject> globalSelection)
        {
            return  object.getObject(true) == null; //object.isReadOnly() && object.getIdentifier()==null &&
        }

    };

    private FixProposal choice;
    private FlexoModelObject modelObject;
    
    protected RepairPTOCEntry(PTOCEntry focusedObject, Vector<PTOCObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    public enum FixProposal {
    	DELETE, CHOOSE_OTHER_OBJECT, MAKE_NORMAL_SECTION
    }

    public PTOCRepository getRepository() {
    	return getFocusedObject().getRepository();
    }
    
    @Override
    protected void doAction(Object context) throws DuplicateSectionException, InvalidLevelException
    {
    	switch (choice) {
		case DELETE:
			getFocusedObject().delete();
			break;
		case MAKE_NORMAL_SECTION:
			//getFocusedObject().setIsReadOnly(false);
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
