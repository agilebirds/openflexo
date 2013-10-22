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
package org.openflexo.fge.swing.control.tools;

import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.openflexo.fge.control.tools.DianaStyles;
import org.openflexo.fge.swing.SwingViewFactory;

/**
 * SWING implementation of {@link DianaStyles} toolbar
 * 
 * @author sylvain
 * 
 */
public class JDianaStyles extends DianaStyles<JToolBar, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaStyles.class.getPackage().getName());

	private JToolBar component;

	public JToolBar getComponent() {
		if (component == null) {
			component = new JToolBar();
			component.setRollover(true);
			component.add((JComponent) getForegroundSelector());
			component.add((JComponent) getBackgroundSelector());
			component.add((JComponent) getShapeSelector());
			component.add((JComponent) getShadowStyleSelector());
			component.add((JComponent) getTextStyleSelector());
			component.add(Box.createHorizontalGlue());
			component.validate();
		}
		return component;
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
