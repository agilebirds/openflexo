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
package org.openflexo.components.browser;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.selection.SelectionManager;

public class ConfigurableProjectBrowser extends ProjectBrowser {

	private BrowserConfiguration _configuration;

	public ConfigurableProjectBrowser(BrowserConfiguration configuration) {
		this(configuration, null, true);
	}

	public ConfigurableProjectBrowser(BrowserConfiguration configuration, boolean initNow) {
		this(configuration, null, initNow);
	}

	public ConfigurableProjectBrowser(BrowserConfiguration configuration, SelectionManager selectionManager) {
		this(configuration, selectionManager, true);
	}

	public ConfigurableProjectBrowser(BrowserConfiguration configuration, SelectionManager selectionManager, boolean initNow) {
		super(configuration, selectionManager, initNow);
		_configuration = configuration;
		if (initNow) {
			init();
		}
	}

	@Override
	public FlexoObject getDefaultRootObject() {
		if (_configuration != null) {
			return _configuration.getDefaultRootObject();
		}
		return null;
	}

	@Override
	public void configure() {
		if (_configuration != null) {
			_configuration.configure(this);
		}
	}

	public BrowserConfiguration getConfiguration() {
		return _configuration;
	}

}
