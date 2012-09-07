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
package org.openflexo.dm.view.controller;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.components.widget.DMEntitySelector;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.model.FlexoPerspective;

public abstract class DMPerspective extends FlexoPerspective {

	protected JPanel searchPanel;
	private DMEntity searchedEntity = null;
	private DMController controller;
	private DMEntitySelector<DMEntity> entitySelector;

	public DMPerspective(String name, final DMController controller) {
		super(name);
		this.controller = controller;

		searchPanel = new JPanel(new BorderLayout());
		searchPanel.add(new JLabel(FlexoLocalization.localizedForKey("search")), BorderLayout.WEST);
		entitySelector = new DMEntitySelector<DMEntity>(controller.getProject(), searchedEntity, DMEntity.class) {
			@Override
			public void setEditedObject(DMEntity entity) {
				if (!browserMayRepresent(entity)) {
					return;
				}
				changeBrowserFiltersFor(entity);
				super.setEditedObject(entity);
				searchedEntity = entity;
				controller.getSelectionManager().setSelectedObject(entity);
			}
		};
		searchPanel.add(entitySelector, BorderLayout.CENTER);
	}

	public void setProject(FlexoProject project) {
		entitySelector.setRootObject(project.getDataModel());
	}

	public DMController getController() {
		return controller;
	}

	protected class PropertiesBrowser extends DMBrowser {
		private DMEntity representedEntity = null;

		protected PropertiesBrowser(DMController controller) {
			super(controller, false);
		}

		protected DMEntity getRepresentedEntity() {
			return representedEntity;
		}

		protected void setRepresentedEntity(DMEntity representedEntity) {
			this.representedEntity = representedEntity;
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return representedEntity;
		}
	}

	protected abstract boolean browserMayRepresent(DMEntity entity);

	protected abstract void changeBrowserFiltersFor(DMEntity entity);

}