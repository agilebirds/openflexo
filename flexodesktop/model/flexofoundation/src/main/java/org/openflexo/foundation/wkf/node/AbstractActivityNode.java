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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoUtils;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.foundation.stats.AbstractActivityStatistics;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.MetricsValueAdded;
import org.openflexo.foundation.wkf.MetricsValueRemoved;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddActivityMetricsValue;
import org.openflexo.foundation.wkf.action.AddToConsultedRole;
import org.openflexo.foundation.wkf.action.AddToInformedRole;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.action.GenerateActivityScreenshot;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.RemoveFromConsultedRole;
import org.openflexo.foundation.wkf.action.RemoveFromInformedRole;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleInserted;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.wkf.dm.RoleTextColorChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;

/**
 * An abstract activity represents an activity that can be a node of the activity petri graph. This is either an ActivityFlexoNode or a
 * SubProcessNode.
 * 
 * @author sguerin
 * 
 */
public abstract class AbstractActivityNode extends FatherNode implements MetricsValueOwner, FlexoObserver, ReferenceOwner {

	private static final Logger logger = Logger.getLogger(AbstractActivityNode.class.getPackage().getName());

	private FlexoModelObjectReference<Role> role;

	private FlexoModelObjectReference<Role> roleA;

	private List<FlexoModelObjectReference<Role>> consultedRoles;
	private List<FlexoModelObjectReference<Role>> informedRoles;

	private String documentationUrl;
	private FlexoModelObjectReference<FlexoProcess> linkedProcess;

	private String _acronym;

	private FlexoCSS cssSheet;

	private transient AbstractActivityStatistics statistics;

	public static FlexoActionizer<AddActivityMetricsValue, AbstractActivityNode, WKFObject> addMetricsActionizer;
	public static FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue> deleteMetricsActionizer;

	public static FlexoActionizer<AddToConsultedRole, AbstractActivityNode, AbstractActivityNode> addConsultedRoleActionizer;
	public static FlexoActionizer<RemoveFromConsultedRole, Role, AbstractActivityNode> removeFromConsultedRoleActionizer;

	public static FlexoActionizer<AddToInformedRole, AbstractActivityNode, AbstractActivityNode> addInformedRoleActionizer;
	public static FlexoActionizer<RemoveFromInformedRole, Role, AbstractActivityNode> removeFromInformedRoleActionizer;

	/**
	 * Default constructor
	 */
	public AbstractActivityNode(FlexoProcess process) {
		super(process);
		metricsValues = new Vector<MetricsValue>();
		this.consultedRoles = new Vector<FlexoModelObjectReference<Role>>();
		this.informedRoles = new Vector<FlexoModelObjectReference<Role>>();
	}

	static {
		addActionForClass(OpenOperationLevel.actionType, AbstractActivityNode.class);
		addActionForClass(GenerateActivityScreenshot.actionType, AbstractActivityNode.class);
	}

