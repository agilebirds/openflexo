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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select an EditionPatternInstance.<br>
 * 
 * The scope of searched EPI is either:
 * <ul>
 * <li>the whole project, if {@link FlexoProject} has been set</li>
 * <li>a view, if {@link View} has been set</li>
 * <li>a virtual model instance, if {@link VirtualModelInstance} has been set</li>
 * </ul>
 * 
 * @author sguerin
 * 
 */
public class FIBEditionPatternInstanceSelector extends FIBModelObjectSelector<EditionPatternInstance> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBEditionPatternInstanceSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/EditionPatternInstanceSelector.fib");

	private ViewPointLibrary viewPointLibrary;
	private ViewPoint viewPoint;
	private VirtualModel virtualModel;
	private EditionPattern editionPattern;
	private View view;
	private VirtualModelInstance virtualModelInstance;

	public FIBEditionPatternInstanceSelector(EditionPatternInstance editedObject) {
		super(editedObject);
	}

	@Override
	public void delete() {
		super.delete();
		viewPointLibrary = null;
		viewPoint = null;
		virtualModel = null;
		editionPattern = null;
		view = null;
		virtualModelInstance = null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<EditionPatternInstance> getRepresentedType() {
		return EditionPatternInstance.class;
	}

	@Override
	public String renderedString(EditionPatternInstance editedObject) {
		if (editedObject != null) {
			return editedObject.getStringRepresentation();
		}
		return "";
	}

	public ViewPointLibrary getViewPointLibrary() {
		return viewPointLibrary;
	}

	@CustomComponentParameter(name = "viewPointLibrary", type = CustomComponentParameter.Type.MANDATORY)
	public void setViewPointLibrary(ViewPointLibrary viewPointLibrary) {
		this.viewPointLibrary = viewPointLibrary;
	}

	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	@CustomComponentParameter(name = "viewPoint", type = CustomComponentParameter.Type.OPTIONAL)
	public void setViewPoint(ViewPoint viewPoint) {
		this.viewPoint = viewPoint;
	}

	public VirtualModel getVirtualModel() {
		return virtualModel;
	}

	@CustomComponentParameter(name = "virtualModel", type = CustomComponentParameter.Type.OPTIONAL)
	public void setVirtualModel(VirtualModel virtualModel) {
		this.virtualModel = virtualModel;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	@CustomComponentParameter(name = "editionPattern", type = CustomComponentParameter.Type.OPTIONAL)
	public void setEditionPattern(EditionPattern editionPattern) {
		System.out.println(">>>>>>>>> Sets EditionPattern with " + editionPattern);
		this.editionPattern = editionPattern;
	}

	public FlexoObject getRootObject() {
		if (getEditionPattern() != null) {
			return getEditionPattern();
		} else if (getVirtualModel() != null) {
			return getVirtualModel();
		} else if (getViewPoint() != null) {
			return getViewPoint();
		} else {
			return getViewPointLibrary();
		}
	}

	public List<EditionPatternInstance> getEPInstances(EditionPattern ep) {
		if (getVirtualModelInstance() != null) {
			return getVirtualModelInstance().getEPInstances(ep);
		} else if (getView() != null) {
			List<EditionPatternInstance> returned = new ArrayList<EditionPatternInstance>();
			for (VirtualModelInstance vmi : getView().getVirtualModelInstances()) {
				returned.addAll(vmi.getEPInstances(ep));
			}
			return returned;
		} else if (getProject() != null) {
			List<EditionPatternInstance> returned = new ArrayList<EditionPatternInstance>();
			for (ViewResource vr : getProject().getViewLibrary().getAllResources()) {
				for (VirtualModelInstance vmi : vr.getView().getVirtualModelInstances()) {
					returned.addAll(vmi.getEPInstances(ep));
				}
			}
			return returned;
		}
		return null;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
		System.out.println(">>>>>>>>> Sets view with " + view);
	}

	public VirtualModelInstance getVirtualModelInstance() {
		return virtualModelInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance virtualModelInstance) {
		this.virtualModelInstance = virtualModelInstance;
		System.out.println(">>>>>>>>> Sets VirtualModelInstance with " + virtualModelInstance);
	}

	@Override
	public FlexoProject getProject() {
		return super.getProject();
	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		System.out.println(">>>>>>>>> Sets project with " + project);
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