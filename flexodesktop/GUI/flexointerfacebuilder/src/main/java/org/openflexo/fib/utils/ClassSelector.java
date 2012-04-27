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
package org.openflexo.fib.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to edit a binding
 * 
 * @author sguerin
 * 
 */
public class ClassSelector extends TextFieldCustomPopup<Class> implements FIBCustomComponent<Class, ClassSelector> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(ClassSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ClassSelector.fib");

	private static final Color DEFAULT_COLOR1 = Color.RED;
	private static final Color DEFAULT_COLOR2 = Color.WHITE;

	private Class _revertValue;

	protected ClassSelectorDetailsPanel _selectorPanel;

	public ClassSelector(Class editedObject) {
		super(editedObject);
		setRevertValue(editedObject);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
			_selectorPanel = null;
		}

	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Class oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = oldValue;
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public Class getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Class editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ClassSelectorDetailsPanel makeCustomPanel(Class editedObject) {
		return new ClassSelectorDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Class editedObject) {
		// logger.info("updateCustomPanel with "+editedObject+" _selectorPanel="+_selectorPanel);
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public class ClassSelectorDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;

		protected ClassSelectorDetailsPanel(Class aClass) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(LoadedClassesInfo.instance(aClass));

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void delete() {
			controller.delete();
			fibView.delete();
			controller = null;
			fibView = null;
		}

		public void update() {
			controller.setDataObject(LoadedClassesInfo.instance(getEditedObject()));
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public class CustomFIBController extends FIBController<LoadedClassesInfo> {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				setEditedObject(LoadedClassesInfo.instance().getSelectedClassInfo().getRepresentedClass());
				ClassSelector.this.apply();
			}

			public void cancel() {
				ClassSelector.this.cancel();
			}

			public void reset() {
				setEditedObject(null);
				ClassSelector.this.apply();
			}

			public void classChanged() {
				System.out.println("Class changed !!!");
			}

		}

	}

	/* @Override
	 public void setEditedObject(BackgroundStyle object)
	 {
	 	logger.info("setEditedObject with "+object);
	 	super.setEditedObject(object);
	 }*/

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	/*protected void pointerLeavesPopup()
	{
	   cancel();
	}*/

	public ClassSelectorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	public ClassSelector getJComponent() {
		return this;
	}

	@Override
	public Class<Class> getRepresentedType() {
		return Class.class;
	}

	@Override
	public String renderedString(Class editedObject) {
		if (editedObject == null) {
			return "";
		}
		return editedObject.getSimpleName();
	}

	/*public static void main(String[] args)
	{

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() { 
				return
				FIBAbstractEditor.makeArray(LoadedClassesInfo.instance(java.lang.Object.class)); 
			} 
			@Override
			public File getFIBFile() { return FIB_FILE; }
		};

		editor.launch();
	}*/

}