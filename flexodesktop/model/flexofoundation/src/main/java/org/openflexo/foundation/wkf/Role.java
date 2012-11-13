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
package org.openflexo.foundation.wkf;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoImportableObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ws.client.PPMWebService.PPMObject;
import org.openflexo.ws.client.PPMWebService.PPMRole;

/**
 * Represents a role in the workflow
 * 
 * @author sguerin
 * 
 */
public final class Role extends WorkflowModelObject implements FlexoImportableObject, DataFlexoObserver, Serializable, DeletableObject,
		InspectableObject, Sortable {

	private static final Logger logger = Logger.getLogger(Role.class.getPackage().getName());

	private String roleName;
	// private FlexoColor roleColor;
	private int index = -1;

	// private int posX = 0;
	// private int posY = 0;

	private boolean isSystemRole = false;
	private boolean isAssignable = true;

	private Vector<RoleSpecialization> _roleSpecializations;

	public static FlexoActionizer<AddRoleSpecialization, Role, WorkflowModelObject> addParentRoleActionizer;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public Role(FlexoWorkflowBuilder builder) {
		this(builder.getProject(), builder.workflow);
		initializeDeserialization(builder);
	}

	/**
	 * Constructor used during deserialization
	 * 
	 * @deprecated (used before version 1.2.1)
	 */
	@Deprecated
	public Role(FlexoProcessBuilder builder) {
		this(builder.getProject(), null);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public Role(FlexoProject project, FlexoWorkflow workflow) {
		super(project, workflow);
		_roleSpecializations = new Vector<RoleSpecialization>();
	}

	public Role(FlexoWorkflow workflow, String aRoleName) {
		this(workflow.getProject(), workflow);
		roleName = aRoleName;
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getFullyQualifiedName() + ".ROLE." + getName();
	}

	@Override
	public boolean isDescriptionImportant() {
		return true;
	}

	/**
	 * Default inspector name
	 */
	@Override
	public String getInspectorName() {
		if (isImported()) {
			return Inspectors.WKF.IMPORTED_ROLE;
		}
		return Inspectors.WKF.ROLE_INSPECTOR;
	}

	@Override
	public String getName() {
		return roleName;
	}

	@Override
	public void setName(String aName) throws DuplicateRoleException {
		if (roleName == null && aName != null || roleName != null && aName == null || roleName != null && aName != null
				&& !roleName.equals(aName)) {
			if (getRoleList() != null && getRoleList().roleWithName(aName) != null) {
				if (isDeserializing()) {
					setName(aName + "-1");
				}
				throw new DuplicateRoleException(aName);
			}
			roleName = aName;
			setChanged();
			notifyObservers(new RoleNameChange());
		}
	}

	public String getNameForInspector() {
		if (isImported()) {
			return FlexoLocalization.localizedForKey("[external]") + " " + (getName() != null ? getName() : "");
		}
		return getName();
	}

	/*
	 * public String getDescription() { return roleDescription; }
	 * 
	 * public void setDescription(String aDescription) { roleDescription = aDescription; }
	 */
	public Color getColor() {
		return getBgColor(DEFAULT);
	}

	@Override
	public Color getBgColor(String context) {
		return super.getBgColor(context, null);
	}

	public void setColor(Color aColor) {
		setBgColor(aColor, DEFAULT);
		setChanged();
		notifyObservers(new RoleColorChange());
	}

	/**
	 * Bridging Color<->FlexoColor: used by dynamic invokation in inspector
	 */
	public Color getAwtColor() {
		return getColor();
	}

	/**
	 * Bridging Color<->FlexoColor: used by dynamic invokation in inspector
	 */
	public void setAwtColor(Color aColor) {
		setColor(aColor);
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> returned = new Vector<Validable>();
		returned.add(this);
		return returned;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	public Vector<AbstractNode> getNodesUsingRole() {
		Vector<AbstractNode> returned = new Vector<AbstractNode>();
		for (FlexoProcess p : getProject().getAllLocalFlexoProcesses()) {
			returned.addAll(getNodesUsingRole(p));
		}
		return returned;
	}

	public Vector<AbstractNode> getNodesUsingRole(FlexoProcess process) {
		Vector<AbstractNode> v = new Vector<AbstractNode>();
		for (AbstractNode a : process.getActivityPetriGraph().getAllEmbeddedAbstractNodes()) {
			if (a instanceof AbstractActivityNode) {
				if (((AbstractActivityNode) a).getRole() == this) {
					v.add(a);
				}
			} else if (a instanceof EventNode) {
				if (((EventNode) a).getRole() == this) {
					v.add(a);
				}
			} else if (a instanceof OperatorNode) {
				if (((OperatorNode) a).getRole() == this) {
					v.add(a);
				}
			}
		}
		return v;
	}

	public boolean isUsedInProcess(FlexoProcess process) {
		return isRoleUsedInProcess(process, isDefaultRole() ? null : this);
	}

	public boolean isUsedInPetriGraphNodes(Vector<? extends AbstractNode> nodes) {
		return isRoleUsedInPetriGraphNodes(nodes, isDefaultRole() ? null : this);
	}

	/**
	 * Checks wheter the role <code>role</code> is used in the process <code>process</code> by any node defined in that process.
	 * 
	 * @param process
	 *            the process in which to look
	 * @param role
	 *            the role against which to check (may be null)
	 * @return true if any node of the process <code>process</code> uses the role <code>role</code>, false otherwise.
	 */
	public static boolean isRoleUsedInProcess(FlexoProcess process, Role role) {
		if (process.getActivityPetriGraph() == null) {
			return false;
		}
		return isRoleUsedInPetriGraphNodes(process.getActivityPetriGraph().getAllEmbeddedAbstractNodes(), role);
	}

	public static boolean isRoleUsedInPetriGraphNodes(Vector<? extends AbstractNode> nodes, Role role) {
		for (AbstractNode a : nodes) {
			if (a instanceof AbstractActivityNode) {
				if (((AbstractActivityNode) a).getRole() == role) {
					return true;
				}
			} else if (a instanceof EventNode) {
				if (((EventNode) a).getRole() == role) {
					return true;
				}
			} else if (a instanceof OperatorNode) {
				if (((OperatorNode) a).getRole() == role) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isImported = false;

	@Override
	public boolean isImported() {
		if (getRoleList() != null) {
			return getRoleList().isImportedRoleList();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("No role list defined on " + this);
		}
		return isImported;
	}

	public static Role createImportedRoleFromRole(RoleList roleList, PPMRole role) {
		Role fir = new Role(roleList.getFlexoWorkflow(), role.getName());
		fir.isImported = true;
		fir.updateFromObject(role);
		return fir;
	}

	public void updateFromObject(PPMRole object) {
		try {
			super.updateFromObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (object.getRgb() != null) {
			setColor(new Color(object.getRed(), object.getGreen(), object.getBlue()));
		} else {
			setColor(null);
		}
	}

	@Override
	public Class<? extends PPMObject> getEquivalentPPMClass() {
		return PPMRole.class;
	}

	public PPMRole getEquivalentPPMRole() {
		PPMRole role = new PPMRole();
		copyObjectAttributesInto(role);
		if (getColor() != null) {
			role.setRgbColor(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
		}
		return role;
	}

	public boolean isEquivalentTo(PPMRole object) {
		if (!super.isEquivalentTo(object)) {
			return false;
		}
		if (getColor() != null && object.getRgb() == null || getColor() == null && object.getRgb() != null) {
			return false;
		}
		if (getColor() != null && object.getRgb() != null) {
			if (getColor().getRed() != object.getRed()) {
				return false;
			}
			if (getColor().getGreen() != object.getGreen()) {
				return false;
			}
			if (getColor().getBlue() != object.getBlue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final void delete() {
		for (RoleSpecialization rs : (Vector<RoleSpecialization>) getRoleSpecializations().clone()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("1 Delete " + rs.getFullyQualifiedName());
			}
			rs.delete();
		}
		for (RoleSpecialization rs : (Vector<RoleSpecialization>) getInverseRoleSpecializations().clone()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("2 Delete " + rs.getFullyQualifiedName());
			}
			rs.delete();
		}
		if (isImported()) {
			isImported = true;// This is important, because it allows to do some tests on this deleted object
		}
		if (getRoleList() != null && getRoleList().getRoles().contains(this)) {
			getRoleList().removeFromRoles(this);
		}
		super.delete();
		setChanged();
		notifyObservers(new RoleRemoved(this));
		deleteObservers();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WorkflowModelObject> getAllEmbeddedDeleted() {
		Vector<WorkflowModelObject> returned = new Vector<WorkflowModelObject>();
		returned.add(this);
		returned.addAll(getRoleSpecializations());
		returned.addAll(getInverseRoleSpecializations());
		return returned;
	}

	public Vector<RoleSpecialization> getInverseRoleSpecializations() {
		Vector<RoleSpecialization> returned = new Vector<RoleSpecialization>();
		for (Role r : getRolesSpecializingMyself()) {
			for (RoleSpecialization rs : r.getRoleSpecializations()) {
				if (rs.getParentRole() == this) {
					returned.add(rs);
				}
			}
		}
		return returned;
	}

	private RoleList roleList;

	public RoleList getRoleList() {
		return roleList;
	}

	public void setRoleList(RoleList roleList) {
		if (this.roleList != null) {
			this.roleList.removeFromRoles(this);
		}
		this.roleList = roleList;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "role";
	}

	@Override
	public int getIndex() {
		if (isBeingCloned()) {
			return -1;
		}
		if (index == -1 && getCollection() != null) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return getIndex();
	}

	@Override
	public void setIndexValue(int index) {
		if (index == this.index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyAttributeModification("index", old, index);
		if (!isDeserializing() && !isCreatedByCloning() && getProject() != null) {
			getWorkflow().setChanged();
			getWorkflow().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public Role[] getCollection() {
		if (getRoleList() == null) {
			return null;
		}
		return getRoleList().getRoles().toArray(new Role[0]);
	}

	// ===================================================================
	// =========================== FlexoObserver =========================
	// ===================================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO Auto-generated method stub

	}

	public Vector<RoleSpecialization> getRoleSpecializations() {
		return _roleSpecializations;
	}

	public void setRoleSpecializations(Vector<RoleSpecialization> roles) {
		_roleSpecializations = roles;
	}

	public void addToRoleSpecializations(RoleSpecialization aRoleSpecialization) {
		if (isImported()) {
			return;
		}
		if (aRoleSpecialization.getRole() == null) {
			aRoleSpecialization.setRole(this);
		}
		if (!_roleSpecializations.contains(aRoleSpecialization) && aRoleSpecialization.getRole() == this
				&& aRoleSpecialization.getParentRole() != this) {
			_roleSpecializations.add(aRoleSpecialization);
			notifyAttributeModification("roleSpecializations", null, aRoleSpecialization);
			aRoleSpecialization.getParentRole().notifyNewRoleSpecializing(this);
		}
	}

	public void removeFromRoleSpecializations(RoleSpecialization aRoleSpecialization) {
		if (_roleSpecializations.contains(aRoleSpecialization)) {
			_roleSpecializations.remove(aRoleSpecialization);
			notifyAttributeModification("roleSpecializations", aRoleSpecialization, null);
		}
		aRoleSpecialization.getParentRole().notifyRoleNoMoreSpecializing(this);
	}

	public void notifyNewRoleSpecializing(Role specializingRole) {
		setChanged();
		notifyObservers(new RoleSpecializing(specializingRole));
	}

	public void notifyRoleNoMoreSpecializing(Role specializingRole) {
		setChanged();
		notifyObservers(new RoleNoMoreSpecializing(specializingRole));
	}

	public boolean isRoleSpecializationDeletable(RoleSpecialization aRole) {
		return true;
	}

	public void performAddRoleSpecialization() {
		if (addParentRoleActionizer != null) {
			addParentRoleActionizer.run(this, EMPTY_VECTOR);
		}
	}

	public void performDeleteRoleSpecialization(RoleSpecialization aRoleSpecialization) {
		removeFromRoleSpecializations(aRoleSpecialization);
	}

	/**
	 * Return all roles potentially specializable for current role
	 * 
	 * @return
	 */
	public Vector<Role> getAvailableRolesForSpecialization() {
		// logger.info("What are the role that "+this+" i may specialize ?");
		Vector<Role> returned = new Vector<Role>();
		for (Role r : getRoleList().getRoles()) {
			// logger.info("Regarding "+r+":");
			// logger.info("mightSpecializeRole(r) = "+mightSpecializeRole(r));
			// logger.info("!isTransitivelySpecializingRole(r) = "+!isTransitivelySpecializingRole(r));
			if (r != this && mightSpecializeRole(r) && !isTransitivelySpecializingRole(r)) {
				returned.add(r);
			}
		}
		return returned;
	}

	/**
	 * Return a boolean indicating if supplied role might be specialized by this role (avoid cycles)
	 * 
	 * @param aRole
	 * @return
	 */
	public boolean mightSpecializeRole(Role aRole) {
		return !aRole.isTransitivelySpecializingRole(this);
	}

	public boolean isSpecializingRole(Role aRole) {
		if (aRole == this) {
			return true;
		}
		for (RoleSpecialization rs : getRoleSpecializations()) {
			if (rs.getParentRole() == aRole) {
				return true;
			}
		}
		return false;
	}

	public Vector<Role> getRolesSpecializingRole(Role aRole) {
		Vector<Role> v = new Vector<Role>();
		if (getRoleList() != null) {
			for (Role r : getRoleList().getRoles()) {
				if (r.isSpecializingRole(aRole)) {
					v.add(r);
				}
			}
		}
		return v;
	}

	public Vector<Role> getRolesSpecializingMyself() {
		return getRolesSpecializingRole(this);
	}

	public boolean isTransitivelySpecializingRole(Role aRole) {
		if (isSpecializingRole(aRole)) {
			return true;
		}
		for (RoleSpecialization r : getRoleSpecializations()) {
			if (r.getParentRole().isTransitivelySpecializingRole(aRole)) {
				return true;
			}
		}
		return false;
	}

	public boolean getIsSystemRole() {
		return isSystemRole;
	}

	public void setIsSystemRole(boolean aFlag) {
		if (aFlag != isSystemRole) {
			this.isSystemRole = aFlag;
			notifyAttributeModification("isSystemRole", !aFlag, aFlag);
		}
	}

	public boolean getIsAssignable() {
		return isAssignable;
	}

	public void setIsAssignable(boolean aFlag) {
		if (aFlag != isAssignable) {
			this.isAssignable = aFlag;
			notifyAttributeModification("isAssignable", !aFlag, aFlag);
			if (getWorkflow() != null) {
				getWorkflow().clearAssignableRolesCache();
			}
		}
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	public boolean isDefaultRole() {
		return getWorkflow().getRoleList().getDefaultRole() == this;
	}

	public static class ImportedRoleShouldExistOnServer extends ValidationRule<ImportedRoleShouldExistOnServer, Role> {
		public ImportedRoleShouldExistOnServer() {
			super(Role.class, "imported_role_should_exist_on_server");
		}

		@Override
		public ValidationIssue<ImportedRoleShouldExistOnServer, Role> applyValidation(Role process) {
			if (process.isImported() && process.isDeletedOnServer()) {
				return new ValidationWarning<ImportedRoleShouldExistOnServer, Role>(this, process,
						"role_($object.name)_no_longer_exists_on_server", new DeleteRole());
			}
			return null;
		}

		public static class DeleteRole extends FixProposal<ImportedRoleShouldExistOnServer, Role> {
			public DeleteRole() {
				super("delete_role_($object.name)");
			}

			@Override
			protected void fixAction() {
				getObject().delete();
			}
		}
	}

	/**
	 * used by velocity
	 * 
	 * @return sorted list of role where an arrow starting from this is going and where the arrival role is also the starting point of at
	 *         least one arrow.
	 */
	public ArrayList<Role> getChildNonLeaf() {
		ArrayList<Role> reply = new ArrayList<Role>();
		for (RoleSpecialization rs : getRoleSpecializations()) {
			Role r = rs.getParentRole();
			if (r.getRoleSpecializations().size() > 0) {
				reply.add(r);
			}
		}
		sort(reply);
		return reply;
	}

	/**
	 * used by velocity
	 * 
	 * @return sorted list of roles where an arrow starting from this is going and where the arrival role don't have any starting arrow.
	 */
	public ArrayList<Role> getChildLeaf() {
		ArrayList<Role> reply = new ArrayList<Role>();
		for (RoleSpecialization rs : getRoleSpecializations()) {
			Role r = rs.getParentRole();
			if (r.getRoleSpecializations().size() == 0) {
				reply.add(r);
			}
		}
		sort(reply);
		return reply;
	}

	public static void sort(ArrayList<Role> list) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((Role) o1).getIndex() - ((Role) o2).getIndex();
			}
		});
	}
}
