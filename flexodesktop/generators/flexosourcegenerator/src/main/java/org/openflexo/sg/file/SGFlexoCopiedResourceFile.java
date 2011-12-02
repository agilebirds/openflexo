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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.generator.file.AbstractCGFile;

public class SGFlexoCopiedResourceFile extends AbstractCGFile {

	public SGFlexoCopiedResourceFile(GeneratedSourcesBuilder builder) {
		super(builder.generatedSources);
	}

	public SGFlexoCopiedResourceFile(GeneratedSources generatedSources) {
		super(generatedSources);
	}

	public SGFlexoCopiedResourceFile(SourceRepository repository, FlexoCopiedResource resource) {
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
