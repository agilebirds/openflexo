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

import org.openflexo.fge.notifications.FGENotification;

public class ShadowStyleImpl extends FGEStyleImpl implements ShadowStyle {

	private boolean drawShadow;
	private int shadowDarkness;
	private int shadowDepth;
	private int shadowBlur;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShadowStyleImpl() {
		drawShadow = true;
		shadowDarkness = FGEConstants.DEFAULT_SHADOW_DARKNESS;
		shadowDepth = FGEConstants.DEFAULT_SHADOW_DEEP;
		shadowBlur = FGEConstants.DEFAULT_SHADOW_BLUR;
	}

	@Deprecated
	private static ShadowStyleImpl makeNone() {
		ShadowStyleImpl returned = new ShadowStyleImpl();
		returned.drawShadow = false;
		returned.shadowDepth = 0;
		return returned;
	}

	@Deprecated
	private static ShadowStyleImpl makeDefault() {
		return new ShadowStyleImpl();
	}

	@Override
	public boolean getDrawShadow() {
		return drawShadow;
	}

	@Override
	public void setDrawShadow(boolean aFlag) {
		if (requireChange(this.drawShadow, aFlag)) {
			boolean oldValue = drawShadow;
			this.drawShadow = aFlag;
			setChanged();
			notifyObservers(new FGENotification(Parameters.drawShadow, oldValue, aFlag));
		}
	}

	@Override
	public int getShadowDarkness() {
		return shadowDarkness;
	}

	@Override
	public void setShadowDarkness(int aValue) {
		if (requireChange(this.shadowDarkness, aValue)) {
			int oldShadowDarkness = shadowDarkness;
			shadowDarkness = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowDarkness, oldShadowDarkness, aValue));
		}
	}

	@Override
	public int getShadowDepth() {
		return shadowDepth;
	}

	@Deprecated
	public int getShadowDeep() {
		return getShadowDepth();
	}

	@Override
	public void setShadowDepth(int aValue) {
		if (requireChange(this.shadowDepth, aValue)) {
			int oldShadowDeep = shadowDepth;
			shadowDepth = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowDepth, oldShadowDeep, aValue));
		}
	}

	@Override
	public int getShadowBlur() {
		return shadowBlur;
	}

	@Override
	public void setShadowBlur(int aValue) {
		if (requireChange(this.shadowBlur, aValue)) {
			int oldShadowBlur = shadowBlur;
			shadowBlur = aValue;
			setChanged();
			notifyObservers(new FGENotification(Parameters.shadowBlur, oldShadowBlur, aValue));
		}
	}

	@Override
	public ShadowStyleImpl clone() {
		try {
			return (ShadowStyleImpl) super.clone();
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