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
package org.openflexo.components.browser.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;

import org.openflexo.components.browser.BrowserFilter;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BrowserFilterMenuItem extends JCheckBoxMenuItem implements ActionListener {
	protected static final Logger logger = Logger.getLogger(BrowserFilterMenuItem.class.getPackage().getName());

	protected BrowserFilter _filter;

	protected ProjectBrowser _browser;

	public BrowserFilterMenuItem(ProjectBrowser browser, BrowserFilter filter) {
		super(filter.getLocalizedName(), filter.getIcon(), filter.getStatus() == BrowserFilterStatus.SHOW);
		_filter = filter;
		_browser = browser;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (_filter != null) {
			_filter.setStatus(isSelected() ? BrowserFilterStatus.SHOW : BrowserFilterStatus.HIDE);
			if (_browser != null) {
				_browser.update();
			}
		}
	}

	public void update() {
		setSelected(_filter.getStatus() == BrowserFilterStatus.SHOW);
	}
}
