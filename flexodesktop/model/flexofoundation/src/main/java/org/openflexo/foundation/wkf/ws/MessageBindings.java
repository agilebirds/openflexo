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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;

public class MessageBindings extends WKFObject implements InspectableObject, Bindable, LevelledObject, FlexoObserver {

	private static final Logger logger = Logger.getLogger(MessageBindings.class.getPackage().getName());

	private MessageEdge _messageEdge;

	private AbstractMessageDefinition _messageDefinition;

	private Vector<MessageEntryBinding> _bindings;

	/**
	 * Constructor used during deserialization
	 */
	public MessageBindings(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public MessageBindings(FlexoProcess process) {
		super(process);
		_bindings = new Vector<MessageEntryBinding>();
	}

	/**
	 * Dynamic constructor
	 */
	public MessageBindings(MessageEdge messageEdge, AbstractMessageDefinition messageDefinition) {
		this(messageEdge.getProcess());
		setMessageEdge(messageEdge);
		setMessageDefinition(messageDefinition);
	}

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

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.MESSAGE_INSPECTOR;
	}

	public AbstractMessageDefinition getMessageDefinition() {
		return _messageDefinition;
	}

	@Override
	public String getName() {
		if (getMessageDefinition() != null) {
			return getMessageDefinition().getName();
		}
		return FlexoLocalization.localizedForKey("unbound");
	}

	@Override
	public void setName(String aName) {
		if (getMessageDefinition() != null) {
			getMessageDefinition().setName(aName);
		}
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
		for (Enumeration en = getBindings().elements(); en.hasMoreElements();) {
			MessageEntryBinding next = (MessageEntryBinding) en.nextElement();
			next.lookupBindingDefinition();
		}
	}

	public MessageEdge getMessageEdge() {
		return _messageEdge;
	}

	public void setMessageEdge(MessageEdge messageEdge) {
		_messageEdge = messageEdge;
		for (MessageEntryBinding b : getBindings()) {
			if (b.getBindingValue() != null) {
				b.getBindingValue().setOwner(messageEdge);
			}
		}
	}

	@Override
	public BindingModel getBindingModel() {
		if (getProcess() != null) {
			return getProcess().getBindingModel();
		} else {
			return null;
		}
	}

	public void addToBindings(MessageEntryBinding value) {
		value.setMessageBindings(this);
		_bindings.add(value);
	}

	public void removeFromBindings(MessageEntryBinding value) {
		value.setMessageBindings(null);
		_bindings.remove(value);
	}

	public void setBindings(Vector<MessageEntryBinding> value) {
		_bindings = value;
	}

	public Vector<MessageEntryBinding> getBindings() {
		if (_bindings != null && getMessageDefinition() != null && _bindings.size() != getMessageDefinition().getEntries().size()) {
			updateBindings();
		}
		return _bindings;
	}

