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
package org.openflexo.foundation.ptoc;

/**
 * MOS
 * @author MOSTAFA
 * 
 * TODO_MOS
 * 
 */



import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;


import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.dm.PTOCRepositoryChanged;
import org.openflexo.foundation.cg.dm.TOCRepositoryChanged;
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.cg.utils.DocConstants.ProcessDocSectionSubType;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ptoc.action.MovePTOCEntry;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.toc.action.AddTOCEntry;
import org.openflexo.foundation.toc.action.MoveTOCEntry;
import org.openflexo.foundation.toc.action.RemoveTOCEntry;
import org.openflexo.foundation.toc.action.RepairTOCEntry;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.xml.FlexoPTOCBuilder;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

public class PTOCEntry extends PTOCUnit {

	public static final int MAXIMUM_DEPTH=10;

	
//	//TODO_MOS remove these properties 
//	protected Vector<PTOCEntry> tocEntries;
//	
//	private boolean startOnANewPage = false;
//	//
	
	
	//TODO_MOS
	protected Vector<PTOCUnit> ptocUnits;	
	//
	
	
	private boolean recursionEnabled = true;
	private boolean includeStatusList = true;
	

	

	/**
     * Create a new GeneratedCodeRepository.
     */
    public PTOCEntry(FlexoPTOCBuilder builder)
    {
        this(builder.ptocData);
        initializeDeserialization(builder);
    }

	public PTOCEntry(PTOCData data) {
		super(data);
		ptocUnits = new Vector<PTOCUnit>();
		//TODO_MOS 
		//tocEntries = new Vector<PTOCEntry>();
	}

	

	public PTOCEntry(PTOCData generatedCode, FlexoModelObject modelObject) {
		this(generatedCode);
		this.objectReference = new FlexoModelObjectReference<FlexoModelObject>(generatedCode.getProject(), modelObject);
		this.objectReference.setSerializeClassName(true); // Even if the object is not loaded yet, we need to know its class name.
		this.objectReference.setOwner(this);
	}


//TODO_MOS
	

	@Override
	public void delete() {
		for(PTOCUnit unit: (Vector<PTOCUnit>)ptocUnits.clone())
			unit.delete();
		if (objectReference!=null)
			objectReference.delete();
		super.delete();
		if (getParent()!=null)
			getParent().removeFromPTocUnits(this);
		else if (getRepository()!=null)
			getRepository().removeFromPTocUnits(this);
		deleteObservers();
		repository = null;
		//content = null;
	}

	
	@Override
	public String getFullyQualifiedName() {
		if (getRepository()==null)
			return getTitle();
		if (getParent()==null)
			return "PTOC_ENTRY."+getRepository().getTitle()+"."+getTitle();
		else
			return getParent().getFullyQualifiedName()+"."+getTitle();
	}

	//TODO_MOS change this actionlist
	@Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
    	Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
    	v.add(AddTOCEntry.actionType);
    	v.add(RemoveTOCEntry.actionType);
    	v.add(RepairTOCEntry.actionType);
    	v.add(MovePTOCEntry.actionType);
    	return v;
    }

	//TODO_MOS
	public boolean acceptsUnitAsChild(PTOCUnit unit) {
		return unit!=this && !isChildOf(unit) && !(unit instanceof PTOCRepository) && this.canHaveChildrenWithDepth(unit.getDepth()) && (unit.getPreferredLevel()==-1 || unit.getPreferredLevel()==getLevel()+1);
	}

	

	public void setRepository(PTOCRepository repository) {
		PTOCRepository old = this.repository;
		this.repository = repository;
		for (PTOCUnit unit : ptocUnits) {
			unit.setRepository(repository);
		}
		setChanged();
		notifyObservers(new PTOCRepositoryChanged(old,repository));
	}

	

	


	

	

	

	public String getValidReference() {
		return "PTOC-"+getLevel()+"-"+getTitle();
	}

	

	
	public Vector<PTOCUnit> getPtocUnits(){
		return getPTocUnits();
	}
	
	//TODO_MOS
	public Vector<PTOCUnit> getPTocUnits() {
		return ptocUnits;
	}

	public Enumeration<PTOCUnit> getSortedPTocUnits() {
		disableObserving();
		PTOCUnit[] o = FlexoIndexManager.sortArray(getPTocUnits().toArray(new PTOCUnit[0]));
        enableObserving();
        return ToolBox.getEnumeration(o);
	}

	public void setPTocUnits(Vector<PTOCUnit> ptocunits) {
		this.ptocUnits = ptocunits;
	}
	
	public void setPtocUnits(Vector<PTOCUnit> ptocunits) {
		setPTocUnits(ptocunits);
	}

	public void addToPTocUnits(PTOCUnit unit) {
		if (!ptocUnits.contains(unit)) {
			ptocUnits.add(unit);
			unit.setRepository(getRepository());
			if(!isDeserializing())
				unit.setIndexValue(ptocUnits.size());
			unit.setParent(this);
			if (!isDeserializing() && !isCreatedByCloning())
				FlexoIndexManager.reIndexObjectOfArray(getPTocUnits().toArray(new PTOCUnit[0]));
			if (getRepository()!=null)
				getRepository().notifyDocumentChanged(unit);
			setChanged();
			notifyObservers(new PTOCModification("tocEntries",null,unit));
		}
	}

	public void removeFromPTocUnits(PTOCUnit unit) {
		if (ptocUnits.contains(unit)) {
			ptocUnits.remove(unit);
			unit.setParent(null);
			unit.setRepository(null);
			if (!isDeserializing() && !isCreatedByCloning())
				FlexoIndexManager.reIndexObjectOfArray(getPTocUnits().toArray(new PTOCUnit[0]));
			if (getRepository()!=null)
				getRepository().notifyDocumentChanged(unit);
			setChanged();
			notifyObservers(new PTOCModification("tocEntries",unit,null));
		}
	}
	
