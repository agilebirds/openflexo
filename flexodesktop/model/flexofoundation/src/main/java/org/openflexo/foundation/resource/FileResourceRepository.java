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
package org.openflexo.foundation.resource;

import java.io.File;
import java.util.logging.Logger;

/**
 * A {@link FileResourceRepository} implements a {@link ResourceRepository} base on a {@link File} which is the root directory
 * 
 * @author sylvain
 * 
 * @param <R>
 * @param <TA>
 */
public class FileResourceRepository<R extends FlexoResource<?>> extends ResourceRepository<R> {

	private static final Logger logger = Logger.getLogger(FileResourceRepository.class.getPackage().getName());

	private File directory;

	/**
	 * Creates a new {@link FileResourceRepository}
	 */
	public FileResourceRepository(File directory) {
		super();
		this.directory = directory;
	}

	public File getDirectory() {
		return directory;
	}

}
