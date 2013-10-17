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

import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.openflexo.fge.control.notifications.ObjectAddedToSelection;
import org.openflexo.fge.control.notifications.ObjectRemovedFromSelection;
import org.openflexo.fge.control.notifications.SelectionCleared;
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
	public void update(Observable o, Object notification) {
		if (o == getEditor()) {
			if (notification instanceof ObjectAddedToSelection || notification instanceof ObjectRemovedFromSelection
					|| notification instanceof SelectionCleared) {
				updateSelection();
			}
		}
	}

	private void updateSelection() {
		if (getSelection().size() > 0) {
			getTextStyleSelector().setEditedObject(getSelection().get(0).getTextStyle());
			if (getSelectedShapes().size() > 0) {
				getForegroundSelector().setEditedObject(getSelectedShapes().get(0).getGraphicalRepresentation().getForeground());
			} else if (getSelectedConnectors().size() > 0) {
				getForegroundSelector().setEditedObject(getSelectedConnectors().get(0).getGraphicalRepresentation().getForeground());
			}
		} else {
			getTextStyleSelector().setEditedObject(getEditor().getCurrentTextStyle());
			getForegroundSelector().setEditedObject(getEditor().getCurrentForegroundStyle());
		}
		if (getSelectedShapes().size() > 0) {
			shapeFactory.setShape(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			// getShapeSelector().setEditedObject(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			bsFactory.setBackgroundStyle(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			// getBackgroundSelector().setEditedObject(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			getShadowStyleSelector().setEditedObject(getSelectedShapes().get(0).getGraphicalRepresentation().getShadowStyle());
		} else {
			shapeFactory.setShape(getEditor().getCurrentShape());
			// getShapeSelector().setEditedObject(getEditor().getCurrentShape());
			bsFactory.setBackgroundStyle(getEditor().getCurrentBackgroundStyle());
			// getBackgroundSelector().setEditedObject(getEditor().getCurrentBackgroundStyle());
			getShadowStyleSelector().setEditedObject(getEditor().getCurrentShadowStyle());
		}
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
