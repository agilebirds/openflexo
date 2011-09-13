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
package org.openflexo.foundation.ie.wizards;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.dm.action.CreateDMEOAttribute;
import org.openflexo.foundation.dm.action.CreateDMEOEntity;
import org.openflexo.foundation.dm.action.CreateDMEOModel;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition.ComponentDefinitionBindingModel;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectRestructuration;


public class EntityFromWidgets {

	private Map<IEWidget, PropertyProposal> relevantWidgets;
	private FlexoProject _project;
	
	private DMEOEntity _dmeoEntity;
	private DMEOModel _dmeoModel;
	private ProjectDatabaseRepository _projectDatabaseRepository;
	
	//the factory will create PropertyProposal for any relevant widget.
	private PropertyProposalFactory _factory;
	private boolean _useDMEOEntity;
	private IEWOComponent _component;
	
	public EntityFromWidgets(FlexoProject project,List<FlexoModelObject> widgets, boolean useDMEOEntity){
		super();
		_project = project;
		_useDMEOEntity = useDMEOEntity;
		_factory = PropertyProposalFactory.getFactory(this);
		relevantWidgets = extractRelevantWidgets(widgets, useDMEOEntity);
	}

	public void justDoIt(String projectDataBaseRepositoryName,String eomodelName,String eoentityName,List<PropertyProposal> selectedProps,FlexoEditor editor) throws InvalidNameException{
		if(_useDMEOEntity){
			createProjectPersistantRepository(projectDataBaseRepositoryName, editor);
			createEOModel(eomodelName, editor);
			createEOEntity(eoentityName, editor);
			createAttributes(selectedProps, editor);
			makeBindings(eoentityName,_component,selectedProps);
		}
	}
	
	private void makeBindings(String componentPropertyName, IEWOComponent component, List<PropertyProposal> selectedProposals){
		//first we have to create a component binding for the created entity
		DMProperty entityBindingPathElement = component.getComponentDMEntity().createDMProperty(componentPropertyName, DMType.makeResolvedDMType(_dmeoEntity), DMPropertyImplementationType.PUBLIC_FIELD); // TODO
		entityBindingPathElement.setIsBindable(true);
		component.getComponentDMEntity().setMandatory(entityBindingPathElement, true);
		//now we have to build a binding value for each widget
		BindingVariable bv = ((ComponentDefinitionBindingModel)component.getBindingModel()).getBindingVariableAt(0);
		PropertyProposal itemProposal = null;
		IEWidget owner = null;
		BindingDefinition bindingDef = null;
		for(int i=0;i<selectedProposals.size();i++){
			itemProposal = selectedProposals.get(i);
			owner = itemProposal.getWidget();
			//a widget have a lot of bindings : we have to get the binding related to this wizard.
			bindingDef = _factory.retreiveRelevantBindingDefinition(owner);
			BindingValue bindingValue = new BindingValue(bindingDef,owner);
			bindingValue.setBindingVariable(bv);
			bindingValue.setBindingPathElementAtIndex(entityBindingPathElement, 0);
			bindingValue.setBindingPathElementAtIndex(itemProposal.getImplementedProperty(), 0);
		}
	}

	private void createAttributes(List<PropertyProposal> selectedProps, FlexoEditor editor) {
		Iterator<PropertyProposal> it = selectedProps.iterator();
		PropertyProposal proposal;
		while(it.hasNext()){
			proposal = it.next();
			CreateDMEOAttribute createAttributeAction = CreateDMEOAttribute.actionType.makeNewAction(_dmeoEntity, null,editor);
			createAttributeAction.doAction();
			try {
				createAttributeAction.getNewEOAttribute().setName(proposal.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvalidNameException e) {
				e.printStackTrace();
			}
			createAttributeAction.getNewEOAttribute().setPrototype(((EOAttributeProposal)proposal).getPrototype());
			createAttributeAction.getNewEOAttribute().setColumnName(((EOAttributeProposal)proposal).getColumnName());
			proposal.setImplementedProperty(createAttributeAction.getNewEOAttribute());
		}
		
	}

	private void createProjectPersistantRepository(String projectDataBaseRepositoryName, FlexoEditor editor){
		_projectDatabaseRepository = _project.getDataModel().getProjectDatabaseRepositoryName(projectDataBaseRepositoryName);
		if(_projectDatabaseRepository==null){
			CreateProjectDatabaseRepository createProjectDBRepositoryAction = CreateProjectDatabaseRepository.actionType.makeNewAction(_projectDatabaseRepository, null,editor);
			createProjectDBRepositoryAction.setNewRepositoryName(projectDataBaseRepositoryName);
			createProjectDBRepositoryAction.doAction();
		}
	}
	private void createEOModel(String eomodelName, FlexoEditor editor){
		_dmeoModel = _project.getDataModel().getDMEOModelNamed(eomodelName);
		if(_dmeoModel == null){
			//we need to create a new EOModel
			File eoModelFile = new File(ProjectRestructuration.getExpectedDataModelDirectory(_project.getProjectDirectory()),eomodelName);
			CreateDMEOModel createEOModelAction = CreateDMEOModel.actionType.makeNewAction(_projectDatabaseRepository, null,editor);
			createEOModelAction.setEOModelFile(eoModelFile);
			createEOModelAction.setFocusedObject(_projectDatabaseRepository);
			createEOModelAction.doAction();
		}
	}
	
	private void createEOEntity(String eoentityName, FlexoEditor editor) throws InvalidNameException{
		_dmeoEntity = _dmeoModel.getDMEOEntityNamed(eoentityName);
		if(_dmeoEntity == null){
			//we need to create a new EOModel
			CreateDMEOEntity createEOEntity = CreateDMEOEntity.actionType.makeNewAction(_dmeoModel, null,editor);
			createEOEntity.setFocusedObject(_dmeoModel);
			createEOEntity.doAction();
			createEOEntity.getNewEntity().setName(eoentityName);
		}
	}
	
	
	private Map<IEWidget, PropertyProposal> extractRelevantWidgets(List<FlexoModelObject> widgets, boolean useDMEOEntity) {
		Map<IEWidget, PropertyProposal> reply = new Hashtable<IEWidget, PropertyProposal>();
		Iterator<FlexoModelObject> it = widgets.iterator();
		FlexoModelObject modelObject = null;
		while (it.hasNext()) {
			modelObject = it.next();
			if(modelObject instanceof IEWidget){
				IEWidget widget = (IEWidget)modelObject;
				if (_factory.isRelevant(widget)) {
					if (_component == null)
						_component = widget.getWOComponent();
					else {
						if (_component != widget.getWOComponent())
							throw new IllegalArgumentException("All selected widgets must be part of the same component.");
					}
					if (useDMEOEntity)
						reply.put(widget, _factory.getEOAttributeProposal(widget));
					else
						reply.put(widget, _factory.getDMAttributeProposal(widget));
				}
			}
		}
		return reply;
	}
	
	public boolean isPropertyNameUsed(String propertyName){
		Iterator<PropertyProposal> it = relevantWidgets.values().iterator();
		while(it.hasNext()){
			if(it.next().getName().equals(propertyName))return true;
		}
		return false;
	}
}
