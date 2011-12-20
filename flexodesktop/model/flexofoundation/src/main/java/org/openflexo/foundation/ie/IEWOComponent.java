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
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.DuplicateComponentAction;
import org.openflexo.foundation.ie.action.GenerateComponentScreenshot;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.cl.action.ShowComponentUsage;
import org.openflexo.foundation.ie.dm.ComponentDeleteRequest;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentNameChanged;
import org.openflexo.foundation.ie.dm.TopWidgetInserted;
import org.openflexo.foundation.ie.dm.TopWidgetRemoved;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.util.FlexoConceptualColor;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEControlWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITabWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoRadioManager;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents an abstract WOComponent
 * 
 * @author bmangez, sguerin
 */
public abstract class IEWOComponent extends IEObject implements XMLStorageResourceData, InspectableObject, Validable, Bindable {
	private static final Logger logger = Logger.getLogger(IEWOComponent.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private FlexoComponentResource _resource;

	private transient FlexoProject _project;

	private FlexoColor _mainColor;

	private FlexoColor _textColor;

	private String _helpText;

	private FlexoConceptualColor _mainConceptualColor;

	private FlexoConceptualColor _textConceptualColor;

	public static final String WOCOMPONENT_NAME_ATTRIBUTENAME = "woComponentName";

	private ComponentDefinition _componentDefinition;

	private Vector<RepetitionOperator> repetitions;

	private Vector<IESequenceTab> tabContainers;

	private FlexoRadioManager radioButtonManager = new FlexoRadioManager(this);

	/* Key: String, Value: FlexoModelObject */
	private BidiMap nameForWidgetMap = new DualHashBidiMap();

	private IESequenceWidget rootSequence;

	private Date lastUpdate;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEWOComponent(FlexoComponentBuilder builder) {
		this(builder.componentDefinition, builder.getProject());
		builder.woComponent = this;
		_resource = builder.resource;
	}

	/**
	 * @param model
	 * 
	 */
	public IEWOComponent(ComponentDefinition model, FlexoProject project) {
		super(project);
		if (project == null) {
			new Exception("No project at all, this is going to kill me!").printStackTrace();
		}
		setProject(project);
		setRootSequence(new IESequenceWidget(this, this, project));
		_componentDefinition = model;
		tabContainers = new Vector<IESequenceTab>();
		if (model != null) {
			model.addObserver(this);
		}
	}

	@Override
	public void initializeDeserialization(Object builder) {
		super.initializeDeserialization(builder);
		if (getProject() != null) {
			getProject().getBindingValueConverter().setPreProcessor(new BindingValue.DecodingPreProcessor() {
				@Override
				public String preProcessString(String aString) {
					if (!aString.startsWith("component.") && !aString.startsWith("$")) {
						return "component." + aString;
					}
					return aString;
				}
			});
		}
	}

	/**
     *
     */
	@Override
	public void finalizeDeserialization(Object builder) {
		if (rootSequence.getOperator() != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("There is an operator on the root sequence. This is not allowed, I will remove it.");
			}
			rootSequence.resetOperator();
		}
		rootSequence.simplifySequenceTree();
		super.finalizeDeserialization(builder);
		Iterator<IESequenceTab> i = tabContainers.iterator();
		while (i.hasNext()) {
			IESequenceTab tab = i.next();
			if (tab.isSubsequence()) {
				i.remove();
			}
		}
		if (getProject() != null) {
			getProject().getBindingValueConverter().setPreProcessor(null);
		}
		getComponentDefinition().setHasTabContainer(hasTabContainer());
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#delete()
	 */
	@Override
	public void delete() {
		delete(true);
	}

	public void delete(boolean deleteDefinition) {
		if (getFlexoResource() != null) {
			getFlexoResource().delete();
			_resource = null;
			if (_componentDefinition != null) {
				if (deleteDefinition) {
					ComponentDefinition cd = _componentDefinition;
					_componentDefinition = null;
					cd.delete();
				} else {
					_componentDefinition = null;
				}
			}
			super.delete();
			nameForWidgetMap.clear();
			nameForWidgetMap = null;
		}
	}

	@Override
	public IEObject getParent() {
		return null;// WOComponents have no father!
	}

	@Override
	public synchronized void setIsModified() {
		if (ignoreNotifications()) {
			return;
		}
		if (nameForWidgetMap != null) {
			nameForWidgetMap.clear();// Any modification is capable of modify the names of the objects.
		}
		lastUpdate = null;
		super.setIsModified();
		// lastUpdate = lastMemoryUpdate();
	}

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	@Override
	public Date getLastUpdate() {
		if (lastUpdate == null) {
			lastUpdate = lastMemoryUpdate();
		}
		if (lastUpdate == null) {
			lastUpdate = super.getLastUpdate();
		}
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(DuplicateComponentAction.actionType);
		returned.add(GenerateComponentScreenshot.actionType);
		returned.add(DropIEElement.actionType);
		returned.add(DropPartialComponent.actionType);
		returned.add(ShowComponentUsage.actionType);
		return returned;
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================
	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * WOComponent itself
	 * 
	 * @return this
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return this;
	}

	public ComponentDefinition getComponentDefinition() {
		if (_componentDefinition == null) {
			if (getProject() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No component def and no project for :" + getName());
				}
			}
			_componentDefinition = getProject().getFlexoComponentLibrary().getComponentNamed(getName());
		}
		return _componentDefinition;
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> v = new Vector<IObject>();
		v.add(getComponentDefinition());
		v.add(this);
		v.add(rootSequence);
		return v;
	}

