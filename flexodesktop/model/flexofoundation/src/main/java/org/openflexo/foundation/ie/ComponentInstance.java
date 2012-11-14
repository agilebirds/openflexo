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
package org.openflexo.foundation.ie;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.dm.BindingAdded;
import org.openflexo.foundation.ie.dm.BindingRemoved;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DMEntityParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.xmlcode.XMLMapping;

/**
 * A ComponentInstance represents an instance (a use case) of a ComponentDefinition in a given context (this context can be determined with
 * the type of resource data: {@link #getXMLResourceData()} Note that a component instance owner has 3 notifications to forward to its
 * component instance(s): 1) When the owner changes of resource data--> updateDependancies() 2) When the owner is deleted-->delete (perform
 * this call asap so that resource data is still set on the calling owner) 3) When the component instance is removed from the owner-->delete
 * 
 * @author bmangez, gpolet
 */
public abstract class ComponentInstance extends IEObject implements Bindable, FlexoObserver, Validable {

	private static final Logger logger = Logger.getLogger(ComponentInstance.class.getPackage().getName());

	private Vector<ComponentInstanceBinding> _bindings;

	private ComponentDefinition _componentDefinition;

	private String xmlComponentName;

	private transient ComponentInstanceOwner _owner;

	/* This variable should not be used, but instead, use the getXMLResourceData() method */
	private XMLStorageResourceData container;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	private ComponentInstance(FlexoProject project, XMLStorageResourceData aContainer) {
		super(project);
		container = aContainer;
		_bindings = new Vector<ComponentInstanceBinding>();
	}

	protected ComponentInstance(ComponentDefinition componentDef, XMLStorageResourceData aContainer) {
		this(componentDef != null ? componentDef.getProject() : /*(aContainer!=null?*/aContainer.getProject()/*:null)*/, aContainer);
		if (componentDef != null) {// -->ie, we are creating this component instance, we are not deserializing it.
			setComponentDefinition(componentDef);
			updateDependancies(null, aContainer);
		}
	}

	public ComponentInstance(FlexoProcessBuilder builder) {
		this(builder.isCloner ? null : builder.getProject(), builder.isCloner ? null : builder.process);
		initializeDeserialization(builder);
	}

	public ComponentInstance(FlexoNavigationMenuBuilder builder) {
		this(builder.getProject(), builder.navigationMenu);
		initializeDeserialization(builder);
	}

	public ComponentInstance(FlexoComponentBuilder builder) {
		this(builder.getProject(), builder.woComponent);
		initializeDeserialization(builder);
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		updateBindings();
	}

	public void updateDependancies(XMLStorageResourceData oldResourceData, XMLStorageResourceData newResourceData) {
		if (oldResourceData == newResourceData) {
			return;
		}
		if (oldResourceData != null && getComponentDefinition() != null) {
			ComponentDefinition cd = getComponentDefinition();
			Iterator<ComponentInstance> i = cd.getComponentInstances().iterator();
			boolean removeDependancy = true;
			while (i.hasNext() && removeDependancy) {
				ComponentInstance ci = i.next();
				if (ci != this) {
					if (ci.getXMLResourceData() == oldResourceData) {
						removeDependancy = false;
					}
				}
			}
			if (removeDependancy) {
				oldResourceData.getFlexoResource().removeFromDependentResources(cd.getComponentResource());
			}
		}
		rebuildDependancy(newResourceData);
	}

