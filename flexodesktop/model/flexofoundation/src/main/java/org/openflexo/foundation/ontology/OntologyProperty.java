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

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

public abstract class OntologyProperty extends OntologyObject {

	private static final Logger logger = Logger.getLogger(OntologyProperty.class.getPackage().getName());

	private OntProperty ontProperty;

	private DomainStatement domainStatement;
	private RangeStatement rangeStatement;

	private final Vector<OntologyProperty> superProperties;
	private final Vector<OntologyProperty> subProperties;

	private final boolean isAnnotationProperty;

	protected OntologyProperty(OntProperty anOntProperty, FlexoOntology ontology) {
		super(anOntProperty, ontology);
		ontProperty = anOntProperty;
		superProperties = new Vector<OntologyProperty>();
		subProperties = new Vector<OntologyProperty>();
		isAnnotationProperty = anOntProperty.isAnnotationProperty();
	}

	protected void init() {
		updateOntologyStatements();
		updateSuperProperties();
		updateSubProperties();
	}

	@Override
	protected void update() {
		updateOntologyStatements();
		updateSuperProperties();
		updateSubProperties();
	}

	@Override
	public void setName(String aName) {
		ontProperty = renameURI(aName, ontProperty, OntProperty.class);
	}

	@Override
	protected void _setOntResource(OntResource r) {
		ontProperty = (OntProperty) r;
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

	private void updateSuperProperties() {
		// superClasses.clear();
		try {
			Iterator it = ontProperty.listSuperProperties(true);
			while (it.hasNext()) {
				OntProperty father = (OntProperty) it.next();
				OntologyProperty fatherProp = getOntologyLibrary().getProperty(father.getURI());
				if (fatherProp != null) {
					if (!superProperties.contains(fatherProp)) {
						superProperties.add(fatherProp);
					}
					// System.out.println("Add "+fatherClass.getName()+" as a super class of "+getName());
					if (!fatherProp.subProperties.contains(this)) {
						// System.out.println("Add "+getName()+" as a sub class of "+fatherClass.getName());
						fatherProp.subProperties.add(this);
					}
				}
			}
		} catch (ConversionException e) {
			logger.warning("Unexpected " + e.getMessage() + " while processing " + getURI());
			// Petit hack en attendant de mieux comprendre le probleme
			if (getURI().equals("http://www.w3.org/2004/02/skos/core#altLabel")
					|| getURI().equals("http://www.w3.org/2004/02/skos/core#prefLabel")
					|| getURI().equals("http://www.w3.org/2004/02/skos/core#hiddenLabel")) {
				OntologyProperty label = getOntologyLibrary().getProperty("http://www.w3.org/2000/01/rdf-schema#label");
				if (!superProperties.contains(label)) {
					superProperties.add(label);
				}
				if (!label.subProperties.contains(this)) {
					label.subProperties.add(this);
				}
			}
		}
	}

	private void updateSubProperties() {
		// subClasses.clear();
		try {
			Iterator it = ontProperty.listSubProperties(true);
			while (it.hasNext()) {
				OntProperty child = (OntProperty) it.next();
				OntologyProperty childProperty = getOntologyLibrary().getProperty(child.getURI());
				if (childProperty != null) {
					if (!subProperties.contains(childProperty)) {
						subProperties.add(childProperty);
					}
					if (!childProperty.superProperties.contains(this)) {
						childProperty.superProperties.add(this);
					}
				}
			}
		} catch (ConversionException e) {
			logger.warning("Unexpected " + e.getMessage() + " while processing " + getURI());
		}
	}

	public Vector<OntologyProperty> getSuperProperties() {
		return superProperties;
	}

	public Vector<OntologyProperty> getSubProperties() {
		return subProperties;
	}

	/**
	 * Return a vector of Ontology property, as a subset of getSubProperties(), which correspond to all properties necessary to see all
	 * properties belonging to supplied context, which is an ontology
	 * 
	 * @param context
	 * @return
	 */
	public Vector<OntologyProperty> getSubProperties(FlexoOntology context) {
		Vector<OntologyProperty> returned = new Vector<OntologyProperty>();
		for (OntologyProperty aProperty : getSubProperties()) {
			if (isRequired(aProperty, context)) {
				returned.add(aProperty);
			}
		}
		return returned;
	}

	private boolean isRequired(OntologyProperty aProperty, FlexoOntology context) {
		if (aProperty.getFlexoOntology() == context) {
			return true;
		}
		for (OntologyProperty aSubProperty : aProperty.getSubProperties()) {
			if (isRequired(aSubProperty, context)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAnnotationProperty() {
		return isAnnotationProperty;
	}

	@Override
	public void updateOntologyStatements() {
		super.updateOntologyStatements();
		for (OntologyStatement s : getSemanticStatements()) {
			if (s instanceof DomainStatement) {
				domainStatement = (DomainStatement) s;
			}
			if (s instanceof RangeStatement) {
				rangeStatement = (RangeStatement) s;
			}
		}
	}

	public DomainStatement getDomainStatement() {
		if (domainStatement == null) {
			for (OntologyProperty p : getSuperProperties()) {
				DomainStatement d = p.getDomainStatement();
				if (d != null)
					return d;
			}
			return null;
		}
		return domainStatement;
	}

	public RangeStatement getRangeStatement() {
		if (rangeStatement == null) {
			for (OntologyProperty p : getSuperProperties()) {
				RangeStatement r = p.getRangeStatement();
				if (r != null)
					return r;
			}
			return null;
		}
		return rangeStatement;
	}

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
			for (OntologyProperty p : getSuperProperties()) {
				OntologyObject o = p.getDomain();
				if (o != null)
					return o;
			}
			return null;
		}
		return getDomainStatement().getDomain();
	}

	public OntologyObject getRange() {
		/*		if (getURI().equals("http://www.w3.org/2000/01/rdf-schema#label")) {
					System.out.println("Pour "+getURI()+" le range statement est "+getRangeStatement());
				}*/
		if (getRangeStatement() == null) {
			return null;
		}
		return getRangeStatement().getRange();
	}

}