	public FlexoRadioManager getRadioButtonManager() {
		return radioButtonManager;
	}

	public String getNameRegexp() {
		return IERegExp.JAVA_CLASS_NAME_REGEXP;
	}

	public ComponentDMEntity getComponentDMEntity() {
		if (getComponentDefinition() != null) {
			return getComponentDefinition().getComponentDMEntity();
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getComponentDefinition() != null) {
			return getComponentDefinition().getBindingModel();
		}
		return null;
	}

	@Override
	public String getDescription() {
		if (getComponentDefinition() != null && !isSerializing()) {
			if (getComponentDefinition().getDescription() == null || getComponentDefinition().getDescription().trim().length() == 0) {
				return getRootSequence().getDescription();
			} else {
				return getComponentDefinition().getDescription();
			}
		}
		return null;
	}

	@Override
	public void setDescription(String text) {
		if (getComponentDefinition() != null && !isDeserializing()) {
			getComponentDefinition().setDescription(text);
		}
	}

	@Override
	public boolean getHasSpecificDescriptions() {
		if (getComponentDefinition() != null && !isSerializing()) {
			return getComponentDefinition().getHasSpecificDescriptions();
		}
		return false;
	}

	@Override
	public void setHasSpecificDescriptions(boolean hasSpecificDescription) {
		if (getComponentDefinition() != null && !isDeserializing()) {
			getComponentDefinition().setHasSpecificDescriptions(hasSpecificDescription);
		}
	}

	@Override
	public Map<String, String> getSpecificDescriptions() {
		if (getComponentDefinition() != null && !isSerializing()) {
			return getComponentDefinition().getSpecificDescriptions();
		}
		return null;
	}

	@Override
	public void setSpecificDescriptions(Map<String, String> desc) {
		if (getComponentDefinition() != null && !isDeserializing()) {
			getComponentDefinition().setSpecificDescriptions(desc);
		}
	}

	@Override
	public String getSpecificDescriptionForKey(String key) {
		if (getComponentDefinition() != null && !isSerializing()) {
			return getComponentDefinition().getSpecificDescriptionForKey(key);
		}
		return null;
	}

	@Override
	public void setSpecificDescriptionsForKey(String description, String key) {
		if (getComponentDefinition() != null && !isDeserializing()) {
			getComponentDefinition().setSpecificDescriptionsForKey(description, key);
		}
	}

