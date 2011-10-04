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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;



public class ExampleDrawingShape extends ExampleDrawingObject {

    private static final Logger logger = Logger.getLogger(ExampleDrawingShape.class.getPackage().getName());

    //private String multilineText;
    private Vector<ExampleDrawingConnector> incomingConnectors;
    private Vector<ExampleDrawingConnector> outgoingConnectors;
    
    //private EditionPatternInstance editionPatternInstance; 
    
	/**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public ExampleDrawingShape()
    {
        super();
        incomingConnectors = new Vector<ExampleDrawingConnector>();
        outgoingConnectors = new Vector<ExampleDrawingConnector>();
   }

	@Override
	public void delete()
	{
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		for (ExampleDrawingConnector c : incomingConnectors) {
			c.delete();
		}
		for (ExampleDrawingConnector c : outgoingConnectors) {
			c.delete();
		}
		super.delete();
		deleteObservers();
	}


	@Override
	public String getClassNameKey() 
	{
		return "calc_drawing_shape";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return getShema().getFullyQualifiedName()+"."+getName();
	}

    @Override
	public String getInspectorName() 
    {
    	return Inspectors.CED.CALC_DRAWING_SHAPE_INSPECTOR;
    }

 	public Vector<ExampleDrawingConnector> getIncomingConnectors() 
	{
		return incomingConnectors;
	}

	public void setIncomingConnectors(Vector<ExampleDrawingConnector> incomingConnectors) 
	{
		this.incomingConnectors = incomingConnectors;
	}

	public void addToIncomingConnectors(ExampleDrawingConnector connector) 
	{
		incomingConnectors.add(connector);
	}

	public void removeFromIncomingConnectors(ExampleDrawingConnector connector) 
	{
		incomingConnectors.remove(connector);
	}

	public Vector<ExampleDrawingConnector> getOutgoingConnectors() 
	{
		return outgoingConnectors;
	}

	public void setOutgoingConnectors(Vector<ExampleDrawingConnector> outgoingConnectors) 
	{
		this.outgoingConnectors = outgoingConnectors;
	}
    
	public void addToOutgoingConnectors(ExampleDrawingConnector connector) 
	{
		outgoingConnectors.add(connector);
	}

	public void removeFromOutgoingConnectors(ExampleDrawingConnector connector) 
	{
		outgoingConnectors.remove(connector);
	}

	@Override
	public boolean isContainedIn(ExampleDrawingObject o)
	{
		if (o == this) {
			return true;
		}
		if ((getParent() != null) && (getParent() == o)) {
			return true;
		}
		if (getParent() != null) {
			return getParent().isContainedIn(o);
		}
		return false;
	}
	

}
