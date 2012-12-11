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
package org.openflexo.technologyadapter.owl.gui;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLStatement;
import org.openflexo.toolbox.ImageIconResource;

public class OWLIconLibrary {

	private static final Logger logger = Logger.getLogger(OWLIconLibrary.class.getPackage().getName());

	public static final ImageIconResource ONTOLOGY_ICON = new ImageIconResource("src/main/resources/Icons/Ontology.png");
	public static final ImageIconResource ONTOLOGY_LIBRARY_ICON = new ImageIconResource("src/main/resources/Icons/OntologyLibrary.png");
	public static final ImageIconResource ONTOLOGY_CLASS_ICON = new ImageIconResource("src/main/resources/Icons/OntologyClass.png");
	public static final ImageIconResource ONTOLOGY_INDIVIDUAL_ICON = new ImageIconResource(
			"src/main/resources/Icons/OntologyIndividual.png");
	public static final ImageIconResource ONTOLOGY_OBJECT_PROPERTY_ICON = new ImageIconResource(
			"src/main/resources/Icons/OntologyObjectProperty.png");
	public static final ImageIconResource ONTOLOGY_DATA_PROPERTY_ICON = new ImageIconResource(
			"src/main/resources/Icons/OntologyDataProperty.png");
	public static final ImageIconResource ONTOLOGY_PROPERTY_ICON = new ImageIconResource("src/main/resources/Icons/OntologyProperty.png");
	public static final ImageIconResource ONTOLOGY_ANNOTATION_PROPERTY_ICON = new ImageIconResource(
			"src/main/resources/Icons/OntologyAnnotationProperty.png");
	public static final ImageIconResource ONTOLOGY_STATEMENT_ICON = new ImageIconResource("src/main/resources/Icons/OntologyStatement.png");

	public static ImageIcon iconForObject(Class<? extends OWLObject> objectClass) {
		if (OWLOntology.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_ICON;
		} else if (OWLClass.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_CLASS_ICON;
		} else if (OWLIndividual.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_INDIVIDUAL_ICON;
		}/* else if (object instanceof IFlexoOntologyStructuralProperty && ((IFlexoOntologyStructuralProperty) object).isAnnotationProperty()) {
			return ONTOLOGY_ANNOTATION_PROPERTY_ICON;
			}*/else if (OWLObjectProperty.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_OBJECT_PROPERTY_ICON;
		} else if (OWLDataProperty.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_DATA_PROPERTY_ICON;
		} else if (OWLStatement.class.isAssignableFrom(objectClass)) {
			return ONTOLOGY_STATEMENT_ICON;
		}
		logger.warning("No icon for " + objectClass);
		return null;
	}
}
