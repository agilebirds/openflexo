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

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;


public abstract class ViewElement extends ViewObject implements Bindable, BindingEvaluationContext {

    private static final Logger logger = Logger.getLogger(ViewElement.class.getPackage().getName());

	/**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public ViewElement(VEShemaBuilder builder)
    {
    	this(builder.shema);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for OEShema
     * 
     * @param shemaDefinition
     */
    public ViewElement(View shema)
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
    	return (getPatternRole() != null);
    }
    
    protected String getOntologicConceptLabelValue()
    {
    	if (getPatternRole() != null)
    		return (String)getPatternRole().getLabel().getBindingValue(this);
     	return null;
    }
    
    protected void setOntologicConceptLabelValue(String aValue)
    {
    	if (getPatternRole() != null && getPatternRole().getReadOnlyLabel())
    		getPatternRole().getLabel().setBindingValue(aValue,this);
     }
    
	public EditionPattern getEditionPattern() 
	{
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
		// Default behaviour is to have only one EditionPattern
		if (getEditionPatternReferences().size() > 0)
			return getEditionPatternReferences().firstElement();
		return null;
	}

	public EditionPatternInstance getEditionPatternInstance() 
	{
		if (getEditionPatternReference() != null) return getEditionPatternReference().getEditionPatternInstance();
		return null;
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
	
	@Override
	public Object getValue(BindingVariable variable) 
	{
		logger.info("Tiens, que dois je repondre alors qu'on me demande pour "+variable);
		return null;
	}
}
