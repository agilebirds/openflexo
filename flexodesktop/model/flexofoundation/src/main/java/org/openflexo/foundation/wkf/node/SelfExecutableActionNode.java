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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PetriGraphSet;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a self activated action node
 * 
 * @author sguerin
 * 
 */
public final class SelfExecutableActionNode extends ActionNode implements SelfExecutableNode {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SelfExecutableActionNode.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 */
	public SelfExecutableActionNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public SelfExecutableActionNode(FlexoProcess process) {
		super(process);
		assignments = new Vector<BindingAssignment>();
		assignmentDescriptions = new Vector<String>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		for (int i = 0; (i < assignments.size()) && (i < assignmentDescriptions.size()); i++) {
			String desc = assignmentDescriptions.get(i);
			if ("null".equals(desc)) {
				desc = null;
			}
			assignments.get(i).setDescription(desc);
		}
		super.finalizeDeserialization(builder);
	}

	public static final String EXECUTION_PRIMITIVE = "executionPrimitive";
	private BindingValue _executionPrimitive;

	@Override
	public WKFBindingDefinition getExecutionPrimitiveBindingDefinition() {
		return WKFBindingDefinition.get(this, EXECUTION_PRIMITIVE, (DMType) null, BindingDefinitionType.EXECUTE, false);
	}

	@Override
	public BindingValue getExecutionPrimitive() {
		if (isBeingCloned()) {
			return null;
		}
		return _executionPrimitive;
	}

	@Override
	public void setExecutionPrimitive(BindingValue executionPrimitive) {
		BindingValue oldBindingValue = _executionPrimitive;
		_executionPrimitive = executionPrimitive;
		if (_executionPrimitive != null) {
			_executionPrimitive.setOwner(this);
			_executionPrimitive.setBindingDefinition(getExecutionPrimitiveBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(EXECUTION_PRIMITIVE, oldBindingValue, executionPrimitive));
	}

	@Override
	public String getDefaultName() {
		return FlexoLocalization.localizedForKey("execution");
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.SELF_EXECUTABLE_ACTION_INSPECTOR;
	}

	private Vector<BindingAssignment> assignments;

	@Override
	public Vector<BindingAssignment> getAssignments() {
		return assignments;
	}

	@Override
	public void setAssignments(Vector<BindingAssignment> someAssignments) {
		this.assignments = someAssignments;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("assignments", null, null)); // TODO notify better
	}

	@Override
	public void addToAssignments(BindingAssignment assignment) {
		if (assignment == null) {
			return;
		}
		assignment.setOwner(this);
		assignments.add(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("assignments", null, null)); // TODO notify better
	}

	@Override
	public void removeFromAssignments(BindingAssignment assignment) {
		assignment.setOwner(null);
		assignments.remove(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("assignments", null, null)); // TODO notify better
	}

	private Vector<String> assignmentDescriptions;

	public Vector<String> getAssignmentDescriptions() {
		Vector<String> returned = new Vector<String>();
		for (BindingAssignment a : getAssignments()) {
			returned.add(a.getDescription() != null ? a.getDescription() : "null");
		}
		return returned;
	}

	public void setAssignmentDescriptions(Vector<String> someAssignments) {
		this.assignmentDescriptions = someAssignments;
	}

	public void addToAssignmentDescriptions(String assignment) {
		assignmentDescriptions.add(assignment);
	}

	public void removeFromAssignmentDescriptions(String assignment) {
		assignmentDescriptions.remove(assignment);
	}

	public BindingAssignment createAssignement() {
		BindingAssignment returned = new BindingAssignment(this);
		addToAssignments(returned);
		return returned;
	}

	public void deleteAssignement(BindingAssignment assignment) {
		removeFromAssignments(assignment);
	}

	public boolean isAssignementDeletable(BindingAssignment assignment) {
		return true;
	}

	private ActionPetriGraph _executionPetriGraph = null;

	@Override
	public boolean hasExecutionPetriGraph() {
		return _executionPetriGraph != null;
	}

	@Override
	public ActionPetriGraph getExecutionPetriGraph() {
		return _executionPetriGraph;
	}

	public void setExecutionPetriGraph(ActionPetriGraph executionPetriGraph) {
		_executionPetriGraph = executionPetriGraph;
		_executionPetriGraph.setContainer(this, FlexoProcess.EXECUTION_CONTEXT);
		setChanged();
		notifyObservers(new PetriGraphSet(executionPetriGraph));
	}

	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		if (getExecutionPetriGraph() != null) {
			returned.addAll(getExecutionPetriGraph().getAllEmbeddedWKFObjects());
		}
		return returned;
	}

}
