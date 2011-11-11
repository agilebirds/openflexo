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

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.dm.ProcessBusinessDataRepository;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.logging.FlexoLogger;

public class CustomComponentGenerator extends JavaClassGenerator {

	private static final String TEMPLATE_NAME = "CustomComponent.java.vm";
	private static final Logger logger = FlexoLogger.getLogger(CustomComponentGenerator.class.getPackage().getName());

	public CustomComponentGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, projectGenerator.getProject().getDataModel().getWORepository().getCustomComponentEntity());
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	protected VelocityContext defaultContext() {
		VelocityContext velocityContext = super.defaultContext();
		velocityContext.put("WOCOMPONENTNAME", getIdentifier());
		velocityContext.put("ENTITY", getEntity());
		velocityContext.put("entity", getEntity());
		/*   		if(getGenerationTarget().equals(CodeType.PROTOTYPE)){
		   			velocityContext.put("eo_props", getEntity().getPropertiesEOTypesOnly());
		   			velocityContext.put("eopropReplacement", merge("DMEOEntityReplacementProperties.java.vm",velocityContext));
				}
				*/
		return velocityContext;
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.JavaClassGenerator#rebuildDependanciesForResource(JavaFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(JavaFileResource resource) {
		if (resource != null) {
			resource.addToDependantResources(getProject().getFlexoDKVResource());
		}
	}

	@Override
	public String getTemplateName() {
		return TEMPLATE_NAME;
	}

	public String getCustomComponentCurrentProcessBusinessDataGetterName() {
		return ProcessBusinessDataRepository.getCustomComponentCurrentProcessBusinessDataGetterName();
	}
}
