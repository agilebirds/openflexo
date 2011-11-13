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

import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;
import org.openflexo.toolbox.FileFormat;

public class SGTextFile extends SGFile {

	public SGTextFile(GeneratedSourcesBuilder builder) {
		this(builder.generatedSources);
		initializeDeserialization(builder);
	}

	public SGTextFile(GeneratedSources generatedSources) {
		super(generatedSources);
	}

	public SGTextFile(SourceRepository repository, TextFileResource resource) {
		super(repository, resource);
	}

	@Override
	public TextFileResource getResource() {
		return (TextFileResource) super.getResource();
	}

	public SGTextFileResource getTextResource() {
		return (SGTextFileResource) _getResource();
	}

	@Override
	public FileFormat getFileFormat() {
		if (getTextResource() != null) {
			return getTextResource().getFileFormat();
		}
		return FileFormat.UNKNOWN_ASCII_FILE;
	}

}
