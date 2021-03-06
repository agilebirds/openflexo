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

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Allows to filter some elements in a browser The semantic of those filters is the OR: if any of declared filter for a browser says that
 * supplied element should be displayed, display this element
 * 
 * @author sguerin
 * 
 */
public abstract class CustomBrowserFilter extends BrowserFilter {

	private static final Logger logger = Logger.getLogger(CustomBrowserFilter.class.getPackage().getName());

	public CustomBrowserFilter(String name, Icon icon) {
		super(name, icon);
	}

	public abstract boolean accept(FlexoModelObject object);
}
