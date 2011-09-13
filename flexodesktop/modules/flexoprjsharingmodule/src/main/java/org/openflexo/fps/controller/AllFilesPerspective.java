/**
 * 
 */
package org.openflexo.fps.controller;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.icon.FPSIconLibrary;

public class AllFilesPerspective extends FPSPerspective
{

	/**
	 * 
	 */
	private final FPSController fpsController;
	
	public AllFilesPerspective(FPSController fpsController)
	{
		super(fpsController,"all_files");
		this.fpsController = fpsController;
	}

	@Override
	public ImageIcon getActiveIcon()
	{
		return FPSIconLibrary.FPS_AFP_ACTIVE_ICON;
	}

	@Override
	public ImageIcon getSelectedIcon()
	{
		return FPSIconLibrary.FPS_AFP_SELECTED_ICON;
	}

	@Override
	public JPanel getFooter()
	{
		return this.fpsController._footer;
	}

	@Override
	public void setFilters()
	{
		this.fpsController.getSharedProjectBrowser().getAllFilesAndDirectoryFilter().setStatus(BrowserFilterStatus.SHOW);
		this.fpsController.getSharedProjectBrowser().getUpToDateFilesFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getLocallyModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getRemotelyModifiedFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getConflictingFileFilter().setStatus(BrowserFilterStatus.HIDE);
		this.fpsController.getSharedProjectBrowser().getIgnoredFileFilter().setStatus(BrowserFilterStatus.HIDE);
	}

}