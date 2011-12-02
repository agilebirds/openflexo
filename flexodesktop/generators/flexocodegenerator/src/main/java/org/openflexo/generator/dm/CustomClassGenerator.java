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
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.utils.JavaClassGenerator;
import org.openflexo.logging.FlexoLogger;

public class CustomClassGenerator extends JavaClassGenerator {

	protected static final String TEMLPATE_NAME = "CustomClass.java.vm";
	private Vector<String> imports;
	private static final Logger logger = FlexoLogger.getLogger(CustomClassGenerator.class.getPackage().getName());

	public CustomClassGenerator(ProjectGenerator projectGenerator, DMEntity entity) {
		super(projectGenerator, entity);
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public VelocityContext defaultContext() {
		VelocityContext context = super.defaultContext();
		imports = new Vector<String>();
		context.put("entity", getObject());

		context.put("props", getEntity().getProperties());

		return context;
	}

	public String addToImports(String fullClassName) {
		if (fullClassName == null) {
			return null;
		}
		if (!imports.contains(fullClassName.trim())) {
			if (fullClassName.indexOf(".") > -1 && !fullClassName.trim().startsWith("java.lang.")) {
				if (!fullClassName.startsWith("default_package")) {
					imports.add(fullClassName.trim());
				}
			}
		}
		return fullClassName;
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
