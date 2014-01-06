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
package org.openflexo.view.controller;

import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;

public abstract class InteractiveProjectLoadingHandler implements ProjectLoadingHandler {

	private static final Logger logger = Logger.getLogger(InteractiveProjectLoadingHandler.class.getPackage().getName());

	private boolean performingAutomaticConversion = false;

	public InteractiveProjectLoadingHandler() {
		super();
	}

	protected boolean isPerformingAutomaticConversion() {
		return performingAutomaticConversion;
	}

	protected Vector<ResourceToConvert> searchResourcesToConvert(FlexoProject project) {
		Vector<ResourceToConvert> resourcesToConvert = new Vector<ResourceToConvert>();

		for (FlexoResource<?> resource : project.getAllResources()) {
			if (resource instanceof PamelaResource) {
				PamelaResource<?, ?> pamelaResource = (PamelaResource<?, ?>) resource;
				if (!pamelaResource.getModelVersion().equals(pamelaResource.latestVersion())) {
					resourcesToConvert.add(new ResourceToConvert(pamelaResource));
					logger.fine("Require conversion for " + pamelaResource + " from " + pamelaResource.getModelVersion() + " to "
							+ pamelaResource.latestVersion());
				}
			}
		}

		return resourcesToConvert;
	}

	protected void performConversion(FlexoProject project, Vector<ResourceToConvert> resourcesToConvert, FlexoProgress progress) {
		List<PamelaResource<?, ?>> resources = new ArrayList<PamelaResource<?, ?>>();
		for (ResourceToConvert resourceToConvert : resourcesToConvert) {
			resources.add(resourceToConvert.getResource());
		}
		progress.setProgress(FlexoLocalization.localizedForKey("converting_project"));
		progress.resetSecondaryProgress(resourcesToConvert.size());
		performingAutomaticConversion = true;
		// DependencyAlgorithmScheme scheme = project.getDependancyScheme();
		// Pessimistic dependancy scheme is cheaper and optimistic is not intended for this situation
		// project.setDependancyScheme(DependencyAlgorithmScheme.Pessimistic);
		// FlexoResource.sortResourcesWithDependancies(resources);
		for (PamelaResource<?, ?> res : resources) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("converting") + " " + res.getURI() + " "
					+ FlexoLocalization.localizedForKey("from") + " " + res.getModelVersion() + " "
					+ FlexoLocalization.localizedForKey("to") + " " + res.latestVersion());
			if (!res.isDeleted()) {
				try {
					res.getResourceData(null);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FlexoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// Converts the resource by loading it.
			}
		}
		// project.setDependancyScheme(scheme);
		performingAutomaticConversion = false;

	}

	@Override
	public void notifySevereLoadingFailure(FlexoResource<?> resource, Exception e) {
		if (resource instanceof FlexoFileResource) {
			FlexoFileResource<?> r = (FlexoFileResource<?>) resource;
			if (e.getMessage().indexOf("JDOMParseException") > -1 && !GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(
						null,
						"Could not load project: file '"
								+ r.getFile().getAbsolutePath()
								+ "' contains invalid XML!\n"
								+ e.getMessage().substring(e.getMessage().indexOf("JDOMParseException") + 20,
										e.getMessage().indexOf("StackTrace") - 1), "XML error", JOptionPane.ERROR_MESSAGE);

			}
			e.printStackTrace();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Full exception message: " + e.getMessage());
			}
			if (!GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(
						null,
						FlexoLocalization.localizedForKey("could_not_open_resource_manager_file") + "\n"
								+ FlexoLocalization.localizedForKey("to_avoid_damaging_the_project_flexo_will_exit") + "\n"
								+ FlexoLocalization.localizedForKey("error_is_caused_by_file") + " : '" + r.getFile().getAbsolutePath()
								+ "'", FlexoLocalization.localizedForKey("error_during_opening_project"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected class ResourceToConvert {

		private final PamelaResource<?, ?> resource;

		ResourceToConvert(PamelaResource<?, ?> resource) {
			this.resource = resource;
		}

		public Icon getIcon() {
			return IconLibrary.getIconForResource(resource);
		}

		public String getName() {
			return resource.getName();
		}

		public String getCurrentVersion() {
			return resource.getModelVersion().toString();
		}

		public String getLatestVersion() {
			return resource.latestVersion().toString();
		}

		public PamelaResource<?, ?> getResource() {
			return resource;
		}

		protected void convert() {
			try {
				resource.getResourceData(null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
