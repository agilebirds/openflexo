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
package org.openflexo.icon;

import javax.swing.ImageIcon;

public class IconMarker {

	private ImageIcon _image;
	private int _pX;
	private int _pY;
	private long _id;

	private static long _next = 1;

	public IconMarker(ImageIcon image, int pX, int pY) {
		super();
		_id = _next;
		_next = _next * 2;
		this._image = image;
		this._pX = pX;
		this._pY = pY;
	}

	public int getPX() {
		return _pX;
	}

	public int getPY() {
		return _pY;
	}

	public ImageIcon getImage() {
		return _image;
	}

	public long getID() {
		return _id;
	}

	public IconMarker clone(int x, int y) {
		return new IconMarker(_image, x, y);
	}
}
