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

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;

/**
 * Abstract representation of a IN port associated to a PortRegistery associated to a FlexoProcess: such a port receive message and could be
 * of type IN (normal), NEW (new instance) or DELETE (delete instance)
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractInPort extends FlexoPort {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractInPort.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	/**
	 * Stores the input message definition
	 */
	private MessageDefinition _inputMessageDefinition;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public AbstractInPort(FlexoProcess process) {
		super(process);
	}

	@Override
	public abstract String getDefaultName();

	@Override
	public abstract String getInspectorName();

	@Override
	public boolean isInPort() {
		return true;
	}

	@Override
	public boolean isOutPort() {
		return false;
	}

	public MessageDefinition getInputMessageDefinition() {
		if (_inputMessageDefinition == null) {
			_inputMessageDefinition = new MessageDefinition(getProcess(), this);
			_inputMessageDefinition.setIsInputMessageDefinition();
			_inputMessageDefinition.setName(_inputMessageDefinition.getDefaultName());
		}
		return _inputMessageDefinition;
	}

	public void setInputMessageDefinition(MessageDefinition aMessageDefinition) {
		if (aMessageDefinition != _inputMessageDefinition) {
			if (aMessageDefinition != null) {
				aMessageDefinition.setPort(this);
			}

			_inputMessageDefinition = aMessageDefinition;

			if (_inputMessageDefinition != null) {
				_inputMessageDefinition.setIsInputMessageDefinition();
			}
			setChanged();
		}
	}

	@Override
	public boolean mayHaveOutgoingPostConditions() {
		return true;
	}

	@Override
	public boolean mayHaveIncomingPostConditions() {
		return false;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

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

}
