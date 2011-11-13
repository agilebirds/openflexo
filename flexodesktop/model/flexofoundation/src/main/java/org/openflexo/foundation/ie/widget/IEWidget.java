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
package org.openflexo.foundation.ie.widget;

/**
 * Abstract class defining the common behaviour of a widget embedded in a
 * WOComponent
 * 
 * @author bmangez
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.AdditionalBindingDefinition;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.TopComponentContainer;
import org.openflexo.foundation.ie.action.ExportWidgetToPalette;
import org.openflexo.foundation.ie.action.MakePartialComponent;
import org.openflexo.foundation.ie.action.SuroundWithConditional;
import org.openflexo.foundation.ie.action.SuroundWithRepetition;
import org.openflexo.foundation.ie.action.UnwrapConditional;
import org.openflexo.foundation.ie.action.UnwrapRepetition;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.dm.DisplayNeedsRefresh;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.DateUtils;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.toolbox.ToolBox;

public abstract class IEWidget extends IEAbstractWidget implements InspectableObject, DeletableObject, /* Serializable, */Validable,
		Bindable, Indexable, IWidget {
	private static final Logger logger = Logger.getLogger(IEWidget.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private Hashtable _bindings;

	protected String _inspectorName;

	protected int _row;

	protected int _col;

	protected int _index;

	private String _tooltip;

	private String _label;

	private boolean _isRootOfPartialComponent = false;

	private String _partialComponentName;

	private transient FlexoProject _project;

	private static final String BINDING_NAME = "bindingName";

	protected transient IEObject _parent;

	protected transient IEWOComponent _woComponent;

	private AbstractBinding _bindingName;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(prj);
		_project = prj;
		_parent = parent;
		setWOComponent(woComponent);
		_bindings = new Hashtable();
		_additionalBindings = new Vector<AdditionalBindingDefinition>();
	}

	public IEWidget getDraggedModel() {
		return this;
	}

	// ==========================================================================
	// ============================= DeletableObject
	// ============================
	// ==========================================================================

	@Override
	public final void delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("delete IEWidget with parent:" + getParent());
		}
		performOnDeleteOperations();
		removeFromContainer();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete " + this.getClass().getName() + " : " + this);
		}
		super.delete();
		performAfterDeleteOperations();
		deleteObservers();
	}

	public void performOnDeleteOperations() {

	}

	public void performAfterDeleteOperations() {

	}

	@Override
	public void removeFromContainer() {
		if (getParent() != null && getParent() instanceof IETDWidget) {
			if (((IETDWidget) getParent()).getSequenceWidget() == this) {
				((IETDWidget) getParent()).removeSequence((IESequence) this);
			} else {
				((IETDWidget) getParent()).removeFromInnerWidgets(this);
			}
		}
		if (getParent() != null && getParent() instanceof TopComponentContainer && this instanceof IETopComponent) {
			((TopComponentContainer) getParent()).removeTopComponent((IETopComponent) this);
		}
		if (getParent() != null && getParent() instanceof IEBlocWidget && this instanceof InnerBlocWidgetInterface) {
			((IEBlocWidget) getParent()).removeContent((InnerBlocWidgetInterface) this);
		}
		if (getParent() != null && getParent() instanceof IESequenceTR && this instanceof ITableRow) {
			((IESequenceTR) getParent()).removeFromInnerWidgets((ITableRow) this);
		}
		if (getParent() != null && getParent() instanceof IESequenceWidget) {
			((IESequenceWidget) getParent()).removeFromInnerWidgets(this);
		}
		if (getParent() != null && getParent() instanceof IESequenceTopComponent && this instanceof IETopComponent) {
			((IESequenceTopComponent) getParent()).removeFromInnerWidgets((IETopComponent) this);
		}
		if (getParent() != null && getParent() instanceof IESequenceTab && this instanceof ITabWidget) {
			((IESequenceTab) getParent()).removeFromInnerWidgets((ITabWidget) this);
		}
		setWOComponent(null);
	}

	public void notifyDisplayNeedsRefresh() {
		setChanged();
		notifyObservers(new DisplayNeedsRefresh(this));
	}

	public String getNiceName() {
		String niceName = getCalculatedLabel();
		if (niceName != null && niceName.trim().length() > 0) {
			return ToolBox.getJavaName(niceName);
		}
		return getWidgetType();
	}

	public String getWidgetType() {
		String cls = getClass().getSimpleName();
		if (cls.startsWith("IE")) {
			cls = cls.substring("IE".length());
		}
		if (cls.endsWith("Widget")) {
			cls = cls.substring(0, cls.length() - "Widget".length());
		}
		return cls;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(MakePartialComponent.actionType);
		returned.add(SuroundWithConditional.actionType);
		returned.add(SuroundWithRepetition.actionType);
		returned.add(UnwrapConditional.actionType);
		returned.add(UnwrapRepetition.actionType);
		returned.add(ExportWidgetToPalette.actionType);
		// returned.add(GenerateEntityFromSelection.actionType);
		return returned;
	}

	// ==========================================================================
	// ============================= InspectableObject
	// ==========================
	// ==========================================================================
	public String getCSSName() {
		return getFlexoCSS().getName();
	}

	public FlexoCSS getFlexoCSS() {
		return getProject().getCssSheet();
	}

	public Vector<String> getAvailableMethods() {
		return new Vector<String>(getWOComponent().getComponentDMEntity().getMethods().keySet());
	}

	/**
	 * Returns a vector of PopupComponentDefinition objects
	 */
	public Vector getAvailablePopups() {
		return getProject().getFlexoComponentLibrary().getPopupsComponentList();
	}

	public abstract String getDefaultInspectorName();

	public IEHTMLTableWidget getParentTable() {
		return getAncestorOfClass(IEHTMLTableWidget.class);
	}

	@Override
	public String getInspectorName() {
		if (_inspectorName == null) {
			return getDefaultInspectorName();
		}
		return _inspectorName;
	}

	public void setInspectorName(String inspectorName) {
		_inspectorName = inspectorName;
	}

	public String getTooltip() {
		return _tooltip;
	}

	public void setTooltip(String tip) {
		_tooltip = tip;
		setChanged();
		notifyObservers(new IEDataModification("tooltip", null, _tooltip));
	}

	public String getJavascriptToolTip() {
		String answer = ToolBox.getJavascriptComment(getTooltip());
		if ("You can define some tooltips available for the end-user here".equals(answer)) {
			return null;
		}
		return answer;
	}

	// ==========================================================================
	// ============================= XMLSerializable
	// ============================
	// ==========================================================================
	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================

	public boolean parentIsSubSequence() {
		return getParent() instanceof IESequence && ((IESequence) getParent()).isSubsequence();
	}

	public boolean isFirst() {
		return getParent() instanceof IESequence && ((IESequence) getParent()).indexOf(this) == 0;
	}

	public boolean isLast() {
		return getParent() instanceof IESequence && ((IESequence) getParent()).indexOf(this) == ((IESequence) getParent()).size() - 1;
	}

	public IEWidget getPreviousWidget() {
		if (getParent() instanceof IESequence) {
			if (isFirst()) {
				if (parentIsSubSequence()) {
					return ((IEWidget) getParent()).getPreviousWidget();
				} else {
					return null;
				}
			} else {
				IWidget w = ((IESequence) getParent()).get(((IESequence) getParent()).indexOf(this) - 1);
				if (w instanceof IESequence) {
					return (IEWidget) ((IESequence) w).lastObject();
				} else {
					return (IEWidget) w;
				}
			}
		} else {
			return null;
		}
	}

	public IEWidget getNextWidget() {
		if (getParent() instanceof IESequence) {
			if (isLast()) {
				if (parentIsSubSequence()) {
					return ((IEWidget) getParent()).getNextWidget();
				} else {
					return null;
				}
			} else {
				IWidget w = ((IESequence) getParent()).get(((IESequence) getParent()).indexOf(this) + 1);
				if (w instanceof IESequence) {
					return (IEWidget) ((IESequence) w).firstObject();
				} else {
					return (IEWidget) w;
				}
			}
		} else {
			return null;
		}
	}

	public IEBlocWidget findBlocInParent() {
		return getAncestorOfClass(IEBlocWidget.class);
	}

	public IETDWidget findTDInParent() {
		return getAncestorOfClass(IETDWidget.class);
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion For NOW this is not really implemented, we
	 * juste return this
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<IEWidget> getAllEmbeddedDeleted() {
		Vector returned = new Vector();
		returned.add(this);
		return returned;
	}

	public boolean isUsedName(String name) {
		return _bindings.containsKey(name);
	}

	public Enumeration bindings() {
		return _bindings.elements();
	}

	@Override
	public int getIndex() {
		if (getParent() != null && getParent() instanceof IESequence) {
			int i = ((IESequence) getParent()).indexOf(this);
			if (i > -1) {
				_index = i;
			}
		}
		return _index;
	}

	@Override
	public void setIndex(int i) {
		if (_index != i) {
			_index = i;
			setChanged();
			notifyObservers(new IEDataModification("index", null, new Integer(_index)));
		}
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * WOComponent
	 * 
	 * @return the WOComponent in which this widget is embedded
	 */
	@Override
	public final XMLStorageResourceData getXMLResourceData() {
		return getWOComponent();
	}

	@Override
	public IEWOComponent getWOComponent() {
		if (_woComponent == null) {
			if (getParent() instanceof IEWidget) {
				return ((IEWidget) getParent()).getWOComponent();
			} else if (getParent() instanceof IEWOComponent) {
				return (IEWOComponent) getParent();
			}
		}
		return _woComponent;
	}

	public ComponentDefinition getComponentDefinition() {
		return getWOComponent().getComponentDefinition();
	}

	public final Hashtable<String, String> getLocalizedProperties() {
		return getLocalizableProperties(new Hashtable<String, String>());
	}

	protected Hashtable<String, String> getLocalizableProperties(Hashtable<String, String> props) {
		if (StringUtils.isNotEmpty(getTooltip())) {
			props.put("tooltip", getTooltip());
		}
		return props;
	}

	protected boolean noWOChange(IEWOComponent woComponent) {
		return woComponent == null && _woComponent == null || woComponent != null && woComponent.equals(_woComponent);
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		if (_woComponent != null) {
			_woComponent.notifyWidgetRemoved(this);
		}
		_woComponent = woComponent;
		setChanged();
		notifyObservers(new IEDataModification("wOComponent", null, woComponent));
		if (_woComponent != null) {
			_woComponent.notifyWidgetAdded(this); // This call is very important because it will update the WOComponent components cache
		}
	}

	public ComponentDMEntity getComponentDMEntity() {
		if (getWOComponent() != null) {
			return getWOComponent().getComponentDMEntity();
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		/*
		 * if (parentWidget()!=null) return parentWidget().getBindingModel();
		 */
		if (getWOComponent() != null) {
			return getWOComponent().getBindingModel();
		}
		return null;
	}

	public WidgetBindingDefinition getBindingNameDefinition() {
		return WidgetBindingDefinition.get(this, BINDING_NAME, String.class, BindingDefinitionType.GET, false);
	}

	public final AbstractBinding getBindingName() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingName;
	}

	public final void setBindingName(AbstractBinding bindingName) {
		AbstractBinding oldBindingName = _bindingName;
		_bindingName = bindingName;
		if (_bindingName != null) {
			_bindingName.setOwner(this);
			_bindingName.setBindingDefinition(getBindingNameDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(BINDING_NAME, oldBindingName, bindingName));
	}

	public <T extends IEObject> T getAncestorOfClass(Class<T> t) {
		IEObject o = getParent();
		while (o != null && !t.isAssignableFrom(o.getClass())) {
			if (o instanceof IEWidget) {
				o = ((IEWidget) o).getParent();
			} else {
				o = null;
			}
		}
		return (T) o;
	}

	public IEWidget parentWidget() {
		if (getParent() instanceof IEWidget) {
			return (IEWidget) getParent();
		}
		return null;
	}

	@Override
	public IEObject getParent() {
		return _parent;
	}

	@Override
	public void setParent(IEObject parent) {
		_parent = parent;
		setChanged();
		notifyObservers(new IEDataModification("parent", null, parent));
	}

	public boolean isTopComponent() {
		return false;
	}

	public Vector getAvailableDomains() {
		return getProject().getDKVModel().getSortedDomains();
	}

	public boolean getIsRootOfPartialComponent() {
		return _isRootOfPartialComponent;
	}

	public void setIsRootOfPartialComponent(boolean rootOfPartialComponent) {
		_isRootOfPartialComponent = rootOfPartialComponent;
		setChanged();
		notifyObservers(new IEDataModification("isRootOfPartialComponent", null, new Boolean(rootOfPartialComponent)));
	}

	public String getPartialComponentName() {
		return _partialComponentName;
	}

	public void setPartialComponentName(String componentName) {
		_partialComponentName = componentName;
		setChanged();
		notifyObservers(new IEDataModification("partialComponentName", null, _partialComponentName));
	}

	public ComponentDefinition getPartialComponentDefinition() {
		if (_partialComponentName != null) {
			return getProject().getFlexoComponentLibrary().getComponentNamed(_partialComponentName);
		}
		return null;
	}

	public boolean canBePartialComponent() {
		return true;
	}

	public BindingVariable createsBindingVariable(String name, DMType type, DMPropertyImplementationType implementationType) {
		return createsBindingVariable(name, type, implementationType, false);
	}

	public BindingVariable createsBindingVariable(String name, DMType type, DMPropertyImplementationType implementationType,
			boolean isBindable) {
		ComponentDMEntity componentDMEntity = null;
		if (getWOComponent() != null && getWOComponent().getComponentDefinition() != null) {
			componentDMEntity = getWOComponent().getComponentDefinition().getComponentDMEntity();
		}
		if (componentDMEntity != null) {
			DMProperty newProperty = new DMProperty(getProject().getDataModel(), /* componentDMEntity, */
			name, type, DMCardinality.SINGLE, false, true, implementationType);
			componentDMEntity.registerProperty(newProperty, isBindable);
			return getBindingModel().bindingVariableNamed(name);
		}
		return null;
	}

	public void removeBindingVariable(String name) {
		ComponentDMEntity componentDMEntity = null;
		if (getWOComponent() != null && getWOComponent().getComponentDefinition() != null) {
			componentDMEntity = getWOComponent().getComponentDefinition().getComponentDMEntity();
		}
		if (componentDMEntity != null) {
			DMProperty prop = componentDMEntity.getProperty(name);
			if (prop != null) {
				componentDMEntity.unregisterProperty(prop, true);
			}
		}
		setChanged();
	}

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

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return _label;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		String old = _label;
		_label = label;
		setChanged();
		notifyObservers(new IEDataModification("label", old, label));
	}

	public void addToAdditionalBindings(AdditionalBindingDefinition bd) {
		_additionalBindings.add(bd);
		bd.setOwner(this);
		setChanged();
	}

	public void removeFromAdditionalBindings(AdditionalBindingDefinition bd) {
		_additionalBindings.remove(bd);
		bd.setOwner(null);
		setChanged();
	}

	public void setAdditionalBindings(Vector<AdditionalBindingDefinition> value) {
		_additionalBindings = value;
		if (_additionalBindings != null) {
			Enumeration en = _additionalBindings.elements();
			while (en.hasMoreElements()) {
				AdditionalBindingDefinition element = (AdditionalBindingDefinition) en.nextElement();
				element.setOwner(this);
			}
		}
		setChanged();
		notifyObservers(new IEDataModification("additionalBindings", null, value));
	}

	public Vector<AdditionalBindingDefinition> getAdditionalBindings() {

		// if ((_additionalBindings != null) && (getComponentDefinition() !=
		// null) && (_additionalBindings.size() !=
		// getComponentDefinition().getBindingDefinitions().size())) {
		// updateBindings();
		// }
		if (isBeingCloned()) {
			return EmptyVector.EMPTY_VECTOR(AdditionalBindingDefinition.class);
		}
		return _additionalBindings;
	}

	private String getNextDefautBindingName() {
		int i = 1;
		while (true) {
			if (findBindingNamed("binding-" + i) == null) {
				return "binding-" + i;
			}
			i++;
		}
	}

	public AdditionalBindingDefinition findBindingNamed(String s) {
		if (_additionalBindings != null) {
			Enumeration en = _additionalBindings.elements();
			while (en.hasMoreElements()) {
				AdditionalBindingDefinition element = (AdditionalBindingDefinition) en.nextElement();
				if (element.getVariableName().equalsIgnoreCase(s)) {
					return element;
				}
			}
		}
		return null;
	}

	public AdditionalBindingDefinition createNewAdditionalBinding() {
		String newBindingName = getNextDefautBindingName();
		AdditionalBindingDefinition answer = new AdditionalBindingDefinition(_project);
		answer.setVariableName(newBindingName);
		addToAdditionalBindings(answer);
		setChanged();
		notifyObservers(new AdditionalBindingCreated(answer));
		return answer;
	}

	public boolean isAdditionalBindingDeletable(AdditionalBindingDefinition bd) {
		return true;
	}

	public String getNameOrCalculatedLabel() {
		if (getName() != null && getName().trim().length() > 0) {
			return getName();
		} else {
			return getCalculatedLabel();
		}
	}

	private static final String START_CHAR = "^[A-Za-z]";

	protected String ensureCalculatedLabelIsAcceptable(String proposal) {
		if (proposal == null) {
			return null;
		}
		if (proposal.length() == 1) {
			if (!proposal.matches(START_CHAR)) {
				return "label" + proposal;
			}
		}
		return proposal;
	}

	public String getCalculatedLabel() {
		if (this instanceof IELabelWidget) {
			return ensureCalculatedLabelIsAcceptable(((IELabelWidget) this).getValue());
		}
		if (this instanceof IEHeaderWidget) {
			return ensureCalculatedLabelIsAcceptable(((IEHeaderWidget) this).getValue());
		}
		if (this instanceof IETabWidget) {
			return ensureCalculatedLabelIsAcceptable(((IETabWidget) this).getTitle());
		}
		if (getLabel() != null && getLabel().trim().length() > 0) {
			return ensureCalculatedLabelIsAcceptable(getLabel());
		}
		if (isInHTMLList()) {
			HTMLListDescriptor desc = getHTMLListDescriptor();
			IENonEditableTextWidget header = desc.findHeaderForWidget(this);
			if (header != null) {
				return ensureCalculatedLabelIsAcceptable(header.getValue());
			}

		}
		if (getParent() == null) {
			return null;
		} else if (getParent() instanceof IEBlocWidget) {
			return null;
		} else if (getParent() instanceof IESequenceWidget) {
			IESequenceWidget p = (IESequenceWidget) getParent();
			if (p.getPrevious(this) != null) {
				if (p.getPrevious(this) instanceof IELabelWidget) {
					return ensureCalculatedLabelIsAcceptable(((IELabelWidget) p.getPrevious(this)).getValue());
				} else {
					return ensureCalculatedLabelIsAcceptable(((IEWidget) p.getPrevious(this)).getCalculatedLabel());
				}
			} else {
				// this is the first of the sequence
				if (p.getParent() instanceof IETDWidget) {
					IETDWidget td = (IETDWidget) p.getParent();
					IESequenceTD tdSeq = td.getParent();
					if (tdSeq.getPrevious(td) != null) {
						IETDWidget previousTD = (IETDWidget) tdSeq.getPrevious(td);
						if ((IEWidget) previousTD.getSequenceWidget().getLast() != null) {
							return ensureCalculatedLabelIsAcceptable(((IEWidget) previousTD.getSequenceWidget().getLast())
									.getCalculatedLabel());
						} else {
							while (previousTD != null) {
								previousTD = (IETDWidget) tdSeq.getPrevious(previousTD);
								if (previousTD != null && (IEWidget) previousTD.getSequenceWidget().getLast() != null) {
									return ensureCalculatedLabelIsAcceptable(((IEWidget) previousTD.getSequenceWidget().getLast())
											.getCalculatedLabel());
								}
							}
						}
					}
					IEWidget htmlTable = td.getParent();
					while (htmlTable != null && !(htmlTable instanceof IEHTMLTableWidget)) {
						htmlTable = (IEWidget) htmlTable.getParent();
					}
					if (htmlTable instanceof IEHTMLTableWidget) {
						return ensureCalculatedLabelIsAcceptable(((IEHTMLTableWidget) htmlTable).getCalculatedLabel());
					}
				} else if (getParent() instanceof IESequenceWidget) {
					return ensureCalculatedLabelIsAcceptable(((IEWidget) getParent()).getCalculatedLabel());
				}
				/*
				 * if (!(getParent() instanceof IESequenceTopComponent)) if (logger.isLoggable(Level.WARNING))
				 * logger.warning("widgets located within a " + getParent().getClass().getName() +
				 * " are not capable to find their label !!!!!");
				 */
				return null;
			}
		} /*
			* else { if (!(getParent() instanceof IESequenceTopComponent)) if (logger.isLoggable(Level.WARNING))
			* logger.warning("widgets located within a " + getParent().getClass().getName() + " are not capable to find their label"); return
			* null; }
			*/
		return null;
	}

	/**
	 * @return
	 */
	/*
	 * public String lookForLabelInTD() { IESequenceWidget td = (IESequenceWidget)getParent(); Vector v = td.getInnerWidgets(); int index =
	 * v.indexOf(this); if (index<0) { if (logger.isLoggable(Level.WARNING))
	 * logger.warning("Widget "+this+" has a TD parent which does not contain it as one of its children"); return null; } while (index>=0) {
	 * if (td.getT) index--; } return null; }
	 */

	public boolean isInList() {
		return isInHTMLList();
	}

	public boolean isInHTMLList() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc != null) {
			return this.equals(desc.getRepeatedSequenceTR().getFirstTR()) || desc.isRepeatedCell(findTDInParent());
		}
		return false;
	}

	@Override
	public HTMLListDescriptor getHTMLListDescriptor() {
		HTMLListDescriptor reply = HTMLListDescriptor.createInstanceForWidget(this);
		if (reply == null) {
			IEBlocWidget bloc = findBlocInParent();
			if (bloc != null) {
				return HTMLListDescriptor.createInstanceForBloc(bloc);
			}
		}
		return reply;
	}

	public RepetitionOperator repetition() {
		IEObject parent = getParent();
		while (parent != null) {
			if (parent instanceof IESequence) {
				if (((IESequence) parent).isRepetition()) {
					return (RepetitionOperator) ((IESequence) parent).getOperator();
				}
			}
			if (parent instanceof IEWidget) {
				parent = ((IEWidget) parent).getParent();
			} else {
				return null;
			}
		}
		return null;
	}

	public boolean isInRepetition() {
		return repetition() != null;
	}

	public ConditionalOperator conditional() {
		IEObject parent = getParent();
		while (parent != null) {
			if (parent instanceof IESequence) {
				if (((IESequence) parent).isConditional()) {
					return (ConditionalOperator) ((IESequence) parent).getOperator();
				}
			}
			if (parent instanceof IEWidget) {
				parent = ((IEWidget) parent).getParent();
			} else {
				return null;
			}
		}
		return null;
	}

	public boolean isInConditional() {
		return conditional() != null;
	}

	public boolean isInSearchArea() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc != null) {
			return desc.isInSearchArea(this);
		}
		return false;
	}

	public boolean isSonOf(IEWidget parent) {
		IEObject p = getParent();
		while (p != null && p instanceof IEWidget) {
			if (p == parent) {
				return true;
			} else {
				p = ((IEWidget) p).getParent();
			}
		}
		return false;
	}

	public boolean isParentOf(IEWidget parent) {
		IEObject p = parent.getParent();
		while (p != null && p instanceof IEWidget) {
			if (p == this) {
				return true;
			} else {
				p = ((IEWidget) p).getParent();
			}
		}
		return false;
	}

	public boolean isInPopup() {
		return getWOComponent() instanceof IEPopupComponent;
	}

	public boolean isInPage() {
		return getWOComponent() instanceof IEOperationComponent;
	}

	private Vector<AdditionalBindingDefinition> _additionalBindings;

	public boolean isSearchRowForList() {
		return false;
	}

	/**
	 * @return
	 */
	public boolean parentIsConditional() {
		if (getParent() instanceof IESequence) {
			return ((IESequence) getParent()).hasOperatorConditional();
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean parentIsRepetition() {
		if (getParent() instanceof IESequence) {
			return ((IESequence) getParent()).hasOperatorRepetition();
		}
		return false;
	}

	public boolean isInABlock() {
		return findBlocInParent() != null;
	}

	@Override
	public abstract void removeInvalidComponentInstances();

	@Override
	public abstract boolean areComponentInstancesValid();

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return EmptyVector.EMPTY_VECTOR(IEHyperlinkWidget.class);
	}

	/**
	 * Overrides getAllNonSequenceWidget
	 * 
	 * @see org.openflexo.foundation.ie.widget.IWidget#getAllNonSequenceWidget()
	 */
	@Override
	public Vector<IWidget> getAllNonSequenceWidget() {
		Vector<IWidget> v = new Vector<IWidget>();
		v.add(this);
		return v;
	}

	@Override
	public String getRawRowKeyPath() {
		return null;
	}

	public String getAnchor() {
		return "ANCHOR" + getFlexoID();
	}

	public boolean generateJavascriptID() {
		return false;
	}

	public Vector<HyperlinkType> availableHyperlinks() {
		if (getWOComponent() instanceof IEPopupComponent) {
			return availableHyperlinksInPopup();
		} else {
			return availableHyperlinksInPage();
		}
	}

	private static Vector<HyperlinkType> availableHyperlinksInPopup() {
		if (_availableHyperlinksInPopup.size() == 0) {
			_availableHyperlinksInPopup.add(HyperlinkType.CONFIRM);
			_availableHyperlinksInPopup.add(HyperlinkType.CANCEL);
			_availableHyperlinksInPopup.add(HyperlinkType.SEARCH);
			_availableHyperlinksInPopup.add(HyperlinkType.URL);
			_availableHyperlinksInPopup.add(HyperlinkType.MAILTO);
			_availableHyperlinksInPopup.add(HyperlinkType.IMAGE);
			_availableHyperlinksInPopup.add(HyperlinkType.CLIENTSIDESCRIPT);
			_availableHyperlinksInPopup.add(HyperlinkType.HELP);
			_availableHyperlinksInPopup.add(HyperlinkType.PRINT);
		}
		return _availableHyperlinksInPopup;
	}

	private static final Vector<HyperlinkType> _availableHyperlinksInPopup = new Vector<HyperlinkType>();

	private static Vector<HyperlinkType> availableHyperlinksInPage() {
		if (_availableHyperlinksInPage.size() == 0) {
			_availableHyperlinksInPage.add(HyperlinkType.FLEXOACTION);
			_availableHyperlinksInPage.add(HyperlinkType.DISPLAYACTION);
			_availableHyperlinksInPage.add(HyperlinkType.SEARCH);
			_availableHyperlinksInPage.add(HyperlinkType.URL);
			_availableHyperlinksInPage.add(HyperlinkType.MAILTO);
			_availableHyperlinksInPage.add(HyperlinkType.IMAGE);
			_availableHyperlinksInPage.add(HyperlinkType.CLIENTSIDESCRIPT);
			_availableHyperlinksInPage.add(HyperlinkType.HELP);
			_availableHyperlinksInPage.add(HyperlinkType.PRINT);
		}
		return _availableHyperlinksInPage;
	}

	private static final Vector<HyperlinkType> _availableHyperlinksInPage = new Vector<HyperlinkType>();

	/**
	 * Return the key to use in the process instance dictionary for this IEWidget.
	 * 
	 * @return the key to use in the process instance dictionary for this IEWidget.
	 */
	public String getProcessInstanceDictionaryKey() {
		// Check if there is binding
		AbstractBinding bindingValue = null;
		if (this instanceof IEWidgetWithMainBinding) {
			bindingValue = ((IEWidgetWithMainBinding) this).getMainBinding();
		}

		if (bindingValue != null && bindingValue.isBindingValid()) {
			if (bindingValue instanceof BindingValue) {
				BindingPathElement bindingPathLastElement = ((BindingValue) bindingValue).getBindingPathLastElement();

				if (bindingPathLastElement instanceof DMProperty) { // Binded on a property -> use the property name as key
					return ((DMProperty) bindingPathLastElement).getName(); // Do not clean key here, we need to use the property name as is
																			// to be able to retrieve the property based on the key.
				}
			}

			return null;
		}

		String label = getLabel();

		if (StringUtils.isEmpty(label)) {
			label = getGeneratedProcessInstanceDictionaryKey();
		}

		return label == null ? null : ToolBox.cleanStringForProcessDictionaryKey(label);
	}

	public String getGeneratedProcessInstanceDictionaryKey() {
		return getGeneratedProcessInstanceDictionaryKey(-1);
	}

	protected String getGeneratedProcessInstanceDictionaryKey(int numberOfPreviousWidgetWithSameGeneratedLabel) {
		String label = null;

		if (this instanceof IELabelWidget) {
			label = ((IELabelWidget) this).getValue();
		} else if (this instanceof IEHeaderWidget) {
			label = ((IEHeaderWidget) this).getValue();
		} else if (this instanceof IETabWidget) {
			label = ((IETabWidget) this).getTitle();
		} else if (getLabel() != null && getLabel().trim().length() > 0) {
			label = getLabel();
		} else if (isInHTMLList()) {
			HTMLListDescriptor desc = getHTMLListDescriptor();
			IENonEditableTextWidget header = desc.findHeaderForWidget(this);
			if (header != null) {
				label = header.getValue();
			}
		}

		if (label == null) {
			if (getParent() == null) {
				return null;
			}

			if (getParent() instanceof IEBlocWidget) {
				return null;
			}

			if (getParent() instanceof IESequenceWidget) {
				if (this instanceof IEWidgetWithValueList) {
					numberOfPreviousWidgetWithSameGeneratedLabel++;
				}

				IESequenceWidget p = (IESequenceWidget) getParent();
				if (p.getPrevious(this) != null) {
					if (p.getPrevious(this) instanceof IELabelWidget) {
						label = ((IELabelWidget) p.getPrevious(this)).getValue();
					} else {
						label = ((IEWidget) p.getPrevious(this))
								.getGeneratedProcessInstanceDictionaryKey(numberOfPreviousWidgetWithSameGeneratedLabel);
						numberOfPreviousWidgetWithSameGeneratedLabel = 0;
					}
				} else {
					// this is the first of the sequence
					if (p.getParent() instanceof IETDWidget) {
						IETDWidget td = (IETDWidget) p.getParent();
						IESequenceTD tdSeq = td.getParent();
						if (tdSeq.getPrevious(td) != null) {
							IETDWidget previousTD = (IETDWidget) tdSeq.getPrevious(td);
							if ((IEWidget) previousTD.getSequenceWidget().getLast() != null) {
								label = ((IEWidget) previousTD.getSequenceWidget().getLast())
										.getGeneratedProcessInstanceDictionaryKey(numberOfPreviousWidgetWithSameGeneratedLabel);
								numberOfPreviousWidgetWithSameGeneratedLabel = 0;
							} else {
								while (previousTD != null) {
									previousTD = (IETDWidget) tdSeq.getPrevious(previousTD);
									if (previousTD != null && (IEWidget) previousTD.getSequenceWidget().getLast() != null) {
										label = ((IEWidget) previousTD.getSequenceWidget().getLast())
												.getGeneratedProcessInstanceDictionaryKey(numberOfPreviousWidgetWithSameGeneratedLabel);
										numberOfPreviousWidgetWithSameGeneratedLabel = 0;
										break;
									}
								}
							}
						} else {
							IEWidget htmlTable = td.getParent();
							while (htmlTable != null && !(htmlTable instanceof IEHTMLTableWidget)) {
								htmlTable = (IEWidget) htmlTable.getParent();
							}

							if (htmlTable instanceof IEHTMLTableWidget) {
								label = htmlTable.getGeneratedProcessInstanceDictionaryKey(numberOfPreviousWidgetWithSameGeneratedLabel);
								numberOfPreviousWidgetWithSameGeneratedLabel = 0;
							}
						}
					} else if (getParent() instanceof IESequenceWidget) {
						label = ((IEWidget) getParent())
								.getGeneratedProcessInstanceDictionaryKey(numberOfPreviousWidgetWithSameGeneratedLabel);
						numberOfPreviousWidgetWithSameGeneratedLabel = 0;
					}
				}
			}
		}

		if (label != null) {
			return label + (numberOfPreviousWidgetWithSameGeneratedLabel > 0 ? "_" + numberOfPreviousWidgetWithSameGeneratedLabel : "");
		}

		return null;
	}

	public boolean getIsAcceptableForAnyProcessInstanceDictionary() {
		if (!(getClass() == IECheckBoxWidget.class) && !(getClass() == IEDropDownWidget.class) && !(getClass() == IEHyperlinkWidget.class)
				&& !(getClass() == IERadioButtonWidget.class) && !(getClass() == IEStringWidget.class)
				&& !(getClass() == IETextAreaWidget.class) && !(getClass() == IETextFieldWidget.class)
				&& !(getClass() == IEWysiwygWidget.class) && !(getClass() == IEBrowserWidget.class)) {
			return false;
		}

		if (isInRepetition() && (this instanceof IECheckBoxWidget || this instanceof IERadioButtonWidget)) {
			return false;
		}

		if (this instanceof IEControlWidget && ((IEControlWidget) this).getIsFilterForRepetition() != null) {
			return false;
		}

		if (this instanceof IEHyperlinkWidget && ((IEHyperlinkWidget) this).isCustomButton()) {
			return false;
		}

		if (getProcessInstanceDictionaryKey() == null) {
			return false;
		}

		return true;
	}

	public boolean getIsAcceptableForProcessInstanceDictionary(FlexoProcess widgetCurrentProcess, FlexoProcess targettingProcess) {
		if (!getIsAcceptableForAnyProcessInstanceDictionary()) {
			return false;
		}

		if (isInRepetition()) {
			widgetCurrentProcess = repetition().getInferredListProcess();
			if (widgetCurrentProcess == null) {
				return false;
			}
		}

		// Check if there is binding
		AbstractBinding bindingValue = null;
		if (this instanceof IEEditableTextWidget) {
			bindingValue = ((IEEditableTextWidget) this).getBindingValue();
		} else if (this instanceof IENonEditableTextWidget) {
			bindingValue = ((IENonEditableTextWidget) this).getBindingValue();
		} else if (this instanceof IEBrowserWidget) {
			bindingValue = ((IEBrowserWidget) this).getBindingSelection();
		} else if (this instanceof IERadioButtonWidget) {
			bindingValue = ((IERadioButtonWidget) this).getBindingChecked();
		} else if (this instanceof IECheckBoxWidget) {
			bindingValue = ((IECheckBoxWidget) this).getBindingChecked();
		} else if (this instanceof IEHyperlinkWidget) {
			bindingValue = ((IEHyperlinkWidget) this).getBindingValue();
		} else if (this instanceof IEDropDownWidget) {
			bindingValue = ((IEDropDownWidget) this).getBindingSelection();
		}

		if (bindingValue != null && bindingValue.isBindingValid()) {
			if (bindingValue instanceof BindingValue) {
				BindingPathElement bindingPathLastElement = ((BindingValue) bindingValue).getBindingPathLastElement();

				if (bindingPathLastElement != null && bindingPathLastElement.getEntity() != null) {
					return bindingPathLastElement.getEntity() == targettingProcess.getBusinessDataType();
				}
			}

			return false;
		}

		return widgetCurrentProcess == targettingProcess;
	}

	protected static List<Object> parseValueListToAppropriateType(String value, String values, TextFieldType type, Domain domain,
			FlexoProcess process) {
		List<Object> result = new ArrayList<Object>();

		if (type == TextFieldType.KEYVALUE && domain != null) {
			Object[] o = domain.getSortedKeys();
			for (int i = 0; i < o.length; i++) {
				result.add(((Key) o[i]).getName());
			}
			return result;
		}

		if (type == TextFieldType.STATUS_LIST && process != null && process.getStatusList().size() > 0) {
			for (Status status : process.getStatusList().getStatus()) {
				result.add(status.getName());
			}
			return result;
		}

		List<String> parsedCsv = ToolBox.parseCsvLine(values);

		if (parsedCsv.isEmpty() && !StringUtils.isEmpty(value)) {
			parsedCsv.add(value);
		}

		if (type == TextFieldType.DATE) {
			// Need to parse all dates together to infer the correct pattern
			Date[] dates = DateUtils.parseDate(parsedCsv.toArray(new String[0]));
			if (dates != null) {
				result.addAll(Arrays.asList(dates));
			}

			return result;
		}

		for (String valueString : parsedCsv) {
			valueString = valueString.trim().length() > 0 ? valueString.trim() : valueString;

			if (type == null || type == TextFieldType.TEXT || type == TextFieldType.STATUS_LIST || type == TextFieldType.KEYVALUE) {
				result.add(valueString);
			} else // Number
			{
				try {
					switch (type) {
					case DOUBLE:
						result.add(new Double(valueString));
						break;
					case FLOAT:
						result.add(new Float(valueString));
						break;
					case INTEGER:
						result.add(new Integer(valueString));
						break;
					}
				} catch (NumberFormatException e) {
					logger.info("Cannot use 'valueString' as prototype value as it is not of type " + type.getName());
				}
			}
		}

		return result;
	}
}