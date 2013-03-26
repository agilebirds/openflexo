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
package org.openflexo.wkf.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.OpenEmbeddedProcess;
import org.openflexo.foundation.wkf.action.ShowHidePortmap;
import org.openflexo.foundation.wkf.action.ShowHidePortmapRegistery;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class OpenEmbeddedProcessInitializer extends ActionInitializer<OpenEmbeddedProcess, SubProcessNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenEmbeddedProcessInitializer(WKFControllerActionInitializer actionInitializer) {
		super(OpenEmbeddedProcess.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenEmbeddedProcess> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenEmbeddedProcess>() {
			@Override
			public boolean run(EventObject e, OpenEmbeddedProcess action) {
				if (action.getFocusedObject().hasSubProcessReference()) {
					return action.getFocusedObject().getSubProcess(true) != null;
				}
				if (action.getProcessToOpen() != null && action.getProcessToOpen().isImported()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_edit/inspect_an_imported_process"));
					return false;
				}
				if (action.getProcessToOpen() == null) {
					if (action.getFocusedObject().getProcess().getProject() == getEditor().getProject()) {
						return new SubProcessSelectorDialog(action.getFocusedObject().getProject(), getControllerActionInitializer(),
								action.getFocusedObject(), action.getFocusedObject().getProcess().getProcessNode()).askAndSetSubProcess();
					} else {
						return false;
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenEmbeddedProcess> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenEmbeddedProcess>() {
			@Override
			public boolean run(EventObject e, OpenEmbeddedProcess action) {
				if (action.getFocusedObject() instanceof SubProcessNode) {
					// We just dropped a SubProcessNode
					// Default status of DELETE ports is hidden
					SubProcessNode spNode = action.getFocusedObject();
					if (spNode.getPortMapRegistery() != null) {
						if (spNode instanceof SingleInstanceSubProcessNode || spNode instanceof LoopSubProcessNode
								|| spNode instanceof WSCallSubProcessNode) {
							for (FlexoPortMap pm : spNode.getPortMapRegistery().getAllDeletePortmaps()) {
								ShowHidePortmap.actionType.makeNewAction(pm, null, action.getEditor()).doAction();
							}
						}
						if (spNode instanceof WSCallSubProcessNode) {
							for (FlexoPortMap pm : spNode.getPortMapRegistery().getAllOutPortmaps()) {
								ShowHidePortmap.actionType.makeNewAction(pm, null, action.getEditor()).doAction();
							}
						}
						if (spNode instanceof MultipleInstanceSubProcessNode) {
							for (FlexoPortMap pm : spNode.getPortMapRegistery().getAllOutPortmaps()) {
								ShowHidePortmap.actionType.makeNewAction(pm, null, action.getEditor()).doAction();
							}
						}
						if (spNode instanceof SingleInstanceSubProcessNode || spNode instanceof LoopSubProcessNode
								|| spNode instanceof MultipleInstanceSubProcessNode) {
							ShowHidePortmapRegistery.actionType.makeNewAction(spNode.getPortMapRegistery(), null, action.getEditor())
									.doAction();
						}
					}
				}
				if (action.getFocusedObject() instanceof SubProcessNode && action.getFocusedObject().hasSubProcessReference()) {
					FlexoProcess subProcess = action.getFocusedObject().getSubProcess(true);
					if (subProcess != null) {
						getControllerActionInitializer().getWKFController().setCurrentFlexoProcess(subProcess);
					}
				}
				return true;
			}
		};
	}

}
