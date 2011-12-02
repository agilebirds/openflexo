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
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a FlexoRole
 * 
 * @author sguerin
 * 
 */
public class FIBProcessSelector extends FIBModelObjectSelector<FlexoProcess> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBProcessSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ProcessSelector.fib");

	public FIBProcessSelector(FlexoProcess editedObject) {
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
	protected Enumeration<FlexoProcess> getAllSelectableValues() {
		if (getProject() != null) {
			return getProject().getWorkflow().getSortedProcesses();
		}
		return null;
	}

	@Override
	protected boolean isAcceptableValue(FlexoModelObject o) {
		return super.isAcceptableValue(o);
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<FlexoProcess> getRepresentedType() {
		return FlexoProcess.class;
	}

	@Override
	public String renderedString(FlexoProcess editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public static class CustomFIBController extends FIBModelObjectSelector.CustomFIBController {
		public CustomFIBController(FIBComponent component, FIBProcessSelector selector) {
			super(component, selector);
		}

		public Icon getIconForProcess(FlexoProcess process) {
			return WKFIconLibrary.PROCESS_ICON;
		}

	}

	/*public static void main(String[] args)
	{
		testSelector(new FIBProcessSelector(null));
		
	}*/
}