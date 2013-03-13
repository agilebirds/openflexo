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

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

/**
 * Generic {@link FetchRequest} allowing to retrieve a selection of some individuals matching some conditions and a given type.<br>
 * This action is technology-specific and must be redefined in a given technology
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 * @param <T>
 */
public abstract class SelectIndividual<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, T extends IFlexoOntologyIndividual>
		extends FetchRequest<M, MM, T> {

	protected static final Logger logger = FlexoLogger.getLogger(SelectIndividual.class.getPackage().getName());

	private String typeURI = null;

	public SelectIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public abstract Class<T> getFetchedType();

	public IFlexoOntologyClass getType() {
		if (StringUtils.isNotEmpty(typeURI)) {
			return getVirtualModel().getOntologyClass(typeURI);
		}
		return null;
	}

	public void setType(IFlexoOntologyClass ontologyClass) {
		if (ontologyClass != null) {
			typeURI = ontologyClass.getURI();
		} else {
			typeURI = null;
		}
	}

	public String _getOntologyClassURI() {
		if (getType() != null) {
			return getType().getURI();
		}
		return typeURI;
	}

	public void _setOntologyClassURI(String ontologyClassURI) {
		this.typeURI = ontologyClassURI;
	}

}
