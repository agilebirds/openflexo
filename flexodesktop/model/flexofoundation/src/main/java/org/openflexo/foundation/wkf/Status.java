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
package org.openflexo.foundation.wkf;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.wkf.action.AddStatus;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;


/**
 * Represents a status of a process
 * 
 * @author sguerin
 * 
 */
public final class Status extends WKFObject implements DeletableObject, InspectableObject, LevelledObject, Sortable
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(Status.class.getPackage().getName());

    private String statusName;

    private String statusDescription;

    private int index  = -1;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public Status(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public Status(FlexoProcess process)
    {
        super(process);
    }

    /**
     * Default constructor
     * @throws DuplicateStatusException 
     */
    public Status(FlexoProcess process, String aStatusName) throws DuplicateStatusException
    {
        this(process);
        setName(aStatusName);
    }

    @Override
	public String getFullyQualifiedName()
    {
        return getProcess().getName() + ".STATUS." + getName();
    }

    /**
     * Default inspector name
     */
    @Override
	public String getInspectorName()
    {
        return "Status.inspector";
    }

    @Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
        returned.add(AddStatus.actionType);
        return returned;
    }

    @Override
	public String getName()
    {
        return statusName;
    }

    public String getNameAndProcess()
    {
        return new StringBuilder().append(getName()).append(" [").append(getProcess().getName()).append(']').toString();
    }

    @Override
	public void setName(String aName) throws DuplicateStatusException
    {
        if ((statusName == null && aName != null) || (statusName != null && aName == null)
                || (statusName != null && aName != null && !statusName.equals(aName))) {
           	if ((getStatusList() != null) && (getStatusList().statusWithName(aName) != null)) {
        		if (isDeserializing()) setName(aName+"-1");
          		throw new DuplicateStatusException(this,aName);
           	}
           String oldValue = statusName;
            statusName = aName;
            setChanged();
            notifyObservers(new WKFAttributeDataModification("name", oldValue, aName));
            if (getProcess() != null)
                getProcess().notifyStatusListUpdated();
        }
    }

    @Override
	public String getDescription()
    {
        return statusDescription;
    }

    @Override
	public void setDescription(String aDescription)
    {
        statusDescription = aDescription;
    }

    public StatusList getStatusList()
    {
        if (getProcess() != null) {
            return getProcess().getStatusList(false);
        }
        return null;
    }

    /**
     * Return a Vector of all embedded WKFObjects
     * 
     * @return a Vector of WKFObject instances
     */
    @Override
	public Vector getAllEmbeddedWKFObjects()
    {
        Vector returned = new Vector();
        returned.add(this);
        return returned;
    }

    /**
     * Returns the level of a Status (which is {@link FlexoLevel.PROCESS}).
     * 
     * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
     */
    @Override
	public FlexoLevel getLevel()
    {
        return FlexoLevel.PROCESS;
    }

    // ==========================================================================
    // ================================= Delete ===============================
    // ==========================================================================

    @Override
	public final void delete()
    {
        if (getStatusList() != null && getStatusList().getStatus().contains(this))
            getStatusList().removeFromStatus(this);
        super.delete();
        deleteObservers();
    }

    /**
     * Build and return a vector of all the objects that will be deleted during
     * process deletion
     * 
     * @param aVector
     *            of DeletableObject
     */
    @Override
	public Vector<WKFObject> getAllEmbeddedDeleted()
    {
        return getAllEmbeddedWKFObjects();
    }

    @Override
	public String toString()
    {
        return "STATUS." + getName();
    }

    /**
     * Overrides getClassNameKey
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "status";
    }
    @Override
	public int getIndex()
    {
        if (isBeingCloned())
            return -1;
        if (index==-1 && getCollection()!=null) {
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
        FlexoIndexManager.switchIndexForKey(this.index,index,this);
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
	public void setIndexValue(int index) {
    	if(index==this.index)
            return;
        int old = this.index;
        this.index = index;
        setChanged();
        notifyAttributeModification("index", old, index);
        if (!isDeserializing() && !isCreatedByCloning() && getProcess()!=null) { 
            getProcess().setChanged();
            getProcess().notifyObservers(new ChildrenOrderChanged());
        }
    }
    
    /**
     * Overrides getCollection
     * @see org.openflexo.foundation.utils.Sortable#getCollection()
     */
    @Override
	public Status[] getCollection()
    {
        if (getProcess()==null)
            return null;
        return getProcess().getStatusList().getStatus().toArray(new Status[0]);
    }
    @Override
    public boolean isDescriptionImportant() {
    	return !"Default".equals(getName());
    }
}
