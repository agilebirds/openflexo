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
package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

public class AddClass extends AddConcept {

	private static final Logger logger = Logger.getLogger(AddClass.class.getPackage().getName());

	private String ontologyClassURI = null;

	public AddClass() {
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.AddClass;
	}

	/*@Override
	public List<ClassPatternRole> getAvailablePatternRoles() {
		return getEditionPattern().getPatternRoles(ClassPatternRole.class);
	}*/

	@Override
	public ClassPatternRole getPatternRole() {
		PatternRole superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof ClassPatternRole) {
			return (ClassPatternRole) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	@Override
	public OntologyClass getOntologyClass() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (StringUtils.isNotEmpty(ontologyClassURI)) {
			if (getOntologyLibrary() != null) {
				return getOntologyLibrary().getClass(ontologyClassURI);
			}
		} else {
			if (getPatternRole() instanceof ClassPatternRole) {
				return getPatternRole().getOntologicType();
			}
		}
		return null;
	}

	@Override
	public void setOntologyClass(OntologyClass ontologyClass) {
		if (ontologyClass != null) {
			if (getPatternRole() instanceof ClassPatternRole) {
				if (getPatternRole().getOntologicType().isSuperConceptOf(ontologyClass)) {
					ontologyClassURI = ontologyClass.getURI();
				} else {
					getPatternRole().setOntologicType(ontologyClass);
				}
			} else {
				ontologyClassURI = ontologyClass.getURI();
			}
		} else {
			ontologyClassURI = null;
		}
	}

	public String _getOntologyClassURI() {
		if (getOntologyClass() != null) {
			if (getPatternRole() instanceof ClassPatternRole && getPatternRole().getOntologicType() == getOntologyClass()) {
				// No need to store an overriding type, just use default provided by pattern role
				return null;
			}
			return getOntologyClass().getURI();
		}
		return ontologyClassURI;
	}

	public void _setOntologyClassURI(String ontologyClassURI) {
		this.ontologyClassURI = ontologyClassURI;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ADD_CLASS_INSPECTOR;
	}

	/*@Override
	protected void updatePatternRoleType()
	{
		if (getPatternRole() == null) {
			return;
		}
	}*/

	private ViewPointDataBinding className;

	private BindingDefinition CLASS_NAME = new BindingDefinition("className", String.class, BindingDefinitionType.GET, false);

	public BindingDefinition getClassNameBindingDefinition() {
		return CLASS_NAME;
	}

	public ViewPointDataBinding getClassName() {
		if (className == null) {
			className = new ViewPointDataBinding(this, EditionActionBindingAttribute.className, getClassNameBindingDefinition());
		}
		return className;
	}

	public void setClassName(ViewPointDataBinding className) {
		className.setOwner(this);
		className.setBindingAttribute(EditionActionBindingAttribute.className);
		className.setBindingDefinition(getClassNameBindingDefinition());
		this.className = className;
	}

}
