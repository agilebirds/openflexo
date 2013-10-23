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

import javax.swing.JFrame;

import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaInspectors;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors.JInspector;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector.BackgroundStyleFactory;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector.ShapeFactory;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.FileResource;

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

	private JFrame frame;

	public JDianaInspectors(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super.attachToEditor(editor);
		if (foregroundStyleInspector != null) {
			foregroundStyleInspector.setData(getInspectedForegroundStyle(), true);
		}
		if (textStyleInspector != null) {
			textStyleInspector.setData(getInspectedTextStyle(), true);
		}
		if (shadowInspector != null) {
			shadowInspector.setData(getInspectedShadowStyle(), true);
		}
	}

	public static FileResource FOREGROUND_STYLE_FIB_FILE = new FileResource("Fib/ForegroundStylePanel.fib");
	public static FileResource TEXT_STYLE_FIB_FILE = new FileResource("Fib/TextStylePanel.fib");
	public static FileResource SHADOW_STYLE_FIB_FILE = new FileResource("Fib/ShadowStylePanel.fib");

	public JInspector<ForegroundStyle> getForegroundStyleInspector() {
		if (foregroundStyleInspector == null) {
			foregroundStyleInspector = new JInspector<ForegroundStyle>(FIBLibrary.instance()
					.retrieveFIBComponent(FOREGROUND_STYLE_FIB_FILE), getInspectedForegroundStyle(), frame, "Foreground");
		}
		return foregroundStyleInspector;
	}

	public JInspector<BackgroundStyleFactory> getBackgroundStyleInspector() {
		if (backgroundStyleInspector == null) {
			// bsFactory = new BackgroundStyleFactory(getEditor().getCurrentBackgroundStyle());
			backgroundStyleInspector = new JInspector<BackgroundStyleFactory>(FIBLibrary.instance().retrieveFIBComponent(
					FIBBackgroundStyleSelector.FIB_FILE), bsFactory, frame, "Background");
		}
		return backgroundStyleInspector;
	}

	public JInspector<TextStyle> getTextStyleInspector() {
		if (textStyleInspector == null) {
			textStyleInspector = new JInspector<TextStyle>(FIBLibrary.instance().retrieveFIBComponent(TEXT_STYLE_FIB_FILE),
					getInspectedTextStyle(), frame, "Text");
		}
		return textStyleInspector;
	}

	public JInspector<ShadowStyle> getShadowStyleInspector() {
		if (shadowInspector == null) {
			shadowInspector = new JInspector<ShadowStyle>(FIBLibrary.instance().retrieveFIBComponent(SHADOW_STYLE_FIB_FILE),
					getInspectedShadowStyle(), frame, "Shadow");
		}
		return shadowInspector;
	}

	public JInspector<ShapeFactory> getShapeInspector() {
		if (shapeInspector == null) {
			// shapeFactory = new ShapeFactory(getEditor().getCurrentShape());
			shapeInspector = new JInspector<ShapeFactory>(FIBLibrary.instance().retrieveFIBComponent(FIBShapeSelector.FIB_FILE),
					shapeFactory, frame, "Shape");
		}
		return shapeInspector;
	}

	@SuppressWarnings("serial")
	public static class JInspector<T> extends FIBDialog<T> implements DianaInspectors.Inspector<T> {

		protected JInspector(FIBComponent fibComponent, T data, JFrame frame, String title) {
			super(fibComponent, data, frame, false, (LocalizedDelegate) null);
			setTitle(title);
			setAlwaysOnTop(true);
		}

	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}
