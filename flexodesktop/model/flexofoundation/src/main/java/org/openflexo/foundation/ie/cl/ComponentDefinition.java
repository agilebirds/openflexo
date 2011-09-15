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
package org.openflexo.foundation.ie.cl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;


import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.dm.DMType.KindOfType;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.DummyComponentInstance;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.action.DuplicateComponentAction;
import org.openflexo.foundation.ie.action.GenerateComponentScreenshot;
import org.openflexo.foundation.ie.action.LabelizeComponentAction;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.ie.cl.action.ShowComponentUsage;
import org.openflexo.foundation.ie.dm.ComponentDeleteRequest;
import org.openflexo.foundation.ie.dm.ComponentDeleted;
import org.openflexo.foundation.ie.dm.ComponentLoaded;
import org.openflexo.foundation.ie.dm.ComponentNameChanged;
import org.openflexo.foundation.ie.dm.ComponentNameChanged2;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.CompoundIssue;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;

/**
 * Abstract class representing a component, but only the definition, not the
 * component itself (no need to load the component to handle a
 * ComponentDefinition)
 *
 * @author sguerin
 *
 */
public abstract class ComponentDefinition extends IECLObject implements InspectableObject, Serializable, FlexoObserver, Bindable, Sortable
{

	public static final ComponentComparator COMPARATOR = new ComponentComparator();

	public static class ComponentComparator implements Comparator<ComponentDefinition>
	{
		protected ComponentComparator()
		{
		}

