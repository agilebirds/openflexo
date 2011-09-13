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
 * Utility class containing all icons used in context of VPMModule
 * 
 * @author sylvain
 *
 */
public class VPMIconLibrary extends IconLibrary {

	// Module icons
	public static final ImageIcon CED_ACTIVE_ICON = new ImageIconResource("Icons/VPM/CED_A_Small.gif");
	public static final ImageIcon CED_UNACTIVE_ICON = new ImageIconResource("Icons/VPM/CED_NA_Small.gif");
	public static final ImageIcon CED_SELECTED_ICON = new ImageIconResource("Icons/VPM/CED_S_Small.gif");
	public static final ImageIcon CED_BIG_ACTIVE_ICON = new ImageIconResource("Icons/VPM/CED_A.gif");
	public static final ImageIcon CED_BIG_UNACTIVE_ICON = new ImageIconResource("Icons/VPM/CED_NA.gif");
	public static final ImageIcon CED_BIG_SELECTED_ICON = new ImageIconResource("Icons/VPM/CED_S.gif");

	// Perspective icons
	public static final ImageIcon VPM_ACTIVE_ICON = new ImageIconResource("Icons/VPM/CalcPerspective_A.gif");
	public static final ImageIcon VPM_SELECTED_ICON = new ImageIconResource("Icons/VPM/CalcPerspective_S.gif");
	public static final ImageIcon VPM_OP_ACTIVE_ICON = new ImageIconResource("Icons/VPM/OntologyPerspective_A.gif");
	public static final ImageIcon VPM_OP_SELECTED_ICON = new ImageIconResource("Icons/VPM/OntologyPerspective_S.gif");
	
	// Editor icons
	public static final ImageIcon NO_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/NoHierarchyViewMode.gif");
	public static final ImageIcon PARTIAL_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/PartialHierarchyViewMode.gif");
	public static final ImageIcon FULL_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/FullHierarchyViewMode.gif");

	// Model icons
	public static final ImageIconResource CALC_LIBRARY_ICON = new ImageIconResource("Icons/Model/VPM/CalcLibraryIcon.gif");
	public static final ImageIconResource CALC_ICON = new ImageIconResource("Icons/Model/VPM/CalcIcon.gif");
	public static final ImageIconResource CALC_PALETTE_ICON = new ImageIconResource("Icons/Model/VPM/CalcPaletteIcon.gif");
	public static final ImageIconResource EDITION_PATTERN_ICON = new ImageIconResource("Icons/Model/VPM/EditionPatternIcon.gif");
	public static final ImageIconResource ACTION_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/ActionSchemeIcon.gif");
	public static final ImageIconResource DROP_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/DropSchemeIcon.gif");
	public static final ImageIconResource LINK_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/LinkSchemeIcon.gif");
	public static final ImageIconResource EDITION_PATTERN_PARAMETER_ICON = new ImageIconResource("Icons/Model/VPM/ParameterIcon.gif");
	public static final ImageIconResource EDITION_PATTERN_ACTION_ICON = new ImageIconResource("Icons/Model/VPM/ActionIcon.gif");
	public static final ImageIconResource LOCALIZATION_ICON = new ImageIconResource("Icons/Model/VPM/LocalizationIcon.gif");
	public static final ImageIconResource UNKNOWN_ICON = new ImageIconResource("Icons/Model/VPM/UnknownIcon.gif");
	public static final ImageIconResource EXAMPLE_DIAGRAM_ICON = new ImageIconResource("Icons/Model/VPM/ExampleDiagram.gif");
	public static final ImageIconResource CALC_SHAPE_ICON = new ImageIconResource("Icons/Model/VPM/ShapeIcon.gif");
	public static final ImageIconResource CALC_CONNECTOR_ICON = new ImageIconResource("Icons/Model/VPM/ConnectorIcon.gif");

}
