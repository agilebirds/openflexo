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
package org.openflexo.fib.editor.view.container;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableTabPanelView<T> extends FIBTabPanelView<T> implements FIBEditableView<FIBTabPanel, JTabbedPane> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableTabPanelView.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBTabPanel, JTabbedPane> delegate;

	private Vector<PlaceHolder> placeholders;

	private final FIBEditorController editorController;

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public FIBEditableTabPanelView(FIBTabPanel model, FIBEditorController editorController) {
		super(model, editorController.getController());
		this.editorController = editorController;
		delegate = new FIBEditableViewDelegate<FIBTabPanel, JTabbedPane>(this);
		placeholders = new Vector<PlaceHolder>();
		model.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void delete() {
		placeholders.clear();
		placeholders = null;
		delegate.delete();
		getComponent().getPropertyChangeSupport().removePropertyChangeListener(this);
		super.delete();
	}

	@Override
	public Vector<PlaceHolder> getPlaceHolders() {
		return placeholders;
	}

	@Override
	public FIBEditableViewDelegate<FIBTabPanel, JTabbedPane> getDelegate() {
		return delegate;
	}

	@Override
	public synchronized void buildSubComponents() {
		super.buildSubComponents();
		getJComponent().addTab("+", new JPanel());
		getJComponent().setModel(new DefaultSingleSelectionModel() {
			@Override
			public void setSelectedIndex(int index) {
				if (getJComponent().getTabCount() > 1) {
					index = Math.min(index, getJComponent().getTabCount() - 2);
				}
				super.setSelectedIndex(index);
			}
		});
		getJComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int tab = getJComponent().getUI().tabForCoordinate(getJComponent(), e.getX(), e.getY());
				if (tab == getJComponent().getTabCount() - 1) {
					FIBTab newTabComponent = editorController.getFactory().newFIBTab();
					newTabComponent.setLayout(Layout.border);
					newTabComponent.setTitle("NewTab");
					newTabComponent.finalizeDeserialization();
					getComponent().addToSubComponents(newTabComponent);
				}
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		delegate.receivedModelNotifications((FIBModelObject) evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
	}

}
