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
package org.openflexo.generator.ie;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;

/**
 * @author gpolet
 * 
 */
public class TabComponentGenerator extends ComponentGenerator {

	private static final Logger logger = Logger.getLogger(TabComponentGenerator.class.getPackage().getName());

	/**
	 * @param projectGenerator
	 * @param componentDefinition
	 * @param componentGeneratedName
	 */
	public TabComponentGenerator(ProjectGenerator projectGenerator, TabComponentDefinition componentDefinition) {
		super(projectGenerator, componentDefinition, componentDefinition.getComponentName());
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// Java file
		javaResource = GeneratedFileResourceFactory.createNewTabComponentJavaFileResource(repository, this);
		resources.add(javaResource);

		// WO file
		woResource = GeneratedFileResourceFactory.createNewTabComponentWOFileResource(repository, this);
		resources.add(woResource);

		// API file
		apiResource = GeneratedFileResourceFactory.createNewTabComponentAPIFileResource(repository, this);
		resources.add(apiResource);
	}

	@Override
	public TabComponentDefinition getComponentDefinition() {
		return (TabComponentDefinition) super.getComponentDefinition();
	}

}
