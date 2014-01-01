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
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a VirtualModelInstance
 * 
 * @author sguerin
 * 
 */
public class FIBVirtualModelInstanceSelector extends FIBFlexoObjectSelector<VirtualModelInstanceResource> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBVirtualModelInstanceSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/VirtualModelInstanceSelector.fib");

	private ViewLibrary viewLibrary;
	private View view;
	private VirtualModel virtualModel;

	public FIBVirtualModelInstanceSelector(VirtualModelInstanceResource editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		viewLibrary = null;
		view = null;
		virtualModel = null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<VirtualModelInstanceResource> getRepresentedType() {
		return VirtualModelInstanceResource.class;
	}

	@Override
	public String renderedString(VirtualModelInstanceResource editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public ViewLibrary getViewLibrary() {
		return viewLibrary;
	}

	@CustomComponentParameter(name = "viewLibrary", type = CustomComponentParameter.Type.OPTIONAL)
	public void setViewLibrary(ViewLibrary viewLibrary) {
		this.viewLibrary = viewLibrary;
	}

	public View getView() {
		return view;
	}

	@CustomComponentParameter(name = "view", type = CustomComponentParameter.Type.OPTIONAL)
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Return virtual model which selected VirtualModelInstance should conform
	 * 
	 * @return
	 */
	public VirtualModel getVirtualModel() {
		return virtualModel;
	}

	/**
	 * Sets virtual model which selected VirtualModelInstance should conform
	 * 
	 * @param virtualModel
	 */
	@CustomComponentParameter(name = "virtualModel", type = CustomComponentParameter.Type.OPTIONAL)
	public void setVirtualModel(VirtualModel virtualModel) {
		this.virtualModel = virtualModel;
	}

	public FlexoObject getRootObject() {
		if (getView() != null) {
			return (FlexoObject) getView().getResource();
		} else if (getViewLibrary() != null) {
			return getViewLibrary();
		} else {
			return getProject();
		}
	}

	@Override
	protected boolean isAcceptableValue(Object o) {
		if (o instanceof VirtualModelInstanceResource) {
			VirtualModelInstance vmi = ((VirtualModelInstanceResource) o).getVirtualModelInstance();
			if (getVirtualModel() != null) {
				return vmi.getVirtualModel() == getVirtualModel();
			}
			return true;
		}
		return super.isAcceptableValue(o);
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
				FIBVirtualModelSelector selector = new FIBVirtualModelSelector(null);
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