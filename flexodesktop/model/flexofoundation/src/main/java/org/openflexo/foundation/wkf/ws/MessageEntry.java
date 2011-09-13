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

import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * 
 * Used to store port's message definition entry
 * 
 * @author sguerin
 */
public class MessageEntry extends BindingDefinition {

	private AbstractMessageDefinition _message;

	/**
	 * Constructor used during deserialization
	 */
	public MessageEntry(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public MessageEntry(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor
	 */
	public MessageEntry(FlexoProcess process, AbstractMessageDefinition message) {
		this(process);
		setMessage(message);
	}

	@Override
	public String getFullyQualifiedName() {
		if (getMessage() != null)
			return getMessage().getFullyQualifiedName() + ".MESSAGE";
		return null;
	}

	public AbstractMessageDefinition getMessage() {
		return _message;
	}

	public void setMessage(AbstractMessageDefinition message) {
		_message = message;
	}

	public String getTypeClassName() {
		if (getType() != null && getType().getBaseEntity() != null)
			return getType().getBaseEntity().getFullyQualifiedName();
		return null;
	}

	public void setTypeClassName(String className) {
		if (className != null)
			setType(DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity(className)));
		setChanged();
	}

}
