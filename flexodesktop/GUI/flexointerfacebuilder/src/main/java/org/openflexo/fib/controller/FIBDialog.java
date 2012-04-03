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
package org.openflexo.fib.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;

@SuppressWarnings("serial")
public class FIBDialog<T> extends JDialog {

	private static final Logger logger = Logger.getLogger(FIBController.class.getPackage().getName());

	private FIBView view;

	public static <T> FIBDialog<T> instanciateComponent(File componentFile, T data, JFrame frame, boolean modal) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			logger.warning("FileNotFoundException: " + componentFile.getAbsolutePath());
			return null;
		}
		return instanciateComponent(fibComponent, data, frame, modal);
	}

	public static <T> FIBDialog<T> instanciateComponent(String fibResourcePath, T data, JFrame frame, boolean modal) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResourcePath);
		if (fibComponent == null) {
			logger.warning("ResourceNotFoundException: " + fibResourcePath);
			return null;
		}
		return instanciateComponent(fibComponent, data, frame, modal);
	}

	private FIBDialog(JFrame frame, boolean modal, FIBComponent fibComponent) {
		super(frame, fibComponent.getParameter("title"), modal);
		view = FIBController.makeView(fibComponent);
		getContentPane().add(view.getResultingJComponent());
		List<FIBButton> def = fibComponent.getDefaultButtons();
		if (def.size() > 0) {
			JButton button = (JButton) view.geDynamicJComponentForObject(def.get(0));
			if (button != null) {
				getRootPane().setDefaultButton(button);
			}
		}
		validate();
		pack();
	}

	protected FIBDialog(FIBComponent fibComponent, T data, JFrame frame, boolean modal) {
		this(frame, modal, fibComponent);
		getController().setDataObject(data);
	}

	public FIBController<T> getController() {
		return view.getController();
	}

	public T getData() {
		return getController().getDataObject();
	}

	public Status getStatus() {
		return getController().getStatus();
	}

	/**
	 * @param flexoFrame
	 */
	public void center() {
		Point center;
		if (getOwner() != null && getOwner().isVisible()) {
			center = new Point(getOwner().getLocationOnScreen().x + getOwner().getWidth() / 2, getOwner().getLocationOnScreen().y
					+ getOwner().getHeight() / 2);
		} else {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			center = new Point(screenSize.width / 2, screenSize.height / 2);
		}
		setLocation(center.x - getSize().width / 2, center.y - getSize().height / 2);
	}

	public void showDialog() {
		center();
		setVisible(true);
		toFront();
	}

	public static <T> FIBDialog<T> instanciateComponent(FIBComponent fibComponent, T data, JFrame frame, boolean modal) {
		FIBDialog<T> dialog = new FIBDialog<T>(fibComponent, data, frame, modal);
		dialog.showDialog();
		return dialog;
	}

}
