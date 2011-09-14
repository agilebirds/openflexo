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
package org.openflexo.foundation.ontology.calc;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingConnectorInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingConnectorRemoved;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShapeInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShapeRemoved;
import org.openflexo.xmlcode.XMLMapping;


public abstract class CalcDrawingObject extends CalcObject implements Bindable {

	private static final Logger logger = Logger.getLogger(CalcDrawingObject.class.getPackage().getName());

    private String _name;
    private CalcDrawingObject parent = null;
   private Vector<CalcDrawingObject> childs;
    
    // We dont want to import graphical engine in foundation
    // But you can assert graphical representation is a org.openflexo.fge.GraphicalRepresentation.
    // For a CalcDrawingSchema, graphicalRepresentation is a DrawingGraphicalRepresentation
    // For a CalcDrawingShape, graphicalRepresentation is a ShapeGraphicalRepresentation
    // For a CalcDrawingConnector, graphicalRepresentation is a ConnectorGraphicalRepresentation
    private Object _graphicalRepresentation;
    
    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Never use this constructor except for ComponentLibrary
     */
    public CalcDrawingObject()
    {
        super();
        childs = new Vector<CalcDrawingObject>();
    }

	@Override
	public void delete()
	{
		for (CalcDrawingObject o : childs) {
			o.delete();
		}
		super.delete();
		childs.clear();
	}

	public CalcDrawingShema getShema()
    {
		if (getParent() != null)
			return getParent().getShema();
		return null;
    }

 	@Override
	public OntologyCalc getCalc() 
	{
 		if (getShema() != null)
 			return getShema().getCalc();
 		return null;
	}

	@Override
	public XMLMapping getXMLMapping() 
	{
		return getCalcLibrary().get_CALC_DRAWING_MODEL();
	}

	@Override
	public String getName() 
	{
		return _name;
	}

	@Override
	public void setName(String name)
	{
		if (requireChange(_name, name)) {
			String oldName = _name;
			_name = name;
			setChanged();
			notifyObservers(new NameChanged(oldName,name));
		}
	}

	public Vector<CalcDrawingObject> getChilds()
	{
		return childs;
	}

	public void setChilds(Vector<CalcDrawingObject> someChilds)
	{
		childs.addAll(someChilds);
	}

	public void addToChilds(CalcDrawingObject aChild)
	{
		//logger.info("****** addToChild() put "+aChild+" under "+this);
		childs.add(aChild);
		aChild.parent = this;
		setChanged();
		if (aChild instanceof CalcDrawingShape) {
			notifyObservers(new CalcDrawingShapeInserted((CalcDrawingShape)aChild,this));
		}
		if (aChild instanceof CalcDrawingConnector) {
			notifyObservers(new CalcDrawingConnectorInserted((CalcDrawingConnector)aChild,this));
		}
	}

	public void removeFromChilds(CalcDrawingObject aChild)
	{
		childs.remove(aChild);
		aChild.parent = null;
		setChanged();
		if (aChild instanceof CalcDrawingShape) {
			notifyObservers(new CalcDrawingShapeRemoved((CalcDrawingShape)aChild,this));
		}
		if (aChild instanceof CalcDrawingConnector) {
			notifyObservers(new CalcDrawingConnectorRemoved((CalcDrawingConnector)aChild,this));
		}
	}

	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
		//setChanged();
	}
	
	private Vector<CalcDrawingObject> ancestors;
	
	public CalcDrawingObject getParent() 
	{
		return parent;
	}
	
	public Vector<CalcDrawingObject> getAncestors()
	{
		if (ancestors == null) {
			ancestors = new Vector<CalcDrawingObject>();
			CalcDrawingObject current = getParent();
			while (current != null) {
				ancestors.add(current);
				current = current.getParent();
			}
		}
		return ancestors;
	}

	public static CalcDrawingObject getFirstCommonAncestor(CalcDrawingObject child1, CalcDrawingObject child2)
	{
		Vector<CalcDrawingObject> ancestors1 = child1.getAncestors();
		Vector<CalcDrawingObject> ancestors2 = child2.getAncestors();
		for (int i=0; i<ancestors1.size(); i++) {
			CalcDrawingObject o1 = ancestors1.elementAt(i);
			if (ancestors2.contains(o1)) return o1;
		}
		return null;
	}

	@Override
	public void setChanged()
	{
		super.setChanged();
		if (getShema() != null && !ignoreNotifications()) getShema().setIsModified();
	}

	public abstract boolean isContainedIn(CalcDrawingObject o);

	public final boolean contains(CalcDrawingObject o)
	{
		return o.isContainedIn(this);
	}
	
	@Override
	public BindingFactory getBindingFactory() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BindingModel getBindingModel() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
