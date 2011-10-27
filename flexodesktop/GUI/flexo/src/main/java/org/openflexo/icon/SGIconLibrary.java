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
 * Utility class containing all icons used in context of SGModule
 * 
 * @author sylvain
 *
 */
public class SGIconLibrary extends GeneratorIconLibrary {

	// Module icons
	public static final ImageIcon SG_SMALL_ICON = new ImageIconResource("Icons/SG/SG_A_Small.gif");
	public static final ImageIcon SG_MEDIUM_ICON = new ImageIconResource("Icons/SG/module-sg.png");
	public static final ImageIcon SG_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/SG/module-sg-hover.png");
	public static final ImageIcon SG_BIG_ICON = new ImageIconResource("Icons/SG/module-sg-big.png");

	// Perspective icons
	public static final ImageIcon SG_MRP_ACTIVE_ICON = new ImageIconResource("Icons/SG/MRPerspective_A.gif");
	public static final ImageIcon SG_MRP_SELECTED_ICON = new ImageIconResource("Icons/SG/MRPerspective_S.gif");
	public static final ImageIcon SG_SGP_ACTIVE_ICON = new ImageIconResource("Icons/SG/SGPerspective_A.gif");
	public static final ImageIcon SG_SGP_SELECTED_ICON = new ImageIconResource("Icons/SG/SGPerspective_S.gif");
	public static final ImageIcon SG_VP_ACTIVE_ICON = new ImageIconResource("Icons/SG/VersionningPerspective_A.gif");
	public static final ImageIcon SG_VP_SELECTED_ICON = new ImageIconResource("Icons/SG/VersionningPerspective_S.gif");
	
	// Model icons
	public static final ImageIcon GENERATED_CODE_ICON = new ImageIconResource("Icons/SG/GeneratedCode.gif");
	public static final ImageIcon GENERATED_CODE_REPOSITORY_ICON = new ImageIconResource("Icons/SG/GeneratedCodeRepository.gif");
	public static final ImageIcon SYMBOLIC_FOLDER_ICON = new ImageIconResource("Icons/SG/prj_obj.gif");
	public static final ImageIcon JAVA_SOURCE_FOLDER_ICON = new ImageIconResource("Icons/SG/JavaSourceFolder.gif");
	public static final ImageIcon TARGET_ICON = new ImageIconResource("Icons/SG/Target.gif");
	public static final ImageIcon TARGET_FOLDER_ICON = new ImageIconResource("Icons/SG/TargetFolder.gif");


}
