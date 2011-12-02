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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.localization.FlexoLocalization;

/**
 * Allows to filter some elements in a browser
 * 
 * @author sguerin
 * 
 */
public abstract class BrowserFilter {

	private static final Logger logger = Logger.getLogger(BrowserFilter.class.getPackage().getName());

	public enum BrowserFilterStatus {
		HIDE, SHOW, OPTIONAL_INITIALLY_HIDDEN, OPTIONAL_INITIALLY_SHOWN;
	}

	private BrowserFilterStatus status = BrowserFilterStatus.SHOW;

	private String name;

	private Icon icon;

	protected BrowserFilter(String n, Icon i) {
		super();
		this.name = n;
		this.icon = i;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Setting icon " + i + " for " + name);
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon i) {
		this.icon = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public BrowserFilterStatus getStatus() {
		return status;
	}

	public void setStatus(BrowserFilterStatus s) {
		this.status = s;
	}

}
