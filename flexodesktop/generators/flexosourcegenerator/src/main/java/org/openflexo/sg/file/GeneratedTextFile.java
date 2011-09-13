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
package org.openflexo.sg.file;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.cg.TextFile;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.generator.SGTextFileGenerator;


public class GeneratedTextFile extends TextFile implements GenerationAvailableFile {

	protected static final Logger logger = FlexoLogger.getLogger(GeneratedTextFile.class.getPackage().getName());

	public GeneratedTextFile(File f, SGTextFileResource resource) {
		super(f);
		try {
			setFlexoResource(resource);
		} catch (DuplicateResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GeneratedTextFile() {
		super();
	}

	@Override
	public SGTextFileResource getFlexoResource() {
		return (SGTextFileResource) super.getFlexoResource();
	}

	@Override
	public SGTextFileGenerator getGenerator() {
		return getFlexoResource().getGenerator();
	}

}