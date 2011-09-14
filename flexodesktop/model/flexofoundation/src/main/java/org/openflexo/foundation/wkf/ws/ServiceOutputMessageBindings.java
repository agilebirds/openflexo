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

import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * container for bindings in a ServiceOperation. a ServiceMessageBindings is roughly a Vector of ServiceMessageEntryBindings So containing
 * bindings related to 1 MessageDefinition, either an InputMessageDefinition, or an OutputMessageDefinition.
 * 
 * the ServiceOperation has virtually 2 binding models: one for the inputMessage, one for the outputMessage Consequently, we define the
 * binding model in the ServiceOperation but this container is the Bindable object.
 * 
 * 
 * For an IN Operation, a binding defines for a variable 'a_pt' in the related Port's input message, the corresponding variable 'e_op' in
 * the Operation's input message: a_pt= f(e_op)
 * 
 * For an OUT Operation, a binding defines for a variable 'd_op' in the operation's output message, the corresponding variable 'b_pt' in the
 * related Port's output message: d_op = f(a_pt)
 * 
 * 
 * 
 * @author dvanvyve
 * 
 */
public class ServiceOutputMessageBindings extends ServiceMessageBindings implements InspectableObject, Bindable {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ServiceOutputMessageBindings.class.getPackage().getName());

	// MessageDefinition from the Port's Input Message
	// private AbstractMessageDefinition _messageDefinition;

	// private Vector _bindings;

	/**
	 * Constructor used during deserialization
	 */
	public ServiceOutputMessageBindings(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ServiceOutputMessageBindings(FlexoProcess process) {
		super(process);
		_bindings = new Vector();
	}

	/**
	 * Dynamic constructor
	 */
	public ServiceOutputMessageBindings(ServiceOperation operation, ServiceMessageDefinition operationMessage) {
		this(operation.getProcess());
		setServiceOperation(operation);
		setMessageDefinition(operationMessage);
	}

	@Override
	public BindingModel getBindingModel() {
		return getOutputBindingModel();
	}

	private BindingModel outputBindingModel;

	/**
	 * d_op = f(a_pt) => binding model is based on port's message definition
	 * 
	 * @return
	 */
	private BindingModel getOutputBindingModel() {
		if (outputBindingModel == null) {// && getServiceOperation()!=null) {
			outputBindingModel = new DynamicOutputBindingModel();
		}
		return outputBindingModel;
	}

	public class DynamicOutputBindingModel extends DynamicBindingModel {
		// Vector bindingVariables = vector of MessageEntries. of ServiceMessageDefinition !!!
		// constructed dynamically from MessageDefinition.

		public DynamicOutputBindingModel() {
			super();

		}

		@Override
		protected AbstractMessageDefinition getMessageDefinition() {
			if (getServiceOperation() != null) {
				if (getServiceOperation().getPort() instanceof OutputPort) {
					return ((OutputPort) getServiceOperation().getPort()).getOutputMessageDefinition();
				}
			}
			return null;
		}

		@Override
		public boolean allowsNewBindingVariableCreation() {
			return true;
		}

		/**
		 * the variablesTable contains only BindingVariables that have been requested by the 'getBidingVariableAt' method.
		 * 
		 * @param entry
		 * @return
		 */
		@Override
		protected BindingVariable addBindingVariableForEntry(MessageEntry entry) {
			BindingVariable var = new BindingVariable(ServiceOutputMessageBindings.this, getProject().getDataModel(), FlexoLocalization
					.localizedForKey("access_to_message_value"));
			var.setVariableName(entry.getVariableName());
			var.setType(entry.getType());
			variablesTable.put(entry, var);
			return var;
		}

	}

	public MessageDefinition getPortMessageDefinition() {
		return (MessageDefinition) getMessageDefinition();
	}

	@Override
	public void delete() {

		getServiceOperation().setOutputMessageBindings(null);
		outputBindingModel = null;
		super.delete();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "service_input_message_bindings";
	}

}
