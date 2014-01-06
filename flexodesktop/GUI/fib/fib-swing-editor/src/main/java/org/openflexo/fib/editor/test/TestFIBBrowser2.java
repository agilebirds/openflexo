package org.openflexo.fib.editor.test;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Vector;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class TestFIBBrowser2 {

	public static FileResource FIB_FILE = new FileResource("TestFIB/TestBrowser2.fib");

	public static void main(String[] args) {
		final TestClass mainClass = new TestClass("Main");
		final TestMethod method11 = new TestMethod("method1");
		final TestMethod method12 = new TestMethod("method2");
		final TestClass class1 = new TestClass("Class1", method11, method12);
		final TestMethod method21 = new TestMethod("method1Disabled");
		final TestMethod method22 = new TestMethod("method2Disabled");
		final TestMethod method23 = new TestMethod("method3");
		final TestClass class2 = new TestClass("Class2", method21, method22, method23);
		final TestMethod method31 = new TestMethod("method1");
		final TestMethod method32 = new TestMethod("method2Disabled");
		final TestClass class3 = new TestClass("Class3Disabled", method31);
		final TestMethod method51 = new TestMethod("method1");
		final TestClass class5 = new TestClass("Class5Invisible", method51);
		final TestPackage testPackage = new TestPackage("my.package", mainClass, class1, class2, class3, class5);
		final TestMethod method41 = new TestMethod("fooDisabled");
		final TestMethod method42 = new TestMethod("foo2");
		final TestClass class4 = new TestClass("Class4", method41, method42);

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return FIBAbstractEditor.makeArray(testPackage);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}
		};
		editor.addAction("change_name", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class1.setName("new_class_1_name");
			}
		});
		editor.addAction("enable_class3", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.setName("Class3");
			}
		});
		editor.addAction("add_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.addClass(class4);
			}
		});
		editor.addAction("add_method", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class3.addMethod(method32);
			}
		});
		editor.addAction("remove_class", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class4);
			}
		});
		editor.addAction("remove_classes", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPackage.removeClass(class2);
				testPackage.removeClass(class1);
			}
		});
		editor.addAction("make_class5_visible", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				class5.setName("Class5");
			}
		});
		editor.launch();
	}

	public abstract static class TestObject implements HasPropertyChangeSupport {
		private PropertyChangeSupport pcSupport;

		public TestObject() {
			pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	public static class TestPackage extends TestObject {
		public String name;
		public TestClass mainClass;
		public Vector<TestClass> otherClasses;

		public TestPackage(String aName, TestClass mainClass, TestClass... otherClasses) {
			super();
			this.name = aName;
			this.mainClass = mainClass;
			this.otherClasses = new Vector<TestClass>();
			for (TestClass c : otherClasses) {
				this.otherClasses.add(c);
			}
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		public void addClass(TestClass c) {
			this.otherClasses.add(c);
			getPropertyChangeSupport().firePropertyChange("otherClasses", null, null);
		}

		public void removeClass(TestClass c) {
			this.otherClasses.remove(c);
			getPropertyChangeSupport().firePropertyChange("otherClasses", null, null);
		}

		@Override
		public String toString() {
			return "TestPackage[" + name + "]";
		}

		public TestClass createNewClass() {
			TestClass returned = new TestClass("NewClass");
			addClass(returned);
			return returned;
		}
	}

	public static class TestClass extends TestObject {
		private String name;
		public Vector<TestMethod> methods;

		public TestClass(String aName) {
			methods = new Vector<TestMethod>();
			this.name = aName;
		}

		public TestClass(String aName, TestMethod... methods) {
			this(aName);
			for (TestMethod m : methods) {
				addMethod(m);
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		public void addMethod(TestMethod m) {
			methods.add(m);
			getPropertyChangeSupport().firePropertyChange("methods", null, null);
		}

		public void removeMethod(TestMethod m) {
			methods.remove(m);
			methods.add(m);
			getPropertyChangeSupport().firePropertyChange("methods", null, null);
		}

		public TestMethod createNewMethod() {
			TestMethod returned = new TestMethod("NewMethod");
			addMethod(returned);
			return returned;
		}

		public TestMethod createNewDisabledMethod() {
			TestMethod returned = new TestMethod("NewDisabledMethod");
			addMethod(returned);
			return returned;
		}

		public void delete() {
			System.out.println("Suppression !!!");
		}

		public void helloWorld1() {
			System.out.println("Hello World 1 !");
		}

		public void helloWorld2() {
			System.out.println("Hello World 2 !");
		}

		@Override
		public String toString() {
			return "TestClass[" + name + "]";
		}
	}

	public static class TestMethod extends TestObject {
		public String name;

		public TestMethod(String aName) {
			this.name = aName;
		}

		public void setName(String string) {
			String oldName = name;
			name = string;
			getPropertyChangeSupport().firePropertyChange("name", oldName, name);
		}

		@Override
		public String toString() {
			return "TestMethod[" + name + "]";
		}
	}

}
