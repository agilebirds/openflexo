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
 * Utility class containing all icons used in context of DGModule
 * 
 * @author sylvain
 *
 */
public class DGIconLibrary extends GeneratorIconLibrary {

	// Module icons
	public static final ImageIcon DG_ACTIVE_ICON = new ImageIconResource("Icons/DG/DG_A_Small.gif");
	public static final ImageIcon DG_UNACTIVE_ICON = new ImageIconResource("Icons/DG/DG_NA_Small.gif");
	public static final ImageIcon DG_SELECTED_ICON = new ImageIconResource("Icons/DG/DG_S_Small.gif");
	public static final ImageIcon DG_BIG_ACTIVE_ICON = new ImageIconResource("Icons/DG/DG_A.gif");
	public static final ImageIcon DG_BIG_UNACTIVE_ICON = new ImageIconResource("Icons/DG/DG_NA.gif");
	public static final ImageIcon DG_BIG_SELECTED_ICON = new ImageIconResource("Icons/DG/DG_S.gif");

	// Perspective icons
	public static final ImageIcon DG_VP_ACTIVE_ICON = new ImageIconResource("Icons/DG/VersionningPerspective_A.gif");
	public static final ImageIcon DG_VP_SELECTED_ICON = new ImageIconResource("Icons/DG/VersionningPerspective_S.gif");
	public static final ImageIcon DG_DGP_SELECTED_ICON = new ImageIconResource("Icons/DG/DGPerspective_S.gif");
	public static final ImageIcon DG_DGP_ACTIVE_ICON = new ImageIconResource("Icons/DG/DGPerspective_A.gif");


	public static final ImageIcon GENERATED_DOC_ICON = new ImageIconResource("Icons/DG/GeneratedDoc.gif");
	public static final ImageIcon GENERATE_PDF = new ImageIconResource("Icons/DG/GeneratePDF.gif");
	public static final ImageIcon GENERATE_DOCX = new ImageIconResource("Icons/DG/GenerateDOCX.gif");
	public static final ImageIcon REINJECT_DOCX = new ImageIconResource("Icons/DG/ReinjectDOCX.gif");
	public static final ImageIcon GENERATE_ZIP = new ImageIconResource("Icons/DG/GenerateZIP.gif");

	public static final ImageIcon GENERATE_DOC_BUTTON = new ImageIconResource("Icons/DG/GenerateDocButton.gif");
	public static final ImageIcon GENERATE_DOC_AND_WRITE_BUTTON = new ImageIconResource("Icons/DG/GenerateDocAndWriteButton.gif");

	public static final ImageIcon SYMBOLIC_FOLDER_ICON = new ImageIconResource("Icons/DG/prj_obj.gif");
	public static final ImageIcon TARGET_FOLDER_ICON = new ImageIconResource("Icons/DG/TargetFolder.gif");

    
}
