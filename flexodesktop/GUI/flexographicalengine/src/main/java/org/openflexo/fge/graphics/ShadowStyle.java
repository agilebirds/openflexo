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
package org.openflexo.fge.graphics;

import java.util.Observable;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.xmlcode.XMLSerializable;

public class ShadowStyle extends Observable implements XMLSerializable, Cloneable {

	private boolean drawShadow;
	private int shadowDarkness;
	private int shadowDepth;
	private int shadowBlur;

	public static enum Parameters implements GRParameter {
		drawShadow, shadowDarkness, shadowDepth, shadowBlur
	}

	public ShadowStyle() {
		drawShadow = true;
		shadowDarkness = FGEConstants.DEFAULT_SHADOW_DARKNESS;
		shadowDepth = FGEConstants.DEFAULT_SHADOW_DEEP;
		shadowBlur = FGEConstants.DEFAULT_SHADOW_BLUR;
	}

	public static ShadowStyle makeNone() {
		ShadowStyle returned = new ShadowStyle();
		returned.drawShadow = false;
		returned.shadowDepth = 0;
		return returned;
	}

	public static ShadowStyle makeDefault() {
		return new ShadowStyle();
	}

	public boolean getDrawShadow() {
		return drawShadow;
	}

	public void setDrawShadow(boolean aFlag) {
		if (requireChange(this.drawShadow, aFlag)) {
			boolean oldValue = drawShadow;
			this.drawShadow = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.drawShadow, oldValue, aFlag));
		}
	}

	public int getShadowDarkness() {
		return shadowDarkness;
	}

	public void setShadowDarkness(int aValue) {
		if (requireChange(this.shadowDarkness, aValue)) {
			int oldShadowDarkness = shadowDarkness;
			shadowDarkness = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowDarkness, oldShadowDarkness, aValue));
		}
	}

	public int getShadowDepth() {
		return shadowDepth;
	}

	@Deprecated
	public int getShadowDeep() {
		return getShadowDepth();
	}

	public void setShadowDepth(int aValue) {
		if (requireChange(this.shadowDepth, aValue)) {
			int oldShadowDeep = shadowDepth;
			shadowDepth = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowDepth, oldShadowDeep, aValue));
		}
	}

	public int getShadowBlur() {
		return shadowBlur;
	}

	public void setShadowBlur(int aValue) {
		if (requireChange(this.shadowBlur, aValue)) {
			int oldShadowBlur = shadowBlur;
			shadowBlur = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowBlur, oldShadowBlur, aValue));
		}
	}

	@Override
	public ShadowStyle clone() {
		try {
			return (ShadowStyle) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen, we are clonable
			e.printStackTrace();
			return null;
		}
	}

	private boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

}