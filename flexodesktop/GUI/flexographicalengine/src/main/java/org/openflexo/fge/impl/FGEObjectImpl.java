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
package org.openflexo.fge.impl;

import java.util.logging.Logger;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEObject;
import org.openflexo.inspector.DefaultInspectableObject;

public abstract class FGEObjectImpl extends DefaultInspectableObject implements FGEObject {
	private static final Logger logger = Logger.getLogger(FGEObjectImpl.class.getPackage().getName());

	private FGEModelFactory factory;

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(FGEModelFactory factory) {
		this.factory = factory;
	}

	@Override
	@Deprecated
	public String getInspectorName() {
		return null;
	}
}
