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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.action.AddStatus;
import org.openflexo.foundation.wkf.dm.StatusInserted;
import org.openflexo.foundation.wkf.dm.StatusRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a list of status of a process
 * 
 * @author sguerin
 * 
 */
public final class StatusList extends WKFObject implements DeletableObject, LevelledObject {

	private static final Logger logger = Logger.getLogger(StatusList.class.getPackage().getName());

	private Status _defaultStatus;

	private Vector<Status> _status;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public StatusList(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private StatusList(FlexoProcess process) {
		super(process);
		_status = new Vector<Status>();
	}

	/**
	 * Default constructor (public API outside XML serialization)
	 */
	public StatusList(FlexoProcess process, boolean createDefaultStatus) {
		this(process);
		if (createDefaultStatus) {
			Status defaultStatus;
			try {
				defaultStatus = new Status(process, FlexoLocalization.localizedForKey("default"));
				defaultStatus.setDontGenerate(true);
				addToStatus(defaultStatus);
				setDefaultStatus(defaultStatus);
			} catch (DuplicateStatusException e) {
				// Should never happen !
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".STATUS_LIST";
	}

	public Vector<Status> getStatus() {
		return _status;
	}

	public Vector<Status> getInheritedStatus() {
		Vector<Status> returned = new Vector<Status>();
		FlexoProcess current = getProcess();
		while (current.getParentProcess() != null) {
			current = current.getParentProcess();
			if (current.getStatusList() != null)
				returned.addAll(current.getStatusList().getStatus());
		}
		return returned;
	}

	public Vector<Status> getAllAvailableStatus() {
		Vector<Status> returned = new Vector<Status>();
		returned.addAll(getStatus());
		returned.addAll(getInheritedStatus());
		return returned;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(AddStatus.actionType);
		return returned;
	}

	public void setStatus(Vector<Status> status) {
		_status = status;
		if (getProject() != null) {
			Iterator i = status.iterator();
			while (i.hasNext()) {
				Status s = (Status) i.next();
				getProject().getGlobalStatus().put(s.getFullyQualifiedName(), s);
			}
		}
	}

	public void addToStatus(Status aStatus) throws DuplicateStatusException {
		if (aStatus.getName() == null)
			aStatus.setName(getNextNewStatusName());

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("addToStatus with " + aStatus.getFullyQualifiedName());
		}

		if (statusWithName(aStatus.getName()) != null) {
			if (isDeserializing()) {
				aStatus.setName(aStatus.getName() + "-1");
				addToStatus(aStatus);
				return;
			}
			throw new DuplicateStatusException(aStatus, aStatus.getName());
		}

		aStatus.setProcess(getProcess());
		_status.add(aStatus);
		if (getProject() != null) {
			if (getProject().getGlobalStatus().get(aStatus.getFullyQualifiedName()) != null
					&& getProject().getGlobalStatus().get(aStatus.getFullyQualifiedName()) != aStatus)
				if (logger.isLoggable(Level.WARNING))
					logger.warning("There are two different statuses with the same name: " + aStatus.getFullyQualifiedName()
							+ " this can be ignored during conversions");
			getProject().getGlobalStatus().put(aStatus.getFullyQualifiedName(), aStatus);
			if (!isDeserializing()) {
				notifyStatusAdded(aStatus, getProcess());
			}
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("No project for status list " + this);
		}
	}

	/**
	 * @param status
	 * @param process
	 */
	private void notifyStatusAdded(Status status, FlexoProcess process) {
		setChanged();
		notifyObservers(new StatusInserted(status, process));
		if ((getProcess() != null) && (getProcess().getSubProcesses() != null)) {
			for (Enumeration e = getProcess().getSubProcesses().elements(); e.hasMoreElements();) {
				FlexoProcess subProcess = (FlexoProcess) e.nextElement();
				subProcess.getStatusList().notifyStatusAdded(status, process);
			}
		}
		if (getProcess() != null)
			getProcess().notifyStatusListUpdated();
	}

	public void removeFromStatus(Status aStatus) {
		_status.remove(aStatus);
		if (getProject() != null)
			getProject().getGlobalStatus().remove(aStatus.getFullyQualifiedName());
		else if (logger.isLoggable(Level.WARNING))
			logger.warning("No project for status list " + this);
		if (!aStatus.isDeleted()) {
			aStatus.delete();
		}
		if (!isDeserializing()) {
			notifyStatusRemoved(aStatus, getProcess());
		}
	}

	/**
	 * @param status
	 * @param process
	 */
	private void notifyStatusRemoved(Status status, FlexoProcess process) {
		setChanged();
		notifyObservers(new StatusRemoved(status, process));
		for (Enumeration e = getProcess().getSubProcesses().elements(); e.hasMoreElements();) {
			FlexoProcess subProcess = (FlexoProcess) e.nextElement();
			subProcess.getStatusList().notifyStatusRemoved(status, process);
		}
	}

	public Status statusWithName(String aStatusName) {
		if (getProject() != null)
			if (aStatusName != null && aStatusName.indexOf(".STATUS.") > -1)
				return getProject().getGlobalStatus().get(aStatusName);
		for (Enumeration e = _status.elements(); e.hasMoreElements();) {
			Status temp = (Status) e.nextElement();
			if ((temp != null) && (temp.getName().equals(aStatusName))) {
				return temp;
			}
		}
		return null;
	}

	public Status statusWithID(long aStatusId) {
		for (Enumeration e = _status.elements(); e.hasMoreElements();) {
			Status temp = (Status) e.nextElement();
			if (temp.getFlexoID() == aStatusId) {
				return temp;
			}
		}
		return null;
	}

	private String defaultStatusAsString;

	public Status getDefaultStatus() {
		if (_defaultStatus == null && defaultStatusAsString != null) {
			if (getProject() != null) {
				_defaultStatus = getProject().getGlobalStatus().get(defaultStatusAsString);
				if (_defaultStatus == null && !isDeserializing()) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Status with name " + defaultStatusAsString + " could not be found.");
					defaultStatusAsString = null;
				}
			} else if (!isDeserializing()) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("No project for Status List in process " + (getProcess() != null ? getProcess().getName() : null));
			}
		}
		return _defaultStatus;
	}

