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
package org.openflexo.drm.helpset;

import java.util.Vector;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.localization.Language;

public class HelpSetConfiguration {

	private final Language language;
	private final Vector<DocItemFolder> docItemFolders;
	private final String title;
	private final String distributionName;

	public HelpSetConfiguration(String aTitle, Language aLanguage, String aDistributionName, Vector<DocItemFolder> someDocItemFolders) {
		super();
		title = aTitle;
		language = aLanguage;
		distributionName = aDistributionName;
		docItemFolders = someDocItemFolders;
	}

	public Vector<DocItemFolder> getDocItemFolders() {
		return docItemFolders;
	}

	public Language getLanguage() {
		return language;
	}

	public String getTitle() {
		return title;
	}

	public String getDistributionName() {
		return distributionName;
	}

}
