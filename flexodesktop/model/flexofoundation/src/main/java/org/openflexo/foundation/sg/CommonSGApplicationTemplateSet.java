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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateJarResource;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.JavaResourceUtil;

public class CommonSGApplicationTemplateSet extends CommonSGTemplateSet {

	public CommonSGApplicationTemplateSet(File directory, CGTemplateRepository repository, boolean recursive) {
		super(directory, repository, recursive);
	}

	@Override
	public String getFullyQualifiedName() {
		return getRepository().getFullyQualifiedName() + ".COMMON";
	}

	@Override
	public String getInspectorName() {
		return null; // Cannot be inspected
	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey("default_templates");
	}

	@Override
	public String getRootFolderName() {
		return getRepository().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CGTemplate[] findAllTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>(Arrays.asList(super.findAllTemplates()));

		// Add all templates from technology modules implementations.
		for (TechnologyModuleDefinition technologyModuleDefinition : TechnologyModuleDefinition.getAllTechnologyModuleDefinitions()) {
			for (String templateResourcePath : JavaResourceUtil.getMatchingResources(technologyModuleDefinition.getClass(), ".vm")) {
				result.add(new CGTemplateJarResource(this, technologyModuleDefinition.getTechnologyLayer().getFolderName(),
						templateResourcePath));
			}
		}

		return result.toArray(new CGTemplate[0]);
	}
}
