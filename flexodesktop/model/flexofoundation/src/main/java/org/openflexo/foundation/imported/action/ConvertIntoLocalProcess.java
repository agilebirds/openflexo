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

import java.util.Vector;

import org.openflexo.foundation.ConvertedIntoLocalObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;


public class ConvertIntoLocalProcess extends FlexoAction<ConvertIntoLocalProcess,FlexoProcess,FlexoProcess> {

	public static final FlexoActionType<ConvertIntoLocalProcess, FlexoProcess, FlexoProcess> actionType = new FlexoActionType<ConvertIntoLocalProcess, FlexoProcess, FlexoProcess>("convert_into_local_process") {

		@Override
		protected boolean isEnabledForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return isVisibleForSelection(object, globalSelection) && object.getWorkflow().getLocalFlexoProcessWithName(object.getName())==null;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoProcess object, Vector<FlexoProcess> globalSelection) {
			return object!=null && object.isImported() && object.isTopLevelProcess() && object.isDeletedOnServer() && object.getProcessNode()!=null;
		}

		@Override
		public ConvertIntoLocalProcess makeNewAction(FlexoProcess focusedObject, Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
			return new ConvertIntoLocalProcess(focusedObject,globalSelection,editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
	}

	public ConvertIntoLocalProcess(FlexoProcess focusedObject,
			Vector<FlexoProcess> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private static class ProcessStructure {
		private FlexoProcess process;
		private String uri;
		private Vector<ProcessStructure> subProcesses;

		protected ProcessStructure(FlexoProcess p) {
			this.process = p;
			this.uri = p.getURI();
			this.subProcesses = new Vector<ProcessStructure>();
			for (FlexoProcess sub: this.process.getSubProcesses()) {
				subProcesses.add(new ProcessStructure(sub));
			}
		}

		public void convertIntoLocal(){
			for (ProcessStructure sub : subProcesses) {
				sub.convertIntoLocal();
			}
			FlexoProcess.initProcessObjects(process);
			process.setURIFromSourceObject(uri);
			process.setURI(null);
			process.setVersionURI(null);
			process.setChanged();
			process.notifyObservers(new ConvertedIntoLocalObject(process));
		}
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		FlexoProcess p = getFocusedObject();
		FlexoProcessNode node = p.getProcessNode();
		FlexoWorkflow workflow = node.getWorkflow();
		ProcessStructure structure = new ProcessStructure(p);
		if (node.getWorkflow().getImportedRootNodeProcesses().contains(node)) {
			 workflow.removeFromImportedRootNodeProcesses(node);
			 workflow.addToTopLevelNodeProcesses(node);
			 structure.convertIntoLocal();
		}
	}

}
