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
package org.openflexo.foundation.imported.action;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoRemoteException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.gen.FlexoProcessImageBuilder;
import org.openflexo.foundation.gen.FlexoProcessImageNotificationCenter;
import org.openflexo.foundation.imported.DeltaStatus;
import org.openflexo.foundation.imported.FlexoImportedProcessLibraryDelta;
import org.openflexo.foundation.imported.FlexoImportedProcessLibraryDelta.DeltaVisitor;
import org.openflexo.foundation.imported.FlexoImportedProcessLibraryDelta.ProcessDelta;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.FlexoImportedProcessLibrary;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.PPMProcess;

public class RefreshImportedProcessAction extends RefreshImportedObjectAction<RefreshImportedProcessAction,FlexoModelObject,FlexoModelObject> {

	protected static final Logger logger = FlexoLogger.getLogger(RefreshImportedProcessAction.class.getPackage().getName());

	public static final FlexoActionType<RefreshImportedProcessAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<RefreshImportedProcessAction, FlexoModelObject, FlexoModelObject>("refresh_imported_processes"){

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object!=null;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object!=null && object.getProject().getWorkflow().getImportedProcessLibrary()!=null;
		}

		@Override
		public RefreshImportedProcessAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new RefreshImportedProcessAction(focusedObject,globalSelection,editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoWorkflow.class);
		FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
		FlexoModelObject.addActionForClass(actionType, FlexoImportedProcessLibrary.class);
	}

	protected RefreshImportedProcessAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private FlexoImportedProcessLibraryDelta libraryDelta;

	private RefreshProcessDeltaVisitor visitor;

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProject project = getFocusedObject().getProject();
		final FlexoWorkflow lib = project.getFlexoWorkflow();
		Vector<FlexoProcess> processes = lib.getImportedProcesses();
		String[] uris = new String[processes.size()];
		int i = 0;
		for (FlexoProcess process : processes) {
			uris[i] = process.getURI();
			i++;
		}
		PPMProcess[] updatedProcesses;
		try {
			updatedProcesses = getWebService().refreshProcesses(getLogin(), getMd5Password(), uris);
		} catch (RemoteException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.log(Level.WARNING,"Remote exception: "+e.getMessage(),e);
			throw new FlexoRemoteException(null,e);
		}
		if (updatedProcesses!=null) {
			libraryDelta = new FlexoImportedProcessLibraryDelta(lib,updatedProcesses);
			visitor = new RefreshProcessDeltaVisitor(lib);
			libraryDelta.visit(visitor,true);
			
			FlexoProcessImageBuilder.startBackgroundDownloadOfSnapshots(visitor.getProcessToKeep(), getWebService(), getLogin(), getMd5Password());
			
			
			for(FlexoProcess process:visitor.getProcessToDelete()) {
				if (visitor.getProcessToKeep().contains(process)) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Visitor reported process "+process.getName()+" to be kept and deleted! Keeping it.");
					continue;
				}
				if (process.isTopLevelProcess()) {
					process.markAsDeletedOnServer();
				} else {
					if (visitor.getProcessToDelete().contains(process.getParentProcess()))
						continue;
					else
						process.delete();
				}
			}
		}
		FlexoProcessImageNotificationCenter.getInstance().notifyNewImage();
	}

	public FlexoImportedProcessLibraryDelta getLibraryDelta() {
		return libraryDelta;
	}

	public String getReport() {
		if (visitor!=null)
			return visitor.getReport();
		return FlexoLocalization.localizedForKey("refresh_has_not_been_performed");
	}

	private final class RefreshProcessDeltaVisitor implements DeltaVisitor {
		private final FlexoWorkflow lib;

		private StringBuilder report;

		private Vector<FlexoProcess> processToDelete;
		private Vector<FlexoProcess> processToKeep;

		protected RefreshProcessDeltaVisitor(FlexoWorkflow lib) {
			this.lib = lib;
			this.processToDelete = new Vector<FlexoProcess>();
			this.processToKeep = new Vector<FlexoProcess>();
			report = new StringBuilder();
		}

		@Override
		public void visit(ProcessDelta delta) {
			PPMProcess process = delta.getPPMProcess();
			DeltaStatus status = delta.getStatus();
			switch (status) {
			case UNCHANGED:
				break;
			case DELETED:
				if (delta.getFiProcess().isTopLevelProcess()) {
					if (!delta.getFiProcess().isDeletedOnServer()) {
						if (report.length()>0)
							report.append("\n");
						report.append(FlexoLocalization.localizedForKey("the_process")).append(" ").append(delta.getFiProcess().getName()).append(" ").append(FlexoLocalization.localizedForKey("has_been_removed_from_server"));
					}
				}
				addToProcessToDelete(delta.getFiProcess());
				break;
			case UPDATED:
				FlexoProcess fip = lib.getRecursivelyImportedProcessWithURI(process.getUri());
				if (fip.isTopLevelProcess()) {
					if (report.length()>0)
						report.append("\n");
					report.append(FlexoLocalization.localizedForKey("the_process")).append(" ").append(fip.getName()).append(" ").append(FlexoLocalization.localizedForKey("has_been_updated"));
					FlexoProcessImageBuilder.startBackgroundDownloadOfSnapshots(delta.getFiProcess(), getWebService(), getLogin(), getMd5Password());
				}
				try {
					fip.updateFromObject(process);
				} catch (Exception e) {
					e.printStackTrace();
				}// This does not take children into account (automatically handled with NEW/DELETED)
				break;
			case NEW:
				if (delta.getParent()==null) {
					// We have received a new root process-->we import it
					// Import process action will automatically verify that the process is not already somewhere in the workflow
					ImportProcessesAction importProcesses = ImportProcessesAction.actionType.makeNewEmbeddedAction(lib, null, RefreshImportedProcessAction.this);
					Vector<PPMProcess> v = new Vector<PPMProcess>();
					v.add(delta.getPPMProcess());
					importProcesses.setProcessesToImport(v);
					importProcesses.doAction();
					if (importProcesses.getImportReport().getProperlyImported().size()==1)
						addToProcessToKeep(importProcesses.getImportReport().getProperlyImported().get(delta.getPPMProcess()));
				} else {
					// We create the new sub-process
					try {
						addToProcessToKeep(FlexoProcess.createImportedProcessFromProcess(lib, delta.getPPMProcess()));
					} catch (InvalidFileNameException e) {
						if (logger.isLoggable(Level.SEVERE))
							logger.severe("Invalid file name thrown for process "+delta.getPPMProcess().getName()+": "+e.getMessage());
						e.printStackTrace();
					} catch (DuplicateResourceException e) {
						if (logger.isLoggable(Level.SEVERE))
							logger.severe("DuplicateResourceException file name thrown for process "+delta.getPPMProcess().getName()+": "+e.getMessage());
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		}

		/**
		 * @param newFIP
		 */
		private void addToProcessToKeep(FlexoProcess newFIP) {
			if (processToDelete.contains(newFIP))
				processToDelete.remove(newFIP);
			processToKeep.add(newFIP);
		}

		/**
		 * @param delta
		 */
		private void addToProcessToDelete(FlexoProcess process) {
			if (processToKeep.contains(process))
				return;
			processToDelete.add(process);
		}

		public Vector<FlexoProcess> getProcessToKeep() {
			return processToKeep;
		}

		public Vector<FlexoProcess> getProcessToDelete() {
			return processToDelete;
		}

		public String getReport() {
			if (report.length()==0) {
				return FlexoLocalization.localizedForKey("there_are_no_changes");
			}
			return report.toString();
		}
	}

}