	@Override
	public String getName() {
		if (_componentDefinition != null) {
			return _componentDefinition.getName();
		}
		return null;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, DuplicateClassNameException, InvalidNameException {
		if (getComponentDefinition() != null) {
			getComponentDefinition().setName(name);
		}
	}

	public FlexoColor getMainColor() {
		return _mainColor;
	}

	public void setMainColor(FlexoColor c) {
		_mainColor = c;
		setChanged();
		notifyObservers(new DataModification(DataModification.BLOC_BG_CLOR_CHANGE, null, c));
	}

	public FlexoColor getTextColor() {
		return _textColor;
	}

	public void setTextColor(FlexoColor c) {
		_textColor = c;
		setChanged();
		notifyObservers(new DataModification(DataModification.BLOC_FG_CLOR_CHANGE, null, c));
	}

	public FlexoConceptualColor getConceptualMainColor() {
		if (_mainConceptualColor == null) {
			if (getMainColor() != null) {
				_mainConceptualColor = new FlexoConceptualColor.CustomColor(getMainColor());
			} else {
				_mainConceptualColor = FlexoConceptualColor.MAIN_COLOR;
			}
		}
		return _mainConceptualColor;
	}

	public FlexoConceptualColor getConceptualTextColor() {
		if (_textConceptualColor == null) {
			if (getTextColor() != null) {
				_textConceptualColor = new FlexoConceptualColor.CustomColor(getTextColor());
			} else {
				_textConceptualColor = FlexoConceptualColor.TEXT_COLOR;
			}
		}
		return _textConceptualColor;
	}

	@Override
	public String getHelpText() {
		return _helpText;
	}

	public void setHelpText(String text) {
		_helpText = text;
		setChanged();
		notifyModification("helpText", null, _helpText);
	}

	/**
	 * Return a Vector of all ComponentInstance objects involved in the definition of that component
	 * 
	 * @return a newly created Vector of ComponentInstance objects
	 */
	public Vector<ComponentInstance> getAllComponentInstances() {
		Vector allIEObjects = getAllEmbeddedIEObjects();
		Vector<ComponentInstance> returned = new Vector<ComponentInstance>();
		for (FlexoResource r : getFlexoResource().getDependentResources()) {
			if (r instanceof FlexoComponentResource) {
				for (ComponentInstance ci : ((FlexoComponentResource) r).getComponentDefinition().getComponentInstances()) {
					if (ci.getXMLResourceData() == this) {

					}
				}
			}
		}
		for (Enumeration en = allIEObjects.elements(); en.hasMoreElements();) {
			IEObject next = (IEObject) en.nextElement();
			if (next instanceof ComponentInstance) {
				returned.add((ComponentInstance) next);
			}
		}
		return returned;
	}

	public boolean isTopComponent() {
		Enumeration<IWidget> en = getRootSequence().getAllNonSequenceWidget().elements();
		boolean isTopComponent = en.hasMoreElements();// If root sequence is empty then this component is not a top component
		while (en.hasMoreElements()) {
			IEWidget widget = (IEWidget) en.nextElement();
			if (!widget.isTopComponent()) {
				return false;
			}
		}
		return isTopComponent;
	}

	public boolean isASingleHTMLTable() {
		Vector<IEWidget> v = getRootSequence().getInnerWidgets();
		return v.size() == 1 && v.firstElement() instanceof IEHTMLTableWidget;
	}

	public boolean isAListOfTableRows() {
		Enumeration en = getRootSequence().getInnerWidgets().elements();
		if (!en.hasMoreElements()) {
			return false;
		}
		while (en.hasMoreElements()) {
			IEWidget w = (IEWidget) en.nextElement();
			if (!(w instanceof ITableRow)) {
				return false;
			}
		}
		return true;
	}

	// ==========================================================================
	// ===================== Resource managing
	// ==================================
	// ==========================================================================

	@Override
	public FlexoComponentResource getFlexoResource() {
		return _resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoComponentResource) resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return _resource;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	/**
	 * Save this object using ResourceManager scheme
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification) Receive a
	 *      notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * 
	 *      Handles ComponentNameChanged notifications
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification aNotification) throws FlexoException {
		if (aNotification instanceof ComponentNameChanged) {

			ComponentNameChanged notification = (ComponentNameChanged) aNotification;
			Vector instances = getAllComponentInstances();

			for (Enumeration en = instances.elements(); en.hasMoreElements();) {
				ComponentInstance ci = (ComponentInstance) en.nextElement();
				if (ci.getComponentName().equals(notification.oldValue())) {
					ci.notifyComponentNameChanged(notification.component);
				}
			}
		}
		if (aNotification instanceof ComponentDeleteRequest) {
			ComponentDeleteRequest notification = (ComponentDeleteRequest) aNotification;
			for (Enumeration en = getAllComponentInstances().elements(); en.hasMoreElements();) {
				ComponentInstance ci = (ComponentInstance) en.nextElement();
				if (ci.getComponentName().equals(notification.component.getComponentName())) {
					notification.addToWarnings(notification.component.getComponentName() + " is used by " + getName());
				}
			}
		}
		if (aNotification instanceof ComponentDeleted) {
			ComponentDeleted notification = (ComponentDeleted) aNotification;
			for (Enumeration en = getAllComponentInstances().elements(); en.hasMoreElements();) {
				ComponentInstance ci = (ComponentInstance) en.nextElement();
				if (ci.getComponentName().equals(notification.component.getComponentName())) {
					IEReusableWidget[] reusable = findReusableWidgetForComponentInstance(ci);
					for (int i = 0; i < reusable.length; i++) {
						reusable[i].delete();
					}
					IEHyperlinkWidget[] hyperlink = findHyperlinksForComponentInstance(ci);
					for (int i = 0; i < hyperlink.length; i++) {
						hyperlink[i].setPopupComponentDefinition(null);
					}
				}
			}
		}
	}

	private IEReusableWidget[] findReusableWidgetForComponentInstance(ComponentInstance ci) {
		Vector allIEObjects = getAllEmbeddedIEObjects();
		Vector<IEReusableWidget> returned = new Vector<IEReusableWidget>();
		for (Enumeration en = allIEObjects.elements(); en.hasMoreElements();) {
			IEObject next = (IEObject) en.nextElement();
			if (next instanceof IEReusableWidget) {
				if (((IEReusableWidget) next).getReusableComponentInstance().equals(ci)) {
					returned.add((IEReusableWidget) next);
				}
			}
		}
		IEReusableWidget[] answer = new IEReusableWidget[returned.size()];
		int i = 0;
		Enumeration en = returned.elements();
		while (en.hasMoreElements()) {
			answer[i] = (IEReusableWidget) en.nextElement();
			i++;
		}
		return answer;
	}

	private IEHyperlinkWidget[] findHyperlinksForComponentInstance(ComponentInstance ci) {
		Vector allIEObjects = getAllEmbeddedIEObjects();
		Vector<IEHyperlinkWidget> returned = new Vector<IEHyperlinkWidget>();
		for (Enumeration en = allIEObjects.elements(); en.hasMoreElements();) {
			IEObject next = (IEObject) en.nextElement();
			if (next instanceof IEHyperlinkWidget) {
				if (((IEHyperlinkWidget) next).getPopupComponentDefinition() != null
						&& ((IEHyperlinkWidget) next).getPopupComponentDefinition().equals(ci.getComponentDefinition())) {
					returned.add((IEHyperlinkWidget) next);
				}
			}
		}
		IEHyperlinkWidget[] answer = new IEHyperlinkWidget[returned.size()];
		int i = 0;
		Enumeration en = returned.elements();
		while (en.hasMoreElements()) {
			answer[i] = (IEHyperlinkWidget) en.nextElement();
			i++;
		}
		return answer;
	}

	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return rootSequence.getAllButtonInterface();
	}

	public Vector<RepetitionOperator> getAllRepetitionOperator() {
		Vector<RepetitionOperator> reply = new Vector<RepetitionOperator>();
		Enumeration en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IEObject widget = (IEObject) en.nextElement();
			if (widget instanceof IESequence && ((IESequence) widget).getOperator() != null
					&& ((IESequence) widget).getOperator() instanceof RepetitionOperator) {
				reply.add((RepetitionOperator) ((IESequence) widget).getOperator());
			}
		}
		return reply;
	}

	public Vector<RepetitionOperator> getAllList() {
		if (repetitions == null) {
			repetitions = new Vector<RepetitionOperator>();
			Enumeration en = getAllEmbeddedIEObjects(true).elements();
			while (en.hasMoreElements()) {
				IObject o = (IObject) en.nextElement();
				if (o instanceof RepetitionOperator) {
					repetitions.add((RepetitionOperator) o);
				}
			}
		}
		return repetitions;
	}

	public Vector<IEHyperlinkWidget> getAvailableFlexoActions() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("getAvailableFlexoActions");
		}
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration en = getAllButtonInterface().elements();
		while (en.hasMoreElements()) {
			IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
			if (w.getIsFlexoAction()) {
				v.add(w);
			}
		}
		return v;
	}

	public Vector<IEHyperlinkWidget> getAvailableDisplayActions() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("getAvailableFlexoActions");
		}
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration en = getAllButtonInterface().elements();
		while (en.hasMoreElements()) {
			IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
			if (w.getIsDisplayAction()) {
				v.add(w);
			}
		}
		return v;
	}

	public String getInput() {
		if (_componentDefinition != null) {
			return _componentDefinition.getInput();
		} else {
			return null;
		}
	}

	public void setInput(String s) {
		if (_componentDefinition != null) {
			_componentDefinition.setInput(s);
		}
	}

	public String getBehavior() {
		if (_componentDefinition != null) {
			return _componentDefinition.getBehavior();
		} else {
			return null;
		}
	}

	public void setBehavior(String s) {
		if (_componentDefinition != null) {
			_componentDefinition.setBehavior(s);
		}
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
	 * @param _widget
	 * @return
	 */
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		return rootSequence.getAllDateTextfields();
	}

