package org.openflexo.fge.impl;

import java.awt.Paint;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.NoneBackgroundStyle;

public class NoneBackgroundStyleImpl extends BackgroundStyleImpl implements NoneBackgroundStyle {

	@Override
	public Paint getPaint(GraphicalRepresentation gr, double scale) {
		return null;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.NONE;
	}

	@Override
	public String toString() {
		return "BackgroundStyle.NONE";
	}

}
