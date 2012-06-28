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
package org.openflexo.module;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.GeneralPreferences;
import org.openflexo.fib.AskLocalResourceCenterDirectory;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.ProjectExitingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;

public class FlexoResourceCenterService {

	private static final Logger logger = Logger.getLogger(FlexoResourceCenterService.class.getPackage().getName());

	private static FlexoResourceCenterService flexoResourceCenterService;

	private static final Object monitor = new Object();

	public static FlexoResourceCenterService instance() {
		if (flexoResourceCenterService == null) {
			synchronized (monitor) {
				if (flexoResourceCenterService == null) {
					flexoResourceCenterService = new FlexoResourceCenterService();
				}
			}
		}
		return flexoResourceCenterService;
	}

	private FlexoResourceCenter flexoResourceCenter;

	private void installFlexoResourceCenter(FlexoResourceCenter aResourceCenter) {
		flexoResourceCenter = aResourceCenter;
	}

	public FlexoResourceCenter getFlexoResourceCenter() {
		return getFlexoResourceCenter(true);
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	public FlexoResourceCenter createAndSetFlexoResourceCenter(File dir) {
		FlexoResourceCenter rc = LocalResourceCenterImplementation.instanciateNewLocalResourceCenterImplementation(dir);
		installFlexoResourceCenter(rc);
		return rc;
	}

	public FlexoResourceCenter getFlexoResourceCenter(boolean createIfNotExist) {
		if (flexoResourceCenter == null && createIfNotExist) {
			if (GeneralPreferences.getLocalResourceCenterDirectory() == null
					|| !GeneralPreferences.getLocalResourceCenterDirectory().exists()) {
				if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
					AskLocalResourceCenterDirectory data = new AskLocalResourceCenterDirectory();
					data.setLocalResourceDirectory(FlexoProject.getResourceCenterFile());
					FIBDialog<AskLocalResourceCenterDirectory> dialog = FIBDialog.instanciateAndShowDialog(
							AskLocalResourceCenterDirectory.FIB_FILE, data, FlexoFrame.getActiveFrame(), true,
							FlexoLocalization.getMainLocalizer());
					switch (dialog.getStatus()) {
					case VALIDATED:
						if (data.getLocalResourceDirectory() != null) {
							if (!data.getLocalResourceDirectory().exists()) {
								data.getLocalResourceDirectory().mkdirs();
							}
							if (!data.getLocalResourceDirectory().exists()) {
								break;
							}
							createAndSetFlexoResourceCenter(data.getLocalResourceDirectory());
							GeneralPreferences.setLocalResourceCenterDirectory(data.getLocalResourceDirectory());
						}
						break;
					case CANCELED:
						// I think that this should not be allowed.
						break;
					case QUIT:
						try {
							getModuleLoader().quit(true);
						} catch (ProjectExitingCancelledException e) {
						}
						break;
					default:
						break;
					}
				} else { // Otherwise, dont ask but create resource center in home directory if required
					File resourceCenterDirectory = FlexoProject.getResourceCenterFile();
					if (!resourceCenterDirectory.exists()) {
						logger.info("Create directory " + resourceCenterDirectory);
						resourceCenterDirectory.mkdirs();
						LocalResourceCenterImplementation rc = LocalResourceCenterImplementation
								.instanciateNewLocalResourceCenterImplementation(resourceCenterDirectory);
						installFlexoResourceCenter(rc);
						GeneralPreferences.setLocalResourceCenterDirectory(resourceCenterDirectory);
					} else {
						flexoResourceCenter = new LocalResourceCenterImplementation(resourceCenterDirectory);
					}
				}
			} else if (flexoResourceCenter == null) {
				flexoResourceCenter = new LocalResourceCenterImplementation(GeneralPreferences.getLocalResourceCenterDirectory());
			}
			if (flexoResourceCenter != null) {
				flexoResourceCenter.update();
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No resource center chosen. This is likely to cause problems.");
				}
			}
		}
		return flexoResourceCenter;
	}

}
