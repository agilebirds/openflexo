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
package org.openflexo.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.logging.FlexoLogger;

public abstract class MetaGenerator<T extends FlexoModelObject, R extends GenerationRepository> extends Generator<T, R> {

	private static final Logger logger = FlexoLogger.getLogger(MetaGenerator.class.getPackage().getName());

	public MetaGenerator(AbstractProjectGenerator<R> projectGenerator, T object) {
		super(projectGenerator, object);
	}

	@Override
	public final GeneratedCodeResult getGeneratedCode() {
		// Meta generators donnot generate code by definition
		return null;
	}

	@Override
	public final boolean isCodeAlreadyGenerated() {
		// Meta generators donnot generate code by definition
		return true;
	}

	/**
	 * Overrides generate. Should not be used on MetaGenerator, use generate on CGFile
	 */
	@Override
	@Deprecated
	public void generate(boolean forceRegenerate) throws GenerationException {
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Project generator are meta generators! generate should not be called on this object");
		}
	}
}
