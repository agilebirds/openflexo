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
package org.openflexo.foundation.view.diagram;

import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;

/**
 * This class defines and implements the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
public class DiagramTechnologyAdapter extends VirtualModelTechnologyAdapter {

	private static final Logger logger = Logger.getLogger(DiagramTechnologyAdapter.class.getPackage().getName());

	public DiagramTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Openflexo diagram";
	}

	@Override
	public DiagramModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new DiagramModelSlot(viewPoint, this);
	}

	@Override
	public DiagramModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new DiagramModelSlot((DiagramSpecification) virtualModel, this);
	}

}
