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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.ShowHidePortmapRegistery;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PortMapInserted;
import org.openflexo.foundation.wkf.dm.PortMapRegisteryOrientationChanged;
import org.openflexo.foundation.wkf.dm.PortMapRegisteryRemoved;
import org.openflexo.foundation.wkf.dm.PortMapRemoved;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;


/**
 * A PortMapRegistery is attached to a SubProcessNode and contains all the
 * portmaps used in the context of WebServices
 *
 * @author sguerin
 *
 */
public final class PortMapRegistery extends WKFObject implements InspectableObject, LevelledObject, DeletableObject, FlexoObserver
{

    private static final Logger logger = Logger.getLogger(PortMapRegistery.class.getPackage().getName());

    public static final int NORTH = 0;

    public static final int EAST = 1;

    public static final int SOUTH = 2;

    public static final int WEST = 3;

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================

    private SubProcessNode _subProcessNode;

    private int _orientation = NORTH;

    private Vector<FlexoPortMap> _portMaps;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public PortMapRegistery(FlexoProcessBuilder builder)
    {
        this(builder.process);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public PortMapRegistery(FlexoProcess process)
    {
        super(process);
        _portMaps = new Vector<FlexoPortMap>();
    }

    /**
     * @param portRegistery
     */
    public PortMapRegistery(SubProcessNode subProcessNode)
    {
        this(subProcessNode.getProcess());
        setSubProcessNode(subProcessNode);
        setOrientation(NORTH);
    }

    @Override
    public String getFullyQualifiedName()
    {
        return getProcess().getFullyQualifiedName() + ".PORTMAP_REGISTERY";
    }



    public SubProcessNode getSubProcessNode()
    {
        return _subProcessNode;
    }

    public void setSubProcessNode(SubProcessNode subProcessNode)
    {
        if (_subProcessNode != subProcessNode) {
            if (_subProcessNode != null) {
                if (_subProcessNode.getActiveServiceInterface() != null) {
                    _subProcessNode.getActiveServiceInterface().deleteObserver(this);
                }
            }
        }
        _subProcessNode = subProcessNode;
    }

    /**
     * refactoring of this method: lookupRegistery instead of lookupPortRegistry
     *
     */
    public void lookupServiceInterface()
    {
    	if (logger.isLoggable(Level.FINE))
			logger.fine("lookupServiceInterface");
		getServiceInterface();
		updateFromServiceInterface();
    }


    public ServiceInterface getServiceInterface()
    {
        if ((getSubProcessNode() != null) && (getSubProcessNode().getSubProcess() != null)) {
            if (getSubProcessNode().getActiveServiceInterface() != null) {
            		getSubProcessNode().getActiveServiceInterface().addObserver(this);
            } else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("No related ServiceInterface !!!!");
            }
            return getSubProcessNode().getActiveServiceInterface();
        }
        return null;
    }

