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
package org.openflexo.foundation.wkf.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;

/**
 * @author nid
 * 
 */
public class WorkflowPathToOperationNode {
	private OperationNode operationNode;
	private Map<FlexoProcess, Status> newStatusesByProcess = new HashMap<FlexoProcess, Status>();
	private List<FlexoProcess> deletedProcesses = new ArrayList<FlexoProcess>();
	private List<FlexoProcess> createdProcesses = new ArrayList<FlexoProcess>();
	private List<WorkflowPathCondition> conditions = new ArrayList<WorkflowPathCondition>();

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public WorkflowPathToOperationNode clone() {
		WorkflowPathToOperationNode clone = new WorkflowPathToOperationNode();
		clone.setOperationNode(operationNode);
		clone.newStatusesByProcess.putAll(newStatusesByProcess);
		clone.deletedProcesses.addAll(deletedProcesses);
		clone.createdProcesses.addAll(createdProcesses);
		clone.conditions.addAll(conditions);
		return clone;
	}

	public OperationNode getOperationNode() {
		return operationNode;
	}

	public void setOperationNode(OperationNode operationNode) {
		this.operationNode = operationNode;
	}

	public Collection<Status> getNewStatuses() {
		return newStatusesByProcess.values();
	}

	public Status getNewStatusForProcess(FlexoProcess process) {
		return newStatusesByProcess.get(process);
	}

	public void addNewStatus(Status newStatus) {
		this.newStatusesByProcess.put(newStatus.getProcess(), newStatus);
	}

	public List<FlexoProcess> getDeletedProcesses() {
		return deletedProcesses;
	}

	public void setDeletedProcesses(List<FlexoProcess> deletedProcesses) {
		this.deletedProcesses = deletedProcesses;
	}

	public List<FlexoProcess> getCreatedProcesses() {
		return createdProcesses;
	}

	public void setCreatedProcesses(List<FlexoProcess> createdProcesses) {
		this.createdProcesses = createdProcesses;
	}

	public List<WorkflowPathCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<WorkflowPathCondition> conditions) {
		this.conditions = conditions;
	}

	public void addIfOperator(IFOperator ifOperator, boolean value) {
		addCondition(ifOperator.getProcess(), ifOperator.getName(), ifOperator.getConditionPrimitive(), value);
		if (value && ifOperator.getNewStatusForPositiveEvaluation() != null) {
			addNewStatus(ifOperator.getNewStatusForPositiveEvaluation());
		} else if (!value && ifOperator.getNewStatusForNegativeEvaluation() != null) {
			addNewStatus(ifOperator.getNewStatusForNegativeEvaluation());
		}
	}

	/**
	 * Add a condition to this path.
	 * 
	 * @param process
	 *            : the current process for this condition
	 * @param conditionDescription
	 *            : the condition description. At least conditionDescription or conditionBinding must not be null.
	 * @param conditionBinding
	 *            : the actual condition binding. At least conditionDescription or conditionBinding must not be null.
	 * @param isPositiveEvaluation
	 *            : true if this path is used if the condition match, false if this path is used if the condition doesn't match.
	 */
	public void addCondition(FlexoProcess process, String conditionDescription, AbstractBinding conditionBinding,
			boolean isPositiveEvaluation) {
		conditions.add(new WorkflowPathCondition(process, conditionDescription, conditionBinding, isPositiveEvaluation));
	}

	public static class WorkflowPathCondition {
		private FlexoProcess process;
		private String conditionDescription;
		private AbstractBinding conditionBinding;
		private boolean isPositiveEvaluation;

		public WorkflowPathCondition(FlexoProcess process, String conditionDescription, AbstractBinding conditionBinding,
				boolean isPositiveEvaluation) {
			this.process = process;
			this.conditionDescription = conditionDescription;
			this.conditionBinding = conditionBinding;
			this.isPositiveEvaluation = isPositiveEvaluation;
		}

		public FlexoProcess getProcess() {
			return process;
		}

		public void setProcess(FlexoProcess process) {
			this.process = process;
		}

		public String getConditionDescription() {
			return conditionDescription;
		}

		public void setConditionDescription(String conditionDescription) {
			this.conditionDescription = conditionDescription;
		}

		public boolean getIsPositiveEvaluation() {
			return isPositiveEvaluation;
		}

		public void setIsPositiveEvaluation(boolean isPositiveEvaluation) {
			this.isPositiveEvaluation = isPositiveEvaluation;
		}

		public AbstractBinding getConditionBinding() {
			return conditionBinding;
		}

		public void setConditionBinding(AbstractBinding conditionBinding) {
			this.conditionBinding = conditionBinding;
		}
	}
}
