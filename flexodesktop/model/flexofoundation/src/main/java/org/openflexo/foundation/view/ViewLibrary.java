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
package org.openflexo.foundation.view;

/*
 * FlexoWorkflow.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileResourceRepository;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoViewResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.view.diagram.model.View;

/**
 * The {@link ViewLibrary} contains all {@link FlexoViewResource} referenced in a {@link FlexoProject}
 * 
 * @author sylvain
 */

public class ViewLibrary extends FileResourceRepository<FlexoViewResource> {

	private static final Logger logger = Logger.getLogger(ViewLibrary.class.getPackage().getName());

	private FlexoProject project;

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public ViewLibrary(FlexoProject project) {
		super(getExpectedViewLibraryDirectory(project));
		this.project = project;
		getRootFolder().setName(project.getName());
		exploreDirectoryLookingForViews(getDirectory(), getRootFolder());
	}

	public static File getExpectedViewLibraryDirectory(FlexoProject project) {
		File returned = new File(project.getProjectDirectory(), "Views");
		if (!returned.exists()) {
			returned.mkdir();
		}
		return returned;
	}

	/**
	 * Creates and returns a newly created view library
	 * 
	 * @return a newly created view library
	 */
	public static ViewLibrary createNewViewLibrary(FlexoProject project) {
		return new ViewLibrary(project);
	}

	public FlexoProject getProject() {
		return project;
	}

	public List<View> getViewsForViewPointWithURI(String vpURI) {
		List<View> views = new ArrayList<View>();
		for (FlexoViewResource vr : getAllResources()) {
			if (vr.getViewPoint() != null && vr.getViewPointResource().getURI().equals(vpURI)) {
				views.add(vr.getView());
			}
		}
		return views;
	}

	public void delete(FlexoViewResource vr) {
		logger.info("Remove view " + vr);
		unregisterResource(vr);
		vr.delete();
	}

	public void delete(View v) {
		delete(v.getFlexoResource());
	}

	public boolean isValidForANewViewName(String value) {
		if (value == null) {
			return false;
		}
		return getRootFolder().isValidResourceName(value);
	}

	public FlexoViewResource getViewResourceNamed(String value) {
		if (value == null) {
			return null;
		}
		return getRootFolder().getResourceWithName(value);
	}

	/**
	 * 
	 * @param directory
	 * @param folder
	 * @param viewPointLibrary
	 * @return a flag indicating if some View were found
	 */
	private boolean exploreDirectoryLookingForViews(File directory, RepositoryFolder<FlexoViewResource> folder) {
		boolean returned = false;
		logger.fine("Exploring " + directory);
		if (directory.exists() && directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(".view")) {
					FlexoViewResource vRes;
					try {
						vRes = new FlexoViewResource(getProject(), f.getName(), new FlexoProjectFile(f, getProject()), null);
						logger.info("Found and register view " + vRes.getURI() + " file=" + vRes.getFile().getAbsolutePath());
						registerResource(vRes, folder);
						returned = true;
					} catch (InvalidFileNameException e) {
						e.printStackTrace();
					}
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					RepositoryFolder<FlexoViewResource> newFolder = new RepositoryFolder<FlexoViewResource>(f.getName(), folder, this);
					if (exploreDirectoryLookingForViews(f, newFolder)) {
						returned = true;
					} else {
						folder.removeFromChildren(newFolder);
					}
				}
			}
		}
		return returned;
	}

}
