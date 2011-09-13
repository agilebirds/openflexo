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
import org.openflexo.generator.utils.BuildPropertiesGenerator;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class BuildPropertiesResource extends TextFileResource<BuildPropertiesGenerator, CGTextFile> implements GenerationAvailableFileResource{

	private static final Logger logger = FlexoLogger.getLogger(BuildPropertiesResource.class.getPackage().getName());
	
	public BuildPropertiesResource(FlexoProject project) {
		super(project);
		setResourceFormat(FileFormat.TEXT);
	}

	public BuildPropertiesResource(FlexoProjectBuilder builder) {
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
                if (getRepository().getLastWarNameUpdate().before(requestDate)
                        || getRepository().getLastWarNameUpdate().equals(requestDate)) {
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
    public BuildPropertiesFile getGeneratedResourceData()
    {
    	return (BuildPropertiesFile)super.getGeneratedResourceData();
    }
	@Override
    protected BuildPropertiesFile createGeneratedResourceData()
    {
        return new BuildPropertiesFile(getFile(),this);
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
		if(getRepository()!=null) {
			addToDependantResources(getRepository().getGeneratedCode().getFlexoResource());
		}
	}

    public void registerObserverWhenRequired()
    {
    }
    static String getDefaultFileName()
    {
        return "build.properties";
    }


}
