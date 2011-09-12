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
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoUtils;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.stats.AbstractActivityStatistics;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
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
import org.openflexo.foundation.wkf.action.AddToAccountableRole;
import org.openflexo.foundation.wkf.action.AddToConsultedRole;
import org.openflexo.foundation.wkf.action.AddToInformedRole;
import org.openflexo.foundation.wkf.action.AddToResponsibleRole;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.action.GenerateActivityScreenshot;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.RemoveFromAccountableRole;
import org.openflexo.foundation.wkf.action.RemoveFromConsultedRole;
import org.openflexo.foundation.wkf.action.RemoveFromInformedRole;
import org.openflexo.foundation.wkf.action.RemoveFromResponsibleRole;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleInserted;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.wkf.dm.RoleTextColorChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * An abstract activity represents an activity that can be a node of the
 * activity petri graph. This is either an ActivityFlexoNode or a
 * SubProcessNode.
 *
 * @author sguerin
 *
 */
public abstract class AbstractActivityNode extends FatherNode implements MetricsValueOwner, FlexoObserver
{

	private static final Logger logger = Logger.getLogger(AbstractActivityNode.class.getPackage().getName());

	private Role _role;

	private Role roleA;
	private Role roleC;
	private Role roleI;

	private Vector<Role> responsibleRoles;
	private Vector<Role> accountableRoles;
	private Vector<Role> consultedRoles;
	private Vector<Role> informedRoles;

	private String documentationUrl;
	private FlexoProcess linkedProcess;

	private String _acronym;

	private FlexoCSS cssSheet;

	/**
	 * Deprecated: kept for backward compatibility
	 */
	private String _roleName;

	private transient AbstractActivityStatistics statistics;

	public static FlexoActionizer<AddActivityMetricsValue, AbstractActivityNode, WKFObject> addMetricsActionizer;
	public static FlexoActionizer<DeleteMetricsValue,MetricsValue,MetricsValue> deleteMetricsActionizer;

	public static FlexoActionizer<AddToResponsibleRole, AbstractActivityNode, AbstractActivityNode> addResponsibleRoleActionizer;
	public static FlexoActionizer<RemoveFromResponsibleRole, Role, AbstractActivityNode> removeFromResponsibleRoleActionizer;

	public static FlexoActionizer<AddToAccountableRole, AbstractActivityNode, AbstractActivityNode> addAccountableRoleActionizer;
	public static FlexoActionizer<RemoveFromAccountableRole, Role, AbstractActivityNode> removeFromAccountableRoleActionizer;

	public static FlexoActionizer<AddToConsultedRole, AbstractActivityNode, AbstractActivityNode> addConsultedRoleActionizer;
	public static FlexoActionizer<RemoveFromConsultedRole, Role, AbstractActivityNode> removeFromConsultedRoleActionizer;

	public static FlexoActionizer<AddToInformedRole, AbstractActivityNode, AbstractActivityNode> addInformedRoleActionizer;
	public static FlexoActionizer<RemoveFromInformedRole, Role, AbstractActivityNode> removeFromInformedRoleActionizer;

	/**
	 * Default constructor
	 */
	public AbstractActivityNode(FlexoProcess process)
	{
		super(process);
		metricsValues = new Vector<MetricsValue>();
		this.responsibleRoles = new Vector<Role>();
		this.accountableRoles = new Vector<Role>();
		this.consultedRoles = new Vector<Role>();
		this.informedRoles = new Vector<Role>();
		_role = null;
	}

	static {
		addActionForClass(OpenOperationLevel.actionType, AbstractActivityNode.class);
		addActionForClass(GenerateActivityScreenshot.actionType, AbstractActivityNode.class);
	}

	/**
	 * Default inspector name: implemented in sub-classes !
	 */
	@Override
	public String getInspectorName()
	{
		return Inspectors.WKF.ABSTRACT_ACTIVITY_NODE_INSPECTOR;
	}

	public static String DEFAULT_ACTIVITY_NODE_NAME()
	{
		return FlexoLocalization.localizedForKey("activity_default_name");
	}

