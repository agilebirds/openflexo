package org.openflexo.fge.impl;

import org.openflexo.fge.NoneBackgroundStyle;

public abstract class NoneBackgroundStyleImpl extends BackgroundStyleImpl implements NoneBackgroundStyle {

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.NONE;
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.NONE";
	}*/

}
