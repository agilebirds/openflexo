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
package org.openflexo.vpm.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.vpm.controller.CEDController;
import org.openflexo.vpm.controller.OntologyBrowser;
import org.openflexo.vpm.controller.OntologyLibraryBrowser;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser.OEViewMode;
import org.openflexo.components.browser.view.BrowserViewCellRenderer;
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.FlexoOntology;

public class OntologyPerspectiveBrowserView extends JPanel {

	private OntologyLibraryBrowser mainBrowser;
	private OntologyBrowser ontologyBrowser;
	private OntologyLibraryBrowserView mainBrowserView;
	private CEDBrowserView ontologyBrowserView;
	private JSplitPane splitPane;
	private int dividerLocation;

	public OntologyPerspectiveBrowserView(final CEDController controller) {
		super(new BorderLayout());
		mainBrowser = new OntologyLibraryBrowser(controller);
		mainBrowserView = new OntologyLibraryBrowserView(mainBrowser, controller, SelectionPolicy.ParticipateToSelection);
		ontologyBrowser = new OntologyBrowser(controller);
		ontologyBrowserView = new CEDBrowserView(ontologyBrowser, controller, SelectionPolicy.ForceSelection);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainBrowserView, ontologyBrowserView);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dividerLocation = splitPane.getDividerLocation();
				mainBrowserView.treeDoubleClick(controller.getBaseOntologyLibrary());
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

	public class OntologyLibraryBrowserView extends CEDBrowserView {

		public OntologyLibraryBrowserView(OntologyLibraryBrowser browser, final CEDController controller, SelectionPolicy selectionPolicy) {
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
					super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

					if (_controller.getCurrentModuleView() != null
							&& (((BrowserElement) value).getObject() == _controller.getCurrentModuleView().getRepresentedObject())) {
						setBackground(getBackgroundSelectionColor());
						setForeground(getTextSelectionColor());
						selected = true;
					}
					return this;
				}
			});

			FlowLayout flowLayout = new FlowLayout();
			JPanel viewModePanels = new JPanel(flowLayout);
			viewModePanels.setBorder(BorderFactory.createEmptyBorder());
			flowLayout.setHgap(2);
			// logger.info("hGap="+flowLayout.getHgap()+" vGap="+flowLayout.getVgap());

			ViewModeButton noHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.NO_HIERARCHY_MODE_ICON, "no_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					ontologyBrowserView.setVisible(true);
					splitPane.setDividerLocation(dividerLocation);
					mainBrowser.switchToNoHierarchyMode(controller.getBaseOntologyLibrary());
					ontologyBrowser.setOEViewMode(OEViewMode.NoHierarchy);
				}
			};
			ViewModeButton partialHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.PARTIAL_HIERARCHY_MODE_ICON,
					"partial_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					ontologyBrowserView.setVisible(true);
					splitPane.setDividerLocation(dividerLocation);
					mainBrowser.switchToPartialHierarchyMode(controller.getBaseOntologyLibrary());
					ontologyBrowser.setOEViewMode(OEViewMode.PartialHierarchy);
				}
			};
			ViewModeButton fullHierarchyViewModeButton = new ViewModeButton(VPMIconLibrary.FULL_HIERARCHY_MODE_ICON, "full_hierarchy_mode") {
				@Override
				public void actionPerformed(ActionEvent e) {
					dividerLocation = splitPane.getDividerLocation();
					ontologyBrowserView.setVisible(false);
					mainBrowser.switchToFullHierarchyMode(controller.getBaseOntologyLibrary());
					ontologyBrowser.setOEViewMode(OEViewMode.FullHierarchy);
				}
			};

			viewModePanels.add(noHierarchyViewModeButton);
			viewModePanels.add(partialHierarchyViewModeButton);
			viewModePanels.add(fullHierarchyViewModeButton);

			add(viewModePanels, BorderLayout.NORTH);
		}

		protected abstract class ViewModeButton extends JButton implements MouseListener, ActionListener {
			protected ViewModeButton(ImageIcon icon, String unlocalizedDescription) {
				super(icon);
				setToolTipText(FlexoLocalization.localizedForKey(unlocalizedDescription));
				setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				addMouseListener(this);
				addActionListener(this);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createEtchedBorder());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

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
