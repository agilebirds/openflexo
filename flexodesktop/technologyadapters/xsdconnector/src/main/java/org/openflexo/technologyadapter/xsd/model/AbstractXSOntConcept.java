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
import java.util.List;
import java.util.Set;

import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;

public abstract class AbstractXSOntConcept extends AbstractXSOntObject implements IFlexoOntologyConcept {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(AbstractXSOntConcept.class
			.getPackage().getName());

	private final Set<XSOntProperty> propertiesTakingMySelfAsRange;
	private final Set<XSOntProperty> propertiesTakingMySelfAsDomain;

	protected AbstractXSOntConcept(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);

		propertiesTakingMySelfAsRange = new HashSet<XSOntProperty>();
		propertiesTakingMySelfAsDomain = new HashSet<XSOntProperty>();
	}

	protected AbstractXSOntConcept(XSDTechnologyAdapter adapter) {
		this(null, null, null, adapter);
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
	public final List<IFlexoOntologyAnnotation> getAnnotations() {
		// no annotations defined in XSD/XML technology
		return null;
	}

}