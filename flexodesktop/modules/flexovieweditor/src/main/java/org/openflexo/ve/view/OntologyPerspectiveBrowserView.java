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
package org.openflexo.ve.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser.OEViewMode;
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.components.browser.view.BrowserViewCellRenderer;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.ve.controller.OntologyBrowser;
import org.openflexo.ve.controller.OntologyLibraryBrowser;
import org.openflexo.ve.controller.VEBrowser;
import org.openflexo.ve.controller.VEController;

public class OntologyPerspectiveBrowserView extends JPanel {

	private OntologyLibraryBrowser mainBrowser;
	private OntologyBrowser ontologyBrowser;
	private OntologyLibraryBrowserView mainBrowserView;
	private VEBrowserView ontologyBrowserView;
	private JSplitPane splitPane;
	private int dividerLocation;

	public OntologyPerspectiveBrowserView(final VEController controller) {
		super(new BorderLayout());
		mainBrowser = new OntologyLibraryBrowser(controller);
		mainBrowserView = new OntologyLibraryBrowserView(mainBrowser, controller, SelectionPolicy.ParticipateToSelection);
		ontologyBrowser = new OntologyBrowser(controller);
		ontologyBrowserView = new VEBrowserView(ontologyBrowser, controller, SelectionPolicy.ForceSelection);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainBrowserView, ontologyBrowserView);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dividerLocation = splitPane.getDividerLocation();
				mainBrowserView.treeDoubleClick(controller.getProject().getProjectOntology());
				// controller.getSelectionManager().setSelectedObject(controller.getProject().getProjectOntology());
				// focusOnOntology(controller.getProject().getProjectOntology());
			}
		});
	}

	public void focusOnOntology(FlexoOntology ontology) {
		ontologyBrowser.deleteBrowserListener(mainBrowserView);
		ontologyBrowser.setRepresentedOntology(ontology);
		ontologyBrowser.update();
		ontologyBrowser.addBrowserListener(mainBrowserView);
	}

	public class OntologyLibraryBrowserView extends VEBrowserView {

		public OntologyLibraryBrowserView(VEBrowser browser, final VEController controller, SelectionPolicy selectionPolicy) {
			super(browser, controller, selectionPolicy);

			treeView.setCellRenderer(new BrowserViewCellRenderer() {
				/**
				 * Overrides getTreeCellRendererComponent
				 * 
				 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean,
				 *      boolean, boolean, int, boolean)
				 */
				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
						int row, boolean hasFocus) {
					sel |= getController().getCurrentModuleView() != null
							&& ((BrowserElement) value).getObject() == getController().getCurrentModuleView().getRepresentedObject();
					return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				}
			});

			// logger.info("hGap="+flowLayout.getHgap()+" vGap="+flowLayout.getVgap());

			ViewModeButton noHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.NO_HIERARCHY_MODE_ICON, "no_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					ontologyBrowserView.setVisible(true);
					splitPane.setDividerLocation(dividerLocation);
					mainBrowser.switchToNoHierarchyMode(controller.getProject());
					ontologyBrowser.setOEViewMode(OEViewMode.NoHierarchy);
					// elementTypeFilterChanged();
				}

				@Override
				public void setFilters() {
					// TODO Auto-generated method stub

				}
			};
			ViewModeButton partialHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.PARTIAL_HIERARCHY_MODE_ICON,
					"partial_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					ontologyBrowserView.setVisible(true);
					splitPane.setDividerLocation(dividerLocation);
					mainBrowser.switchToPartialHierarchyMode(controller.getProject());
					ontologyBrowser.setOEViewMode(OEViewMode.PartialHierarchy);
					// elementTypeFilterChanged();
				}

				@Override
				public void setFilters() {
					// TODO Auto-generated method stub

				}
			};
			ViewModeButton fullHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.FULL_HIERARCHY_MODE_ICON, "full_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					dividerLocation = splitPane.getDividerLocation();
					ontologyBrowserView.setVisible(false);
					mainBrowser.switchToFullHierarchyMode(controller.getProject());
					ontologyBrowser.setOEViewMode(OEViewMode.FullHierarchy);
					// elementTypeFilterChanged();
				}

				@Override
				public void setFilters() {
					// TODO Auto-generated method stub

				}
			};

			addHeaderComponent(noHierarchyViewModeButton);
			addHeaderComponent(partialHierarchyViewModeButton);
			addHeaderComponent(fullHierarchyViewModeButton);

		}

		@Override
		public void treeDoubleClick(FlexoModelObject object) {
			super.treeDoubleClick(object);
			if (object instanceof FlexoOntology) {
				focusOnOntology((FlexoOntology) object);
			}
		}

	}
}
