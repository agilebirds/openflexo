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
package org.openflexo.technologyadapter.owl.model;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.ontology.OntProperty;

public class OWLDataProperty extends OWLProperty implements IFlexoOntologyDataProperty, Comparable<IFlexoOntologyDataProperty> {

	static final Logger logger = Logger.getLogger(IFlexoOntologyDataProperty.class.getPackage().getName());

	protected OWLDataProperty(OntProperty aDataProperty, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aDataProperty, ontology, adapter);
	}

	@Override
	public boolean delete() {
		getFlexoOntology().removeDataProperty(this);
		getOntResource().remove();
		getFlexoOntology().updateConceptsAndProperties();
		super.delete();
		deleteObservers();
		return true;
	}

	/*@Override
	public DatatypeProperty getOntProperty() {
		return (DatatypeProperty) super.getOntProperty();
	}*/

	@Override
	public int compareTo(IFlexoOntologyDataProperty o) {
		return COMPARATOR.compare(this, o);
	}

	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		if (concept instanceof OWLDataProperty) {
			OWLDataProperty ontologyDataProperty = (OWLDataProperty) concept;
			return ontologyDataProperty.getOntProperty().hasSuperProperty(getOntProperty(), false);
		}
		return false;
	}

	/**
	 * Return a vector of Ontology property, as a subset of getSubProperties(), which correspond to all properties necessary to see all
	 * properties belonging to supplied context, which is an ontology
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<OWLDataProperty> getSubProperties(IFlexoOntology context) {
		if (context instanceof OWLOntology) {
			List<OWLDataProperty> returned = new Vector<OWLDataProperty>();
			for (OWLDataProperty p : ((OWLOntology) context).getAccessibleDataProperties()) {
				if (p.isSubConceptOf(this)) {
					if (!returned.contains(p)) {
						returned.add(p);
					}
				}
			}
			return returned;
		}

		return null;
	}

	public OWLDataType getDataType() {
		return getRange();
	}

	@Override
	public OWLDataType getRange() {
		if (getRangeStatement() != null) {
			return getRangeStatement().getDataType();
		}
		return null;
	}

	@Override
	public String getDisplayableDescription() {
		return "<html>Datatype property <b>" + getName() + "</b><br>" + "<i>" + getURI() + "</i><br>" + "Domain: "
				+ (getDomain() != null ? getDomain().getURI() : "?") + "<br>" + "Range: "
				+ (getRange() != null ? getRange().toString() : "?") + "<br>" + "</html>";
	}

	@Override
	public String getHTMLDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("Datatype property <b>" + getName() + "</b><br>");
		sb.append("<i>" + getURI() + "</i><br>");
		sb.append("<b>Asserted in:</b> " + getOntology().getURI() + "<br>");
		sb.append("<b>Domain:</b> " + (getDomain() != null ? getDomain().getURI() : "?") + "<br>");
		sb.append("<b>Datatype:</b> " + (getDataType() != null ? getDataType().toString() : "?") + "<br>");
		if (redefinesOriginalDefinition()) {
			sb.append("<b>Redefines:</b> " + getOriginalDefinition() + "<br>");
		}
		sb.append("</html>");
		return sb.toString();
	}

	@Override
	protected void recursivelySearchRangeAndDomains() {
		super.recursivelySearchRangeAndDomains();
		for (OWLProperty aProperty : getSuperProperties()) {
			propertiesTakingMySelfAsRange.addAll(aProperty.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(aProperty.getPropertiesTakingMySelfAsDomain());
		}
		OWLClass DATA_PROPERTY_CONCEPT = getOntology().getClass(OWL_DATA_PROPERTY_URI);
		// DATA_PROPERTY_CONCEPT is generally non null but can be null when reading RDFS for example
		if (DATA_PROPERTY_CONCEPT != null) {
			propertiesTakingMySelfAsRange.addAll(DATA_PROPERTY_CONCEPT.getPropertiesTakingMySelfAsRange());
			propertiesTakingMySelfAsDomain.addAll(DATA_PROPERTY_CONCEPT.getPropertiesTakingMySelfAsDomain());
		}
	}

}