//TODO_MOS Copy it

//	public PTOCEntry getPTOCEntryWithID(DocSection id) {
//		if (id==null)
//			return null;
//		for (PTOCEntry entry : tocEntries) {
//			if (id==entry.getIdentifier())
//				return entry;
//		}
//		for (PTOCEntry entry : tocEntries) {
//			PTOCEntry returned = entry.getPTOCEntryWithID(id);
//			if (returned!=null)
//				return returned;
//		}
//		return null;
//	}

	public PTOCUnit getPTOCUnitForObject(FlexoModelObject object) {
		if (object==null)
			return null;
		for (PTOCUnit unit : ptocUnits) {
			if (object==unit.getObject())
				return unit;
		}
		for (PTOCUnit entry : ptocUnits) {
			PTOCUnit returned = null;
			if(entry instanceof PTOCEntry)
			 returned = ((PTOCEntry)entry).getPTOCUnitForObject(object);
			if (returned!=null)
				return returned;
		}
		return null;
	}


    /**
     * Overrides getCollection
     *
     * @see org.openflexo.foundation.utils.Sortable#getCollection()
     */
    

	

	public boolean canHaveChildren() {
		return getLevel()<MAXIMUM_DEPTH;
	}

	public boolean canHaveChildrenWithDepth(int depth) {
		return getLevel()+depth < MAXIMUM_DEPTH+1;
	}

	public int getDepth() {
		if (ptocUnits.size()>0) {
			int deepest = 0;
			for (PTOCUnit entry : ptocUnits) {
				int dl = 0 ;
				if(entry instanceof PTOCEntry)
					dl= entry.getDepth();
				if (dl>deepest)
					deepest = dl;
			}
			return deepest+1;
		} else
			return 1;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.DE.DE_PTOC_ENTRY_INSPECTOR;
	}

	public String getTitleNumber() {
		if (getParent()!=null && getParent()!=getRepository())
			return getParent().getTitleNumber()+"."+getIndex();
		return String.valueOf(getIndex());
	}

	public String getDisplayString() {
		return getTitleNumber()+" "+getTitle()+(getRepository()!=null?"["+getRepository().getTitle()+"]":"");
	}

	private static int preferredLevelForModelObjectClass(Class klass) {
		if(klass.equals(FlexoProcess.class)){
			return 2;
		}
		if(klass.equals(DMEOEntity.class)){
			return 3;
		}
		return -1;
	}

	

	
	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {
		setChanged();
		notifyObservers(new PTOCModification(reference, null));
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {
		setChanged();
		notifyObservers(new PTOCModification(reference, null));
	}


	public void setRecursionEnabled(boolean v){
		if(v==recursionEnabled)return;
		recursionEnabled = v;
		setChanged();
		notifyObservers(new PTOCModification("recursionEnabled",!v,v));
		setChanged();
		notifyAttributeModification("recursionEnabled", !v,v);
	}

	public boolean getRecursionEnabled(){
		return recursionEnabled;
	}

	public void setIncludeStatusList(boolean v){
		if(v==includeStatusList)return;
		includeStatusList = v;
		setChanged();
		notifyObservers(new PTOCModification("includeStatusList",!v,v));

		setChanged();
		notifyAttributeModification("includeStatusList", !v,v);
	}

	public boolean getIncludeStatusList(){
		return includeStatusList;
	}



	

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference reference) {
		setChanged();
	}

	public static PTOCUnit cloneEntryFromTemplate(PTOCEntry father, PTOCEntry source) {
		PTOCEntry reply = new PTOCEntry(father.getData());
		reply.setTitle(source.getTitle());
		father.addToPTocUnits(reply);
		Enumeration<PTOCUnit> en = source.getSortedPTocUnits();
		while(en.hasMoreElements()){
			reply.addToPTocUnits(PTOCUnit.cloneUnitFromTemplate(reply, en.nextElement()));
		}
		return reply;
	}
}
