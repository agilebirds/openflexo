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
import org.openflexo.foundation.cg.generator.GeneratedComponent;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.generator.GeneratorFormatter;
import org.openflexo.generator.JavaCodeMerger;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.JavaAppendingException;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class WOMainGenerator extends MetaWOGenerator {
	private static final Logger logger = FlexoLogger.getLogger(WOMainGenerator.class.getPackage().getName());

	public WOMainGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null, "Main", "");
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public synchronized void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			VelocityContext velocityContext = defaultContext();
			String javaCode = merge("Main.java.vm", velocityContext);
			try {
				javaAppendingException = null;
				javaCode = JavaCodeMerger.mergeJavaCode(javaCode, getEntity(), javaResource);
			} catch (JavaParseException e) {
				javaAppendingException = new JavaAppendingException(this, getEntity().getFullQualifiedName(), e);
				logger.warning("Could not parse generated code. Escape java merge.");
			}
			_javaFormattingException = null;
			try {
				javaCode = GeneratorFormatter.formatJavaCode(javaCode, "", getIdentifier(), this, getProject());
			} catch (JavaFormattingException javaFormattingException) {
				_javaFormattingException = javaFormattingException;
			}
			String apiCode = merge("Main.api.vm", velocityContext);
			String htmlCode = merge("Main.html.vm", velocityContext);
			String wodCode = merge("Main.wod.vm", velocityContext);
			generatedCode = new GeneratedComponent(getComponentClassName(), javaCode, apiCode, htmlCode, wodCode,
					GeneratorUtils.defaultWOO());
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentJavaFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentJavaFileResource java) {

	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentWOFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentWOFileResource wo) {

	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaWOGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.UtilComponentAPIFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(UtilComponentAPIFileResource api) {

	}

}
