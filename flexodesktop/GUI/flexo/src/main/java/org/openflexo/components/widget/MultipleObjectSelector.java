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
package org.openflexo.components.widget;

import java.util.Vector;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.DefaultBrowserConfiguration;
import org.openflexo.components.browser.DefaultBrowserConfiguration.ObjectVisibilityDelegate;
import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserView;
import org.openflexo.components.widget.SelectionTabularBrowserModel.SelectionTabularBrowserModelSelectionListener;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.controller.FlexoController;

public class MultipleObjectSelector<E extends FlexoObject> extends TabularBrowserView implements
		SelectionTabularBrowserModelSelectionListener {

	public static interface ObjectSelectabilityDelegate<O extends FlexoObject> {
		public boolean isSelectable(O object);
	}

	public MultipleObjectSelector(FlexoObject rootObject, FlexoController controller, ObjectVisibilityDelegate visibilityDelegate,
			ObjectSelectabilityDelegate<E> selectabilityDelegate) {
		super(controller, new SelectionTabularBrowserModel<E>(new DefaultBrowserConfiguration(rootObject, visibilityDelegate),
				selectabilityDelegate));
		getModel().addToSelectionListeners(this);
	}

	public MultipleObjectSelector(BrowserConfiguration browserConfiguration, FlexoController controller,
			ObjectSelectabilityDelegate<E> selectabilityDelegate) {
		super(controller, new SelectionTabularBrowserModel<E>(browserConfiguration, selectabilityDelegate));
		getModel().addToSelectionListeners(this);
	}

	@Override
	public void delete() {
		if (getModel() != null) {
			getModel().removeFromSelectionListeners(this);
		}
		super.delete();
	}

	/**
	 * @return
	 */
	@Override
	public SelectionTabularBrowserModel<E> getModel() {
		return (SelectionTabularBrowserModel<E>) super.getModel();
	}

	@Override
	public Vector<FlexoObject> getSelectedObjects() {
		return new Vector<FlexoObject>(getModel().getSelectionColumn().getSelectedObjects());
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoObject> objects) {
		getModel()._selectionColumn.setSelectedObjects((Vector<E>) objects);
		// getTreeTable().getTree().treeStructureChanged();
	}

	public static interface TabularBrowserConfiguration extends BrowserConfiguration {
		public int getBrowsingColumnWidth();

		public int getExtraColumnCount();

		public AbstractColumn<?, ?> getExtraColumnAt(int index);

		public boolean isSelectable(FlexoObject obj);
	}

	@Override
	public FlexoObject getRootObject() {
		return getModel().getDefaultRootObject();
	}

	public void setRootObject(FlexoObject rootObject) {
		logger.info("setRootObject with " + rootObject);
		if (((DefaultBrowserConfiguration) getModel().getConfiguration()).getDefaultRootObject() != rootObject) {
			((DefaultBrowserConfiguration) getModel().getConfiguration()).setDefaultRootObject(rootObject);
			getModel().update();
		}
	}

	private FlexoProject _project;

	public FlexoProject getProject() {
		return _project;
	}

	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	@Override
	public void notifySelectionChanged() {
	}

}
