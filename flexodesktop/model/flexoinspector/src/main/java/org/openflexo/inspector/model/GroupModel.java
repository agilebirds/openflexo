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
package org.openflexo.inspector.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.openflexo.xmlcode.XMLSerializable;


public class GroupModel extends ParametersContainerModelObject implements XMLSerializable, InnerTabWidget
{

    public String name;

    private Hashtable<String,PropertyModel> properties;

    private TabModel tabModel;

    public Integer constraint;
    
    public GroupModel()
    {
        super();
        properties = new Hashtable<String,PropertyModel>();
    }

    /**
     * @param model
     */
    public void setInspectorModel(InspectorModel model)
    {
        _inspectorModel = model;
    }

    public InspectorModel getInspectorModel()
    {
        return _inspectorModel;
    }

    private InspectorModel _inspectorModel;

    /**
     * @param depends
     * @return
     */
    public PropertyModel getPropertyNamed(String depends)
    {
        for (Enumeration e = properties.elements(); e.hasMoreElements();) {
            PropertyModel next = (PropertyModel) e.nextElement();
            if (next.name.equals(depends))
                return next;
        }
        return null;
    }

    public String getWidgetTypeForProperty(String propName)
    {
        Iterator it = properties.values().iterator();
        PropertyModel prop = null;
        while (it.hasNext()) {
            prop = ((PropertyModel) it.next());
            if (prop.name.equals(propName))
                return prop.getWidget();
        }
        return null;
    }

    public Hashtable<String,PropertyModel> getProperties() {
        return properties;
    }

    public void setProperties(Hashtable<String,PropertyModel> prop) {
        this.properties = prop;
    }

    public void setPropertyForKey(PropertyModel propertyModel, String key)
    {
        properties.put(key,propertyModel);
        if (getTabModel()!=null)
            propertyModel.setTabModel(getTabModel());
    }

    public boolean removePropertyWithKey(String key)
    {
        return properties.remove(key)!=null;
    }
    
    @Override
	public int getIndex()
    {
        return constraint;
    }

    public TabModel getTabModel()
    {
        return tabModel;
    }

    public void setTabModel(TabModel tabModel)
    {
        this.tabModel = tabModel;
        for (Enumeration e = properties.elements(); e.hasMoreElements();) {
            PropertyModel next = (PropertyModel) e.nextElement();
            next.setTabModel(tabModel);
        }
    }


}


