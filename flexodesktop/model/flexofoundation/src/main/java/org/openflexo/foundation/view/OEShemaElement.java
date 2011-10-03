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

import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.LabelRepresentation;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.xml.OEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;


public abstract class OEShemaElement extends OEShemaObject implements Bindable {

    private static final Logger logger = Logger.getLogger(OEShemaElement.class.getPackage().getName());

    private EditionPatternInstance editionPatternInstance; 
    
	/**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public OEShemaElement(OEShemaBuilder builder)
    {
    	this(builder.shema);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for OEShema
     * 
     * @param shemaDefinition
     */
    public OEShemaElement(OEShema shema)
    {
        super(shema);
   }

	@Override
	public void delete()
	{
		if (getEditionPatternInstance() != null) {
			getEditionPatternInstance().delete();
		}
		super.delete();
	}

    @Override
    public String getName() 
    {
    	if (labelIsBoundToOntologicConcept()) {
    		return getOntologicConceptLabelValue();
    	}
     	return super.getName();
    }
    
	@Override
	public void setName(String name)
	{
		//logger.info("setName of OEShemaElement with "+name);
    	if (labelIsBoundToOntologicConcept()) {
    		setOntologicConceptLabelValue(name);
    	}
    	else {
    		try {
    			super.setName(name);
    		} catch (DuplicateResourceException e) {
    			// cannot happen
    			e.printStackTrace();
    		} catch (InvalidNameException e) {
    			// cannot happen
    			e.printStackTrace();
    		}
    	}
	}

	//public abstract AddShemaElementAction getEditionAction();

    public boolean labelIsBoundToOntologicConcept()
    {
    	return (getLinkedConcept() != null) && (getPatternRole() != null);
    }
    
    protected String getOntologicConceptLabelValue()
    {
    	if (labelIsBoundToOntologicConcept()) {
    		LabelRepresentation lr = getPatternRole().getLabelRepresentation();
    		if (lr == null) {
				return null;
			}
    		//if (lr.isStaticText()) return lr.getText();
    		return lr.getDynamicValue(getLinkedConcept());
    	}
    	return null;
    }
    
    protected void setOntologicConceptLabelValue(String aValue)
    {
    	if (labelIsBoundToOntologicConcept()) {
    		LabelRepresentation lr = getPatternRole().getLabelRepresentation();
       		if (lr == null) {
				return;
			}
    		//if (lr.isStaticText()) return;
    		lr.setDynamicValue(getLinkedConcept(),aValue);
    	}
     }
    
	public AbstractOntologyObject getLinkedConcept()
	{
		if (getEditionPatternInstance() == null) {
			return null;
		}
		if (getEditionPattern() == null) {
			return null;
		}
		if (getPatternRole() == null) {
			return null;
		}
		if (getPatternRole().getBoundPatternRole() == null) {
			return null;
		}
		FlexoModelObject actor = getEditionPatternInstance().getPatternActor(getPatternRole().getBoundPatternRole());
		if (actor instanceof AbstractOntologyObject) {
			return (AbstractOntologyObject)actor;
		} else {
			return null;
		}
	}
	
	public String getLinkedConceptURI()
	{
		if (getLinkedConcept() != null) {
			return getLinkedConcept().getURI();
		}
		return null;
	}

	public EditionPattern getEditionPattern() 
	{
		//System.out.println("pattern instance = "+getEditionPatternInstance());
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getPattern();
		}
		return null;
	}

	public GraphicalElementPatternRole getPatternRole()
	{
		EditionPatternReference ref = getEditionPatternReference();
		if ((ref != null) 
				&& (ref.getPatternRole() instanceof GraphicalElementPatternRole)) {
			return (GraphicalElementPatternRole)ref.getPatternRole();
		}
		return null;
	}
	
	public EditionPatternReference getEditionPatternReference() 
	{
		return getEditionPatternReference(_getEditionPatternIdentifier(),_getEditionPatternInstanceId());
	}

	public EditionPatternInstance getEditionPatternInstance() 
	{
		if (editionPatternInstance == null) {
			//System.out.println("_editionPatternIdentifier="+_editionPatternIdentifier);
    		if (_editionPatternIdentifier != null) {
    			EditionPatternReference ref = getEditionPatternReference(_editionPatternIdentifier,_editionPatternInstanceId);
    			//System.out.println("ref="+ref);
    			if (ref != null) {
					editionPatternInstance = ref.getEditionPatternInstance();
				}
    		}
    	}
		return editionPatternInstance;
	}

	private String _editionPatternIdentifier;
	private long _editionPatternInstanceId;
	
	// Used while serializing/deserializing
	public String _getEditionPatternIdentifier() 
	{
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getPattern().getName();
		}
		return _editionPatternIdentifier;
	}

	// Used while serializing/deserializing
	public void _setEditionPatternIdentifier(String editionPatternIdentifier)
	{
		_editionPatternIdentifier = editionPatternIdentifier;
	}

	// Used while serializing/deserializing
	public long _getEditionPatternInstanceId() 
	{
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getInstanceId();
		}
		return _editionPatternInstanceId;
	}

	// Used while serializing/deserializing
	public void _setEditionPatternInstanceId(long id)
	{
		_editionPatternInstanceId = id;
	}

	@Override
	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole)
	{
		super.registerEditionPatternReference(editionPatternInstance,patternRole);
		_editionPatternIdentifier = editionPatternInstance.getPattern().getName();
		_editionPatternInstanceId = editionPatternInstance.getInstanceId();
	}

    @Override
	public String getInspectorTitle()
    {
   		for (EditionPatternReference ref : getEditionPatternReferences()) {
			return FlexoLocalization.localizedForKey(ref.getEditionPattern().getName());
   		}
   		// Otherwise, take default inspector name
    	return super.getInspectorTitle();
    }

	@Override
	public BindingFactory getBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
