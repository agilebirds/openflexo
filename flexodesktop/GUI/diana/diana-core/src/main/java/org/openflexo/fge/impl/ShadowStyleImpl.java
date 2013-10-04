package org.openflexo.fge.impl;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.notifications.FGENotification;

public class ShadowStyleImpl extends FGEStyleImpl implements ShadowStyle {
	private boolean drawShadow;
	private int shadowDarkness;
	private int shadowDepth;
	private int shadowBlur;

	public ShadowStyleImpl() {
		drawShadow = true;
		shadowDarkness = FGEConstants.DEFAULT_SHADOW_DARKNESS;
		shadowDepth = FGEConstants.DEFAULT_SHADOW_DEEP;
		shadowBlur = FGEConstants.DEFAULT_SHADOW_BLUR;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private static ShadowStyleImpl makeNone() {
		ShadowStyleImpl returned = new ShadowStyleImpl();
		returned.drawShadow = false;
		returned.shadowDepth = 0;
		return returned;
	}

	@SuppressWarnings("unused")
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
			notifyObservers(new FGENotification(DRAW_SHADOW, oldValue, aFlag));
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
			notifyObservers(new FGENotification(SHADOW_DARKNESS, oldShadowDarkness, aValue));
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
			notifyObservers(new FGENotification(SHADOW_DEPTH, oldShadowDeep, aValue));
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
			notifyObservers(new FGENotification(SHADOW_BLUR, oldShadowBlur, aValue));
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
