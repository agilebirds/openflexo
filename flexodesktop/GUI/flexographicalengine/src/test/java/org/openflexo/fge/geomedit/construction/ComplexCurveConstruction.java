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
package org.openflexo.fge.geomedit.construction;

import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;

public abstract class ComplexCurveConstruction extends GeometricConstruction<FGEComplexCurve> {

	public final FGEGeneralShape getCurve() {
		return getData();
	}

	@Override
	protected abstract FGEComplexCurve computeData();

	private Closure closure = Closure.OPEN_NOT_FILLED;

	public Closure getClosure() {
		return closure;
	}

	public void setClosure(Closure aClosure) {
		this.closure = aClosure;
		setModified();
	}

}
