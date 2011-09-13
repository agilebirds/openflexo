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
package org.openflexo.foundation.sg;


import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;

public class ApplicationSGTemplateRepository extends CGTemplateRepository {

	private static final FileResource flexoTemplatesDirectory
	= new FileResource("Generator/TechnologyModules");

	public ApplicationSGTemplateRepository(SGTemplates templates)
	{
		super(flexoTemplatesDirectory,templates, null);
	}
	
	@Override
	public CommonSGTemplateSet makeCommonTemplateSet()
	{
		return new CommonSGApplicationTemplateSet(getDirectory(), this, true);
	}
	
	@Override
	public String getFullyQualifiedName() 
	{
		return "APPLICATION_TEMPLATES";
	}

	public FileResource getFlexoTemplatesDirectory() 
	{
		return flexoTemplatesDirectory;
	}

	@Override
	public boolean readOnly()
	{
		return true;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.SG.SG_APPLICATION_TEMPLATE_REPOSITORY;
	}

	@Override
	public String getHelpText()
	{
		return FlexoLocalization.localizedForKey("contains_default_templates_used_for_code_generation");
	}

	@Override
	public String getName()
	{
		return FlexoLocalization.localizedForKey("application_templates");
	}
	
	@Override
	public boolean isApplicationRepository() {
		return true;
	}
}
