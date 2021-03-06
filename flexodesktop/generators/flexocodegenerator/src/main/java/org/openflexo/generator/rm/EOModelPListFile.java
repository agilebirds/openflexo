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
import org.openflexo.foundation.rm.cg.PListFile;
import org.openflexo.generator.FlexoResourceGenerator;

public class EOModelPListFile extends PListFile implements GenerationAvailableFile {

	public EOModelPListFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EOModelPListFile(File f, EOModelPListFileResource resource) {
		super(f);
		try {
			setFlexoResource(resource);
		} catch (DuplicateResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public EOModelPListFileResource getFlexoResource() {
		return (EOModelPListFileResource) super.getFlexoResource();
	}

	@Override
	public FlexoResourceGenerator getGenerator() {
		return getFlexoResource().getGenerator();
	}
}
