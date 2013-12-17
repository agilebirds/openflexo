package org.openflexo.wkf.controller.action;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class SubProcessSelectorDialog extends FIBDialog<SubProcessSelectorDialog.SubProcessSelectorData> {

	private static final Logger logger = FlexoLogger.getLogger(SubProcessSelectorDialog.class.getPackage().getName());

	private static final File FIB_FILE = new FileResource("Fib/FIBSubProcessSelector.fib");

	public static enum BoundProcessChoice {
		BIND_NEW, BIND_EXISTING, BIND_LATER;
	}

	public static class SubProcessSelectorData implements HasPropertyChangeSupport {

		private FlexoProject project;
		private WKFControllerActionInitializer controllerActionInitializer;
		private BoundProcessChoice choice = BoundProcessChoice.BIND_NEW;
		private FlexoProcessNode existingProcess;
		private String newProcessName;
		private String subProcessNodeName;
		private FlexoProcessNode parentProcess;

		private PropertyChangeSupport propertyChangeSupport;

		private final SubProcessNode subProcessNode;
		private String errorMessage;
		private boolean showErrorMessage;
		private boolean noParentProcess;

		private boolean showRoleSelector;
		private Role associatedWithRole;

		public SubProcessSelectorData(FlexoProject project, WKFControllerActionInitializer controllerActionInitializer,
				SubProcessNode subProcessNode, FlexoProcessNode process) {
			this.project = project;
			this.controllerActionInitializer = controllerActionInitializer;
			this.subProcessNode = subProcessNode;
			subProcessNodeName = subProcessNode.getName();
			this.parentProcess = process;
			this.propertyChangeSupport = new PropertyChangeSupport(this);
			noParentProcess = true;
			validate();
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return propertyChangeSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		public FlexoProject getProject() {
			return project;
		}

		public WKFControllerActionInitializer getControllerActionInitializer() {
			return controllerActionInitializer;
		}

		public SubProcessNode getSubProcessNode() {
			return subProcessNode;
		}

		public BoundProcessChoice getChoice() {
			return choice;
		}

		public void setChoice(BoundProcessChoice choice) {
			if (choice == null) {
				return;
			}
			this.choice = choice;
			switch (choice) {
			case BIND_NEW:
				if (subProcessNodeName == null || subProcessNodeName.trim().length() == 0 || subProcessNodeName != null
						&& existingProcess != null && subProcessNodeName.equals(existingProcess.getName())) {
					setSubProcessNodeName(newProcessName);
				}
				break;
			case BIND_EXISTING:
				if (subProcessNodeName == null || subProcessNodeName.trim().length() == 0 || subProcessNodeName != null
						&& subProcessNodeName.equals(newProcessName)) {
					if (existingProcess != null) {
						setSubProcessNodeName(existingProcess.getName());
					} else {
						setSubProcessNodeName(null);
					}
				}
				break;
			}
			propertyChangeSupport.firePropertyChange("choice", null, choice);
			validate();
		}

		public FlexoProcessNode getExistingProcess() {
			return existingProcess;
		}

		public void setExistingProcess(FlexoProcessNode existingProcess) {
			FlexoProcessNode old = this.existingProcess;
			this.existingProcess = existingProcess;
			if (existingProcess != null
					&& (subProcessNodeName == null || subProcessNodeName.trim().length() == 0 || old != null
							&& old.getName().equals(subProcessNodeName))) {
				setSubProcessNodeName(existingProcess.getName());
			}
			propertyChangeSupport.firePropertyChange("existingProcess", old, existingProcess);
			validate();
		}

		public String getNewProcessName() {
			return newProcessName;
		}

		public void setNewProcessName(String newProcessName) {
			String old = this.newProcessName;
			this.newProcessName = newProcessName;
			propertyChangeSupport.firePropertyChange("newProcessName", old, newProcessName);
			if (subProcessNodeName == null || subProcessNodeName.trim().length() == 0 || subProcessNodeName.equals(old)) {
				setSubProcessNodeName(newProcessName);
			}
			validate();
		}

		public FlexoProcessNode getParentProcess() {
			return parentProcess;
		}

		public void setParentProcess(FlexoProcessNode parentProcess) {
			this.parentProcess = parentProcess;
			propertyChangeSupport.firePropertyChange("parentProcess", null, parentProcess);
		}

		public boolean getNoParentProcess() {
			return noParentProcess;
		}

		public void setNoParentProcess(boolean value) {
			noParentProcess = value;
		}

		public String getSubProcessNodeName() {
			return subProcessNodeName;
		}

		public void setSubProcessNodeName(String subProcessNodeName) {
			this.subProcessNodeName = subProcessNodeName;
			propertyChangeSupport.firePropertyChange("subProcessNodeName", null, subProcessNodeName);
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		private void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			propertyChangeSupport.firePropertyChange("errorMessage", null, this.errorMessage);
		}

		public boolean isAcceptableParentProcessNode(FlexoProcessNode parentProcessNode) {
			return parentProcessNode == null || parentProcessNode.isAncestorOf(subProcessNode.getProcess().getProcessNode());
		}

		public boolean isAcceptableExistingProcessNode(FlexoProcessNode existingProcessNode) {
			return subProcessNode.isAcceptableAsSubProcess(existingProcessNode);
		}

		private void validate() {
			switch (choice) {
			case BIND_EXISTING:
				if (existingProcess == null) {
					setErrorMessage(FlexoLocalization.localizedForKey("please_select_a_valid_process"));
					return;
				}
				break;
			case BIND_NEW:
				if (newProcessName == null || newProcessName.trim().length() == 0) {
					setErrorMessage(FlexoLocalization.localizedForKey("please_submit_a_valid_process_name"));
					return;
				} else {
					if (getProject().getWorkflow().getLocalFlexoProcessWithName(newProcessName) != null) {
						setErrorMessage(FlexoLocalization.localizedForKeyWithParams("process_named_($0)_already_exists", newProcessName));
						return;
					}
				}
				break;
			}
			setErrorMessage(null);
		}

		public boolean isShowErrorMessage() {
			return showErrorMessage;
		}

		public void setShowErrorMessage(boolean showErrorMessage) {
			this.showErrorMessage = showErrorMessage;
			propertyChangeSupport.firePropertyChange("showErrorMessage", !showErrorMessage, showErrorMessage);
		}

		public boolean isShowRoleSelector() {
			return showRoleSelector;
		}

		public void setShowRoleSelector(boolean showRoleSelector) {
			this.showRoleSelector = showRoleSelector;
			propertyChangeSupport.firePropertyChange("showRoleSelector", !showRoleSelector, showRoleSelector);
			// Next line is there because KVLibrary does not understand properly "is" prefix
			propertyChangeSupport.firePropertyChange("isShowRoleSelector", !showRoleSelector, showRoleSelector);
		}

		public Role getAssociatedWithRole() {
			return associatedWithRole;
		}

		public void setAssociatedWithRole(Role associatedWithRole) {
			this.associatedWithRole = associatedWithRole;
			propertyChangeSupport.firePropertyChange("associatedWithRole", null, associatedWithRole);
		}

	}

	public SubProcessSelectorDialog(FlexoProject project, WKFControllerActionInitializer controllerActionInitializer,
			SubProcessNode subProcessNode, FlexoProcessNode process, Role associatedWithRole) {
		super(FIBLibrary.instance().retrieveFIBComponent(FIB_FILE), new SubProcessSelectorData(project, controllerActionInitializer,
				subProcessNode, process), controllerActionInitializer.getController().getFlexoFrame(), true, FlexoLocalization
				.getMainLocalizer());
		getData().setShowRoleSelector(associatedWithRole == null);
		getData().setAssociatedWithRole(associatedWithRole);
	}

	public boolean askAndSetSubProcess() {
		setSize(560, 300);
		setLocationRelativeTo(getOwner());
		while (true) {
			setVisible(true);
			if (getStatus() == Status.VALIDATED) {
				getData().setShowErrorMessage(true);
				getData().validate();
				if (getData().getErrorMessage() != null) {
					continue;
				}
				getData().getSubProcessNode().setRole(getData().getAssociatedWithRole());
				getData().getSubProcessNode().setName(getData().getSubProcessNodeName());
				switch (getData().getChoice()) {
				case BIND_EXISTING:
					FlexoProcessNode existingProcess = getData().getExistingProcess();
					if (existingProcess != null) {
						FlexoProcess process;
						if (existingProcess.getWorkflow().isCache()) {
							process = existingProcess.getProcess(true);
							if (process == null) {
								return false;
							}
						} else {
							process = existingProcess.getProcess();
						}
						if (process != null) {
							getData().getSubProcessNode().setSubProcess(process);
							return true;
						}
					}
					break;
				case BIND_LATER:
					return true;
				case BIND_NEW:
					AddSubProcess addSubProcess = AddSubProcess.actionType.makeNewAction(getData().subProcessNode.getProcess(), null,
							getData().getControllerActionInitializer().getEditor());
					addSubProcess.setNewProcessName(getData().getNewProcessName());
					if (!getData().getNoParentProcess()) {
						addSubProcess.setParentProcess(getData().parentProcess != null ? getData().parentProcess.getProcess() : null);
					}
					addSubProcess.setShowNewProcess(false);
					addSubProcess.doAction();
					if (addSubProcess.hasActionExecutionSucceeded()) {
						getData().getSubProcessNode().setSubProcess(addSubProcess.getNewProcess());
						return true;
					}
					break;
				}
			} else {
				return false;
			}
		}

	}
}
