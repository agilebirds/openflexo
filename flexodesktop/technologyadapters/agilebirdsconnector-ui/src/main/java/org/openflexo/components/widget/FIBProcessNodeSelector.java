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
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a FlexoRole
 * 
 * @author sguerin
 * 
 */
public class FIBProcessNodeSelector extends FIBFlexoObjectSelector<FlexoProcessNode> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBProcessNodeSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ProcessNodeSelector.fib");

	public FIBProcessNodeSelector(FlexoProcessNode editedObject) {
		super(editedObject);
	}

	@Override
	protected CustomFIBController makeCustomFIBController(FIBComponent fibComponent) {
		return new CustomFIBController(fibComponent, this);
	}

	/**
	 * Override when required
	 */
	@Override
	protected Collection<FlexoProcessNode> getAllSelectableValues() {
		if (getProject() != null) {
			List<FlexoProcessNode> allSelectableValues = new ArrayList<FlexoProcessNode>();
			allSelectableValues.addAll(getProject().getWorkflow().getAllLocalProcessNodes());
			if (getProject().getProjectData() != null) {
				for (FlexoProjectReference ref : getProject().getProjectData().getImportedProjects()) {
					if (ref.getWorkflow() != null) {
						allSelectableValues.addAll(ref.getWorkflow().getAllLocalProcessNodes());
					}
				}
			}
			return allSelectableValues;
		}
		return null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<FlexoProcessNode> getRepresentedType() {
		return FlexoProcessNode.class;
	}

	@Override
	public String renderedString(FlexoProcessNode editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public static class CustomFIBController extends FIBFlexoObjectSelector.SelectorFIBController {
		public CustomFIBController(FIBComponent component, FIBProcessNodeSelector selector) {
			super(component, selector);
		}

		public Icon getIconForProcess(FlexoProcessNode process) {
			if (process.getWorkflow().isCache()) {
				return IconFactory.getImageIcon(WKFIconLibrary.PROCESS_ICON, new IconMarker[] { IconLibrary.IMPORT });
			} else {
				return WKFIconLibrary.PROCESS_ICON;
			}
		}

	}

	/*public static void main(String[] args) {
		testSelector(new FIBProcessSelector(null));
	}*/
}
