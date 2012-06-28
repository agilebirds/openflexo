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

import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;

import com.hp.hpl.jena.ontology.OntProperty;

public class OntologyObjectProperty extends OntologyProperty implements Comparable<OntologyObjectProperty> {

	static final Logger logger = Logger.getLogger(OntologyObjectProperty.class.getPackage().getName());

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
		return (getRange() == getOntology().getOntologyObject(RDFSURIDefinitions.RDFS_LITERAL_URI));
	}

	@Override
	public String getHTMLDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("Object property <b>" + getName() + "</b><br>");
		sb.append("<i>" + getURI() + "</i><br>");
		sb.append("<b>Asserted in:</b> " + getOntology().getURI() + "<br>");
		sb.append("<b>Domain:</b> " + (getDomain() != null ? getDomain().getURI() : "?") + "<br>");
		sb.append("<b>Range:</b> " + (getRange() != null ? getRange().getURI() : "?") + "<br>");
		if (redefinesOriginalDefinition()) {
			sb.append("<b>Redefines:</b> " + getOriginalDefinition() + "<br>");
		}
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		for (OntologyProperty aProperty : getSuperProperties()) {
			propertiesTakingMySelfAsRange.addAll(aProperty.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(aProperty.getPropertiesTakingMySelfAsDomain());
		}
		OntologyClass OBJECT_PROPERTY_CONCEPT = getOntology().getClass(OWL_OBJECT_PROPERTY_URI);
		// OBJECT_PROPERTY_CONCEPT is generally non null but can be null when reading RDFS for example
		if (OBJECT_PROPERTY_CONCEPT != null) {
			propertiesTakingMySelfAsRange.addAll(OBJECT_PROPERTY_CONCEPT.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(OBJECT_PROPERTY_CONCEPT.getPropertiesTakingMySelfAsDomain());
		}
	}

}
