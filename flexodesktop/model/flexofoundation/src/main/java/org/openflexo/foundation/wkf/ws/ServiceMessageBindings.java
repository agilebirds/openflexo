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
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.toolbox.EmptyVector;

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
public abstract class ServiceMessageBindings extends WKFObject implements Bindable, FlexoObserver {

	private static final Logger logger = Logger.getLogger(ServiceMessageBindings.class.getPackage().getName());

	protected ServiceOperation _serviceOperation;

	protected AbstractMessageDefinition _messageDefinition;

	protected Vector<ServiceMessageEntryBinding> _bindings;

	/**
	 * Constructor used during deserialization
	 */
	public ServiceMessageBindings(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ServiceMessageBindings(FlexoProcess process) {
		super(process);
		_bindings = new Vector<ServiceMessageEntryBinding>();
	}

	@Override
	public void initializeDeserialization(Object builder) {
		// setServiceOperation(((FlexoProcessBuilder)builder).serviceOperation);
		super.initializeDeserialization(builder);
	}

	@Override
	public abstract BindingModel getBindingModel();

	@Override
	public Vector getAllEmbeddedWKFObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInspectorName() {
		// Never inspected by its own
		return null;
	}

	public AbstractMessageDefinition getMessageDefinition() {
		return _messageDefinition;
	}

	public void setMessageDefinition(AbstractMessageDefinition messageDefinition) {
		if (messageDefinition != _messageDefinition) {
			if (_messageDefinition != null) {
				_messageDefinition.deleteObserver(this);
			}
			_messageDefinition = messageDefinition;
			if (_messageDefinition != null) {
				_messageDefinition.addObserver(this);
			}
			lookupBindingEntries();
		}
	}

	private void lookupBindingEntries() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("lookup Binding " + getBindings().size() + " Entries of Message " + getMessageDefinition().getName());
		}

