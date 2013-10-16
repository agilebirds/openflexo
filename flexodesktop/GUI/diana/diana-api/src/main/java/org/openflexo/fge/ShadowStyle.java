/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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
@XMLElement(xmlTag = "ShadowStyle")
public interface ShadowStyle extends FGEStyle {

	public static final String NONE_CONFIGURATION = "none";
	public static final String DEFAULT_CONFIGURATION = "default";

	@PropertyIdentifier(type = Boolean.class)
	public static final String DRAW_SHADOW_KEY = "drawShadow";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_DARKNESS_KEY = "shadowDarkness";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_DEPTH_KEY = "shadowDepth";
	@PropertyIdentifier(type = Integer.class)
	public static final String SHADOW_BLUR_KEY = "shadowBlur";

	public static GRParameter<Boolean> DRAW_SHADOW = GRParameter.getGRParameter(ShadowStyle.class, DRAW_SHADOW_KEY, Boolean.class);
	public static GRParameter<Integer> SHADOW_DARKNESS = GRParameter.getGRParameter(ShadowStyle.class, SHADOW_DARKNESS_KEY, Integer.class);
	public static GRParameter<Integer> SHADOW_DEPTH = GRParameter.getGRParameter(ShadowStyle.class, SHADOW_DEPTH_KEY, Integer.class);
	public static GRParameter<Integer> SHADOW_BLUR = GRParameter.getGRParameter(ShadowStyle.class, SHADOW_BLUR_KEY, Integer.class);

	/*public static enum Parameters implements GRParameter {
		drawShadow, shadowDarkness, shadowDepth, shadowBlur
	}*/

	@Getter(value = DRAW_SHADOW_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDrawShadow();

	@Setter(value = DRAW_SHADOW_KEY)
	public void setDrawShadow(boolean aFlag);

	@Getter(value = SHADOW_DARKNESS_KEY, defaultValue = "150")
	@XMLAttribute
	public int getShadowDarkness();

	@Setter(value = SHADOW_DARKNESS_KEY)
	public void setShadowDarkness(int aValue);

	@Getter(value = SHADOW_DEPTH_KEY, defaultValue = "2")
	@XMLAttribute
	public int getShadowDepth();

	@Setter(value = SHADOW_DEPTH_KEY)
	public void setShadowDepth(int aValue);

	@Getter(value = SHADOW_BLUR_KEY, defaultValue = "4")
	@XMLAttribute
	public int getShadowBlur();

	@Setter(value = SHADOW_BLUR_KEY)
	public void setShadowBlur(int aValue);

	// public ShadowStyle clone();

}