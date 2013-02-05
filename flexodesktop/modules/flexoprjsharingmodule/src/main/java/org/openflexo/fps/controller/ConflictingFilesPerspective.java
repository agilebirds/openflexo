/**
 * 
 */
package org.openflexo.fps.controller;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.icon.FPSIconLibrary;

public class ConflictingFilesPerspective extends FPSPerspective {

	/**
	 * 
	 */
	private final FPSController fpsController;

	public ConflictingFilesPerspective(FPSController fpsController) {
		super(fpsController, "conflicting_files");
		this.fpsController = fpsController;
		setTopLeftView(fpsController.getCvsRepositoryBrowserView());
		setBottomLeftView(fpsController.getSharedProjectBrowserView());
		setBottomCenterView(fpsController.getConsoleView());

	}

	@Override
	public ImageIcon getActiveIcon() {
		return FPSIconLibrary.FPS_CFP_ACTIVE_ICON;
	}

	@Override
	public JPanel getFooter() {
		return this.fpsController._footer;
	}

	@Override
	public void setFilters() {
		this.fpsController.getSharedProjectBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getLocallyModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getRemotelyModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getConflictingFileFilter().setStatus(BrowserFilterStatus.SHOW);
		this.fpsController.getSharedProjectBrowser().getIgnoredFileFilter().setStatus(BrowserFilterStatus.HIDE);
	}

}