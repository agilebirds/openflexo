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
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class LocalizedStringGenerator extends MetaWOGenerator {
	private static final Logger logger = FlexoLogger.getLogger(HelpPopupGenerator.class.getPackage().getName());

	public LocalizedStringGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null, projectGenerator.getPrefix() + "LocalizedString", "");
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
			String javaCode = merge("LocalizedString.java.vm", velocityContext);
			String htmlCode = merge("LocalizedString.html.vm", velocityContext);
			String wodCode = merge("LocalizedString.wod.vm", velocityContext);
			String apiCode = merge("LocalizedString.api.vm", velocityContext);
			generatedCode = new GeneratedComponent(getComponentClassName(), javaCode, apiCode, htmlCode, wodCode,
					GeneratorUtils.defaultWOO());

			stopGeneration();

		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentJavaFileResource java) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentWOFileResource wo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebuildDependanciesForResource(UtilComponentAPIFileResource api) {
		// TODO Auto-generated method stub

	}

}
