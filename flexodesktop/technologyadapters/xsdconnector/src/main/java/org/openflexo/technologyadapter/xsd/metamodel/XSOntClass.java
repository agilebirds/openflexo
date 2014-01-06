/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.xsd.metamodel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.technologyadapter.xml.model.IXMLType;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntConcept;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSOntologyURIDefinitions;
import org.openflexo.toolbox.StringUtils;

public class XSOntClass extends AbstractXSOntConcept implements IFlexoOntologyClass, XSOntologyURIDefinitions, IXMLType {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntClass.class.getPackage()
			.getName());

	private final List<XSOntClass> superClasses = new ArrayList<XSOntClass>();
	private final HashMap<String,XSOntProperty> properties = new HashMap<String,XSOntProperty>();

	protected XSOntClass(XSOntology ontology, String name, String uri, XSDTechnologyAdapter adapter) {
		super(ontology, name, uri, adapter);
	}

	@Override
	public boolean isSuperClassOf(IFlexoOntologyClass aClass) {
		if (aClass instanceof XSOntClass == false) {
			return false;
		}
		if (aClass == this || equalsToConcept(aClass)) {
			return true;
		}
		for (XSOntClass c : ((XSOntClass) aClass).getSuperClasses()) {
			if (isSuperClassOf(c)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSuperConceptOf(IFlexoOntologyConcept concept) {
		if (concept instanceof XSOntClass){
		return isSuperClassOf((IFlexoOntologyClass) concept);
		}
		else return false;
	}

	@Override
	public boolean isSubConceptOf(IFlexoOntologyConcept concept) {
		if (concept instanceof XSOntClass){
		return concept.isSuperConceptOf(this);
		}
		else return false;
	}

	@Override
	public void addPropertyTakingMyselfAsDomain(XSOntProperty property) {
		super.addPropertyTakingMyselfAsDomain(property);
			properties.put(property.getName(), property);
	}

	@Override
	public List<XSOntClass> getSuperClasses() {
		return superClasses;
	}

	public void addToSuperClasses(IFlexoOntologyClass aClass) {
		if (!(aClass instanceof XSOntClass)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Class " + aClass + " is not a XSOntClass");
			}
			return;
		}
		superClasses.add((XSOntClass) aClass);
	}

	public void removeFromSuperClasses(IFlexoOntologyClass aClass) {
		superClasses.remove(aClass);
	}

	@Override
	public List<? extends IFlexoOntologyClass> getSubClasses(IFlexoOntology context) {
		List<XSOntClass> listAllClasses = getOntology().getAccessibleClasses();
		ArrayList<XSOntClass> returned = new ArrayList<XSOntClass>();
		for (XSOntClass aClass : listAllClasses){
			if (aClass instanceof XSOntClass && isSuperClassOf(aClass)) {
				returned.add((XSOntClass) aClass);
			}
		}
		return returned;
	}

	@Override
	public boolean isNamedClass() {
		return StringUtils.isNotEmpty(getURI());
	}

	@Override
	public boolean isRootConcept() {
		return isNamedClass() && getURI().equals(XS_THING_URI);
	}

	@Override
	public String getDisplayableDescription() {
		// TODO tell where it's from (element/complex type)
		return getName();
	}

	@Override
	public <T> T accept(IFlexoOntologyConceptVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public XSOntology getContainer() {
		return getOntology();
	}

	@Override
	public List<? extends IFlexoOntologyFeatureAssociation> getStructuralFeatureAssociations() {
		List<XSOntProperty> returned = new ArrayList<XSOntProperty>();
		for (XSOntProperty xsOntRest : properties.values()) {
			returned.add(xsOntRest);
		}
		for (XSOntClass sc: this.getSuperClasses()) {
			returned.addAll((Collection<? extends XSOntProperty>) sc.getStructuralFeatureAssociations());
			}
		return (List<? extends IFlexoOntologyFeatureAssociation>) returned;
	}


	public XSOntProperty getPropertyByName(String name) {
		XSOntProperty returned = null;
		returned = properties.get(name);
		if (returned == null) {
			// Check if property exists in superclasses
			for (XSOntClass sc: this.getSuperClasses()) {
				returned = sc.getPropertyByName(name);
				if (returned != null){
					return returned;
				}
			}
		}
		return returned;
	}

}
