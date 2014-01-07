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
package org.openflexo.fib.model;

import java.util.Vector;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTabPanel.FIBTabPanelImpl.class)
@XMLElement(xmlTag = "TabPanel")
public interface FIBTabPanel extends FIBContainer {

	@PropertyIdentifier(type = boolean.class)
	public static final String RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY = "restrictPreferredSizeToSelectedComponent";

	@Getter(value = RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isRestrictPreferredSizeToSelectedComponent();

	@Setter(RESTRICT_PREFERRED_SIZE_TO_SELECTED_COMPONENT_KEY)
	public void setRestrictPreferredSizeToSelectedComponent(boolean restrictPreferredSizeToSelectedComponent);

	public static abstract class FIBTabPanelImpl extends FIBContainerImpl implements FIBTabPanel {

		private boolean restrictPreferredSizeToSelectedComponent = false;

		public FIBTabPanelImpl() {
		}

		@Override
		public String getIdentifier() {
			return null;
		}

		public Vector<FIBTab> getTabs() {
			Vector<FIBTab> returned = new Vector<FIBTab>();
			for (FIBComponent subComponent : getSubComponents()) {
				if (subComponent instanceof FIBTab) {
					returned.add((FIBTab) subComponent);
				}
			}
			return returned;
		}

		@Override
		public boolean isRestrictPreferredSizeToSelectedComponent() {
			return restrictPreferredSizeToSelectedComponent;
		}

		@Override
		public void setRestrictPreferredSizeToSelectedComponent(boolean restrictPreferredSizeToSelectedComponent) {
			this.restrictPreferredSizeToSelectedComponent = restrictPreferredSizeToSelectedComponent;
			getPropertyChangeSupport().firePropertyChange("restrictPreferredSizeToSelectedComponent",
					!restrictPreferredSizeToSelectedComponent, restrictPreferredSizeToSelectedComponent);
		}

	}
}
