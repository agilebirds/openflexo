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

import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.owl.OWLStatement;
import org.openflexo.toolbox.ImageIconResource;

public class OntologyIconLibrary {

	private static final Logger logger = Logger.getLogger(OntologyIconLibrary.class.getPackage().getName());

	public static final ImageIconResource ONTOLOGY_ICON = new ImageIconResource("Icons/Model/OE/Ontology.png");
	public static final ImageIconResource ONTOLOGY_LIBRARY_ICON = new ImageIconResource("Icons/Model/OE/OntologyLibrary.png");
	public static final ImageIconResource ONTOLOGY_CLASS_ICON = new ImageIconResource("Icons/Model/OE/IFlexoOntologyClass.png");
	public static final ImageIconResource ONTOLOGY_INDIVIDUAL_ICON = new ImageIconResource("Icons/Model/OE/IFlexoOntologyIndividual.png");
	public static final ImageIconResource ONTOLOGY_OBJECT_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/IFlexoOntologyObjectProperty.png");
	public static final ImageIconResource ONTOLOGY_DATA_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/IFlexoOntologyDataProperty.png");
	public static final ImageIconResource ONTOLOGY_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/IFlexoOntologyStructuralProperty.png");
	public static final ImageIconResource ONTOLOGY_ANNOTATION_PROPERTY_ICON = new ImageIconResource(
			"Icons/Model/OE/OntologyAnnotationProperty.png");
	public static final ImageIconResource ONTOLOGY_STATEMENT_ICON = new ImageIconResource("Icons/Model/OE/OntologyStatement.png");

	public static ImageIcon iconForObject(AbstractOntologyObject object) {
		if (object instanceof IFlexoOntology) {
			return ONTOLOGY_ICON;
		} else if (object instanceof IFlexoOntologyClass) {
			return ONTOLOGY_CLASS_ICON;
		} else if (object instanceof IFlexoOntologyIndividual) {
			return ONTOLOGY_INDIVIDUAL_ICON;
		} else if (object instanceof IFlexoOntologyStructuralProperty && ((IFlexoOntologyStructuralProperty) object).isAnnotationProperty()) {
			return ONTOLOGY_ANNOTATION_PROPERTY_ICON;
		} else if (object instanceof IFlexoOntologyObjectProperty) {
			return ONTOLOGY_OBJECT_PROPERTY_ICON;
		} else if (object instanceof IFlexoOntologyDataProperty) {
			return ONTOLOGY_DATA_PROPERTY_ICON;
		} else if (object instanceof OWLStatement) {
			return ONTOLOGY_STATEMENT_ICON;
		}
		logger.warning("No icon for " + object.getClass());
		return null;
	}
}
