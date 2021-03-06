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
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.owl.OWLStatement;
import org.openflexo.toolbox.ImageIconResource;

public class OntologyIconLibrary {

	private static final Logger logger = Logger.getLogger(OntologyIconLibrary.class.getPackage().getName());

	public static final ImageIconResource ONTOLOGY_ICON = new ImageIconResource("Icons/Model/OE/Ontology.png");
	public static final ImageIconResource ONTOLOGY_LIBRARY_ICON = new ImageIconResource("Icons/Model/OE/OntologyLibrary.png");
	public static final ImageIconResource ONTOLOGY_CLASS_ICON = new ImageIconResource("Icons/Model/OE/OntologyClass.png");
	public static final ImageIconResource ONTOLOGY_INDIVIDUAL_ICON = new ImageIconResource("Icons/Model/OE/OntologyIndividual.png");
	public static final ImageIconResource ONTOLOGY_OBJECT_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/OntologyObjectProperty.png");
	public static final ImageIconResource ONTOLOGY_DATA_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/OntologyDataProperty.png");
	public static final ImageIconResource ONTOLOGY_PROPERTY_ICON = new ImageIconResource("Icons/Model/OE/OntologyProperty.png");
	public static final ImageIconResource ONTOLOGY_ANNOTATION_PROPERTY_ICON = new ImageIconResource(
			"Icons/Model/OE/OntologyAnnotationProperty.png");
	public static final ImageIconResource ONTOLOGY_STATEMENT_ICON = new ImageIconResource("Icons/Model/OE/OntologyStatement.png");

	public static ImageIcon iconForObject(AbstractOntologyObject object) {
		if (object instanceof FlexoOntology) {
			return ONTOLOGY_ICON;
		} else if (object instanceof OntologyClass) {
			return ONTOLOGY_CLASS_ICON;
		} else if (object instanceof OntologyIndividual) {
			return ONTOLOGY_INDIVIDUAL_ICON;
		} else if (object instanceof OntologyProperty && ((OntologyProperty) object).isAnnotationProperty()) {
			return ONTOLOGY_ANNOTATION_PROPERTY_ICON;
		} else if (object instanceof OntologyObjectProperty) {
			return ONTOLOGY_OBJECT_PROPERTY_ICON;
		} else if (object instanceof OntologyDataProperty) {
			return ONTOLOGY_DATA_PROPERTY_ICON;
		} else if (object instanceof OWLStatement) {
			return ONTOLOGY_STATEMENT_ICON;
		}
		logger.warning("No icon for " + object.getClass());
		return null;
	}
}
