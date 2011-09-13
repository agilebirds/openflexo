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


import org.openflexo.components.AskParametersDialog;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ProcessParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.ServiceInterfaceSelectorParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.param.ParameterDefinition.ValueListener;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.InvalidLevelException;
import org.openflexo.foundation.wkf.action.ShowHidePortmap;
import org.openflexo.foundation.wkf.action.ShowHidePortmapRegistery;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.ws.DefaultServiceInterface;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.swleditor.SWLEditorConstants;


public class DropWKFElementInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DropWKFElementInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(DropWKFElement.actionType,actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer()
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DropWKFElement> getDefaultInitializer()
	{
		return new FlexoActionInitializer<DropWKFElement>() {
			@Override
			public boolean run(ActionEvent e, DropWKFElement action)
			{
				if (action.getPetriGraph() == null) {
					return false;
				}
				if (action.getObject() instanceof SubProcessNode && !action.leaveSubProcessNodeUnchanged()) {
					final SubProcessNode node = (SubProcessNode) action.getObject();
					FlexoProcess process = action.getProcess();
					node.setProcess(process);
					if (node.getSubProcess() == null) {
						return askAndSetSubProcess(node, process);
					}
				}

				if (action.handlePaletteOffset() && action.getGraphicalContext() != null) {

					// Little hack to get object to drop location
					// We must implement here all differences between object in palette
					// and object just dropped in WKF
					int deltaX = 0;
					int deltaY = 0;
					if (action.getGraphicalContext().equals(ProcessEditorConstants.BASIC_PROCESS_EDITOR)) {
						deltaX = 0; //ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
						deltaY = 0; //ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						if (action.getPetriGraph() instanceof OperationPetriGraph
								|| action.getPetriGraph() instanceof ActionPetriGraph) {
							deltaY = ProcessEditorConstants.REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX-ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
						if (action.getObject() instanceof SubProcessNode) {
							deltaX = ProcessEditorConstants.PORTMAP_REGISTERY_WIDTH-ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
							deltaY = ProcessEditorConstants.PORTMAP_REGISTERY_WIDTH-ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
					}
					else if (action.getGraphicalContext().equals(SWLEditorConstants.SWIMMING_LANE_EDITOR)) {
						deltaX = 0; //ProcessEditorConstants.REQUIRED_SPACE_ON_LEFT;
						deltaY = 0; //ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						if (action.getPetriGraph() instanceof OperationPetriGraph
								|| action.getPetriGraph() instanceof ActionPetriGraph) {
							deltaY = SWLEditorConstants.REQUIRED_SPACE_ON_TOP_FOR_CLOSING_BOX-ProcessEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
						if (action.getObject() instanceof SubProcessNode) {
							deltaX = SWLEditorConstants.PORTMAP_REGISTERY_WIDTH-SWLEditorConstants.REQUIRED_SPACE_ON_LEFT;
							deltaY = SWLEditorConstants.PORTMAP_REGISTERY_WIDTH-SWLEditorConstants.REQUIRED_SPACE_ON_TOP;
						}
					}
					action.setPosX(action.getPosX()-deltaX);
					action.setPosY(action.getPosY()-deltaY);
				}

				return true;
			}

			/**
			 * @param node
			 * @param process
			 * @return
			 */
			private boolean askAndSetSubProcess(final SubProcessNode node, FlexoProcess process) {
				ParameterDefinition[] parameters = new ParameterDefinition[5];

				final String EXISTING_PROCESS = FlexoLocalization.localizedForKey("bind_existing_process");
				final String NEW_PROCESS = FlexoLocalization.localizedForKey("bind_a_new_process");
				final String NO_PROCESS = FlexoLocalization.localizedForKey("bind_a_process_later");
				String[] modes = { NEW_PROCESS, EXISTING_PROCESS, NO_PROCESS };
				String defaultValue = NEW_PROCESS;
				final RadioButtonListParameter<String> insertModeSelector = new RadioButtonListParameter<String>("mode", "select_a_choice",defaultValue, modes);
				parameters[0] = insertModeSelector;
				final ServiceInterfaceSelectorParameter processSelector = createProcessSelector(node, EXISTING_PROCESS);
				parameters[1] = processSelector;

				String baseName = FlexoLocalization.localizedForKey("new_process_name");
				final TextFieldParameter newProcessNameTextField = new TextFieldParameter("newProcessName", "name_of_new_process", getProject().getFlexoWorkflow()
						.findNextDefaultProcessName(baseName));
				final TextFieldParameter nodeNameParameter = new TextFieldParameter("nodeName","sub_process_node_name",newProcessNameTextField.getValue());
				parameters[2] = newProcessNameTextField;
				// This widget is visible if and only if mode is NEW_PROCESS
				parameters[2].setDepends("mode");
				parameters[2].setConditional("mode=" + '"' + NEW_PROCESS + '"');

				boolean isWS = node instanceof WSCallSubProcessNode;
				parameters[3] = new ProcessParameter("parentProcess", "parent_process", isWS?null:process);
				// This widget is visible if and only if mode is NEW_PROCESS
				if (isWS) {
					// Lame hack to avoid possibility to create a sub-process for a WS-call
					parameters[3].setConditional("true=false");
				} else {
					parameters[3].setDepends("mode");
					parameters[3].setConditional("mode=" + '"' + NEW_PROCESS + '"');
				}
				// Sets the 'selectable property'. We need here to access the parameters
				// themselves:
				// We explain here that we should use the
				// "isAcceptableProcess(FlexoProcess)" method
				// defined in ProcessParameter class
				parameters[3].addParameter("isSelectable", "params.parentProcess.isAcceptableProcess");
				((ProcessParameter) parameters[3]).setProcessSelectingConditional(new ProcessParameter.ProcessSelectingConditional() {
					@Override
					public boolean isSelectable(FlexoProcess aProcess)
					{
						return aProcess.isAncestorOf(node.getProcess());
					}
				});
				parameters[4] = nodeNameParameter;
				parameters[4].setDepends("newProcessName,selectedProcess");
				newProcessNameTextField.addValueListener(new ValueListener<String>(){
					@Override
					public void newValueWasSet(ParameterDefinition<String> param, String oldValue, String newValue) {
						if (nodeNameParameter.getValue()==null
								|| nodeNameParameter.getValue().trim().length()==0
								|| nodeNameParameter.getValue().equals(oldValue)) {
							nodeNameParameter.setValue(newProcessNameTextField.getValue());
						}
					}
				});
				insertModeSelector.addValueListener(new ValueListener<String>(){

					@Override
					public void newValueWasSet(ParameterDefinition param, String oldValue, String newValue) {
						if (newValue == EXISTING_PROCESS) {
							if (nodeNameParameter.getValue()==null || nodeNameParameter.getValue().trim().length()==0 || nodeNameParameter.getValue().equals(newProcessNameTextField.getValue())) {
								if (processSelector.getValue()!=null && processSelector.getValue().getProcess()!=null) {
									nodeNameParameter.setValue(processSelector.getValue().getProcess().getName());
								} else {
									nodeNameParameter.setValue("");
								}
							}
						} else if (newValue == NEW_PROCESS) {
							if (nodeNameParameter.getValue()==null || nodeNameParameter.getValue().trim().length()==0 || processSelector.getValue()!=null && nodeNameParameter.getValue().equals(processSelector.getValue().getName())) {
								nodeNameParameter.setValue(newProcessNameTextField.getValue());
							}
						}
					}

				});
				processSelector.addValueListener(new ValueListener<WKFObject>() {

					@Override
					public void newValueWasSet(ParameterDefinition<WKFObject> param, WKFObject oldValue, WKFObject newValue) {
						if (nodeNameParameter.getValue()==null || nodeNameParameter.getValue().trim().length()==0 || oldValue!=null && nodeNameParameter.getValue().equals(oldValue.getName())|| oldValue.getProcess()!=null && nodeNameParameter.getValue().equals(oldValue.getProcess().getName())) {
							if (newValue!=null && newValue.getProcess()!=null) {
								nodeNameParameter.setValue(newValue.getProcess().getName());
							} else {
								nodeNameParameter.setValue("");
							}
						}
					}

				});
				//							parameters[4] = new ColorParameter("color","background_color", node.getBackColor()!=null && !node.getBackColor().equals(Color.WHITE)?node.getBackColor():process.getNewActivityColor());

				//							AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
				//							getProject(), FlexoLocalization.localizedForKey("create_new_sub_process_node"), FlexoLocalization
				//							.localizedForKey("what_would_you_like_to_do"), parameters);


				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
						getProject(),
						null,
						FlexoLocalization.localizedForKey("create_new_sub_process_node"),
						FlexoLocalization.localizedForKey("what_would_you_like_to_do"),
						new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								if (insertModeSelector.getValue()==null) {
									errorMessage = "";
									return false;
								}
								if(NEW_PROCESS.equals(insertModeSelector.getValue()) && (newProcessNameTextField.getValue()==null || newProcessNameTextField.getValue().trim().length()==0)){
									errorMessage = FlexoLocalization.localizedForKey("please_submit_a_valid_process_name");
									return false;
								}
								if(EXISTING_PROCESS.equals(insertModeSelector.getValue()) && processSelector.getValue()==null){
									errorMessage = FlexoLocalization.localizedForKey("please_select_a_valid_process");
									return false;
								}
								return true;
							}
						},
						parameters);

				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					//node.setBackColor((FlexoColor) parameters[4].getValue());
					if (dialog.parameterValueWithName("mode").equals(EXISTING_PROCESS)) {
						WKFObject processInterface = (WKFObject) dialog.parameterValueWithName("selectedProcess");

						if (processInterface instanceof PortRegistery) {
							FlexoProcess selectedProcess = ((PortRegistery) processInterface).getProcess();
							if (logger.isLoggable(Level.INFO)) {
								logger.info("Selected PortRegistry for Process:" + selectedProcess.getName());
							}
							node.setSubProcess(selectedProcess);
						} else if (processInterface instanceof DefaultServiceInterface) {
							FlexoProcess selectedProcess = ((DefaultServiceInterface) processInterface).getProcess();
							if (logger.isLoggable(Level.INFO)) {
								logger.info("Selected DefaultInterface for Process:" + selectedProcess.getName());
							}

							node.setSubProcess(selectedProcess);
						} else if (processInterface instanceof FlexoProcess) {
							FlexoProcess selectedProcess = (FlexoProcess) processInterface;
							if (logger.isLoggable(Level.INFO)) {
								logger.info("Selected Process:" + selectedProcess.getName());
							}

							node.setSubProcess(selectedProcess);
						} else if (processInterface instanceof ServiceInterface) {
							ServiceInterface selectedInterface = (ServiceInterface) processInterface;
							if (logger.isLoggable(Level.INFO)) {
								logger.info("Selected Interface:" + selectedInterface.getName());
							}

							node.setServiceInterface(selectedInterface);
							node.setSubProcess(selectedInterface.getProcess());

						} else {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("no process selected ???");
							}
						}

					} else if (dialog.parameterValueWithName("mode").equals(NEW_PROCESS)) {
						String newProcessName = (String) dialog.parameterValueWithName("newProcessName");
						FlexoProcess parentProcess = (FlexoProcess) dialog.parameterValueWithName("parentProcess");
						FlexoProcess newProcess = createSubProcess(parentProcess, newProcessName);
						while (newProcess == null) {
							newProcessName = FlexoController.askForString(FlexoLocalization.localizedForKey("name_of_new_process"));
							if (newProcessName == null) {
								return false; // Cancel
							}
							newProcess = createSubProcess(parentProcess, newProcessName);
						}
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Create and set new process  " + newProcess);
						}
						if (isWS) {
							newProcess.setIsWebService(true);
						}
						node.setName(newProcess.getName());
						node.setSubProcess(newProcess);
					} else if (dialog.parameterValueWithName("mode").equals(NO_PROCESS)) {
						node.setSubProcess(null);
					}
					node.setName(nodeNameParameter.getValue());
					return true;
				}
				getControllerActionInitializer().getWKFController().getMainPane().repaint();
				return false;
			}

			public FlexoProcess createSubProcess(FlexoProcess process, String newProcessName)
			{
				if (newProcessName != null) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Adding a new sub-process called " + newProcessName + " as child of " + process);
					}
					FlexoProcess newProcess;
					try {
						newProcess = FlexoProcess.createNewProcess(getProject().getFlexoWorkflow(), process, newProcessName, false);
					} catch (DuplicateResourceException e) {
						FlexoController.notify("Process named " + newProcessName + " already exists !");
						return null;
					} catch (InvalidFileNameException e) {
						FlexoController.notify("Process named " + newProcessName + " is_not_valid");
						return null;
					}
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("DONE. Added a new sub-process called " + newProcessName + " as child of " + process);
					}
					return newProcess;
				} else {
					return null;
				}
			}

			/**
			 * @param node
			 * @param existingProcessMode
			 * @return
			 */
			private ServiceInterfaceSelectorParameter createProcessSelector(final SubProcessNode node, final String existingProcessMode) {
				final ServiceInterfaceSelectorParameter processSelector = new ServiceInterfaceSelectorParameter("selectedProcess", "process_to_bind", null);
				// This widget is visible if and only if mode is EXISTING_PROCESS
				processSelector.setDepends("mode");
				processSelector.setConditional("mode=" + '"' + existingProcessMode + '"');
				// Sets the 'selectable property'. We need here to access the parameters
				// themselves:
				// We explain here that we should use the
				// "isAcceptableProcess(FlexoProcess)" method
				// defined in ProcessParameter class
				processSelector.addParameter("isSelectable", "params.selectedProcess.isAcceptableProcess");
				processSelector.setProcessSelectingConditional(new ServiceInterfaceSelectorParameter.ProcessSelectingConditional() {
					@Override
					public boolean isSelectable(FlexoProcess aProcess)
					{
						boolean returned = node.isAcceptableAsSubProcess(aProcess);
						return returned;
					}
				});
				return processSelector;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DropWKFElement> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<DropWKFElement>() {
			@Override
			public boolean run(ActionEvent e, DropWKFElement action)
			{
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getObject());

				if (action.getObject() instanceof SubProcessNode && !action.leaveSubProcessNodeUnchanged()) {
					// We just dropped a SubProcessNode
					// Default status of DELETE ports is hidden
					SubProcessNode spNode = (SubProcessNode)action.getObject();
					if (spNode.getPortMapRegistery() != null) {
						if (spNode instanceof SingleInstanceSubProcessNode || spNode instanceof LoopSubProcessNode || spNode instanceof WSCallSubProcessNode) {
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
							ShowHidePortmapRegistery.actionType.makeNewAction(spNode.getPortMapRegistery(), null, action.getEditor()).doAction();
						}
					}
				}

				SelectionManagingDrawingController<? extends DefaultDrawing<?>> controller = null;
				if (getControllerActionInitializer().getWKFController().getCurrentPerspective()==getControllerActionInitializer().getWKFController().PROCESS_EDITOR_PERSPECTIVE) {
					controller =  getControllerActionInitializer().getWKFController().PROCESS_EDITOR_PERSPECTIVE.getControllerForProcess(action.getProcess());
				} else if (getControllerActionInitializer().getWKFController().getCurrentPerspective()==getControllerActionInitializer().getWKFController().SWIMMING_LANE_PERSPECTIVE) {
					controller =  getControllerActionInitializer().getWKFController().SWIMMING_LANE_PERSPECTIVE.getControllerForProcess(action.getProcess());
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Drop in WKF but current perspective is neither BPE or SWL");
					}
					return true;
				}
				DefaultDrawing<?> drawing = controller.getDrawing();
				ShapeGraphicalRepresentation<?> newNodeGR = (ShapeGraphicalRepresentation<?>)drawing.getGraphicalRepresentation(action.getObject());
				if (newNodeGR==null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find GR for newly created node: "+action.getObject());
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
						SwingUtilities.invokeLater(new Runnable(){
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
	protected FlexoExceptionHandler<DropWKFElement> getDefaultExceptionHandler()
	{
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
