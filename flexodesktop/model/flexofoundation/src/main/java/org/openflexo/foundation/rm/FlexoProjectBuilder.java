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

import java.io.File;

import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.localization.FlexoLocalization;

/**
 * Used only during XML serialization to build project
 * 
 * @author sguerin
 * 
 */
public class FlexoProjectBuilder {
	public ProjectLoadingHandler loadingHandler;

	public FlexoProject project;

	public File projectDirectory;

	public FlexoProgress progress;

	private int resourcesCount;

	private int current;

	private int stepsToNotify;

	public FlexoResourceCenterService resourceCenterService;

	public void initResourcesCount(int resourcesCount) {
		if (progress != null) {
			this.resourcesCount = resourcesCount;
			if (resourcesCount < 20) {
				stepsToNotify = 1;
			} else if (resourcesCount < 100) {
				stepsToNotify = 5;
			} else if (resourcesCount < 500) {
				stepsToNotify = 25;
			} else if (resourcesCount < 1000) {
				stepsToNotify = 50;
			} else {
				stepsToNotify = resourcesCount / 20;
			}
			current = 0;
			// System.out.println("Steps to notify "+stepsToNotify);
			progress.resetSecondaryProgress(resourcesCount / stepsToNotify + 1);
		}
	}

	public void notifyResourceLoading(FlexoResource resource) {
		if (progress != null) {
			current++;
			if (stepsToNotify > 0 && current % stepsToNotify == 0) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading_resources...") + " " + current * 100
						/ resourcesCount + " % " + FlexoLocalization.localizedForKey("completed"));
			}
		}
	}

}
