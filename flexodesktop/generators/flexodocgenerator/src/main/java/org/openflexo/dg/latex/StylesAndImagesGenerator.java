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
package org.openflexo.dg.latex;

import java.util.Vector;

import org.openflexo.dg.DGGenerator;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;

/**
 * @author gpolet
 * 
 */
public class StylesAndImagesGenerator extends DGGenerator<FlexoProject> {
	private GeneralStyleDocGenerator generalStyleDocGenerator;

	private DataModelStyleDocGenerator dataModelStyleDocGenerator;

	/**
	 * @param generator
	 * @param project
	 */
	public StylesAndImagesGenerator(ProjectDocLatexGenerator generator, FlexoProject project) {
		super(generator, project);
		generalStyleDocGenerator = new GeneralStyleDocGenerator(generator, project);
		dataModelStyleDocGenerator = new DataModelStyleDocGenerator(generator, project);
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.dg.DGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.DGRepository, java.util.Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		generalStyleDocGenerator.buildResourcesAndSetGenerators(repository, resources);
		dataModelStyleDocGenerator.buildResourcesAndSetGenerators(repository, resources);
	}

	/**
	 * Overrides getFileExtension
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		return null;
	}

	/**
	 * Overrides getFileName
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileName()
	 */
	@Override
	public String getFileName() {
		return null;
	}

	/**
	 * Overrides getTemplateName
	 * 
	 * @see org.openflexo.dg.DGGenerator#getTemplateName()
	 */
	@Override
	public String getTemplateName() {
		return null;
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.dg.FlexoResourceGenerator#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return "STYLE-AND-IMAGES";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		return false;
	}

}
