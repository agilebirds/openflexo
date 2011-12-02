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
package org.openflexo.cgmodule.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.cgmodule.GeneratorCst;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.controller.browser.GeneratorBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class GeneratorBrowserView extends BrowserView {

	private GeneratorController controller;

	/**
	 * @param browser
	 * @param kl
	 */
	public GeneratorBrowserView(GeneratorController controller, ProjectBrowser browser) {
		super(browser, controller.getKeyEventListener(), controller.getEditor());
		this.controller = controller;

		FlowLayout flowLayout = new FlowLayout();
		JPanel viewModePanels = new JPanel(flowLayout);
		viewModePanels.setBorder(BorderFactory.createEmptyBorder());
		flowLayout.setHgap(2);
		// logger.info("hGap="+flowLayout.getHgap()+" vGap="+flowLayout.getVgap());

		ViewModeButton interestingFilesViewModeButton = new ViewModeButton(GeneratorIconLibrary.INTERESTING_FILES_VIEW_MODE_ICON,
				"interesting_files_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.SHOW);
			}
		};
		ViewModeButton generationModifiedViewModeButton = new ViewModeButton(GeneratorIconLibrary.GENERATION_MODIFIED_VIEW_MODE_ICON,
				"generation_modified_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.HIDE);
			}
		};
		ViewModeButton diskModifiedViewModeButton = new ViewModeButton(GeneratorIconLibrary.DISK_MODIFIED_VIEW_MODE_ICON,
				"disk_modified_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.HIDE);
			}
		};
		ViewModeButton conflictingFilesViewModeButton = new ViewModeButton(GeneratorIconLibrary.CONFLICTING_FILES_VIEW_MODE_ICON,
				"conflicting_files_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.HIDE);
			}
		};
		ViewModeButton needsReinjectingViewModeButton = new ViewModeButton(GeneratorIconLibrary.NEED_REINJECTING_VIEW_MODE_ICON,
				"needs_model_reinjection_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.HIDE);
			}
		};
		ViewModeButton generationErrorViewModeButton = new ViewModeButton(GeneratorIconLibrary.GENERATION_ERROR_VIEW_MODE_ICON,
				"generation_errors_mode") {
			@Override
			public void setFilters() {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.HIDE);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.HIDE);
			}
		};

		viewModePanels.add(interestingFilesViewModeButton);
		viewModePanels.add(generationModifiedViewModeButton);
		viewModePanels.add(diskModifiedViewModeButton);
		viewModePanels.add(conflictingFilesViewModeButton);
		viewModePanels.add(needsReinjectingViewModeButton);
		viewModePanels.add(generationErrorViewModeButton);

		String htmlText = "<html><u>" + FlexoLocalization.localizedForKey("all_files") + "</u>" + "</html>";
		final JLabel seeAllLabels = new JLabel(htmlText, SwingConstants.RIGHT);
		seeAllLabels.setVerticalAlignment(SwingConstants.BOTTOM);
		seeAllLabels.setForeground(Color.BLUE);
		seeAllLabels.setFont(FlexoCst.SMALL_FONT);
		seeAllLabels.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		seeAllLabels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				seeAllLabels.setForeground(Color.MAGENTA);
				seeAllLabels.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				seeAllLabels.setForeground(Color.BLUE);
				seeAllLabels.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				getBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsGenerationFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getGenerationErrorFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getGenerationModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getDiskModifiedFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getUnresolvedConflictsFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getNeedsReinjectingFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().getOtherFilesFilter().setStatus(BrowserFilterStatus.SHOW);
				getBrowser().update();
			}
		});

		JPanel viewModeSelector = new JPanel(new BorderLayout());
		viewModeSelector.add(viewModePanels, BorderLayout.WEST);
		viewModeSelector.add(seeAllLabels, BorderLayout.EAST);

		add(viewModeSelector, BorderLayout.NORTH);

		setMinimumSize(new Dimension(GeneratorCst.MINIMUM_BROWSER_VIEW_WIDTH, GeneratorCst.MINIMUM_BROWSER_VIEW_HEIGHT));
		setPreferredSize(new Dimension(GeneratorCst.PREFERRED_BROWSER_VIEW_WIDTH, GeneratorCst.PREFERRED_BROWSER_VIEW_HEIGHT));

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

		@Override
		public void actionPerformed(ActionEvent e) {
			setFilters();
			getBrowser().update();
		}

		public abstract void setFilters();
	}

	@Override
	public GeneratorBrowser getBrowser() {
		return (GeneratorBrowser) super.getBrowser();
	}

	/**
	 * Overrides treeSingleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeSingleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeSingleClick(FlexoModelObject object) {
		/*  if (object instanceof FlexoWorkflow || object instanceof FlexoProject || object instanceof DMEORepository || object instanceof DMEOModel || object instanceof DMEOEntity || object instanceof OperationNode)
		      controller.setCurrentEditedObjectAsModuleView(object); */
		if (object instanceof GenerationRepository) {
			controller.setCurrentEditedObjectAsModuleView(object);
		} else if (object instanceof CGFile) {
			/*
			CGFile file = (CGFile)object;
			logger.info("File "+file.getFileName()+" resource "+file.getResource()
					+" status="+file.getGenerationStatus()
					+" needsGeneration="+file.getResource().needsGeneration()
					+" lastGenerationDate="+file.getResource().getLastGenerationDate());
			String s1 = "";
			for (Enumeration<FlexoResource> en = file.getResource().getDependantResources().elements(false,FlexoResourceManager.getDependancyScheme());en.hasMoreElements();) {
				FlexoResource res = en.nextElement();
				s1 += " "+res+"/"+res.getLastUpdate();
			}
			logger.info("dependant resources: "+s1);
			String s2 = "";
			for (Enumeration<FlexoResource> en = file.getResource().getDependantResources().elements();en.hasMoreElements();) {
				FlexoResource res = en.nextElement();
				s2 += " "+res+"/"+res.getLastUpdate();
			}
			logger.info("dependant resources: "+s2);

			if (file.getResource() instanceof GenerationAvailableFileResource) {
				FlexoResourceGenerator generator = ((GenerationAvailableFileResource)file.getResource()).getGenerator();
				if (generator != null) {
					String templates = "";
					for (CGTemplateFile template : generator.getUsedTemplates()) {
						templates += " "+template.getFileName();
					}
					logger.info("used templates: "+templates);
					logger.info("Generator needs regeneration "+generator.needsGeneration());
				}
			}*/
		}
		controller.refreshFooter();
	}

	/**
	 * Overrides treeDoubleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeDoubleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		controller.setCurrentEditedObjectAsModuleView(object);
	}

}
