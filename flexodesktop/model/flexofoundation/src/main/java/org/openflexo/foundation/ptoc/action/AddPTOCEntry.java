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


/**
 * MOS 
 * @author MOSTAFA
 * TODO_MOS
 */

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
import org.openflexo.foundation.ptoc.PTOCEntry;
import org.openflexo.foundation.ptoc.PTOCObject;
import org.openflexo.foundation.ptoc.PTOCRepository;



public class AddPTOCEntry extends FlexoAction<AddPTOCEntry, PTOCObject,PTOCObject>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddPTOCEntry.class.getPackage().getName());

    public static FlexoActionType<AddPTOCEntry, PTOCObject, PTOCObject> actionType = new FlexoActionType<AddPTOCEntry, PTOCObject, PTOCObject>(
            "add_toc_entry", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
        public AddPTOCEntry makeNewAction(PTOCObject focusedObject, Vector<PTOCObject> globalSelection, FlexoEditor editor)
        {
            return new AddPTOCEntry(focusedObject, globalSelection, editor);
        }

        @Override
        protected boolean isVisibleForSelection(PTOCObject object, Vector<PTOCObject> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isEnabledForSelection(PTOCObject object, Vector<PTOCObject> globalSelection)
        {
            return (object instanceof PTOCEntry && ((PTOCEntry)object).canHaveChildren());
        }

    };

    private String tocEntryTitle;

    AddPTOCEntry(PTOCObject focusedObject, Vector<PTOCObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    private DocSection section;
    private ProcessDocSectionSubType subType;
    private FlexoModelObject modelObject;

    public PTOCRepository getRepository() {
    	return ((PTOCEntry)getFocusedObject()).getRepository();
    }
    
    @Override
    protected void doAction(Object context) throws DuplicateSectionException, InvalidLevelException
    {
//    	if (section!=null) {
//    		if (getRepository().getPTOCEntryWithID(section)!=null && modelObject==null)
//    			throw new DuplicateSectionException();
//    	}
//    	PTOCEntry entry;
//    	if (section!=null || modelObject!=null) {
//    		if(section!=null && modelObject!=null)
//    			entry = getRepository().createObjectEntry(modelObject,section);
//    		else if(section!=null)
//    			entry = getRepository().createDefaultEntry(section);
//    		else
//    			entry = getRepository().createObjectEntry(modelObject);
//    		entry.setTitle(getTocEntryTitle());
//    		if(subType!=null)entry.setSubType(subType);
//    		if (entry.getPreferredLevel()==-1) {
//    			addEntryToFocusedObject(entry);
//    		} else {
//    			if (entry.getPreferredLevel()==1) {
//    				getRepository().addToTocEntries(entry);
//    			}
//    			else {
//    				if (getFocusedObject() instanceof PTOCRepository || (((PTOCEntry)getFocusedObject()).getLevel()+1<entry.getPreferredLevel())) {
//    					throw new InvalidLevelException();
//    				} else {
//    					PTOCEntry parent = ((PTOCEntry)getFocusedObject());
//    					while (parent.getLevel()>=entry.getPreferredLevel()) {
//    						parent = parent.getParent();
//    					}
//    					parent.addToTocEntries(entry);
//    				}
//    			}
//    		}
//    	} else {
//    		entry = new PTOCEntry(getFocusedObject().getData());
//    		if(subType!=null)entry.setSubType(subType);
//			entry.setTitle(getTocEntryTitle());
//			addEntryToFocusedObject(entry);
//    	}
    }

    private void addEntryToFocusedObject(PTOCEntry entry) {
   		((PTOCEntry)getFocusedObject()).addToPTocUnits(entry);
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