	/**
	 * Default inspector name: implemented in sub-classes !
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.WKF.ABSTRACT_ACTIVITY_NODE_INSPECTOR;
	}

	public static String DEFAULT_ACTIVITY_NODE_NAME() {
		return FlexoLocalization.localizedForKey("activity_default_name");
	}

	@Override
	public String getDefaultName() {
		if (isBeginNode() || isEndNode()) {
			return super.getDefaultName();
		} else {
			return DEFAULT_ACTIVITY_NODE_NAME();
		}
	}

	@Override
	public ActivityPetriGraph getParentPetriGraph() {
		return (ActivityPetriGraph) super.getParentPetriGraph();
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.ACTIVITY;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> v = super.getAllEmbeddedWKFObjects();
		v.addAll(getMetricsValues());
		return v;
	}

	public boolean isSubProcessNode() {
		return this instanceof SubProcessNode;
	}

	public abstract boolean mightHaveOperationPetriGraph();

	@Override
	public boolean hasContainedPetriGraph() {
		return mightHaveOperationPetriGraph() && super.hasContainedPetriGraph();
	}

	public OperationPetriGraph getOperationPetriGraph() {
		if (mightHaveOperationPetriGraph()) {
			return (OperationPetriGraph) getContainedPetriGraph();
		} else {
			return null;
		}
	}

	public void setOperationPetriGraph(OperationPetriGraph aPetriGraph) {
		setContainedPetriGraph(aPetriGraph);
	}

	private Vector<MetricsValue> metricsValues;

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleReference() {
		return role;
	}

	// Used when deserializing
	public void setRoleReference(FlexoModelObjectReference<Role> aRoleReference) {
		this.role = aRoleReference;
		if (this.role != null) {
			this.role.setOwner(this);
		}
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public FlexoProcess getLinkedProcess() {
		if (linkedProcess != null) {
			return linkedProcess.getObject();
		} else {
			return null;
		}
	}

	public void setLinkedProcess(FlexoProcess linkedProcess) {
		if (this.linkedProcess != null) {
			this.linkedProcess.delete();
			this.linkedProcess = null;
		}
		if (linkedProcess != null) {
			this.linkedProcess = new FlexoModelObjectReference<FlexoProcess>(linkedProcess);
			this.linkedProcess.setOwner(this);
		}

	}

	// Used when serializing
	public FlexoModelObjectReference<FlexoProcess> getLinkedProcessReference() {
		return linkedProcess;
	}

	// Used when deserializing
	public void setLinkedProcessReference(FlexoModelObjectReference<FlexoProcess> aProcessReference) {
		this.linkedProcess = aProcessReference;
	}

	private Role observedRole;
	private PropertyChangeListenerRegistrationManager manager;

	public Role getRole() {
		if (role != null) {
			final Role object = getWorkflow().getCachedRole(role);
			if (object != null && object.isCache() && manager == null) {
				String projectURI = role.getEnclosingProjectIdentifier();
				if (projectURI != null) {
					ProjectData data = getProject().getProjectData();
					if (data != null) {
						final FlexoProjectReference projectRef = data.getProjectReferenceWithURI(projectURI, true);
						if (projectRef != null) {
							manager = new PropertyChangeListenerRegistrationManager();
							manager.addListener(FlexoProjectReference.WORKFLOW, new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									manager.delete();
									manager = null;
									if (role != null && role.getObject(true) != null) {
										observedRole = role.getObject(true);
										observedRole.addObserver(AbstractActivityNode.this);
										if (!object.getColor().equals(observedRole.getColor())) {
											AbstractActivityNode.this.setChanged();
											AbstractActivityNode.this.notifyObservers(new RoleColorChange());
										}

									}
								}
							}, projectRef);
						}
					}
				}
			}
			return object;
		} else {
			return null;
		}
	}

	public void setRole(Role aRole) {
		if (aRole != null && aRole.isCache()) {
			aRole = aRole.getUncachedObject();
			if (aRole == null) {
				return;
			}
		}
		if (observedRole != aRole || aRole == null) {
			if (manager != null) {
				manager.delete();
				manager = null;
			}
			if (observedRole != null) {
				observedRole.deleteObserver(this);
				observedRole = null;
			}
			if (role != null) {
				role.delete();
				role = null;
			}
			if (aRole != null) {
				aRole.addObserver(this);
				observedRole = aRole;
				role = new FlexoModelObjectReference<Role>(aRole);
				role.setOwner(this);
			}
			setChanged();
			notifyObservers(new RoleChanged(observedRole, aRole));
		}
	}

	// Roles Accountable, Consulted, Informed

	public Role getRoleA() {
		if (roleA != null) {
			return getWorkflow().getCachedRole(roleA);
		} else {
			return null;
		}
	}

	public void setRoleA(Role aRole) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setRoleA() with " + aRole);
		}
		if (aRole != null && aRole.isCache()) {
			aRole = aRole.getUncachedObject();
			if (aRole == null) {
				return;
			}
		}
		Role oldRole = getRoleA();
		if (oldRole != aRole) {
			if (aRole != null) {
				roleA = new FlexoModelObjectReference<Role>(aRole);
				roleA.setOwner(this);
			} else {
				roleA = null;
			}
			notifyAttributeModification("roleA", oldRole, roleA);
		}
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleAReference() {
		return roleA;
	}

	// Used when deserializing
	public void setRoleAReference(FlexoModelObjectReference<Role> aRoleReference) {
		this.roleA = aRoleReference;
		if (this.roleA != null) {
			this.roleA.setOwner(this);
		}
	}

	public List<Role> getInformedRoles() {
		List<Role> roles = new ArrayList<Role>();
		for (FlexoModelObjectReference<Role> role : informedRoles) {
			roles.add(role.getObject());
		}
		return roles;
	}

	public void setInformedRoles(List<Role> informedRoles) {
		List<FlexoModelObjectReference<Role>> roles = new ArrayList<FlexoModelObjectReference<Role>>();
		for (Role r : informedRoles) {
			FlexoModelObjectReference<Role> ref = new FlexoModelObjectReference<Role>(r);
			ref.setOwner(this);
			roles.add(ref);
		}
		this.informedRoles = roles;
	}

	public void addToInformedRoles(Role role) {
		for (FlexoModelObjectReference<Role> r : informedRoles) {
			if (r.getObject() == role) {
				return;
			}
		}
		FlexoModelObjectReference<Role> ref = new FlexoModelObjectReference<Role>(role);
		informedRoles.add(ref);
		setChanged();
		notifyAttributeModification("informedRoles", null, informedRoles);
	}

	public void removeFromInformedRoles(Role role) {
		for (FlexoModelObjectReference<Role> r : informedRoles) {
			if (r.getObject() == role) {
				informedRoles.remove(r);
			}
			break;
		}
		setChanged();
		notifyAttributeModification("informedRoles", informedRoles, null);
	}

	public List<Role> getConsultedRoles() {
		List<Role> roles = new ArrayList<Role>();
		for (FlexoModelObjectReference<Role> role : consultedRoles) {
			roles.add(role.getObject());
		}
		return roles;
	}

	public void setConsultedRoles(List<Role> consultedRoles) {
		List<FlexoModelObjectReference<Role>> roles = new ArrayList<FlexoModelObjectReference<Role>>();
		for (Role r : consultedRoles) {
			FlexoModelObjectReference<Role> ref = new FlexoModelObjectReference<Role>(r);
			ref.setOwner(this);
			roles.add(ref);
		}

		this.consultedRoles = roles;
	}

	public void addToConsultedRoles(Role role) {
		for (FlexoModelObjectReference<Role> r : consultedRoles) {
			if (r.getObject() == role) {
				return;
			}
		}
		FlexoModelObjectReference<Role> ref = new FlexoModelObjectReference<Role>(role);
		consultedRoles.add(ref);
		setChanged();
		notifyAttributeModification("consultedRoles", null, consultedRoles);
	}

	public void removeFromConsultedRoles(Role role) {
		for (FlexoModelObjectReference<Role> r : consultedRoles) {
			if (r.getObject() == role) {
				consultedRoles.remove(r);
			}
			break;
		}
		setChanged();
		notifyAttributeModification("consultedRoles", consultedRoles, null);
	}

	public List<Role> getAvailableConsultedRoles() {
		List<Role> roles = new ArrayList<Role>(getWorkflow().getAllAssignableRoles());
		roles.removeAll(getConsultedRoles());
		return roles;
	}

	public List<Role> getAvailableInformedRoles() {
		List<Role> roles = new ArrayList<Role>(getWorkflow().getAllAssignableRoles());
		roles.removeAll(getInformedRoles());
		return roles;
	}

	public void addConsultedRole() {
		if (addConsultedRoleActionizer != null) {
			addConsultedRoleActionizer.run(this, null);
		}
	}

	public void removeConsultedRole(Role value) {
		if (removeFromConsultedRoleActionizer != null) {
			removeFromConsultedRoleActionizer.run(value, FlexoUtils.asVector(this));
		}
	}

	public void addInformedRole() {
		if (addInformedRoleActionizer != null) {
			addInformedRoleActionizer.run(this, null);
		}
	}

	public void removeInformedRole(Role value) {
		if (removeFromInformedRoleActionizer != null) {
			removeFromInformedRoleActionizer.run(value, FlexoUtils.asVector(this));
		}
	}

	public List<FlexoModelObjectReference<Role>> getConsultedRoleReferences() {
		if (getConsultedRoles() != null && getConsultedRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getConsultedRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(role));
			}
			return roles;
		}
		return null;
	}

	public void setConsultedRoleReferences(List<FlexoModelObjectReference<Role>> consultedRoleReferences) {
		for (FlexoModelObjectReference<Role> role : consultedRoleReferences) {
			addToConsultedRoleReferences(role);
		}
	}

	public void addToConsultedRoleReferences(FlexoModelObjectReference<Role> role) {
		consultedRoles.add(role);
		if (role != null) {
			role.setOwner(this);
		}
		setChanged();
		notifyAttributeModification("consultedRoles", null, consultedRoles);
	}

	public void removeFromConsultedRoleReferences(FlexoModelObjectReference<Role> role) {
		consultedRoles.remove(role);
		if (role != null) {
			role.setOwner(null);
		}
		setChanged();
		notifyAttributeModification("consultedRoles", null, consultedRoles);
	}

	public List<FlexoModelObjectReference<Role>> getInformedRoleReferences() {
		if (getInformedRoles() != null && getInformedRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getInformedRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(role));
			}
			return roles;
		}
		return null;
	}

	public void setInformedRoleReferences(List<FlexoModelObjectReference<Role>> informedRoleReferences) {
		for (FlexoModelObjectReference<Role> role : informedRoleReferences) {
			addToInformedRoleReferences(role);
		}
	}

	public void addToInformedRoleReferences(FlexoModelObjectReference<Role> role) {
		informedRoles.add(role);
		if (role != null) {
			role.setOwner(this);
		}
		setChanged();
		notifyAttributeModification("informedRoles", null, informedRoles);
	}

	public void removeFromInformedRoleReferences(FlexoModelObjectReference<Role> role) {
		informedRoles.remove(role);
		if (role != null) {
			role.setOwner(null);
		}
		setChanged();
		notifyAttributeModification("informedRoles", null, informedRoles);
	}

	@Override
	public Vector<MetricsValue> getMetricsValues() {
		return metricsValues;
	}

	public void setMetricsValues(Vector<MetricsValue> metricsValues) {
		this.metricsValues = metricsValues;
		setChanged();
	}

	@Override
	public void addToMetricsValues(MetricsValue value) {
		if (value.getMetricsDefinition() != null) {
			metricsValues.add(value);
			value.setOwner(this);
			setChanged();
			notifyObservers(new MetricsValueAdded(value, "metricsValues"));
		}
	}

	@Override
	public void removeFromMetricsValues(MetricsValue value) {
		metricsValues.remove(value);
		value.setOwner(null);
		setChanged();
		notifyObservers(new MetricsValueRemoved(value, "metricsValues"));
	}

	@Override
	public void updateMetricsValues() {
		getWorkflow().updateMetricsForActivity(this);
	}

	public void addMetrics() {
		if (addMetricsActionizer != null) {
			addMetricsActionizer.run(this, null);
		}
	}

	public void deleteMetrics(MetricsValue value) {
		if (deleteMetricsActionizer != null) {
			deleteMetricsActionizer.run(value, null);
		}
	}

	public Vector<OperationNode> getAllOperationNodes() {
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllOperationNodes();
		}
		return new Vector<OperationNode>();
	}

	/**
	 * Returns all the operation nodes embedded in this activity. This returns also operation nodes embedded in LOOPOperator and
	 * SelfExecutableNode.
	 * 
	 * @return all the operation nodes embedded in this activity.
	 */
	public Vector<OperationNode> getAllEmbeddedOperationNodes() {
		// TODO: optimize me later !!!
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllEmbeddedOperationNodes();
		}
		return new Vector<OperationNode>();
	}

	/**
	 * Returns all the Operation nodes that are in the underlying operation petrigraph and the ones embedded by LOOPOperator and
	 * SelfExecutableNode. This is done recursively on all nodes.
	 * 
	 * @return all the operation nodes embedded in the underlying operation petri graph.
	 */
	public Vector<OperationNode> getAllEmbeddedSortedOperationNodes() {
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllEmbeddedSortedOperationNodes();
		}
		return new Vector<OperationNode>();
	}

	/**
	 * Returns all the action nodes embedded in this activity. This returns also action nodes embedded in LOOPOperator and
	 * SelfExecutableNode.
	 * 
	 * @return all the action nodes embedded in this activity.
	 */
	public Vector<ActionNode> getAllEmbeddedActionNodes() {
		if (getOperationPetriGraph() != null) {
			Vector<ActionNode> v = new Vector<ActionNode>();
			for (OperationNode o : getOperationPetriGraph().getAllEmbeddedOperationNodes()) {
				v.addAll(o.getAllActionNodes());
			}
		}
		return new Vector<ActionNode>();
	}

	public OperationNode getOperationNodeNamed(String name) {
		for (OperationNode node : getAllEmbeddedOperationNodes()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public Enumeration getSortedOperationNodes() {
		disableObserving();
		Object[] o = FlexoIndexManager.sortArray(getAllOperationNodes().toArray(new OperationNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<EventNode> getAllEventNodes() {
		// TODO: optimize me later !!!
		Vector<EventNode> returned = new Vector<EventNode>();
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllEventNodes();
		}
		return returned;
	}

	public Vector<OperatorNode> getAllOperatorNodes() {
		// TODO: optimize me later !!!
		Vector<OperatorNode> returned = new Vector<OperatorNode>();
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllOperatorNodes();
		}
		return returned;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	public boolean isEmbedded() {
		return getParentPetriGraph() != null && getParentPetriGraph().getContainer() != getProcess();
	}

	@Override
	public String getFullyQualifiedName() {
		if (getProcess() == null) {
			return "Process:NULL." + formattedString(getNodeName());
		}
		return getProcess().getFullyQualifiedName() + "." + formattedString(getNodeName());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof RoleInserted || dataModification instanceof RoleRemoved
				|| dataModification instanceof RoleNameChange || dataModification instanceof RoleColorChange
				|| "color".equals(dataModification.propertyName())) {
			if (dataModification instanceof RoleRemoved) {
				if (((RoleRemoved) dataModification).oldValue() == getRole()) {
					setRole(null);
				}

			}
			setChanged();
			notifyObservers(dataModification);
		}
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class ActivityMustHaveARole extends ValidationRule<ActivityMustHaveARole, AbstractActivityNode> {
		public ActivityMustHaveARole() {
			super(AbstractActivityNode.class, "activity_must_have_a_role");
		}

		@Override
		public ValidationIssue<ActivityMustHaveARole, AbstractActivityNode> applyValidation(final AbstractActivityNode activity) {
			if (activity.getNodeType() == NodeType.NORMAL && !(activity instanceof SelfExecutableActivityNode)
					&& activity.getRole() == null) {
				ValidationError<ActivityMustHaveARole, AbstractActivityNode> error = new ValidationError<ActivityMustHaveARole, AbstractActivityNode>(
						this, activity, "activity_($object.name)_has_no_role");
				for (Role role : activity.getProcess().getWorkflow().getRoleList().getRoles()) {
					error.addToFixProposals(new SetRoleToExistingRole(role));
				}
				error.addToFixProposals(new CreateAndAssignNewRole());
				error.addToFixProposals(new DeletionFixProposal<ActivityMustHaveARole, AbstractActivityNode>("delete_this_activity"));
				return error;
			}
			return null;
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType != CodeType.PROTOTYPE;
		}

		public static class SetRoleToExistingRole extends FixProposal<ActivityMustHaveARole, AbstractActivityNode> {
			public Role role;

			public SetRoleToExistingRole(Role aRole) {
				super("set_($object.name)_role_to_($role.name)");
				role = aRole;
			}

			@Override
			protected void fixAction() {
				getObject().setRole(role);
			}
		}

		public static class CreateAndAssignNewRole extends ParameteredFixProposal<ActivityMustHaveARole, AbstractActivityNode> {
			public CreateAndAssignNewRole() {
				super("create_and_assign_new_role_to_activity_($object.name)", "newRoleName", "enter_a_name_for_the_new_role",
						FlexoLocalization.localizedForKey("new_role"));
			}

			@Override
			protected void fixAction() {
				Role newRole;
				String newRoleName = (String) getValueForParameter("newRoleName");
				RoleList roleList = getObject().getProcess().getWorkflow().getRoleList();
				newRole = new Role(roleList.getWorkflow(), newRoleName);
				try {
					roleList.addToRoles(newRole);
				} catch (DuplicateRoleException e) {
					try {
						newRole.setName(newRole.getIsSystemRole() ? roleList.getNextNewSystemRoleName() : roleList.getNextNewUserRoleName());
						roleList.addToRoles(newRole);
					} catch (DuplicateRoleException e1) {
						// should never happen
						e1.printStackTrace();
					}
				}
				getObject().setRole(newRole);
			}
		}

	}

	public static class ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed extends
			ValidationRule<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed, AbstractActivityNode> {
		public ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed() {
			super(AbstractActivityNode.class, "activity_could_not_define_operation_petri_graph_when_not_allowed");
		}

		@Override
		public ValidationIssue<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed, AbstractActivityNode> applyValidation(
				final AbstractActivityNode activity) {
			if (activity.getContainedPetriGraph() != null && !activity.mightHaveOperationPetriGraph()) {
				ValidationError<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed, AbstractActivityNode> error = new ValidationError<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed, AbstractActivityNode>(
						this, activity,
						"activity_($object.name)_define_an_operation_petri_graph_while_this_is_forbidden_for_this_kind_of_activity");
				error.addToFixProposals(new DeleteInconsistentPetriGraph());
				return error;
			}
			return null;
		}

		public static class DeleteInconsistentPetriGraph extends
				FixProposal<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed, AbstractActivityNode> {
			public DeleteInconsistentPetriGraph() {
				super("delete_inconsistent_petri_graph");
			}

			@Override
			protected void fixAction() {
				if (getObject() != null) {
					if (getObject().getContainedPetriGraph() != null) {
						getObject().getContainedPetriGraph().delete();
					}
					getObject().setOperationPetriGraph(null);
				}
			}
		}
	}

	public String getAcronym() {
		return _acronym;
	}

	public void setAcronym(String acronym) {
		String old = _acronym;
		_acronym = acronym;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("acronym", old, _acronym));
	}

	public static final String getTypeName() {
		return "ABSTRACTACTIVITY";
	}

	protected Color roleTextColor = FlexoColor.GRAY_COLOR;

	private Role cachedRole;

	public Color getRoleTextColor() {
		return roleTextColor;
	}

	public void setRoleTextColor(Color roleTextColor) {
		Color old = roleTextColor;
		this.roleTextColor = roleTextColor;
		setChanged();
		notifyObservers(new RoleTextColorChanged(old, roleTextColor));
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public FlexoCSS getCalculatedCssSheet() {
		if (cssSheet == null) {
			if (getProcess() != null) {
				return getProcess().getCalculatedCssSheet();
			} else {
				return getProject().getCssSheet();
			}
		} else {
			return cssSheet;
		}
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public FlexoCSS getCssSheet() {
		return cssSheet;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void setCssSheet(FlexoCSS cssSheet) {
		FlexoCSS old = this.cssSheet;
		this.cssSheet = cssSheet;
		setChanged();
		notifyAttributeModification("cssSheet", old, cssSheet);
	}

	/**
	 * Wheter there is within all the operations of this activity at least one operation associated to a component
	 * 
	 * @return
	 */
	public boolean hasWOComponent() {
		for (OperationNode op : getAllEmbeddedOperationNodes()) {
			if (op.hasWOComponent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
		if (reference == role) {
			setRole(role.getObject());
		}
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {

	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference) {
		setChanged();
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		if (role == reference) {
			setRole(null);
		} else if (roleA == reference) {
			setRoleA(null);
		} else if (informedRoles.contains(reference)) {
			removeFromInformedRoleReferences((FlexoModelObjectReference<Role>) reference);
		} else if (consultedRoles.contains(reference)) {
			removeFromConsultedRoleReferences((FlexoModelObjectReference<Role>) reference);
		}
	}

	public AbstractActivityStatistics getStatistics() {
		if (statistics == null) {
			statistics = new AbstractActivityStatistics(this);
		}
		return statistics;
	}

	public void refreshStatistics() {
		getStatistics().refresh();
	}

	public String getRACICodeForRole(Role aRole) {
		if (aRole == null) {
			return "";
		}
		if (aRole.equals(getRole())) {
			return "R";
		}
		if (aRole.equals(getRoleA())) {
			return "A";
		}
		if (getConsultedRoles().contains(aRole)) {
			return "C";
		}
		if (getInformedRoles().contains(aRole)) {
			return "I";
		}

		return "";
	}

	public List<EventNode> getAllBoundaryEvents() {
		ArrayList<EventNode> reply = new ArrayList<EventNode>();
		for (EventNode n : getProcess().getAllEventNodes()) {
			if (this.equals(n.getBoundaryOf())) {
				reply.add(n);
			}
		}
		return reply;
	}

	@Override
	public void delete() {
		for (EventNode boundaryEvent : getAllBoundaryEvents()) {
			boundaryEvent.delete();
		}
		if (role != null) {
			role.delete();
			role = null;
		}
		if (roleA != null) {
			roleA.delete();
			roleA = null;
		}

		for (FlexoModelObjectReference<Role> ref : informedRoles) {
			ref.delete();
		}
		informedRoles.clear();
		for (FlexoModelObjectReference<Role> ref : consultedRoles) {
			ref.delete();
		}
		consultedRoles.clear();
		super.delete();
	}

	@Override
	protected int getDefaultWidth() {
		return 150;
	}

	@Override
	protected int getDefaultHeight() {
		return 90;
	}
}
