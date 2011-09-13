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
package org.openflexo.foundation.dm;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.dm.DMProcessBusinessDataAccessingMethod.CodeType;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoDMBuilder;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class ComponentDMEntity extends DMEntity
{

	private static final Logger logger = Logger.getLogger(ComponentDMEntity.class.getPackage().getName());

    private ComponentDefinition _componentDefinition;

    private Vector<ComponentBindingDefinition> _bindingDefinitions;

    private Vector<DMProperty> _mandatoryBindingProperties;

    private Vector<DMProperty> _bindableProperties;

    private Vector<DMProperty> _settableBindingProperties;

    /**
     * Constructor used during deserialization
     */
    public ComponentDMEntity(FlexoDMBuilder builder)
    {
        this(builder.dmModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public ComponentDMEntity(DMModel dmModel)
    {
        super(dmModel);
        _bindingDefinitions = new Vector<ComponentBindingDefinition>();
        _mandatoryBindingProperties = new Vector<DMProperty>();
        _bindableProperties = new Vector<DMProperty>();
        _settableBindingProperties = new Vector<DMProperty>();
        if(!isDeserializing() && getParentType()==null)setParentType(DMType.makeResolvedDMType(dmModel.getWORepository().getCustomComponentEntity()), true);
    }

    /**
     * Constructor used for dynamic creation
     */
    public ComponentDMEntity(DMModel dmModel, ComponentDefinition componentDefinition)
    {
        super(dmModel,
        		componentDefinition.getName(),
        		dmModel.getComponentRepository().getDefaultComponentPackage().getName(),
        		componentDefinition.getName(),
                DMType.makeResolvedDMType(dmModel.getWORepository().getCustomComponentEntity()));
        _bindingDefinitions = new Vector<ComponentBindingDefinition>();
        _mandatoryBindingProperties = new Vector<DMProperty>();
        _settableBindingProperties = new Vector<DMProperty>();
        _bindableProperties = new Vector<DMProperty>();
        _componentDefinition = componentDefinition;
     }

    @Override
	public boolean isDeletable()
    {
        return true;
    }


    public ComponentDefinition getComponentDefinition()
    {
        return _componentDefinition;
    }

    public void setComponentDefinition(ComponentDefinition componentDefinition)
    {
        _componentDefinition = componentDefinition;
    }

    /**
     * Stores in an hashtable ComponentBindingDefinition related to a DMProperty
     * Ensure that no new ComponentBindingDefinition is created once a property is renamed
     */
    private transient Hashtable<DMProperty,ComponentBindingDefinition> _knownBindingDefinitionForProperties = new Hashtable<DMProperty, ComponentBindingDefinition>();

    private ComponentBindingDefinition getComponentBindingDefinitionForProperty (DMProperty property)
    {
        ComponentBindingDefinition returned = _knownBindingDefinitionForProperties.get(property);
        if (returned == null) {
            ComponentBindingDefinition newBindingDefinition = new ComponentBindingDefinition(this, property);
            _knownBindingDefinitionForProperties.put(property,newBindingDefinition);
            return newBindingDefinition;
        }
        else {
            logger.fine("Retrieve known ComponentBindingDefinition !");
            return returned;
        }
    }

    public void setPropertyForKey(DMProperty property, String propertyName, boolean isBindable)
    {
        super.setPropertyForKey(property, propertyName);
        if(isBindable){
        	addToBindableProperties(property);
        }
    }

    private void registerBindableProperty(DMProperty property){
    	ComponentBindingDefinition newBindingDefinition = getComponentBindingDefinitionForProperty(property);
    	if(!_bindingDefinitions.contains(newBindingDefinition))
    		_bindingDefinitions.add(newBindingDefinition);
    	propertiesNeedsReordering = true;
    	reorderProperties();
    	if (!isDeserializing() && getComponentDefinition() != null) {
    		getComponentDefinition().notifyComponentDefinitionsHasChanged();
    	}
    }
    private void unregisterBindableProperty(DMProperty property){
    	ComponentBindingDefinition newBindingDefinition = _knownBindingDefinitionForProperties.get(property);
    	if(newBindingDefinition!=null){
    		_bindingDefinitions.remove(newBindingDefinition);
    		propertiesNeedsReordering = true;
    		reorderProperties();
    		if (!isDeserializing() && getComponentDefinition() != null) {
    			getComponentDefinition().notifyComponentDefinitionsHasChanged();
    		}
    	}
    }
    @Override
	public void removePropertyWithKey(String propertyName)
	{
		removePropertyWithKey(propertyName, true);
	}

	@Override
	public void removePropertyWithKey(String propertyName, boolean notify)
    {
        DMProperty property = getDeclaredProperty(propertyName);
        BindingDefinition bd = bindingDefinitionForProperty(property);
        super.removePropertyWithKey(propertyName, notify);
        _bindingDefinitions.remove(bd);
        _mandatoryBindingProperties.remove(property);
        _bindableProperties.remove(property);
        _settableBindingProperties.remove(property);
        propertiesNeedsReordering = true;
        reorderProperties();
        /* Not needed
        if (getComponentDefinition() != null) {
            getComponentDefinition().notifyBindingDefinitionRemoved(bd);
        }*/
    }

    public ComponentBindingDefinition bindingDefinitionForProperty(DMProperty property)
    {
        for (Enumeration en = _bindingDefinitions.elements(); en.hasMoreElements();) {
            ComponentBindingDefinition next = (ComponentBindingDefinition) en.nextElement();
            if (next.getProperty() == property) {
                return next;
            }
        }
        return null;
    }

    public ComponentBindingDefinition bindingDefinitionNamed(String aName)
    {
        for (Enumeration en = _bindingDefinitions.elements(); en.hasMoreElements();) {
            ComponentBindingDefinition next = (ComponentBindingDefinition) en.nextElement();
            if (next.getVariableName().equals(aName)) {
                return next;
            }
        }
        return null;
    }

    public Vector<ComponentBindingDefinition> getBindingDefinitions()
    {
        if (propertiesNeedsReordering) {
            reorderProperties();
        }
       return _bindingDefinitions;
    }

    public boolean isMandatory(DMProperty property)
    {
        return _mandatoryBindingProperties.contains(property);
    }

    public void setMandatory(DMProperty property, boolean mandatory)
    {
        if (mandatory) {
            addToMandatoryBindingProperties(property);
        } else {
            removeFromMandatoryBindingProperties(property);
        }
        setChanged();
    }

    public boolean isSettable(DMProperty property)
    {
        return _settableBindingProperties.contains(property);
    }

    public void setSettable(DMProperty property, boolean settable)
    {
        if (settable) {
            addToSettableBindingProperties(property);
        } else {
            removeFromSettableBindingProperties(property);
        }
        setChanged();
    }

    public Vector<DMProperty> getMandatoryBindingProperties()
    {
        return _mandatoryBindingProperties;
    }

    public void setMandatoryBindingProperties(Vector<DMProperty> mandatoryBindingProperties)
    {
        _mandatoryBindingProperties = mandatoryBindingProperties;
        Enumeration<DMProperty> en = mandatoryBindingProperties.elements();
        while(en.hasMoreElements()){
        	addToBindableProperties(en.nextElement());
        }
    }

    public void addToMandatoryBindingProperties(DMProperty property)
    {
        if (!_mandatoryBindingProperties.contains(property)) {
            _mandatoryBindingProperties.add(property);
            addToBindableProperties(property);
        }

    }

    public void removeFromMandatoryBindingProperties(DMProperty property)
    {
        if (_mandatoryBindingProperties.contains(property)) {
            _mandatoryBindingProperties.remove(property);
        }
    }

    @Override
	protected void reorderProperties()
    {
        if (propertiesNeedsReordering) {
            Collections.sort(_bindingDefinitions, BindingDefinition.bindingDefinitionComparator);
        }
        super.reorderProperties();
    }

    public boolean isBindable(DMProperty property)
    {
        return _bindableProperties.contains(property);
    }

    public void setBindable(DMProperty property, boolean bindable)
    {
        if (bindable) {
            addToBindableProperties(property);
        } else {
            removeFromBindableProperties(property);
        }
        setChanged();
    }

    public Vector<DMProperty> getBindableProperties()
    {
        return _bindableProperties;
    }

    public void setBindableProperties(Vector<DMProperty> bindableProperties)
    {
    	_bindableProperties = bindableProperties;
    }

    public void addToBindableProperties(DMProperty property)
    {
        if (!_bindableProperties.contains(property)) {
        	_bindableProperties.add(property);
        	registerBindableProperty(property);
        }
    }

    public void removeFromBindableProperties(DMProperty property)
    {
        if (_bindableProperties.contains(property)) {
        	_bindableProperties.remove(property);
        	unregisterBindableProperty(property);
        }
        removeFromMandatoryBindingProperties(property);
        removeFromSettableBindingProperties(property);
    }


    public Vector<DMProperty> getSettableBindingProperties()
    {
        return _settableBindingProperties;
    }

    public void setSettableBindingProperties(Vector<DMProperty> settableBindingProperties)
    {
        _settableBindingProperties = settableBindingProperties;
        Enumeration<DMProperty> en = settableBindingProperties.elements();
        while(en.hasMoreElements()){
        	addToBindableProperties(en.nextElement());
        }
    }

    public void addToSettableBindingProperties(DMProperty property)
    {
        if (!_settableBindingProperties.contains(property)) {
            _settableBindingProperties.add(property);
        }
    }

    public void removeFromSettableBindingProperties(DMProperty property)
    {
        if (_settableBindingProperties.contains(property)) {
        	_settableBindingProperties.remove(property);
        }
    }
    
    public void addOrUpdateAccessingBusinessDataMethod(FlexoProcess process)
    {
    	DMProcessBusinessDataAccessingMethod method = DMProcessBusinessDataAccessingMethod.getProcessBusinessDataAccessingMethod(this, process, CodeType.CUSTOMCOMPONENT_CURRENTBUSINESSDATA);
    	
    	if(method != null)
    		method.updateProcess(process);
    	else if(process.getBusinessDataType() != null) {
    		String methodName = DMProcessBusinessDataAccessingMethod.getAccessingBusinessDataMethodName(process);
    		for (String s : getMethods().keySet()) {
    			logger.info("Found method: "+s);
    		}
    		if (getMethod(methodName+"()") != null) {
    			logger.info("Already containing method "+getMethod(methodName+"()").getSignature());
    		}
    		else {
    			logger.info("Not found method: "+methodName+"() create it");
    	   		new DMProcessBusinessDataAccessingMethod(this, process, CodeType.CUSTOMCOMPONENT_CURRENTBUSINESSDATA);
    		}
    	}
    }
    
    public void removeAccessingBusinessDataMethod(FlexoProcess process)
    {
    	DMProcessBusinessDataAccessingMethod method = DMProcessBusinessDataAccessingMethod.getProcessBusinessDataAccessingMethod(this, process, CodeType.CUSTOMCOMPONENT_CURRENTBUSINESSDATA);
    	if(method != null)
    		method.delete();
    }
}
