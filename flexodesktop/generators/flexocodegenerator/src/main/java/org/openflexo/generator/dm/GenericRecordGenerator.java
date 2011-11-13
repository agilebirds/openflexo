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
package org.openflexo.generator.dm;

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.EOEntityJavaFileResource;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.JavaClassGenerator;
import org.openflexo.logging.FlexoLogger;

public class GenericRecordGenerator extends JavaClassGenerator {

	protected static final String TEMLPATE_NAME = "GenericRecord.vm";
	private Vector<String> imports;
	private static final Logger logger = FlexoLogger.getLogger(GenericRecordGenerator.class.getPackage().getName());

	public GenericRecordGenerator(ProjectGenerator projectGenerator, DMEOEntity entity) {
		super(projectGenerator, entity);
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public DMEOEntity getEOEntity() {
		return (DMEOEntity) getEntity();
	}

	@Override
	public VelocityContext defaultContext() {
		VelocityContext context = super.defaultContext();
		imports = new Vector<String>();
		context.put("entity", getObject());
		if (getEOEntity().getPrimaryKeyAttributes().size() > 0)
			context.put("primaryKeyAttributeName", getEOEntity().getPrimaryKeyAttributes().elementAt(0).getName());
		else
			context.put("primaryKeyAttributeName", "<undefined>");

		context.put("props", getEOEntity().getAllNonEOProperties());
		context.put("nonEOproperties", "");

		return context;
	}

	public String addToImports(String fullClassName) {
		if (fullClassName == null)
			return null;
		if (!imports.contains(fullClassName.trim()))
			if (fullClassName.indexOf(".") > -1 && !fullClassName.trim().startsWith("java.lang."))
				if (!fullClassName.startsWith("default_package"))
					imports.add(fullClassName.trim());
		return fullClassName;
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// Java file
		javaResource = (EOEntityJavaFileResource) resourceForKeyWithCGFile(ResourceType.JAVA_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewEOEntityJavaFileResource(repository, this);
			logger.info("Created DMENTITY JAVA resource " + javaResource.getName());
		} else {
			javaResource.setGenerator(this);
			logger.info("Successfully retrieved DMENTITY JAVA resource " + javaResource.getName());
		}
		javaResource.registerObserverWhenRequired();
		resources.add(javaResource);
	}

	@Override
	public void rebuildDependanciesForResource(JavaFileResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTemplateName() {
		return TEMLPATE_NAME;
	}

}
