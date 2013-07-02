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
import org.openflexo.fge.impl.ShadowStyleImpl;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represent shadow properties which should be applied to a graphical representation
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ShadowStyleImpl.class)
@XMLElement(xmlTag = "ShadowStyle")
public interface ShadowStyle extends FGEStyle {

	public static final String NONE_CONFIGURATION = "none";
	public static final String DEFAULT_CONFIGURATION = "default";

	public static final String DRAW_SHADOW = "drawShadow";
	public static final String SHADOW_DARKNESS = "shadowDarkness";
	public static final String SHADOW_DEPTH = "shadowDepth";
	public static final String SHADOW_BLUR = "shadowBlur";

	public static enum Parameters implements GRParameter {
		drawShadow, shadowDarkness, shadowDepth, shadowBlur
	}

	@Getter(value = DRAW_SHADOW, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawShadow();

	@Setter(value = DRAW_SHADOW)
	public void setDrawShadow(boolean aFlag);

	@Getter(value = SHADOW_DARKNESS, defaultValue = "150")
	@XMLAttribute
	public int getShadowDarkness();

	@Setter(value = SHADOW_DARKNESS)
	public void setShadowDarkness(int aValue);

	@Getter(value = SHADOW_DEPTH, defaultValue = "2")
	@XMLAttribute
	public int getShadowDepth();

	@Setter(value = SHADOW_DEPTH)
	public void setShadowDepth(int aValue);

	@Getter(value = SHADOW_BLUR, defaultValue = "4")
	@XMLAttribute
	public int getShadowBlur();

	@Setter(value = SHADOW_BLUR)
	public void setShadowBlur(int aValue);

	public ShadowStyle clone();

}