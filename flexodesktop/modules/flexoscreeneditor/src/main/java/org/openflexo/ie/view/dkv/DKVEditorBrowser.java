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
package org.openflexo.ie.view.dkv;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class DKVEditorBrowser extends ProjectBrowser {

	private static final Logger logger = FlexoLogger.getLogger(DKVEditorBrowser.class.getPackage().getName());

	/**
	 * @param project
	 */
	public DKVEditorBrowser(IEController controller) {
		super(controller);
	}

	/**
	 * Overrides configure
	 * 
	 * @see org.openflexo.components.browser.ProjectBrowser#configure()
	 */
	@Override
	public void configure() {
		setFilterStatus(BrowserElementType.DKV_KEY_LIST, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
		setFilterStatus(BrowserElementType.DKV_KEY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
	}

}
