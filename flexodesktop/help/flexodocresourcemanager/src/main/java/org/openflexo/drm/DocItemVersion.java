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

import org.openflexo.localization.Language;
import org.openflexo.toolbox.FlexoVersion;

public class DocItemVersion extends DRMObject {

	static final Logger logger = Logger.getLogger(DocItemVersion.class.getPackage().getName());

	private DocItem docItem;
	private Language language;
	private FlexoVersion version;
	private String shortHTMLDescription;
	private String fullHTMLDescription;

	public DocItem getDocItem() {
		return docItem;
	}

	public void setDocItem(DocItem docItem) {
		this.docItem = docItem;
	}

	public FlexoVersion getVersion() {
		return version;
	}

	public void setVersion(FlexoVersion version) {
		this.version = version;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getShortHTMLDescription() {
		return shortHTMLDescription;
	}

	public void setShortHTMLDescription(String shortHTMLDescription) {
		this.shortHTMLDescription = shortHTMLDescription;
	}

	public String getFullHTMLDescription() {
		return fullHTMLDescription;
	}

	public void setFullHTMLDescription(String fullHTMLDescription) {
		this.fullHTMLDescription = fullHTMLDescription;
	}

	@Override
	public String getIdentifier() {
		return getDocItem().getIdentifier() + "." + getLanguage().getIdentifier() + "." + getVersion().toString();
	}
}
