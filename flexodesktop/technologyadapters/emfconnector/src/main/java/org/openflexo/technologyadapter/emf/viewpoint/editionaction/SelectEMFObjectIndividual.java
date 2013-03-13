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
package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.SelectIndividual;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * EMF technology - specific {@link FetchRequest} allowing to retrieve a selection of some {@link EMFObjectIndividual} matching some
 * conditions and a given type.<br>
 * 
 * @author sylvain
 */
public class SelectEMFObjectIndividual extends SelectIndividual<EMFModel, EMFMetaModel, EMFObjectIndividual> {

	private static final Logger logger = Logger.getLogger(SelectEMFObjectIndividual.class.getPackage().getName());

	public SelectEMFObjectIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Class<EMFObjectIndividual> getFetchedType() {
		return EMFObjectIndividual.class;
	}

	@Override
	public List<EMFObjectIndividual> performAction(EditionSchemeAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, List<EMFObjectIndividual> initialContext) {
		// TODO Auto-generated method stub

	}
}
