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

import java.awt.Window;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;

@SuppressWarnings("serial")
public class FIBDialog<T> extends JDialog {

	private static final Logger logger = Logger.getLogger(FIBController.class.getPackage().getName());

	private FIBView view;

	public static <T> FIBDialog<T> instanciateDialog(File componentFile, T data, Window frame, boolean modal, LocalizedDelegate localizer) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			logger.warning("FileNotFoundException: " + componentFile.getAbsolutePath());
			return null;
		}
		return instanciateDialog(fibComponent, data, frame, modal, localizer);
	}

	public static <T> FIBDialog<T> instanciateDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		return new FIBDialog<T>(fibComponent, data, frame, modal, localizer);
	}

	public static <T> FIBDialog<T> instanciateAndShowDialog(FIBComponent fibComponent, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		FIBDialog<T> dialog = instanciateDialog(fibComponent, data, frame, modal, localizer);
		dialog.showDialog();
		return dialog;
	}

	public static <T> FIBDialog<T> instanciateAndShowDialog(File componentFile, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			logger.warning("FileNotFoundException: " + componentFile.getAbsolutePath());
			return null;
		}
		return instanciateAndShowDialog(fibComponent, data, frame, modal, localizer);
	}

	public static <T> FIBDialog<T> instanciateAndShowDialog(String fibResourcePath, T data, Window frame, boolean modal,
			LocalizedDelegate localizer) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResourcePath);
		if (fibComponent == null) {
			logger.warning("ResourceNotFoundException: " + fibResourcePath);
			return null;
		}
		return instanciateAndShowDialog(fibComponent, data, frame, modal, localizer);
	}

	private FIBDialog(Window window, boolean modal, FIBComponent fibComponent, LocalizedDelegate localizer) {
		super(window, fibComponent.getParameter("title"), modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
		initDialog(fibComponent, localizer);
	}

	public void initDialog(FIBComponent fibComponent, LocalizedDelegate localizer) {
		view = FIBController.makeView(fibComponent, localizer);
		getContentPane().add(view.getResultingJComponent());
		List<FIBButton> def = fibComponent.getDefaultButtons();
		boolean defaultButtonSet = false;
		if (def.size() > 0) {
			JButton button = (JButton) view.geDynamicJComponentForObject(def.get(0));
			if (button != null) {
				getRootPane().setDefaultButton(button);
				defaultButtonSet = true;
			}
		}
		if (!defaultButtonSet) {
			// TODO: choose a button
		}
		pack();
	}

	protected FIBDialog(FIBComponent fibComponent, T data, Window frame, boolean modal, LocalizedDelegate localizer) {
		this(frame, modal, fibComponent, localizer);
		getController().setDataObject(data);
	}

	public FIBController getController() {
		return view.getController();
	}

	public T getData() {
		return (T) getController().getDataObject();
	}

	public Status getStatus() {
		return getController().getStatus();
	}

	/**
	 * @param flexoFrame
	 */
	public void center() {
		setLocationRelativeTo(getOwner());
	}

	public void showDialog() {
		pack();
		center();
		setVisible(true);
	}

}