    /**
     * Return a Vector of all embedded WKFObjects
     *
     * @return a Vector of WKFObject instances
     */
    @Override
    public Vector<WKFObject> getAllEmbeddedWKFObjects()
    {
        Vector<WKFObject> returned = new Vector<WKFObject>();
        returned.add(this);
        for (Enumeration<FlexoPortMap> e = getPortMaps().elements(); e.hasMoreElements();) {
            FlexoPortMap portMap = e.nextElement();
            returned.addAll(portMap.getAllEmbeddedWKFObjects());
        }
        return returned;
    }

    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
         Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
         returned.remove(WKFDelete.actionType);
         returned.add(ShowHidePortmapRegistery.actionType);
        return returned;
    }

    @Override
    public Vector<WKFObject> getAllEmbeddedDeleted()
    {
        return getAllEmbeddedWKFObjects();
    }

    public Vector<FlexoPortMap> getPortMaps()
    {
        return _portMaps;
    }

    public void reorderPortmaps(FlexoPortMap reorderedPortMap, FlexoPortMap after)
    {
    	_portMaps.remove(reorderedPortMap);
    	int index = _portMaps.indexOf(after);
    	_portMaps.insertElementAt(reorderedPortMap, index+1);
    }

    public Vector<FlexoPortMap> getVisiblePortMaps() {
        Vector<FlexoPortMap> v = new Vector<FlexoPortMap>(_portMaps.size()+1);
        Enumeration en = _portMaps.elements();
        while (en.hasMoreElements()) {
            FlexoPortMap map = (FlexoPortMap) en.nextElement();
            if (!map.getIsHidden())
                v.add(map);

        }
        return v;
    }

    public void setPortMaps(Vector<FlexoPortMap> portMaps)
    {
        _portMaps = portMaps;
    }

    public void addToPortMaps(FlexoPortMap aPortMap)
    {
    	//logger.info(">>>>>> addToPortMaps("+aPortMap.getPortName()+","+aPortMap.getOperation()+")");
         if (!_portMaps.contains(aPortMap)) {
            _portMaps.add(aPortMap);
            aPortMap.setPortMapRegistery(this);
            aPortMap.addObserver(this);
            setChanged();
            notifyObservers(new PortMapInserted(aPortMap));
            if (getProcess()!=null)
            	getProcess().clearCachedObjects();
        }
    }

    public void removeFromPortMaps(FlexoPortMap aPortMap)
    {
       //	logger.info(">>>>>> removeFromPortMaps("+aPortMap+")");
        if (_portMaps.contains(aPortMap)) {
            _portMaps.remove(aPortMap);
             aPortMap.setPortMapRegistery(null);
             aPortMap.deleteObserver(this);
            setChanged();
            notifyObservers(new PortMapRemoved(aPortMap));
            if (getProcess()!=null)
            	getProcess().clearCachedObjects();
        }
    }


    public void updateFromServiceInterface()
    {
     	if (logger.isLoggable(Level.FINE))
    		logger.fine("updateFromServiceInterface()");
    	Vector<FlexoPortMap> portMapsToDelete = new Vector<FlexoPortMap>(getPortMaps());
    	if (getServiceInterface() != null) {
    		//Vector operations = getServiceInterface().getOperations();
    		//if (logger.isLoggable(Level.FINE)) logger.fine("Operations of serviceInterface:"+operations);
    		for (Enumeration e = getServiceInterface().getSortedOperations(); e.hasMoreElements();) {
    			ServiceOperation op = (ServiceOperation) e.nextElement();
    			if (portMapForOperation(op) == null) {
    				// Creates the portmap

    				FlexoPortMap newPortMap = new FlexoPortMap(op, getProcess());
    				newPortMap.setIndex(op.getPort().getIndex());
    				addToPortMaps(newPortMap);
    			} else {

    				portMapsToDelete.remove(portMapForOperation(op));
    			}
    			if (portMapForOperation(op) != null) {
    				portMapForOperation(op).registerUnderSubProcessNode();
    			}
    		}
    	} else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("No related ServiceInterface ! !");
    	}
    	for (Enumeration<FlexoPortMap> e = portMapsToDelete.elements(); e.hasMoreElements();) {
    		FlexoPortMap portMap = e.nextElement();
    		if (logger.isLoggable(Level.FINE))
    			logger.fine("Remove portmap " + portMap.getPortName());
    		portMap.delete();
    	}
    }

    private FlexoPortMap portMapForOperation(ServiceOperation op)
    {
        for (Enumeration e = getPortMaps().elements(); e.hasMoreElements();) {
            FlexoPortMap portMap = (FlexoPortMap) e.nextElement();
            if (portMap.getOperation() == op) {
                return portMap;
            }
        }
        return null;
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
    	if (logger.isLoggable(Level.FINE))
			logger.fine("update PortMapRegistry" + observable + " - " + dataModification.getClass().getName());
		if (!isSerializing()) {
			if (observable instanceof ServiceInterface) {
				updateFromServiceInterface();
			}
		}
		if (observable instanceof FlexoPortMap && dataModification instanceof ObjectVisibilityChanged) {
			if (((ObjectVisibilityChanged)dataModification).isVisible())
				setIsHidden(false);
			else
				hideIfRequired();
		}
    }

	private void hideIfRequired() {
    	boolean hide = true;
    	for(FlexoPortMap map:getPortMaps()) {
    		if (map.getIsVisible()) {
    			hide = false;
    			break;
    		}
    	}
    	if (hide) {
    		setIsHidden(true);
    	}
    }

    @Override
    public final void delete()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("delete() in PortMapRegistery");
        for (Enumeration e = ((Vector)getPortMaps().clone()).elements(); e.hasMoreElements();) {
            FlexoPortMap portMap = (FlexoPortMap) e.nextElement();
            portMap.delete();
        }
        super.delete();
        setChanged();
        notifyObservers(new PortMapRegisteryRemoved(this));
        if (getSubProcessNode()!=null) {
        	getSubProcessNode().setPortMapRegistery(null);
        	setSubProcessNode(null);
        }
        setProcess(null);
        deleteObservers();
    }

    @Override
	public String getInspectorName()
    {
        return Inspectors.WKF.PORTMAP_REGISTERY_INSPECTOR;
    }

    @Override
	public FlexoLevel getLevel()
    {
        return FlexoLevel.ACTIVITY;
    }

    public int getOrientation()
    {
        return _orientation;
    }

    public void setOrientation(int orientation)
    {
        int oldOrientation = _orientation;
        if (oldOrientation != orientation) {
            _orientation = orientation;
             setChanged();
            notifyObservers(new PortMapRegisteryOrientationChanged(oldOrientation, orientation));
        }
    }

     // ==========================================================================
    // ============================= Validation
    // =================================
    // ==========================================================================

    /**
     * must refer to  a serviceInterface
     */
    public static class PortMapRegisteryMustReferToServiceInterface extends ValidationRule
    {
        public PortMapRegisteryMustReferToServiceInterface()
        {
            super(PortMapRegistery.class, "portmap_registery_must_be_linked_to_a_service_interface");
        }

        @Override
        public ValidationIssue applyValidation(final Validable object)
        {
            final PortMapRegistery portMapRegistery = (PortMapRegistery) object;
            if (portMapRegistery.getServiceInterface() == null) {
                ValidationError error = new ValidationError(this, object, "portmap_registery_is_not_linked_to_a_service_interface");
                error.addToFixProposals(new DeletionFixProposal("delete_this_portmap_registery"));
                return error;
            }
            return null;
        }

    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
    public String getClassNameKey()
    {
        return "port_map_registery";
    }

	public FlexoPortMap getPortMapForPort(FlexoPort flexoPort) {
		for (FlexoPortMap portMap : _portMaps) {
			if (portMap.getOperation().getPort()==flexoPort)
				return portMap;
		}
		return null;
	}

	public Vector<FlexoPortMap> getAllNewPortmaps()
	{
		 Vector<FlexoPortMap> returned = new Vector<FlexoPortMap>();
		 for (FlexoPortMap pm : getPortMaps()) {
			 if (pm.getOperation().getPort() instanceof NewPort) returned.add(pm);
		 }
		 return returned;
	}

	public Vector<FlexoPortMap> getAllDeletePortmaps()
	{
		 Vector<FlexoPortMap> returned = new Vector<FlexoPortMap>();
		 for (FlexoPortMap pm : getPortMaps()) {
			 if (pm.getOperation().getPort() instanceof DeletePort) returned.add(pm);
		 }
		 return returned;
	}

	public Vector<FlexoPortMap> getAllOutPortmaps()
	{
		 Vector<FlexoPortMap> returned = new Vector<FlexoPortMap>();
		 for (FlexoPortMap pm : getPortMaps()) {
			 if (pm.getOperation().getPort() instanceof OutPort) returned.add(pm);
		 }
		 return returned;
	}

    public boolean getIsHidden()
    {
        return !getIsVisible(true);
    }

    public void setIsHidden(boolean hidePortMap)
    {
    	setIsVisible(!hidePortMap);
    }

 }
