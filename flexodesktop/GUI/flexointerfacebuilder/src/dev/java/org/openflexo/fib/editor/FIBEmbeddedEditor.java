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
package org.openflexo.fib.editor;

import java.io.File;

public class FIBEmbeddedEditor {

	private File fibFile;
	private Object[] data;

	private class Editor extends FIBAbstractEditor {

		@Override
		public Object[] getData() {
			return FIBEmbeddedEditor.this.getData();
		}

		@Override
		public File getFIBFile() {
			return FIBEmbeddedEditor.this.getFIBFile();
		}

		@Override
		public boolean exitOnDispose() {
			return false;
		}

	}

	public FIBEmbeddedEditor(File aFile, Object object) {
		super();
		this.fibFile = aFile;
		this.data = new Object[1];
		this.data[0] = object;
		FIBAbstractEditor.init(new Editor());
	}

	public Object[] getData() {
		return data;
	}

	public File getFIBFile() {
		return fibFile;
	}
}
