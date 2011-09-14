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
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.cg.utils.DocConstants.ProcessDocSectionSubType;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;


public class AddTOCEntry extends FlexoAction<AddTOCEntry, TOCObject,TOCObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddTOCEntry.class.getPackage().getName());

    public static FlexoActionType<AddTOCEntry, TOCObject, TOCObject> actionType = new FlexoActionType<AddTOCEntry, TOCObject, TOCObject>(
            "add_toc_entry", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
        public AddTOCEntry makeNewAction(TOCObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor)
        {
            return new AddTOCEntry(focusedObject, globalSelection, editor);
        }

        @Override
        protected boolean isVisibleForSelection(TOCObject object, Vector<TOCObject> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isEnabledForSelection(TOCObject object, Vector<TOCObject> globalSelection)
        {
            return (object instanceof TOCEntry && ((TOCEntry)object).canHaveChildren());
        }

    };

    private String tocEntryTitle;

    AddTOCEntry(TOCObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    private DocSection section;
    private ProcessDocSectionSubType subType;
    private FlexoModelObject modelObject;

    public TOCRepository getRepository() {
    	return ((TOCEntry)getFocusedObject()).getRepository();
    }
    
    @Override
    protected void doAction(Object context) throws DuplicateSectionException, InvalidLevelException
    {
    	if (section!=null) {
    		if (getRepository().getTOCEntryWithID(section)!=null && modelObject==null)
    			throw new DuplicateSectionException();
    	}
    	TOCEntry entry;
    	if (section!=null || modelObject!=null) {
    		if(section!=null && modelObject!=null)
    			entry = getRepository().createObjectEntry(modelObject,section);
    		else if(section!=null)
    			entry = getRepository().createDefaultEntry(section);
    		else
    			entry = getRepository().createObjectEntry(modelObject);
    		entry.setTitle(getTocEntryTitle());
    		if(subType!=null)entry.setSubType(subType);
    		if (entry.getPreferredLevel()==-1) {
    			addEntryToFocusedObject(entry);
    		} else {
    			if (entry.getPreferredLevel()==1) {
    				getRepository().addToTocEntries(entry);
    			}
    			else {
    				if (getFocusedObject() instanceof TOCRepository || (((TOCEntry)getFocusedObject()).getLevel()+1<entry.getPreferredLevel())) {
    					throw new InvalidLevelException();
    				} else {
    					TOCEntry parent = ((TOCEntry)getFocusedObject());
    					while (parent.getLevel()>=entry.getPreferredLevel()) {
    						parent = parent.getParent();
    					}
    					parent.addToTocEntries(entry);
    				}
    			}
    		}
    	} else {
    		entry = new TOCEntry(getFocusedObject().getData());
    		if(subType!=null)entry.setSubType(subType);
			entry.setTitle(getTocEntryTitle());
			addEntryToFocusedObject(entry);
    	}
    }

    private void addEntryToFocusedObject(TOCEntry entry) {
   		((TOCEntry)getFocusedObject()).addToTocEntries(entry);
    }
    
	public String getTocEntryTitle() {
		return tocEntryTitle;
	}

	public void setTocEntryTitle(String tocEntryTitle) {
		this.tocEntryTitle = tocEntryTitle;
	}

	public void setSection(DocSection section) {
		this.section = section;
	}
	
	public void setModelObject(FlexoModelObject modelObject) {
		this.modelObject = modelObject;
	}

	public ProcessDocSectionSubType getSubType() {
		return subType;
	}

	public void setSubType(ProcessDocSectionSubType subType) {
		this.subType = subType;
	}
}
