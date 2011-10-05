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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.VEShemaBuilder;



public class ViewShape extends ViewElement {

    private static final Logger logger = Logger.getLogger(ViewShape.class.getPackage().getName());

    //private String multilineText;
    private Vector<ViewConnector> incomingConnectors;
    private Vector<ViewConnector> outgoingConnectors;
    
    //private EditionPatternInstance editionPatternInstance; 
    
	/**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public ViewShape(VEShemaBuilder builder)
    {
    	this(builder.shema);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for OEShema
     * 
     * @param shemaDefinition
     */
    public ViewShape(View shema)
    {
        super(shema);
        incomingConnectors = new Vector<ViewConnector>();
        outgoingConnectors = new Vector<ViewConnector>();
   }

	@Override
	public void delete()
	{
		if (getParent() != null) {
			getParent().removeFromChilds(this);
		}
		for (ViewConnector c : incomingConnectors) {
			c.delete();
		}
		for (ViewConnector c : outgoingConnectors) {
			c.delete();
		}
		super.delete();
		deleteObservers();
	}



	@Override
	public String getClassNameKey() 
	{
		return "oe_shape";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return getShema().getFullyQualifiedName()+"."+getName();
	}

    @Override
	public String getInspectorName() 
    {
    	return Inspectors.VE.OE_SHAPE_INSPECTOR;
    }

    /*@Override
    public AddShemaElementAction getEditionAction() 
    {
    	return getAddShapeAction();
    }
    
	public AddShape getAddShapeAction()
	{
		if (getEditionPattern() != null && getPatternRole() != null)
			return getEditionPattern().getAddShapeAction(getPatternRole());
		return null;
	}*/

	/*public String getMultilineText() 
	{
		return multilineText;
	}

	public void setMultilineText(String multilineText) 
	{
		this.multilineText = multilineText;
	}*/

	public Vector<ViewConnector> getIncomingConnectors() 
	{
		return incomingConnectors;
	}

	public void setIncomingConnectors(Vector<ViewConnector> incomingConnectors) 
	{
		this.incomingConnectors = incomingConnectors;
	}

	public void addToIncomingConnectors(ViewConnector connector) 
	{
		incomingConnectors.add(connector);
	}

	public void removeFromIncomingConnectors(ViewConnector connector) 
	{
		incomingConnectors.remove(connector);
	}

	public Vector<ViewConnector> getOutgoingConnectors() 
	{
		return outgoingConnectors;
	}

	public void setOutgoingConnectors(Vector<ViewConnector> outgoingConnectors) 
	{
		this.outgoingConnectors = outgoingConnectors;
	}
    
	public void addToOutgoingConnectors(ViewConnector connector) 
	{
		outgoingConnectors.add(connector);
	}

	public void removeFromOutgoingConnectors(ViewConnector connector) 
	{
		outgoingConnectors.remove(connector);
	}
	
	@Override
	public boolean isContainedIn(ViewObject o)
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
	
	@Override
	public String getDisplayableDescription()
	{
		return "Shape"+(getLinkedConcept() != null ? " representing "+getLinkedConcept().getDisplayableDescription() : "");
	}

	private Vector<LinkScheme> availableLinkSchemeFromThisShape = null;
	
	public Vector<LinkScheme> getAvailableLinkSchemeFromThisShape()
	{
		if (getLinkedConcept() == null) {
			return null;
		}

		if (!(getLinkedConcept() instanceof OntologyObject)) {
			return null;
		}

		if (availableLinkSchemeFromThisShape == null) {

			ViewPoint calc = getShema().getCalc();
			if (calc == null) {
				return null;
			}
			calc.loadWhenUnloaded();
		
			availableLinkSchemeFromThisShape = new Vector<LinkScheme>();

			for (EditionPattern ep : calc.getEditionPatterns()) {
				for (LinkScheme ls : ep.getLinkSchemes()) {
					if (ls.getFromTargetClass().isSuperConceptOf((OntologyObject)getLinkedConcept())) {
						// This candidate is acceptable
						availableLinkSchemeFromThisShape.add(ls);
					}
				}
			}
		}

		return availableLinkSchemeFromThisShape;
	}


}
