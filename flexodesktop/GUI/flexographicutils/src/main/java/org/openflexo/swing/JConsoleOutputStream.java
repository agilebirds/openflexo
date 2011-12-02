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
package org.openflexo.swing;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

public class JConsoleOutputStream extends OutputStream {

	private JConsole _console;
	private StringBuffer _buffer;
	private Color _color;

	public JConsoleOutputStream(JConsole console, Color color) {
		super();
		if (console == null) {
			throw new IllegalArgumentException("console cannot be null.");
		}
		_console = console;
		_buffer = new StringBuffer();
		if (color == null) {
			_color = Color.BLACK;
		} else {
			_color = color;
		}
	}

	private void resetBuffer() {
		_buffer = new StringBuffer();
	}

	@Override
	public void write(int b) throws IOException {
		if (b == Byte.valueOf(Character.LINE_SEPARATOR) || (char) b == '\n') {
			String out = _buffer.toString().trim();
			if (out.length() > 0) {
				_console.log(out + "\n", _color);
			}
			resetBuffer();
		} else {
			_buffer.append((char) b);
		}

	}

	@Override
	public void flush() throws IOException {
		_console.log(_buffer.toString(), _color);
		resetBuffer();
	}

}
