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

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.help.ApplicationHelpEntryPoint;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PortMapInserted;
import org.openflexo.foundation.wkf.dm.PortMapRegisteryInserted;
import org.openflexo.foundation.wkf.dm.PortMapRegisteryOrientationChanged;
import org.openflexo.foundation.wkf.dm.PortMapRegisteryRemoved;
import org.openflexo.foundation.wkf.dm.PortMapRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.ws.DefaultServiceInterface;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class SubProcessNode extends AbstractActivityNode implements ApplicationHelpEntryPoint, ReferenceOwner {

	protected static final boolean forceWSCallConsistency = false;

	private static final Logger logger = Logger.getLogger(SubProcessNode.class.getPackage().getName());

	private FlexoModelObjectReference<FlexoProcess> _subProcess;

	// serialized. If null, then the serviceInterface to use is the DefaultServiceInterface
	private ServiceInterface _serviceInterface;

	private PortMapRegistery _portMapRegistery;

	private boolean displaySubProcessImage = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public SubProcessNode(FlexoProcess process) {
		super(process);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * Dynamic constructor with ServiceInterface...
	 */
	public SubProcessNode(FlexoProcess process, ServiceInterface _interface) {
		this(process);
		setServiceInterface(_interface);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractActivityNode#delete()
	 */
	@Override
	public boolean delete() {
		super.delete();
		if (getServiceInterface() != null) {
			setServiceInterface(null);
		}
		if (getSubProcess() != null) {
			getSubProcess().removeFromSubProcessNodes(this);
		}
		if (_portMapRegistery != null) {
			_portMapRegistery.delete();
		}
		return true;
	}

	public boolean isAcceptableAsSubProcess(FlexoProcess aProcess) {
		if (aProcess == null) {
			return true; // Null value is allowed in the context of edition
		}
		return isAcceptableAsSubProcess(aProcess, aProcess.getParentProcess());

	}

	public boolean isAcceptableAsSubProcess(FlexoProcess aProcess, FlexoProcess parentProcess) {
		if (aProcess.isTopLevelProcess()) {
			return true;
		}

		if (parentProcess == null) {
			return /* !aProcess.isRootProcess() */true;// Allowed to invoke the root process
		} else {
			return parentProcess.isAncestorOf(getProcess());
		}
	}

	public boolean isAcceptableAsSubProcess(FlexoProcessNode existingProcessNode) {
		if (existingProcessNode == null) {
			return true; // Null value is allowed in the context of edition
		}
		return isAcceptableAsSubProcess(existingProcessNode, existingProcessNode.getFatherProcessNode());
	}

	public boolean isAcceptableAsSubProcess(FlexoProcessNode aProcess, FlexoProcessNode parentProcess) {
		if (aProcess.isTopLevelProcess()) {
			return true;
		}

		if (parentProcess == null) {
			return /* !aProcess.isRootProcess() */true;// Allowed to invoke the root process
		} else {
			return parentProcess.isAncestorOf(getProcess().getProcessNode());
		}
	}

	@Override
	public boolean isAccessible() {
		boolean b = super.isAccessible();
		if (b) {
			return true;
		} else {
			if (getPortMapRegistery() != null) {
				for (FlexoPortMap portMap : getPortMapRegistery().getPortMaps()) {
					if (portMap.getIncomingPostConditions().size() > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// Used when serializing
	public FlexoModelObjectReference<FlexoProcess> getSubProcessReference() {
		return _subProcess;
	}

	// Used when deserializing
	public void setSubProcessReference(FlexoModelObjectReference<FlexoProcess> aSubProcessReference) {
		if (aSubProcessReference != null) {
			_subProcess = aSubProcessReference;
			_subProcess.setOwner(this);
		}
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		if (reference == _subProcess) {
			setSubProcess(null);
		} else {
			super.objectDeleted(reference);
		}
	}

	public boolean hasSubProcess() {
		return getSubProcess() != null;
	}

	public boolean hasSubProcessReference() {
		return _subProcess != null;
	}

	public FlexoProcess getSubProcess(boolean forceLoading) {
		if (_subProcess != null) {
			return _subProcess.getObject(forceLoading);
		} else {
			return null;
		}
	}

	public FlexoProcess getSubProcess() {
		if (_subProcess != null) {
			return _subProcess.getObject();
		} else {
			return null;
		}
	}

	public void setSubProcess(FlexoProcess aSubProcess) {
		setSubProcess(aSubProcess, true);
	}

	private boolean isPortMapRegisteryVisible() {
		boolean defaultValue = !(this instanceof SingleInstanceSubProcessNode && this instanceof LoopSubProcessNode);
		if (getPortMapRegistery() != null) {
			defaultValue = !getPortMapRegistery().getIsHidden();
		}
		return _booleanGraphicalPropertyForKey("isPortMapRegisteryVisible", defaultValue);
	}

	private void setIsPortMapRegisteryVisible(boolean b) {
		_setGraphicalPropertyForKey(b, "isPortMapRegisteryVisible");
	}

	public void setSubProcess(FlexoProcess aSubProcess, boolean notify) {
		if (getSubProcess() != aSubProcess) {
			if (aSubProcess != null && !isAcceptableAsSubProcess(aSubProcess) && !isDeserializing()) {
				logger.warning("Sorry, this process is not acceptable as sub-process for this SubProcessNode");
				return;
			}
			FlexoProcess oldSubProcess = _subProcess != null ? _subProcess.getObject(false) : null;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setSubProcess() with " + aSubProcess + " for " + this);
			}
			if (aSubProcess != null) {
				_subProcess = new FlexoModelObjectReference<FlexoProcess>(aSubProcess, this);
			} else {
				_subProcess = null;
			}
			if (_subProcess != null && !isCreatedByCloning()) {
				if (getProcess() != null && getProcess() != aSubProcess) {
					getProcess().getFlexoResource().addToDependentResources(aSubProcess.getFlexoResource());
				}
				aSubProcess.addToSubProcessNodes(this);
			}

			if (oldSubProcess != null && oldSubProcess != aSubProcess) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("delete the old portmap registery");
				}
				if (_portMapRegistery != null) {
					_portMapRegistery.delete();
					setPortMapRegistery(null);
				}
				oldSubProcess.removeFromSubProcessNodes(this);
			}
			if (aSubProcess != null) {
				if (!isDeserializing()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("update the portmap registery");
					}
					updatePortMapRegistery();
				}
			}
			if (oldSubProcess != aSubProcess && notify) {
				notifyAttributeModification("subProcess", oldSubProcess, aSubProcess);
			}
		}
	}

	@Override
	public void setProcess(FlexoProcess p) {
		super.setProcess(p);
		if (getSubProcess() != null) {
			if (getProcess() != null && getProcess() != getSubProcess()) {
				getProcess().getFlexoResource().addToDependentResources(getSubProcess().getFlexoResource());
			}
			getSubProcess().addToSubProcessNodes(this);
		}
	}

	@Override
	public void refreshStatistics() {
		super.refreshStatistics();
		if (hasSubProcess()) {
			getSubProcess().getStatistics().refresh();
			// Little hack for inspector
			notifyAttributeModification("subProcess.statistics.activityCount", null, null);
			notifyAttributeModification("subProcess.statistics.realActivityCount", null, null);
			notifyAttributeModification("subProcess.statistics.operationCount", null, null);
			notifyAttributeModification("subProcess.statistics.realOperationCount", null, null);
			notifyAttributeModification("subProcess.statistics.actionCount", null, null);
			notifyAttributeModification("subProcess.statistics.realActionCount", null, null);
		}
	}

	/**
	 * call this method to get the chosen ServiceInterface.
	 * 
	 * @return
	 */
	public ServiceInterface getActiveServiceInterface() {
		if (getSubProcess() == null) {
			return null;
		}
		if (getServiceInterface() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Active service interface:" + getServiceInterface().getName());
			}
			return getServiceInterface();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Active service interface is the default");
		}
		return getSubProcess().getPortRegisteryInterface();
	}

	/**
	 * do no use this method (for serialisation only), call getActiveServiceInterface()
	 */
	public ServiceInterface getServiceInterface() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getServiceInterface(): " + _serviceInterface);
		}
		if (getSubProcess() != null && serviceInterfaceName != null && _serviceInterface == null) {
			_serviceInterface = getSubProcess().getServiceInterfaceNamed(serviceInterfaceName);
			if (_serviceInterface != null) {
				serviceInterfaceName = null;
				_serviceInterface.addObserver(this);
			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find service interface '" + serviceInterfaceName + "' in process " + getSubProcess().getName());
			}
		}
		return _serviceInterface;
	}

	public void setServiceInterface(ServiceInterface anInterface) {
		if (_serviceInterface != null) {
			_serviceInterface.deleteObserver(this);
		}
		_serviceInterface = anInterface;
		if (_serviceInterface != null) {
			_serviceInterface.addObserver(this);
		}
	}

	private String serviceInterfaceName;

	public String getServiceInterfaceName() {
		if (getServiceInterface() != null && !(getServiceInterface() instanceof DefaultServiceInterface)) {
			return getServiceInterface().getName();
		}
		return null;
	}

	public void setServiceInterfaceName(String serviceInterfaceName) {
		this.serviceInterfaceName = serviceInterfaceName;
	}

	@Override
	public abstract String getInspectorName();

	@Deprecated
	public String getSubProcessName() {
		if (getSubProcess() != null) {
			return getSubProcess().getName();
		} else {
			return null;
		}
	}

	@Deprecated
	public void setSubProcessName(String subProcessName) {
		// Not relevant anymore
		/*
		 * if (getSubProcess() != null) { _subProcess = null; } _subProcessName = subProcessName; _subProcess = getSubProcess();
		 */
	}

	public PortMapRegistery getPortMapRegistery() {
		if (_portMapRegistery == null && getSubProcess() != null) {
			updatePortMapRegistery();
		}
		return _portMapRegistery;
	}

	public void setPortMapRegistery(PortMapRegistery portMapRegistery) {
		PortMapRegistery oldPortMapRegistery = _portMapRegistery;
		_portMapRegistery = portMapRegistery;
		if (_portMapRegistery != null) {
			_portMapRegistery.setSubProcessNode(this);
		}
		if (oldPortMapRegistery != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Notify that PortMapRegistery has been removed");
			}
			setChanged();
			notifyObservers(new PortMapRegisteryRemoved(oldPortMapRegistery));
			oldPortMapRegistery.deleteObserver(this);
		}
		if (portMapRegistery != oldPortMapRegistery) {
			if (portMapRegistery != null) {
				if (!isDeserializing()) {
					portMapRegistery.setIsVisible(isPortMapRegisteryVisible());
				}
				portMapRegistery.addObserver(this);
				setChanged();
				notifyObservers(new PortMapRegisteryInserted(portMapRegistery));
			}
		}
	}

	// here we must take into account that the portMap CAN map a ServiceInterface
	private void updatePortMapRegistery() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updatePortMapRegistery()");
		}
		if (getSubProcess() != null) {
			if (_portMapRegistery == null) {
				// constuctor of PortMapRegistery must check the serviceInterface
				setPortMapRegistery(new PortMapRegistery(this));
			}
			// update in portMapRegistery must check the ServiceInterface
			_portMapRegistery.updateFromServiceInterface();
		} else if (_portMapRegistery != null) {
			_portMapRegistery.delete();
			setPortMapRegistery(null);
		}
	}

	public boolean getIsWebService() {
		return this instanceof WSCallSubProcessNode;
		// return ((getSubProcess() != null) && (getSubProcess().getIsWebService()));
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		if (_portMapRegistery != null) {
			returned.add(_portMapRegistery);
			returned.addAll(_portMapRegistery.getAllEmbeddedWKFObjects());
		}
		return returned;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		super.update(observable, dataModification);
		if (dataModification instanceof PortMapRegisteryOrientationChanged) {
			forwardNotification(dataModification);
		} else if (dataModification instanceof PortMapInserted) {
			forwardNotification(dataModification);
		} else if (dataModification instanceof PortMapRemoved) {
			forwardNotification(dataModification);
		} else if (dataModification instanceof WKFAttributeDataModification
				&& ((WKFAttributeDataModification) dataModification).getAttributeName().equals("isWebService")) {
			forwardNotification(dataModification);
		} else if (dataModification instanceof NameChanged) {
			if (observable == getSubProcess() || observable == getServiceInterface()) {
				forwardNotification(dataModification);
			}
		} else if (dataModification instanceof ObjectVisibilityChanged && observable == getPortMapRegistery()) {
			setIsPortMapRegisteryVisible(!getPortMapRegistery().getIsHidden());
		}

	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class SubProcessNodeMustReferToAProcess extends ValidationRule<SubProcessNodeMustReferToAProcess, SubProcessNode> {
		public SubProcessNodeMustReferToAProcess() {
			super(SubProcessNode.class, "sub_process_node_must_refer_to_a_process");
		}

		@Override
		public ValidationIssue<SubProcessNodeMustReferToAProcess, SubProcessNode> applyValidation(SubProcessNode subProcessNode) {
			if (subProcessNode.getSubProcess() == null) {
				ValidationError<SubProcessNodeMustReferToAProcess, SubProcessNode> error = new ValidationError<SubProcessNodeMustReferToAProcess, SubProcessNode>(
						this, subProcessNode, "sub_process_node_($object.name)_is_not_link_to_any_sub_process");
				for (FlexoProcess p : subProcessNode.getProject().getAllFlexoProcesses()) {
					if (subProcessNode.isAcceptableAsSubProcess(p)) {
						error.addToFixProposals(new SetSubProcessToExistingSubProcess(p));
					}
				}
				error.addToFixProposals(new DeletionFixProposal<SubProcessNodeMustReferToAProcess, SubProcessNode>(
						"delete_this_sub_process_node"));
				return error;
			}
			return null;
		}

		public class SetSubProcessToExistingSubProcess extends FixProposal<SubProcessNodeMustReferToAProcess, SubProcessNode> {
			public FlexoProcess subProcess;

			public SetSubProcessToExistingSubProcess(FlexoProcess aSubProcess) {
				super("set_($object.name)_sub_process_to_($subProcess.name)");
				subProcess = aSubProcess;
			}

			@Override
			protected void fixAction() {
				getObject().setSubProcess(subProcess);
			}
		}

	}

	public static class SubProcessReferenceMustBeValid extends ValidationRule<SubProcessReferenceMustBeValid, SubProcessNode> {
		public SubProcessReferenceMustBeValid() {
			super(SubProcessNode.class, "sub_process_node_must_refer_to_a_valid_process");
		}

		@Override
		public ValidationIssue<SubProcessReferenceMustBeValid, SubProcessNode> applyValidation(SubProcessNode subProcessNode) {
			if (subProcessNode.getSubProcess() != null) {
				if (!subProcessNode.isAcceptableAsSubProcess(subProcessNode.getSubProcess())) {
					ValidationError<SubProcessReferenceMustBeValid, SubProcessNode> error = new ValidationError<SubProcessReferenceMustBeValid, SubProcessNode>(
							this, subProcessNode, "sub_process_node_($object.name)_is_linked_to_an_invalid_process");
					for (FlexoProcess p : subProcessNode.getProject().getWorkflow().getAllFlexoProcesses()) {
						if (subProcessNode.isAcceptableAsSubProcess(p)) {
							error.addToFixProposals(new SetSubProcessToExistingSubProcess(p));
						}
					}
					error.addToFixProposals(new SetSubProcessToNull());
					error.addToFixProposals(new DeletionFixProposal<SubProcessReferenceMustBeValid, SubProcessNode>(
							"delete_this_sub_process_node"));
					return error;
				}
			}
			return null;
		}

		public class SetSubProcessToExistingSubProcess extends FixProposal<SubProcessReferenceMustBeValid, SubProcessNode> {
			public FlexoProcess subProcess;

			public SetSubProcessToExistingSubProcess(FlexoProcess aSubProcess) {
				super("set_($object.name)_sub_process_to_($subProcess.name)");
				subProcess = aSubProcess;
			}

			@Override
			protected void fixAction() {
				getObject().setSubProcess(subProcess);
			}
		}

		public class SetSubProcessToNull extends FixProposal<SubProcessReferenceMustBeValid, SubProcessNode> {
			public SetSubProcessToNull() {
				super("reset_($object.name)_sub_process_reference");
			}

			@Override
			protected void fixAction() {
				getObject().setSubProcess(null);
			}
		}

	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "sub_process_node";
	}

	public boolean isLoop() {
		return this instanceof MultipleInstanceSubProcessNode && ((MultipleInstanceSubProcessNode) this).getIsSequential();
	}

	public boolean isFork() {
		return this instanceof MultipleInstanceSubProcessNode && !((MultipleInstanceSubProcessNode) this).getIsSequential();
	}

	public boolean isSingle() {
		return this instanceof SingleInstanceSubProcessNode;
	}

	public boolean isLoopSingle() {
		return this instanceof LoopSubProcessNode;
	}

	public boolean isWSCall() {
		return this instanceof WSCallSubProcessNode;
	}

	@Override
	public ApplicationHelpEntryPoint getParentHelpEntry() {
		return getProcess();
	}

	@Override
	public List<ApplicationHelpEntryPoint> getChildsHelpObjects() {
		Vector<ApplicationHelpEntryPoint> reply = new Vector<ApplicationHelpEntryPoint>();
		reply.addAll(getAllOperationNodes());
		return reply;
	}

	@Override
	public String getShortHelpLabel() {
		return getName();
	}

	@Override
	public String getTypedHelpLabel() {
		return "SubProcess : " + getName();
	}

	public void setDisplaySubProcessImage(boolean displaySubProcessImage) {
		if (this.displaySubProcessImage == displaySubProcessImage) {
			return;
		}
		this.displaySubProcessImage = displaySubProcessImage;
		notifyAttributeModification("displaySubProcessImage", !displaySubProcessImage, displaySubProcessImage);
	}

	public boolean getDisplaySubProcessImage() {
		return displaySubProcessImage;
	}

}
