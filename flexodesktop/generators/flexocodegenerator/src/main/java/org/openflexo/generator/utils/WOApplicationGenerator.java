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
package org.openflexo.generator.utils;

import java.util.logging.Logger;

import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.logging.FlexoLogger;

public class WOApplicationGenerator extends JavaClassGenerator
{

    private static final String TEMPLATE_NAME = "Application.java.vm";
	private static final Logger logger = FlexoLogger.getLogger(WOApplicationGenerator.class.getPackage().getName());

    public WOApplicationGenerator(ProjectGenerator projectGenerator)
    {
        super(projectGenerator,projectGenerator.getProject().getDataModel().getWORepository().getCustomApplicationEntity());
    }

	@Override
	public Logger getGeneratorLogger()
	{
		return logger;
	}

	/**
     * Overrides rebuildDependanciesForResource
     * @see org.openflexo.generator.utils.JavaClassGenerator#rebuildDependanciesForResource(JavaFileResource)
     */
    @Override
    public void rebuildDependanciesForResource(JavaFileResource resource)
    {
        
    }
    
    @Override
    public String getTemplateName() {
    	return TEMPLATE_NAME;
    }
}