	public void setDefaultStatus(Status defaultStatus) {
		Status old = this._defaultStatus;
		this._defaultStatus = defaultStatus;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("defaultStatus", old, defaultStatus));
	}

	public String getDefaultStatusAsString() {
		if (getDefaultStatus() != null)
			return getDefaultStatus().getFullyQualifiedName();
		else
			return null;
	}

	public void setDefaultStatusAsString(String status) {
		this.defaultStatusAsString = status;
	}

	public Status createNewStatus() {
		String newStatusName = getNextNewStatusName();
		Status newStatus = null;
		try {
			newStatus = new Status(getProcess(), newStatusName);
			newStatus.setDescription(FlexoLocalization.localizedForKey("no_description"));
			addToStatus(newStatus);
		} catch (DuplicateStatusException e) {
			// should never happen
			e.printStackTrace();
		}
		return newStatus;
	}

	public String getNextNewStatusName() {
		String baseName = "status";
		String newStatusName = baseName;
		int inc = 0;
		while (statusWithName(newStatusName) != null) {
			inc++;
			newStatusName = baseName + inc;
		}
		return newStatusName;
	}

	public void deleteStatus(Status aStatus) {
		removeFromStatus(aStatus);
	}

	public boolean isStatusDeletable(Status aStatus) {
		return true;
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		returned.add(this);
		returned.addAll(getStatus());
		return returned;
	}

	/**
	 * Returns the level of a StatusList (which is {@link FlexoLevel.PROCESS}).
	 * 
	 * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
	 */
	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.PROCESS;
	}

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		_status.removeAllElements();
		super.delete();
		deleteObservers();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during process deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	// ==========================================================================
	// ============================= Validation =================================
	// ==========================================================================

	public static class ProcessShouldHaveAtLeastAStatus extends ValidationRule {
		public ProcessShouldHaveAtLeastAStatus() {
			super(StatusList.class, "process_should_have_at_least_a_status");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final StatusList statusList = (StatusList) object;
			if (statusList.getStatus().size() == 0) {
				ValidationWarning warning = new ValidationWarning(this, object, "process_($object.process.name)_has_no_status_defined");
				warning.addToFixProposals(new CreateDefaultStatus());
				return warning;
			}
			return null;
		}

	}

	public static class ProcessShouldHaveADefaultStatus extends ValidationRule {
		public ProcessShouldHaveADefaultStatus() {
			super(StatusList.class, "process_should_have_a_default_status");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final StatusList statusList = (StatusList) object;
			if (statusList.getDefaultStatus() == null) {
				ValidationWarning warning = new ValidationWarning(this, object, "process_($object.process.name)_has_no_default_status");
				for (Enumeration e = statusList.getStatus().elements(); e.hasMoreElements();) {
					Status status = (Status) e.nextElement();
					warning.addToFixProposals(new SetDefaultStatus(status));
				}
				return warning;
			}
			return null;
		}

	}

	public static class SetDefaultStatus extends FixProposal {
		public Status status;

		public SetDefaultStatus(Status aStatus) {
			super("set_default_status_for_process_($object.process.name)_to_($status.name)");
			status = aStatus;
		}

		@Override
		protected void fixAction() {
			((StatusList) getObject()).setDefaultStatus(status);
		}
	}

	public static class CreateDefaultStatus extends ParameteredFixProposal {
		public CreateDefaultStatus() {
			super("create_default_status_for_process_($object.process.name)", "defaultStatusName", "enter_a_name_for_the_default_status",
					FlexoLocalization.localizedForKey("new_status"));
		}

		@Override
		protected void fixAction() {
			Status newStatus = null;
			String newStatusName = (String) getValueForParameter("defaultStatusName");
			StatusList statusList = (StatusList) getObject();
			try {
				newStatus = new Status(statusList.getProcess(), newStatusName);
				newStatus.setDescription(FlexoLocalization.localizedForKey("no_description"));
				statusList.addToStatus(newStatus);
			} catch (DuplicateStatusException e) {
				newStatusName = statusList.getNextNewStatusName();
				try {
					newStatus = new Status(statusList.getProcess(), newStatusName);
					newStatus.setDescription(FlexoLocalization.localizedForKey("no_description"));
					statusList.addToStatus(newStatus);
				} catch (DuplicateStatusException e1) {
					// should never happen
					e1.printStackTrace();
				}
			}
			((StatusList) getObject()).setDefaultStatus(newStatus);
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "status_list";
	}

	public int size() {
		return _status.size();
	}

	public Enumeration<Status> elements() {
		return _status.elements();
	}
}
