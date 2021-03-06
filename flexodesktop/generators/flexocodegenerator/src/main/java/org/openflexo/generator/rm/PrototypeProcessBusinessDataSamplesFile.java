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
package org.openflexo.generator.rm;

import java.io.File;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.cg.TextFile;
import org.openflexo.generator.utils.PrototypeProcessBusinessDataSamplesGenerator;

public class PrototypeProcessBusinessDataSamplesFile extends TextFile implements GenerationAvailableFile {

	public PrototypeProcessBusinessDataSamplesFile() {
		super();
	}

	public PrototypeProcessBusinessDataSamplesFile(File f, PrototypeProcessBusinessDataSamplesFileResource resource) {
		super(f);
		try {
			setFlexoResource(resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PrototypeProcessBusinessDataSamplesFileResource getFlexoResource() {
		return (PrototypeProcessBusinessDataSamplesFileResource) super.getFlexoResource();
	}

	@Override
	public PrototypeProcessBusinessDataSamplesGenerator getGenerator() {
		return getFlexoResource().getGenerator();
	}

	/**
	 * @see org.openflexo.foundation.rm.cg.ASCIIFile#getEncoding()
	 */
	@Override
	public String getEncoding() {
		return "Windows-1252";
	}
}
