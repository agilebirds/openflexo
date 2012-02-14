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

import java.util.List;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.Language;

public class HelpSetConfiguration {

	private Language language;
	private List<DocItemFolder> docItemFolders;
	private String title;
	private String distributionName;

	public HelpSetConfiguration(String aTitle, Language aLanguage, String aDistributionName, List<DocItemFolder> someDocItemFolders) {
		super();
		title = aTitle;
		language = aLanguage;
		distributionName = aDistributionName;
		docItemFolders = someDocItemFolders;
	}

	public List<DocItemFolder> getDocItemFolders() {
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
