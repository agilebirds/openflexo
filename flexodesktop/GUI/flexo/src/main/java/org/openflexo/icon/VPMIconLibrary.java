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

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.calc.ActionScheme;
import org.openflexo.foundation.ontology.calc.CalcDrawingConnector;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;
import org.openflexo.foundation.ontology.calc.CalcFolder;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.CalcLibraryObject;
import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.calc.ConnectorPatternRole;
import org.openflexo.foundation.ontology.calc.DataPropertyAssertion;
import org.openflexo.foundation.ontology.calc.DropScheme;
import org.openflexo.foundation.ontology.calc.EditionAction;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.foundation.ontology.calc.EditionPatternParameter;
import org.openflexo.foundation.ontology.calc.FlexoModelObjectPatternRole;
import org.openflexo.foundation.ontology.calc.LinkScheme;
import org.openflexo.foundation.ontology.calc.LocalizedDictionary;
import org.openflexo.foundation.ontology.calc.ObjectPropertyAssertion;
import org.openflexo.foundation.ontology.calc.OntologicObjectPatternRole;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.foundation.ontology.calc.PaletteElementPatternParameter;
import org.openflexo.foundation.ontology.calc.PrimitivePatternRole;
import org.openflexo.foundation.ontology.calc.ShapePatternRole;
import org.openflexo.foundation.ontology.calc.ShemaPatternRole;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ImageIconResource;

/**
 * Utility class containing all icons used in context of VPMModule
 * 
 * @author sylvain
 *
 */
public class VPMIconLibrary extends IconLibrary {

	private static final Logger logger = Logger.getLogger(VPMIconLibrary.class.getPackage().getName());

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

	public static ImageIcon iconForObject(CalcLibraryObject object)
	{
		if (object instanceof CalcFolder) return FOLDER_ICON;
		else if (object instanceof CalcLibrary) return CALC_LIBRARY_ICON;
		else if (object instanceof CalcPalette) return CALC_PALETTE_ICON;
		else if (object instanceof CalcPaletteElement) return CALC_SHAPE_ICON;
		else if (object instanceof DataPropertyAssertion) return OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
		else if (object instanceof ObjectPropertyAssertion) return OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
		else if (object instanceof CalcDrawingConnector) return CALC_CONNECTOR_ICON;
		else if (object instanceof CalcDrawingShape) return CALC_SHAPE_ICON;
		else if (object instanceof CalcDrawingShema) return EXAMPLE_DIAGRAM_ICON;
		else if (object instanceof EditionAction) return EDITION_PATTERN_ACTION_ICON;
		else if (object instanceof EditionPattern) return EDITION_PATTERN_ICON;
		else if (object instanceof EditionPatternParameter) return EDITION_PATTERN_PARAMETER_ICON;
		else if (object instanceof ActionScheme) return ACTION_SCHEME_ICON;
		else if (object instanceof DropScheme) return DROP_SCHEME_ICON;
		else if (object instanceof LinkScheme) return LINK_SCHEME_ICON;
		else if (object instanceof OntologyCalc) return CALC_ICON;
		else if (object instanceof PaletteElementPatternParameter) return EDITION_PATTERN_PARAMETER_ICON;
		else if (object instanceof FlexoModelObjectPatternRole) {
			switch (((FlexoModelObjectPatternRole) object).getFlexoModelObjectType()) {
			case Process:
				return WKFIconLibrary.PROCESS_ICON;
			case ProcessFolder:
				return WKFIconLibrary.PROCESS_FOLDER_ICON;
			case Role:
				return WKFIconLibrary.ROLE_ICON;
			case Activity:
				return WKFIconLibrary.ACTIVITY_NODE_ICON;
			case Operation:
				return WKFIconLibrary.OPERATION_NODE_ICON;
			case Action:
				return WKFIconLibrary.ACTION_NODE_ICON;
			case Event:
				return WKFIconLibrary.EVENT_ICON;
			default:
				return null;
			}
		}
		else if (object instanceof ConnectorPatternRole) return CALC_CONNECTOR_ICON;
		else if (object instanceof ShapePatternRole) return CALC_SHAPE_ICON;
		else if (object instanceof ShemaPatternRole) return EXAMPLE_DIAGRAM_ICON;
		else if (object instanceof PrimitivePatternRole) return UNKNOWN_ICON;
		else if (object instanceof OntologicObjectPatternRole) {
			switch (((OntologicObjectPatternRole) object).getOntologicObjectType()) {
			case Class:
				return OntologyIconLibrary.ONTOLOGY_CLASS_ICON;
			case Individual:
				return OntologyIconLibrary.ONTOLOGY_INDIVIDUAL_ICON;
			case ObjectProperty:
				return OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
			case DataProperty:
				return OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
			case OntologyStatement:
				return OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON;
			default:
				return null;
			}
		}
		else if (object instanceof LocalizedDictionary) return LOCALIZATION_ICON;
		logger.warning("No icon for "+object.getClass());
		return UNKNOWN_ICON;
	}

}
