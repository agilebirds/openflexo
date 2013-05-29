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

package org.openflexo.technologyadapter.xsd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConceptVisitor;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.XSDModelSlot.XSURIProcessor;
import org.openflexo.toolbox.StringUtils;

import com.sun.xml.xsom.impl.scd.Iterators.Map;

public class XSOntClass extends AbstractXSOntConcept implements IFlexoOntologyClass, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntClass.class.getPackage()
			.getName());

	private final List<XSOntClass> superClasses = new ArrayList<XSOntClass>();
	// CG : changed to map to enable to access restrictions by name
	private final HashMap<String,XSOntRestriction> restrictions = new HashMap<String,XSOntRestriction>();

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
		if (aClass instanceof XSOntRestriction) {
			restrictions.put(aClass.getName(), (XSOntRestriction) aClass);
		}
		superClasses.add((XSOntClass) aClass);
	}

	public void removeFromSuperClasses(IFlexoOntologyClass aClass) {
		superClasses.remove(aClass);
	}

	@Override
	public List<? extends IFlexoOntologyClass> getSubClasses(IFlexoOntology context) {
		// TODO Auto-generated method stub
		return null;
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
		List<XSOntRestriction> returned = new ArrayList<XSOntRestriction>();
		for (XSOntRestriction xsOntRest : restrictions.values()) {
				returned.add(xsOntRest);
			}
		return returned;
	}


	public XSOntRestriction getFeatureAssociationNamed(String name) {
		return restrictions.get(name);
	}
	
}
