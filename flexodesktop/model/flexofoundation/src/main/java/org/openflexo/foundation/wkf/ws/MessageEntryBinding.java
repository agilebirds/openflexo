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

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class MessageEntryBinding extends FlexoModelObject implements InspectableObject, Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MessageBindings.class.getPackage().getName());

	private MessageBindings _messageBindings;

	private MessageEntry _bindingDefinition;

	private String _bindingDefinitionName;

	private AbstractBinding _bindingValue;

	/**
	 * Constructor used by XMLCoDe in the context of process deserialization
	 * 
	 * @param builder
	 */
	public MessageEntryBinding(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * Constructor used by XMLCoDe in the context of component deserialization
	 * 
	 * @param builder
	 */
	public MessageEntryBinding(FlexoComponentBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	public MessageEntryBinding(MessageBindings messageBindings, MessageEntry bindingDefinition) {
		super(messageBindings.getProject());
		setMessageBindings(messageBindings);
		setBindingDefinition(bindingDefinition);
	}

	public MessageEntryBinding(MessageBindings messageBindings, MessageEntry bindingDefinition, BindingValue value) {
		this(messageBindings, bindingDefinition);
		setBindingValue(value);
	}

	@Override
	public BindingModel getBindingModel() {

		if (_messageBindings != null)
			return _messageBindings.getBindingModel();
		return null;
	}

	public MessageEntry getBindingDefinition() {
		lookupBindingDefinition();
		return _bindingDefinition;
	}

	public void lookupBindingDefinition() {
		// logger.info("lookupMessageDefinition() for "+getBindingDefinitionName()+" "+getBindingValue());
		// logger.info("_bindingDefinition="+_bindingDefinition);
		// logger.info("_messageBindings="+_messageBindings);
		// if (_messageBindings!=null) logger.info("_messageBindings.getMessageDefinition()="+_messageBindings.getMessageDefinition());
		// logger.info("_bindingDefinitionName="+_bindingDefinitionName);
		if ((_bindingDefinition == null) && (_messageBindings != null) && (_messageBindings.getMessageDefinition() != null)
				&& (_bindingDefinitionName != null)) {
			_bindingDefinition = _messageBindings.getMessageDefinition().entryWithName(_bindingDefinitionName);
			if (getBindingValue() != null) {
				if (getBindingValue().getBindingDefinition() == null) {
					getBindingValue().setBindingDefinition(_bindingDefinition);
				}
			}
		}
	}

	public void setBindingDefinition(MessageEntry messageEntry) {
		_bindingDefinition = messageEntry;
		if (_bindingValue != null) {
			_bindingValue.setBindingDefinition(messageEntry);
		}
		setChanged();
	}

	public String getBindingDefinitionName() {
		if (_bindingDefinition != null) {
			return _bindingDefinition.getVariableName();
		}
		return _bindingDefinitionName;
	}

	public void setBindingDefinitionName(String bindingDefinitionName) {
		_bindingDefinitionName = bindingDefinitionName;
		if (_messageBindings != null) {
			setBindingDefinition(_messageBindings.getMessageDefinition().entryWithName(_bindingDefinitionName));
		}
		setChanged();
	}

	public MessageBindings getMessageBindings() {
		return _messageBindings;
	}

	public void setMessageBindings(MessageBindings messageBindings) {
		_messageBindings = messageBindings;
		if ((_bindingValue != null) && (getMessageEdge() != null)) {
			_bindingValue.setOwner(getMessageEdge());
		}
		if ((_messageBindings != null) && (_messageBindings.getMessageDefinition() != null)) {
			if (_bindingDefinitionName != null) {
				setBindingDefinition(_messageBindings.getMessageDefinition().entryWithName(_bindingDefinitionName));
			}
		}
	}

	public MessageEdge getMessageEdge() {
		return getMessageBindings().getMessageEdge();
	}

	public AbstractBinding getBindingValue() {
		return _bindingValue;
	}

	public void setBindingValue(AbstractBinding value) {
		_bindingValue = value;
		if (_bindingValue != null) {
			if (_bindingValue.getBindingDefinition() == null) {
				_bindingValue.setBindingDefinition(getBindingDefinition());
			}
		}
		setChanged();
	}

	@Override
	public FlexoProject getProject() {
		if (_messageBindings != null)
			return _messageBindings.getProject();
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return (_messageBindings != null ? _messageBindings.getFullyQualifiedName() : "null") + "." + getBindingDefinitionName() + "="
				+ (getBindingValue() == null ? "null" : getBindingValue().getStringRepresentation());
	}

	@Override
	public XMLMapping getXMLMapping() {
		if (_messageBindings != null)
			return _messageBindings.getXMLMapping();
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (_messageBindings != null)
			return _messageBindings.getXMLResourceData();
		return null;
	}

	@Override
	public String getInspectorName() {
		// never inspected by its own
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "message_entry_binding";
	}

}
