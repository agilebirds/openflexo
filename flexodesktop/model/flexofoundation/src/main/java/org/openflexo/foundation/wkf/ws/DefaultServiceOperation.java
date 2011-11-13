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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.inspector.InspectableObject;

/**
 * dynamic ServiceOperation that maps exactly on a port.
 * 
 * @author dvanvyve
 * 
 */
public class DefaultServiceOperation extends ServiceOperation implements InspectableObject, LevelledObject, DeletableObject {

	private static final Logger logger = Logger.getLogger(DefaultServiceOperation.class.getPackage().getName());

	/**
	 * Constructor with process and name
	 */
	public DefaultServiceOperation(DefaultServiceInterface anInterface, FlexoPort aPort) throws DuplicateWKFObjectException {
		super(anInterface, aPort.getName(), aPort);
	}

	@Override
	public String getName() {
		return getPort().getName();
	}

	@Override
	public void setName(String aName) {
		getPort().setName(aName);
	}

	@Override
	public ServiceMessageDefinition getInputMessageDefinition() {
		if (isInOperation() || isInOutOperation()) {
			if (_inputMessageDefinition == null) {
				_inputMessageDefinition = new DefaultServiceMessageDefinition(getProcess(), this,
						((AbstractInPort) getPort()).getInputMessageDefinition());
			}
			return _inputMessageDefinition;
		}
		return null;
	}

	@Override
	public void setInputMessageDefinition(ServiceMessageDefinition aMessageDefinition) {
		// not possible.
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Set MessageDefinition on a Default Service Operation !");
	}

	@Override
	public ServiceMessageDefinition getOutputMessageDefinition() {
		if (isOutOperation() || isInOutOperation()) {
			if (_outputMessageDefinition == null) {
				_outputMessageDefinition = new DefaultServiceMessageDefinition(getProcess(), this,
						((OutputPort) getPort()).getOutputMessageDefinition());
			}
			return _outputMessageDefinition;
		}
		return null;
	}

	@Override
	public void setOutputMessageDefinition(ServiceMessageDefinition aMessageDefinition) {
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Set MessageDefinition on a Default Service Operation !");
	}

	@Override
	public String getDescription() {
		return getPort().getDescription();
	}

	@Override
	public void setDescription(String a) {
		getPort().setDescription(a);
	}

	@Override
	public ServiceInputMessageBindings getInputMessageBindings() {
		// no BINDING
		return null;
	}

	@Override
	public void setInputMessageBindings(ServiceInputMessageBindings messageBindings) {
		// no BINDING
	}

	@Override
	public ServiceOutputMessageBindings getOutputMessageBindings() {
		return null;
	}

	@Override
	public void setOutputMessageBindings(ServiceOutputMessageBindings messageBindings) {
		// no binding
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {

		super.delete();

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

	public void update(FlexoObservable observable, DataModification dataModification) {

		// nothing
		// super.update(observable, dataModification);
	}

	@Override
	public String getClassNameKey() {
		return "default_service_operation";
	}

}
