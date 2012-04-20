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
package org.openflexo.foundation.viewpoint.inspector;

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObjectProperty;

/**
 * Represents an inspector entry for an ontology object property
 * 
 * @author sylvain
 * 
 */
public class ObjectPropertyInspectorEntry extends PropertyInspectorEntry {

	private String rangeURI;

	@Override
	public Class getDefaultDataClass() {
		return OntologyObjectProperty.class;
	}

	@Override
	public String getWidgetName() {
		return "OntologyPropertySelector";
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String domainURI) {
		this.rangeURI = domainURI;
	}

	public OntologyClass getRange() {
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getRangeURI());
	}

	public void setRange(OntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

}
