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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

public class ProjectResourceCenter extends FileSystemBasedResourceCenter {

	protected static final Logger logger = Logger.getLogger(ProjectResourceCenter.class.getPackage().getName());

	private FlexoProject project;

	public static ProjectResourceCenter instanciateProjectResourceCenter(FlexoProject project) {
		logger.info("Instanciate ResourceCenter from " + project.getProjectDirectory());
		ProjectResourceCenter projectResourceCenter = new ProjectResourceCenter(project);
		try {
			projectResourceCenter.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectResourceCenter;
	}

	private ProjectResourceCenter(FlexoProject project) {
		super(project.getProjectDirectory());
		this.project = project;
	}

	public FlexoProject getProject() {
		return project;
	}

	@Override
	public final void initialize(TechnologyAdapterService technologyAdapterService) {
		super.initialize(technologyAdapterService);
	}

	@Override
	public List<FlexoResource<?>> getAllResources(IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		return Collections.emptyList();
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, FlexoVersion version, Class<T> type, IProgress progress) {
		return null;
	}

	@Override
	public void publishResource(FlexoResource<?> resource, FlexoVersion newVersion, IProgress progress) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() throws IOException {
		System.out.println("Updating ProjectResourceCenter.... " + this);
	}

}