	@Override
	public String getDefaultName()
	{
		if (isBeginNode() || isEndNode()) {
			return super.getDefaultName();
		} else {
			return DEFAULT_ACTIVITY_NODE_NAME();
		}
	}

	// Used for old models, deprecated now !
	public void finalizeRoleLinking()
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalizeRoleLinking() called");
		}
		if (_roleName != null) {
			if (getProcess().getRoleList().roleWithName(_roleName) != null) {
				_role = getProcess().getRoleList().roleWithName(_roleName);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Role " + _roleName + " has been found and linked.");
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find Role " + _roleName + " in process " + getProcess().getName());
				}
			}
		}
	}

	@Override
	public ActivityPetriGraph getParentPetriGraph()
	{
		return (ActivityPetriGraph)super.getParentPetriGraph();
	}

	@Override
	public FlexoLevel getLevel()
	{
		return FlexoLevel.ACTIVITY;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> v = super.getAllEmbeddedWKFObjects();
		v.addAll(getMetricsValues());
		return v;
	}

	public boolean isSubProcessNode()
	{
		return this instanceof SubProcessNode;
	}

	public abstract boolean mightHaveOperationPetriGraph();

	@Override
	public boolean hasContainedPetriGraph()
	{
		return mightHaveOperationPetriGraph() && super.hasContainedPetriGraph();
	}

	public OperationPetriGraph getOperationPetriGraph()
	{
		if (mightHaveOperationPetriGraph()) {
			return (OperationPetriGraph) getContainedPetriGraph();
		} else {
			return null;
		}
	}

	public void setOperationPetriGraph(OperationPetriGraph aPetriGraph)
	{
		setContainedPetriGraph(aPetriGraph);
	}

	public Role getRole()
	{
		if (_role == null && !isDeserializing()) {
			if (inheritedRoleName != null) {
				_role = getProject().getWorkflow().getRoleList().roleWithName(inheritedRoleName);
				if (_role == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find Role " + inheritedRoleName + " in role list");
					}
				} else {
					_role.addObserver(this);
				}
			}
		}
		return _role;
	}

	private String inheritedRoleName;

	private Vector<MetricsValue> metricsValues;

	/**
	 * Kept for backward compatibility only
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public String getInheritedRoleName()
	{
		return null;
	}

	/**
	 * Kept for backward compatibility only
	 * @deprecated
	 */
	@Deprecated
	public void setInheritedRoleName(String aRoleName)
	{
		this.inheritedRoleName = aRoleName;
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleReference()
	{
		if (getRole()!=null) {
			return new FlexoModelObjectReference<Role>(getProject(),getRole());
		} else {
			return null;
		}
	}

	// Used when deserializing
	public void setRoleReference(FlexoModelObjectReference<Role> aRoleReference)
	{
		setRole(aRoleReference.getObject(true));
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public FlexoProcess getLinkedProcess() {
		return linkedProcess;
	}

	public void setLinkedProcess(FlexoProcess linkedProcess) {
		this.linkedProcess = linkedProcess;
	}

	// Used when serializing
	public FlexoModelObjectReference<FlexoModelObject> getLinkedProcessReference() {
		if (getLinkedProcess() != null) {
			return new FlexoModelObjectReference<FlexoModelObject>(getProject(), getLinkedProcess());
		} else {
			return null;
		}
	}

	// Used when deserializing
	public void setLinkedProcessReference(FlexoModelObjectReference<FlexoProcess> aProcessReference) {
		if(aProcessReference.getResource().equals(getProcess().getFlexoResource())){
			setLinkedProcess(getProcess());
		} else {
			setLinkedProcess(aProcessReference.getObject(true));
		}
	}


	public void setRole(Role aRole)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setRole() with " + aRole);
		}
		Role oldRole = _role;
		if (oldRole != aRole) {
			_role = aRole;
			if (oldRole != null) {
				oldRole.deleteObserver(this);
			}
			if (_role != null) {
				_role.addObserver(this);
			}
			setChanged();
			notifyObservers(new RoleChanged(oldRole, aRole));
		}
	}

	public String getRoleName()
	{
		if (getRole() != null) {
			return getRole().getName();
		} else {
			return null;
		}
	}

	/**
	 * Deprecated: kept for backward compatibility during deserialization
	 * process from version 1.0
	 *
	 * @deprecated
	 */
	@Deprecated
	public void setRoleName(String aRoleName)
	{
		_roleName = aRoleName;
	}

	// Roles Accountable, Consulted, Informed

	public Role getRoleA() {
		return roleA;
	}

	public void setRoleA(Role aRole)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setRoleA() with " + aRole);
		}
		Role oldRole = roleA;
		if (oldRole != aRole) {
			roleA = aRole;
			if (oldRole != null) {
				oldRole.deleteObserver(this);
			}
			if (roleA != null) {
				roleA.addObserver(this);
			}
			notifyAttributeModification("roleA", oldRole, roleA);
		}
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleAReference()
	{
		if (getRoleA()!=null) {
			return new FlexoModelObjectReference<Role>(getProject(),getRoleA());
		} else {
			return null;
		}
	}

	// Used when deserializing
	public void setRoleAReference(FlexoModelObjectReference<Role> aRoleReference)
	{
		setRoleA(aRoleReference.getObject(true));
	}

	public Role getRoleC() {
		return roleC;
	}

	public void setRoleC(Role aRole)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setRoleC() with " + aRole);
		}
		Role oldRole = roleC;
		if (oldRole != aRole) {
			roleC = aRole;
			if (oldRole != null) {
				oldRole.deleteObserver(this);
			}
			if (roleC != null) {
				roleC.addObserver(this);
			}
			notifyAttributeModification("roleC", oldRole, roleC);
		}
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleCReference()
	{
		if (getRoleC()!=null) {
			return new FlexoModelObjectReference<Role>(getProject(),getRoleC());
		} else {
			return null;
		}
	}

	// Used when deserializing
	public void setRoleCReference(FlexoModelObjectReference<Role> aRoleReference)
	{
		setRoleC(aRoleReference.getObject(true));
	}

	public Role getRoleI() {
		return roleI;
	}

	public void setRoleI(Role aRole)
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setRoleI() with " + aRole);
		}
		Role oldRole = roleI;
		if (oldRole != aRole) {
			roleI = aRole;
			if (oldRole != null) {
				oldRole.deleteObserver(this);
			}
			if (roleI != null) {
				roleI.addObserver(this);
			}
			setChanged();
			notifyAttributeModification("roleI", oldRole, roleI);
		}
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleIReference()
	{
		if (getRoleI()!=null) {
			return new FlexoModelObjectReference<Role>(getProject(),getRoleI());
		} else {
			return null;
		}
	}

	// Used when deserializing
	public void setRoleIReference(FlexoModelObjectReference<Role> aRoleReference)
	{
		setRoleI(aRoleReference.getObject(true));
	}

	public Vector<Role> getResponsibleRoles() {
		if (responsibleRoles.size() == 0 && getRole() != null) {
			responsibleRoles.add(getRole());
			// Maybe nullify role?
		}
		return responsibleRoles;
	}

	public void setResponsibleRoles(Vector<Role> responsibleRoles) {
		this.responsibleRoles = responsibleRoles;
	}

	public void addToResponsibleRoles(Role role) {
		if (!responsibleRoles.contains(role)) {
			responsibleRoles.add(role);
			setChanged();
			notifyAttributeModification("responsibleRoles", null, responsibleRoles);
		}
	}

	public void removeFromResponsibleRoles(Role role) {
		responsibleRoles.remove(role);
		setChanged();
		notifyAttributeModification("responsibleRoles", responsibleRoles, null);
	}

	public Vector<Role> getInformedRoles() {
		if (informedRoles.size() == 0 && getRoleI() != null) {
			informedRoles.add(getRoleI());
			// Maybe nullify roleI?
		}
		return informedRoles;
	}

	public void setInformedRoles(Vector<Role> informedRoles) {
		this.informedRoles = informedRoles;
	}

	public void addToInformedRoles(Role role) {
		if (!informedRoles.contains(role)) {
			informedRoles.add(role);
			setChanged();
			notifyAttributeModification("informedRoles", null, informedRoles);
		}
	}

	public void removeFromInformedRoles(Role role) {
		informedRoles.remove(role);
		setChanged();
		notifyAttributeModification("informedRoles", informedRoles, null);
	}

	public Vector<Role> getConsultedRoles() {
		if (consultedRoles.size() == 0 && getRoleC() != null) {
			consultedRoles.add(getRoleC());
			// Maybe nullify roleC?
		}
		return consultedRoles;
	}

	public void setConsultedRoles(Vector<Role> consultedRoles) {
		this.consultedRoles = consultedRoles;
	}

	public void addToConsultedRoles(Role role) {
		if (!consultedRoles.contains(role)) {
			consultedRoles.add(role);
			setChanged();
			notifyAttributeModification("consultedRoles", null, consultedRoles);
		}
	}

	public void removeFromConsultedRoles(Role role) {
		consultedRoles.remove(role);
		setChanged();
		notifyAttributeModification("consultedRoles", consultedRoles, null);
	}

	public Vector<Role> getAccountableRoles() {
		if (accountableRoles.size() == 0 && getRoleA() != null) {
			accountableRoles.add(getRoleA());
			// Maybe nullify roleA?
		}
		return accountableRoles;
	}

	public void setAccountableRoles(Vector<Role> accountableRoles) {
		this.accountableRoles = accountableRoles;
	}

	public void addToAccountableRoles(Role role) {
		if (!accountableRoles.contains(role)) {
			accountableRoles.add(role);
			setChanged();
			notifyAttributeModification("accountableRoles", null, accountableRoles);
		}
	}

	public void removeFromAccountableRoles(Role role) {
		accountableRoles.remove(role);
		setChanged();
		notifyAttributeModification("accountableRoles", accountableRoles, null);
	}

	public Vector<Role> getAvailableResponsibleRoles() {
		Vector<Role> roles = getWorkflow().getAllAssignableRoles();
		roles.removeAll(getResponsibleRoles());
		return roles;
	}

	public Vector<Role> getAvailableAccountableRoles() {
		Vector<Role> roles = getWorkflow().getAllAssignableRoles();
		roles.removeAll(getAccountableRoles());
		return roles;
	}

	public Vector<Role> getAvailableConsultedRoles() {
		Vector<Role> roles = getWorkflow().getAllAssignableRoles();
		roles.removeAll(getConsultedRoles());
		return roles;
	}

	public Vector<Role> getAvailableInformedRoles() {
		Vector<Role> roles = getWorkflow().getAllAssignableRoles();
		roles.removeAll(getInformedRoles());
		return roles;
	}

	public void addResponsibleRole() {
		if (addResponsibleRoleActionizer != null) {
			addResponsibleRoleActionizer.run(this, null);
		}
	}

	public void removeResponsibleRole(Role value) {
		if (removeFromResponsibleRoleActionizer != null) {
			removeFromResponsibleRoleActionizer.run(value, FlexoUtils.asVector(this));
		}
	}

	public void addAccountableRole() {
		if (addAccountableRoleActionizer != null) {
			addAccountableRoleActionizer.run(this, null);
		}
	}

	public void removeAccountableRole(Role value) {
		if (removeFromAccountableRoleActionizer != null) {
			removeFromAccountableRoleActionizer.run(value, FlexoUtils.asVector(this));
		}
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

	public Vector<FlexoModelObjectReference<Role>> getResponsibleRoleReferences() {
		if (getResponsibleRoles() != null && getResponsibleRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getResponsibleRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(getProject(), role));
			}
			return roles;
		}
		return null;
	}

	public void setResponsibleRoleReferences(Vector<FlexoModelObjectReference<Role>> responsibleRoleReferences) {
		for (FlexoModelObjectReference<Role> role : responsibleRoleReferences) {
			addToResponsibleRoleReferences(role);
		}
	}

	public void addToResponsibleRoleReferences(FlexoModelObjectReference<Role> role) {
		addToResponsibleRoles(role.getObject(true));
	}

	public void removeFromResponsibleRoleReferences(FlexoModelObjectReference<Role> role) {
		// Unused
	}

	public Vector<FlexoModelObjectReference<Role>> getAccountableRoleReferences() {
		if (getAccountableRoles() != null && getAccountableRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getAccountableRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(getProject(), role));
			}
			return roles;
		}
		return null;
	}

	public void setAccountableRoleReferences(Vector<FlexoModelObjectReference<Role>> accountableRoleReferences) {
		for (FlexoModelObjectReference<Role> role : accountableRoleReferences) {
			addToAccountableRoleReferences(role);
		}
	}

	public void addToAccountableRoleReferences(FlexoModelObjectReference<Role> role) {
		addToAccountableRoles(role.getObject(true));
	}

	public void removeFromAccountableRoleReferences(FlexoModelObjectReference<Role> role) {
		// Unused
	}

	public Vector<FlexoModelObjectReference<Role>> getConsultedRoleReferences() {
		if (getConsultedRoles() != null && getConsultedRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getConsultedRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(getProject(), role));
			}
			return roles;
		}
		return null;
	}

	public void setConsultedRoleReferences(Vector<FlexoModelObjectReference<Role>> consultedRoleReferences) {
		for (FlexoModelObjectReference<Role> role : consultedRoleReferences) {
			addToConsultedRoleReferences(role);
		}
	}

	public void addToConsultedRoleReferences(FlexoModelObjectReference<Role> role) {
		addToConsultedRoles(role.getObject(true));
	}

	public void removeFromConsultedRoleReferences(FlexoModelObjectReference<Role> role) {
		// Unused
	}

	public Vector<FlexoModelObjectReference<Role>> getInformedRoleReferences() {
		if (getInformedRoles() != null && getInformedRoles().size() > 0) {
			Vector<FlexoModelObjectReference<Role>> roles = new Vector<FlexoModelObjectReference<Role>>();
			for (Role role : getInformedRoles()) {
				roles.add(new FlexoModelObjectReference<Role>(getProject(), role));
			}
			return roles;
		}
		return null;
	}

	public void setInformedRoleReferences(Vector<FlexoModelObjectReference<Role>> informedRoleReferences) {
		for (FlexoModelObjectReference<Role> role : informedRoleReferences) {
			addToInformedRoleReferences(role);
		}
	}

	public void addToInformedRoleReferences(FlexoModelObjectReference<Role> role) {
		addToInformedRoles(role.getObject(true));
	}

	public void removeFromInformedRoleReferences(FlexoModelObjectReference<Role> role) {
		// Unused
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
		if (value.getMetricsDefinition()!=null) {
			metricsValues.add(value);
			value.setOwner(this);
			setChanged();
			notifyObservers(new MetricsValueAdded(value,"metricsValues"));
		}
	}

	@Override
	public void removeFromMetricsValues(MetricsValue value) {
		metricsValues.remove(value);
		value.setOwner(null);
		setChanged();
		notifyObservers(new MetricsValueRemoved(value,"metricsValues"));
	}

	@Override
	public void updateMetricsValues() {
		getWorkflow().updateMetricsForActivity(this);
	}

	public void addMetrics() {
		if (addMetricsActionizer!=null) {
			addMetricsActionizer.run(this, null);
		}
	}

	public void deleteMetrics(MetricsValue value) {
		if (deleteMetricsActionizer!=null) {
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
	 * Returns all the operation nodes embedded in this activity. This returns also operation nodes embedded in LOOPOperator and SelfExecutableNode.
	 * @return all the operation nodes embedded in this activity.
	 */
	public Vector<OperationNode> getAllEmbeddedOperationNodes()
	{
		// TODO: optimize me later !!!
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllEmbeddedOperationNodes();
		}
		return new Vector<OperationNode>();
	}

	/**
	 * Returns all the Operation nodes that are in the underlying operation petrigraph and the ones embedded by LOOPOperator and SelfExecutableNode.
	 * This is done recursively on all nodes.
	 * @return all the operation nodes embedded in the underlying operation petri graph.
	 */
	public Vector<OperationNode> getAllEmbeddedSortedOperationNodes() {
		if (getOperationPetriGraph()!=null) {
			return getOperationPetriGraph().getAllEmbeddedSortedOperationNodes();
		}
		return new Vector<OperationNode>();
	}

	/**
	 * Returns all the action nodes embedded in this activity. This returns also action nodes embedded in LOOPOperator and SelfExecutableNode.
	 * @return all the action nodes embedded in this activity.
	 */
	public Vector<ActionNode> getAllEmbeddedActionNodes()
	{
		if (getOperationPetriGraph() != null) {
			Vector<ActionNode> v = new Vector<ActionNode>();
			for (OperationNode o : getOperationPetriGraph().getAllEmbeddedOperationNodes()) {
				v.addAll(o.getAllActionNodes());
			}
		}
		return new Vector<ActionNode>();
	}

	public OperationNode getOperationNodeNamed(String name)
	{
		for (OperationNode node : getAllEmbeddedOperationNodes()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public Enumeration getSortedOperationNodes()
	{
		disableObserving();
		Object[] o = FlexoIndexManager.sortArray(getAllOperationNodes().toArray(new OperationNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<EventNode> getAllEventNodes()
	{
		// TODO: optimize me later !!!
		Vector<EventNode> returned = new Vector<EventNode>();
		if (getOperationPetriGraph() != null) {
			return getOperationPetriGraph().getAllEventNodes();
		}
		return returned;
	}

	public Vector<OperatorNode> getAllOperatorNodes()
	{
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
		return getParentPetriGraph()!=null && getParentPetriGraph().getContainer() != getProcess();
	}

	@Override
	public String getFullyQualifiedName()
	{
		if (getProcess() == null) {
			return "Process:NULL." + formattedString(getNodeName());
		}
		return getProcess().getFullyQualifiedName() + "." + formattedString(getNodeName());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if ((dataModification instanceof RoleInserted) || (dataModification instanceof RoleRemoved)
				|| (dataModification instanceof RoleNameChange) || (dataModification instanceof RoleColorChange)
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

	public static class ActivityMustHaveARole extends ValidationRule<ActivityMustHaveARole,AbstractActivityNode>
	{
		public ActivityMustHaveARole()
		{
			super(AbstractActivityNode.class, "activity_must_have_a_role");
		}

		@Override
		public ValidationIssue<ActivityMustHaveARole,AbstractActivityNode> applyValidation(final AbstractActivityNode activity)
		{
			if ((activity.getNodeType() == NodeType.NORMAL) && !(activity instanceof SelfExecutableActivityNode)
					&& (activity.getRole() == null)) {
				ValidationError<ActivityMustHaveARole,AbstractActivityNode> error
				= new ValidationError<ActivityMustHaveARole,AbstractActivityNode>(this, activity, "activity_($object.name)_has_no_role");
				for (Role role : activity.getProcess().getWorkflow().getRoleList().getRoles()) {
					error.addToFixProposals(new SetRoleToExistingRole(role));
				}
				error.addToFixProposals(new CreateAndAssignNewRole());
				error.addToFixProposals(new DeletionFixProposal<ActivityMustHaveARole,AbstractActivityNode>("delete_this_activity"));
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
		public boolean isValidForTarget(TargetType targetType)
		{
			return targetType != CodeType.PROTOTYPE;
		}

		public static class SetRoleToExistingRole extends FixProposal<ActivityMustHaveARole,AbstractActivityNode>
		{
			public Role role;

			public SetRoleToExistingRole(Role aRole)
			{
				super("set_($object.name)_role_to_($role.name)");
				role = aRole;
			}

			@Override
			protected void fixAction()
			{
				getObject().setRole(role);
			}
		}

		public static class CreateAndAssignNewRole extends ParameteredFixProposal<ActivityMustHaveARole,AbstractActivityNode>
		{
			public CreateAndAssignNewRole()
			{
				super("create_and_assign_new_role_to_activity_($object.name)", "newRoleName", "enter_a_name_for_the_new_role",
						FlexoLocalization.localizedForKey("new_role"));
			}

			@Override
			protected void fixAction()
			{
				Role newRole;
				String newRoleName = (String) getValueForParameter("newRoleName");
				RoleList roleList = getObject().getProcess().getWorkflow().getRoleList();
				newRole = new Role(roleList.getWorkflow(), newRoleName);
				try {
					roleList.addToRoles(newRole);
				} catch (DuplicateRoleException e) {
					try {
						newRole.setName(newRole.getIsSystemRole()?roleList.getNextNewSystemRoleName():roleList.getNextNewUserRoleName());
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

	public static class ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed extends ValidationRule<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed,AbstractActivityNode>
	{
		public ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed()
		{
			super(AbstractActivityNode.class, "activity_could_not_define_operation_petri_graph_when_not_allowed");
		}

		@Override
		public ValidationIssue<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed,AbstractActivityNode> applyValidation(final AbstractActivityNode activity)
		{
			if (activity.getContainedPetriGraph() != null && !activity.mightHaveOperationPetriGraph()) {
				ValidationError<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed,AbstractActivityNode> error
				= new ValidationError<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed,AbstractActivityNode>
				(this, activity, "activity_($object.name)_define_an_operation_petri_graph_while_this_is_forbidden_for_this_kind_of_activity");
				error.addToFixProposals(new DeleteInconsistentPetriGraph());
				return error;
			}
			return null;
		}

		public static class DeleteInconsistentPetriGraph extends FixProposal<ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed,AbstractActivityNode>
		{
			public DeleteInconsistentPetriGraph()
			{
				super("delete_inconsistent_petri_graph");
			}

			@Override
			protected void fixAction()
			{
				if (getObject() != null) {
					if (getObject().getContainedPetriGraph() != null) {
						getObject().getContainedPetriGraph().delete();
					}
					getObject().setOperationPetriGraph(null);
				}
			}
		}
	}



	public String getAcronym()
	{
		return _acronym;
	}

	public void setAcronym(String acronym)
	{
		String old = _acronym;
		_acronym = acronym;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("acronym", old, _acronym));
	}

	public static final String getTypeName()
	{
		return "ABSTRACTACTIVITY";
	}

	protected FlexoColor roleTextColor = FlexoColor.GRAY_COLOR;

	public FlexoColor getRoleTextColor()
	{
		return roleTextColor;
	}

	public void setRoleTextColor(FlexoColor roleTextColor)
	{
		FlexoColor old = roleTextColor;
		this.roleTextColor = roleTextColor;
		setChanged();
		notifyObservers(new RoleTextColorChanged(old, roleTextColor));
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public FlexoCSS getCalculatedCssSheet()
	{
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
	public FlexoCSS getCssSheet()
	{
		return cssSheet;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void setCssSheet(FlexoCSS cssSheet)
	{
		FlexoCSS old = this.cssSheet;
		this.cssSheet = cssSheet;
		setChanged();
		notifyAttributeModification("cssSheet", old, cssSheet);
	}

	/**
	 * Wheter there is within all the operations of this activity at least one operation associated to a component
	 * @return
	 */
	public boolean hasWOComponent()
	{
		for (OperationNode op : getAllEmbeddedOperationNodes()) {
			if (op.hasWOComponent()) {
				return true;
			}
		}
		return false;
	}

	public AbstractActivityStatistics getStatistics()
	{
		if (statistics==null) {
			statistics = new AbstractActivityStatistics(this);
		}
		return statistics;
	}

	public void refreshStatistics() {
		getStatistics().refresh();
	}

	private Role representationRole = null;

	public String getRACICodeForRole(Role aRole){
		if(aRole==null) {
			return "";
		}
		if (aRole.equals(_role) || getResponsibleRoles().contains(aRole)) {
			return "R";
		}
		if (aRole.equals(roleA) || getAccountableRoles().contains(aRole)) {
			return "A";
		}
		if (aRole.equals(roleC) || getConsultedRoles().contains(aRole)) {
			return "C";
		}
		if (aRole.equals(roleI) || getInformedRoles().contains(aRole)) {
			return "I";
		}

		return "";
	}

	public List<EventNode> getAllBoundaryEvents() {
		ArrayList<EventNode> reply = new ArrayList<EventNode>();
		for(EventNode n:getProcess().getAllEventNodes()){
			if(this.equals(n.getBoundaryOf())) {
				reply.add(n);
			}
		}
		return reply;
	}

	@Override
	public void delete() {
		for(EventNode boundaryEvent:getAllBoundaryEvents()) {
			boundaryEvent.delete();
		}
		super.delete();
	}
}
