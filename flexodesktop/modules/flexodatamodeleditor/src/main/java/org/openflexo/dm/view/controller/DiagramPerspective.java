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
package org.openflexo.dm.view.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.dm.view.DMBrowserView;
import org.openflexo.dm.view.erdiagram.DiagramView;
import org.openflexo.dm.view.erdiagram.ERDiagramController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class DiagramPerspective extends DMPerspective {

	private final DMController controller;

	private final DMBrowser browser;
	private final DiagramBrowser diagramBrowser;
	private final DMBrowserView browserView;
	private final DMBrowserView diagramBrowserView;

	private final JLabel infoLabel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public DiagramPerspective(DMController controller) {
		super("er_diagram_perspective", controller);
		this.controller = controller;
		browser = new DMBrowser(controller);
		browser.setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
		browser.setFilterStatus(BrowserElementType.DM_EOENTITY, BrowserFilterStatus.HIDE);
		browser.setDMViewMode(DMViewMode.Diagrams);
		browserView = new DMBrowserView(browser, controller) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ERDiagram) {
					focusOnDiagram((ERDiagram) object);
				}
			}

		};
		diagramBrowser = new DiagramBrowser(controller);
		diagramBrowserView = new DMBrowserView(diagramBrowser, controller);
		setTopLeftView(browserView);
		setBottomLeftView(diagramBrowserView);
		infoLabel = new JLabel("ALT-drag to define inheritance, CTRL-drag to define properties");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	public void focusOnDiagram(ERDiagram diagram) {
		diagramBrowser.deleteBrowserListener(browserView);
		diagramBrowser.setRepresentedDiagram(diagram);
		diagramBrowser.addBrowserListener(browserView);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DMEIconLibrary.DME_DP_ACTIVE_ICON;
	}

	@Override
	public ERDiagram getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof ERDiagram) {
			return (ERDiagram) proposedObject;
		}
		if (proposedObject instanceof DMObject) {
			if (((DMObject) proposedObject).getDMModel().getDiagrams().size() > 0) {
				return ((DMObject) proposedObject).getDMModel().getDiagrams().firstElement();
			}
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof ERDiagram;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject diagram, FlexoController controller) {
		if (diagram instanceof ERDiagram) {
			return new ERDiagramController((DMController) controller, (ERDiagram) diagram).getDrawingView();
		} else {
			return null;
		}
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentDiagramView() != null) {
			return getCurrentDiagramView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	public DiagramView getCurrentDiagramView() {
		if (controller != null && controller.getCurrentModuleView() instanceof DiagramView) {
			return (DiagramView) controller.getCurrentModuleView();
		}
		return null;
	}

	protected class DiagramBrowser extends DMBrowser {
		private ERDiagram representedDiagram = null;

		protected DiagramBrowser(DMController controller) {
			super(controller);
		}

		protected ERDiagram getRepresentedDiagram() {
			return representedDiagram;
		}

		protected void setRepresentedDiagram(ERDiagram representedDiagram) {
			this.representedDiagram = representedDiagram;
			setRootObject(representedDiagram);
		}

	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		super.notifyModuleViewDisplayed(moduleView);
		if (moduleView instanceof DiagramView) {
			DiagramView diagram = (DiagramView) moduleView;
			browser.setRootObject(diagram.getRepresentedObject());
		}
	}

	@Override
	protected boolean browserMayRepresent(DMEntity entity) {
		return false;
	}

	@Override
	protected void changeBrowserFiltersFor(DMEntity entity) {

	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		if (project != null) {
			browser.setRootObject(project.getDataModel());
		} else {
			browser.setRootObject(null);
		}
		if (project != null) {
			if (project.getDataModel().getJDKRepository().hasDiagrams()) {
				browser.setFilterStatus(BrowserElementType.JDK_REPOSITORY, BrowserFilterStatus.SHOW);
			}
			if (project.getDataModel().getWORepository().hasDiagrams()) {
				browser.setFilterStatus(BrowserElementType.WO_REPOSITORY, BrowserFilterStatus.SHOW);
			}
			for (ExternalRepository rep : project.getDataModel().getExternalRepositories()) {
				if (rep.hasDiagrams()) {
					browser.setFilterStatus(BrowserElementType.EXTERNAL_REPOSITORY, BrowserFilterStatus.SHOW);
				}
			}
			if (project.getDataModel().getEOPrototypeRepository().hasDiagrams()) {
				browser.setFilterStatus(BrowserElementType.DM_EOPROTOTYPES_REPOSITORY, BrowserFilterStatus.SHOW);
			}
			if (project.getDataModel().getExecutionModelRepository().hasDiagrams()) {
				browser.setFilterStatus(BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY, BrowserFilterStatus.SHOW);
			}
		}
	}

}