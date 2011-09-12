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
package org.openflexo.foundation.imported;

import java.util.Vector;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.ws.client.PPMWebService.PPMProcess;


public class FlexoImportedProcessLibraryDelta {
	
	public interface DeltaVisitor {
		public void visit(ProcessDelta process);
	}
	
	public class ProcessDelta {
		/**
		 * Various states:
		 * 1. Classical one, fiProcess and ppmProcess are both not null: it means that fiProcess was imported and ppmProcess is its refreshed version (either updated or not)
		 * 2. fiProcess is null but not ppmProcess: it means that there is a new process
		 * 3. fiProcess is not null but ppmProcess is null: it means that the process has been deleted on the server
		 * 4. fiProcess and ppmProcess are null: it means that this delta is the dummy root delta which maps the imported process library 
		 */
		private FlexoProcess fiProcess;
		private PPMProcess ppmProcess;
		
		private ProcessDelta parent;
		private Vector<ProcessDelta> subProcesses;
		private DeltaStatus status;
		
		public ProcessDelta(FlexoProcess fiProcess, PPMProcess ppmProcess) {
			this.fiProcess = fiProcess;
			this.ppmProcess = ppmProcess;
			subProcesses = new Vector<ProcessDelta>();
		}
		
		public void addToSubProcesses(ProcessDelta processDelta) {
			subProcesses.add(processDelta);
			// We set a parent delta, only if we are not the dummy root delta
			if (!isDummyRoot())
				processDelta.parent = this;
		}

		/**
		 * @return
		 */
		public boolean isDummyRoot() {
			return fiProcess==null && ppmProcess==null;
		}
		
		public void visit(DeltaVisitor visitor) {
			visit(visitor, false);
		}
		
		public void visit(DeltaVisitor visitor, boolean isTopDown) {
			for (ProcessDelta delta : subProcesses) {
				if (!isTopDown) {
					// First we visit the children, then the father
					delta.visit(visitor,isTopDown);
				}
				visitor.visit(delta);
				if (isTopDown) {
					// We visit the children after their father
					delta.visit(visitor,isTopDown);
				}
			}
		}

		public PPMProcess getPPMProcess() {
			return ppmProcess;
		}

		public FlexoProcess getFiProcess() {
			return fiProcess;
		}

		public DeltaStatus getStatus() {
			return status;
		}
		
		public void setStatus(DeltaStatus status) {
			this.status = status;
		}

		public ProcessDelta getParent() {
			return parent;
		}
	}

	// This is the dummy root delta which actually maps the imported process library.
	private ProcessDelta delta;

	public FlexoImportedProcessLibraryDelta(FlexoWorkflow library, PPMProcess[] updatedProcesses) {
		delta = computeDiff(null, null, library.getImportedProcesses(), updatedProcesses);
	}
	
	public void visit(DeltaVisitor visitor) {
		delta.visit(visitor);
	}
	
	public void visit(DeltaVisitor visitor, boolean isTopDown) {
		delta.visit(visitor, isTopDown);
	}
	
	private ProcessDelta computeDiff(FlexoProcess fiProcess, PPMProcess current, Vector<FlexoProcess> originalChildren, PPMProcess[] newChildren) {
		// New delta
		ProcessDelta delta = new ProcessDelta(fiProcess, current);
		
		// Create a copy of vectors
		Vector<FlexoProcess> originalChildrenCopy;
		if (originalChildren!=null)
			originalChildrenCopy = new Vector<FlexoProcess>(originalChildren);
		else
			originalChildrenCopy = new Vector<FlexoProcess>();
		
		// Iterate on the new list
		if (newChildren != null) {
			for (PPMProcess p : newChildren) {

				// Lookup in the processes already imported
				FlexoProcess previous = FlexoModelObject.getObjectWithURI(originalChildrenCopy, p.getUri());

				// 1. previous is null-->new process
				if (previous == null) {
					ProcessDelta computedDiff = computeDiff(null, p, null, p.getSubProcesses());
					computedDiff.setStatus(DeltaStatus.NEW);
					delta.addToSubProcesses(computedDiff);
				} else {
					// We remove it from the copy of the original vector
					originalChildrenCopy.remove(previous);
					ProcessDelta computedDiff = computeDiff(previous, p, previous.getSubProcesses(), p.getSubProcesses());
					// 2. previous is not null and process has not changed
					if (previous.isEquivalentTo(p, true))
						computedDiff.setStatus(DeltaStatus.UNCHANGED);
					// 3. previous is not null but process has changed
					else
						computedDiff.setStatus(DeltaStatus.UPDATED);
					delta.addToSubProcesses(computedDiff);
				}
			}
		}
		// 4. All processes left in the copy of the original have been deleted
		for(FlexoProcess fip:originalChildrenCopy) {
			ProcessDelta computedDiff = computeDiff(fip, null, fip.getSubProcesses(), new PPMProcess[0]);
			computedDiff.setStatus(DeltaStatus.DELETED);
			delta.addToSubProcesses(computedDiff);
		}
		return delta;
	}
	
}
