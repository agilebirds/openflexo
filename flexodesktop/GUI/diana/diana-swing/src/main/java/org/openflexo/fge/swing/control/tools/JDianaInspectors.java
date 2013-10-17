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

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.notifications.ObjectAddedToSelection;
import org.openflexo.fge.control.notifications.ObjectRemovedFromSelection;
import org.openflexo.fge.control.notifications.SelectionCleared;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors.JInspector;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector.BackgroundStyleFactory;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.localization.LocalizedDelegate;

/**
 * SWING implementation of {@link DianaInspectors}
 * 
 * @author sylvain
 * 
 */
public class JDianaInspectors extends DianaInspectors<JInspector<?>, SwingViewFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(JDianaInspectors.class.getPackage().getName());

	private JInspector<ForegroundStyle> foregroundStyleInspector;
	private JInspector<BackgroundStyleFactory> backgroundStyleInspector;
	private JInspector<TextStyle> textStyleInspector;
	private JInspector<ShadowStyle> shadowInspector;
	private JInspector<ShapeFactory> shapeInspector;

	private BackgroundStyleFactory bsFactory;
	private ShapeFactory shapeFactory;

	public JDianaInspectors() {
		bsFactory = new BackgroundStyleFactory(null);
		shapeFactory = new ShapeFactory(null);
	}

	public JInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JInspector<ForegroundStyle>(FIBLibrary.instance().retrieveFIBComponent(
					FIBForegroundStyleSelector.FIB_FILE), null);
			System.out.println("Controller: " + foregroundStyleInspector.getController());
		}
		return foregroundStyleInspector;
	}

	public JInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JInspector<BackgroundStyleFactory>(FIBLibrary.instance().retrieveFIBComponent(
					FIBBackgroundStyleSelector.FIB_FILE), bsFactory);
		}
		return backgroundStyleInspector;
	}

	public JInspector<TextStyle> getTextStyleInspector() {
		if (textStyleInspector == null) {
			textStyleInspector = new JInspector<TextStyle>(FIBLibrary.instance().retrieveFIBComponent(FIBTextStyleSelector.FIB_FILE),
					getEditor().getCurrentTextStyle());
		}
		return textStyleInspector;
	}

	public JInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JInspector<ShadowStyle>(FIBLibrary.instance().retrieveFIBComponent(FIBShadowStyleSelector.FIB_FILE),
					getEditor().getCurrentShadowStyle());
		}
		return shadowInspector;
	}

	public JInspector<ShapeFactory> getShapeInspector() {
		if (shapeInspector == null) {
			// shapeFactory = new ShapeFactory(getEditor().getCurrentShape());
			shapeInspector = new JInspector<ShapeFactory>(FIBLibrary.instance().retrieveFIBComponent(FIBShapeSelector.FIB_FILE),
					shapeFactory);
		}
		return shapeInspector;
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

	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBDialog<T> implements DianaInspectors.Inspector<T> {

		protected JInspector(FIBComponent fibComponent, T data) {
			super(fibComponent, data, null, false, (LocalizedDelegate) null);
		}

	}

	private void updateSelection() {
		if (getSelection().size() > 0) {
			getTextStyleInspector().setData(getSelection().get(0).getTextStyle());
			if (getSelectedShapes().size() > 0) {
				getForegroundStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getForeground());
			} else if (getSelectedConnectors().size() > 0) {
				getForegroundStyleInspector().setData(getSelectedConnectors().get(0).getGraphicalRepresentation().getForeground());
			}
		} else {
			getTextStyleInspector().setData(getEditor().getCurrentTextStyle());
			getForegroundStyleInspector().setData(getEditor().getCurrentForegroundStyle());
		}
		if (getSelectedShapes().size() > 0) {
			shapeFactory.setShape(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			// getShapeInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getShapeSpecification());
			bsFactory.setBackgroundStyle(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			// getBackgroundStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getBackground());
			getShadowStyleInspector().setData(getSelectedShapes().get(0).getGraphicalRepresentation().getShadowStyle());
		} else {
			shapeFactory.setShape(getEditor().getCurrentShape());
			// getShapeInspector().setData(getEditor().getCurrentShape());
			bsFactory.setBackgroundStyle(getEditor().getCurrentBackgroundStyle());
			// getBackgroundStyleInspector().setData(getEditor().getCurrentBackgroundStyle());
			getShadowStyleInspector().setData(getEditor().getCurrentShadowStyle());
		}
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
