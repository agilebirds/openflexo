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
package org.openflexo.technologyadapter.xsd.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.AbstractOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.OntologyObjectConverter;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.Language;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.xmlcode.StringConvertable;

public abstract class AbstractXSOntObject extends AbstractOntologyObject implements IFlexoOntologyConcept, XSOntologyURIDefinitions,
		InspectableObject, StringConvertable<IFlexoOntologyConcept> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractXSOntObject.class
			.getPackage().getName());

	private String uri;
	private String name;
	private XSOntology ontology;

	private final Set<XSOntProperty> propertiesTakingMySelfAsRange;
	private final Set<XSOntProperty> propertiesTakingMySelfAsDomain;

	private XSDTechnologyAdapter technologyAdapter;

	protected AbstractXSOntObject(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super();

		technologyAdapter = adapter;
		this.name = name;
		this.uri = uri;
		this.ontology = ontology;
		propertiesTakingMySelfAsRange = new HashSet<XSOntProperty>();
		propertiesTakingMySelfAsDomain = new HashSet<XSOntProperty>();
	}

	protected AbstractXSOntObject(XSDTechnologyAdapter adapter) {
		this(null, null, null, adapter);
	}

	@Override
	public XSDTechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("The ontology object name changed, renaming of the object URI not implemented yet");
		}
	}

	@Override
	public boolean getIsReadOnly() {
		return getFlexoOntology().getIsReadOnly();
	}

	@Override
	public XSOntology getFlexoOntology() {
		return ontology;
	}

	@Override
	public OntologyObjectConverter getConverter() {
		/*if (getOntologyLibrary() != null) {
			return getOntologyLibrary().getOntologyObjectConverter();
		}*/
		return null;
	}

	@Override
	public Object getPropertyValue(IFlexoOntologyStructuralProperty property) {
		// Stub
		return null;
	}

	@Override
	public void setPropertyValue(IFlexoOntologyStructuralProperty property, Object newValue) {
		// Stub
	}

	@Override
	public String getHTMLDescription() {
		return getDisplayableDescription();
	}

	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		// TODO Ask Sylvain
		return false;
	}

	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept concept) {
		// TODO Ask Sylvain
		return false;
	}

	@Override
	public boolean equalsToConcept(IFlexoOntologyConcept o) {
		// TODO Ask Sylvain
		return false;
	}

	@Override
	public XSOntology getOntology() {
		return ontology;
	}

	@Override
	public Object getAnnotationValue(IFlexoOntologyDataProperty property, Language language) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Annotations not yet supported by XSOntologies");
		}
		return null;
	}

	@Override
	public void setAnnotationValue(Object value, IFlexoOntologyDataProperty property, Language language) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Annotations not yet supported by XSOntologies");
		}
	}

	@Override
	public Object getAnnotationObjectValue(IFlexoOntologyObjectProperty property) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Annotations not yet supported by XSOntologies");
		}
		return null;
	}

	@Override
	public void setAnnotationObjectValue(Object value, IFlexoOntologyObjectProperty property, Language language) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Annotations not yet supported by XSOntologies");
		}
	}

	public void clearPropertiesTakingMyselfAsRangeOrDomain() {
		propertiesTakingMySelfAsRange.clear();
		propertiesTakingMySelfAsDomain.clear();
	}

	public void addPropertyTakingMyselfAsRange(XSOntProperty property) {
		propertiesTakingMySelfAsRange.add(property);
	}

	public void addPropertyTakingMyselfAsDomain(XSOntProperty property) {
		propertiesTakingMySelfAsDomain.add(property);
	}

	@Override
	public Set<? extends XSOntProperty> getPropertiesTakingMySelfAsRange() {
		return propertiesTakingMySelfAsRange;
	}

	@Override
	public Set<? extends XSOntProperty> getPropertiesTakingMySelfAsDomain() {
		// TODO Auto-generated method stub
		return propertiesTakingMySelfAsDomain;
	}

	@Override
	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyObjectProperty property, IFlexoOntologyConcept object) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Statements aren't supported by XSOntology objects");
		}
		return null;
	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyStructuralProperty property, Object value) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Statements aren't supported by XSOntology objects");
		}
		return null;
	}

	@Override
	public Object addPropertyStatement(IFlexoOntologyStructuralProperty property, String value, Language language) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Statements aren't supported by XSOntology objects");
		}
		return null;
	}

	@Override
	public Object addDataPropertyStatement(IFlexoOntologyDataProperty property, Object value) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Statements aren't supported by XSOntology objects");
		}
		return null;
	}

	@Override
	public boolean isOntology() {
		return false;
	}

	@Override
	public boolean isOntologyClass() {
		return false;
	}

	@Override
	public boolean isOntologyIndividual() {
		return false;
	}

	@Override
	public boolean isOntologyObjectProperty() {
		return false;
	}

	@Override
	public boolean isOntologyDataProperty() {
		return false;
	}

}
