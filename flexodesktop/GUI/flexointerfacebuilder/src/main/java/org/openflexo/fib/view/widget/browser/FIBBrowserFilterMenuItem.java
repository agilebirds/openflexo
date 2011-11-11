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
package org.openflexo.fib.view.widget.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;

import org.openflexo.localization.FlexoLocalization;

public class FIBBrowserFilterMenuItem extends JCheckBoxMenuItem implements ActionListener {
	protected static final Logger logger = Logger.getLogger(FIBBrowserFilterMenuItem.class.getPackage().getName());

	protected FIBBrowserElementType _elementType;

	public FIBBrowserFilterMenuItem(FIBBrowserElementType elementType) {
		super(FlexoLocalization.localizedForKey(elementType.getController().getLocalizer(), elementType.getBrowserElement().getName()),
				elementType.getBrowserElement().getImageIcon(), !elementType.isFiltered());
		_elementType = elementType;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_elementType.setFiltered(!isSelected());
	}

	public void update() {
		setSelected(!_elementType.isFiltered());
	}
}
