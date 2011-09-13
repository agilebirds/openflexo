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
package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;


/**
 * @author sylvain
 * 
 */
public abstract class FlexoGeneratedResource<GRD extends GeneratedResourceData> extends FlexoFileResource<GRD>
{

	private static final Logger logger = Logger.getLogger(FlexoGeneratedResource.class.getPackage().getName());
    
	@Override
	public abstract ResourceType getResourceType();
    
    /**
     * @param builder
     */
    public FlexoGeneratedResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public FlexoGeneratedResource(FlexoProject aProject)
    {
        super(aProject);
    }

    private Date _lastGenerationDate;

    public Date getLastGenerationDate()
    {
    	if (_lastGenerationDate == null || (isConnected() && !getFile().exists())) {
    		_lastGenerationDate = getDiskLastModifiedDate();
    	}
        return _lastGenerationDate;
    }

    public void setLastGenerationDate(Date aDate)
    {
        _lastGenerationDate = aDate;
    }

    /**
     * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager
     * to compute dependancies between resources. This method returns the date that must be considered
     * as last known update for this resource
     * 
     * The date to be considered for generated resources are typically the date when this resource 
     * was generated for the last time.
     * 
     * @return a Date object
     */
    @Override
	public synchronized Date getLastUpdate()
    {
        return getLastGenerationDate();
    }

    /**
     * Return a flag indicating if resource is loaded in memory
     * 
     * @return
     */
    public synchronized boolean isLoaded()
    {
        return (_resourceData != null);
    }

    // Enhance visibility of this method
    @Override
	public FileWritingLock willWriteOnDisk()
    {
    	return super.willWriteOnDisk();
    }

    // Enhance visibility of this method
    @Override
	public void hasWrittenOnDisk(FileWritingLock lock)
    {
    	super.hasWrittenOnDisk(lock);
    }

    // Enhance visibility of this method
    @Override
	public boolean isSaving()
    {
    	return super.isSaving();
    }

    /**
     * Calling this ensure that generated resource is up-to-date. If
     * re-genereration was necessary, if is performed. If this case, return true
     * 
     * @return a boolean indicating if regeneration was necessary
     */
    public boolean ensureGenerationIsUpToDate() throws FlexoException
    {
        FlexoResourceTree tree;
        try {
            tree = update();
            return !tree.isEmpty();
        } catch (ResourceDependancyLoopException e) {
            if (logger.isLoggable(Level.SEVERE))
                logger.log(Level.SEVERE, "Loop in dependant resources of "+this+"!", e);
            throw new FlexoException("Loop in dependant resources of "+this+"!",e);
        } catch (FileNotFoundException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.log(Level.WARNING, "File not found exception.",e);
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean needsGeneration()
    {
        if (!getFile().exists()) {
            return true;
        }
        try {
            return needsUpdate();
        } catch (ResourceDependancyLoopException e) {
            // Warns about the exception
            if (logger.isLoggable(Level.SEVERE))
                logger.log(Level.SEVERE, "Loop in dependant resources of "+this+"!", e);
            return false;
        }
   }
    
    @Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependancyLoopException ,FlexoException, FileNotFoundException
    {
        // This is a little hack for resource that depends of nothing (which is wrong!!! a generated resource should always depend of something)
        if (updatedResources.isEmpty() && !getFile().exists()) {
            if (!getDependantResources().isEmpty())
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("This is not normal, the generated file ("+getFileName()+") does not exist but RM has not computed that it must be generated");
            generate();
        }
        if (!updatedResources.isEmpty()) {
            for (Enumeration<FlexoResource<FlexoResourceData>> e = getDependantResources().elements(false,getProject().getDependancyScheme());e.hasMoreElements();) {
                FlexoResource<FlexoResourceData> resource = e.nextElement();
                resource.update();
            }
            generate();
        }
        else
            getGeneratedResourceData();
    }

    /**
     * Calling this assume that when returning, generated resource will be
     * up-to-date. Regenerate or not depending on dependancies computing
     * 
     * @param requestingResources
     * @return true is generation was required
     */
    /*
     * public boolean ensureGenerationIsUpToDate(Vector requestingResources) {
     * try { logger.info("Checking dependancies"); Vector updatedResources =
     * checkDependancies(requestingResources); logger.info("Found
     * "+updatedResources.size()+" resources for which last update is more
     * recent than last generated date which is "+(new SimpleDateFormat("dd/MM
     * HH:mm:ss")).format(getLastGenerationDate())); if
     * ((updatedResources.size() > 0) || (!getFile().exists())) { // If either
     * some dependant resources required to be synchronized // OR if file was
     * never generated, generate it logger.info("Generate...."); generate();
     * logger.info("Generate....DONE"); return true; } else { logger.info("No
     * regeneration required"); } } catch (LoadResourceException e) {
     * logger.warning ("Could not load resource: "+e.getClass().getName()+". See
     * console for details."); e.printStackTrace();
     * e.getCause().printStackTrace(); } catch (SaveResourceException e) { //
     * Warns about the exception logger.warning ("Could not save generated data:
     * "+e.getClass().getName()+". See console for details.");
     * e.printStackTrace(); } return false; }
     */

    public final void generate() throws SaveResourceException, FlexoException
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Generate " + getName());
        _resourceData = doGenerate();
        FileWritingLock lock = willWriteOnDisk();
        _resourceData.writeToFile(getFile());
        hasWrittenOnDisk(lock);
        finalizeGeneration();
        setLastGenerationDate(new Date());
        notifyResourceChanged();
        notifyResourceStatusChanged();
        if (logger.isLoggable(Level.FINE))
            logger.fine("Generate....DONE");
    }

    /**
     * This method is intended to be overidden by sub-classes that need to free
     * resources and data.
     * 
     */
    public void finalizeGeneration()
    {

    }

    public GRD getGeneratedResourceData()
    {
        if (_resourceData != null) {
            return _resourceData;
        } else {
            if (isGeneratedResourceDataReadable()) {
                try {
                	_resourceData = readGeneratedResourceData();
				} catch (FlexoException e) {
					e.printStackTrace();
				}
            }
            return _resourceData;
        }
    }

    protected GRD doGenerate() throws FlexoException
    {
        if (getGeneratedResourceData() != null) {
            getGeneratedResourceData().regenerate();
            return getGeneratedResourceData();
        } else {
        	GRD returned = createGeneratedResourceData();
            returned.generate();
            return returned;
        }
    }

    public void resetGeneratedResourceData() {
    	_resourceData = null;
    }
    
    protected abstract GRD createGeneratedResourceData();

    public abstract boolean isGeneratedResourceDataReadable();

    public abstract GRD readGeneratedResourceData() throws FlexoException;

    @Override
	protected final Date getRequestDateToBeUsedForOptimisticDependancyChecking(FlexoResource resource)
    {
		return getLastUpdate();
    }

}
