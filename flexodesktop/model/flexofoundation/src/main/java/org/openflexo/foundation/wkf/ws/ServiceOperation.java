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
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ServiceOperationRemoved;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * a service operation represents a port (operation in the ws language) that we want to externalise
 * 
 * @author dvanvyve
 * 
 */
public class ServiceOperation extends WKFObject implements InspectableObject, LevelledObject, DeletableObject {

	private static final Logger logger = Logger.getLogger(ServiceOperation.class.getPackage().getName());

	private String _name;

	private ServiceInterface _interface;

	private FlexoPort _port;

	protected ServiceMessageDefinition _outputMessageDefinition;

	protected ServiceMessageDefinition _inputMessageDefinition;

	private ServiceInputMessageBindings _inputMessageBindings = null;

	private ServiceOutputMessageBindings _outputMessageBindings = null;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public ServiceOperation(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor used during deserialization
	 */
	public ServiceOperation(FlexoProcessBuilder builder) {
		this(builder.process);
		// builder.serviceOperation=this;
		initializeDeserialization(builder);
	}

	/**
	 * Constructor with process and name
	 */
	public ServiceOperation(ServiceInterface anInterface, String aName, FlexoPort aPort) throws DuplicateWKFObjectException {
		this(anInterface.getProcess());
		setServiceInterface(anInterface);
		setPort(aPort);
		setName(aName);
	}

	// ==========================================================================
	// ============================= Methods & accessors
	// ================================
	// ==========================================================================

	@Override
	public String getInspectorName() {
		if (isInOperation()) {
			return Inspectors.WKF.IN_SERVICE_OPERATION_INSPECTOR;
		} else if (isOutOperation()) {
			return Inspectors.WKF.OUT_SERVICE_OPERATION_INSPECTOR;
		} else if (isInOutOperation()) {
			return Inspectors.WKF.INOUT_SERVICE_OPERATION_INSPECTOR;
		}
		return Inspectors.WKF.SERVICE_OPERATION_INSPECTOR;
	}

	public ServiceInterface getServiceInterface() {
		return _interface;
	}

	public void setServiceInterface(ServiceInterface anInterface) {
		_interface = anInterface;
	}

	/**
	 * 
	 * @return true if associated Port is an IN port only, AND NOT an OUT port or IN/OUT port.
	 */
	public boolean isInOperation() {
		if (getPort() != null) {
			return getPort().isInPort() && !getPort().isOutPort();
		}
		return false;
	}

	/**
	 * 
	 * @return true if associated Port is an OUT port only, and NOT an IN port or IN/OUT port
	 */
	public boolean isOutOperation() {
		if (getPort() != null) {
			return !getPort().isInPort() && getPort().isOutPort();
		}
		return false;
	}

	/**
	 * 
	 * @return true if associated port is an IN OUT port.
	 */
	public boolean isInOutOperation() {
		if (getPort() != null) {
			return getPort().isInPort() && getPort().isOutPort();
		}
		return false;
	}

	public boolean isInputOperation() {
		// same as port.isInputPort
		return isInOperation() || isInOutOperation();
	}

	public boolean isOutputOperation() {
		return isOutOperation() || isInOutOperation();
	}

	public FlexoProcess getFlexoProcess() {
		return getServiceInterface().getProcess();
	}

	public FlexoPort getPort() {
		return _port;
	}

	public void setPort(FlexoPort aPort) {
		_port = aPort;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) throws DuplicateWKFObjectException {

		if (!isDeserializing()) {
			checkOperationName(getServiceInterface(), aName, this);
		}
		_name = aName;
		setChanged();
		notifyObservers(new NameChanged(null, _name));
	}

	/*
	 * String oldValue = getName(); if ((oldValue == null) || (!oldValue.equals(aName))) { try {
	 * checkOperationName(getServiceInterface(),aName, this); _name=aName; } catch (DuplicateWKFObjectException e) {
	 * 
	 * setName(getProcess().findNextInitialName(getPrefixForFullQualifiedName(), aName)); } } }
	 */

	private String getPrefixForFullQualifiedName() {
		if (isInOperation()) {
			return "IN_OP";
		} else if (isOutOperation()) {
			return "OUT_OP";
		} else if (isInOutOperation()) {
			return "INOUT_OP";
		} else {
			return "UNTYPED_OPERATION";
		}
	}

	@Override
	public String getFullyQualifiedName() {
		if (getServiceInterface() != null) {
			return getServiceInterface().getFullyQualifiedName() + "." + getPrefixForFullQualifiedName() + "." + formattedString(getName());
		} else {
			return "NULL." + formattedString(getName());
		}
	}

	private static String formattedString(String s) {
		if (s != null) {
			s = s.replaceAll(" ", "");
		}
		return s;
	}

	public static void checkOperationName(ServiceInterface serviceInterface, String aName, ServiceOperation currentOp)
			throws DuplicateWKFObjectException {
		Vector allOperations = serviceInterface.getOperations();
		for (Enumeration e = allOperations.elements(); e.hasMoreElements();) {
			ServiceOperation p = (ServiceOperation) e.nextElement();
			if (p != currentOp && p.getName().equals(aName)) {
				throw new DuplicateWKFObjectException(p, "service_operation_with_the_same_name_already_exist");
			}
		}
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.PROCESS;
	}

	public ServiceMessageDefinition getInputMessageDefinition() {
		if (isInOperation() || isInOutOperation()) {
			if (_inputMessageDefinition == null) {
				_inputMessageDefinition = new ServiceMessageDefinition(getProcess(), this);
				_inputMessageDefinition.setIsInputMessageDefinition();
				_inputMessageDefinition.setName(_inputMessageDefinition.getDefaultName());
			}
			return _inputMessageDefinition;
		}
		return null;
	}

	public void setInputMessageDefinition(ServiceMessageDefinition aMessageDefinition) {
		// conditions isInOperation etc can be incoherent during Deserialization !
		if (isDeserializing() || isInOperation() || isInOutOperation()) {
			if (aMessageDefinition != _inputMessageDefinition) {
				if (aMessageDefinition != null) {
					aMessageDefinition.setOperation(this);
				}

				_inputMessageDefinition = aMessageDefinition;
				if (_inputMessageDefinition != null) {
					_inputMessageDefinition.setIsInputMessageDefinition();
					_inputMessageDefinition.setName(_inputMessageDefinition.getDefaultName());
				}
				setChanged();
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to set an input message definition on an OUT ServiceOperation !");
			}
		}
	}

	public ServiceMessageDefinition getOutputMessageDefinition() {
		if (isOutOperation() || isInOutOperation()) {
			if (_outputMessageDefinition == null) {
				_outputMessageDefinition = new ServiceMessageDefinition(getProcess(), this);
				_outputMessageDefinition.setIsOutputMessageDefinition();
				_outputMessageDefinition.setName(_outputMessageDefinition.getDefaultName());

			}
			return _outputMessageDefinition;
		}
		return null;
	}

	public void setOutputMessageDefinition(ServiceMessageDefinition aMessageDefinition) {
		if (isDeserializing() || isOutOperation() || isInOutOperation()) {
			if (aMessageDefinition != _outputMessageDefinition) {
				if (aMessageDefinition != null) {
					aMessageDefinition.setOperation(this);
				}

				_outputMessageDefinition = aMessageDefinition;
				if (_outputMessageDefinition != null) {
					_outputMessageDefinition.setIsOutputMessageDefinition();
					_outputMessageDefinition.setName(_outputMessageDefinition.getDefaultName());
				}
				setChanged();
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to set an output message definition on an IN ServiceOperation !");
			}
		}
	}

	public ServiceInputMessageBindings getInputMessageBindings() {
		if (isInputOperation() && _inputMessageBindings == null && ((AbstractInPort) getPort()).getInputMessageDefinition() != null) {
			_inputMessageBindings = new ServiceInputMessageBindings(this, ((AbstractInPort) getPort()).getInputMessageDefinition());
		}
		return _inputMessageBindings;
	}

	public void setInputMessageBindings(ServiceInputMessageBindings messageBindings) {
		if (messageBindings != null) {
			messageBindings.setMessageDefinition(((AbstractInPort) getPort()).getInputMessageDefinition());
			messageBindings.setServiceOperation(this);
		}
		_inputMessageBindings = messageBindings;
	}

	public ServiceOutputMessageBindings getOutputMessageBindings() {
		if (isOutputOperation() && _outputMessageBindings == null) {
			_outputMessageBindings = new ServiceOutputMessageBindings(this, getOutputMessageDefinition());
		}
		return _outputMessageBindings;
	}

	public void setOutputMessageBindings(ServiceOutputMessageBindings messageBindings) {
		if (messageBindings != null) {
			messageBindings.setMessageDefinition(getOutputMessageDefinition());
			messageBindings.setServiceOperation(this);
		}
		_outputMessageBindings = messageBindings;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public void delete() {
		if (_inputMessageBindings != null) {
			_inputMessageBindings.delete();
		}
		if (_outputMessageBindings != null) {
			_outputMessageBindings.delete();
		}
		if (_outputMessageDefinition != null) {
			_outputMessageDefinition.delete();
		}
		if (_inputMessageDefinition != null) {
			_inputMessageDefinition.delete();
		}

		if (getServiceInterface() != null) {
			getServiceInterface().removeFromOperations(this);
		}

		super.delete();
		setChanged();
		notifyObservers(new ServiceOperationRemoved(this));
		deleteObservers();
		_inputMessageBindings = null;
		_outputMessageBindings = null;
		_outputMessageDefinition = null;
		_inputMessageDefinition = null;
		_name = null;
		_interface = null;
		_port = null;
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	public String getLocalizedLabel() {
		if (isInOperation()) {
			return FlexoLocalization.localizedForKey("in_operation");
		} else if (isInOutOperation()) {
			return FlexoLocalization.localizedForKey("in_out_operation");
		} else if (isOutOperation()) {
			return FlexoLocalization.localizedForKey("out_operation");
		} else {
			return null;
		}
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		if (getInputMessageBindings() != null) {
			returned.add(getInputMessageBindings());
		}
		if (getOutputMessageBindings() != null) {
			returned.add(getOutputMessageBindings());
		}
		if (getInputMessageDefinition() != null) {
			returned.add(getInputMessageDefinition());
		}
		if (getOutputMessageDefinition() != null) {
			returned.add(getOutputMessageDefinition());
		}

		return returned;
	}

	@Override
	public String getClassNameKey() {
		return "service_operation";
	}

	public String getTabInputBindingDescription() {
		return FlexoLocalization.localizedForKey("service_operation_inputBinding_description");
	}

	public String getTabOutputBindingDescription() {
		return FlexoLocalization.localizedForKey("service_operation_outputBinding_description");
	}

	public String getTabInputOutputBindingDescription() {
		return FlexoLocalization.localizedForKey("service_operation_input-outputBinding_description");
	}

}
