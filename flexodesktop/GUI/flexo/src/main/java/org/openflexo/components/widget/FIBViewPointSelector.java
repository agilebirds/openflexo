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
import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a ViewPoint while browsing in ViewPoint library
 * 
 * @author sguerin
 * 
 */
public class FIBViewPointSelector extends FIBModelObjectSelector<ViewPoint> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBViewPointSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ViewPointSelector.fib");

	public FIBViewPointSelector(ViewPoint editedObject) {
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
	public Class<ViewPoint> getRepresentedType() {
		return ViewPoint.class;
	}

	@Override
	public String renderedString(ViewPoint editedObject) {
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

	/**
	 * This method must be implemented if we want to implement completion<br>
	 * Completion will be performed on that selectable values<br>
	 * Return all viewpoints of this library
	 */
	@Override
	protected Collection<ViewPoint> getAllSelectableValues() {
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getViewPoints();
		}
		return null;
	}

	// Please uncomment this for a live test
	// Never commit this uncommented since it will not compile on continuous build
	// To have icon, you need to choose "Test interface" in the editor (otherwise, flexo controller is not insanciated in EDIT mode)
	/*public static void main(String[] args) {
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				FlexoResourceCenter resourceCenter = FlexoResourceCenterService.instance().getFlexoResourceCenter();
				FIBViewPointSelector selector = new FIBViewPointSelector(null);
				selector.setViewPointLibrary(resourceCenter.retrieveViewPointLibrary());
				return makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return FIB_FILE;
			}

			@Override
			public FIBController makeNewController(FIBComponent component) {
				return new FlexoFIBController<FIBViewPointSelector>(component);
			}
		};
		editor.launch();
	}*/

}