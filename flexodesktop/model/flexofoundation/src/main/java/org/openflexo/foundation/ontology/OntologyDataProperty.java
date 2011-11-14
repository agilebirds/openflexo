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

import com.hp.hpl.jena.ontology.DatatypeProperty;

public class OntologyDataProperty extends OntologyProperty implements Comparable<OntologyDataProperty> {

	protected OntologyDataProperty(DatatypeProperty aDataProperty, FlexoOntology ontology) {
		super(aDataProperty, ontology);
	}

	@Override
	public void delete() {
		getFlexoOntology().removeDataProperty(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() {
		return "ontology_data_property";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OntologyDataProperty:" + getURI();
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_READ_ONLY_INSPECTOR; // read-only
		} else {
			return Inspectors.VE.ONTOLOGY_DATA_PROPERTY_INSPECTOR;
		}
	}

	@Override
	public DatatypeProperty getOntProperty() {
		return (DatatypeProperty) super.getOntProperty();
	}

	@Override
	public int compareTo(OntologyDataProperty o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public boolean isSuperConceptOf(OntologyObject concept) {
		if (concept instanceof OntologyDataProperty) {
			OntologyDataProperty ontologyDataProperty = (OntologyDataProperty) concept;
			return ontologyDataProperty.getOntProperty().hasSuperProperty(getOntProperty(), false);
		}
		return false;
	}

	public OntologicDataType getDataType() {
		if (getRangeStatement() != null) {
			return getRangeStatement().getDataType();
		}
		return null;
	}

	@Override
	public String getDisplayableDescription() {
		return "<html>Datatype property <b>" + getName() + "</b><br>" + "<i>" + getURI() + "</i><br>" + "Domain: "
				+ (getDomain() != null ? getDomain().getURI() : "?") + "<br>" + "Range: "
				+ (getDataType() != null ? getDataType().toString() : "?") + "<br>" + "</html>";
	}

	@Override
	public boolean isOntologyDataProperty() {
		return true;
	}

}
