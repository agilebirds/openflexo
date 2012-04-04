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
import org.openflexo.foundation.ontology.OntologyObjectProperty;

public class ObjectPropertyParameter extends PropertyParameter {

	private String rangeURI;

	@Override
	public Type getType() {
		return OntologyObjectProperty.class;
	};

	@Override
	public WidgetType getWidget() {
		return WidgetType.OBJECT_PROPERTY;
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String rangeURI) {
		this.rangeURI = rangeURI;
	}

	public OntologyClass getRange() {
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getRangeURI());
	}

	public void setRange(OntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

}
