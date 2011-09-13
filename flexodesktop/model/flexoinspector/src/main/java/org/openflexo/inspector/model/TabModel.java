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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.AbstractController;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLSerializable;


public class TabModel extends Observable implements XMLSerializable
{

	private static final Logger logger = Logger.getLogger(TabModel.class.getPackage().getName());

	public String name;

	public Integer index;

	public LayoutModel layoutModel;

	private Hashtable<String,PropertyModel> properties;

	private Hashtable<String,GroupModel> groups;

	public Vector hiddenProperties;

	public Vector<HiddenGroupModel> hiddenGroups;

	public String layoutName;

	public String visibilityContext;

	public TabModel()
	{
		super();
		properties = new Hashtable<String,PropertyModel>();
		hiddenProperties = new Vector();
		hiddenGroups = new Vector<HiddenGroupModel>();
		groups = new Hashtable<String, GroupModel>();

	}

	public void finalizeTabModelDecoding()
	{
		for (Enumeration<PropertyModel> e = properties.elements(); e.hasMoreElements();) {
			PropertyModel next = e.nextElement();
			next.setTabModel(this);
		}
		for (Enumeration<GroupModel> e = groups.elements(); e.hasMoreElements();) {
			GroupModel next = e.nextElement();
			next.setTabModel(this);
		}

	}

	private TabModel copy(AbstractController c)
	{
		try {
			if (encodedModel==null) {
				encodedModel = XMLCoder.encodeObjectWithMapping(this, InspectorMapping.getInstance(),StringEncoder.getDefaultInstance());
			}
			TabModel returned = (TabModel) XMLDecoder.decodeObjectWithMapping(encodedModel, InspectorMapping.getInstance(), c);
			returned._inspectorModel = _inspectorModel;
			return returned;
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected exception occured: " + e.getClass().getName());
			}
			e.printStackTrace();
		}
		return null;
	}

	public static TabModel createMergedModel(Vector<TabModel> tabs, AbstractController c)
	{
		if (tabs.size() == 0) {
			return null;
		}
		Enumeration<TabModel> en = tabs.elements();
		TabModel answer = en.nextElement().copy(c);
		while (en.hasMoreElements()) {
			answer = performMerge(answer, en.nextElement());
		}
		return answer;
	}

	private static TabModel performMerge(TabModel lower, TabModel upper)
	{
		Iterator upperIterator = upper.properties.values().iterator();
		while (upperIterator.hasNext()) {
			PropertyModel propModel = (PropertyModel) upperIterator.next();
			lower.setPropertyForKey(propModel,propModel.name);
		}
		Iterator upperIterator2 = upper.groups.values().iterator();
		while (upperIterator2.hasNext()) {
			GroupModel lineModel = (GroupModel) upperIterator2.next();
			lower.setGroupForKey(lineModel,lineModel.name);
		}

		upperIterator = upper.hiddenProperties.iterator();
		while (upperIterator.hasNext()) {
			HiddenPropertyModel propertyToHide = (HiddenPropertyModel) upperIterator.next();
			lower.removePropertyWithKey(propertyToHide.name);
		}
		upperIterator2 = upper.hiddenGroups.iterator();
		while (upperIterator2.hasNext()) {
			HiddenGroupModel lineToHide = (HiddenGroupModel) upperIterator2.next();
			lower.removePropertyWithKey(lineToHide.name);
		}
		if (upper.visibilityContext != null) {
			if (lower.visibilityContext != null) {
				String merge = lower.visibilityContext;
				StringTokenizer st1 = new StringTokenizer(upper.visibilityContext,",");
				while (st1.hasMoreTokens()) {
					String token = st1.nextToken();
					StringTokenizer st2 = new StringTokenizer(lower.visibilityContext,",");
					boolean found = false;
					while(st2.hasMoreTokens() && !found) {
						found = st2.nextToken().indexOf(token) >= 0;
					}
					if (!found) {
						merge = merge + "," + token;
					}
				}
				lower.visibilityContext = merge;
			} else {
				lower.visibilityContext = upper.visibilityContext;
			}
		}
		return lower;
	}

	public String getWidgetTypeForProperty(String propName)
	{
		Iterator it = properties.values().iterator();
		PropertyModel prop = null;
		while (it.hasNext()) {
			prop = ((PropertyModel) it.next());
			if (prop.name.equals(propName)) {
				return prop.getWidget();
			}
		}
		Iterator i = groups.values().iterator();
		while (i.hasNext()) {
			GroupModel line = (GroupModel) i.next();
			String type = line.getWidgetTypeForProperty(propName);
			if (type!=null) {
				return type;
			}
		}
		return null;
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

	private String encodedModel;

	/**
	 * @param depends
	 * @return
	 */
	public PropertyModel getPropertyNamed(String depends)
	{
		for (Enumeration e = properties.elements(); e.hasMoreElements();) {
			PropertyModel next = (PropertyModel) e.nextElement();
			if (next.name.equals(depends)) {
				return next;
			}
		}
		for (Enumeration e = groups.elements(); e.hasMoreElements();) {
			GroupModel next = (GroupModel) e.nextElement();
			PropertyModel p = next.getPropertyNamed(depends);
			if (p!=null) {
				return p;
			}
		}
		return null;
	}

	public Hashtable<String,PropertyModel> getProperties()
	{
		return properties;
	}

	public void setProperties(Hashtable<String,PropertyModel> prop)
	{
		orderedProperties = null;
		this.properties = prop;
	}

	public void setPropertyForKey(PropertyModel propertyModel, String key)
	{
		orderedProperties = null;
		properties.put(key,propertyModel);
		propertyModel.setTabModel(this);
	}

	public void removePropertyWithKey(String key)
	{
		orderedProperties = null;
		Object o = properties.remove(key);
		if (o==null) {
			Enumeration en = groups.elements();
			while (en.hasMoreElements()) {
				GroupModel line = (GroupModel) en.nextElement();
				if (line.removePropertyWithKey(key)) {
					return;
				}
			}
		}
	}

	/**
	 * @param depends
	 * @return
	 */
	 public GroupModel getLineNamed(String depends)
	{
		for (Enumeration e = groups.elements(); e.hasMoreElements();) {
			GroupModel next = (GroupModel) e.nextElement();
			if (next.name.equals(depends)) {
				return next;
			}
		}
		return null;
	}

	 public Hashtable<String,GroupModel> getGroups() {
		 return groups;
	 }

	 public void setGroups(Hashtable<String,GroupModel> lines) {
		 this.groups = lines;
	 }

	 public void setGroupForKey(GroupModel lineModel, String key)
	 {
		 groups.put(key,lineModel);
		 lineModel.setTabModel(this);
	 }

	 public void removeGroupWithKey(String key)
	 {
		 groups.remove(key);
	 }

	 private Vector<PropertyModel> orderedProperties = null;

	 public Vector<PropertyModel> getOrderedProperties()
	 {
		 if (orderedProperties == null) {
			 orderedProperties = new Vector<PropertyModel>(properties.values());
			 Collections.sort(orderedProperties,new Comparator<PropertyModel>() {
				 @Override
				 public int compare(PropertyModel o1, PropertyModel o2)
				 {
					 return o1.constraint-o2.constraint;
				 }
			 });
		 }
		 return orderedProperties;
	 }

}


