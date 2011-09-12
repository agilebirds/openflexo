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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

public class InspectorModel extends ModelObject
{
	private static final Logger logger = FlexoLogger.getLogger(InspectorModel.class.getPackage().getName());

    public String inspectorName;

    public String title;

    private InspectorModel superInspector;

    public String superInspectorName;

    public String inspectedClassName;

    private Hashtable<Integer, TabModel> tabs;
    
    public Vector<InspectorModel> extendingInspectors;

    public InspectorModel()
    {
        super();
        tabs = new Hashtable<Integer, TabModel>();
        extendingInspectors = new Vector<InspectorModel>();
    }

    public String getWidgetTypeForProperty(String propName)
    {
        Iterator it = tabs.values().iterator();
        while (it.hasNext()) {
            String answer = ((TabModel) it.next()).getWidgetTypeForProperty(propName);
            if (answer != null)
                return answer;
        }
        return null;
    }

    /**
     * @param depends
     * @return
     */
    public PropertyModel getPropertyNamed(String depends)
    {
        Iterator it = tabs.values().iterator();
        while (it.hasNext()) {
            PropertyModel answer = ((TabModel) it.next()).getPropertyNamed(depends);
            if (answer != null)
                return answer;
        }
        if (getSuperInspector() != null) {
            return getSuperInspector().getPropertyNamed(depends);
        }
        return null;
    }

    public void debug()
    {
        // System.out.println("InspectorModel: "+hashCode());
        Iterator it = tabs.values().iterator();
        while (it.hasNext()) {
            TabModel tabModel = (TabModel) it.next();
            System.out.println("----------------- " + tabModel.name + "/" + tabModel.hashCode() + " -----------------");
            for (Enumeration en = tabModel.getProperties().elements(); en.hasMoreElements();) {
                PropertyModel p = (PropertyModel) en.nextElement();
                System.out.println(p.name + "/" + p.hashCode() + " tabModel: " + p.getTabModel().name + p.getTabModel().hashCode()
                        + " inspectorModel: " + p.getInspectorModel().hashCode());
            }
        }
    }

    public PropertyModel getInfo()
    {
        Enumeration en = getTabs().elements();
        while (en.hasMoreElements()) {
            TabModel tm = (TabModel) en.nextElement();
            if (tm.name!=null && tm.name.equals("Bindings")) {
                Enumeration en1 = tm.getProperties().elements();
                while (en1.hasMoreElements()) {
                    PropertyModel pm = (PropertyModel) en1.nextElement();
                    if (pm.name!=null && pm.name.equals("additionalBindings")) {
                        return pm;
                    }
                }
            }
        }
        return null;
    }

    public Hashtable<Integer, TabModel> getTabs()
    {
        return tabs;
    }

    public void setTabs(Hashtable<Integer, TabModel> t)
    {
        this.tabs = t;
    }

    public void setTabForKey(TabModel tabModel, Integer key)
    {
        tabs.put(key, tabModel);
        tabModel.setInspectorModel(this);
    }

    public void removeTabWithKey(Integer key)
    {
        tabs.remove(key);
    }

    /**
	 * @param superInspector the superInspector to set
	 */
	public void setSuperInspector(InspectorModel superInspector) {
		this.superInspector = superInspector;
		superInspector.extendingInspectors.add(this);
	}

	/**
	 * @return the superInspector
	 */
	public InspectorModel getSuperInspector() {
		return superInspector;
	}

	public Vector<PropertyModel> getAllPropertyModel()
    {
        Vector<PropertyModel> returned = new Vector<PropertyModel>();
        for (Enumeration en = getTabs().elements(); en.hasMoreElements();) {
            TabModel tab = (TabModel) en.nextElement();
            for (Enumeration en2 = tab.getProperties().elements(); en2.hasMoreElements();) {
                PropertyModel property = (PropertyModel) en2.nextElement();
                returned.add(property);
            }
        }
        return returned;
    }
	
}