	public void getAllDateTexfields(IETopComponent top, Vector<IETextFieldWidget> v) {
		if (top instanceof IEBlocWidget) {
			IEBlocWidget block = (IEBlocWidget) top;
			v.addAll(block.getAllDateTextfields());
		} else if (top instanceof IEHTMLTableWidget) {
			v.addAll(((IEHTMLTableWidget) top).getAllDateTextfields());
		} else if (top instanceof IESequenceTopComponent) {
			Iterator<IETopComponent> i = ((IESequenceTopComponent) top).iterator();
			while (i.hasNext()) {
				IETopComponent obj = i.next();
				getAllDateTexfields(obj, v);
			}
		}
	}

	public boolean hasTabContainer() {
		return tabContainers != null && tabContainers.size() > 0;
	}

	public Vector<IESequenceTab> getTabContainers() {
		return tabContainers;
	}

	public Vector<IETabWidget> getAllTabWidget() {
		Vector<IETabWidget> v = new Vector<IETabWidget>();
		for (IESequenceTab tab : getTabContainers()) {
			for (IWidget w : tab.getAllNonSequenceWidget()) {
				v.add((IETabWidget) w);
			}
		}
		return v;
	}

	/**
	 * @return
	 */
	public static final String getTypeName() {
		return "WOComponent";
	}

