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
 * Utility class containing all icons used in context of WSModule
 * 
 * @author sylvain
 *
 */
public class WSEIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon WS_ACTIVE_ICON = new ImageIconResource("Icons/WSE/WS_A_Small.gif");
	public static final ImageIcon WS_UNACTIVE_ICON = new ImageIconResource("Icons/WSE/WS_NA_Small.gif");
	public static final ImageIcon WS_SELECTED_ICON = new ImageIconResource("Icons/WSE/WS_S_Small.gif");
	public static final ImageIcon WS_BIG_ACTIVE_ICON = new ImageIconResource("Icons/WSE/WS_A.gif");
	public static final ImageIcon WS_BIG_UNACTIVE_ICON = new ImageIconResource("Icons/WSE/WS_NA.gif");
	public static final ImageIcon WS_BIG_SELECTED_ICON = new ImageIconResource("Icons/WSE/WS_S.gif");

	// Perspective icons
	public static final ImageIcon WSE_WSEP_ACTIVE_ICON = new ImageIconResource("Icons/WSE/WSPerspective_A.gif");
	public static final ImageIcon WSE_WSEP_SELECTED_ICON = new ImageIconResource("Icons/WSE/WSPerspective_S.gif");

	// Model icons
	public static final ImageIcon WS_LIBRARY_ICON = new ImageIconResource("Icons/Model/WS/Library_WS.gif");
	public static final ImageIcon WS_INTERNAL_FOLDER_ICON = new ImageIconResource("Icons/Model/WS/Folder_WS.gif");
	public static final ImageIcon WS_EXTERNAL_FOLDER_ICON = new ImageIconResource("Icons/Model/WS/Folder_WS.gif");
	public static final ImageIcon INTERNAL_WS_SERVICE_ICON = new ImageIconResource("Icons/Model/WS/smallWSIcon.gif");
	public static final ImageIcon EXTERNAL_WS_SERVICE_ICON = new ImageIconResource("Icons/Model/WS/smallWSIcon.gif");
	public static final ImageIcon WS_PORTTYPE_ICON = new ImageIconResource("Icons/Model/WS/SmallProcess.gif");
	public static final ImageIcon WS_REPOSITORY_ICON = new ImageIconResource("Icons/Model/WS/smallWSRepository.gif");
	public static final ImageIcon WS_PORTTYPE_FOLDER_ICON = new ImageIconResource("Icons/Model/WS/smallProcessFolder.gif");
	public static final ImageIcon WS_REPOSITORY_FOLDER_ICON = new ImageIconResource("Icons/Model/WS/Folder_WS.gif");
	public static final ImageIcon WS_OUT_MESSAGE_LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallMessageIn.gif");
	public static final ImageIcon WS_IN_MESSAGE_LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallMessageOut.gif");
	public static final ImageIcon WS_FAULT_MESSAGE__LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallFaultThrower.gif");
	public static final ImageIcon WS_IN_OUT_OPERATION_LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallPortInOutLeft.gif");
	public static final ImageIcon WS_IN_OPERATION_LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallPortInLeft.gif");
	public static final ImageIcon WS_OUT_OPERATION_LEFT_ICON = new ImageIconResource("Icons/Model/WS/SmallPortOutLeft.gif");

}
