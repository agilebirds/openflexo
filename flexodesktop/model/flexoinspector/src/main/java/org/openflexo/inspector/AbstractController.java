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
package org.openflexo.inspector;

import org.openflexo.drm.HelpDelegate;
import org.openflexo.inspector.model.TabModel;

/**
 * @author sguerin
 */
public interface AbstractController {
	public InspectorDelegate getDelegate();

	public HelpDelegate getHelpDelegate();

	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject);

	public boolean isTabPanelVisible(TabModel tab, InspectableObject inspectable);

	public InspectorWidgetConfiguration getConfiguration();

	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception);

}
