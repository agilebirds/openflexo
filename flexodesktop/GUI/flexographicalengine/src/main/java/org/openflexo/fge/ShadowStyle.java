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
package org.openflexo.fge;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represent shadow properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ShadowStyleImpl.class)
public interface ShadowStyle extends FGEStyle {

	public static enum Parameters implements GRParameter {
		drawShadow, shadowDarkness, shadowDepth, shadowBlur
	}

	public boolean getDrawShadow();

	public void setDrawShadow(boolean aFlag);

	public int getShadowDarkness();

	public void setShadowDarkness(int aValue);

	public int getShadowDepth();

	@Deprecated
	public int getShadowDeep();

	public void setShadowDepth(int aValue);

	public int getShadowBlur();

	public void setShadowBlur(int aValue);

	public ShadowStyle clone();

}