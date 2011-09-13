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

import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.generator.file.AbstractCGFile;


public abstract class SGFile extends AbstractCGFile {

	public SGFile(GeneratedSourcesBuilder builder) {
		this(builder.generatedSources);
		initializeDeserialization(builder);
	}

	public SGFile(GeneratedSources generatedSources) {
		super(generatedSources);
	}

	public SGFile(SourceRepository repository, CGRepositoryFileResource<?, ?, ?> resource) {
		super(repository, resource);
	}

	public static String makeIdentifier(String fileName, String symbolicPathName, String relativePathName) {
		return symbolicPathName + File.separator + relativePathName + File.separator + fileName;
	}

}
