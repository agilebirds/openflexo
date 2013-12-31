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
package org.openflexo.components.browser.dm;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.dm.DMTranstyper;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMTranstyperElement extends DMElement {

	public DMTranstyperElement(DMTranstyper transtyper, ProjectBrowser browser, BrowserElement parent) {
		super(transtyper, BrowserElementType.DM_TRANSTYPER, browser, parent);
	}

	@Override
	public String getName() {
		return getDMTranstyper().getName();
	}

	protected DMTranstyper getDMTranstyper() {
		return (DMTranstyper) getObject();
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

}
