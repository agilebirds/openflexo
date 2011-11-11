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
package org.openflexo.wkf.view.doc;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.tabular.model.TextColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.ProcessBrowser;
import org.openflexo.wkf.controller.WKFController;

public class WKFDocumentationView extends JPanel implements SelectionSynchronizedModuleView<FlexoProcess> {

	private static final Logger logger = Logger.getLogger(WKFDocumentationView.class.getPackage().getName());

	private FlexoProcess _process;
	private WKFController _controller;
	private WKFTabularBrowserView _treeTable;

	private static final String EMPTY_STRING = "";

	public WKFDocumentationView(FlexoProcess process, WKFController controller) {
		super();
		_process = process;
		_controller = controller;
		// TabularBrowserModel model = new TabularBrowserModel(makeBrowser(process,controller),controller.getProject()," ",150);
		TabularBrowserModel model = makeTabularBrowserModel(process);
		model.addToColumns(new TextColumn<FlexoModelObject>("description", 400) {
			@Override
			public boolean isCellEditableFor(FlexoModelObject object) {
				return hasDescription(object);
			}

			@Override
			public String getValue(FlexoModelObject object) {
				if (hasDescription(object)) {
					return getDescription(object);
				} else
					return EMPTY_STRING;
			}

			@Override
			public void setValue(FlexoModelObject object, String aValue) {
				if (hasDescription(object)) {
					setDescription(object, aValue);
				}
			}
		});
		_treeTable = new WKFTabularBrowserView(controller, model, 10);
		setLayout(new BorderLayout());
		add(_treeTable, BorderLayout.CENTER);
		validate();
	}

	@Override
	public FlexoProcess getRepresentedObject() {
		return _process;
	}

	@Override
	public void deleteModuleView() {
		_controller.removeModuleView(this);
		logger.warning("implements me !");
	}

	@Override
	public FlexoPerspective<FlexoProcess> getPerspective() {
		return _controller.DOCUMENTATION_PERSPECTIVE;
	}

	public WKFTabularBrowserView getTabularBrowserView() {
		return _treeTable;
	}

	// Make abstract above

	public TabularBrowserModel makeTabularBrowserModel(final FlexoProcess process) {
		BrowserConfiguration configuration = new BrowserConfiguration() {
			@Override
			public FlexoProject getProject() {
				return process.getProject();
			}

			@Override
			public void configure(ProjectBrowser browser) {
				browser.setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				// browser.setFilterStatus(BrowserElementType.ROLE, ElementTypeBrowserFilter.OPTIONAL_INITIALLY_HIDDEN);
				browser.setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				// browser.setFilterStatus(BrowserElementType.DEADLINE, ElementTypeBrowserFilter.OPTIONAL_INITIALLY_HIDDEN);
				browser.setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
				browser.setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
				browser.setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
			}

			@Override
			public FlexoModelObject getDefaultRootObject() {
				return process;
			}

			@Override
			public BrowserElementFactory getBrowserElementFactory() {
				return null; // Use default factory
			}
		};

		TabularBrowserModel returned = new TabularBrowserModel(configuration, " ", 150);
		// returned.setRowHeight(100);
		return returned;
	}

	public ProjectBrowser makeBrowser(FlexoModelObject rootObject, FlexoController controller) {
		ProcessBrowser returned = new ProcessBrowser((WKFController) controller);
		returned.setCurrentProcess((FlexoProcess) rootObject);
		return returned;
	}

	public boolean hasDescription(FlexoModelObject object) {
		if (object instanceof AbstractNode) {
			return true;
		} else if (object instanceof FlexoProcess) {
			return true;
		}
		return false;
	}

	public String getDescription(FlexoModelObject object) {
		if (object instanceof AbstractNode) {
			return ((AbstractNode) object).getDescription();
		} else if (object instanceof FlexoProcess) {
			return ((FlexoProcess) object).getDescription();
		}
		return null;
	}

	public void setDescription(FlexoModelObject object, String aDescription) {
		if (object instanceof AbstractNode) {
			((AbstractNode) object).setDescription(aDescription);
		} else if (object instanceof FlexoProcess) {
			((FlexoProcess) object).setDescription(aDescription);
		}
	}

	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		getTabularBrowserView().fireObjectSelected(object);
	}

	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
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
