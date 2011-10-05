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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.xmlcode.AccessorInvocationException;


//TODO: Use FIB instead as soon as possible
@Deprecated 
public class EditionPatternPropertyListModel extends PropertyListModel {

    private static final Logger logger = Logger.getLogger(PropertyModel.class.getPackage().getName());

	private String patternRole;

    @Override
	public InspectableObject getDerivedModel(InspectableObject inspectable) 
    {
    	if (inspectable instanceof FlexoModelObject) {
    		return (InspectableObject)objectForRole((FlexoModelObject)inspectable, patternRole);
    	}
    	return inspectable;
    }
    
    @Override
	public synchronized boolean hasObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException
    {
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
       			OntologyProperty property = patternActor.getProject().getProjectOntologyLibrary().getProperty(name);
    			if (property != null) {
    				if (patternActor instanceof OntologyIndividual) {
						return true;
    				}
    			}
    			return false;
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return false;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return false;
    	}
    }


    @Override
	public synchronized Object getObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException
    {
    	//logger.info("Requested  getObjectValue() key="+getLastAccessor()+" for "+inspectable+" patternRole="+patternRole);
    	
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
    			OntologyProperty property = patternActor.getProject().getProjectOntologyLibrary().getProperty(name);
    			if (property != null) {
    				if (patternActor instanceof OntologyIndividual) {
						return ((OntologyIndividual)patternActor).getPropertyValue(property);
    				}
    			}
     			return super.getObjectValue(patternActor);
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return null;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return null;
    	}
     }

    /**
     * This method can be called to store the newValue in the model.
     * 
     * @param newValue
     */
    @Override
	public synchronized void setObjectValue(KeyValueCoding inspectable, Object newValue) throws AccessorInvocationException
    {
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
       			OntologyProperty property = patternActor.getProject().getProjectOntologyLibrary().getProperty(name);
    			if (property != null) {
    				if (patternActor instanceof OntologyIndividual) {
    					((OntologyIndividual)patternActor).setPropertyValue(property,newValue);
    					return;
    				}
    			}
      			super.setObjectValue(patternActor,newValue);
    	   		return;
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return;
    	}
    }

   /* public synchronized boolean hasObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException
    {
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
    			return super.hasObjectValue(patternActor);
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return false;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return false;
    	}
    }


    public synchronized Object getObjectValue(KeyValueCoding inspectable) throws AccessorInvocationException
    {
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
    			OntologyObject ontologyObject = patternActor.getProject().getOntologyLibrary().getOntologyObject(name);
    			if (ontologyObject != null) {
    				if (patternActor instanceof OntologyIndividual) {
    					if (ontologyObject instanceof OntologyDataProperty) {
    						return ((OntologyIndividual)patternActor).getDataPropertyValue((OntologyDataProperty)ontologyObject);
    					}
    				}
    			}
    			return super.getObjectValue(patternActor);
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return null;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return null;
    	}
     }

    public synchronized void setObjectValue(KeyValueCoding inspectable, Object newValue) throws AccessorInvocationException
    {
    	if (inspectable instanceof FlexoModelObject) {
    		FlexoModelObject patternActor = objectForRole((FlexoModelObject)inspectable, patternRole);
    		if (patternActor != null) {
    			super.setObjectValue(patternActor,newValue);
    	   		return;
    		}
       		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Pattern actor is null for patternRole="+patternRole+" for FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return;
    	}
    	else {
    		if (logger.isLoggable(Level.WARNING))
    			logger.warning("Inspectable object is null or not a FlexoModelObject for key " + name + ". We should definitely investigate this.");
    		return;
    	}
    }
*/
    
    @Override
    public EditionPatternInspector getInspectorModel() 
    {
     	return (EditionPatternInspector)super.getInspectorModel();
    }
    
    protected FlexoModelObject objectForRole(FlexoModelObject inspectable, String patternRole)
    {
    	if (patternRole == null) return null;
    	
    	EditionPattern ep = getInspectorModel().getEditionPattern();
    	if (ep == null) return null;
    	
    	EditionPatternReference ref = inspectable.getEditionPatternReference(ep);
    	if (ref == null) return null;
    	
    	EditionPatternInstance epi = ref.getEditionPatternInstance();
    	if (epi == null) return null;

    	return epi.getPatternActor(patternRole);
    }

    @Override
	public String getKey()
    {
    	return patternRole+"#"+name;
    }

    @Override
    public boolean allowDelegateHandling() 
    {
    	return false;
    }

    @Override
	public boolean isEditionPatternPropertyList()
    {
    	return true;
    }

    private boolean editOntologyProperty = false;
    
    public boolean getIsOntologyProperty()
    {
    	return getOntologyProperty() != null || editOntologyProperty;
    }
    
    public void setIsOntologyProperty(boolean flag)
    {
    	if (flag) {
    		editOntologyProperty = true;
    	}
    	else {
       		editOntologyProperty = false;
       		setData(null);
    	}
    }
    
	public OntologyProperty getOntologyProperty()
	{
		if (getInspector() != null)
			return getInspector().getEditionPattern().getOntologyLibrary().getProperty(getData());
		return null;
	}
	
	public void setOntologyProperty(OntologyProperty p)
	{
		setData(p != null ? p.getURI() : null);
	}
	
	public String _getPatternRole()
	{
		return patternRole;
	}


	public void _setPatternRole(String patternRole)
	{
		if (getTabModel() != null) getTabModel().removePropertyWithKey(getKey());
		this.patternRole = patternRole;
		if (getTabModel() != null) getTabModel().setPropertyForKey(this, getKey());
	}

	public PatternRole getPatternRoleReference()
	{
		if (getInspector() != null) return getInspector().getEditionPattern().getPatternRole(patternRole);
		return null;
	}


	public void setPatternRoleReference(PatternRole aPatternRole)
	{
		_setPatternRole(aPatternRole != null ? aPatternRole.getPatternRoleName() : null);
	}

	private EditionPatternInspector inspector;
	
	public EditionPatternInspector getInspector() 
	{
		return inspector;
	}

	public void setInspector(EditionPatternInspector inspector)
	{
		this.inspector = inspector;
	}
	
	@Override
	public String getWidget()
	{
		return "TABLE";
	}

	public String getData()
	{
		return name;
	}


	public void setData(String data)
	{
		if (getTabModel() != null) getTabModel().removePropertyWithKey(getKey());
		this.name = data;
		if (getTabModel() != null) getTabModel().setPropertyForKey(this, getKey());
	}
	
	public PropertyListColumn createColumn()
	{
		PropertyListColumn newColumn = new PropertyListColumn();
		newColumn.label = "new_column";
		addToColumns(newColumn);
		return newColumn;
	}

	public PropertyListColumn deleteColumn(PropertyListColumn column)
	{
		removeFromColumns(column);
		return column;
	}

	public PropertyListAction createAction()
	{
		PropertyListAction newAction = new PropertyListAction();
		newAction.name = "new_action";
		addToActions(newAction);
		return newAction;
	}

	public PropertyListAction deleteAction(PropertyListAction action)
	{
		removeFromActions(action);
		return action;
	}
}
