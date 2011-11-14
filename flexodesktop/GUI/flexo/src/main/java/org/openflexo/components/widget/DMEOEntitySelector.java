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
package org.openflexo.components.widget;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * @author gpolet
 * 
 */
public class DMEOEntitySelector<T extends DMEOEntity> extends DMEntitySelector<T> {

	/**
	 * @param project
	 * @param entity
	 */
	public DMEOEntitySelector(FlexoProject project, T entity, Class<T> entityClass) {
		super(project, entity, entityClass);
	}

	/**
	 * Overrides makeCustomPanel
	 * 
	 * @see org.openflexo.components.widget.DMEntitySelector#makeCustomPanel(org.openflexo.foundation.dm.DMEntity)
	 */
	@Override
	protected AbstractSelectorPanel<T> makeCustomPanel(T editedObject) {
		AbstractSelectorPanel<T> sel = new DMEntitySelectorPanel() {
			/**
			 * Overrides createBrowser
			 * 
			 * @see org.openflexo.components.widget.DMEntitySelector.DMEntitySelectorPanel#createBrowser(org.openflexo.foundation.rm.FlexoProject)
			 */
			@Override
			protected ProjectBrowser createBrowser(FlexoProject project) {
				return new DataModelBrowser() {
					/**
					 * Overrides configure
					 * 
					 * @see org.openflexo.components.widget.DMEntitySelector.DataModelBrowser#configure()
					 */
					@Override
					public void configure() {
						super.configure();
						setFilterStatus(BrowserElementType.DM_PACKAGE, BrowserFilterStatus.HIDE);
						setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
						setFilterStatus(BrowserElementType.DM_REPOSITORY, BrowserFilterStatus.HIDE);
						setFilterStatus(BrowserElementType.DM_REPOSITORY_FOLDER, BrowserFilterStatus.HIDE, true);
					}
				};
			}

		};
		return sel;
	}
}