		/**
		 * Overrides compare
		 *
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ComponentDefinition o1, ComponentDefinition o2)
		{
			return o1.getName().compareTo(o2.getName());
		}

	}

	private static final Logger logger = Logger.getLogger(ComponentDefinition.class.getPackage().getName());

	private Vector<ComponentInstance> componentInstances = new Vector<ComponentInstance>();

	private DummyComponentInstance _dummyComponentInstance;

	private ComponentDMEntity _componentDMEntity = null;

	protected FlexoComponentFolder _folder;

	protected String _componentName;

	private String _input;

	private String _behavior;

	private boolean hasTabContainer=false;

	private int index  = -1;

	protected ComponentDefinition(FlexoComponentLibrary componentLibrary)
	{
		super(componentLibrary);
	}

	protected ComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject prj) throws DuplicateResourceException
			{
		this(componentLibrary);
		_componentName = aComponentName;
		createsComponentDMEntityIfRequired();
		if (aFolder != null) {
			aFolder.addToComponents(this);
		}
		componentLibrary.handleNewComponentCreated(this);
			}

	public ComponentDefinition(FlexoComponentLibraryBuilder builder)
	{
		this(builder.componentLibrary);
	}

	@SuppressWarnings("unchecked")
	public DummyComponentInstance getDummyComponentInstance() {
		if (_dummyComponentInstance==null) {
			return _dummyComponentInstance = new DummyComponentInstance(this);
		}
		return _dummyComponentInstance;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
	{
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddComponent.actionType);
		returned.add(AddComponentFolder.actionType);
		returned.add(DuplicateComponentAction.actionType);
		returned.add(LabelizeComponentAction.actionType);
		returned.add(GenerateComponentScreenshot.actionType);
		returned.add(ShowComponentUsage.actionType);
		return returned;
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "COMPONENT." + getName();
	}

	@Override
	public String getName()
	{
		return _componentName;
	}

	@Override
	public void setName(String aName) throws DuplicateResourceException, DuplicateClassNameException, InvalidNameException
	{
		setComponentName(aName);
	}

	@Override
	public IEObject getParent() {
		return getFolder();
	}

	public FlexoComponentFolder getFolder()
	{
		return _folder;
	}

	public void setFolder(FlexoComponentFolder aFolder)
	{
		_folder = aFolder;
		if (!isDeserializing()) {
			if (_folder == null) {
				this.index = -1;
			} else {
				this.index = _folder.getComponents().size();
			}
		}
	}

	public String getComponentPrefix()
	{
		return _folder.getComponentPrefix();
	}

	public IEWOComponent getWOComponent()
	{
		return getWOComponent(null);
	}

	public IEWOComponent getWOComponent(FlexoProgress progress)
	{
		return getComponentResource().getResourceData(progress);
	}

	public String getComponentName()
	{
		return _componentName;
	}

	public void setComponentName(String name) throws DuplicateResourceException, DuplicateClassNameException, InvalidNameException
	{
		if (_componentName != null && !_componentName.equals(name) && name != null && !isDeserializing()) {
			if (!name.matches(IERegExp.JAVA_CLASS_NAME_REGEXP)) {
				throw new InvalidNameException();
			}

			if(ReservedKeyword.contains(name)) {
				throw new InvalidNameException();
			}
			if (getProject() != null) {
				ComponentDefinition cd = getComponentLibrary().getComponentNamed(name);
				if (cd != null && cd!=this) {
					throw new DuplicateResourceException(getComponentResource());
				}
				DMEntity e  = getProject().getDataModel().getEntityNamed(name);
				if (e != null && e!=getComponentDMEntity()) {
					throw new DuplicateClassNameException(name);
				}
				FlexoComponentResource resource = getComponentResource();
				if (resource != null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Renaming component resource !");
					}
					try {
						getProject().renameResource(resource, name);
					} catch (DuplicateResourceException e1) {
						throw e1;
					}
				}
				if (getComponentDMEntity() != null) {
					getComponentDMEntity().setEntityClassName(name);
					getComponentDMEntity().setName(name);
				}
				String oldComponentName = _componentName;
				_componentName = name;
				createsComponentDMEntityIfRequired();
				setChanged();
				notifyObservers(new ComponentNameChanged("name", this, oldComponentName, name));
				// After that, launch this notification for the CG resources
				setChanged();
				notifyObservers(new ComponentNameChanged2("name", this, oldComponentName, name));
			}
		} else {
			_componentName = name;
			createsComponentDMEntityIfRequired();
		}
	}

	protected ComponentDMEntity createsComponentDMEntityIfRequired()
	{
		return getComponentDMEntity();
	}

	public ComponentDMEntity getComponentDMEntity()
	{
		if (_componentDMEntity == null || _componentDMEntity.isDeleted()) {
			if (getProject() != null) {
				DMModel dmModel = getProject().getDataModel();
				ComponentRepository componentRepository = dmModel.getComponentRepository();
				_componentDMEntity = componentRepository.getComponentDMEntity(this);
				if (_componentDMEntity == null && _componentName != null && !_componentName.trim().equals("")) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Creates entry in ComponentRepository");
					}
					_componentDMEntity = new ComponentDMEntity(getProject().getDataModel(), this);
					getProject().getDataModel().getComponentRepository().registerEntity(_componentDMEntity);
				}
				if (_componentDMEntity != null) {
					_componentDMEntity.setComponentDefinition(this);
				}
			}
		}
		return _componentDMEntity;
	}

	public String requestDeletion()
	{
		setChanged();
		ComponentDeleteRequest request = new ComponentDeleteRequest(this);
		setChanged();
		notifyObservers(request);
		int count = 0;
		if (request.hasWarnings()) {
			StringBuffer buffer = new StringBuffer();
			Enumeration en = request.warnings().elements();
			while (en.hasMoreElements()) {
				buffer.append((String) en.nextElement()).append("\n");
				count++;
				if(count>9 && en.hasMoreElements()){
					buffer.append("and "+(request.warnings().size()-10)+" more...\n");
					break;
				}
			}
			return buffer.toString();
		} else {
			return null;
		}
	}

	@Override
	public void delete()
	{
		setChanged();
		notifyObservers(new ComponentDeleted(this));
		getProject().getFlexoComponentLibrary().delete(this);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Removing component !");
		}
		if (hasComponentResource() && getWOComponent()!=null) {
			getWOComponent().delete(false);
		}
		if (_componentDMEntity != null) {
			_componentDMEntity.delete();
		}
		super.delete();
		deleteObservers();
	}

	public abstract IEWOComponent createNewComponent();

	public boolean hasComponentResource() {
		return getComponentResource(false)!=null;
	}

	/**
	 * This method is final because if you want to override the default behaviour, you should instead override the method {@link #getComponentResource(boolean)}
	 * @return
	 */
	public final FlexoComponentResource getComponentResource() {
		return getComponentResource(true);
	}

	public abstract FlexoComponentResource getComponentResource(boolean createIfNotExists);

