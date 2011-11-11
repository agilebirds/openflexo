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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.InvalidLevelException;
import org.openflexo.foundation.wkf.action.ShowHidePortmap;
import org.openflexo.foundation.wkf.action.ShowHidePortmapRegistery;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SWLEditorConstants;

public class DropWKFElementInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DropWKFElementInitializer(WKFControllerActionInitializer actionInitializer) {
		super(DropWKFElement.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DropWKFElement> getDefaultInitializer() {
		return new FlexoActionInitializer<DropWKFElement>() {
			@Override
			public boolean run(ActionEvent e, DropWKFElement action) {
				if (action.getPetriGraph() == null) {
					return false;
				}
				if (action.getObject() instanceof SubProcessNode && !action.leaveSubProcessNodeUnchanged()) {
					final SubProcessNode node = (SubProcessNode) action.getObject();
					FlexoProcess process = action.getProcess();
					node.setProcess(process);
					if (node.getSubProcess() == null) {
						return new SubProcessSelectorDialog(node.getProject(), getControllerActionInitializer()).askAndSetSubProcess(node,
								process);
					}
				}

				if (action.handlePaletteOffset() && action.getGraphicalContext() != null) {

					// Little hack to get object to drop location
					// We must implement here all differences between object in palette
					// and object just dropped in WKF
					int deltaX = 0;
					int deltaY = 0;
					if (action.getGraphicalContext().equals(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
						deltaX = 0; // ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
						deltaY = 0; // ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						if (action.getPetriGraph() instanceof OperationPetriGraph || action.getPetriGraph() instanceof ActionPetriGraph) {
							deltaY = ProcessEditorConstants.REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX
									- ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
						if (action.getObject() instanceof SubProcessNode) {
							deltaX = ProcessEditorConstants.PORTMAP_REGISTERY_WIDTH - ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
							deltaY = ProcessEditorConstants.PORTMAP_REGISTERY_WIDTH - ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
					} else if (action.getGraphicalContext().equals(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
						deltaX = 0; // ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
						deltaY = 0; // ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						if (action.getPetriGraph() instanceof OperationPetriGraph || action.getPetriGraph() instanceof ActionPetriGraph) {
							deltaY = SWLEditorConstants.REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX
									- ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
						if (action.getObject() instanceof SubProcessNode) {
							deltaX = SWLEditorConstants.PORTMAP_REGISTERY_WIDTH - SWLEditorConstants.REQUIRED_SPACE_ON_LEFT;
							deltaY = SWLEditorConstants.PORTMAP_REGISTERY_WIDTH - SWLEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
					}
					action.setPosX(action.getPosX() - deltaX);
					action.setPosY(action.getPosY() - deltaY);
				}

				return true;
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<DropWKFElement> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DropWKFElement>() {
			@Override
			public boolean run(ActionEvent e, DropWKFElement action) {
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getObject());

				if (action.getObject() instanceof SubProcessNode && !action.leaveSubProcessNodeUnchanged()) {
					// We just dropped a SubProcessNode
					// Default status of DELETE ports is hidden
					SubProcessNode spNode = (SubProcessNode) action.getObject();
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
						spNode.getPortMapRegistery().resetLocation(ProcessEditorConstants.BASIC_PROCESS_EDITOR);
						spNode.getPortMapRegistery().resetLocation(SWLEditorConstants.SWIMMING_LANE_EDITOR);
						if (spNode instanceof SingleInstanceSubProcessNode || spNode instanceof LoopSubProcessNode
								|| spNode instanceof MultipleInstanceSubProcessNode) {
							ShowHidePortmapRegistery.actionType.makeNewAction(spNode.getPortMapRegistery(), null, action.getEditor())
									.doAction();
						}
					}
				}

				SelectionManagingDrawingController<? extends DefaultDrawing<?>> controller = null;
				if (getControllerActionInitializer().getWKFController().getCurrentPerspective() == getControllerActionInitializer()
						.getWKFController().PROCESS_EDITOR_PERSPECTIVE) {
					controller = getControllerActionInitializer().getWKFController().PROCESS_EDITOR_PERSPECTIVE
							.getControllerForProcess(action.getProcess());
				} else if (getControllerActionInitializer().getWKFController().getCurrentPerspective() == getControllerActionInitializer()
						.getWKFController().SWIMMING_LANE_PERSPECTIVE) {
					controller = getControllerActionInitializer().getWKFController().SWIMMING_LANE_PERSPECTIVE
							.getControllerForProcess(action.getProcess());
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Drop in WKF but current perspective is neither BPE or SWL");
					}
					return true;
				}
				DefaultDrawing<?> drawing = controller.getDrawing();
				ShapeGraphicalRepresentation<?> newNodeGR = (ShapeGraphicalRepresentation<?>) drawing.getGraphicalRepresentation(action
						.getObject());
				if (newNodeGR == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find GR for newly created node: " + action.getObject());
					}
					return true;
				}
				final ShapeView<?> view = controller.getDrawingView().shapeViewForObject(newNodeGR);
				if (view == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Cannot build view for newly created node insertion");
					}
					return false;
				} else {
					if (action.getEditNodeLabel()) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								if (view != null && view.getLabelView() != null) {
									view.getLabelView().startEdition();
								}
							}
						});
					}
				}

				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<DropWKFElement> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<DropWKFElement>() {
			@Override
			public boolean handleException(FlexoException exception, DropWKFElement action) {
				if (exception instanceof InvalidLevelException) {

					FlexoController.notify(FlexoLocalization.localizedForKey("cannot_put_node_at_this_place_wrong_level"));
					return true;
				}
				return false;
			}
		};
	}

}
