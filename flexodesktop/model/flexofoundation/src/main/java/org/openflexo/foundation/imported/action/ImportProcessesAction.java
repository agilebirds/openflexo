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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.imported.dm.ProcessAlreadyImportedException;
import org.openflexo.foundation.wkf.FlexoImportedProcessLibrary;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.InvalidProcessReferencesException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.PPMProcess;

public class ImportProcessesAction extends FlexoAction<ImportProcessesAction, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = FlexoLogger.getLogger(ImportProcessesAction.class.getPackage().getName());

	public static final FlexoActionType<ImportProcessesAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ImportProcessesAction, FlexoModelObject, FlexoModelObject>(
			"import_processes", FlexoActionType.defaultGroup, FlexoActionType.importMenu, FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		public ImportProcessesAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new ImportProcessesAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}
	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoWorkflow.class);
		FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
		FlexoModelObject.addActionForClass(actionType, FlexoImportedProcessLibrary.class);
	}

	private Vector<PPMProcess> processesToImport;

	private ProcessImportReport importReport;

	public Vector<PPMProcess> getProcessesToImport() {
		return processesToImport;
	}

	public void setProcessesToImport(Vector<PPMProcess> processesToImport) {
		this.processesToImport = processesToImport;
	}

	protected ImportProcessesAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoWorkflow lib = getEditor().getProject().getFlexoWorkflow();
		importReport = new ProcessImportReport();
		for (PPMProcess p : getProcessesToImport()) {
			if (!isValid(p) || p.getParentProcess() != null) {
				importReport.addToInvalidProcesses(p);
			} else {
				try {
					FlexoProcess fip = lib.importProcess(p);
					importReport.addToProperlyImportedProcesses(p, fip);
				} catch (ProcessAlreadyImportedException e) {
					importReport.addToAlreadyImportedProcesses(p);
					if (logger.isLoggable(Level.FINE)) {
						logger.log(Level.FINE, "Process " + p.getName() + " was already imported", e);
					}
				} catch (InvalidParentProcessException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("This should not happen for imported processes!");
					}
				} catch (InvalidProcessReferencesException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("This case is not handled yet for imported processes!");
					}
				}
			}
		}
	}

	private boolean isValid(PPMProcess p) {
		return p.getUri() != null && p.getVersionUri() != null;
	}

	public ProcessImportReport getImportReport() {
		return importReport;
	}

	public static class ProcessImportReport {
		private LinkedHashMap<PPMProcess, FlexoProcess> properlyImported;
		private Vector<PPMProcess> invalidProcesses;
		private Vector<PPMProcess> alreadyImportedProcesses;

		public ProcessImportReport() {
			properlyImported = new LinkedHashMap<PPMProcess, FlexoProcess>();
			invalidProcesses = new Vector<PPMProcess>();
			alreadyImportedProcesses = new Vector<PPMProcess>();
		}

		public LinkedHashMap<PPMProcess, FlexoProcess> getProperlyImported() {
			return properlyImported;
		}

		public void setProperlyImported(LinkedHashMap<PPMProcess, FlexoProcess> properlyImported) {
			this.properlyImported = properlyImported;
		}

		public void addToProperlyImportedProcesses(PPMProcess processToImport, FlexoProcess matchingImportedProcess) {
			properlyImported.put(processToImport, matchingImportedProcess);
		}

		public Vector<PPMProcess> getInvalidProcesses() {
			return invalidProcesses;
		}

		public void setInvalidProcesses(Vector<PPMProcess> invalidProcess) {
			this.invalidProcesses = invalidProcess;
		}

		public void addToInvalidProcesses(PPMProcess process) {
			invalidProcesses.add(process);
		}

		public Vector<PPMProcess> getAlreadyImportedProcesses() {
			return alreadyImportedProcesses;
		}

		public void setAlreadyImportedProcesses(Vector<PPMProcess> invalidProcess) {
			this.alreadyImportedProcesses = invalidProcess;
		}

		public void addToAlreadyImportedProcesses(PPMProcess process) {
			alreadyImportedProcesses.add(process);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			// 1. Properly imported
			Iterator<PPMProcess> i = getProperlyImported().keySet().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_processes_have_been_properly_imported"));
			// 2. Invalid roles
			i = getInvalidProcesses().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_processes_were_not_valid"));
			// 3. Already imported
			i = getAlreadyImportedProcesses().iterator();
			append(sb, i, FlexoLocalization.localizedForKey("the_following_processes_were_already_imported"));
			if (sb.length() == 0) {
				return FlexoLocalization.localizedForKey("nothing_has_been_imported");
			} else {
				sb.append("</html>");
			}
			return sb.toString();
		}

		private void append(StringBuilder sb, Iterator<PPMProcess> i, String title) {
			if (sb.length() == 0) {
				sb.append("<html>");
			}
			boolean needsClosingUl = false;
			if (i.hasNext()) {
				sb.append(title).append(':').append("<ul>");
				needsClosingUl = true;
			}
			while (i.hasNext()) {
				PPMProcess p = i.next();
				sb.append("<li>").append(p.getName()).append("</li>");
			}
			if (needsClosingUl) {
				sb.append("</ul>");
				needsClosingUl = false;
			}
		}

	}

}
