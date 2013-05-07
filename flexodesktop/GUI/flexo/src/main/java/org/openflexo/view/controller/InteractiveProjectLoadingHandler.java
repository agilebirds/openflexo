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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoRMResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResource.DependencyAlgorithmScheme;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource.LoadXMLResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.inspector.InspectableObject;
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

		for (FlexoXMLStorageResource<? extends XMLStorageResourceData> resource : project.getXMLStorageResources()) {
			if (!resource.getXmlVersion().equals(resource.latestVersion())) {
				resourcesToConvert.add(new ResourceToConvert(resource));
				logger.fine("Require conversion for " + resource + " from " + resource.getXmlVersion() + " to " + resource.latestVersion());
			}
		}

		return resourcesToConvert;
	}

	protected void performConversion(FlexoProject project, Vector<ResourceToConvert> resourcesToConvert, FlexoProgress progress) {
		List<FlexoXMLStorageResource<? extends XMLStorageResourceData>> resources = new ArrayList<FlexoXMLStorageResource<? extends XMLStorageResourceData>>();
		for (ResourceToConvert resourceToConvert : resourcesToConvert) {
			if (resourceToConvert.getResource() instanceof FlexoProcessResource) {
				if (!resources.contains(project.getFlexoWorkflowResource())) {
					resources.add(project.getFlexoWorkflowResource());
				}
			} else {
				resources.add(resourceToConvert.getResource());
			}
		}
		progress.setProgress(FlexoLocalization.localizedForKey("converting_project"));
		progress.resetSecondaryProgress(resourcesToConvert.size());
		performingAutomaticConversion = true;
		DependencyAlgorithmScheme scheme = project.getDependancyScheme();
		// Pessimistic dependancy scheme is cheaper and optimistic is not intended for this situation
		project.setDependancyScheme(DependencyAlgorithmScheme.Pessimistic);
		FlexoResource.sortResourcesWithDependancies(resources);
		for (FlexoXMLStorageResource<? extends XMLStorageResourceData> res : resources) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("converting") + " " + res.getResourceIdentifier() + " "
					+ FlexoLocalization.localizedForKey("from") + " " + res.getXmlVersion() + " " + FlexoLocalization.localizedForKey("to")
					+ " " + res.latestVersion());
			if (!res.isDeleted()) {
				res.getResourceData();// Converts the resource by loading it.
			}
		}
		project.setDependancyScheme(scheme);
		performingAutomaticConversion = false;
	}

	@Override
	public void notifySevereLoadingFailure(FlexoResource resource, Exception exception) {
		if (exception instanceof LoadXMLResourceException && resource instanceof FlexoRMResource) {
			FlexoRMResource r = (FlexoRMResource) resource;
			LoadXMLResourceException e = (LoadXMLResourceException) exception;
			if (e.getExtendedMessage().indexOf("JDOMParseException") > -1 && !GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(
						null,
						"Could not load project: file '"
								+ r.getFile().getAbsolutePath()
								+ "' contains invalid XML!\n"
								+ e.getExtendedMessage().substring(e.getExtendedMessage().indexOf("JDOMParseException") + 20,
										e.getExtendedMessage().indexOf("StackTrace") - 1), "XML error", JOptionPane.ERROR_MESSAGE);

			}
			e.printStackTrace();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Full exception message: " + e.getExtendedMessage());
			}
			if (!GraphicsEnvironment.isHeadless()) {
				JOptionPane.showMessageDialog(
						null,
						FlexoLocalization.localizedForKey("could_not_open_resource_manager_file") + "\n"
								+ FlexoLocalization.localizedForKey("to_avoid_damaging_the_project_flexo_will_exit") + "\n"
								+ FlexoLocalization.localizedForKey("error_is_caused_by_file") + " : '"
								+ e.getDeprecatedFileResource().getResourceIdentifier() + "'",
						FlexoLocalization.localizedForKey("error_during_opening_project"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected class ResourceToConvert extends TemporaryFlexoModelObject implements InspectableObject {
		private final FlexoXMLStorageResource<? extends XMLStorageResourceData> _resource;

		ResourceToConvert(FlexoXMLStorageResource<? extends XMLStorageResourceData> resource) {
			_resource = resource;
		}

		public Icon getIcon() {
			return FilesIconLibrary.smallIconForFileFormat(_resource.getResourceType().getFormat());
		}

		public String getResourceType() {
			return _resource.getResourceType().getName();
		}

		@Override
		public String getName() {
			return _resource.getName();
		}

		public String getCurrentVersion() {
			return _resource.getXmlVersion().toString();
		}

		public String getLatestVersion() {
			return _resource.latestVersion().toString();
		}

		@Override
		public String getInspectorName() {
			// unused
			return null;
		}

		public FlexoXMLStorageResource<? extends XMLStorageResourceData> getResource() {
			return _resource;
		}

		protected void convert() {
			_resource.getResourceData();
		}
	}

}
