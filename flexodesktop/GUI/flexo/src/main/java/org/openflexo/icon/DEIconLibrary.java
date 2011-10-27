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
 * Utility class containing all icons used in context of DEModule
 * 
 * @author sylvain
 *
 */
public class DEIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon DE_SMALL_ICON = new ImageIconResource("Icons/DG/DE_A_Small.gif");
	public static final ImageIcon DE_MEDIUM_ICON = new ImageIconResource("Icons/DE/module-dg.png");
	public static final ImageIcon DE_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/DE/module-dg-hover.png");
	public static final ImageIcon DE_BIG_ICON = new ImageIconResource("Icons/DE/module-dg-big.png");

	// Perspective icons
	public static final ImageIcon DE_DE_ACTIVE_ICON = new ImageIconResource("Icons/DE/DocEditorPerspective_A.png");
	public static final ImageIcon DE_DE_SELECTED_ICON = new ImageIconResource("Icons/DE/DocEditorPerspective_S.png");
	
	// Model icons
    
	public static final ImageIcon TOC_ENTRY = new ImageIconResource("Icons/DE/TocEntry.gif");
	public static final ImageIcon TOC_REPOSITORY = new ImageIconResource("Icons/DE/TOCRepository.png");
	public static final ImageIcon TOC_ENTRY_BIG = new ImageIconResource("Icons/DE/TocEntryBig.png");
	
    
}
