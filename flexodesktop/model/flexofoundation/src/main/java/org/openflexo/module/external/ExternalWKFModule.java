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
package org.openflexo.module.external;

import javax.swing.JComponent;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;

/**
 * External view of the WKF Module
 * 
 * @author sguerin
 */
public interface ExternalWKFModule extends ExternalModule {

	public FlexoWorkflow getFlexoWorkflow();

	public JComponent createScreenshotForObject(FlexoModelObject target);

	public JComponent createScreenshotForProcess(FlexoProcess target);

	public float getScreenshotQuality();

	public void finalizeScreenshotGeneration();

	public Object getProcessRepresentation(FlexoProcess process, boolean showAll);

	public void disposeProcessRepresentation(Object processRepresentation);
}
