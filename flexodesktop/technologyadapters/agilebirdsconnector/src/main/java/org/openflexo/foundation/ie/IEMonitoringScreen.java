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
package org.openflexo.foundation.ie;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.cl.MonitoringScreenDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Represents a whole-page WOComponent representing a perspective to monitor a FlexoProcess
 * 
 * @author sguerin
 * 
 */
public final class IEMonitoringScreen extends IEPageComponent {

	public IEMonitoringScreen(FlexoComponentBuilder builder) {
		super(builder);
		initializeDeserialization(builder);
	}

	public IEMonitoringScreen(MonitoringScreenDefinition componentDefinition, FlexoProject prj) {
		super(componentDefinition, prj);
	}

	public FlexoProcess getMonitoredProcess() {
		return getComponentDefinition().getProcess();
	}

	@Override
	public MonitoringScreenDefinition getComponentDefinition() {
		return (MonitoringScreenDefinition) super.getComponentDefinition();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.IE.MONITORING_SCREEN_INSPECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return "MonitoringScreen:" + getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "monitoring_screen";
	}

}
