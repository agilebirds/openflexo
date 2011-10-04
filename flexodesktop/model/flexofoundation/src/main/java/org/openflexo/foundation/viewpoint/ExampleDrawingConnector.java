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

import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;



public class ExampleDrawingConnector extends ExampleDrawingObject {

    private static final Logger logger = Logger.getLogger(ExampleDrawingConnector.class.getPackage().getName());

	private ExampleDrawingShape startShape;
	private ExampleDrawingShape endShape;
	
    /**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public ExampleDrawingConnector()
    {
        super();
   }

    /**
     * Common constructor for OEShema
     * 
     * @param shemaDefinition
     */
    public ExampleDrawingConnector(ExampleDrawingShape aStartShape, ExampleDrawingShape anEndShape)
    {
        super();
        setStartShape(aStartShape);
        setEndShape(anEndShape);
   }
    
	@Override
	public void delete()
	{
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() 
	{
		return "calc_drawing_connector";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return getShema().getFullyQualifiedName()+"."+getName();
	}

    @Override
	public String getInspectorName() 
    {
    	return Inspectors.CED.CALC_DRAWING_CONNECTOR_INSPECTOR;
    }

	public ExampleDrawingShape getEndShape() 
	{
		return endShape;
	}

	public void setEndShape(ExampleDrawingShape endShape) 
	{
		this.endShape = endShape;
		endShape.addToIncomingConnectors(this);
	}

	public ExampleDrawingShape getStartShape() 
	{
		return startShape;
	}

	public void setStartShape(ExampleDrawingShape startShape) 
	{
		this.startShape = startShape;
		startShape.addToOutgoingConnectors(this);
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
