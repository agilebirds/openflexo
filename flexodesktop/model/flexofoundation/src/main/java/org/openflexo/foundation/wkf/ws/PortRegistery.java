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

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PortInserted;
import org.openflexo.foundation.wkf.dm.PortRemoved;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * A PortRegistery is attached to a FlexoProcess and contains all the ports used in the context of SubProcesses
 * 
 * @author sguerin
 * 
 */
public final class PortRegistery extends WKFObject implements InspectableObject, LevelledObject, DeletableObject {

	private static final Logger logger = Logger.getLogger(PortRegistery.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private Vector<FlexoPort> _newPorts;
	private Vector<FlexoPort> _deletePorts;
	private Vector<FlexoPort> _inPorts;
	private Vector<FlexoPort> _inOutPorts;
	private Vector<FlexoPort> _outPorts;

	// protected boolean _isVisible = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public PortRegistery(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public PortRegistery(FlexoProcess process) {
		super(process);
		_newPorts = new Vector<FlexoPort>();
		_deletePorts = new Vector<FlexoPort>();
		_inPorts = new Vector<FlexoPort>();
		_inOutPorts = new Vector<FlexoPort>();
		_outPorts = new Vector<FlexoPort>();
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".PORT_REGISTERY";
	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey("port_registery");
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		returned.add(this);
		for (FlexoPort p : getAllPorts()) {
			returned.addAll(p.getAllEmbeddedWKFObjects());
		}
		/*returned.addAll(getNewPorts());
		returned.addAll(getDeletePorts());
		returned.addAll(getInPorts());
		returned.addAll(getInOutPorts());
		returned.addAll(getOutPorts());*/
		return returned;
	}

	public void addToPorts(FlexoPort aPort) {
		if (aPort instanceof NewPort) {
			addToNewPorts((NewPort) aPort);
		} else if (aPort instanceof DeletePort) {
			addToDeletePorts((DeletePort) aPort);
		} else if (aPort instanceof InPort) {
			addToInPorts((InPort) aPort);
		} else if (aPort instanceof InOutPort) {
			addToInOutPorts((InOutPort) aPort);
		} else if (aPort instanceof OutPort) {
			addToOutPorts((OutPort) aPort);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected value in addToPorts()");
			}
		}
	}

	public void removeFromPorts(FlexoPort aPort) {
		if (aPort instanceof NewPort) {
			removeFromNewPorts((NewPort) aPort);
		} else if (aPort instanceof DeletePort) {
			removeFromDeletePorts((DeletePort) aPort);
		} else if (aPort instanceof InPort) {
			removeFromInPorts((InPort) aPort);
		} else if (aPort instanceof InOutPort) {
			removeFromInOutPorts((InOutPort) aPort);
		} else if (aPort instanceof OutPort) {
			removeFromOutPorts((OutPort) aPort);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected value in removeFromPorts()");
			}
		}
	}

	private boolean insertPort(Vector<FlexoPort> portList, FlexoPort aPort) {
		if (!portList.contains(aPort)) {
			portList.add(aPort);
			aPort.setPortRegistery(this);
			if (!isDeserializing()) {
				// aPort.setLocation(new Point((getAllPorts().size()-1) * 80 + 20, 20));
			}
			setChanged();
			notifyObservers(new PortInserted(aPort));
			if (getProcess() != null) {
				getProcess().clearCachedObjects();
			}
			return true;
		} else {
			return false;
		}
	}

	private void removePort(Vector portList, FlexoPort aPort) {
		if (portList.contains(aPort)) {
			portList.remove(aPort);
			setChanged();
			notifyObservers(new PortRemoved(aPort));
			aPort.setPortRegistery(null);
			if (getProcess() != null) {
				getProcess().clearCachedObjects();
			}
		}
	}

	public Vector<FlexoPort> getDeletePorts() {
		return _deletePorts;
	}

	public void setDeletePorts(Vector<FlexoPort> ports) {
		_deletePorts = ports;
	}

	public void addToDeletePorts(DeletePort aPort) {
		insertPort(_deletePorts, aPort);
	}

	public void removeFromDeletePorts(DeletePort aPort) {
		removePort(_deletePorts, aPort);
	}

	public Vector<FlexoPort> getInPorts() {
		return _inPorts;
	}

	public void setInPorts(Vector<FlexoPort> ports) {
		_inPorts = ports;
	}

	public void addToInPorts(InPort aPort) {
		insertPort(_inPorts, aPort);
	}

	public void removeFromInPorts(InPort aPort) {
		removePort(_inPorts, aPort);
	}

	public Vector<FlexoPort> getInOutPorts() {
		return _inOutPorts;
	}

	public void setInOutPorts(Vector<FlexoPort> ports) {
		_inOutPorts = ports;
	}

	public void addToInOutPorts(InOutPort aPort) {
		insertPort(_inOutPorts, aPort);
	}

	public void removeFromInOutPorts(InOutPort aPort) {
		removePort(_inOutPorts, aPort);
	}

	public Vector<FlexoPort> getNewPorts() {
		return _newPorts;
	}

	public void setNewPorts(Vector<FlexoPort> ports) {
		_newPorts = ports;
	}

	public void addToNewPorts(NewPort aPort) {
		insertPort(_newPorts, aPort);
	}

	public void removeFromNewPorts(NewPort aPort) {
		removePort(_newPorts, aPort);
	}

	public Vector<FlexoPort> getOutPorts() {
		return _outPorts;
	}

	public void setOutPorts(Vector<FlexoPort> ports) {
		_outPorts = ports;
	}

	public void addToOutPorts(OutPort aPort) {
		insertPort(_outPorts, aPort);
	}

	public void removeFromOutPorts(OutPort aPort) {
		removePort(_outPorts, aPort);
	}

	public FlexoPort portWithName(String name) {
		for (Enumeration e = getNewPorts().elements(); e.hasMoreElements();) {
			FlexoPort port = (FlexoPort) e.nextElement();
			if (port.getName().equals(name)) {
				return port;
			}
		}
		for (Enumeration e = getDeletePorts().elements(); e.hasMoreElements();) {
			FlexoPort port = (FlexoPort) e.nextElement();
			if (port.getName().equals(name)) {
				return port;
			}
		}
		for (Enumeration e = getInPorts().elements(); e.hasMoreElements();) {
			FlexoPort port = (FlexoPort) e.nextElement();
			if (port.getName().equals(name)) {
				return port;
			}
		}
		for (Enumeration e = getInOutPorts().elements(); e.hasMoreElements();) {
			FlexoPort port = (FlexoPort) e.nextElement();
			if (port.getName().equals(name)) {
				return port;
			}
		}
		for (Enumeration e = getOutPorts().elements(); e.hasMoreElements();) {
			FlexoPort port = (FlexoPort) e.nextElement();
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	/**
	 * Return all contained ports
	 * 
	 * @return a Vector of FlexoPort
	 */
	public Vector<FlexoPort> getAllPorts() {
		Vector<FlexoPort> returned = new Vector<FlexoPort>();
		returned.addAll(getNewPorts());
		returned.addAll(getDeletePorts());
		returned.addAll(getInPorts());
		returned.addAll(getInOutPorts());
		returned.addAll(getOutPorts());
		return returned;
	}

	public Enumeration<FlexoPort> getSortedPorts() {
		disableObserving();
		FlexoPort[] o = FlexoIndexManager.sortArray(getAllPorts().toArray(new FlexoPort[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.PORT_REGISTERY_INSPECTOR;
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.PORT;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "port_registry";
	}

}
