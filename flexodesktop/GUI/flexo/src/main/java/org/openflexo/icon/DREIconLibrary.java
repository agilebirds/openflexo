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
 * Utility class containing all icons used in context of DREModule
 * 
 * @author sylvain
 * 
 */
public class DREIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon DRE_SMALL_ICON = new ImageIconResource("Icons/DRE/DRC_A_Small.gif");
	public static final ImageIcon DRE_MEDIUM_ICON = new ImageIconResource("Icons/DRE/DRC_A.gif");
	public static final ImageIcon DRE_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/DRE/DRC_S.gif");
	public static final ImageIcon DRE_BIG_ICON = new ImageIconResource("Icons/DRE/DRC_A.gif");

	// Perspective icons
	public static final ImageIcon DRE_DRE_ACTIVE_ICON = new ImageIconResource("Icons/DRE/DREPerspective_A.png");
	public static final ImageIcon DRE_DRE_SELECTED_ICON = new ImageIconResource("Icons/DRE/DREPerspective_S.gif");

	// Editor icons
	public static final ImageIcon DOC_FOLDER_ICON = new ImageIconResource("Icons/DRE/Folder.gif");
	public static final ImageIcon DOC_ITEM_ICON = new ImageIconResource("Icons/DRE/DocItem.gif");
	public static final ImageIcon UNDOCUMENTED_DOC_ITEM_ICON = new ImageIconResource("Icons/DRE/UndocumentedDocItem.gif");
	public static final ImageIcon APPROVING_PENDING_DOC_ITEM_ICON = new ImageIconResource("Icons/DRE/ApprovingPendingDocItem.gif");
	public static final ImageIcon AVAILABLE_NEW_VERSION_PENDING_DOC_ITEM_ICON = new ImageIconResource(
			"Icons/DRE/AvailableNewVersionPendingDocItem.gif");

}
