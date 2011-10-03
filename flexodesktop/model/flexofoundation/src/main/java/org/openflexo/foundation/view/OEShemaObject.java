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
package org.openflexo.foundation.view;

import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.xmlcode.XMLMapping;


public abstract class OEShemaObject extends OEObject {

	private static final Logger logger = Logger.getLogger(OEShemaObject.class.getPackage().getName());

    private OEShema _shema;
    private String _name;
    private OEShemaObject parent = null;
   private Vector<OEShemaObject> childs;
    
    // We dont want to import graphical engine in foundation
    // But you can assert graphical representation is a org.openflexo.fge.GraphicalRepresentation.
    // For a OEShema, graphicalRepresentation is a DrawingGraphicalRepresentation
    // For a OEShape, graphicalRepresentation is a ShapeGraphicalRepresentation
    // For a OEConnector, graphicalRepresentation is a ConnectorGraphicalRepresentation
    private Object _graphicalRepresentation;
    
    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Never use this constructor except for ComponentLibrary
     */
    public OEShemaObject(FlexoProject project)
    {
        super(project);
        childs = new Vector<OEShemaObject>();
    }

    /**
     * Default constructor
     */
    public OEShemaObject(OEShema shema)
    {
        this(shema.getProject());
        setShema(shema);
    }

	public OEShema getShema()
    {
        return _shema;
    }

    public void setShema(OEShema shema)
    {
        _shema = shema;
    }

    /**
     * Returns reference to the main object in which this XML-serializable
     * object is contained relating to storing scheme: here it's the component
     * library
     *
     * @return the component library
     */
    @Override
    public XMLStorageResourceData getXMLResourceData()
    {
        return getShema();
    }

    @Override
    public XMLMapping getXMLMapping()
    {
        return getShema().getXMLMapping();
    }

	@Override
	public String getName() 
	{
		return _name;
	}

	@Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException
	{
		if (requireChange(_name, name)) {
			String oldName = _name;
			_name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName,name));
		}
	}

	public Vector<OEShemaObject> getChilds()
	{
		return childs;
	}

	public void setChilds(Vector<OEShemaObject> someChilds)
	{
		childs.addAll(someChilds);
	}

	public void addToChilds(OEShemaObject aChild)
	{
		//logger.info("****** addToChild() put "+aChild+" under "+this);
		childs.add(aChild);
		aChild.parent = this;
		setChanged();
		if (aChild instanceof OEShape) {
			notifyObservers(new ShapeInserted((OEShape)aChild,this));
		}
		if (aChild instanceof OEConnector) {
			notifyObservers(new ConnectorInserted((OEConnector)aChild));
		}
	}

	public void removeFromChilds(OEShemaObject aChild)
	{
		childs.remove(aChild);
		setChanged();
		if (aChild instanceof OEShape) {
			notifyObservers(new ShapeRemoved((OEShape)aChild,this));
		}
		if (aChild instanceof OEConnector) {
			notifyObservers(new ConnectorRemoved((OEConnector)aChild));
		}
	}

	public OEShape getShapeNamed(String name)
	{
		for (OEShemaObject o : childs) {
			if (o instanceof OEShape && o.getName() != null && o.getName().equals(name)) return (OEShape)o;
		}
		return null;
	}
	
	public OEConnector getConnectorNamed(String name)
	{
		for (OEShemaObject o : childs) {
			if (o instanceof OEConnector && o.getName() != null && o.getName().equals(name)) return (OEConnector)o;
		}
		return null;
	}
	
	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
	}
	
	private Vector<OEShemaObject> ancestors;
	
	public OEShemaObject getParent() 
	{
		return parent;
	}
	
	public Vector<OEShemaObject> getAncestors()
	{
		if (ancestors == null) {
			ancestors = new Vector<OEShemaObject>();
			OEShemaObject current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public static OEShemaObject getFirstCommonAncestor(OEShemaObject child1, OEShemaObject child2)
	{
		Vector<OEShemaObject> ancestors1 = child1.getAncestors();
		Vector<OEShemaObject> ancestors2 = child2.getAncestors();
		for (int i=0; i<ancestors1.size(); i++) {
			OEShemaObject o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) return o1;
		}
		return null;
	}

	public abstract boolean isContainedIn(OEShemaObject o);

	public final boolean contains(OEShemaObject o)
	{
		return o.isContainedIn(this);
	}

	public abstract String getDisplayableDescription();


}
