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
package org.openflexo.generator.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.GeneratedCodeResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGTextFile;
import org.openflexo.generator.utils.ApplicationConfProdGenerator;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class ApplicationConfProdResource extends TextFileResource<ApplicationConfProdGenerator, CGTextFile> implements GenerationAvailableFileResource {

	private static final Logger logger = FlexoLogger.getLogger(ApplicationConfProdResource.class.getPackage().getName());
	
	public ApplicationConfProdResource(FlexoProject project) {
		super(project);
		setResourceFormat(FileFormat.TEXT);
	}

	public ApplicationConfProdResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	@Override
    public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof TemplateLocator) {
			return ((TemplateLocator)resource).needsUpdateForResource(this);
		}
		if (resource instanceof GeneratedCodeResource) {
            GeneratedCodeResource res = (GeneratedCodeResource) resource;
            if (res.isLoaded() && (getRepository() != null)) {
                if ((getRepository().getLastWarNameUpdate().before(requestDate)
                        || getRepository().getLastWarNameUpdate().equals(requestDate))
                        && (getRepository().getLastLoginPasswordUpdate().before(requestDate)
                                || getRepository().getLastLoginPasswordUpdate().equals(requestDate))){
                    if (logger.isLoggable(Level.FINER)) {
						logger.finer("OPTIMIST DEPENDANCY CHECKING FOR BUILD.PROPERTIES");
					}
                    return false;
                }
            }
        }
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
    protected ApplicationConfProdFile createGeneratedResourceData()
    {
        return new ApplicationConfProdFile(getFile(),this);
    }

	@Override
    public ApplicationConfProdFile getGeneratedResourceData()
    {
    	return (ApplicationConfProdFile)super.getGeneratedResourceData();
    }

	/**
	 * Overrides getRepository
	 * @see org.openflexo.foundation.rm.cg.CGRepositoryFileResource#getRepository()
	 */
	@Override
	public CGRepository getRepository()
	{
	    return (CGRepository) super.getRepository();
	}

	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		if (getRepository()!=null) {
			addToDependantResources(getRepository().getGeneratedCode().getFlexoResource());
		}
	}

    public void registerObserverWhenRequired()
    {
    }
    
    static String getDefaultFileName()
    {
        return "Application.conf.PROD";
    }

}
