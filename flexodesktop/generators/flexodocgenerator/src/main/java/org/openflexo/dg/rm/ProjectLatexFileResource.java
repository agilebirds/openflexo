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
package org.openflexo.dg.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoTOCResource;
import org.openflexo.foundation.rm.GeneratedDocResource;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class ProjectLatexFileResource extends LatexFileResource<DGLatexGenerator<FlexoProject>> implements FlexoObserver
{
    protected static final Logger logger = FlexoLogger.getLogger(ProjectLatexFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public ProjectLatexFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public ProjectLatexFileResource(FlexoProject aProject)
    {
    	super(aProject);
    }

    private boolean isObserverRegistered = false;

    @Override
	public String getName()
    {
    	if (getCGFile()==null || getCGFile().getRepository()==null || getProject()==null) return super.getName();
    	registerObserverWhenRequired();
    	if (super.getName()==null)
    		setName(nameForRepositoryAndProject(getCGFile().getRepository(), getProject()));
    	return nameForRepositoryAndProject(getCGFile().getRepository(), getProject());
    }

    public void registerObserverWhenRequired()
    {
    	if ((!isObserverRegistered) && (getProject() != null)) {
    		isObserverRegistered = true;
            if (logger.isLoggable(Level.FINE))
                logger.fine("*** addObserver "+getFileName()+" for "+getProject());
            getProject().addObserver(this);
    	}
    }

    public static String nameForRepositoryAndProject(GenerationRepository repository, FlexoProject project)
    {
    	return repository.getName()+".PROJECT_LATEX."+project.getProjectName();
    }

    @Override
	protected LatexFile createGeneratedResourceData()
    {
        return new LatexFile(getFile(),this);
    }

    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        addToDependantResources(getProject().getTOCResource());
        addToDependantResources(getProject().getGeneratedDocResource());
   }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{

	}

   /**
     * Return dependancy computing between this resource, and an other resource,
     * asserting that this resource is contained in this resource's dependant resources
     *
     * @param resource
     * @param dependancyScheme
     * @return
     */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof GeneratedDocResource) {
			if (getGenerator()!=null) {
				if (!requestDate.before(getGenerator().getRepository().getLastUpdateDate())) {
					if (logger.isLoggable(Level.FINER)) logger.finer("OPTIMIST DEPENDANCY CHECKING for DGRepository "+getRepository());
					return false;
				}
			}
		} else if (resource instanceof FlexoTOCResource) {
			if (getGenerator()!=null && getGenerator().getRepository().getTocRepository()!=null) {
				if (!requestDate.before(getGenerator().getRepository().getTocRepository().getLastUpdateDate())) {
					if (logger.isLoggable(Level.FINER)) logger.finer("OPTIMIST DEPENDANCY CHECKING for TOC ENTRY "+getRepository());
					return false;
				}
			}
			return true;
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}


}
