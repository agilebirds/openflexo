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
package org.openflexo.sgmodule.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.browser.SGBrowser;

/**
 * @author sylvain
 * 
 */
public class SGBrowserView extends BrowserView {

	/**
	 * @param browser
	 * @param kl
	 */
	public SGBrowserView(SGController controller, ProjectBrowser browser) {
		super(browser, controller);
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

		addHeaderComponent(interestingFilesViewModeButton);
		addHeaderComponent(generationModifiedViewModeButton);
		addHeaderComponent(diskModifiedViewModeButton);
		addHeaderComponent(conflictingFilesViewModeButton);
		addHeaderComponent(needsReinjectingViewModeButton);
		addHeaderComponent(generationErrorViewModeButton);

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

		addHeaderComponent(seeAllLabels);
		setMinimumSize(new Dimension(SGCst.MINIMUM_BROWSER_VIEW_WIDTH, SGCst.MINIMUM_BROWSER_VIEW_HEIGHT));
	}

	@Override
	public SGBrowser getBrowser() {
		return (SGBrowser) super.getBrowser();
	}

	@Override
	public SGController getController() {
		return (SGController) super.getController();
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
			getController().setCurrentEditedObjectAsModuleView(object);
		} else if (object instanceof CGFile) {

		}
		getController().refreshFooter();
	}

	/**
	 * Overrides treeDoubleClick
	 * 
	 * @see org.openflexo.components.browser.view.BrowserView#treeDoubleClick(org.openflexo.foundation.FlexoModelObject)
	 */
	@Override
	public void treeDoubleClick(FlexoModelObject object) {
		getController().setCurrentEditedObjectAsModuleView(object);
	}

}
