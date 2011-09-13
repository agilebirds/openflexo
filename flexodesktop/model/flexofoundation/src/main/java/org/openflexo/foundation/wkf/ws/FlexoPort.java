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
package org.openflexo.foundation.wkf.ws;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.PortRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.localization.FlexoLocalization;

/**
 * Abstract representation of a port associated to a PortRegistery associated to
 * a FlexoProcess
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoPort extends AbstractNode implements Sortable
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoPort.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private PortRegistery _registery;

	private int index  = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public FlexoPort(FlexoProcess process)
	{
		super(process);
	}

	@Override
	public abstract String getDefaultName();

	@Override
	public String getInspectorName()
	{
		return "FlexoPort.inspector";
	}

	@Override
	public FlexoPort getNode()
	{
		return this;
	}

	@Override
	public FlexoLevel getLevel()
	{
		return FlexoLevel.PORT;
	}

	public PortRegistery getPortRegistery()
	{
		return _registery;
	}

	public void setPortRegistery(PortRegistery registery)
	{
		_registery = registery;
	}

	/**
	 * Return the father which is the FlexoProcess
	 * 
	 * @return
	 */
	public FlexoProcess getFather()
	{
		return getProcess();
	}

	public abstract boolean isInPort();

	public abstract boolean isOutPort();

	@Override
	public void setName(String aName)
	{
		String oldValue = getName();
		if ((oldValue == null) || (!oldValue.equals(aName))) {
			super.setName(findNextName(aName));
			if (stringHasChanged(aName, getName())) {
				setChanged();
				notifyObserversAsReentrantModification(new WKFAttributeDataModification("name",oldValue,getName()));
			}
		}
	}

	private String findNextName(String aName) 
	{
		if (getPortRegistery() == null) {
			return aName;
		}
		String base = aName;
		int i = 1;
		while ((getPortRegistery().portWithName(aName)!=null) && (getPortRegistery().portWithName(aName)!=this)) {
			aName = base+"-"+i;
			i++;
		}
		return aName;
	}
	
	public abstract String getPrefixForFullQualifiedName();

	@Override
	public String getFullyQualifiedName()
	{
		if (getFather() != null) {
			return getFather().getFullyQualifiedName() + "." + getPrefixForFullQualifiedName() + "." + formattedString(getNodeName());
		} else {
			return "NULL." + formattedString(getNodeName());
		}
	}

	@Override
	public boolean isContainedIn(WKFObject obj) {
		if (obj instanceof PortRegistery) {
			return ((PortRegistery)obj).getAllPorts().contains(this);
		}
		return super.isContainedIn(obj);
	}

	public abstract boolean isCorrectelyLinked();


	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete()
	{
		getPortRegistery().removeFromPorts(this);
		super.delete();
		setChanged();
		notifyObservers(new PortRemoved(this));
		deleteObservers();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during
	 * this deletion
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
	public int getIndex()
	{
		if (isBeingCloned()) {
			return -1;
		}
		if ((index==-1) && (getCollection()!=null)) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public final void setIndex(int index)
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
	public final void setIndexValue(int index) {
		if(index==this.index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyAttributeModification("index", old, index);
		if (!isDeserializing() && (getPortRegistery()!=null)) { 
			getPortRegistery().setChanged();
			getPortRegistery().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public FlexoPort[] getCollection()
	{
		if (getPortRegistery()==null) {
			return null;
		}
		return getPortRegistery().getAllPorts().toArray(new FlexoPort[0]);
	}

	public static String getDefaultInitialName()
	{
		return FlexoLocalization.localizedForKey("default_port_name");
	}


	public Vector<FlexoPortMap> getAllPortMaps() {
		Vector<FlexoPortMap> portMaps = new Vector<FlexoPortMap>();
		for(SubProcessNode node:getProcess().getSubProcessNodes()) {
			FlexoPortMap portMap = node.getPortMapRegistery().getPortMapForPort(this);
			if (portMap!=null) {
				portMaps.add(portMap);
			}
		}
		return portMaps;
	}


}
