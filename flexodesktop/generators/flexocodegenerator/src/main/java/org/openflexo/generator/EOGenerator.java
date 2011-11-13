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

import java.io.File;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.generator.exception.GenerationException;

/**
 * This generator is used outside the context of a CGRepository for the purpose of generating EO code
 * 
 * @author sylvain
 * 
 */
public class EOGenerator extends ProjectGenerator {

	public EOGenerator(FlexoProject project) throws GenerationException {
		super(project, null);
	}

	public CodeType getGenerationTarget() {
		return getProject().getTargetType();
	}

	@Override
	public File getRootOutputDirectory() {
		return null;
	}
}
