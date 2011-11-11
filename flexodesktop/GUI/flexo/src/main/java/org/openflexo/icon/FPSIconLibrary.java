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
package org.openflexo.icon;

import javax.swing.ImageIcon;

import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of FPSModule
 * 
 * @author sylvain
 * 
 */
public class FPSIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon FPS_SMALL_ICON = new ImageIconResource("Icons/FPS/FPS_A_Small.gif");
	public static final ImageIcon FPS_MEDIUM_ICON = new ImageIconResource("Icons/FPS/module-fps.png");
	public static final ImageIcon FPS_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/FPS/module-fps-hover.png");
	public static final ImageIcon FPS_BIG_ICON = new ImageIconResource("Icons/FPS/module-fps-big.png");

	// Editor icons
	public static final ImageIcon FPS_MARK_AS_MERGED_ICON = new ImageIconResource("Icons/FPS/MarkAsMergedIcon.gif");
	public static final ImageIcon FPS_MARK_AS_MERGED_DISABLED_ICON = new ImageIconResource("Icons/FPS/MarkAsMergedIcon-disabled.gif");
	public static final ImageIcon FPS_UPDATE_ICON = new ImageIconResource("Icons/FPS/UpdateIcon.gif");
	public static final ImageIcon FPS_UPDATE_DISABLED_ICON = new ImageIconResource("Icons/FPS/UpdateIcon-disabled.gif");
	public static final ImageIcon FPS_COMMIT_ICON = new ImageIconResource("Icons/FPS/CommitIcon.gif");
	public static final ImageIcon FPS_COMMIT_DISABLED_ICON = new ImageIconResource("Icons/FPS/CommitIcon-disabled.gif");
	public static final ImageIcon RESOLVED_CONFLICT_ICON = new ImageIconResource("Icons/FPS/ResolvedConflict.gif");
	public static final ImageIcon FPS_AFP_ACTIVE_ICON = new ImageIconResource("Icons/FPS/AllFilesViewMode.gif");
	public static final ImageIcon FPS_AFP_SELECTED_ICON = new ImageIconResource("Icons/FPS/AllFilesViewMode_S.gif");
	public static final ImageIcon FPS_CFP_ACTIVE_ICON = new ImageIconResource("Icons/FPS/ConflictingViewMode.gif");
	public static final ImageIcon FPS_CFP_SELECTED_ICON = new ImageIconResource("Icons/FPS/ConflictingViewMode_S.gif");
	public static final ImageIcon FPS_IFP_ACTIVE_ICON = new ImageIconResource("Icons/FPS/InterestingFilesViewMode.gif");
	public static final ImageIcon FPS_IFP_SELECTED_ICON = new ImageIconResource("Icons/FPS/InterestingFilesViewMode_S.gif");
	public static final ImageIcon FPS_LMP_ACTIVE_ICON = new ImageIconResource("Icons/FPS/LocallyModifiedViewMode.gif");
	public static final ImageIcon FPS_LMP_SELECTED_ICON = new ImageIconResource("Icons/FPS/LocallyModifiedViewMode_S.gif");
	public static final ImageIcon FPS_RMP_ACTIVE_ICON = new ImageIconResource("Icons/FPS/RemotelyModifiedViewMode.gif");
	public static final ImageIcon FPS_RMP_SELECTED_ICON = new ImageIconResource("Icons/FPS/RemotelyModifiedViewMode_S.gif");

	// Model icons
	public static final ImageIcon CVS_REPOSITORY_LIST_ICON = new ImageIconResource("Icons/FPS/RepositoryList.gif");
	public static final ImageIcon CVS_REPOSITORY_ICON = new ImageIconResource("Icons/FPS/CVSRepository.gif");
	public static final ImageIcon CVS_MODULE_ICON = new ImageIconResource("Icons/FPS/CVSModule.gif");

}
