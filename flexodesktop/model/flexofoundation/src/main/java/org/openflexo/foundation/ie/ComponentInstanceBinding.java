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

import java.text.Collator;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ComponentInstanceBinding extends FlexoModelObject implements InspectableObject, Bindable
{

    static final Logger logger = Logger.getLogger(ComponentInstanceBinding.class.getPackage().getName());

    private ComponentInstance _componentInstance;

    private ComponentBindingDefinition _bindingDefinition;

    private String _bindingDefinitionName;

    private AbstractBinding _bindingValue;

    /**
     * Constructor used by XMLCoDe in the context of process deserialization
     * 
     * @param builder
     */
    public ComponentInstanceBinding(FlexoProcessBuilder builder)
    {
        super(builder.getProject());
        initializeDeserialization(builder);
    }

    /**
     * Constructor used by XMLCoDe in the context of navigation menu
     * deserialization
     * 
     * @param builder
     */
    public ComponentInstanceBinding(FlexoNavigationMenuBuilder builder)
    {
        super(builder.getProject());
        initializeDeserialization(builder);
    }

    /**
     * Constructor used by XMLCoDe in the context of component deserialization
     * 
     * @param builder
     */
    public ComponentInstanceBinding(FlexoComponentBuilder builder)
    {
        super(builder.getProject());
        initializeDeserialization(builder);
    }

    public ComponentInstanceBinding(ComponentInstance ci, ComponentBindingDefinition bd)
    {
        super(ci.getProject());
        setComponentInstance(ci);
        setBindingDefinition(bd);
    }

    public ComponentInstanceBinding(ComponentInstance ci, ComponentBindingDefinition bd, AbstractBinding value)
    {
        this(ci, bd);
        setBindingValue(value);
    }

    @Override
	public BindingModel getBindingModel()
    {
        if (_componentInstance != null)
            return _componentInstance.getBindingModel();
        return null;
    }

    public ComponentBindingDefinition getBindingDefinition()
    {
        if ((_bindingDefinition == null) && (_componentInstance != null) && (_bindingDefinitionName != null)) {
            _bindingDefinition = _componentInstance.getComponentDefinition().bindingDefinitionNamed(_bindingDefinitionName);
        }
        return _bindingDefinition;
    }

    public void setBindingDefinition(ComponentBindingDefinition bindingDefinition)
    {
        _bindingDefinition = bindingDefinition;
        if (_bindingValue != null) {
            _bindingValue.setBindingDefinition(bindingDefinition);
        }
        setChanged();
    }

    public String getBindingDefinitionName()
    {
        if (_bindingDefinition != null) {
            return _bindingDefinition.getVariableName();
        }
        return _bindingDefinitionName;
    }

    public void setBindingDefinitionName(String bindingDefinitionName)
    {
        _bindingDefinitionName = bindingDefinitionName;
        if (_componentInstance != null) {
            setBindingDefinition(_componentInstance.getComponentDefinition().bindingDefinitionNamed(_bindingDefinitionName));
        }
        setChanged();
    }

    public ComponentInstance getComponentInstance()
    {
        return _componentInstance;
    }

    public void setComponentInstance(ComponentInstance componentInstance)
    {
        _componentInstance = componentInstance;
        if (_bindingValue != null) {
            _bindingValue.setOwner(this);
        }
        if (_bindingDefinitionName != null && componentInstance!=null) {
            if (componentInstance.getComponentDefinition()!=null)
                setBindingDefinition(_componentInstance.getComponentDefinition().bindingDefinitionNamed(_bindingDefinitionName));
            else
            	if (!isDeserializing())
            		if (logger.isLoggable(Level.WARNING))
            			logger.warning("Component instance without a component definition: "+componentInstance.getComponentName());
        }
    }

    public AbstractBinding getBindingValue()
    {
        return _bindingValue;
    }

    public void setBindingValue(AbstractBinding value)
    {
        _bindingValue = value;
        if (_bindingValue != null) {
        if (_bindingValue.getBindingDefinition() == null) {
            _bindingValue.setBindingDefinition(getBindingDefinition());
        }
        }
        setChanged();
    }

    @Override
	public FlexoProject getProject()
    {
        if (_componentInstance != null)
            return _componentInstance.getProject();
        return null;
    }

    @Override
	public String getFullyQualifiedName()
    {
        return new StringBuffer(_componentInstance != null ? _componentInstance.getFullyQualifiedName() : "null").append(".").append(getBindingDefinitionName()).append("=").append((getBindingValue() == null ? "null" : getBindingValue().getStringRepresentation())).toString();
    }

    @Override
	public XMLMapping getXMLMapping()
    {
        if (_componentInstance != null)
            return _componentInstance.getXMLMapping();
        return null;
    }

    @Override
	public XMLStorageResourceData getXMLResourceData()
    {
        if (_componentInstance != null)
            return _componentInstance.getXMLResourceData();
        return null;
    }

    @Override
	public String getInspectorName()
    {
        // never inspected by its own
        return null;
    }

    public static final Comparator<ComponentInstanceBinding> componentInstanceBindingComparator = new ComponentInstanceBindingComparator();

    /**
     * Used to sort binding definition according to name alphabetic ordering
     * 
     * @author sguerin
     * 
     */
    public static class ComponentInstanceBindingComparator implements Comparator<ComponentInstanceBinding>
    {
        
        ComponentInstanceBindingComparator(){
            super();
        }

        /**
         * Implements
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
		public int compare(ComponentInstanceBinding o1, ComponentInstanceBinding o2)
        {  
        	String s1 = o1.getBindingDefinitionName();
        	String s2 = o2.getBindingDefinitionName();
        	if ((s1 != null) && (s2 != null))
        		return Collator.getInstance().compare(s1,s2);
        	else return 0;
        }

    }
    
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "component_instance_binding";
    }
    
}
