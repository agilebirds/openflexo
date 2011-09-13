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
package org.openflexo.dm.view.popups;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.DefaultBrowserConfiguration;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProject;


public class SelectDMEntitiesPopup extends MultipleObjectSelectorPopup {

	static final Logger logger = Logger.getLogger(SelectDMEntitiesPopup.class.getPackage().getName());

	public SelectDMEntitiesPopup(String title, String label, String description, Vector<DMEntity> selectedEntities, FlexoProject project, DMController controller)
	{
		super(title,label,description,new DefaultBrowserConfiguration(project.getDataModel(),new DefaultBrowserConfiguration.ObjectVisibilityDelegate() {
			@Override
			public BrowserFilterStatus getVisibility(BrowserElementType elementType) {
				return BrowserFilterStatus.SHOW;
			}  	
		}),project,controller.getFlexoFrame(),controller.getEditor());
		if (selectedEntities != null)
			choicePanel.setSelectedObjects(selectedEntities);
	}


}
