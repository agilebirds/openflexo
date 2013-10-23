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
package org.openflexo.fge.control.tools;

import java.util.List;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link ShadowStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedShadowStyle extends InspectedStyle<ShadowStyle> implements ShadowStyle {

	public InspectedShadowStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, controller != null ? controller.getFactory().makeDefaultShadowStyle() : null);
	}

	@Override
	public List<ShapeNode<?>> getSelection() {
		return getController().getSelectedShapes();
	}

	@Override
	public ShadowStyle getStyle(DrawingTreeNode<?, ?> node) {
		if (node instanceof ShapeNode) {
			return ((ShapeNode<?>) node).getShadowStyle();
		}
		return null;
	}

	@Override
	public boolean getDrawShadow() {
		return getPropertyValue(ShadowStyle.DRAW_SHADOW);
	}

	@Override
	public void setDrawShadow(boolean aFlag) {
		setPropertyValue(ShadowStyle.DRAW_SHADOW, aFlag);
	}

	@Override
	public int getShadowDarkness() {
		return getPropertyValue(ShadowStyle.SHADOW_DARKNESS);
	}

	@Override
	public void setShadowDarkness(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_DARKNESS, aValue);
	}

	@Override
	public int getShadowDepth() {
		return getPropertyValue(ShadowStyle.SHADOW_DEPTH);
	}

	@Override
	public void setShadowDepth(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_DEPTH, aValue);
	}

	@Override
	public int getShadowBlur() {
		return getPropertyValue(ShadowStyle.SHADOW_BLUR);
	}

	@Override
	public void setShadowBlur(int aValue) {
		setPropertyValue(ShadowStyle.SHADOW_BLUR, aValue);
	}

}
