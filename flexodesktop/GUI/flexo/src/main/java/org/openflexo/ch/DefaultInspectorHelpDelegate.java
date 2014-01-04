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
package org.openflexo.ch;

import java.util.logging.Logger;

import javax.help.BadIDException;

import org.openflexo.ApplicationContext;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.Language;
import org.openflexo.help.FlexoHelp;
import org.openflexo.inspector.HelpDelegate;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

public class DefaultInspectorHelpDelegate implements HelpDelegate {

	private static final Logger logger = Logger.getLogger(DenaliWidget.class.getPackage().getName());

	private final DocResourceManager docResourceManager;

	public DefaultInspectorHelpDelegate(DocResourceManager docResourceManager) {
		this.docResourceManager = docResourceManager;
	}

	@Override
	public boolean displayHelpFor(InspectableObject object) {
		DocItem item = docResourceManager.getDocItemFor(object);
		if (item != null) {
			try {
				logger.info("Trying to display help for " + item.getIdentifier());
				FlexoHelp.getHelpBroker().setCurrentID(item.getIdentifier());
				FlexoHelp.getHelpBroker().setDisplayed(true);
			} catch (BadIDException exception) {
				FlexoController.showError(FlexoLocalization.localizedForKey("sorry_no_help_available_for") + " " + item.getIdentifier());
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isHelpAvailableFor(PropertyModel property) {
		ApplicationContext applicationContext = (ApplicationContext) docResourceManager.getServiceManager();
		Language language = docResourceManager.getLanguage(applicationContext.getGeneralPreferences().getLanguage());
		DocItem propertyModelItem = docResourceManager.getDocItemFor(property);
		if (propertyModelItem != null) {
			if (propertyModelItem.getLastApprovedActionForLanguage(language) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getHelpFor(PropertyModel property) {
		ApplicationContext applicationContext = (ApplicationContext) docResourceManager.getServiceManager();
		Language language = docResourceManager.getLanguage(applicationContext.getGeneralPreferences().getLanguage());
		DocItem propertyModelItem = docResourceManager.getDocItemFor(property);
		if (propertyModelItem != null) {
			if (propertyModelItem.getLastApprovedActionForLanguage(language) != null) {
				return propertyModelItem.getLastApprovedActionForLanguage(language).getVersion().getFullHTMLDescription();
			}
		}
		return null;
	}

}