	public void notifyWOComponentHasBeenLoaded()
	{
		setChanged(false);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Notify observers that WO has been loaded");
		}
		notifyObservers(new ComponentLoaded(this));
	}

	public boolean isLoaded()
	{
		return hasComponentResource() && getComponentResource().isLoaded();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is
	 * NOT a recursive method
	 *
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects()
	{
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(this);
		return answer;
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = super.getAllEmbeddedValidableObjects();
		v.addAll(getWOComponent().getAllEmbeddedValidableObjects());
		return v;
	}

	@Override
	public ValidationModel getDefaultValidationModel()
	{
		if (getProject() != null) {
			return getProject().getIEValidationModel();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to project !");
			}
		}
		return null;
	}

	/**
	 * Returns a flag indicating if this object is valid according to default
	 * validation model
	 *
	 * @return boolean
	 */
	@Override
	public boolean isValid()
	{
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified
	 * validation model
	 *
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel)
	{
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default
	 * validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate()
	{
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied
	 * validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel)
	{
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied
	 * ValidationReport. Default validation model is used to perform this
	 * validation.
	 *
	 * @param report,
	 *            a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report)
	{
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied
	 * ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 *
	 * @param report,
	 *            a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel)
	{
		validationModel.validate(this, report);
	}

	/**
	 * Returns all the binding definitions for this component
	 *
	 * @return a vector of ComponentBindingDefinition
	 */
	public Vector<ComponentBindingDefinition> getBindingDefinitions()
	{
		if (getComponentDMEntity() != null) {
			return getComponentDMEntity().getBindingDefinitions();
		}
		return null;
	}

	private Vector<ComponentBindingDefinition> getMandatoryBindingDefinitions()
	{
		Enumeration<ComponentBindingDefinition> en = getBindingDefinitions().elements();
		Vector<ComponentBindingDefinition> answer = new Vector<ComponentBindingDefinition>();
		while (en.hasMoreElements()) {
			ComponentBindingDefinition compDef = en.nextElement();
			if (compDef.getIsMandatory()) {
				answer.add(compDef);
			}
		}
		return answer;
	}

	public void notifyComponentDefinitionsHasChanged() {
		for (ComponentInstance ci : getComponentInstances()) {
			ci.setChanged();
		}
	}

	public boolean isAssignableFromDA()
	{
		Vector mandatoryBD = getMandatoryBindingDefinitions();
		if (mandatoryBD.size() == 0) {
			return true;
		}
		Enumeration en = mandatoryBD.elements();
		while (en.hasMoreElements()) {
			ComponentBindingDefinition cbd = (ComponentBindingDefinition) en.nextElement();
			if (cbd.getType() == null || !cbd.getType().isEOEntity() || !daEntitiesContainsType(getProject(),cbd.getType())) {
				return false;
			}
		}
		return true;
	}

	public boolean isOperation() {
		return this instanceof OperationComponentDefinition;
	}

	public boolean isPopup() {
		return this instanceof PopupComponentDefinition;
	}

	public boolean isTab() {
		return this instanceof TabComponentDefinition;
	}

	public boolean isPage() {
		return true;
	}

	private static boolean daEntitiesContainsType(FlexoProject project, DMType type)
	{
		return type.getBaseEntity() == project.getDataModel().getEntityNamed(String.class.getName()) || type.getBaseEntity() == project.getDataModel().getEntityNamed(Integer.class.getName());
	}

	public ComponentBindingDefinition bindingDefinitionNamed(String aName)
	{
		if (getComponentDMEntity() != null) {
			return getComponentDMEntity().bindingDefinitionNamed(aName);
		}
		return null;
	}

	/*
	 * Commented because bindings are no more stored on the ComponentDefinition
    public void notifyBindingDefinitionAdded(BindingDefinition bindingDefinition)
    {
        setChanged();
        notifyObservers(new BindingAdded(bindingDefinition));
    }

    public void notifyBindingDefinitionRemoved(BindingDefinition bindingDefinition)
    {
        setChanged();
        notifyObservers(new BindingRemoved(bindingDefinition));
    }
	 */
	private BindingModel _bindingModel = null;

	@Override
	public BindingModel getBindingModel()
	{
		if (_bindingModel == null) {
			if (getComponentDMEntity() != null) {
				_bindingModel = new ComponentDefinitionBindingModel();
			}
		}
		return _bindingModel;
	}

	/**
	 *
	 * Binding model for a component definition
	 *
	 * @author sguerin
	 */
	public class ComponentDefinitionBindingModel extends BindingModel
	{
		public static final String COMPONENT_BINDING_VARIABLE_NAME = "component";

		private BindingVariable _componentVariable;

		public ComponentDefinitionBindingModel()
		{
			super();
			_componentVariable = new BindingVariable(ComponentDefinition.this, getProject().getDataModel(), FlexoLocalization
					.localizedForKey("access_to_the_current_component"));
			_componentVariable.setVariableName(COMPONENT_BINDING_VARIABLE_NAME);
			_componentVariable.setType(DMType.makeResolvedDMType(getComponentDMEntity()));

		}

		@Override
		public int getBindingVariablesCount()
		{
			return 1;
		}

		@Override
		public BindingVariable getBindingVariableAt(int index)
		{
			return getComponentBindingVariable();

		}

		public BindingVariable getComponentBindingVariable()
		{
			return _componentVariable;
		}

		@Override
		public boolean allowsNewBindingVariableCreation()
		{
			return false;
		}

	}

	/**
	 *
	 */
	public ComponentBindingDefinition createNewBinding()
	{
		if (getComponentDMEntity() != null) {
			String newPropertyName = getProject().getDataModel().getNextDefautPropertyName(getComponentDMEntity());
			DMProperty newProperty = new DMProperty(getProject().getDataModel(), /* getComponentDMEntity(), */newPropertyName, null,
					DMCardinality.SINGLE, false, true, DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
			boolean success = getComponentDMEntity().registerProperty(newProperty,true);
			ComponentBindingDefinition reply = getComponentDMEntity().bindingDefinitionForProperty(newProperty);
			if(success && reply!=null) {
				getComponentDMEntity().addToMandatoryBindingProperties(newProperty);
			}
			return reply;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not create binding: cannot access ComponentDMEntity !");
			}
			return null;
		}
	}

	public void deleteBinding(ComponentBindingDefinition bd)
	{
		if(bd!=null && bd.getProperty()!=null) {
			bd.getProperty().delete();
		}
	}

	public boolean isBindingDefinitionDeletable(ComponentBindingDefinition bd)
	{
		return true;
	}

	public String getBehavior()
	{
		return _behavior;
	}

	public void setBehavior(String behavior)
	{
		_behavior = behavior;
	}

	public String getInput()
	{
		return _input;
	}

	public void setInput(String input)
	{
		_input = input;
	}

	public String getNameRegexp()
	{
		return IERegExp.JAVA_CLASS_NAME_REGEXP;
	}

	@Override
	public String toString()
	{
		return getComponentName();
	}

	public boolean getHasTabContainer()
	{
		return hasTabContainer;
	}

	public void setHasTabContainer(boolean hasTabContainer)
	{
		this.hasTabContainer = hasTabContainer;
	}

	public ComponentBindingDefinition getBindingDefinitionTyped(DMEntity type)
	{
		return getBindingDefinitionTyped(DMType.makeResolvedDMType(type));
	}

	public ComponentBindingDefinition getBindingDefinitionTyped(DMType type)
	{
		Enumeration<ComponentBindingDefinition> en = getBindingDefinitions().elements();
		while (en.hasMoreElements()) {
			ComponentBindingDefinition bd = en.nextElement();
			if (bd.getIsMandatory() && bd.getType() != null && bd.getType().equals(type)) {
				return bd;
			}
		}
		return null;
	}

	public boolean isIndexed() {
		return this.index>-1;
	}

	@Override
	public int getIndex()
	{
		if (isBeingCloned()) {
			return -1;
		}
		if (index==-1 && getCollection()!=null) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index)
	{
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index,index,this);
		if (getIndex()!=index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index",null,getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue()
	{
		return getIndex();
	}

	@Override
	public void setIndexValue(int index) {
		if (this.index==index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyModification("index", old, index);
		if (!isDeserializing() && !isCreatedByCloning() && getFolder()!=null) {
			getFolder().setChanged();
			getFolder().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public ComponentDefinition[] getCollection()
	{
		if (getFolder()==null) {
			return null;
		}
		return getFolder().getComponents().toArray(new ComponentDefinition[0]);
	}

	public Vector<ComponentInstance> getComponentInstances() {
		return componentInstances;
	}

	public void addToComponentInstances(ComponentInstance instance) {
		addToComponentInstances(instance,true);
	}

    public void addToComponentInstances(ComponentInstance instance, boolean notify) {
		if (!componentInstances.contains(instance) && !(instance instanceof DummyComponentInstance)) {
			if (!instance.getXMLResourceData().getFlexoXMLFileResource().isConverting()) {
				componentInstances.add(instance);
				if(notify)setChanged();
			} else
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Found resource converting, ignoring component instance");
				}
		}
	}

	public void removeFromComponentInstances(ComponentInstance instance) {
		if (componentInstances.remove(instance)) {
			setChanged();
		}
	}

	abstract public List<OperationNode> getAllOperationNodesLinkedToThisComponent();

	// =============================================================
	// ======================== Validation =========================
	// =============================================================

	public static class BindingsMustDefineType<CD extends ComponentDefinition ,CI extends ComponentInstance> extends ValidationRule<BindingsMustDefineType<CD,CI>,CD>
	{

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public BindingsMustDefineType()
		{
			super(ComponentDefinition.class,"binding_must_define_a_type");
		}

		/**
		 * Overrides applyValidation
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<BindingsMustDefineType<CD,CI>,CD> applyValidation(CD cd)
		{
			CompoundIssue<BindingsMustDefineType<CD,CI>,CD> issues = null;
			for (ComponentBindingDefinition cbd : cd.getBindingDefinitions()) {
				DMProperty p = cbd.getProperty();
				if (p.getType()==null || p.getType().getKindOfType()==KindOfType.UNRESOLVED && (p.getType().getName()==null || p.getType().getName().equals("null"))) {
					Vector<FixProposal<BindingsMustDefineType<CD,CI>,CD>> fixes = new Vector<FixProposal<BindingsMustDefineType<CD,CI>,CD>>();
					fixes.add(new SetType<BindingsMustDefineType<CD,CI>, CD, CI>(cbd, DMType.makeResolvedDMType(p.getDMModel().getDMEntity(String.class))));
					fixes.add(new SetType<BindingsMustDefineType<CD,CI>, CD, CI>(cbd, DMType.makeResolvedDMType(p.getDMModel().getDMEntity(Boolean.class))));
					fixes.add(new SetType<BindingsMustDefineType<CD,CI>, CD, CI>(cbd, DMType.makeResolvedDMType(p.getDMModel().getDMEntity(Object.class))));
					fixes.add(new DeleteBinding<BindingsMustDefineType<CD,CI>, CD, CI>(cbd));
					if (issues==null) {
						issues = new CompoundIssue<BindingsMustDefineType<CD,CI>,CD>(cd);
					}
					issues.addToContainedIssues(new ValidationError<BindingsMustDefineType<CD,CI>,CD>(this,cd,"binding_must_define_a_type",fixes));
				}
			}
			return issues;
		}

	}

	public static class DeleteBinding<V extends ValidationRule<V, CD>, CD extends ComponentDefinition, CI extends ComponentInstance> extends FixProposal<V,CD>
	{
		private ComponentBindingDefinition binding;

		public DeleteBinding(ComponentBindingDefinition def) {
			super("delete_binding_($binding.variableName)");
			this.binding = def;
		}

		@Override
		protected void fixAction() {
			((ComponentDefinition)getObject()).deleteBinding(binding);
		}

		public BindingDefinition getBinding() {
			return binding;
		}
	}

	/**
	 * @author gpolet
	 *
	 */
	public static class SetType<V extends ValidationRule<V, CD>, CD extends ComponentDefinition, CI extends ComponentInstance> extends FixProposal<V,CD>
	{

		private DMType type;

		private ComponentBindingDefinition binding;

		/**
		 * @param aMessage
		 */
		public SetType(ComponentBindingDefinition cbd, DMType type)
		{
			super("set_type_to_($type)");
			this.type = type;
			this.binding = cbd;
		}

		/**
		 * Overrides fixAction
		 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
		 */
		@Override
		protected void fixAction()
		{
			binding.getProperty().setType(type);
		}

		public DMType getType()
		{
			return type;
		}

	}

}
