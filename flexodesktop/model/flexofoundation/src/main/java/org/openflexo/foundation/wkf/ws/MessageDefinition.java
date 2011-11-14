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
public class MessageDefinition extends AbstractMessageDefinition implements InspectableObject {

	private static final Logger logger = Logger.getLogger(MessageDefinition.class.getPackage().getName());

	private FlexoPort _port;

	/**
	 * Constructor used during deserialization
	 */
	public MessageDefinition(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public MessageDefinition(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor
	 */
	public MessageDefinition(FlexoProcess process, FlexoPort port) {
		this(process);
		setPort(port);
	}

	public FlexoPort getPort() {
		return _port;
	}

	public void setPort(FlexoPort aPort) {
		_port = aPort;
	}

	@Override
	public WKFObject getFatherObject() {
		return getPort();
	}

	@Override
	public void delete() {
		if (isInputMessageDefinition()) {
			((AbstractInPort) getPort()).setInputMessageDefinition(null);
		}
		if (isOutputMessageDefinition()) {
			((OutputPort) getPort()).setOutputMessageDefinition(null);
		}
		if (isFaultMessageDefinition()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("implement delete for fault message definition");
			}
		}
		super.delete();
		_port = null;

	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "message_definition";
	}

}
