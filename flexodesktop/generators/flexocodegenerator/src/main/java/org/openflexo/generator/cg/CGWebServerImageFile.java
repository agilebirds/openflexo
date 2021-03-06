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
package org.openflexo.generator.cg;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.file.AbstractCGFile;

public class CGWebServerImageFile extends AbstractCGFile {

	public CGWebServerImageFile(GeneratedCodeBuilder builder) {
		super(builder.generatedCode);
	}

	public CGWebServerImageFile(GeneratedOutput generatedCode) {
		super(generatedCode);
	}

	public CGWebServerImageFile(GenerationRepository repository, CGRepositoryFileResource resource) {
		super(repository.getGeneratedCode());
		setResource(resource);
	}

	public void generate() throws FlexoException {
		getGenerator().generate(false);
	}

	public void regenerate() throws FlexoException {
		getGenerator().generate(true);
	}

	@Override
	public boolean hasGenerationErrors() {
		return false;
	}

	public FlexoGeneratedResource getFlexoResource() {
		return super.getResource();
	}

}
