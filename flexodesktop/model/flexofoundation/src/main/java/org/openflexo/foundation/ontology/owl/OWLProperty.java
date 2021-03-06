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
package org.openflexo.foundation.ontology.owl;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntProperty;

public abstract class OWLProperty extends OWLObject<OntProperty> implements OntologyProperty {

	private static final Logger logger = Logger.getLogger(OntologyProperty.class.getPackage().getName());

	private OntProperty ontProperty;

	private DomainStatement domainStatement;
	private RangeStatement rangeStatement;
	private List<DomainStatement> domainStatementList;
	private List<RangeStatement> rangeStatementList;
	private List<OWLObject<?>> domainList;
	private List<OWLObject<?>> rangeList;

	private boolean superDomainStatementWereAppened = false;
	private boolean superRangeStatementWereAppened = false;

	private final Vector<OWLProperty> superProperties;

	protected OWLProperty(OntProperty anOntProperty, OWLOntology ontology) {
		super(anOntProperty, ontology);
		ontProperty = anOntProperty;
		superProperties = new Vector<OWLProperty>();
		domainStatementList = new ArrayList<DomainStatement>();
		rangeStatementList = new ArrayList<RangeStatement>();
		domainList = null;
		rangeList = null;
	}

	/**
	 * Init this OntologyProperty, given base OntProperty
	 */
	protected void init() {
		updateOntologyStatements(ontProperty);
		updateSuperProperties(ontProperty);
	}

	/**
	 * Update this OntologyProperty, given base OntProperty
	 */
	@Override
	protected void update() {
		updateOntologyStatements(ontProperty);
		updateSuperProperties(ontProperty);
	}

	/**
	 * Update this OntologyProperty given a new OntProperty which is assumed to extends base OntProperty
	 * 
	 * @param anOntProperty
	 */
	protected void update(OntProperty anOntProperty) {
		updateOntologyStatements(anOntProperty);
		updateSuperProperties(anOntProperty);
	}

	@Override
	public void setName(String aName) {
		ontProperty = renameURI(aName, ontProperty, OntProperty.class);
	}

	@Override
	protected void _setOntResource(OntProperty r) {
		ontProperty = r;
	}

	public static final Comparator<OntologyProperty> COMPARATOR = new Comparator<OntologyProperty>() {
		@Override
		public int compare(OntologyProperty o1, OntologyProperty o2) {
			return Collator.getInstance().compare(o1.getName(), o2.getName());
		}
	};

	public OntProperty getOntProperty() {
		return ontProperty;
	}

	@Override
	public OntProperty getOntResource() {
		return getOntProperty();
	}

	private void updateSuperProperties(OntProperty anOntProperty) {
		// superClasses.clear();
		try {
			Iterator it = anOntProperty.listSuperProperties(true);
			while (it.hasNext()) {
				OntProperty father = (OntProperty) it.next();
				OWLProperty fatherProp = getOntology().getProperty(father.getURI());
				if (fatherProp != null) {
					if (!superProperties.contains(fatherProp)) {
						superProperties.add(fatherProp);
					}
				}
			}
		} catch (ConversionException e) {
			logger.warning("Unexpected " + e.getMessage() + " while processing " + getURI());
			// Petit hack en attendant de mieux comprendre le probleme
			if (getURI().equals("http://www.w3.org/2004/02/skos/core#altLabel")
					|| getURI().equals("http://www.w3.org/2004/02/skos/core#prefLabel")
					|| getURI().equals("http://www.w3.org/2004/02/skos/core#hiddenLabel")) {
				OWLProperty label = getOntology().getProperty("http://www.w3.org/2000/01/rdf-schema#label");
				if (!superProperties.contains(label)) {
					superProperties.add(label);
				}
			}
		}
	}

	@Override
	public Vector<OWLProperty> getSuperProperties() {
		return superProperties;
	}

	/*private boolean isRequired(OntologyProperty aProperty, FlexoOntology context) {
		if (aProperty.getFlexoOntology() == context) {
			return true;
		}
		for (OntologyProperty aSubProperty : aProperty.getSubProperties()) {
			if (isRequired(aSubProperty, context)) {
				return true;
			}
		}
		return false;
	}*/

	@Override
	public boolean isAnnotationProperty() {
		return getOntResource().isAnnotationProperty();// isAnnotationProperty;
	}

