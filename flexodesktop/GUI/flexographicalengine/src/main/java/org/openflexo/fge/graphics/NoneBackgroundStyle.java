package org.openflexo.fge.graphics;

import java.awt.Paint;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundStyleType;

public class NoneBackgroundStyle extends BackgroundStyle {
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