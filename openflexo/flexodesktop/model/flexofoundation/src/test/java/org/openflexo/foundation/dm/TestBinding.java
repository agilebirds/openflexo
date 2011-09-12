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
package org.openflexo.foundation.dm;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DuplicatePropertyNameException;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.toolbox.FileUtils;


public class TestBinding extends FlexoDMTestCase{

	   public TestBinding(String arg0) {
			super(arg0);
		}

		public void test0Binding()
		{
	        FlexoEditor editor = createProject("BindingTest");
	        FlexoComponentFolder _cf = FlexoTestCase.createFolder("BindingTestFolder", null, editor);
	        IEWOComponent component = FlexoTestCase.createComponent("BindingTestComponent", _cf, AddComponent.ComponentType.OPERATION_COMPONENT, editor);
	        DMProperty testProperty = createProperty(component.getComponentDMEntity(), "testProperty", editor);

	        component.getComponentDMEntity().setBindable(testProperty, true);
	        try {
				testProperty.setName("changedName");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}


	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));

	        component.getComponentDMEntity().setBindable(testProperty, false);

	        assertFalse(component.getComponentDMEntity().isSettable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));

	        try {
				testProperty.setName("changedName2");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}

	        assertFalse(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isSettable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));

	        component.getComponentDMEntity().setMandatory(testProperty, true);
	        try {
				testProperty.setName("changedName3");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}

	        assertTrue(component.getComponentDMEntity().isMandatory(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isSettable(testProperty));

	        component.getComponentDMEntity().setMandatory(testProperty, false);
	        try {
				testProperty.setName("changedName4");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}

	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isSettable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));

	        component.getComponentDMEntity().setSettable(testProperty, true);
	        try {
				testProperty.setName("changedName");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}

	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));

	        try {
				testProperty.setName("changedName2");
			} catch (InvalidNameException e) {
				e.printStackTrace();
				fail();
			} catch (DuplicatePropertyNameException e) {
				e.printStackTrace();
				fail();
			}

	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));

	        saveProject(editor.getProject());
			editor.getProject().close();
	        editor = reloadProject(editor.getProject().getProjectDirectory());
	        _cf = editor.getProject().getFlexoComponentLibrary().getFlexoComponentFolderWithName("BindingTestFolder");
	        component = _cf.getComponentNamed("BindingTestComponent").getWOComponent();
	        assertNotNull(component);
	        testProperty = component.getComponentDMEntity().getProperty("changedName2");
	        assertNotNull(testProperty);

	        component.getComponentDMEntity().setSettable(testProperty, true);

	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));
	        editor.getProject().close();
	        editor = reloadProject(editor.getProject().getProjectDirectory());
	        _cf = editor.getProject().getFlexoComponentLibrary().getFlexoComponentFolderWithName("BindingTestFolder");
	        component = _cf.getComponentNamed("BindingTestComponent").getWOComponent();
	        assertNotNull(component);
	        testProperty = component.getComponentDMEntity().getProperty("changedName2");
	        assertNotNull(testProperty);


	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));





	        component = _cf.getComponentNamed("BindingTestComponent").getWOComponent();
	        assertNotNull(component);
	        testProperty = component.getComponentDMEntity().getProperty("changedName2");
	        assertNotNull(testProperty);
	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertFalse(component.getComponentDMEntity().isMandatory(testProperty));


	        component.getComponentDMEntity().setMandatory(testProperty, true);
	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertTrue(component.getComponentDMEntity().isMandatory(testProperty));

	        saveProject(editor.getProject());
	        editor.getProject().close();
	        editor = reloadProject(editor.getProject().getProjectDirectory());
	        _cf = editor.getProject().getFlexoComponentLibrary().getFlexoComponentFolderWithName("BindingTestFolder");
	        component = _cf.getComponentNamed("BindingTestComponent").getWOComponent();
	        assertNotNull(component);
	        testProperty = component.getComponentDMEntity().getProperty("changedName2");
	        assertNotNull(testProperty);

	        assertTrue(component.getComponentDMEntity().isSettable(testProperty));
	        assertTrue(component.getComponentDMEntity().isBindable(testProperty));
	        assertTrue(component.getComponentDMEntity().isMandatory(testProperty));

	        editor.getProject().close();
	        FileUtils.deleteDir(editor.getProject().getProjectDirectory());
		}

}
