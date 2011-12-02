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
package org.openflexo.doceditor.controller.browser;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;

class DEBrowserConfiguration implements BrowserConfiguration {
	private TOCData tocData;
	private GeneratorBrowserConfigurationElementFactory _factory;

	protected DEBrowserConfiguration(TOCData generatedDoc) {
		super();
		tocData = generatedDoc;
		_factory = new GeneratorBrowserConfigurationElementFactory();
	}

	@Override
	public FlexoProject getProject() {
		if (tocData != null) {
			return tocData.getProject();
		}
		return null;
	}

	@Override
	public void configure(ProjectBrowser aBrowser) {
	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		return tocData;
	}

	@Override
	public BrowserElementFactory getBrowserElementFactory() {
		return _factory;
	}

	class GeneratorBrowserConfigurationElementFactory implements BrowserElementFactory {

		GeneratorBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
			if (object instanceof TOCRepository) {
				return new TOCRepositoryElement((TOCRepository) object, browser, parent);
			} else if (object instanceof TOCEntry) {
				return new TOCEntryElement((TOCEntry) object, browser, parent);
			} else if (object instanceof DocType) {
				return new DocTypeElement(object, browser, parent);
			} else if (object instanceof TOCData) {
				return new TOCDataElement((TOCData) object, browser, parent);
			}
			return null;
		}

	}
}