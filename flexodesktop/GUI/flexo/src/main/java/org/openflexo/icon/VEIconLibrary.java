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
 * Utility class containing all icons used in context of VEModule
 * 
 * @author sylvain
 *
 */
public class VEIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon OE_ACTIVE_ICON = new ImageIconResource("Icons/VE/OE_A_Small.gif");
	public static final ImageIcon OE_UNACTIVE_ICON = new ImageIconResource("Icons/VE/OE_NA_Small.gif");
	public static final ImageIcon OE_SELECTED_ICON = new ImageIconResource("Icons/VE/OE_S_Small.gif");
	public static final ImageIcon OE_BIG_ACTIVE_ICON = new ImageIconResource("Icons/VE/OE_A.gif");
	public static final ImageIcon OE_BIG_UNACTIVE_ICON = new ImageIconResource("Icons/VE/OE_NA.gif");
	public static final ImageIcon OE_BIG_SELECTED_ICON = new ImageIconResource("Icons/VE/OE_S.gif");

	// Perspective icons
	public static final ImageIcon VE_OP_ACTIVE_ICON = new ImageIconResource("Icons/VE/OntologyPerspective_A.gif");
	public static final ImageIcon VE_OP_SELECTED_ICON = new ImageIconResource("Icons/VE/OntologyPerspective_S.gif");
	public static final ImageIcon VE_SP_ACTIVE_ICON = new ImageIconResource("Icons/VE/ShemaPerspective_A.gif");
	public static final ImageIcon VE_SP_SELECTED_ICON = new ImageIconResource("Icons/VE/ShemaPerspective_S.gif");
	
	// Model icons
	public static final ImageIconResource OE_SHEMA_LIBRARY_ICON = new ImageIconResource("Icons/Model/VE/OEShemaLibrary.gif");
	public static final ImageIconResource OE_SHEMA_ICON = new ImageIconResource("Icons/Model/VE/OEShema.gif");
	public static final ImageIconResource OE_SHAPE_ICON = new ImageIconResource("Icons/Model/VE/OEShape.gif");
	public static final ImageIconResource OE_CONNECTOR_ICON = new ImageIconResource("Icons/Model/VE/OEConnector.gif");

}
