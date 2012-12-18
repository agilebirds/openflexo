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
package org.openflexo.technologyadapter.xsd;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;

/**
 * Implementation of the ModelSlot class for the XSD/XML technology adapter
 * 
 * @author Luka Le Roux
 * 
 */
public class XSDModelSlot extends FlexoOntologyModelSlot<XMLModel, XSDMetaModel> {

	public XSDModelSlot(ViewPoint viewPoint, XSDTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public XSDModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<XSDTechnologyAdapter> getTechnologyAdapterClass() {
		return XSDTechnologyAdapter.class;
	}

	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		// TODO
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		return super.defaultPatternRoleName(patternRoleClass);
	}

	@Override
	public BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		return null;
	}

}
