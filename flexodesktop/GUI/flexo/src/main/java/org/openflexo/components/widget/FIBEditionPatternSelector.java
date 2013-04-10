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
package org.openflexo.components.widget;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select an EditionPattern
 * 
 * @author sguerin
 * 
 */
public class FIBEditionPatternSelector extends FIBModelObjectSelector<EditionPattern> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBEditionPatternSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/EditionPatternSelector.fib");

	public FIBEditionPatternSelector(EditionPattern editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		viewPointLibrary = null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<EditionPattern> getRepresentedType() {
		return EditionPattern.class;
	}

	@Override
	public String renderedString(EditionPattern editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	private ViewPointLibrary viewPointLibrary;

	public ViewPointLibrary getViewPointLibrary() {
		return viewPointLibrary;
	}

	@CustomComponentParameter(name = "viewPointLibrary", type = CustomComponentParameter.Type.MANDATORY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary) {
		this.viewPointLibrary = viewPointLibrary;
	}

	private ViewPoint viewPoint;

	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	@CustomComponentParameter(name = "viewPoint", type = CustomComponentParameter.Type.OPTIONAL)
	public void setViewPoint(ViewPoint viewPoint) {
		this.viewPoint = viewPoint;
	}

	private VirtualModel<?> virtualModel;

	public VirtualModel<?> getVirtualModel() {
		return virtualModel;
	}

	@CustomComponentParameter(name = "virtualModel", type = CustomComponentParameter.Type.OPTIONAL)
	public void setVirtualModel(VirtualModel<?> virtualModel) {
		this.virtualModel = virtualModel;
	}

	public FlexoObject getRootObject() {
		if (getVirtualModel() != null) {
			return getVirtualModel();
		} else if (getViewPoint() != null) {
			return getViewPoint();
		} else {
			return getViewPointLibrary();
		}
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				TestApplicationContext testApplicationContext = new TestApplicationContext(new FileResource(
						"src/test/resources/TestResourceCenter"));
				FIBEditionPatternSelector selector = new FIBEditionPatternSelector(null);
				selector.setViewPointLibrary(testApplicationContext.getViewPointLibrary());
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController(component);
			}
		};
		editor.launch();
	}*/

}