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
package org.openflexo.fib.editor.test;

import java.io.File;
import java.util.Vector;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.toolbox.FileResource;

public class TestComponentEDITOR {

	public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(new TestComponentData());
			}

			@Override
			public File getFIBFile() {
				return new FileResource("TestFIB/TestComponent.fib");
			}
		};
		editor.launch();
	}

	public static class TestComponentData {
		public String name;
		public String description;
		public int index;
		public Vector<Foo> someFoo;

		public TestComponentData() {
			name = "name";
			description = "description";
			index = 1;
			someFoo = new Vector<Foo>();
			someFoo.add(new Foo("i", 7));
			someFoo.add(new Foo("am", 2));
			someFoo.add(new Foo("the", 9));
			someFoo.add(new Foo("queen", 12));
		}

	}

	public static class Foo {
		public String fooName;
		public int fooIndex;
		public Vector<Foo2> someFoo2;

		public Foo(String name, int index) {
			fooName = name;
			fooIndex = index;
			someFoo2 = new Vector<Foo2>();
			for (int i = 0; i < index; i++) {
				someFoo2.add(new Foo2(fooName + "_foo2_number_" + i, i));
			}
		}

		@Override
		public String toString() {
			return fooName;
		}
	}

	public static class Foo2 {
		public String foo2Name = "foo2";
		public int foo2Index = 3;

		public Foo2(String name, int index) {
			foo2Name = name;
			foo2Index = index;
		}

		@Override
		public String toString() {
			return foo2Name;
		}
	}
}