		for (Enumeration<ServiceMessageEntryBinding> en = getBindings().elements(); en.hasMoreElements();) {
			ServiceMessageEntryBinding next = en.nextElement();
			next.lookupBindingDefinition();

		}
	}

	public ServiceOperation getServiceOperation() {
		return _serviceOperation;
	}

	public void setServiceOperation(ServiceOperation op) {
		_serviceOperation = op;
		// the owner of a BindingValue is this class and not the ServiceOperation
		/*
		 * for (Enumeration en = getBindings().elements(); en.hasMoreElements();) { ServiceMessageEntryBinding next =
		 * (ServiceMessageEntryBinding) en.nextElement(); if (next.getBindingValue() != null) { next.getBindingValue().setOwner(op); } }
		 */
	}

	public void addToBindings(ServiceMessageEntryBinding value) {
		value.setMessageBindings(this);
		_bindings.add(value);
	}

	public void removeFromBindings(ServiceMessageEntryBinding value) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Removing Binding in ServiceMessageBinding:" + value);
		}
		value.setMessageBindings(null);
		_bindings.remove(value);
	}

	public void setBindings(Vector<ServiceMessageEntryBinding> value) {
		_bindings = value;
	}

	public Vector<ServiceMessageEntryBinding> getBindings() {
		if ((_bindings != null) && (getMessageDefinition() != null) && (_bindings.size() != getMessageDefinition().getEntries().size())) {
			updateBindings();
		}
		return _bindings;
	}

	private boolean isRegistered(MessageEntry bd) {
		for (Enumeration<ServiceMessageEntryBinding> en = _bindings.elements(); en.hasMoreElements();) {
			ServiceMessageEntryBinding next = en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return true;
			}
		}
		return false;
	}

	public ServiceMessageEntryBinding getBinding(MessageEntry bd) {
		for (Enumeration<ServiceMessageEntryBinding> en = _bindings.elements(); en.hasMoreElements();) {
			ServiceMessageEntryBinding next = en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return next;
			}
		}
		return null;
	}

	private void updateBindings() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateBindings() in ServiceMessageBindings");
		}
		Vector toRemove = new Vector();
		toRemove.addAll(_bindings);
		for (Enumeration en = getMessageDefinition().getEntries().elements(); en.hasMoreElements();) {
			MessageEntry next = (MessageEntry) en.nextElement();
			if (!isRegistered(next)) {
				addToBindings(new ServiceMessageEntryBinding(this, next, null));
			} else {
				toRemove.remove(getBinding(next));
			}
		}
		for (Enumeration en = toRemove.elements(); en.hasMoreElements();) {
			ServiceMessageEntryBinding next = (ServiceMessageEntryBinding) en.nextElement();
			removeFromBindings(next);
		}
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("received update in ServiceMessageBindings " + dataModification);
		}
		if ((o == getMessageDefinition()) && ((dataModification instanceof BindingAdded) || (dataModification instanceof BindingRemoved))) {
			updateBindings();
		}
	}

	@Override
	public void delete() {

		// getServiceOperation().setInputMessageBindings(null);
		// getServiceOperation().setOutputMessageBindings()

		Enumeration<ServiceMessageEntryBinding> en = new Vector<ServiceMessageEntryBinding>(_bindings).elements();
		while (en.hasMoreElements()) {
			ServiceMessageEntryBinding binding = en.nextElement();
			deleteBinding(binding);
		}
		_messageDefinition.delete();
		_serviceOperation = null;
		_messageDefinition = null;
		_bindings.clear();
		_bindings = null;
		super.delete();
		setChanged();
		// notifyObservers(new ServiceOperationRemoved(this));
		deleteObservers();

	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE1: that this is NOT a recursive method NOTE2: return null, since there is no
	 * embedded IEObject
	 * 
	 * @return null
	 */
	/*
	 * public Vector getEmbeddedIEObjects() { return new Vector(); }
	 */

	public ServiceMessageEntryBinding createNewBinding() {
		if (getMessageDefinition() != null) {
			MessageEntry newBD = getMessageDefinition().createNewMessageEntry();
			return getBinding(newBD);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not create binding: cannot access MessageDefinition !");
			}
			return null;
		}
	}

	public void deleteBinding(ServiceMessageEntryBinding meb) {
		if (getMessageDefinition() != null) {
			getMessageDefinition().deleteMessageEntry(meb.getBindingDefinition());

		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not delete binding: cannot access MessageDefinition !");
			}
		}
	}

	public boolean isBindingDeletable(ServiceMessageEntryBinding meb) {
		if (getMessageDefinition() != null) {
			return getMessageDefinition().isMessageEntryDeletable(meb.getBindingDefinition());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access binding: cannot access ComponentDefinition !");
			}
		}
		return false;
	}

	public abstract class DynamicBindingModel extends BindingModel {
		// Vector bindingVariables = vector of MessageEntries. of ServiceMessageDefinition !!!
		// constructed dynamically from MessageDefinition.

		@SuppressWarnings("hiding")
		private final Logger logger = Logger.getLogger(ServiceMessageBindings.DynamicBindingModel.class.getPackage().getName());

		protected Hashtable variablesTable;

		public DynamicBindingModel() {
			super();
			variablesTable = new Hashtable();
		}

		protected abstract AbstractMessageDefinition getMessageDefinition();

		@Override
		public int getBindingVariablesCount() {
			if (getMessageDefinition() != null && getMessageDefinition().getEntries() != null) {
				return getMessageDefinition().getEntries().size();
			}
			return 0;
		}

		/**
		 * the variablesTable contains only BindingVariables that have been requested by the 'getBidingVariableAt' method.
		 * 
		 * @param entry
		 * @return
		 */
		protected abstract BindingVariable addBindingVariableForEntry(MessageEntry entry);

		@Override
		public BindingVariable getBindingVariableAt(int index) {
			if (getMessageDefinition() != null && getMessageDefinition().getEntries() != null) {
				MessageEntry entry = getMessageDefinition().getEntries().elementAt(index);
				BindingVariable var = (BindingVariable) variablesTable.get(entry);
				// var=null;//to force recreating the var.

				if (var == null) {
					var = addBindingVariableForEntry(entry);
				} else {
					// apply changes if the entry have changed !
					var.setVariableName(entry.getVariableName());
					var.setType(entry.getType());
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Binding Model Variable:" + var.getVariableName() + " " + var);
				}
				return var;
			}
			return null;
		}

		@Override
		public BindingVariable bindingVariableNamed(String variableName) {
			for (int i = 0; i < getBindingVariablesCount(); i++) {
				BindingVariable next = getBindingVariableAt(i);
				if (next.getVariableName() != null && next.getVariableName().equals(variableName)) {
					return next;
				}
			}
			return null;
		}
	}

	// ==========================================================================
	// ============================== Validation
	// ================================
	// ==========================================================================
	/*
	 * public static class DefinedBindingsMustBeValid extends CheckAllBindingsRule { public DefinedBindingsMustBeValid() {
	 * super("defined_bindings_must_be_valid"); }
	 * 
	 * public ValidationIssue applyValidation(final Validable object) { CompoundIssue errors = null; final ServiceMessageBindings bindings =
	 * (ServiceMessageBindings) object; Enumeration en = bindings.getBindings().elements(); while (en.hasMoreElements()) {
	 * ServiceMessageEntryBinding binding = (ServiceMessageEntryBinding) en.nextElement(); BindingValue bv = binding.getBindingValue(); if
	 * ((bv != null) && (!bv.isBindingValid())) { ValidationError error; error = new MissingRequiredBinding(bindings, binding) { public
	 * String getLocalizedMessage() { return getLocalizedErrorMessageForInvalidValue(); } }; if (errors == null) { errors = new
	 * CompoundIssue(object); } errors.addToContainedIssues(error); } } return errors; }
	 * 
	 * }
	 * 
	 * public static class MandatoryBindingsMustHaveAValue extends CheckAllBindingsRule { public MandatoryBindingsMustHaveAValue() {
	 * super("mandatory_bindings_must_have_a_value"); }
	 * 
	 * public ValidationIssue applyValidation(final Validable object) { CompoundIssue errors = null; final ServiceMessageBindings bindings =
	 * (ServiceMessageBindings) object; Enumeration en = bindings.getBindings().elements(); while (en.hasMoreElements()) {
	 * ServiceMessageEntryBinding binding = (ServiceMessageEntryBinding) en.nextElement(); if
	 * (binding.getBindingDefinition().getIsMandatory()) { BindingValue bv = binding.getBindingValue(); if ((bv == null) ||
	 * (!bv.isBindingValid())) { ValidationError error; if (bv == null) { error = new MissingRequiredBinding(bindings, binding) { public
	 * String getLocalizedMessage() { return getLocalizedErrorMessageForUndefinedAndRequiredValue(); } }; } else { // !bv.isBindingValid()
	 * error = new MissingRequiredBinding(bindings, binding) { public String getLocalizedMessage() { return
	 * getLocalizedErrorMessageForInvalidAndRequiredValue(); } }; } if (errors == null) { errors = new CompoundIssue(object); }
	 * errors.addToContainedIssues(error); } } } return errors; }
	 * 
	 * }
	 * 
	 * public abstract static class CheckAllBindingsRule extends ValidationRule { public CheckAllBindingsRule(String message) {
	 * super(ServiceMessageBindings.class, message); }
	 * 
	 * public abstract ValidationIssue applyValidation(final Validable object);
	 * 
	 * public class MissingRequiredBinding extends ValidationError { public ServiceMessageEntryBinding messageEntryBinding;
	 * 
	 * public String bindingName;
	 * 
	 * public String portName;
	 * 
	 * public MissingRequiredBinding(ServiceMessageBindings message, ServiceMessageEntryBinding aMessageEntryBinding) {
	 * super(CheckAllBindingsRule.this, message, null); messageEntryBinding = aMessageEntryBinding; bindingName =
	 * messageEntryBinding.getBindingDefinitionName(); portName = message.getServiceOperation().getName(); BindingDefinition bd =
	 * aMessageEntryBinding.getBindingDefinition(); if (bd != null) { Vector allAvailableBV = bd.searchMatchingBindingValue(message, 2); for
	 * (int i = 0; i < allAvailableBV.size(); i++) { BindingValue proposal = (BindingValue) allAvailableBV.elementAt(i);
	 * addToFixProposals(new SetBinding(aMessageEntryBinding, proposal)); } } }
	 * 
	 * public String getLocalizedErrorMessageForUndefinedAndRequiredValue() { return FlexoLocalization.localizedForKeyWithParams(
	 * "binding_named_($bindingName)_required_by_port_($portName)_is_not_defined", this); }
	 * 
	 * public String getLocalizedErrorMessageForInvalidAndRequiredValue() { return FlexoLocalization.localizedForKeyWithParams(
	 * "binding_named_($bindingName)_required_by_port_($portName)_has_invalid_value", this); }
	 * 
	 * public String getLocalizedErrorMessageForInvalidValue() { return
	 * FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_has_invalid_value", this); }
	 * 
	 * public FlexoModelObject getSelectableObject() { return messageEntryBinding.getMessageBindings().getServiceOperation(); } }
	 * 
	 * public class SetBinding extends FixProposal { private ServiceMessageEntryBinding componentInstanceBinding;
	 * 
	 * public BindingValue bindingValue;
	 * 
	 * public String bindingName;
	 * 
	 * public SetBinding(ServiceMessageEntryBinding aMessageEntryBinding, BindingValue aBindingValue) {
	 * super("set_binding_($bindingName)_to_($bindingValue.stringRepresentation)"); bindingValue = aBindingValue; bindingName =
	 * aMessageEntryBinding.getBindingDefinitionName(); componentInstanceBinding = aMessageEntryBinding; }
	 * 
	 * protected void fixAction() { componentInstanceBinding.setBindingValue(bindingValue); }
	 * 
	 * public String getBindingName() { return bindingName; }
	 * 
	 * public void setBindingName() { } }
	 * 
	 * }
	 */
	/**
	 * Overrides getAllEmbeddedDeleted
	 * 
	 * @see org.openflexo.foundation.wkf.WKFObject#getAllEmbeddedDeleted()
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return EmptyVector.EMPTY_VECTOR(WKFObject.class);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "service_message_bindings";
	}

}
