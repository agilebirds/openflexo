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
package org.openflexo.foundation.ontology;

import org.openflexo.foundation.Inspectors;

import com.hp.hpl.jena.ontology.OntProperty;

public class OntologyObjectProperty extends OntologyProperty implements Comparable<OntologyObjectProperty> {

	protected OntologyObjectProperty(OntProperty anObjectProperty, FlexoOntology ontology) {
		super(anObjectProperty, ontology);
	}

	@Override
	public void delete() {
		getFlexoOntology().removeObjectProperty(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() {
		return "ontology_object_property";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OntologyObjectProperty:" + getURI();
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_OBJECT_PROPERTY_INSPECTOR;
		}
	}

	@Override
	public OntProperty getOntProperty() {
		return super.getOntProperty();
	}

	@Override
	public int compareTo(OntologyObjectProperty o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept) {
		if (concept instanceof OntologyObjectProperty) {
			OntologyObjectProperty ontologyObjectProperty = (OntologyObjectProperty) concept;
			return ontologyObjectProperty.getOntProperty().hasSuperProperty(getOntProperty(), false);
		}
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		return "<html>Object property <b>" + getName() + "</b><br>" + "<i>" + getURI() + "</i><br>" + "Domain: "
				+ (getDomain() != null ? getDomain().getURI() : "?") + "<br>" + "Range: "
				+ (getRange() != null ? getRange().getURI() : "?") + "<br>" + "</html>";
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return true;
	}

	public boolean isLiteralRange() {
		return (getRange() == getOntologyLibrary().getOntologyObject(OntologyLibrary.RDFS_LITERAL_URI));
	}

	@Override
	public Class getBaseClass() {
		return OntologyObjectProperty.class;
	}

}
