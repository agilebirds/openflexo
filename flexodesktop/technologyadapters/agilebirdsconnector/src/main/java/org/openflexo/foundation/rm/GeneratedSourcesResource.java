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
package org.openflexo.foundation.rm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;

/**
 * Represents all generated sources
 * 
 * @author sguerin
 */
public class GeneratedSourcesResource extends FlexoGeneratedOutputResource<GeneratedSources> {

	private static final Logger logger = Logger.getLogger(GeneratedSourcesResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public GeneratedSourcesResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public GeneratedSourcesResource(FlexoProject aProject) {
		super(aProject);
		if (aProject != null) {
			try {
				setResourceFile(new FlexoProjectFile(ProjectRestructuration.getExpectedGeneratedSourcesFile(aProject), aProject));
			} catch (InvalidFileNameException e) {
				FlexoProjectFile f = new FlexoProjectFile("GeneratedSources");
				f.setProject(aProject);
				try {
					setResourceFile(f);
				} catch (InvalidFileNameException e1) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("This should not happen.");
					}
					e1.printStackTrace();
				}
			}
		}

	}

	public GeneratedSourcesResource(FlexoProject aProject, FlexoProjectFile generatedCodeFile) throws InvalidFileNameException {
		super(aProject);
		setResourceFile(generatedCodeFile);
	}

	public GeneratedSourcesResource(FlexoProject aProject, org.openflexo.foundation.cg.GeneratedOutput cg,
			FlexoProjectFile generatedCodeFile) throws InvalidFileNameException {
		this(aProject, generatedCodeFile);
		_resourceData = (GeneratedSources) cg;
		try {
			cg.setFlexoResource(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.GENERATED_SOURCES;
	}

	@Override
	public Class<GeneratedSources> getResourceDataClass() {
		return GeneratedSources.class;
	}

	@Override
	public Object instanciateNewBuilder() {
		GeneratedSourcesBuilder builder = new GeneratedSourcesBuilder(this);
		builder.generatedSources = _resourceData;
		return builder;
	}

}