	@Override
	public void updateOntologyStatements(OntProperty anOntResource) {
		super.updateOntologyStatements(anOntResource);
		superDomainStatementWereAppened = false;
		superRangeStatementWereAppened = false;
		domainStatementList.clear();
		rangeStatementList.clear();
		domainList = null;
		rangeList = null;
		for (OWLStatement s : getSemanticStatements()) {
			if (s instanceof DomainStatement) {
				domainStatement = (DomainStatement) s;
				domainStatementList.add(domainStatement);
			}
			if (s instanceof RangeStatement) {
				rangeStatement = (RangeStatement) s;
				rangeStatementList.add(rangeStatement);
			}
		}
	}

	/**
	 * Return domain statement, asserting there is only one domain statement
	 * 
	 * @return
	 */
	public DomainStatement getDomainStatement() {
		if (domainStatement == null) {
			for (OWLProperty p : getSuperProperties()) {
				DomainStatement d = p.getDomainStatement();
				if (d != null) {
					return d;
				}
			}
			return null;
		}
		return domainStatement;
	}

	/**
	 * Return range statement, asserting there is only one range statement
	 * 
	 * @return
	 */
	public RangeStatement getRangeStatement() {
		if (rangeStatement == null) {
			for (OWLProperty p : getSuperProperties()) {
				RangeStatement r = p.getRangeStatement();
				if (r != null) {
					return r;
				}
			}
			return null;
		}
		return rangeStatement;
	}

	/**
	 * Return domain as ontology object, asserting there is only one domain statement
	 * 
	 * @return
	 */
	@Override
	public OntologyObject getDomain() {
		/*		if (getURI().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
		//			System.out.println("Pour "+getURI()+" le domain statement est "+getDomainStatement());
		//			return getOntologyLibrary().getOntologyObject("http://www.w3.org/2000/01/rdf-schema#Resource");
					return getOntologyLibrary().THING;
				}
				if (getURI().equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
					System.out.println("Pour "+getURI()+" le domain statement est "+getDomainStatement());
					if (getDomainStatement() == null) {
						for (OntologyProperty p : getSuperProperties()) {
							System.out.println("Examining "+p);
							OntologyObject o = p.getDomain();
							if (o != null) {
								System.out.println("Je retourne "+o);
								return o;
							}
						}
						return null;
					}
		//			return getOntologyLibrary().getOntologyObject("http://www.w3.org/2000/01/rdf-schema#Resource");
					return getOntologyLibrary().THING;
				}*/
		if (getDomainStatement() == null) {
			for (OWLProperty p : getSuperProperties()) {
				OntologyObject o = p.getDomain();
				if (o != null) {
					return o;
				}
			}
			return null;
		}
		return getDomainStatement().getDomain();
	}

	/**
	 * Return range as ontology object, asserting there is only one range statement
	 * 
	 * @return
	 */
	public OntologyObject getRange() {
		/*		if (getURI().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
					System.out.println("Pour "+getURI()+" le range statement est "+getRangeStatement());
				}*/
		if (getRangeStatement() == null) {
			return null;
		}
		return getRangeStatement().getRange();
	}

	/**
	 * Return list of DomainStatement
	 * 
	 * @return
	 */
	public List<DomainStatement> getDomainStatementList() {
		if (!superDomainStatementWereAppened) {
			for (OWLProperty p : getSuperProperties()) {
				domainStatementList.addAll(p.getDomainStatementList());
			}
			superDomainStatementWereAppened = true;
		}
		return domainStatementList;
	}

	public List<RangeStatement> getRangeStatementList() {
		if (!superRangeStatementWereAppened) {
			for (OWLProperty p : getSuperProperties()) {
				rangeStatementList.addAll(p.getRangeStatementList());
			}
			superRangeStatementWereAppened = true;
		}
		return rangeStatementList;
	}

	public List<OWLObject<?>> getDomainList() {
		if (domainList == null) {
			domainList = new ArrayList<OWLObject<?>>();
			for (DomainStatement s : getDomainStatementList()) {
				if (s.getDomain() != null) {
					domainList.add(s.getDomain());
				}
			}
		}
		return domainList;
	}

	public List<OWLObject<?>> getRangeList() {
		if (rangeList == null) {
			rangeList = new ArrayList<OWLObject<?>>();
			for (RangeStatement s : getRangeStatementList()) {
				if (s.getRange() != null) {
					rangeList.add(s.getRange());
				}
			}
		}
		return rangeList;
	}

}
