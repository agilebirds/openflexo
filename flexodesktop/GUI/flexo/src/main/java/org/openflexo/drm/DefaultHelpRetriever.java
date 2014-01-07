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
package org.openflexo.drm;

import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl.HelpRetriever;
import org.openflexo.localization.Language;

public class DefaultHelpRetriever implements HelpRetriever {

	private static final Logger logger = Logger.getLogger(DefaultHelpRetriever.class.getPackage().getName());

	private final DocResourceManager docResourceManager;

	public DefaultHelpRetriever(DocResourceManager docResourceManager) {
		this.docResourceManager = docResourceManager;
	}

	/**
	 * Return help text for supplied object, as defined in DocResourceManager as short version Note: return an HTML version, with embedding
	 * <html>...</html> tags.
	 */
	@Override
	public String shortHelpForObject(FlexoObject object) {
		ApplicationContext applicationContext = (ApplicationContext) docResourceManager.getServiceManager();
		Language language = applicationContext.getGeneralPreferences().getLanguage();

		DocItem propertyModelItem = docResourceManager.getDocItemFor(object);
		if (propertyModelItem != null) {
			if (propertyModelItem.getLastApprovedActionForLanguage(language) != null) {
				return "<html>" + propertyModelItem.getShortHTMLDescription() + "</html>";
			}
		}
		return null;
	}

	/**
	 * Return help text for supplied object, as defined in DocResourceManager as long version Note: return an HTML version, with embedding
	 * <html>...</html> tags.
	 */
	@Override
	public String longHelpForObject(FlexoObject object) {
		ApplicationContext applicationContext = (ApplicationContext) docResourceManager.getServiceManager();
		Language language = applicationContext.getGeneralPreferences().getLanguage();

		DocItem propertyModelItem = docResourceManager.getDocItemFor(object);
		if (propertyModelItem != null) {
			if (propertyModelItem.getLastApprovedActionForLanguage(language) != null) {
				String returned = "<html>" + propertyModelItem.getFullHTMLDescription() + "</html>";
				return returned;
			}
		}
		return null;
	}

}