	private boolean isRegistered(MessageEntry bd) {
		for (Enumeration en = _bindings.elements(); en.hasMoreElements();) {
			MessageEntryBinding next = (MessageEntryBinding) en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return true;
			}
		}
		return false;
	}

	private MessageEntryBinding getBinding(MessageEntry bd) {
		for (Enumeration en = _bindings.elements(); en.hasMoreElements();) {
			MessageEntryBinding next = (MessageEntryBinding) en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return next;
			}
		}
		return null;
	}

	private void updateBindings() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateBindings() in MessageBindings");
		}
		Vector<MessageEntryBinding> toRemove = new Vector<MessageEntryBinding>();
		toRemove.addAll(_bindings);
		for (Enumeration en = getMessageDefinition().getEntries().elements(); en.hasMoreElements();) {
			MessageEntry next = (MessageEntry) en.nextElement();
			if (!isRegistered(next)) {
				addToBindings(new MessageEntryBinding(this, next, null));
			} else {
				toRemove.remove(getBinding(next));
			}
		}
		for (Enumeration en = toRemove.elements(); en.hasMoreElements();) {
			MessageEntryBinding next = (MessageEntryBinding) en.nextElement();
			// removeFromBindings(next);
		}
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("received update in MessageBindings " + dataModification);
		}
		if (o == getMessageDefinition() && (dataModification instanceof BindingAdded || dataModification instanceof BindingRemoved)) {
			updateBindings();
			setChanged();
			notifyObservers(dataModification);
		}
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE1: that this is NOT a recursive method NOTE2: return null, since there is no
	 * embedded IEObject
	 * 
	 * @return null
	 */
	public Vector getEmbeddedIEObjects() {
		return new Vector();
	}

	public MessageEntryBinding createNewBinding() {
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

	/**
     * 
     */
	@Override
	// final is for deleteObservers()
	public final boolean delete() {
		super.delete();
		deleteObservers();
		return true;
	}

	public void deleteBinding(MessageEntryBinding meb) {
		if (getMessageDefinition() != null) {
			getMessageDefinition().deleteMessageEntry(meb.getBindingDefinition());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not delete binding: cannot access MessageDefinition !");
			}
		}
	}

	public boolean isBindingDeletable(MessageEntryBinding meb) {
		if (getMessageDefinition() != null) {
			return getMessageDefinition().isMessageEntryDeletable(meb.getBindingDefinition());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access binding: cannot access ComponentDefinition !");
			}
		}
		return false;
	}

	// ==========================================================================
	// ============================== Validation
	// ================================
	// ==========================================================================

	public static class DefinedBindingsMustBeValid extends CheckAllBindingsRule {
		public DefinedBindingsMustBeValid() {
			super("defined_bindings_must_be_valid");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			CompoundIssue errors = null;
			final MessageBindings bindings = (MessageBindings) object;
			Enumeration en = bindings.getBindings().elements();
			while (en.hasMoreElements()) {
				MessageEntryBinding binding = (MessageEntryBinding) en.nextElement();
				AbstractBinding bv = binding.getBindingValue();
				if (bv != null && !bv.isBindingValid()) {
					ValidationError error;
					error = new MissingRequiredBinding(bindings, binding) {
						@Override
						public String getLocalizedMessage() {
							return getLocalizedErrorMessageForInvalidValue();
						}
					};
					if (errors == null) {
						errors = new CompoundIssue(object);
					}
					errors.addToContainedIssues(error);
				}
			}
			return errors;
		}

	}

	public static class MandatoryBindingsMustHaveAValue extends CheckAllBindingsRule {
		public MandatoryBindingsMustHaveAValue() {
			super("mandatory_bindings_must_have_a_value");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			CompoundIssue errors = null;
			final MessageBindings bindings = (MessageBindings) object;
			Enumeration en = bindings.getBindings().elements();
			while (en.hasMoreElements()) {
				MessageEntryBinding binding = (MessageEntryBinding) en.nextElement();
				if (binding.getBindingDefinition().getIsMandatory()) {
					AbstractBinding bv = binding.getBindingValue();
					if (bv == null || !bv.isBindingValid()) {
						ValidationError error;
						if (bv == null) {
							error = new MissingRequiredBinding(bindings, binding) {
								@Override
								public String getLocalizedMessage() {
									return getLocalizedErrorMessageForUndefinedAndRequiredValue();
								}
							};
						} else { // !bv.isBindingValid()
							error = new MissingRequiredBinding(bindings, binding) {
								@Override
								public String getLocalizedMessage() {
									return getLocalizedErrorMessageForInvalidAndRequiredValue();
								}
							};
						}
						if (errors == null) {
							errors = new CompoundIssue(object);
						}
						errors.addToContainedIssues(error);
					}
				}
			}
			return errors;
		}

	}

	public abstract static class CheckAllBindingsRule extends ValidationRule {
		public CheckAllBindingsRule(String message) {
			super(MessageBindings.class, message);
		}

		@Override
		public abstract ValidationIssue applyValidation(final Validable object);

		public class MissingRequiredBinding extends ValidationError {
			public MessageEntryBinding messageEntryBinding;

			public String bindingName;

			public String portName;

			public MissingRequiredBinding(MessageBindings message, MessageEntryBinding aMessageEntryBinding) {
				super(CheckAllBindingsRule.this, message, null);
				messageEntryBinding = aMessageEntryBinding;
				bindingName = messageEntryBinding.getBindingDefinitionName();
				if (message.getMessageEdge().getFlexoPort() != null) {
					portName = message.getMessageEdge().getFlexoPort().getName();
				} else {
					portName = "null";
				}
				BindingDefinition bd = aMessageEntryBinding.getBindingDefinition();
				if (bd != null) {
					Vector allAvailableBV = bd.searchMatchingBindingValue(message, 2);
					for (int i = 0; i < allAvailableBV.size(); i++) {
						BindingValue proposal = (BindingValue) allAvailableBV.elementAt(i);
						addToFixProposals(new SetBinding(aMessageEntryBinding, proposal));
					}
				}
			}

			public String getLocalizedErrorMessageForUndefinedAndRequiredValue() {
				return FlexoLocalization.localizedForKeyWithParams(
						"binding_named_($bindingName)_required_by_port_($portName)_is_not_defined", this);
			}

			public String getLocalizedErrorMessageForInvalidAndRequiredValue() {
				return FlexoLocalization.localizedForKeyWithParams(
						"binding_named_($bindingName)_required_by_port_($portName)_has_invalid_value", this);
			}

			public String getLocalizedErrorMessageForInvalidValue() {
				return FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_has_invalid_value", this);
			}

			@Override
			public FlexoModelObject getSelectableObject() {
				return messageEntryBinding.getMessageBindings().getMessageEdge();
			}
		}

		public class SetBinding extends FixProposal {
			private MessageEntryBinding componentInstanceBinding;

			public BindingValue bindingValue;

			public String bindingName;

			public SetBinding(MessageEntryBinding aMessageEntryBinding, BindingValue aBindingValue) {
				super("set_binding_($bindingName)_to_($bindingValue.stringRepresentation)");
				bindingValue = aBindingValue;
				bindingName = aMessageEntryBinding.getBindingDefinitionName();
				componentInstanceBinding = aMessageEntryBinding;
			}

			@Override
			protected void fixAction() {
				componentInstanceBinding.setBindingValue(bindingValue);
			}

			public String getBindingName() {
				return bindingName;
			}

			public void setBindingName() {
			}
		}

	}

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
		return "message_bindings";
	}

	@Override
	public FlexoLevel getLevel() {
		if (_messageEdge != null) {
			return _messageEdge.getLevel();
		}
		return FlexoLevel.ACTIVITY;
	}

	/*
	 * @Override public void setLocation(Point newLocation) { logger.info("setLocation() from "+getLocation()+" to "+newLocation);
	 * super.setLocation(newLocation);
	 * 
	 * }
	 */
}
