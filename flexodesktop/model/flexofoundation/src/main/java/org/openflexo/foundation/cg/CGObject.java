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
package org.openflexo.foundation.cg;

import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Abstract class implemented by all objects involved in Generated Code representation
  *
 * @author sguerin
 *
 */
public abstract class CGObject extends FlexoModelObject implements FlexoObserver, InspectableObject
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CGObject.class.getPackage().getName());

    private GeneratedOutput _gc;

    /**
     * Default constructor
     */
    public CGObject(GeneratedOutput generatedCode)
    {
        super(generatedCode.getProject());
        setGeneratedCode(generatedCode);
    }

    public GeneratedOutput getGeneratedCode()
    {
        return _gc;
    }

    public void setGeneratedCode(GeneratedOutput gc)
    {
    	_gc = gc;
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
        return getGeneratedCode();
    }

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================
    /**
     * This consrtuctor should never be called anywhere by anyone except GeneratedOutput
     */
    public CGObject(FlexoProject project)
    {
        super(project);
    }

    // ==========================================================================
    // ========================= XML Serialization ============================
    // ==========================================================================

    @Override
    public XMLMapping getXMLMapping()
    {
        return getGeneratedCode().getXMLMapping();
    }

    // ==========================================================================
    // ============================= Instance Methods
    // ===========================
    // ==========================================================================

    public void update(Observable observable, Object obj)
    {
        // Do nothing, since Observer interface is no more used
        // See FlexoObserver
    }

    @Override
	public void update(FlexoObservable observable, DataModification obj)
    {
        // Ignored at this level: implements it in sub-classes
    }

    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
         Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
          return returned;
    }

     @Override
    public FlexoProject getProject()
     {
         if (getGeneratedCode()!=null)
             return getGeneratedCode().getProject();
         return null;
     }

     public void setProject(FlexoProject aProject)
     {
     }

	protected boolean hasGenerationErrors = false;

	public abstract boolean hasGenerationErrors();

	protected boolean needsRegeneration = false;

	public abstract boolean needsRegeneration();

	protected boolean needsModelReinjection = false;

	public abstract boolean needsModelReinjection();

	protected GenerationStatus generationStatus = GenerationStatus.Unknown;

	public abstract GenerationStatus getGenerationStatus();

	public abstract boolean isEnabled();

	// ==========================================================================
    // ========================== Embedding implementation  =====================
    // ==========================================================================

    public abstract boolean isContainedIn(CGObject obj);

    public boolean contains(CGObject obj)
    {
        return obj.isContainedIn(this);
   }


}
