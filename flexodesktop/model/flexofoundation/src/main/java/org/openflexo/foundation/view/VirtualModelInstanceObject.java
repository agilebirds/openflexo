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
package org.openflexo.foundation.view;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * A {@link VirtualModelInstanceObject} an abstract run-time concept (instance) for an object "living" in a {@link VirtualModelInstance}
 * (instanceof a {@link VirtualModel})
 * 
 * @author sylvain
 * 
 */
public abstract class VirtualModelInstanceObject extends ViewObject {

	private static final Logger logger = Logger.getLogger(VirtualModelInstanceObject.class.getPackage().getName());

	public VirtualModelInstanceObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Return the {@link VirtualModelInstance} where this object is declared and living
	 * 
	 * @return
	 */
	public abstract VirtualModelInstance getVirtualModelInstance();

	/**
	 * Return the {@link View} where this object is declared and living
	 * 
	 * @return
	 */
	@Override
	public View getView() {
		if (getVirtualModelInstance() != null) {
			return getVirtualModelInstance().getView();
		}
		return null;
	}
}
