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

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyProperty;

public abstract class PropertyParameter extends EditionSchemeParameter {

	private String domainURI;
	private String parentPropertyURI;

	@Override
	public Type getType() {
		return OntologyProperty.class;
	};

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public OntologyClass getDomain() {
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getDomainURI());
	}

	public void setDomain(OntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public OntologyProperty getParentProperty() {
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getProperty(_getParentPropertyURI());
	}

	public void setParentProperty(OntologyProperty ontologyProperty) {
		parentPropertyURI = (ontologyProperty != null ? ontologyProperty.getURI() : null);
	}

}
