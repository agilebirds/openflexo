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
package org.openflexo.foundation.wkf;

import java.util.Vector;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;


public class FlexoImportedProcessLibrary extends TemporaryFlexoModelObject {

	private FlexoWorkflow workflow;
	
	public FlexoImportedProcessLibrary(FlexoWorkflow workflow) {
		
		this.workflow = workflow; 
	}

	public FlexoWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public FlexoProject getProject() {
		return workflow.getProject();
	}
	
	@Override
	public String getClassNameKey() {
		return "imported_process_library";
	}
	
	public int size() {
		if (getWorkflow()!=null)
			return getWorkflow().getImportedProcesses().size();
		return 0;
	}
	
	public Vector<FlexoProcess> getImportedProcesses() {
		return getWorkflow().getImportedProcesses();
	}
}
