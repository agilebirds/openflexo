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
package org.openflexo.dre.view;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.components.tabularbrowser.TabularBrowserView;
import org.openflexo.dre.DREBrowser;
import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;

public class DocCenterView extends JPanel implements SelectionSynchronizedModuleView<DocItemFolder> {

	private static final Logger logger = Logger.getLogger(DocCenterView.class.getPackage().getName());

	private DocItemFolder _rootFolder;
	private DREController _controller;
	private DRETabularBrowserView _treeTable;

	private static final String EMPTY_STRING = "";

	public DocCenterView(DocItemFolder rootFolder, DREController controller) {
		super();
		_rootFolder = rootFolder;
		_controller = controller;
		// TabularBrowserModel model = new TabularBrowserModel(makeBrowser(process,controller),controller.getProject()," ",150);
		TabularBrowserModel model = makeTabularBrowserModel(controller.getProject(), rootFolder.getDocResourceCenter());
		model.addToColumns(new EditableStringColumn<FlexoModelObject>("description", 400) {
			@Override
			public boolean isCellEditableFor(FlexoModelObject object) {
				return hasDescription(object);
			}

			@Override
			public String getValue(FlexoModelObject object) {
				if (hasDescription(object)) {
					return getDescription(object);
				} else {
					return EMPTY_STRING;
				}
			}

			@Override
			public void setValue(FlexoModelObject object, String aValue) {
				if (hasDescription(object)) {
					setDescription(object, aValue);
				}
			}
		});
		_treeTable = new DRETabularBrowserView(controller, model, 10);
		setLayout(new BorderLayout());
		add(_treeTable, BorderLayout.CENTER);
		validate();
	}

	@Override
	public DocItemFolder getRepresentedObject() {
		return _rootFolder;
	}

	@Override
	public void deleteModuleView() {
		if (_controller != null) {
			_controller.removeModuleView(this);
		}
		logger.warning("implements me !");
	}

	@Override
	public FlexoPerspective getPerspective() {
		return _controller.DRE_PERSPECTIVE;
	}

	public DRETabularBrowserView getTabularBrowserView() {
		return _treeTable;
	}

	// Make abstract beyond

	public TabularBrowserModel makeTabularBrowserModel(final FlexoProject project, final DocResourceCenter docResourceCenter) {
		return new TabularBrowserModel(DREBrowser.makeBrowserConfiguration(project, docResourceCenter), " ", 150);
	}

	/*public ProjectBrowser makeBrowser(FlexoModelObject rootObject, FlexoController controller)
	{
	    ProcessBrowser returned = new ProcessBrowser((WKFController)controller);
	    returned.setCurrentProcess((FlexoProcess)rootObject);
	    return returned;
	}*/

	public boolean hasDescription(FlexoModelObject object) {
		if (object instanceof DocItemFolder) {
			return true;
		} else if (object instanceof DocItem) {
			return true;
		}
		return false;
	}

	public String getDescription(FlexoModelObject object) {
		if (object instanceof DocItemFolder) {
			return ((DocItemFolder) object).getDescription();
		} else if (object instanceof DocItem) {
			return ((DocItem) object).getDescription();
		}
		return null;
	}

	public void setDescription(FlexoModelObject object, String aDescription) {
		if (object instanceof DocItemFolder) {
			((DocItemFolder) object).setDescription(aDescription);
		} else if (object instanceof DocItem) {
			((DocItem) object).setDescription(aDescription);
		}
	}

	@Override
	public void fireObjectSelected(FlexoObject object) {
		getTabularBrowserView().fireObjectSelected(object);
	}

	@Override
	public void fireObjectDeselected(FlexoObject object) {
		getTabularBrowserView().fireObjectDeselected(object);
	}

	@Override
	public void fireResetSelection() {
		getTabularBrowserView().fireResetSelection();
	}

	@Override
	public void fireBeginMultipleSelection() {
		getTabularBrowserView().fireBeginMultipleSelection();
	}

	@Override
	public void fireEndMultipleSelection() {
		getTabularBrowserView().fireEndMultipleSelection();
	}

	public static class DRETabularBrowserView extends TabularBrowserView {

		public DRETabularBrowserView(DREController controller, TabularBrowserModel model, int visibleRowCount) {
			this(controller, model);
			setVisibleRowCount(visibleRowCount);
		}

		public DRETabularBrowserView(DREController controller, TabularBrowserModel model) {
			super(controller, model);
			setSynchronizeWithSelectionManager(true);
		}
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

}
