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

import org.openflexo.foundation.viewpoint.AbstractCreationScheme;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.AddConnector;
import org.openflexo.foundation.viewpoint.AddDiagram;
import org.openflexo.foundation.viewpoint.AddEditionPattern;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.AddShape;
import org.openflexo.foundation.viewpoint.AddStatement;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.ConditionalAction;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternPatternRole;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.FlexoModelObjectPatternRole;
import org.openflexo.foundation.viewpoint.GraphicalAction;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IterationAction;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.foundation.viewpoint.LocalizedDictionary;
import org.openflexo.foundation.viewpoint.NavigationScheme;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.PaletteElementPatternParameter;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.PrimitivePatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.StatementPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointLibraryObject;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
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
	public static final ImageIcon VPM_SMALL_ICON = new ImageIconResource("Icons/VPM/module-vpm-16.png");
	public static final ImageIcon VPM_MEDIUM_ICON = new ImageIconResource("Icons/VPM/module-vpm-32.png");
	public static final ImageIcon VPM_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/VPM/module-vpm-hover-32.png");
	public static final ImageIcon VPM_BIG_ICON = new ImageIconResource("Icons/VPM/module-vpm-hover-64.png");

	// Perspective icons
	public static final ImageIcon VPM_VPE_ACTIVE_ICON = new ImageIconResource("Icons/VPM/viewpoint-perspective.png");
	public static final ImageIcon VPM_VPE_SELECTED_ICON = new ImageIconResource("Icons/VPM/viewpoint-perspective-hover.png");
	public static final ImageIcon VPM_OP_ACTIVE_ICON = new ImageIconResource("Icons/VPM/ontology-perspective.png");
	public static final ImageIcon VPM_OP_SELECTED_ICON = new ImageIconResource("Icons/VPM/ontology-perspective-hover.png");

	// Editor icons
	public static final ImageIcon NO_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/NoHierarchyViewMode.gif");
	public static final ImageIcon PARTIAL_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/PartialHierarchyViewMode.gif");
	public static final ImageIcon FULL_HIERARCHY_MODE_ICON = new ImageIconResource("Icons/VPM/FullHierarchyViewMode.gif");

	// Model icons
	public static final ImageIconResource CALC_LIBRARY_ICON = new ImageIconResource("Icons/Model/VPM/ViewPointLibrary.png");
	public static final ImageIconResource CALC_ICON = new ImageIconResource("Icons/Model/VPM/ViewPoint.png");
	public static final ImageIconResource CALC_PALETTE_ICON = new ImageIconResource("Icons/Model/VPM/ViewPointPalette.png");
	public static final ImageIconResource EDITION_PATTERN_ICON = new ImageIconResource("Icons/Model/VPM/EditionPattern.png");
	public static final ImageIconResource ACTION_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/ActionSchemeIcon.png");
	public static final ImageIconResource DROP_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/DropSchemeIcon.png");
	public static final ImageIconResource LINK_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/LinkSchemeIcon.png");
	public static final ImageIconResource CREATION_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/CreationSchemeIcon.png");
	public static final ImageIconResource DELETION_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/DeletionSchemeIcon.png");
	public static final ImageIconResource NAVIGATION_SCHEME_ICON = new ImageIconResource("Icons/Model/VPM/NavigationSchemeIcon.png");
	public static final ImageIconResource EDITION_PATTERN_PARAMETER_ICON = new ImageIconResource("Icons/Model/VPM/ParameterIcon.png");
	public static final ImageIconResource EDITION_PATTERN_ACTION_ICON = new ImageIconResource("Icons/Model/VPM/ActionIcon.png");
	public static final ImageIconResource LOCALIZATION_ICON = new ImageIconResource("Icons/Model/VPM/LocalizationIcon.png");
	public static final ImageIconResource UNKNOWN_ICON = new ImageIconResource("Icons/Model/VPM/UnknownIcon.gif");
	public static final ImageIconResource EXAMPLE_DIAGRAM_ICON = new ImageIconResource("Icons/Model/VPM/ExampleDiagram.png");
	public static final ImageIconResource CALC_SHAPE_ICON = new ImageIconResource("Icons/Model/VPM/ShapeIcon.png");
	public static final ImageIconResource CALC_CONNECTOR_ICON = new ImageIconResource("Icons/Model/VPM/ConnectorIcon.gif");
	public static final ImageIconResource DECLARE_PATTERN_ROLE_ICON = new ImageIconResource("Icons/Model/VPM/DeclarePatternRoleIcon.png");
	public static final ImageIconResource GRAPHICAL_ACTION_ICON = new ImageIconResource("Icons/Model/VPM/GraphicalActionIcon.png");
	public static final ImageIconResource CONDITIONAL_ACTION_ICON = new ImageIconResource("Icons/Model/VPM/ConditionalActionIcon.png");
	public static final ImageIconResource ITERATION_ACTION_ICON = new ImageIconResource("Icons/Model/VPM/IterationActionIcon.png");

	public static ImageIcon iconForObject(ViewPointLibraryObject object) {
		if (object instanceof ViewPointFolder) {
			return FOLDER_ICON;
		} else if (object instanceof ViewPointLibrary) {
			return CALC_LIBRARY_ICON;
		} else if (object instanceof ViewPointPalette) {
			return CALC_PALETTE_ICON;
		} else if (object instanceof ViewPointPaletteElement) {
			return CALC_SHAPE_ICON;
		} else if (object instanceof DataPropertyAssertion) {
			return OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
		} else if (object instanceof ObjectPropertyAssertion) {
			return OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
		} else if (object instanceof ExampleDrawingConnector) {
			return CALC_CONNECTOR_ICON;
		} else if (object instanceof ExampleDrawingShape) {
			return CALC_SHAPE_ICON;
		} else if (object instanceof ExampleDrawingShema) {
			return EXAMPLE_DIAGRAM_ICON;
		} else if (object instanceof EditionAction) {
			if (object instanceof AddClass) {
				return OntologyIconLibrary.ONTOLOGY_CLASS_ICON;
			} else if (object instanceof AddIndividual) {
				return OntologyIconLibrary.ONTOLOGY_INDIVIDUAL_ICON;
			} else if (object instanceof AddDiagram) {
				return EXAMPLE_DIAGRAM_ICON;
			} else if (object instanceof AddEditionPattern) {
				return EDITION_PATTERN_ICON;
			} else if (object instanceof AddShape) {
				return CALC_SHAPE_ICON;
			} else if (object instanceof AddConnector) {
				return CALC_CONNECTOR_ICON;
			} else if (object instanceof AddStatement) {
				return OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON;
			} else if (object instanceof DeclarePatternRole) {
				return DECLARE_PATTERN_ROLE_ICON;
			} else if (object instanceof ConditionalAction) {
				return CONDITIONAL_ACTION_ICON;
			} else if (object instanceof IterationAction) {
				return ITERATION_ACTION_ICON;
			} else if (object instanceof DeleteAction) {
				PatternRole pr = ((DeleteAction) object).getPatternRole();
				if (pr != null) {
					ImageIcon baseIcon = iconForObject(pr);
					return IconFactory.getImageIcon(baseIcon, DELETE);
				}
				return DELETE_ICON;
			} else if (object instanceof GraphicalAction) {
				return GRAPHICAL_ACTION_ICON;
			}
			return EDITION_PATTERN_ACTION_ICON;
		} else if (object instanceof EditionPattern) {
			return EDITION_PATTERN_ICON;
		} else if (object instanceof EditionSchemeParameter) {
			return EDITION_PATTERN_PARAMETER_ICON;
		} else if (object instanceof ActionScheme) {
			return ACTION_SCHEME_ICON;
		} else if (object instanceof DropScheme) {
			return DROP_SCHEME_ICON;
		} else if (object instanceof LinkScheme) {
			return LINK_SCHEME_ICON;
		} else if (object instanceof AbstractCreationScheme) {
			return CREATION_SCHEME_ICON;
		} else if (object instanceof NavigationScheme) {
			return NAVIGATION_SCHEME_ICON;
		} else if (object instanceof DeletionScheme) {
			return DELETION_SCHEME_ICON;
		} else if (object instanceof ViewPoint) {
			return CALC_ICON;
		} else if (object instanceof PaletteElementPatternParameter) {
			return EDITION_PATTERN_PARAMETER_ICON;
		} else if (object instanceof FlexoModelObjectPatternRole) {
			if (((FlexoModelObjectPatternRole) object).getFlexoModelObjectType() != null) {
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
			} else {
				return null;
			}
		} else if (object instanceof ConnectorPatternRole) {
			return CALC_CONNECTOR_ICON;
		} else if (object instanceof ShapePatternRole) {
			return CALC_SHAPE_ICON;
		} else if (object instanceof DiagramPatternRole) {
			return EXAMPLE_DIAGRAM_ICON;
		} else if (object instanceof EditionPatternPatternRole) {
			return EDITION_PATTERN_ICON;
		} else if (object instanceof PrimitivePatternRole) {
			return UNKNOWN_ICON;
		} else if (object instanceof ClassPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_CLASS_ICON;
		} else if (object instanceof IndividualPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_INDIVIDUAL_ICON;
		} else if (object instanceof ObjectPropertyPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
		} else if (object instanceof DataPropertyPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
		} else if (object instanceof PropertyPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_PROPERTY_ICON;
		} else if (object instanceof StatementPatternRole) {
			return OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON;
		} else if (object instanceof LocalizedDictionary) {
			return LOCALIZATION_ICON;
		}
		logger.warning("No icon for " + object.getClass());
		return UNKNOWN_ICON;
	}

}
