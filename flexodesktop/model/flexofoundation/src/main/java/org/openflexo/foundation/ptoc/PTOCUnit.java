package org.openflexo.foundation.ptoc;

import java.util.Date;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.inspector.InspectableObject;

public abstract class PTOCUnit extends PTOCObject implements Sortable, InspectableObject, ReferenceOwner{

	protected Date lastUpdateDate;
	protected PTOCRepository repository;
	protected String title;
	protected int index = -1;
	
	protected String resourceName;
	protected FlexoModelObjectReference objectReference;
	protected PTOCEntry parent;
	protected CGRepositoryFileResource resource;
	
	public PTOCUnit(FlexoProject project) {
		super(project);
	}

	public PTOCUnit(PTOCData data) {
		super(data);
	}

	
	@Override
	public void setChanged() {
		lastUpdateDate = new Date();
		super.setChanged();
	}

	@Override
	public String getClassNameKey() {
		return "ptoc_entry";
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public PTOCRepository getRepository() {
		return repository;
	}
	
	
	@Override
	public String getName() {
		return getTitle();
	}

	public String getTitle() {
		return title;
	}

	
	public static PTOCUnit cloneUnitFromTemplate(PTOCEntry father, PTOCUnit source){
		if(source instanceof PTOCEntry){
			return PTOCEntry.cloneEntryFromTemplate(father, (PTOCEntry) source);
		}
		else if(source instanceof PSlide){
			return PSlide.cloneEntryFromTemplate(father, (PSlide) source);
		}
		return null;
	}
	
	public void setTitle(String title) {
		String old = this.title;
		this.title = title;
		setChanged();
		notifyAttributeModification("title", old, title);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
	}
	
	public void notifyAttributeModification(String attributeName, Object oldValue, Object newValue) {
		notifyObservers(new AttributeDataModification(attributeName,oldValue,newValue));
	}

	


	
	
	public int getIndex()
    {
        if (isBeingCloned())
            return -1;
        if (index == -1 && getCollection() != null) {
            index = getCollection().length;
            FlexoIndexManager.reIndexObjectOfArray(getCollection());
        }
        return index;
    }

    @Override
	public void setIndex(int index)
    {
        if (isDeserializing() || isCreatedByCloning()) {
            setIndexValue(index);
            return;
        }
        FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getParent()!=null)
			getParent().setChanged();
		else
			getRepository().setChanged();
		if (getIndex()!=index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index",null,getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
    }

    @Override
	public int getIndexValue()
    {
        return getIndex();
    }

    @Override
	public void setIndexValue(int index)
    {
        if (this.index==index)
            return;
        int old = this.index;
        this.index = index;
        setChanged();
        notifyObservers(new AttributeDataModification("index", old, index));
        if (!isDeserializing() && !isCreatedByCloning()) {
        	if (getRepository()!=null)
        		getRepository().notifyDocumentChanged(this);
        	if (getParent()!=null) {
        		getParent().setChanged();
        		getParent().notifyObservers(new ChildrenOrderChanged());
        	} else {
        		if (getRepository()!=null) {
        			getRepository().setChanged();
        			getRepository().notifyObservers(new ChildrenOrderChanged());
        		}
        	}
        }
        
    }
    
    
    @Override
	public PTOCUnit[] getCollection()
    {
        if (getParent() != null)
            return getParent().getPTocUnits().toArray(new PTOCUnit[0]);
        return getRepository().getPTocUnits().toArray(new PTOCUnit[0]);
    }
    
    public boolean isChildOf(PTOCUnit entry) {
		PTOCEntry aParent = getParent();
		while(aParent!=null) {
			if (aParent == entry)
				return true;
			aParent = aParent.getParent();
		}
		return false;
	}

	public Date getLastUpdateDate() {
		if (lastUpdateDate == null)
			lastUpdateDate = getLastUpdate();
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public FlexoModelObject getObject() {
		return getObject(false);
	}

	public FlexoModelObject getObject(boolean forceResourceLoad) {
		if (getObjectReference()!=null)
			return getObjectReference().getObject(forceResourceLoad);
		else
			return null;
	}

	public void setObject(FlexoModelObject object) {
		if (objectReference!=null) {
			objectReference.delete();
			objectReference = null;
		}
		if (object!=null)
			objectReference = new FlexoModelObjectReference<FlexoModelObject>(getProject(),object);
		else
			objectReference = null;
		if (objectReference!=null) {
			objectReference.setOwner(this);
			objectReference.setSerializeClassName(true);
		}
	}
	
	public PTOCEntry getParent() {
		return parent;
	}
	
	
	
	
	
	public void setParent(PTOCEntry parent) {
		PTOCEntry old = this.parent;
		if (this.parent!=null && this.parent!=parent)
			this.parent.removeFromPTocUnits(this);
		else if (getRepository()!=null && parent!=getRepository())
			getRepository().removeFromPTocUnits(this);
		this.parent = parent;
		if (parent!=null) {
			parent.addToPTocUnits(this);
			setRepository(parent.getRepository());
		}
		setChanged();
		notifyAttributeModification("parent", old, parent);
	}
	
	public FlexoModelObjectReference getObjectReference() {
		return objectReference;
	}
	
	public void setObjectReference(FlexoModelObjectReference objectReference) {
		if (this.objectReference!=null)
			this.objectReference = null;
		this.objectReference = objectReference;
		if (this.objectReference!=null)
			this.objectReference.setOwner(this);
	}

	public String getResourceName() {
		if (resource!=null)
			return resource.getResourceIdentifier();
		else
			return null;
	}

	public void setResourceName(String resourceName) {
		String old = resourceName;
		this.resourceName = resourceName;
		setChanged();
		notifyAttributeModification("resourceName", old, resourceName);
	}
   
	public int getLevel() {
		if (getParent()==null)
			return 0;
		else
			return getParent().getLevel()+1;
	}
	
	public int getPreferredLevel() {
			
			return -1;
		}

		public abstract void setRepository(PTOCRepository repository) ;
	public abstract int getDepth();
}
