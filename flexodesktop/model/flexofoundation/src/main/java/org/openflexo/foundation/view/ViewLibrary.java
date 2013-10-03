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

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FileResourceRepository;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.rm.ViewResourceImpl;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;

/**
 * The {@link ViewLibrary} contains all {@link FlexoViewResource} referenced in a {@link FlexoProject}
 * 
 * @author sylvain
 */

public class ViewLibrary extends FileResourceRepository<ViewResource> {

	private static final Logger logger = Logger.getLogger(ViewLibrary.class.getPackage().getName());

	private FlexoProject project;

	/**
	 * Create a new FlexoComponentLibrary.
	 */
	public ViewLibrary(FlexoProject project) {
		super(project, getExpectedViewLibraryDirectory(project));
		this.project = project;
		getRootFolder().setName(project.getName());
		exploreDirectoryLookingForViews(getDirectory(), getRootFolder());
	}

	public FlexoServiceManager getServiceManager() {
		if (getProject() != null) {
			return getProject().getServiceManager();
		}
		return null;
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
		for (ViewResource vr : getAllResources()) {
			if (vr.getViewPoint() != null && vr.getViewPointResource().getURI().equals(vpURI)) {
				views.add(vr.getView());
			}
		}
		return views;
	}

	/*public void delete(ViewResource vr) {
		logger.info("Remove view " + vr);
		unregisterResource(vr);
		vr.delete();
	}

	public void delete(View v) {
		delete(v.getResource());
	}*/

	public boolean isValidForANewViewName(String value) {
		if (value == null) {
			return false;
		}
		return getRootFolder().isValidResourceName(value);
	}

	public ViewResource getViewResourceNamed(String value) {
		if (value == null) {
			return null;
		}
		return getRootFolder().getResourceWithName(value);
	}

	public ViewResource getView(String viewURI) {
		if (viewURI == null) {
			return null;
		}
		return getResource(viewURI);
	}

	public VirtualModelInstanceResource getVirtualModelInstance(String virtualModelInstanceURI) {
		if (virtualModelInstanceURI == null) {
			return null;
		}
		// System.out.println("lookup mvi " + virtualModelInstanceURI);
		String viewURI = virtualModelInstanceURI.substring(0, virtualModelInstanceURI.lastIndexOf("/"));
		// System.out.println("lookup view " + viewURI);
		ViewResource vr = getView(viewURI);
		if (vr != null) {
			for (VirtualModelInstanceResource vmir : vr.getContents(VirtualModelInstanceResource.class)) {
				if (vmir.getURI().equals(virtualModelInstanceURI)) {
					// System.out.println("Found " + vmir);
					return vmir;
				}
			}
		} else {
			logger.warning("Cannot find View " + viewURI);
		}
		logger.warning("Cannot find VirtualModelInstance " + virtualModelInstanceURI);
		return null;
	}

	/**
	 * 
	 * @param directory
	 * @param folder
	 * @param viewPointLibrary
	 * @return a flag indicating if some View were found
	 */
	private boolean exploreDirectoryLookingForViews(File directory, RepositoryFolder<ViewResource> folder) {
		boolean returned = false;
		System.out.println("Exploring " + directory.getAbsolutePath());
		logger.fine("Exploring " + directory);
		if (directory.exists() && directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(".view")) {
					ViewResource vRes;
					vRes = ViewResourceImpl.retrieveViewResource(f, folder, this);
					if (vRes != null) {
						logger.info("Found and register view " + vRes.getURI() + " file=" + vRes.getFile().getAbsolutePath() + " folder="
								+ folder + " path=" + folder.getFile());
						registerResource(vRes, folder);
						returned = true;
					}
				}
				if (f.isDirectory() && !f.getName().equals("CVS")) {
					RepositoryFolder<ViewResource> newFolder = new RepositoryFolder<ViewResource>(f.getName(), folder, this);
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
