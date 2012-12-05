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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;

public class XSOntClass extends AbstractXSOntObject implements IFlexoOntologyClass, XSOntologyURIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntClass.class.getPackage()
			.getName());

	private final List<XSOntClass> superClasses = new ArrayList<XSOntClass>();

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

	@Override
	public Set<XSOntClass> getAllSuperClasses() {
		Set<XSOntClass> result = new HashSet<XSOntClass>();
		result.addAll(getSuperClasses());
		for (XSOntClass c : getSuperClasses()) {
			result.addAll(c.getAllSuperClasses());
		}
		return result;
	}

	@Override
	public Object addSuperClass(IFlexoOntologyClass aClass) {
		if (aClass instanceof XSOntClass == false) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Class " + aClass + " is not a XSOntClass");
			}
			return null;
		}
		superClasses.add((XSOntClass) aClass);
		return null;
	}

	@Override
	public boolean isNamedClass() {
		return StringUtils.isNotEmpty(getURI());
	}

	@Override
	public boolean isThing() {
		return isNamedClass() && getURI().equals(XS_THING_URI);
	}

	@Override
	public String getDisplayableDescription() {
		// TODO tell where it's from (element/complex type)
		return getName();
	}

	@Override
	public boolean isOntologyClass() {
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "XSD_ontology_class";
	}

	@Override
	public String getInspectorName() {
		if (getIsReadOnly()) {
			return Inspectors.VE.ONTOLOGY_CLASS_READ_ONLY_INSPECTOR;
		} else {
			return Inspectors.VE.ONTOLOGY_CLASS_INSPECTOR;
		}
	}

}
