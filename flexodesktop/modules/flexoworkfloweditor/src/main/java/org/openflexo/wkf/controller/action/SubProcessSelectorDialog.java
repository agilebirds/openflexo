package org.openflexo.wkf.controller.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParameterDefinition.ValueListener;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.ProcessParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.ServiceInterfaceSelectorParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.ws.DefaultServiceInterface;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.controller.FlexoController;

public class SubProcessSelectorDialog {

	private static final Logger logger = FlexoLogger.getLogger(SubProcessSelectorDialog.class.getPackage().getName());

	private FlexoProject project;
	private WKFControllerActionInitializer controllerActionInitializer;

	public SubProcessSelectorDialog(FlexoProject project, WKFControllerActionInitializer controllerActionInitializer) {
		super();
		this.project = project;
		this.controllerActionInitializer = controllerActionInitializer;
	}

	public FlexoProject getProject() {
		return project;
	}

	public WKFControllerActionInitializer getControllerActionInitializer() {
		return controllerActionInitializer;
	}

	/**
	 * @param node
	 * @param process
	 * @return
	 */
	public boolean askAndSetSubProcess(final SubProcessNode node, FlexoProcess process) {
		ParameterDefinition[] parameters = new ParameterDefinition[5];

		final String EXISTING_PROCESS = FlexoLocalization.localizedForKey("bind_existing_process");
		final String NEW_PROCESS = FlexoLocalization.localizedForKey("bind_a_new_process");
		final String NO_PROCESS = FlexoLocalization.localizedForKey("bind_a_process_later");
		String[] modes = { NEW_PROCESS, EXISTING_PROCESS, NO_PROCESS };
		String defaultValue = NEW_PROCESS;
		final RadioButtonListParameter<String> insertModeSelector = new RadioButtonListParameter<String>("mode", "select_a_choice",
				defaultValue, modes);
		parameters[0] = insertModeSelector;
		final ServiceInterfaceSelectorParameter processSelector = createProcessSelector(node, EXISTING_PROCESS);
		parameters[1] = processSelector;

		String baseName = FlexoLocalization.localizedForKey("new_process_name");
		final TextFieldParameter newProcessNameTextField = new TextFieldParameter("newProcessName", "name_of_new_process", getProject()
				.getFlexoWorkflow().findNextDefaultProcessName(baseName));
		final TextFieldParameter nodeNameParameter = new TextFieldParameter("nodeName", "sub_process_node_name",
				newProcessNameTextField.getValue());
		parameters[2] = newProcessNameTextField;
		// This widget is visible if and only if mode is NEW_PROCESS
		parameters[2].setDepends("mode");
		parameters[2].setConditional("mode=" + '"' + NEW_PROCESS + '"');

		boolean isWS = node instanceof WSCallSubProcessNode;
		parameters[3] = new ProcessParameter("parentProcess", "parent_process", isWS ? null : process);
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
			public boolean isSelectable(FlexoProcess aProcess) {
				return aProcess.isAncestorOf(node.getProcess());
			}
		});
		parameters[4] = nodeNameParameter;
		parameters[4].setDepends("newProcessName,selectedProcess");
		newProcessNameTextField.addValueListener(new ValueListener<String>() {
			@Override
			public void newValueWasSet(ParameterDefinition<String> param, String oldValue, String newValue) {
				if (nodeNameParameter.getValue() == null || nodeNameParameter.getValue().trim().length() == 0
						|| nodeNameParameter.getValue().equals(oldValue)) {
					nodeNameParameter.setValue(newProcessNameTextField.getValue());
				}
			}
		});
		insertModeSelector.addValueListener(new ValueListener<String>() {

			@Override
			public void newValueWasSet(ParameterDefinition<String> param, String oldValue, String newValue) {
				if (newValue == EXISTING_PROCESS) {
					if (nodeNameParameter.getValue() == null || nodeNameParameter.getValue().trim().length() == 0
							|| nodeNameParameter.getValue().equals(newProcessNameTextField.getValue())) {
						if (processSelector.getValue() != null && processSelector.getValue().getProcess() != null) {
							nodeNameParameter.setValue(processSelector.getValue().getProcess().getName());
						} else {
							nodeNameParameter.setValue("");
						}
					}
				} else if (newValue == NEW_PROCESS) {
					if (nodeNameParameter.getValue() == null || nodeNameParameter.getValue().trim().length() == 0
							|| processSelector.getValue() != null
							&& nodeNameParameter.getValue().equals(processSelector.getValue().getName())) {
						nodeNameParameter.setValue(newProcessNameTextField.getValue());
					}
				}
			}

		});
		processSelector.addValueListener(new ValueListener<WKFObject>() {

			@Override
			public void newValueWasSet(ParameterDefinition<WKFObject> param, WKFObject oldValue, WKFObject newValue) {
				if (nodeNameParameter.getValue() == null || nodeNameParameter.getValue().trim().length() == 0 || oldValue != null
						&& nodeNameParameter.getValue().equals(oldValue.getName()) || oldValue.getProcess() != null
						&& nodeNameParameter.getValue().equals(oldValue.getProcess().getName())) {
					if (newValue != null && newValue.getProcess() != null) {
						nodeNameParameter.setValue(newValue.getProcess().getName());
					} else {
						nodeNameParameter.setValue("");
					}
				}
			}

		});
		// parameters[4] = new ColorParameter("color","background_color", node.getBackColor()!=null &&
		// !node.getBackColor().equals(Color.WHITE)?node.getBackColor():process.getNewActivityColor());

		// AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
		// getProject(), FlexoLocalization.localizedForKey("create_new_sub_process_node"), FlexoLocalization
		// .localizedForKey("what_would_you_like_to_do"), parameters);

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
				FlexoLocalization.localizedForKey("create_new_sub_process_node"),
				FlexoLocalization.localizedForKey("what_would_you_like_to_do"), new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (insertModeSelector.getValue() == null) {
							errorMessage = "";
							return false;
						}
						if (NEW_PROCESS.equals(insertModeSelector.getValue())
								&& (newProcessNameTextField.getValue() == null || newProcessNameTextField.getValue().trim().length() == 0)) {
							errorMessage = FlexoLocalization.localizedForKey("please_submit_a_valid_process_name");
							return false;
						}
						if (EXISTING_PROCESS.equals(insertModeSelector.getValue()) && processSelector.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_select_a_valid_process");
							return false;
						}
						return true;
					}
				}, parameters);

		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			// node.setBackColor((FlexoColor) parameters[4].getValue());
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

	public FlexoProcess createSubProcess(FlexoProcess process, String newProcessName) {
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
		final ServiceInterfaceSelectorParameter processSelector = new ServiceInterfaceSelectorParameter("selectedProcess",
				"process_to_bind", null);
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
			public boolean isSelectable(FlexoProcess aProcess) {
				boolean returned = node.isAcceptableAsSubProcess(aProcess);
				return returned;
			}
		});
		return processSelector;
	}
}
