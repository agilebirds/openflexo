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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;


/**
 * 
 * Used to store port's message definition
 * 
 * @author sguerin
 */
public class ServiceMessageDefinition extends AbstractMessageDefinition implements InspectableObject {

	private static final Logger logger = Logger.getLogger(ServiceMessageDefinition.class.getPackage().getName());

	private ServiceOperation _operation;

	/**
	 * Constructor used during deserialization
	 */
	public ServiceMessageDefinition(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ServiceMessageDefinition(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor
	 */
	public ServiceMessageDefinition(FlexoProcess process, ServiceOperation operation) {
		this(process);
		setOperation(operation);
	}

	public ServiceOperation getOperation() {
		return _operation;
	}

	public void setOperation(ServiceOperation operation) {
		_operation = operation;
	}

	@Override
	public WKFObject getFatherObject() {
		return getOperation();
	}

	@Override
	public void delete() {
		if (isInputMessageDefinition())
			getOperation().setInputMessageDefinition(null);
		if (isOutputMessageDefinition())
			getOperation().setOutputMessageDefinition(null);
		if (isFaultMessageDefinition()) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("implement delete for fault message definition");
		}
		super.delete();
		deleteObservers();
		_operation = null;
	}

	@Override
	public String getClassNameKey() {
		return "service_message_definition";
	}

}
