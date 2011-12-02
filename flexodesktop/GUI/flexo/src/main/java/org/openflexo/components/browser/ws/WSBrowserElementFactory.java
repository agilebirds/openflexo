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
package org.openflexo.components.browser.ws;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ws.ExternalWSFolder;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.InternalWSFolder;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.foundation.ws.WSRepositoryFolder;

public class WSBrowserElementFactory implements BrowserElementFactory {
	@Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {

		if (object instanceof FlexoWSLibrary) {
			return new WSLibraryElement((FlexoWSLibrary) object, browser, parent);
		} else if (object instanceof ExternalWSFolder) {
			return new ExternalWSFolderElement((ExternalWSFolder) object, browser, parent);
		} else if (object instanceof InternalWSFolder) {
			return new InternalWSFolderElement((InternalWSFolder) object, browser, parent);
		} else if (object instanceof ExternalWSService) {
			return new ExternalWSServiceElement((ExternalWSService) object, browser, parent);
		} else if (object instanceof InternalWSService) {
			return new InternalWSServiceElement((InternalWSService) object, browser, parent);
		} else if (object instanceof WSPortTypeFolder) {
			return new WSPortTypeFolderElement((WSPortTypeFolder) object, browser, parent);
		} else if (object instanceof WSRepositoryFolder) {
			return new WSRepositoryFolderElement((WSRepositoryFolder) object, browser, parent);
		} else if (object instanceof WSPortType) {
			return new WSPortTypeElement((WSPortType) object, browser, parent);
		} else if (object instanceof WSRepository) {
			return new WSRepositoryElement((WSRepository) object, browser, parent);
		}

		return null;
	}

}