	/**
	 * @param newResourceData
	 */
	private void rebuildDependancy(XMLStorageResourceData newResourceData) {
		if (newResourceData != null && getComponentDefinition() != null) {
			newResourceData.getFlexoResource().addToDependentResources(getComponentDefinition().getComponentResource());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not rebuid dependancy for resource data: " + newResourceData + " and component definition is "
						+ getComponentDefinition() + " named: " + getComponentName());
			}
		}
	}

	@Override
	public final void delete() {
		if (_componentDefinition != null) {
			_componentDefinition.removeFromComponentInstances(this);
			_componentDefinition.deleteObserver(this);
		}
		super.delete();
		updateDependancies(getXMLResourceData(), null);
		container = null;
		_owner = null;
		_knownBindings = null;
		_componentDefinition = null;
		xmlComponentName = null;
	}

	// ==========================================================================
	// =========================== Instance methods =============================
	// ==========================================================================

	@Override
	public IEObject getParent() {
		if (_owner instanceof IEObject) {
			return (IEObject) _owner;
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getXMLResourceData() instanceof Bindable) {
			return ((Bindable) getXMLResourceData()).getBindingModel();
		}
		return null;
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.ie.IEObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		if (getOwner() != null) {
			return getOwner().getXMLResourceData().getXMLMapping();
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This component instance does not have nor an owner, nor a container nor a project. This is totally unacceptable.");
			}
			return null;
		}
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * object declared as the container
	 * 
	 * @return container of this object
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		if (getOwner() != null) {
			return getOwner().getXMLResourceData();
		}
		return container;
	}

	public String getInspectorName() {
		return null;
	}

	@Override
	public void setName(String value) {
		setComponentName(value);
	}

	@Override
	public String getName() {
		return getComponentName();
	}

	public final String getComponentName() {
		return xmlComponentName;
	}

	public void setComponentName(String value) {
		xmlComponentName = value;
	}

	public ComponentDefinition getComponentDefinition() {
		if (_componentDefinition == null && xmlComponentName != null) {
			if (getProject() == null) {
				if (isDeserializing()) {
					return null;
				}
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Project not set for ComponentInstance ! Owner is " + getOwner());
				}
				return null;
			}
			ComponentDefinition aComponentDefinition = getProject().getFlexoComponentLibrary().getComponentNamed(xmlComponentName);
			if (aComponentDefinition == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("A ComponentInstance with component name : " + xmlComponentName + " has no component def. Owner is "
							+ getOwner());
				}
			} else {
				setComponentDefinition(aComponentDefinition, false);
			}
		}
		return _componentDefinition;
	}

	public IEWOComponent getWOComponent() {
		return getComponentDefinition().getWOComponent();
	}

	public void setComponentDefinition(ComponentDefinition aComponentDefinition) {
		setComponentDefinition(aComponentDefinition, true);
	}

	public void setComponentDefinition(ComponentDefinition aComponentDefinition, boolean notify) {
		if (aComponentDefinition == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Attempt to set a null component definition on a component instance! Track this call and make sure this never happens ever again.");
			}
			return;
		}
		xmlComponentName = aComponentDefinition.getComponentName();
		if (_componentDefinition != aComponentDefinition) {
			if (_componentDefinition != null && !(this instanceof DummyComponentInstance)) {
				_componentDefinition.deleteObserver(this);
				_componentDefinition.removeFromComponentInstances(this);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Removing " + this + " as observer of " + _componentDefinition);
				}
			}
			_componentDefinition = aComponentDefinition;
			_componentDefinition.addObserver(this);
			if (!(this instanceof DummyComponentInstance)) {
				_componentDefinition.addToComponentInstances(this, notify);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Adding " + this + " as observer of " + _componentDefinition);
			}
			if (notify) {
				setChanged();
			}
		}
	}

	public void notifyComponentNameChanged(ComponentDefinition aComponentDefinition) {
		setComponentDefinition(aComponentDefinition);
		setChanged();
	}

	@Override
	public void setChanged() {
		if (getOwner() != null) {
			getOwner().setChanged();
		}
		super.setChanged();
	}

	public void addToBindings(ComponentInstanceBinding value) {
		value.setComponentInstance(this);
		_bindings.add(value);
	}

	public void removeFromBindings(ComponentInstanceBinding value) {
		value.setComponentInstance(null);
		_bindings.remove(value);
	}

	public void setBindings(Vector<ComponentInstanceBinding> value) {
		_bindings = value;
	}

	public Vector<ComponentInstanceBinding> getBindings() {
		/*if (isBeingCloned())
		    return EmptyVector.EMPTY_VECTOR(ComponentInstanceBinding.class);*/
		if (_bindings != null && getComponentDefinition() != null
				&& _bindings.size() != getComponentDefinition().getBindingDefinitions().size()) {
			updateBindings();
		}
		return _bindings;
	}

	private boolean isRegistered(ComponentBindingDefinition bd) {
		for (Enumeration en = _bindings.elements(); en.hasMoreElements();) {
			ComponentInstanceBinding next = (ComponentInstanceBinding) en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return true;
			}
		}
		return false;
	}

	public ComponentInstanceBinding getBinding(ComponentBindingDefinition bd) {
		for (Enumeration en = _bindings.elements(); en.hasMoreElements();) {
			ComponentInstanceBinding next = (ComponentInstanceBinding) en.nextElement();
			if (next.getBindingDefinition() == bd) {
				return next;
			}
		}
		return null;
	}

	public ComponentInstanceBinding getBinding(String bindingName) {
		for (Enumeration en = _bindings.elements(); en.hasMoreElements();) {
			ComponentInstanceBinding next = (ComponentInstanceBinding) en.nextElement();
			if (next.getBindingDefinition().getVariableName().equals(bindingName)) {
				return next;
			}
		}
		return null;
	}

	private void updateBindings() {
		if (getComponentDefinition() == null) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateBindings() in ComponentInstance");
		}
		Vector<ComponentInstanceBinding> toRemove = new Vector<ComponentInstanceBinding>();
		toRemove.addAll(_bindings);
		for (Enumeration en = getComponentDefinition().getBindingDefinitions().elements(); en.hasMoreElements();) {
			ComponentBindingDefinition next = (ComponentBindingDefinition) en.nextElement();
			if (!isRegistered(next)) {
				// addToBindings(new ComponentInstanceBinding(this, next, null));
				addToBindings(getComponentInstanceBindingForComponentBindingDefinition(next));
			} else {
				toRemove.remove(getBinding(next));
			}
		}
		for (Enumeration en = toRemove.elements(); en.hasMoreElements();) {
			ComponentInstanceBinding next = (ComponentInstanceBinding) en.nextElement();
			removeFromBindings(next);
		}
		Collections.sort(_bindings, ComponentInstanceBinding.componentInstanceBindingComparator);
	}

	/**
	 * Stores in an hashtable ComponentInstanceBinding related to a ComponentBindingDefinition Ensure that no new ComponentInstanceBinding
	 * is created if a component binding definition is renamed for example
	 */
	private transient Hashtable<ComponentBindingDefinition, ComponentInstanceBinding> _knownBindings = new Hashtable<ComponentBindingDefinition, ComponentInstanceBinding>();

	private ComponentInstanceBinding getComponentInstanceBindingForComponentBindingDefinition(ComponentBindingDefinition bd) {
		// TODO: add bindings when deserializing to knownBindings.
		ComponentInstanceBinding returned = _knownBindings.get(bd);
		if (returned == null) {
			ComponentInstanceBinding newBindingDefinition = new ComponentInstanceBinding(this, bd, null);
			_knownBindings.put(bd, newBindingDefinition);
			return newBindingDefinition;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Retrieve known ComponentInstanceBinding !");
			}
			return returned;
		}
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("received update in ComponentInstance " + dataModification);
		}
		if (o == getComponentDefinition() && (dataModification instanceof BindingAdded || dataModification instanceof BindingRemoved)) {
			updateBindings();
		}
	}

	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getWOComponent().getAllEmbeddedIEObjects();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE1: that this is NOT a recursive method NOTE2: return EMPTY_VECTOR, since
	 * there is no embedded IEObject
	 * 
	 * @return EMPTY_VECTOR
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return EmptyVector.EMPTY_VECTOR(IObject.class);
	}

	public ComponentInstanceBinding createNewBinding() {
		if (getComponentDefinition() != null) {
			ComponentBindingDefinition newBD = getComponentDefinition().createNewBinding();
			return getBinding(newBD);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not create binding: cannot access ComponentDefinition !");
			}
			return null;
		}
	}

	public void deleteBinding(ComponentInstanceBinding cib) {
		if (getComponentDefinition() != null) {
			getComponentDefinition().deleteBinding(cib.getBindingDefinition());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not delete binding: cannot access ComponentDefinition !");
			}
		}
	}

	public boolean isBindingDeletable(ComponentInstanceBinding cib) {
		if (getComponentDefinition() != null) {
			return getComponentDefinition().isBindingDefinitionDeletable(cib.getBindingDefinition());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access binding: cannot access ComponentDefinition !");
			}
		}
		return false;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getOwner() instanceof IEWidget && ((IEWidget) getOwner()).getWOComponent() != null) {
			return "COMPONENT_INSTANCE." + getName() + " in " + ((IEWidget) getOwner()).getWOComponent().getName();
		} else if (getOwner() != null) {
			return "COMPONENT_INSTANCE." + getName() + " in " + getOwner().getFullyQualifiedName();
		} else {
			return "COMPONENT_INSTANCE." + getName();
		}
	}

	/**
	 * Returns owner for this component instance, if it has been set. The owner of a ComponentInstance is the FlexoModelObject which emmbed
	 * this instance.
	 */
	public final ComponentInstanceOwner getOwner() {
		return _owner;
	}

	/**
	 * Sets owner for this component instance. The owner of a ComponentInstance is the FlexoModelObject which emmbed this instance.
	 * 
	 * @param owner
	 */
	public final void setOwner(ComponentInstanceOwner owner) {
		if (_owner == owner) {
			return;
		}
		XMLStorageResourceData oldResourceData = getXMLResourceData();
		if (owner == null) {
			delete();
		}
		container = null;
		_owner = owner;
		_componentDefinition = null;// We reset the _componentDefinition so that in case of conversion we will try to retrieve the component
									// definition again.
		// We need to notify the component instance bindings (because now we can retrieve the component definition)
		for (ComponentInstanceBinding b : _bindings) {
			b.setComponentInstance(this);
		}
		if (owner != null) {
			updateDependancies(oldResourceData, getXMLResourceData());
		}
	}

	public boolean isValidInstance() {
		return getComponentDefinition() != null;
	}

	public void rebuildDependancies() {
		if (getXMLResourceData() != null) {
			rebuildDependancy(getXMLResourceData());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot rebuild dependancies because XMLResourceData=" + getXMLResourceData() + " and ComponentDefinition="
						+ getComponentDefinition());
			}
		}
	}

	// ==========================================================================
	// ============================== Validation ================================
	// ==========================================================================

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	public static class DefinedBindingsMustBeValid extends CheckAllBindingsRule {
		public DefinedBindingsMustBeValid() {
			super("defined_bindings_must_be_valid");
		}

		@Override
		public ValidationIssue<CheckAllBindingsRule, ComponentInstance> applyValidation(final ComponentInstance ci) {
			CompoundIssue<CheckAllBindingsRule, ComponentInstance> errors = null;
			Enumeration en = ci.getBindings().elements();
			while (en.hasMoreElements()) {
				ComponentInstanceBinding ciBinding = (ComponentInstanceBinding) en.nextElement();
				AbstractBinding bv = ciBinding.getBindingValue();
				if (bv != null && !bv.isBindingValid()) {
					ValidationError<CheckAllBindingsRule, ComponentInstance> error;
					error = new MissingRequiredBinding(ci, ciBinding) {
						@Override
						public String getLocalizedMessage() {
							return getLocalizedErrorMessageForInvalidValue();
						}
					};
					if (errors == null) {
						errors = new CompoundIssue<CheckAllBindingsRule, ComponentInstance>(ci);
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
		public ValidationIssue<CheckAllBindingsRule, ComponentInstance> applyValidation(final ComponentInstance ci) {
			CompoundIssue<CheckAllBindingsRule, ComponentInstance> errors = null;
			Enumeration en = ci.getBindings().elements();
			while (en.hasMoreElements()) {
				ComponentInstanceBinding ciBinding = (ComponentInstanceBinding) en.nextElement();
				if (ciBinding.getBindingDefinition() != null && ciBinding.getBindingDefinition().getIsMandatory()) {
					AbstractBinding bv = ciBinding.getBindingValue();
					if (bv == null || !bv.isBindingValid()) {
						ValidationError<CheckAllBindingsRule, ComponentInstance> error;
						if (bv == null) {
							error = new MissingRequiredBinding(ci, ciBinding) {
								@Override
								public String getLocalizedMessage() {
									return getLocalizedErrorMessageForUndefinedAndRequiredValue();
								}
							};
						} else { // !bv.isBindingValid()
							error = new MissingRequiredBinding(ci, ciBinding) {
								@Override
								public String getLocalizedMessage() {
									return getLocalizedErrorMessageForInvalidAndRequiredValue();
								}
							};
						}
						if (errors == null) {
							errors = new CompoundIssue<CheckAllBindingsRule, ComponentInstance>(ci);
						}
						errors.addToContainedIssues(error);
					}
				}
			}
			return errors;
		}

	}

	public abstract static class CheckAllBindingsRule extends ValidationRule<CheckAllBindingsRule, ComponentInstance> {
		public CheckAllBindingsRule(String message) {
			super(ComponentInstance.class, message);
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {

			return targetType != CodeType.PROTOTYPE;
		}

		@Override
		public abstract ValidationIssue<CheckAllBindingsRule, ComponentInstance> applyValidation(final ComponentInstance object);

		public class MissingRequiredBinding extends ValidationError<CheckAllBindingsRule, ComponentInstance> {
			public ComponentInstanceBinding componentInstanceBinding;
			public String bindingName;
			public String componentName;
			public String ownerIdentifier;

			public MissingRequiredBinding(ComponentInstance ci, ComponentInstanceBinding aComponentInstanceBinding) {
				super(CheckAllBindingsRule.this, ci, null);
				componentInstanceBinding = aComponentInstanceBinding;
				bindingName = componentInstanceBinding.getBindingDefinitionName();
				componentName = componentInstanceBinding.getComponentInstance().getComponentName();
				ownerIdentifier = componentInstanceBinding.getComponentInstance().getOwner() != null ? ((FlexoModelObject) componentInstanceBinding
						.getComponentInstance().getOwner()).getFullyQualifiedName() : "undentified button or link";
				BindingDefinition bd = aComponentInstanceBinding.getBindingDefinition();
				if (bd != null) {
					if (ci.getOwner() instanceof IEWidget) {
						addToFixProposals(new AddEntryToComponentAndSetBinding(componentInstanceBinding));
					}
					Vector<BindingValue> allAvailableBV = bd.searchMatchingBindingValue(ci, 2);
					for (int i = 0; i < allAvailableBV.size(); i++) {
						BindingValue proposal = allAvailableBV.elementAt(i);
						addToFixProposals(new SetBinding(aComponentInstanceBinding, proposal));
					}
				}
			}

			public String getLocalizedErrorMessageForUndefinedAndRequiredValue() {
				return FlexoLocalization.localizedForKeyWithParams(
						"binding_named_($bindingName)_required_by_component_($componentName)_is_not_defined_for_widget($ownerIdentifier)",
						this);
			}

			public String getLocalizedErrorMessageForInvalidAndRequiredValue() {
				return FlexoLocalization
						.localizedForKeyWithParams(
								"binding_named_($bindingName)_required_by_component_($componentName)_has_invalid_value_for_widget($ownerIdentifier)",
								this);
			}

			public String getLocalizedErrorMessageForInvalidValue() {
				return FlexoLocalization.localizedForKeyWithParams("binding_named_($bindingName)_has_invalid_value", this);
			}

			@Override
			public FlexoModelObject getSelectableObject() {
				return (FlexoModelObject) componentInstanceBinding.getComponentInstance().getOwner();
			}
		}

		public class SetBinding extends FixProposal<CheckAllBindingsRule, ComponentInstance> {
			private ComponentInstanceBinding componentInstanceBinding;
			public BindingValue bindingValue;
			public String bindingName;

			public SetBinding(ComponentInstanceBinding aComponentInstanceBinding, BindingValue aBindingValue) {
				super("set_binding_($bindingName)_to_($bindingValue.stringRepresentation)");
				bindingValue = aBindingValue;
				bindingName = aComponentInstanceBinding.getBindingDefinitionName();
				componentInstanceBinding = aComponentInstanceBinding;
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

		public static class AddEntryToComponentAndSetBinding extends ParameteredFixProposal<CheckAllBindingsRule, ComponentInstance> {
			public BindingDefinition bindingDefinition;
			public String bindingName;
			private ComponentInstanceBinding componentInstanceBinding;

			public AddEntryToComponentAndSetBinding(ComponentInstanceBinding aComponentInstanceBinding) {
				super("add_entry_and_set_($bindingName)_onto", buildParameters(aComponentInstanceBinding.getBindingDefinition()));
				componentInstanceBinding = aComponentInstanceBinding;
				this.bindingName = componentInstanceBinding.getBindingDefinitionName();
				this.bindingDefinition = componentInstanceBinding.getBindingDefinition();
			}

			private static ParameterDefinition[] buildParameters(BindingDefinition bd) {
				ParameterDefinition[] returned = new ParameterDefinition[3];
				returned[0] = new TextFieldParameter("variableName", "variable_name", "");
				returned[1] = new DMEntityParameter("variableType", "variable_type", bd.getType() != null ? bd.getType().getBaseEntity()
						: null);
				returned[2] = new ChoiceListParameter<DMPropertyImplementationType>("implementationType", "implementation_type",
						DMPropertyImplementationType.PUBLIC_FIELD);
				returned[2].addParameter("format", "localizedName");
				return returned;
			}

			@Override
			protected void fixAction() {
				ComponentInstance ci = getObject();
				IEWidget widget = (IEWidget) ci.getOwner();
				String newVariableName = (String) getValueForParameter("variableName");
				DMEntity newVariableType = (DMEntity) getValueForParameter("variableType");
				DMPropertyImplementationType implementationType = (DMPropertyImplementationType) getValueForParameter("implementationType");
				widget.createsBindingVariable(newVariableName, DMType.makeResolvedDMType(newVariableType), implementationType, false);
				DMProperty property = null;
				ComponentDMEntity componentDMEntity = widget.getComponentDMEntity();
				if (componentDMEntity != null) {
					property = componentDMEntity.getDMProperty(newVariableName);
				}
				if (property != null) {
					BindingVariable var = widget.getBindingModel().bindingVariableNamed("component");
					BindingValue newBindingValue = new BindingValue(bindingDefinition, widget);
					newBindingValue.setBindingVariable(var);
					newBindingValue.addBindingPathElement(property);
					newBindingValue.connect();
					componentInstanceBinding.setBindingValue(newBindingValue);
				}
			}

		}
	}

	public Hashtable<String, String> getStaticBindingValues() {
		Hashtable<String, String> reply = new Hashtable<String, String>();
		for (ComponentInstanceBinding cibd : getBindings()) {
			if (cibd.getBindingValue() != null && cibd.getBindingValue().isStaticValue()) {
				reply.put(cibd.getBindingDefinitionName(), ((StaticBinding) cibd.getBindingValue()).getStringRepresentation());
			}
		}
		reply.put("ci", Long.toString(getFlexoID()));
		return reply;
	}

}
