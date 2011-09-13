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
package org.openflexo.foundation.ie.operator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingValue.BindingPathElement;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTypeOwner;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.ExcellButtonStateChange;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.RefreshButtonStateChange;
import org.openflexo.foundation.ie.util.ListType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEControlWidget;
import org.openflexo.foundation.ie.widget.IEEditableFieldWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WorkflowPathToOperationNode;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class RepetitionOperator extends IEOperator implements DMTypeOwner
{

	protected static final Logger logger = FlexoLogger.getLogger(RepetitionOperator.class.getPackage().getName());

	// private RepetitionBindingModel _bindingModel;

	private BindingValue _bindingItem; // Could be considered as a BindingValue because defined as GET_SET

	private AbstractBinding _listAccessor;

	private boolean _fetchObjects = false;

	private boolean _refreshButton = true;

	private boolean _excelButton = false;

	private boolean _hasBatch = true;

	private int _defaultBatchValue = 20;

	private ListType _listType = ListType.FETCH;

	private DMEntity entity;

	private String entityName;

	private DMMethod permanentFilter;

	private String permanentFilterMethodSignature;

	private DMType _contentType;

	private WidgetBindingDefinition _bindingListDefinition = null;

	private WidgetBindingDefinition _bindingItemDefinition = null;

	public RepetitionOperator(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public RepetitionOperator(IEWOComponent wo, IESequence sequence, FlexoProject project)
	{
		super(wo, sequence, project);
	}

	@Override
	public String getFullyQualifiedName()
	{
		return "Repetition."+getName();
	}

	@Override
	public String getClassNameKey()
	{
		return "repetition";
	}

	@Override
	public String getDefaultInspectorName()
	{
		return "Repetition.inspector";
	}

	@Override
	public String getInspectorName()
	{
		return getDefaultInspectorName();
	}

	public DMType getContentType()
	{
		if (_contentType == null) {
			if (_bindingItem!=null && _bindingItem.getAccessedType()!=null){
				_contentType = _bindingItem.getAccessedType().clone();
				_contentType.setOwner(this);
			} else {
				_contentType = DMType.makeResolvedDMType(Object.class, getProject());
				_contentType.setOwner(this);
			}
		}
		return _contentType;
	}

	public void setContentType(DMType contentType)
	{
		DMType oldContentType = _contentType;
		_contentType = contentType;
		if (_contentType!=null) {
			_contentType.setOwner(this);
		}
		_bindingListDefinition = null;
		_bindingItemDefinition = null;
		setChanged();
		notifyObservers(new IEDataModification("contentType", oldContentType, contentType));
	}

	public DMType getItemType() {
		DMType type = getContentType();
		if (_listType==ListType.FETCH && !_fetchObjects) {
			type = DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity("com.webobjects.foundation", "NSDictionary"));
		}
		return type.clone();
	}

	public WidgetBindingDefinition getBindingItemDefinition()
	{
		if (_bindingItemDefinition == null) {
			/*DMType type = getContentType();
    		if (_listType==ListType.FETCH && !_fetchObjects)
    			type = DMType.makeResolvedDMType(getProject().getDataModel().getDMEntity("com.webobjects.foundation", "NSDictionary"));*/
			_bindingItemDefinition = new WidgetBindingDefinition("bindingItem", getContentType(), this, BindingDefinitionType.GET_SET, true);
			if (_bindingItem!=null) {
				_bindingItem.setBindingDefinition(_bindingItemDefinition);
			}
		}
		return _bindingItemDefinition;
	}

	public BindingValue getBindingItem()
	{
		return _bindingItem;
	}

	public void setBindingItem(BindingValue bindingItem)
	{
		if (_bindingItem != null) {
			BindingPathElement element = _bindingItem.getBindingPathLastElement();
			if (element instanceof DMProperty) {
				((DMProperty)element).setIsStaticallyDefinedInTemplate(false);
			} else if (element instanceof DMMethod) {
				((DMMethod)element).setIsStaticallyDefinedInTemplate(false);
			} else {
				if (logger.isLoggable(Level.WARNING) && element!=null) {
					logger.warning("Unknown Binding path element: "+element.getClass().getName()+" Please fix me!");
				}
			}
		}
		_bindingItem = bindingItem;
		if (_bindingItem != null) {
			_bindingItem.setOwner(this);
			_bindingItem.setBindingDefinition(getBindingItemDefinition());
			BindingPathElement element = _bindingItem.getBindingPathLastElement();
			if (element instanceof DMProperty) {
				((DMProperty)element).setIsStaticallyDefinedInTemplate(true);
				((DMProperty)element).setIsBindable(false);
			} else if (element instanceof DMMethod) {
				((DMMethod)element).setIsStaticallyDefinedInTemplate(true);
			} else {
				if (logger.isLoggable(Level.WARNING) && element!=null) {
					logger.warning("Unknown Binding path element: "+element.getClass().getName()+" Please fix me!");
				}
			}
		}
		setChanged();
		notifyObservers(new IEDataModification("itemVariable", null,
				bindingItem));
	}

	public BindingValue getItemVariable()
	{
		return getBindingItem();
	}

	public DMProperty getItemProperty()
	{
		if(getItemVariable()!=null){
			BindingValue bv = getItemVariable();
			return (DMProperty)bv.getBindingPath().get(bv.getBindingPath().size()-1);
		}
		return null;
	}

	public void setItemVariable(BindingValue bindingItem)
	{
		setBindingItem(bindingItem);
	}

	public AbstractBinding getListAccessor()
	{
		return _listAccessor;
	}

	public void setListAccessor(AbstractBinding value) {
		_listAccessor = value;
		if (_listAccessor != null) {
			_listAccessor.setOwner(this);
			_listAccessor.setBindingDefinition(getBindingListDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("listAccessor", null, value));
	}

	public WidgetBindingDefinition getBindingListDefinition()
	{
		if (_bindingListDefinition == null) {
			_bindingListDefinition = new WidgetBindingDefinition("bindingList",DMType.makeListDMType(getContentType(), getProject()),this,BindingDefinitionType.GET,true);
			if (_listAccessor!=null) {
				_listAccessor.setBindingDefinition(_bindingListDefinition);
			}
		}
		return _bindingListDefinition;
	}

	public boolean getFetchObjects()
	{
		return _fetchObjects;
	}

	public void setFetchObjects(boolean objects) {
		_fetchObjects = objects;
		_bindingItemDefinition = null;
		setChanged();
		notifyObservers(new IEDataModification("fetchObjects", null, new Boolean(_fetchObjects)));
	}

	public boolean excelButton()
	{
		return _excelButton;
	}

	public boolean refreshButton()
	{
		return _refreshButton;
	}

	public void setExcelButton(boolean excelButton)
	{
		_excelButton = excelButton;
		setChanged();
		notifyObservers(new IEDataModification("excelButton", null, new Boolean(_excelButton)));
		notifyParentBloc(new ExcellButtonStateChange(excelButton));
	}

	public void setRefreshButton(boolean refreshButton)
	{
		_refreshButton = refreshButton;
		setChanged();
		notifyObservers(new IEDataModification("refreshButton", null, new Boolean(_refreshButton)));
		notifyParentBloc(new RefreshButtonStateChange(refreshButton));
	}

	public boolean getHasBatch()
	{
		return _hasBatch;
	}

	public void setHasBatch(boolean objects) {
		_hasBatch = objects;
		setChanged();
		notifyObservers(new IEDataModification("hasBatch", null, new Boolean(_hasBatch)));
	}

	public int getDefaultBatchValue()
	{
		return _defaultBatchValue;
	}

	public void setDefaultBatchValue(int object) {
		_defaultBatchValue = object;
		setChanged();
		notifyObservers(new IEDataModification("defaultBatchValue", null, new Integer(_defaultBatchValue)));
	}

	private void notifyParentBloc(IEDataModification modif)
	{
		IEBlocWidget bloc = findBlocInParent();
		if (bloc != null) {
			bloc.notifyListActionButtonStateChange(modif);
		}
	}

	public DMEntity getEntity()
	{
		if (getItemVariable() != null) {
			return getItemVariable().getBindingPath().lastElement().getType().getBaseEntity();
		}
		if (entity == null && entityName != null) {
			entity = getProject().getDataModel().getEntityNamed(entityName);
			if (entity == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Entity named: " + entityName
							+ " could not be found");
				}
			}
		}
		return entity;
	}

	@Deprecated
	public void setEntity(DMEntity ent)
	{
		DMEntity old = this.entity;
		this.entity = ent;
		if (old != null && old != ent) {
			removeBindingVariable("fetchObjectOfList" + getFlexoID());
		}
		if (ent != null) {
			if (old != ent) {
				entityName = ent.getFullyQualifiedName();
			}
			createsBindingVariable("fetchObjectOfList" + getFlexoID(), DMType.makeResolvedDMType(ent), DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD, false);
		} else {
			entityName = null;
		}
		if (old != ent) {
			setChanged();
			notifyObservers(new IEDataModification("entity", old, ent));
		}
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String ent_name) {
		this.entityName = ent_name;
		this.entity = null;
	}

	@Override
	public ListType getListType() {
		return _listType;
	}

	@Override
	public void setListType(ListType type) {
		_listType = type;
		_bindingItemDefinition = null;
		_bindingListDefinition = null;
		setChanged();
		notifyObservers(new IEDataModification("listType", null, _listType));
	}

	public boolean useListAccessor() {
		return _listType == ListType.ACCESSOR;
	}

	public DMMethod getPermanentFilter()
	{
		if (permanentFilter == null && permanentFilterMethodSignature != null) {
			permanentFilter = getWOComponent().getComponentDMEntity().getMethod(permanentFilterMethodSignature);
			if (permanentFilter == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find method with signature " + permanentFilterMethodSignature
							+ " in component entity named: " + getWOComponent().getName());
				}
				permanentFilterMethodSignature = null;
			}
		}
		return permanentFilter;
	}

	public void setPermanentFilter(DMMethod f) {
		DMMethod old = this.permanentFilter;
		this.permanentFilter = f;
		if (f == null) {
			permanentFilterMethodSignature = null;
		}
		setChanged();
		notifyObservers(new IEDataModification("permanentFilter", old, f));
	}

	public Vector<DMMethod> getAvailableQualifiers()
	{
		DMEntity qual = getProject().getDataModel().getEntityNamed("com.webobjects.eocontrol.EOQualifier");
		if (qual == null) {
			return new Vector<DMMethod>();
		}
		Vector<DMMethod> ret = new Vector<DMMethod>();
		Enumeration en = getComponentDMEntity().getMethods().keys();
		while (en.hasMoreElements()) {
			DMMethod method = getComponentDMEntity().getMethods().get(en.nextElement());
			if (method.getReturnType()!=null && method.getReturnType().getBaseEntity()!=null && method.getReturnType().getBaseEntity().equals(qual) && method.getParameters().size()==0) {
				ret.add(method);
			}
		}
		return ret;
	}

	public String getPermanentFilterMethodSignature() {
		if (getPermanentFilter() != null) {
			return this.permanentFilter.getSignature();
		} else {
			return null;
		}
	}

	public void setPermanentFilterMethodSignature(String signature) {
		permanentFilterMethodSignature = signature;
		permanentFilter = null;
	}

	/**
	 * Returns all the widgets that are meant to be used for filtering this
	 * list. This method should be used with great care since it parses the all
	 * component to retrieve all those widgets
	 * 
	 * @return all the widgets that are meant to be used for filtering this list
	 */
	public Vector<IEControlWidget> getFilterWidgets()
	{
		Vector<IEControlWidget> result = getWOComponent().getFilterWidgetsForRepetition(this);
		Collections.sort(result, new Comparator<IEControlWidget>()
				{

			@Override
			public int compare(IEControlWidget o1, IEControlWidget o2)
			{
				return o1.compareTo(o2);
			}
				});
		return result;
	}

	/**
	 * Try to infer the business data listed:<br>
	 * 1. If the list is bound, return the Flexo process if it is used as a business data in a process, otherwise return null 2. Retrieve
	 * all operations linked to the component, if all operations are inside the a sub process activity targeting the same process, return
	 * this process.<br>
	 * 3. Retrieve all action in this list, if all actions retrieved targets the same process, return this one.<br>
	 * 3. Otherwise return null
	 * 
	 * @return the inferred flexo process
	 */
	public FlexoProcess getInferredListProcess()
	{
		if (getContentType() == null) {
			return null;
		}

		if (!"java.lang.Object".equals(getContentType().getStringRepresentation()))
		{
			DMEntity entity = getContentType().getBaseEntity();
			if (entity != null && getProject().getAllLocalFlexoProcesses() != null) {
				Enumeration<FlexoProcess> en = getProject().getAllLocalFlexoProcesses().elements();
				while (en.hasMoreElements()) {
					FlexoProcess process = en.nextElement();
					if(entity.equals(process.getBusinessDataType())) {
						return process;
					}
				}
			}

			return null;
		}

		Set<FlexoProcess> foundProcessFromActions = new HashSet<FlexoProcess>();
		Set<FlexoProcess> foundProcessFromSubProcessActivity = new HashSet<FlexoProcess>();
		for(OperationNode operationNode : getWOComponent().getComponentDefinition().getAllOperationNodesLinkedToThisComponent())
		{
			if(foundProcessFromActions.size() <= 1)
			{
				for(ActionNode actionNode : operationNode.getAllActionNodes())
				{
					IEHyperlinkWidget widget = actionNode.getAssociatedButtonWidget();
					if(widget != null && widget.isInRepetition() && widget.repetition().getFlexoID() == getFlexoID())
					{
						for(WorkflowPathToOperationNode workflowPath : actionNode.getNextOperationsForAction())
						{
							foundProcessFromActions.addAll(workflowPath.getCreatedProcesses());
							foundProcessFromActions.addAll(workflowPath.getDeletedProcesses());
							if(workflowPath.getOperationNode() != null) {
								foundProcessFromActions.add(workflowPath.getOperationNode().getProcess());
							}

							if(foundProcessFromActions.size() > 1) {
								break;
							}
						}

						if(foundProcessFromActions.size() > 1) {
							break;
						}
					}
				}
			}

			if(operationNode.getAbstractActivityNode().isSubProcessNode()) {
				foundProcessFromSubProcessActivity.add(((SubProcessNode)operationNode.getAbstractActivityNode()).getSubProcess());
			}
		}

		if(foundProcessFromSubProcessActivity.size() == 1) {
			return foundProcessFromSubProcessActivity.iterator().next();
		}

		if(foundProcessFromActions.size() == 1) {
			return foundProcessFromActions.iterator().next();
		}


		return null;
	}

	/*    public class RepetitionBindingModel extends BindingModel
    {
    	public static final String COMPONENT_BINDING_VARIABLE_NAME = "component";
    	public static final String ITEM_BINDING_VARIABLE_NAME = "itemOfRepetition";

        private BindingVariable _componentVariable;
        private BindingVariable _itemVariable;

        public RepetitionBindingModel()
        {
            super();
            _componentVariable = new BindingVariable(RepetitionOperator.this, getProject().getDataModel(), FlexoLocalization
                    .localizedForKey("access_to_the_current_component"));
            _componentVariable.setVariableName(COMPONENT_BINDING_VARIABLE_NAME);
            _componentVariable.setType(DMType.makeResolvedDMType(getComponentDMEntity()));
            createOrUpdateItemVariable();
        }

        protected void createOrUpdateItemVariable() {
        	if (getBindingItem()!=null && getBindingItem().getBindingPathLastElement() instanceof DMProperty){
	            _itemVariable = new BindingVariable(RepetitionOperator.this, getProject().getDataModel(), FlexoLocalization
	            		.localizedForKey("access_to_the_item_of_the_repetition"));
	            _itemVariable.setVariableName(((DMProperty)getBindingItem().getBindingPathLastElement()).getName());
	            _itemVariable.setType(getContentType().clone());
        	} else {
        		_itemVariable = null;
        	}
        }

        @Override
        public int getBindingVariablesCount()
        {
            return _itemVariable!=null?2:1;
        }

        @Override
        public BindingVariable getBindingVariableAt(int index)
        {
        	if (index==0)
        		return getItemBindingVariable();
        	else
        		return getComponentBindingVariable();
        }

        public BindingVariable getItemBindingVariable() {
			return _itemVariable;
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
	 */
	public static class ListMustHaveAName extends ValidationRule<ListMustHaveAName,RepetitionOperator> {

		public ListMustHaveAName() {
			super(RepetitionOperator.class, "list_must_have_a_name");
		}

		@Override
		public ValidationIssue<ListMustHaveAName, RepetitionOperator> applyValidation(RepetitionOperator object) {
			ValidationError<ListMustHaveAName, RepetitionOperator> error = null;
			if (object.getName()==null || object.getName().trim().length()==0) {
				error = new ValidationError<ListMustHaveAName, RepetitionOperator>(this,object,"list_must_have_a_name",new EnterNewListName());
			}
			return error;
		}

		public static class EnterNewListName extends ParameteredFixProposal<ListMustHaveAName, RepetitionOperator> {

			public EnterNewListName() {
				super("enter_new_list_name", parameters());
			}

			private static ParameterDefinition[] parameters() {
				return new ParameterDefinition[]{
						new TextFieldParameter("name","list_name",null)
				};
			}

			@Override
			protected void fixAction() {
				String s = (String) getValueForParameter("name");
				if (s!=null && s.trim().length()!=0) {
					getObject().setName(s);
				}
			}
		}

	}

	public static class RawRowRepetitionCanNotContainEditableField extends ValidationRule<RawRowRepetitionCanNotContainEditableField,RepetitionOperator>
	{

		/**
		 * @author gpolet
		 * 
		 */
		public class SetListTypeToFetch extends FixProposal<RawRowRepetitionCanNotContainEditableField,RepetitionOperator>
		{

			/**
			 * @param aMessage
			 */
			public SetListTypeToFetch()
			{
				super("set_list_type_to_fetch_objects");
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction()
			{
				getObject().setFetchObjects(true);
			}

		}

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public RawRowRepetitionCanNotContainEditableField()
		{
			super(RepetitionOperator.class, "raw_row_repetition_cannot_contain_editable_field");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<RawRowRepetitionCanNotContainEditableField,RepetitionOperator> applyValidation(RepetitionOperator rep)
		{
			if (rep.getFetchObjects()) {
				return null;
			}
			Vector v = rep.getOperatedSequence().getAllEmbeddedIEObjects();
			Enumeration<IEObject> en = v.elements();
			while (en.hasMoreElements()) {
				IEObject element = en.nextElement();
				if (element instanceof IEEditableFieldWidget) {
					ValidationError<RawRowRepetitionCanNotContainEditableField,RepetitionOperator> err = new ValidationError<RawRowRepetitionCanNotContainEditableField,RepetitionOperator>(this, rep, "raw_row_repetition_cannot_contain_editable_field");
					err.addToFixProposals(new SetListTypeToFetch());
					return err;
				}
			}
			return null;
		}

	}

}