	@Override
	public String toString() {
		return "IEWOComponent:" + getName();
	}

	public Vector<IEControlWidget> getFilterWidgetsForRepetition(RepetitionOperator rep) {
		Vector<IEControlWidget> v = new Vector<IEControlWidget>();
		Enumeration en = getAllEmbeddedIEObjects().elements();
		while (en.hasMoreElements()) {
			IEObject o = (IEObject) en.nextElement();
			if (o instanceof IEControlWidget && ((IEControlWidget) o).getIsFilterForRepetition() == rep) {
				v.add((IEControlWidget) o);
			}
		}
		return v;
	}

	public boolean hasHelpText() {
		return getHelpText() != null && getHelpText().trim().length() > 0;
	}

	public static class ComponentCannotHaveTwoListWithSameName extends
			ValidationRule<ComponentCannotHaveTwoListWithSameName, IEWOComponent> {
		public ComponentCannotHaveTwoListWithSameName() {
			super(IEWOComponent.class, "list_name_must_be_unique_in_a_component");
		}

		@Override
		public ValidationIssue<ComponentCannotHaveTwoListWithSameName, IEWOComponent> applyValidation(IEWOComponent wo) {
			Vector<RepetitionOperator> allList = wo.getAllList();
			if (allList.size() < 2) {
				return null;
			}
			Hashtable<String, RepetitionOperator> h = new Hashtable<String, RepetitionOperator>();
			Enumeration<RepetitionOperator> en = allList.elements();
			while (en.hasMoreElements()) {
				RepetitionOperator rep = en.nextElement();
				if (rep.getName() != null && h.get(rep.getName()) == null) {
					h.put(rep.getName(), rep);
				} else {
					ValidationError<ComponentCannotHaveTwoListWithSameName, IEWOComponent> error = new ValidationError<ComponentCannotHaveTwoListWithSameName, IEWOComponent>(
							this, wo, "list_name_must_be_unique_in_a_component");
					error.addToFixProposals(new ChangeListName(rep));
					return error;
				}
			}
			return null;
		}

	}

	public static class ChangeListName extends FixProposal<ComponentCannotHaveTwoListWithSameName, IEWOComponent> {
		public RepetitionOperator repetition;

		public ChangeListName(RepetitionOperator rep) {
			super("append_flexoID_to_current_list_name");
			repetition = rep;

		}

		@Override
		protected void fixAction() {
			repetition.setName(repetition.getName() + repetition.getFlexoID());
		}
	}

	public String getFirstTabContainerTitle() {
		IESequenceTab obj = null;
		Enumeration<IESequenceTab> en = getAllTabContainers().elements();
		while (en.hasMoreElements()) {
			obj = en.nextElement();
			return obj.getWOComponent().getName() + obj.getFlexoID() + "Tabs";
		}
		return null;
	}

