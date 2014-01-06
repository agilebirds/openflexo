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

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaInteractiveViewer;

/**
 * Implementation of {@link TextStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedTextStyle extends InspectedStyle<TextStyle> implements TextStyle {

	public InspectedTextStyle(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, controller != null ? controller.getFactory().makeDefaultTextStyle() : null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects(ShapeNode.class, ConnectorNode.class, GeometricNode.class);
	}

	@Override
	public TextStyle getStyle(DrawingTreeNode<?, ?> node) {
		return node.getTextStyle();
	}

	@Override
	public Color getColor() {
		return getPropertyValue(TextStyle.COLOR);
	}

	@Override
	public void setColor(Color aColor) {
		setPropertyValue(TextStyle.COLOR, aColor);
	}

	@Override
	public Color getBackgroundColor() {
		return getPropertyValue(TextStyle.BACKGROUND_COLOR);
	}

	@Override
	public void setBackgroundColor(Color aColor) {
		setPropertyValue(TextStyle.BACKGROUND_COLOR, aColor);
	}

	@Override
	public Font getFont() {
		return getPropertyValue(TextStyle.FONT);
	}

	@Override
	public void setFont(Font aFont) {
		setPropertyValue(TextStyle.FONT, aFont);
	}

	@Override
	public int getOrientation() {
		try {
			return getPropertyValue(TextStyle.ORIENTATION);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("OK, je vois que ca chie la");
			System.out.println("En pas a pas");
			return getPropertyValue(TextStyle.ORIENTATION);
		}
	}

	@Override
	public void setOrientation(int anOrientation) {
		setPropertyValue(TextStyle.ORIENTATION, anOrientation);
	}

	@Override
	public boolean getIsBackgroundColored() {
		return getPropertyValue(TextStyle.IS_BACKGROUND_COLORED);
	}

	@Override
	public void setIsBackgroundColored(boolean aFlag) {
		setPropertyValue(TextStyle.IS_BACKGROUND_COLORED, aFlag);
	}

}