	public IETabWidget findTab(TabComponentDefinition tabComponentDefinition) {
		Enumeration en = getAllEmbeddedIEObjects().elements();
		Object o = null;
		while (en.hasMoreElements()) {
			o = en.nextElement();
			if (o instanceof IETabWidget) {
				if (((IETabWidget) o).getTabComponentDefinition().equals(tabComponentDefinition)) {
					return (IETabWidget) o;
				}
			}
		}
		return null;
	}

	public Vector<IEHyperlinkWidget> getAllAbstractButtonWidgetInterface() {
		return getAllButtonInterface();
	}

	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IEObject top = (IEObject) en.nextElement();
			if (top instanceof IESequenceTab && ((IESequenceTab) top).isTabContainer()) {
				reply.add((IESequenceTab) top);
			}
		}
		return reply;

	}

	public Vector<TabComponentDefinition> getAllTabComponents(Vector<TabComponentDefinition> reply) {
		for (IESequenceTab container : getAllTabContainers()) {
			for (ITabWidget itab : container.getInnerWidgets()) {
				for (IETabWidget tab : itab.getAllTabs()) {
					TabComponentDefinition tabcd = tab.getTabComponentDefinition();
					if (!reply.contains(tabcd)) {
						reply.add(tabcd);
						// tabcd.getWOComponent().getAllTabComponents(reply);
					}
				}
			}
		}
		return reply;
	}

	public Vector<IETabWidget> getAllTabWidget(Vector<IETabWidget> reply) {
		for (IESequenceTab container : getAllTabContainers()) {
			for (ITabWidget itab : container.getInnerWidgets()) {
				for (IETabWidget tab : itab.getAllTabs()) {
					if (!reply.contains(tab)) {
						reply.add(tab);
						tab.getTabComponentDefinition().getWOComponent().getAllTabWidget(reply);
					}
				}
			}
		}
		return reply;
	}

	public IETabWidget getTabWidgetForTabComponent(TabComponentDefinition component) {
		Iterator<IESequenceTab> i = tabContainers.iterator();
		while (i.hasNext()) {
			IESequenceTab st = i.next();
			Iterator<IWidget> j = st.getAllNonSequenceWidget().iterator();
			while (j.hasNext()) {
				IETabWidget w = (IETabWidget) j.next();
				if (w.getTabComponentDefinition() == component) {
					return w;
				}
			}
		}
		return null;
	}

	public IESequenceTab getSequenceTabForTabComponent(TabComponentDefinition component) {
		IETabWidget tabWidget = getTabWidgetForTabComponent(component);
		if (tabWidget != null) {
			return tabWidget.getRootParent();
		}
		return null;
	}

	public void notifyWidgetAdded(IEWidget widget) {
		if (widget instanceof RepetitionOperator) {
			repetitions = null;
		} else if (widget instanceof IESequenceTab) {
			if (!((IESequenceTab) widget).isSubsequence() && !tabContainers.contains(widget)) {
				tabContainers.add((IESequenceTab) widget);
			}
			getComponentDefinition().setHasTabContainer(hasTabContainer());
		}
	}

	public void notifyWidgetRemoved(IEWidget widget) {
		if (widget instanceof RepetitionOperator) {
			repetitions = null;
		} else if (widget instanceof IESequenceTab) {
			if (!((IESequenceTab) widget).isSubsequence()) {
				tabContainers.remove(widget);
			}
			getComponentDefinition().setHasTabContainer(hasTabContainer());
		}
	}

	/**
	 * Returns all the strings ({@link IEStringWidget}) of this WOComponent. You should not use directly this method but instead implement a
	 * cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to be used to
	 * preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the strings contained in this component.
	 */
	@Override
	public Vector<IEStringWidget> getStrings() {
		Vector<IEStringWidget> v = new Vector<IEStringWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IEStringWidget) {
				v.add((IEStringWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the textfields ({@link IETextFieldWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the textfields contained in this component.
	 */
	@Override
	public Vector<IETextFieldWidget> getTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IETextFieldWidget) {
				v.add((IETextFieldWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the textareas ({@link IETextAreaWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the textareas contained in this component.
	 */
	@Override
	public Vector<IETextAreaWidget> getTextareas() {
		Vector<IETextAreaWidget> v = new Vector<IETextAreaWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IETextAreaWidget) {
				v.add((IETextAreaWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the dropdowns ({@link IEDropDownWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the dropdowns contained in this component.
	 */
	@Override
	public Vector<IEDropDownWidget> getDropdowns() {
		Vector<IEDropDownWidget> v = new Vector<IEDropDownWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IEDropDownWidget) {
				v.add((IEDropDownWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the browsers ({@link IEBrowserWidget}) of this WOComponent. You should not use directly this method but instead implement
	 * a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to be used
	 * to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the browsers contained in this component.
	 */
	@Override
	public Vector<IEBrowserWidget> getBrowsers() {
		Vector<IEBrowserWidget> v = new Vector<IEBrowserWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IEBrowserWidget) {
				v.add((IEBrowserWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the checkboxes ({@link IECheckBoxWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the checkboxes contained in this component.
	 */
	@Override
	public Vector<IECheckBoxWidget> getCheckboxes() {
		Vector<IECheckBoxWidget> v = new Vector<IECheckBoxWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IECheckBoxWidget) {
				v.add((IECheckBoxWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the radio buttons ({@link IERadioButtonWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the radio buttons contained in this component.
	 */
	@Override
	public Vector<IERadioButtonWidget> getRadios() {
		Vector<IERadioButtonWidget> v = new Vector<IERadioButtonWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IERadioButtonWidget) {
				v.add((IERadioButtonWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the buttons ({@link IEButtonWidget}) of this WOComponent. You should not use directly this method but instead implement a
	 * cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to be used to
	 * preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the buttons contained in this component.
	 */
	public Vector<IEButtonWidget> getButtons() {
		Vector<IEButtonWidget> v = new Vector<IEButtonWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o.getClass() == IEButtonWidget.class) {
				v.add((IEButtonWidget) o);
			}
		}
		return v;
	}

	/**
	 * Returns all the hyperlinks ({@link IEHyperlinkWidget}) of this WOComponent. You should not use directly this method but instead
	 * implement a cache mechanism and use the benefits of the methods notifyWidgetAdded and notifyWidgetRemoved. This method is intended to
	 * be used to preserve the order of this component when presenting this to a user.
	 * 
	 * @return all the hyperlinks contained in this component.
	 */
	@Override
	public Vector<IEHyperlinkWidget> getHyperlinks() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject o = en.nextElement();
			if (o instanceof IEHyperlinkWidget) {
				v.add((IEHyperlinkWidget) o);
			}
		}
		return v;
	}

	public Hashtable<IEObject, Hashtable<String, String>> getLocalizableObjects() {
		Hashtable<IEObject, Hashtable<String, String>> reply = new Hashtable<IEObject, Hashtable<String, String>>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject object = en.nextElement();
			if (object instanceof IEWidget) {
				Hashtable<String, String> props = ((IEWidget) object).getLocalizedProperties();
				if (props.size() > 0) {
					reply.put((IEWidget) object, props);
				}
			}
		}
		return reply;
	}

	public synchronized BidiMap getNameForWidgetMap() {
		if (nameForWidgetMap.size() == 0) {
			buildNameForWidgetMap();
		}
		return nameForWidgetMap;
	}

	public synchronized String getUniqueNameForWidget(IEWidget widget) {
		if (getNameForWidgetMap().getKey(widget) != null) {
			return (String) getNameForWidgetMap().getKey(widget);
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Widget "
					+ widget
					+ " had no computed name meaning that it was probably not properly embedded in the WOComponent."
					+ "I will compute its name now, but this can be source of instability in the names of the widgets. You should try to find why that widget was not embedded.");
		}
		return computeAndStoreNameForWidget(widget);
	}

	private synchronized String computeAndStoreNameForWidget(IEWidget widget) {
		String base = ToolBox.uncapitalize(widget.getNiceName());
		String attempt = base;
		if (nameForWidgetMap.get(attempt) != null || widget instanceof IELabelWidget || ReservedKeyword.contains(attempt)) {
			String widgetType = widget.getWidgetType();
			if (widget instanceof IESequence && ((IESequence<?>) widget).getOperator() != null) {
				widgetType = ((IESequence<?>) widget).getOperator().getWidgetType();
			}
			if (base.toLowerCase().indexOf(widgetType.toLowerCase()) == -1) {
				base = base + widgetType;
			}
			base = ToolBox.uncapitalize(base);
			int i = 1;
			attempt = base;
			while (nameForWidgetMap.get(attempt) != null || ReservedKeyword.contains(attempt)) {
				i++;
				attempt = base + String.valueOf(i);
			}
		}
		nameForWidgetMap.put(attempt, widget);
		return attempt;
	}

	private void buildNameForWidgetMap() {
		Vector<IEWidget> widgets = new Vector<IEWidget>();
		Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IObject object = en.nextElement();
			if (object instanceof IEWidget) {
				IEWidget widget = (IEWidget) object;
				widgets.add(widget);
			}
		}
		Collections.sort(widgets, new Comparator<FlexoModelObject>() {

			@Override
			public int compare(FlexoModelObject o1, FlexoModelObject o2) {
				return o1.compareTo(o2);
			}

		});
		for (DMObject obj : getComponentDMEntity().getOrderedChildren()) {
			nameForWidgetMap.put(ToolBox.uncapitalize(obj.getName()), obj);
		}
		for (IEWidget widget : widgets) {
			computeAndStoreNameForWidget(widget);
		}
	}

	public IESequenceWidget getRootSequence() {
		return rootSequence;
	}

	public void setRootSequence(IESequenceWidget rootSequence) {
		if (this.rootSequence != null) {
			this.rootSequence.setParent(null);
		}
		this.rootSequence = rootSequence;
		if (this.rootSequence != null) {
			this.rootSequence.setParent(this);
		}
	}

	public boolean hasAtLeastOneTabDefined() {
		return getAvailableTabs().size() > 0;
	}

	public Vector<TabComponentDefinition> getAvailableTabs() {
		return getAllTabComponents(new Vector<TabComponentDefinition>());
	}

	public Vector<IEHyperlinkWidget> getAllAbstractButtonNeedingAWorkflowConditional() {
		Vector<IEHyperlinkWidget> reply = new Vector<IEHyperlinkWidget>();
		Enumeration en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IEObject widget = (IEObject) en.nextElement();
			if (widget instanceof IEHyperlinkWidget && ((IEHyperlinkWidget) widget).needAWorkflowConditional()) {
				reply.add((IEHyperlinkWidget) widget);
			}
		}
		return reply;
	}

	public void removeTopWidget(IEWidget widget) {
		rootSequence.removeFromInnerWidgets(widget);
		setChanged();
		notifyObservers(new TopWidgetRemoved(widget));
	}

	public int indexOfTopWidget(IEWidget topWidget) {
		return rootSequence.indexOf(topWidget);
	}

	public void replaceWidgetByReusable(IEWidget oldWidget, IEReusableWidget newWidget) {
		int index = rootSequence.indexOf(oldWidget);
		newWidget.setIndex(index);
		removeTopWidget(oldWidget);
		insertTopWidgetAtIndex(newWidget, index);
	}

	public Enumeration<IEWidget> topComponents() {
		return rootSequence.elements();
	}

	public Iterator<IEWidget> iterator() {
		return rootSequence.iterator();
	}

	public int length() {
		return rootSequence.getWidgetCount();
	}

	public void addToTopWidgetList(IEWidget widget) {
		rootSequence.addToInnerWidgets(widget);
		widget.setParent(rootSequence);
	}

	public void removeFromTopWidgetList(IEWidget topWidget) {
		rootSequence.removeFromInnerWidgets(topWidget);
	}

	public void insertTopWidgetAtIndex(IEWidget topWidget, int index) {
		rootSequence.insertElementAt(topWidget, Math.min(rootSequence.getWidgetCount(), index));
		topWidget.setParent(this);
		topWidget.setWOComponent(this);
	}

	public void notifyTopWidgetInserted(TopWidgetInserted tci) {
		setChanged();
		notifyObservers(tci);
	}

	public void notifyTopComponentRemoved(TopWidgetRemoved tcr) {
		setChanged();
		notifyObservers(tcr);
	}

	public Iterator getRealTopComponents() {
		return rootSequence.getAllNonSequenceWidget().iterator();
	}

	public Vector<IESequence> getAllSequenceWithConditionalOperators() {

		Vector<IESequence> reply = new Vector<IESequence>();
		Enumeration en = getAllEmbeddedIEObjects(true).elements();
		while (en.hasMoreElements()) {
			IEObject widget = (IEObject) en.nextElement();
			if (widget instanceof IESequence && ((IESequence) widget).getOperator() != null
					&& ((IESequence) widget).getOperator() instanceof ConditionalOperator) {
				reply.add((IESequence) widget);
			}
		}
		return reply;
	}
}
