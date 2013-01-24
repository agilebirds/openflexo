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

/*
 * FlexoWorkflow.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.ProcessDMEntity;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.help.ApplicationHelpEntryPoint;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.imported.dm.ProcessAlreadyImportedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoWorkflowResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.ImportedProcessLibraryCreated;
import org.openflexo.foundation.rm.ImportedRoleLibraryCreated;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.MetricsDefinition.MetricsType;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.action.AddMetricsDefinition;
import org.openflexo.foundation.wkf.action.DeleteMetricsDefinition;
import org.openflexo.foundation.wkf.dm.ProcessInserted;
import org.openflexo.foundation.wkf.dm.ProcessRemoved;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.ws.client.PPMWebService.PPMProcess;
import org.openflexo.xmlcode.XMLMapping;

/**
 * A FlexoWorkflow represent all the processes defined in corresponding project, and their hierarchy. A FlexoWorkflow always codes one and
 * only one FlexoProcess as the root process. Despite of that fact, more than one FlexoProcess could have no parent process: those which are
 * not declared as root process are said as "processes without context".
 * 
 * @author benoit,sylvain
 */

public class FlexoWorkflow extends FlexoFolderContainerNode implements XMLStorageResourceData, InspectableObject {

	public static final String ALL_ASSIGNABLE_ROLES = "allAssignableRoles";

	public enum GraphicalProperties {
		CONNECTOR_REPRESENTATION("connectorRepresentation"),
		COMPONENT_FONT("componentFont"),
		ROLE_FONT("roleFont"),
		EVENT_FONT("eventFont"),
		ACTION_FONT("actionFont"),
		OPERATION_FONT("operationFont"),
		ACTIVITY_FONT("activityFont"),
		ARTEFACT_FONT("artefactFont"),
		EDGE_FONT("edgeFont"),
		SHOW_MESSAGES("showMessages"),
		SHOW_WO_NAME("showWOName"),
		SHOW_SHADOWS("showShadows"),
		USE_TRANSPARENCY("useTransparency");
		private String serializationName;

		GraphicalProperties(String s) {
			this.serializationName = s;
		}

		public String getSerializationName() {
			return serializationName;
		}

	}

	private static final Logger logger = Logger.getLogger(FlexoWorkflow.class.getPackage().getName());

	private FlexoWorkflowResource _resource;

	private FlexoProcessNode _rootProcessNode;

	private Vector<FlexoProcessNode> _topLevelNodeProcesses;
	private Vector<FlexoProcessNode> importedRootNodeProcesses;

	private RoleList _roleList;
	private RoleList importedRoleList;

	private List<Role> allAssignableRoles;

	private String projectURI;

	public static FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> addProcessMetricsDefinitionActionizer;
	public static FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> addActivityMetricsDefinitionActionizer;
	public static FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> addOperationMetricsDefinitionActionizer;
	public static FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> addEdgeMetricsDefinitionActionizer;
	public static FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject> addArtefactMetricsDefinitionActionizer;

	public static FlexoActionizer<DeleteMetricsDefinition, MetricsDefinition, MetricsDefinition> deleteMetricsDefinitionActionizer;

	public FlexoWorkflow(FlexoWorkflowBuilder builder) {
		this(builder.getProject());
		builder.workflow = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	/**
	 * Create a new FlexoWorkflow.
	 */
	public FlexoWorkflow(FlexoProject project) {
		super(project, null);
		_topLevelNodeProcesses = new Vector<FlexoProcessNode>();
		importedRootNodeProcesses = new Vector<FlexoProcessNode>();
		processMetricsDefinitions = new Vector<MetricsDefinition>();
		activityMetricsDefinitions = new Vector<MetricsDefinition>();
		operationMetricsDefinitions = new Vector<MetricsDefinition>();
		edgeMetricsDefinitions = new Vector<MetricsDefinition>();
		artefactMetricsDefinitions = new Vector<MetricsDefinition>();
		try {
			setName(project.getProjectName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public FlexoWorkflowResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return _resource;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getWorkflowMapping();
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * workflow itself
	 * 
	 * @return this
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return this;
	}

	@Override
	public FlexoWorkflow getWorkflow() {
		return this;
	}

	/**
	 * Creates and returns a newly created workflow
	 * 
	 * @return a newly created workflow
	 * @throws InvalidFileNameException
	 */
	public static FlexoWorkflow createNewWorkflow(FlexoProject project) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("BEGIN createNewWorkflow(), project=" + project);
		}
		FlexoWorkflow newWorkflow = new FlexoWorkflow(project);

		File wkfFile = ProjectRestructuration.getExpectedWorkflowFile(project, project.getProjectName());
		FlexoProjectFile workflowFile = new FlexoProjectFile(wkfFile, project);
		FlexoWorkflowResource wkfRes = null;
		try {
			wkfRes = new FlexoWorkflowResource(project, newWorkflow, workflowFile);
		} catch (InvalidFileNameException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			project.registerResource(wkfRes);
		} catch (DuplicateResourceException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		}
		newWorkflow.createDefaultProcessMetricsDefinitions();
		newWorkflow.createDefaultActivityMetricsDefinitions();
		newWorkflow.createDefaultOperationMetricsDefinitions();
		newWorkflow.createDefaultEdgeMetricsDefinitions();
		newWorkflow.createDefaultArtefactMetricsDefinitions();

		// FlexoProcess.createNewRootProcess(newWorkflow);

		try {
			wkfRes.saveResourceData();
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("END createNewWorkflow(), project=" + project);
		}
		return newWorkflow;
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject().getProjectName() + ".WORKFLOW";
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoWorkflowResource) resource;
	}

	/**
	 * Save this object using ResourceManager scheme Additionnaly save all known processes related to this workflow
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();
	}

	public String getWorkflowName() {
		return getName();
	}

	public void setWorkflowName(String newName) throws Exception {
		super.setName(newName);
	}

	public File getFile() {
		return _resource.getResourceFile().getFile();
	}

	public Vector<FlexoProcessNode> _getTopLevelNodeProcesses() {
		return _topLevelNodeProcesses;
	}

	public void _setTopLevelNodeProcesses(Vector<FlexoProcessNode> topLevelProcesses) {
		_topLevelNodeProcesses = topLevelProcesses;
	}

	public void addToTopLevelNodeProcesses(FlexoProcessNode processNode) {
		if (!_topLevelNodeProcesses.contains(processNode)) {
			if (isDeserializing() && processNode.getFatherProcessNode() != null) {
				logger.severe("Found a sub process ('" + processNode.getName() + "') at top level -> Ignoring !");
				return;
			}
			_topLevelNodeProcesses.add(processNode);
			processNode.setFatherProcessNode(null);
			if (!isDeserializing()) {
				processNode.setIndexValue(_getTopLevelNodeProcesses().size());
				FlexoIndexManager.reIndexObjectOfArray(_getTopLevelNodeProcesses().toArray(new FlexoProcessNode[0]));
			}
			clearOrphanProcesses();
			setChanged();
			notifyObservers(new ProcessInserted(processNode.getProcess(), null));
		}
	}

	public void removeFromTopLevelNodeProcesses(FlexoProcessNode processNode) {
		if (_topLevelNodeProcesses.contains(processNode)) {
			_topLevelNodeProcesses.remove(processNode);
			clearOrphanProcesses();
			setChanged();
			notifyObservers(new ProcessRemoved(processNode.getProcess(), null));
		}
	}

	private Vector<FlexoProcessNode> orphanProcesses;

	public Vector<FlexoProcessNode> getOrphanProcesses() {
		if (orphanProcesses == null) {
			orphanProcesses = new Vector<FlexoProcessNode>();
			for (Enumeration<FlexoProcessNode> en = getSortedTopLevelProcesses(); en.hasMoreElements();) {
				FlexoProcessNode node = en.nextElement();
				if (node.getParentFolder() == null) {
					node.setIndex(orphanProcesses.size() + 1);
					orphanProcesses.add(node);
				}
			}
		}
		return orphanProcesses;
	}

	public Enumeration<FlexoProcessNode> getSortedOrphanSubprocesses() {
		disableObserving();
		FlexoProcessNode[] o = FlexoIndexManager.sortArray(getOrphanProcesses().toArray(new FlexoProcessNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public void clearOrphanProcesses() {
		orphanProcesses = null;
		getPropertyChangeSupport().firePropertyChange("sortedOrphanSubprocesses", this.orphanProcesses, null);
	}

	public Vector<FlexoProcessNode> getImportedRootNodeProcesses() {
		return importedRootNodeProcesses;
	}

	public void setImportedRootNodeProcesses(Vector<FlexoProcessNode> importedRootNodeProcesses) {
		this.importedRootNodeProcesses = importedRootNodeProcesses;
	}

	public void addToImportedRootNodeProcesses(FlexoProcessNode processNode) {
		if (!importedRootNodeProcesses.contains(processNode)) {

			if (isDeserializing() && processNode.getFatherProcessNode() != null) {
				logger.severe("Found a sub process ('" + processNode.getName() + "') at top level -> Ignoring !");
				return;
			}
			importedRootNodeProcesses.add(processNode);
			processNode.setFatherProcessNode(null);
			if (!isDeserializing()) {
				processNode.setIndexValue(getImportedRootNodeProcesses().size());
				FlexoIndexManager.reIndexObjectOfArray(getImportedRootNodeProcesses().toArray(new FlexoProcessNode[0]));
			}
			setChanged();
			notifyObservers(new ProcessInserted(processNode.getProcess(), null));
		}
	}

	public void removeFromImportedRootNodeProcesses(FlexoProcessNode processNode) {
		if (importedRootNodeProcesses.contains(processNode)) {
			importedRootNodeProcesses.remove(processNode);
			setChanged();
			notifyObservers(new ProcessRemoved(processNode.getProcess(), null));
		}
	}

	public Vector<FlexoProcess> getTopLevelFlexoProcesses() {
		Vector<FlexoProcess> v = new Vector<FlexoProcess>();
		for (FlexoProcessNode node : _getTopLevelNodeProcesses()) {
			v.add(node.getProcess());
		}
		return v;
	}

	public Vector<FlexoProcess> getImportedProcesses() {
		Vector<FlexoProcess> v = new Vector<FlexoProcess>();
		for (FlexoProcessNode node : getImportedRootNodeProcesses()) {
			v.add(node.getProcess());
		}
		return v;
	}

	public Vector<FlexoProcess> getNotDeletedImportedProcesses() {
		Vector<FlexoProcess> v = new Vector<FlexoProcess>();
		for (FlexoProcessNode node : getImportedRootNodeProcesses()) {
			if (!node.getProcess().isDeletedOnServer()) {
				v.add(node.getProcess());
			}
		}
		return v;
	}

	public FlexoProcess getImportedProcessWithURI(String uri) {
		return getObjectWithURI(getImportedProcesses(), uri);
	}

	public FlexoProcess getProcessWithURI(String uri) {
		return getObjectWithURI(getAllFlexoProcesses(), uri);
	}

	public FlexoProcess getRecursivelyImportedProcessWithURI(String uri) {
		return getRecursivelyImportedProcessWithURI(getImportedProcesses(), uri);
	}

	public static FlexoProcess getRecursivelyImportedProcessWithURI(Vector<FlexoProcess> processes, String uri) {
		FlexoProcess fip = getObjectWithURI(processes, uri);
		if (fip == null) {
			Enumeration<FlexoProcess> en = processes.elements();
			while (en.hasMoreElements() && fip == null) {
				FlexoProcess p = en.nextElement();
				fip = getRecursivelyImportedProcessWithURI(p.getSubProcesses(), uri);
			}
		}
		return fip;
	}

	public FlexoProcess importProcess(PPMProcess process) throws ProcessAlreadyImportedException, InvalidFileNameException,
			DuplicateResourceException, InvalidParentProcessException, InvalidProcessReferencesException {
		FlexoProcess fip = getImportedProcessWithURI(process.getUri());
		if (fip != null) {
			throw new ProcessAlreadyImportedException(process, fip);
		}
		fip = getRecursivelyImportedProcessWithURI(process.getUri());
		if (fip != null) {
			fip.setParentProcess(null);
			try {
				fip.updateFromObject(process);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			fip = FlexoProcess.createImportedProcessFromProcess(this, process);
		}
		return fip;
	}

	// Used by templates in doc
	public Enumeration<FlexoProcessNode> getSortedTopLevelProcesses() {
		disableObserving();
		FlexoProcessNode[] o = FlexoIndexManager.sortArray(_getTopLevelNodeProcesses().toArray(new FlexoProcessNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Enumeration<FlexoProcessNode> getSortedTopLevelImportedProcesses() {
		disableObserving();
		FlexoProcessNode[] o = FlexoIndexManager.sortArray(getImportedRootNodeProcesses().toArray(new FlexoProcessNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Enumeration<FlexoProcess> getSortedImportedProcesses() {
		disableObserving();
		Vector<FlexoProcess> vector = new Vector<FlexoProcess>();
		Enumeration<FlexoProcessNode> en = getSortedTopLevelImportedProcesses();
		while (en.hasMoreElements()) {
			vector.add(en.nextElement().getProcess());
		}
		enableObserving();
		return vector.elements();
	}

	private void addProcesses(Vector<FlexoProcessNode> temp, FlexoProcessNode process) {
		temp.add(process);
		for (Enumeration<FlexoProcessNode> e = process.getSubProcesses().elements(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			addProcesses(temp, currentProcessNode);
		}
	}

	public Vector<SubProcessNode> getAllSubProcessNodeUsingProcess(FlexoProcess process) {
		Vector<SubProcessNode> v = new Vector<SubProcessNode>();
		for (FlexoProcess p : getAllFlexoProcesses()) {
			Enumeration<SubProcessNode> en1 = p.getAllSubProcessNodes().elements();
			while (en1.hasMoreElements()) {
				SubProcessNode node = en1.nextElement();
				if (node.getSubProcess() == process) {
					v.add(node);
				}
			}
		}
		return v;
	}

	/**
	 * TODO: optimize me later ! Return an enumeration of all processes, by recursively explore the tree
	 * 
	 * @return an Enumeration of FlexoProcessNode elements
	 */
	public Enumeration<FlexoProcessNode> allLocalProcessNodes() {
		return getAllLocalProcessNodes().elements();
	}

	/**
	 * TODO: optimize me later ! Return an vector of all processes, by recursively explore the tree
	 * 
	 * @return an Vector of FlexoProcessNode elements
	 */
	public Vector<FlexoProcessNode> getAllLocalProcessNodes() {
		Vector<FlexoProcessNode> temp = new Vector<FlexoProcessNode>();
		for (Enumeration<FlexoProcessNode> en = _topLevelNodeProcesses.elements(); en.hasMoreElements();) {
			FlexoProcessNode aTopLevelProcessNode = en.nextElement();
			addProcesses(temp, aTopLevelProcessNode);
		}
		return temp;
	}

	/**
	 * TODO: optimize me later ! Return number of processes
	 * 
	 * @return int: number of processes declared in this workflow
	 */
	public int allLocalProcessesCount() {
		return getAllLocalProcessNodes().size();
	}

	/**
	 * TODO: optimize me later ! Return a Vector of all FlexoProcess objects defined in this workflow
	 * 
	 * @return Vector of FlexoProcess
	 */
	public Vector<FlexoProcess> getAllLocalFlexoProcesses() {
		// TODO: optimize me
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allLocalProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
				removeFromTopLevelNodeProcesses(currentProcessNode);
				setChanged();
			}
		}
		return returned;
	}

	/**
	 * TODO: optimize me later ! Return an enumeration of all processes, by recursively explore the tree
	 * 
	 * @return an Enumeration of FlexoProcessNode elements
	 */
	public Enumeration<FlexoProcessNode> allImportedProcessNodes() {
		return getAllImportedProcessNodes().elements();
	}

	/**
	 * TODO: optimize me later ! Return an vector of all processes, by recursively explore the tree
	 * 
	 * @return an Vector of FlexoProcessNode elements
	 */
	private Vector<FlexoProcessNode> getAllImportedProcessNodes() {
		Vector<FlexoProcessNode> temp = new Vector<FlexoProcessNode>();
		for (Enumeration<FlexoProcessNode> en = importedRootNodeProcesses.elements(); en.hasMoreElements();) {
			FlexoProcessNode aTopLevelProcessNode = en.nextElement();
			addProcesses(temp, aTopLevelProcessNode);
		}
		return temp;
	}

	/**
	 * TODO: optimize me later ! Return number of processes
	 * 
	 * @return int: number of processes declared in this workflow
	 */
	public int allImportedProcessesCount() {
		return getAllImportedProcessNodes().size();
	}

	/**
	 * TODO: optimize me later ! Return a Vector of all FlexoProcess objects defined in this workflow
	 * 
	 * @return Vector of FlexoProcess
	 */
	public Vector<FlexoProcess> getAllImportedFlexoProcesses() {
		// TODO: optimize me
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allImportedProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
				removeFromTopLevelNodeProcesses(currentProcessNode);
				setChanged();
			}
		}
		return returned;
	}

	/**
	 * TODO: optimize me later ! Return an enumeration of all processes, by recursively explore the tree
	 * 
	 * @return an Enumeration of FlexoProcessNode elements
	 */
	public Enumeration<FlexoProcessNode> allProcessNodes() {
		return getAllProcessNodes().elements();
	}

	/**
	 * TODO: optimize me later ! Return an vector of all processes, by recursively explore the tree
	 * 
	 * @return an Vector of FlexoProcessNode elements
	 */
	private Vector<FlexoProcessNode> getAllProcessNodes() {
		Vector<FlexoProcessNode> temp = new Vector<FlexoProcessNode>();
		temp.addAll(getAllLocalProcessNodes());
		temp.addAll(getAllImportedProcessNodes());
		return temp;
	}

	/**
	 * TODO: optimize me later ! Return number of processes
	 * 
	 * @return int: number of processes declared in this workflow
	 */
	public int allProcessesCount() {
		return getAllProcessNodes().size();
	}

	/**
	 * TODO: optimize me later ! Return a Vector of all FlexoProcess objects defined in this workflow
	 * 
	 * @return Vector of FlexoProcess
	 */
	public Vector<FlexoProcess> getAllFlexoProcesses() {
		// TODO: optimize me
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
			}
		}
		return returned;
	}

	public Vector<FlexoProcess> getAllLocalTopLevelFlexoProcesses() {
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allLocalProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
			}
		}
		return returned;
	}

	public Vector<FlexoProcess> getAllImportedTopLevelFlexoProcesses() {
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allImportedProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
			}
		}
		return returned;
	}

	public Vector<FlexoProcess> getAllTopLevelFlexoProcesses() {
		Vector<FlexoProcess> returned = new Vector<FlexoProcess>();
		for (Enumeration<FlexoProcessNode> e = allProcessNodes(); e.hasMoreElements();) {
			FlexoProcessNode currentProcessNode = e.nextElement();
			FlexoProcess process = currentProcessNode.getProcess();
			if (process != null) {
				if (!returned.contains(process)) {
					returned.add(process);
				}
			} else {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("ProcessNode:" + currentProcessNode.getName() + " have a null FlexoProcess, remove it !");
				}
				currentProcessNode.delete();
			}
		}
		return returned;
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	public Vector<FlexoModelObject> getAllEmbeddedWKFObjects() {
		Vector<FlexoModelObject> returned = new Vector<FlexoModelObject>();
		returned.add(this);
		Vector<FlexoProcess> allProcesses = getAllFlexoProcesses();
		for (Enumeration<FlexoProcess> e = allProcesses.elements(); e.hasMoreElements();) {
			FlexoProcess p = e.nextElement();
			returned.addAll(p.getAllEmbeddedWKFObjects());
		}
		returned.addAll(getAllImportedFlexoProcesses());
		return returned;
	}

	public FlexoProcess getLocalFlexoProcessWithName(String name) {
		for (Enumeration<FlexoProcessNode> e = allLocalProcessNodes(); e.hasMoreElements();) {
			FlexoProcess process = e.nextElement().getProcess();
			if (process != null) {
				if (process.getName().equals(name)) {
					return process;
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find process named " + name);
		}
		return null;
	}

	public FlexoProcess getLocalFlexoProcessWithFlexoID(long flexoID) {
		for (Enumeration<FlexoProcessNode> e = allLocalProcessNodes(); e.hasMoreElements();) {
			FlexoProcess process = e.nextElement().getProcess();
			if (process != null) {
				if (process.getFlexoID() == flexoID) {
					return process;
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Could not find process with ID " + flexoID);
		}
		return null;
	}

	@Override
	public String toString() {
		return "WORKFLOW";
	}

	// ==========================================================================
	// ============================= Instance methods
	// ===========================
	// ==========================================================================

	public Vector<OperationComponentInstance> getAllOperationInstantiatingTab(TabComponentDefinition tabDef) {
		Vector<OperationComponentInstance> reply = new Vector<OperationComponentInstance>();
		Enumeration<OperationComponentInstance> en = getAllComponentInstances().elements();
		OperationComponentInstance ci = null;
		while (en.hasMoreElements()) {
			ci = en.nextElement();
			Enumeration<TabComponentInstance> en2 = ci.getOperationComponentDefinition().getWOComponent().getAllTabComponentInstances()
					.elements();
			TabComponentInstance tabci = null;
			while (en2.hasMoreElements()) {
				tabci = en2.nextElement();
				if (tabci.getComponentDefinition().equals(tabDef)) {
					if (!reply.contains(ci)) {
						reply.add(ci);
					}
				}
			}

		}
		return reply;
	}

	public void _setRootProcessNode(FlexoProcessNode processNode) {
		if (_rootProcessNode != processNode) {
			FlexoProcessNode old = _rootProcessNode;
			_rootProcessNode = processNode;
			setChanged();
			notifyAttributeModification("rootProcessNode", old, _rootProcessNode);
			// Ugly hack
			if (old != null) {
				old.setChanged();
				old.notifyAttributeModification("rootProcessNode", old, _rootProcessNode);
			}
			if (_rootProcessNode != null) {
				_rootProcessNode.setChanged();
				_rootProcessNode.notifyAttributeModification("rootProcessNode", old, _rootProcessNode);
			}
		}
	}

	public FlexoProcessNode _getRootProcessNode() {
		return _rootProcessNode;
	}

	public FlexoProcess getRootProcess() {
		if (_getRootProcessNode() != null) {
			return _getRootProcessNode().getProcess();
		} else {
			if (_getTopLevelNodeProcesses().size() > 0) {
				logger.warning("No root process found, choosing first one !");
				setRootProcess(_getTopLevelNodeProcesses().firstElement().getProcess());
				return getRootProcess();
			} else {
				return null;
			}
		}
	}

	public FlexoProcess getRootFlexoProcess() {
		return getRootProcess();
	}

	public void setRootProcess(FlexoProcess aProcess) {
		_setRootProcessNode(aProcess.getProcessNode());

	}

	/*
	 * public Vector getAllComponents() { Vector answer = new Vector(); _getRootProcessNode().getAllComponents(answer); return answer; }
	 */
	public Vector<OperationComponentInstance> getAllComponentInstances() {
		Vector<OperationComponentInstance> answer = new Vector<OperationComponentInstance>();
		Enumeration<FlexoProcess> en = getAllLocalFlexoProcesses().elements();
		while (en.hasMoreElements()) {
			FlexoProcess p = en.nextElement();
			answer.addAll(p.getAllComponentInstances());
		}
		return answer;
	}

	public Vector<OperationComponentInstance> getAllComponentInstances(OperationComponentDefinition cd) {
		Vector<OperationNode> ops = new Vector<OperationNode>();
		Vector<OperationComponentInstance> answer = new Vector<OperationComponentInstance>();
		if (cd != null) {
			Enumeration<OperationComponentInstance> en = getAllComponentInstances().elements();
			while (en.hasMoreElements()) {
				OperationComponentInstance ci = en.nextElement();
				if (cd.equals(ci.getComponentDefinition()) && !ops.contains(ci.getOperationNode())) {
					ops.add(ci.getOperationNode());
					answer.add(ci);
				}
			}
		}
		return answer;
	}

	public Enumeration<FlexoProcess> getSortedProcesses() {
		disableObserving();
		Vector<FlexoProcess> vector = new Vector<FlexoProcess>();
		addSubProcesses(vector, getSortedTopLevelProcesses());
		enableObserving();
		return vector.elements();
	}

	private void addSubProcesses(Vector<FlexoProcess> vector, Enumeration<FlexoProcessNode> en) {
		while (en.hasMoreElements()) {
			FlexoProcessNode processNode = en.nextElement();
			vector.add(processNode.getProcess());
			addSubProcesses(vector, processNode.getSortedSubprocesses());
		}
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	/**
	 * @param baseName
	 * @param node
	 * @return
	 */
	public String findNextDefaultProcessName(String baseName) {
		int i = 2;
		String tryMe = baseName;
		while (getLocalFlexoProcessWithName(tryMe) != null) {
			tryMe = baseName + i;
			i++;
		}
		return tryMe;
	}

	public Role getDefaultRole() {
		if (getRoleList() != null) {
			return getRoleList().getDefaultRole();
		}
		return null;
	}

	/**
	 * @return
	 */
	public RoleList getRoleList() {
		if (_roleList == null && !isDeserializing() && !isSerializing()) {
			_roleList = new RoleList(getProject(), this);
		}
		if (_roleList != null && !isDeserializing()) {
			_roleList.assertDefaultRoleHasBeenCreated();
		}
		return _roleList;
	}

	/**
	 * @param list
	 */
	public void setRoleList(RoleList list) {
		_roleList = list;
	}

	/**
	 * @return
	 */
	public RoleList getImportedRoleList() {
		return getImportedRoleList(false);
	}

	public RoleList getImportedRoleList(boolean createIfNotExist) {
		if (importedRoleList == null && !isDeserializing() && !isSerializing() && createIfNotExist) {
			importedRoleList = new RoleList(getProject(), this);
			setChanged();
			notifyObservers(new ImportedRoleLibraryCreated());
		}
		return importedRoleList;
	}

	/**
	 * @param list
	 */
	public void setImportedRoleList(RoleList list) {
		importedRoleList = list;
	}

	@Override
	public void notifyAttributeModification(String attributeName, Object oldValue, Object newValue) {
		setChanged();
		notifyObservers(new AttributeDataModification(attributeName, oldValue, newValue));
	}

	public boolean getUseTransparency() {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.USE_TRANSPARENCY.getSerializationName(), true);
	}

	public boolean getUseTransparency(boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.USE_TRANSPARENCY.getSerializationName(), defaultValue);
	}

	public void setUseTransparency(boolean value) {
		if (value != getUseTransparency()) {
			_setGraphicalPropertyForKey(value, GraphicalProperties.USE_TRANSPARENCY.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.USE_TRANSPARENCY.getSerializationName(), !value, value);
		}
	}

	public boolean getShowShadows() {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_SHADOWS.getSerializationName(), true);
	}

	public boolean getShowShadows(boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_SHADOWS.getSerializationName(), defaultValue);
	}

	public void setShowShadows(boolean value) {
		if (value != getShowShadows()) {
			_setGraphicalPropertyForKey(value, GraphicalProperties.SHOW_SHADOWS.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.SHOW_SHADOWS.getSerializationName(), !value, value);
		}
	}

	public boolean getShowWOName() {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_WO_NAME.getSerializationName(), true);
	}

	public boolean getShowWOName(boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_WO_NAME.getSerializationName(), defaultValue);
	}

	public void setShowWOName(boolean value) {
		if (value != getShowWOName()) {
			_setGraphicalPropertyForKey(value, GraphicalProperties.SHOW_WO_NAME.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.SHOW_WO_NAME.getSerializationName(), !value, value);
		}
	}

	public boolean getShowMessages() {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_MESSAGES.getSerializationName(), true);
	}

	public boolean getShowMessages(boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(GraphicalProperties.SHOW_MESSAGES.getSerializationName(), defaultValue);
	}

	public void setShowMessages(boolean value) {
		if (value != getShowMessages()) {
			_setGraphicalPropertyForKey(value, GraphicalProperties.SHOW_MESSAGES.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.SHOW_MESSAGES.getSerializationName(), !value, value);
		}
	}

	public FlexoFont getActivityFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ACTIVITY_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getActivityFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ACTIVITY_FONT.getSerializationName(), defaultValue);
	}

	public void setActivityFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.ACTIVITY_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.ACTIVITY_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.ACTIVITY_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.ACTIVITY_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getOperationFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.OPERATION_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getOperationFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.OPERATION_FONT.getSerializationName(), defaultValue);
	}

	public void setOperationFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.OPERATION_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.OPERATION_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.OPERATION_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.OPERATION_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getActionFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getActionFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName(), defaultValue);
	}

	public void setActionFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			logger.info("Was:" + _graphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName()));
			logger.info("Is now:" + textFont);
			logger.info("Equals:" + _graphicalPropertyForKey(GraphicalProperties.ACTION_FONT.getSerializationName()).equals(textFont));
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.ACTION_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.ACTION_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getEventFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.EVENT_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getEventFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.EVENT_FONT.getSerializationName(), defaultValue);
	}

	public void setEventFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.EVENT_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.EVENT_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.EVENT_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.EVENT_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getRoleFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ROLE_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getRoleFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ROLE_FONT.getSerializationName(), defaultValue);
	}

	public void setRoleFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.ROLE_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.ROLE_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.ROLE_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.ROLE_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getComponentFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.COMPONENT_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getComponentFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.COMPONENT_FONT.getSerializationName(), defaultValue);
	}

	public void setComponentFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.COMPONENT_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.COMPONENT_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.COMPONENT_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.COMPONENT_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getArtefactFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ARTEFACT_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getArtefactFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.ARTEFACT_FONT.getSerializationName(), defaultValue);
	}

	public void setArtefactFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.ARTEFACT_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.ARTEFACT_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.ARTEFACT_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.ARTEFACT_FONT.getSerializationName(), null, textFont);
		}
	}

	public FlexoFont getEdgeFont() {
		return _fontGraphicalPropertyForKey(GraphicalProperties.EDGE_FONT.getSerializationName(), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getEdgeFont(FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(GraphicalProperties.EDGE_FONT.getSerializationName(), defaultValue);
	}

	public void setEdgeFont(FlexoFont textFont) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.EDGE_FONT.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.EDGE_FONT.getSerializationName()).equals(textFont);
		if (needChanged) {
			_setGraphicalPropertyForKey(textFont, GraphicalProperties.EDGE_FONT.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.EDGE_FONT.getSerializationName(), null, textFont);
		}
	}

	public Object getConnectorRepresentation() {
		return _graphicalPropertyForKey(GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName());
	}

	public Object getConnectorRepresentation(Object defaultValue) {
		return _objectGraphicalPropertyForKey(GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName(), defaultValue);
	}

	public void setConnectorRepresentation(Object connectorRepresentation) {
		boolean needChanged = !hasGraphicalPropertyForKey(GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName())
				|| !_graphicalPropertyForKey(GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName()).equals(
						connectorRepresentation);
		if (needChanged) {
			_setGraphicalPropertyForKey(connectorRepresentation, GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName());
			setChanged();
			notifyAttributeModification(GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName(), null, connectorRepresentation);
		}
	}

	public Vector<Role> getAllRoles() {
		Vector<Role> reply = new Vector<Role>();
		reply.addAll(getRoleList().getRoles());
		if (getImportedRoleList() != null) {
			reply.addAll(getImportedRoleList().getRoles());
		}
		return reply;
	}

	public void clearAssignableRolesCache() {
		allAssignableRoles = null;
		if (manager != null) {
			manager.delete();
		}
		manager = null;
		notifyAttributeModification(ALL_ASSIGNABLE_ROLES, null, null);
	}

	private PropertyChangeListenerRegistrationManager manager;

	public List<Role> getAllAssignableRoles() {
		if (allAssignableRoles == null) {
			manager = new PropertyChangeListenerRegistrationManager();
			allAssignableRoles = new ArrayList<Role>();
			RoleList roleList = getRoleList();
			appendRoles(roleList, allAssignableRoles);
			if (!isCache()) {
				if (getProject().getProjectData() != null) {
					manager.addListener(ProjectData.IMPORTED_PROJECTS, new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							clearAssignableRolesCache();
						}
					}, getProject().getProjectData());
					for (FlexoProjectReference ref : getProject().getProjectData().getImportedProjects()) {
						manager.addListener(FlexoProjectReference.WORKFLOW, new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								clearAssignableRolesCache();
							}
						}, ref);
						if (ref.getWorkflow() != null) {
							allAssignableRoles.addAll(ref.getWorkflow().getAllAssignableRoles());
							manager.addListener(ALL_ASSIGNABLE_ROLES, new PropertyChangeListener() {

								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									clearAssignableRolesCache();
								}
							}, ref.getWorkflow());
						}
					}
				} else {
					manager.addListener(FlexoProject.PROJECT_DATA, new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							clearAssignableRolesCache();
						}
					}, getProject());
				}
				appendRoles(getImportedRoleList(), allAssignableRoles);
				allAssignableRoles = Collections.unmodifiableList(allAssignableRoles);
			}
		}
		return allAssignableRoles;
	}

	public void appendRoles(RoleList roleList, List<Role> reply) {
		if (roleList != null) {
			for (Role r : roleList.getRoles()) {
				if (r.getIsAssignable()) {
					reply.add(r);
				}
			}
			if (getImportedRoleList() != null) {
				for (Role r : getImportedRoleList().getRoles()) {
					if (r.getIsAssignable()) {
						reply.add(r);
					}
				}
			}
		}
	}

	public Vector<Role> getAllSortedRoles() {
		Vector<Role> reply = new Vector<Role>();
		reply.addAll(getRoleList().getSortedRolesVector());
		if (getImportedRoleList() != null) {
			reply.addAll(getImportedRoleList().getSortedRolesVector());
		}
		return reply;
	}

	public Role getRoleWithURI(String uri) {
		Role reply = getRoleList().getImportedObjectWithURI(uri);
		if (reply == null && getImportedRoleList() != null) {
			reply = getImportedRoleList().getImportedObjectWithURI(uri);
		}
		return reply;
	}

	private FlexoImportedProcessLibrary importedProcessLibrary;

	public FlexoImportedProcessLibrary getImportedProcessLibrary() {
		if (importedProcessLibrary == null && getImportedProcesses().size() > 0) {
			importedProcessLibrary = new FlexoImportedProcessLibrary(this);
			setChanged(false);
			notifyObservers(new ImportedProcessLibraryCreated());
		}
		return importedProcessLibrary;
	}

	/**
	 * @return
	 */
	public Enumeration<Role> getSortedRoles() {
		return getRoleList().getSortedRoles();
	}

	// ========================================================================
	// ========================= Validable interface ==========================
	// ========================================================================

	/**
	 * Return a vector of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> returned = new Vector<Validable>();
		returned.add(this);
		Vector allProcesses = getAllFlexoProcesses();
		for (Enumeration e = allProcesses.elements(); e.hasMoreElements();) {
			FlexoProcess p = (FlexoProcess) e.nextElement();
			returned.addAll(p.getAllEmbeddedValidableObjects());
		}
		returned.addAll(getRoleList().getAllEmbeddedValidableObjects());
		if (getImportedRoleList() != null) {
			returned.addAll(getImportedRoleList().getAllEmbeddedValidableObjects());
		}
		returned.addAll(getProcessMetricsDefinitions());
		returned.addAll(getActivityMetricsDefinitions());
		returned.addAll(getOperationMetricsDefinitions());
		return returned;
	}

	public static class WorkflowMustHaveARootProcess extends ValidationRule<WorkflowMustHaveARootProcess, FlexoWorkflow> {
		public WorkflowMustHaveARootProcess() {
			super(FlexoWorkflow.class, "workflow_must_have_a_root_process");
		}

		@Override
		public ValidationIssue<WorkflowMustHaveARootProcess, FlexoWorkflow> applyValidation(FlexoWorkflow workflow) {
			if (workflow.getRootFlexoProcess() == null) {
				return new ValidationError<WorkflowMustHaveARootProcess, FlexoWorkflow>(this, workflow,
						"workflow_($object.name)_has_no_root_process");
			}
			return null;
		}
	}

	public static class BusinessDataClassMustHaveAStatusColumn extends
			ValidationRule<BusinessDataClassMustHaveAStatusColumn, FlexoWorkflow> {
		public BusinessDataClassMustHaveAStatusColumn() {
			super(FlexoWorkflow.class, "business_data_class_must_have_a_status_column");
		}

		@Override
		public ValidationIssue<BusinessDataClassMustHaveAStatusColumn, FlexoWorkflow> applyValidation(FlexoWorkflow workflow) {
			for (FlexoProcess p : workflow.getAllLocalFlexoProcesses()) {
				if (p.getBusinessDataType() == null) {
					continue;
				}
				Enumeration<DMProperty> en = p.getBusinessDataType().getProperties().elements();
				boolean found = false;
				while (en.hasMoreElements() && !found) {
					DMProperty prop = en.nextElement();
					if (prop.getName() != null && prop.getName().toLowerCase().startsWith("status") && prop.getType() != null
							&& prop.getType().isString()) {
						found = true;
					}
				}
				if (!found) {
					return new ValidationError<BusinessDataClassMustHaveAStatusColumn, FlexoWorkflow>(this, workflow,
							"business_data_class_must_have_a_status_column", new CreateStatusProperty(p.getBusinessDataType()));
				}
			}
			return null;
		}

		public static class CreateStatusProperty extends FixProposal {

			private DMEntity entity;

			/**
			 * @param aMessage
			 */
			public CreateStatusProperty(DMEntity e) {
				super("create_status_property");
				this.entity = e;
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				if (entity instanceof DMEOEntity) {
					DMEOEntity eo = (DMEOEntity) entity;
					DMEOAttribute att;
					try {
						att = DMEOAttribute.createsNewDMEOAttribute(eo.getDMModel(), eo, "status", false, true,
								DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
						att.setPrototype(eo.getDMModel().getEOPrototypeRepository().getPrototypeNamed("str10"));
						eo.registerProperty(att);
					} catch (EOAccessException e) {
						e.printStackTrace();
					}
				} else {
					DMProperty prop = new DMProperty(entity.getDMModel(), "status", DMType.makeResolvedDMType(entity.getDMModel()
							.getDMEntity(String.class)), DMCardinality.SINGLE, false, true,
							DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
					entity.registerProperty(prop, true);
				}
			}

		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return true;
			// return targetType!=CodeType.PROTOTYPE;
		}
	}

	public static class BusinessDataMustNotBeReadOnly extends ValidationRule<BusinessDataMustNotBeReadOnly, FlexoWorkflow> {
		public BusinessDataMustNotBeReadOnly() {
			super(FlexoWorkflow.class, "business_data_class_must_not_be_read_only");
		}

		@Override
		public ValidationIssue<BusinessDataMustNotBeReadOnly, FlexoWorkflow> applyValidation(FlexoWorkflow workflow) {
			for (FlexoProcess p : workflow.getAllLocalFlexoProcesses()) {
				if (p.getBusinessDataType() == null) {
					continue;
				}
				if (p.getBusinessDataType().getIsReadOnly()) {
					return new ValidationError<BusinessDataMustNotBeReadOnly, FlexoWorkflow>(this, workflow,
							"business_data_must_not_be_readonly");
				}
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
			return true;
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "workflow";
	}

	public Vector<TabComponentInstance> getAllTabInstances(TabComponentDefinition definition) {
		Vector<TabComponentInstance> reply = new Vector<TabComponentInstance>();
		Enumeration<OperationComponentInstance> operations = getAllComponentInstances().elements();
		Vector<IETabWidget> tabs = new Vector<IETabWidget>();
		while (operations.hasMoreElements()) {
			operations.nextElement().getComponentDefinition().getWOComponent().getAllTabWidget(tabs);
		}
		Enumeration<IETabWidget> en = tabs.elements();
		IETabWidget t = null;
		while (en.hasMoreElements()) {
			t = en.nextElement();
			if (t.getTabComponentDefinition().equals(definition) && !reply.contains(t.getComponentInstance())) {
				reply.add(t.getComponentInstance());
			}
		}
		return reply;
	}

	public List<ApplicationHelpEntryPoint> getAllHelpEntryPoints() {
		Vector<ApplicationHelpEntryPoint> answer = new Vector<ApplicationHelpEntryPoint>();
		Enumeration<FlexoModelObject> en = getAllEmbeddedWKFObjects().elements();
		while (en.hasMoreElements()) {
			FlexoModelObject p = en.nextElement();
			if (p instanceof FlexoProcess && ((FlexoProcess) p).isImported()) {
				continue;
			}
			if (p instanceof ApplicationHelpEntryPoint) {
				if (p instanceof PetriGraphNode) {
					if (!((PetriGraphNode) p).getDontGenerateRecursive()) {
						if (p instanceof FlexoNode) {
							if (((FlexoNode) p).getNodeType() != NodeType.BEGIN && ((FlexoNode) p).getNodeType() != NodeType.END) {
								if (p instanceof OperationNode) {
									if (((OperationNode) p).getComponentInstance() != null) {
										answer.add((ApplicationHelpEntryPoint) p);
									}
								} else {
									answer.add((ApplicationHelpEntryPoint) p);
								}
							}
						} else {
							answer.add((ApplicationHelpEntryPoint) p);
						}
					}
				} else {
					answer.add((ApplicationHelpEntryPoint) p);
				}
			}
		}
		return answer;
	}

	/**
	 * Recovery method use to detect and repair inconsistencies in the model regarding Process and ProcessDMEntities.
	 */

	public void checkProcessDMEntitiesConsistency() {
		int processCount = getAllLocalFlexoProcesses().size();
		int processDMEntityCount = getProject().getDataModel().getProcessInstanceRepository().getEntities().size();
		if (processCount != processDMEntityCount) {
			logger.severe("Found : " + processCount + " but there is " + processDMEntityCount + " processDMEntity");
		}
		Vector<DMEntity> entities = new Vector<DMEntity>(getProject().getDataModel().getProcessInstanceRepository().getEntities().values());
		Enumeration<FlexoProcess> en = getAllLocalFlexoProcesses().elements();
		while (en.hasMoreElements()) {
			FlexoProcess process = en.nextElement();
			if (process.isImported()) {
				continue;
			}
			ProcessDMEntity processEntity = process.getProcessDMEntity();
			if (processEntity == null) {
				logger.severe("No process entity found for " + process.getName());
			}
			if (entities.contains(processEntity)) {
				entities.remove(processEntity);
			} else {
				logger.severe("Found process entity found for " + process.getName() + " but was not in ProcessInstanceRepository.");
			}
		}
		if (entities.size() > 0) {
			logger.severe("Got " + entities.size() + " without processes !");
			Enumeration<DMEntity> en2 = entities.elements();
			while (en2.hasMoreElements()) {
				en2.nextElement().delete();
			}
		}

		// now ensure that all process instances are in a package
		en = getAllLocalFlexoProcesses().elements();
		while (en.hasMoreElements()) {
			FlexoProcess process = en.nextElement();
			ProcessDMEntity processEntity = process.getProcessDMEntity();
			if (processEntity.getPackage().isDefaultPackage()) {
				logger.severe("Process entity for " + process.getName() + " is in default package :-( I put it in a package :-)");
				processEntity.moveToPackage(process.getExecutionGroupName());
			}
		}

	}

	private Vector<MetricsDefinition> processMetricsDefinitions;
	private Vector<MetricsDefinition> activityMetricsDefinitions;
	private Vector<MetricsDefinition> operationMetricsDefinitions;
	private Vector<MetricsDefinition> edgeMetricsDefinitions;
	private Vector<MetricsDefinition> artefactMetricsDefinitions;

	public Vector<MetricsDefinition> getProcessMetricsDefinitions() {
		return processMetricsDefinitions;
	}

	public void setProcessMetricsDefinitions(Vector<MetricsDefinition> definitions) {
		this.processMetricsDefinitions = definitions;
		setChanged();
	}

	public void addToProcessMetricsDefinitions(MetricsDefinition definition) {
		if (!processMetricsDefinitions.contains(definition)) {
			processMetricsDefinitions.add(definition);
			if (definition.getAlwaysDefined()) {
				updateProcessModelObjects(definition);
			}
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "processMetricsDefinitions"));
		}
	}

	public boolean removeFromProcessMetricsDefinitions(MetricsDefinition definition) {
		boolean b = processMetricsDefinitions.remove(definition);
		if (b) {
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "processMetricsDefinitions"));
		}
		return b;
	}

	public void updateMetricsForProcess(FlexoProcess process) {
		for (MetricsDefinition definition : getProcessMetricsDefinitions()) {
			if (definition.getAlwaysDefined()) {
				updateMetricsValueOwner(definition, process);
			}
		}
	}

	public Vector<MetricsDefinition> getActivityMetricsDefinitions() {
		return activityMetricsDefinitions;
	}

	public void setActivityMetricsDefinitions(Vector<MetricsDefinition> definitions) {
		this.activityMetricsDefinitions = definitions;
		setChanged();
	}

	public void addToActivityMetricsDefinitions(MetricsDefinition definition) {
		if (!activityMetricsDefinitions.contains(definition)) {
			activityMetricsDefinitions.add(definition);
			if (definition.getAlwaysDefined()) {
				updateProcessModelObjects(definition);
			}
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "activityMetricsDefinitions"));
		}
	}

	public boolean removeFromActivityMetricsDefinitions(MetricsDefinition definition) {
		boolean b = activityMetricsDefinitions.remove(definition);
		if (b) {
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "activityMetricsDefinitions"));
		}
		return b;
	}

	public void updateMetricsForActivity(AbstractActivityNode activity) {
		for (MetricsDefinition definition : getActivityMetricsDefinitions()) {
			if (definition.getAlwaysDefined()) {
				updateMetricsValueOwner(definition, activity);
			}
		}
	}

	public Vector<MetricsDefinition> getOperationMetricsDefinitions() {
		return operationMetricsDefinitions;
	}

	public void setOperationMetricsDefinitions(Vector<MetricsDefinition> definitions) {
		this.operationMetricsDefinitions = definitions;
		setChanged();
	}

	public void addToOperationMetricsDefinitions(MetricsDefinition definition) {
		if (!operationMetricsDefinitions.contains(definition)) {
			operationMetricsDefinitions.add(definition);
			if (definition.getAlwaysDefined()) {
				updateProcessModelObjects(definition);
			}
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "operationMetricsDefinitions"));
		}
	}

	public boolean removeFromOperationMetricsDefinitions(MetricsDefinition definition) {
		boolean b = operationMetricsDefinitions.remove(definition);
		if (b) {
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "operationMetricsDefinitions"));
		}
		return b;
	}

	public void updateMetricsForOperation(OperationNode operation) {
		for (MetricsDefinition definition : getOperationMetricsDefinitions()) {
			if (definition.getAlwaysDefined()) {
				updateMetricsValueOwner(definition, operation);
			}
		}
	}

	public Vector<MetricsDefinition> getEdgeMetricsDefinitions() {
		return edgeMetricsDefinitions;
	}

	public void setEdgeMetricsDefinitions(Vector<MetricsDefinition> definitions) {
		this.edgeMetricsDefinitions = definitions;
		setChanged();
	}

	public void addToEdgeMetricsDefinitions(MetricsDefinition definition) {
		if (!edgeMetricsDefinitions.contains(definition)) {
			edgeMetricsDefinitions.add(definition);
			if (definition.getAlwaysDefined()) {
				updateProcessModelObjects(definition);
			}
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "edgeMetricsDefinitions"));
		}
	}

	public boolean removeFromEdgeMetricsDefinitions(MetricsDefinition definition) {
		boolean b = edgeMetricsDefinitions.remove(definition);
		if (b) {
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "edgeMetricsDefinitions"));
		}
		return b;
	}

	public void updateMetricsForEdge(FlexoPostCondition edge) {
		for (MetricsDefinition definition : getEdgeMetricsDefinitions()) {
			if (definition.getAlwaysDefined()) {
				updateMetricsValueOwner(definition, edge);
			}
		}
	}

	public Vector<MetricsDefinition> getArtefactMetricsDefinitions() {
		return artefactMetricsDefinitions;
	}

	public void setArtefactMetricsDefinitions(Vector<MetricsDefinition> definitions) {
		this.artefactMetricsDefinitions = definitions;
		setChanged();
	}

	public void addToArtefactMetricsDefinitions(MetricsDefinition definition) {
		if (!artefactMetricsDefinitions.contains(definition)) {
			artefactMetricsDefinitions.add(definition);
			if (definition.getAlwaysDefined()) {
				updateProcessModelObjects(definition);
			}
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "artefactMetricsDefinitions"));
		}
	}

	public boolean removeFromArtefactMetricsDefinitions(MetricsDefinition definition) {
		boolean b = artefactMetricsDefinitions.remove(definition);
		if (b) {
			setChanged();
			notifyObservers(new MetricsDefinitionAdded(definition, "artefactMetricsDefinitions"));
		}
		return b;
	}

	public void updateMetricsForArtefact(WKFArtefact artefact) {
		for (MetricsDefinition definition : getArtefactMetricsDefinitions()) {
			if (definition.getAlwaysDefined()) {
				updateMetricsValueOwner(definition, artefact);
			}
		}
	}

	public void removeFromMetricsDefinition(MetricsDefinition definition) {
		boolean ok = removeFromProcessMetricsDefinitions(definition);
		if (!ok) {
			ok = removeFromActivityMetricsDefinitions(definition);
		}
		if (!ok) {
			ok = removeFromOperationMetricsDefinitions(definition);
		}
		if (!ok) {
			removeFromEdgeMetricsDefinitions(definition);
		}
		if (!ok) {
			removeFromArtefactMetricsDefinitions(definition);
		}
	}

	public void addProcessMetricsDefinition() {
		if (addProcessMetricsDefinitionActionizer != null) {
			addProcessMetricsDefinitionActionizer.run(this, null);
		}
	}

	public void addActivityMetricsDefinition() {
		if (addActivityMetricsDefinitionActionizer != null) {
			addActivityMetricsDefinitionActionizer.run(this, null);
		}
	}

	public void addOperationMetricsDefinition() {
		if (addOperationMetricsDefinitionActionizer != null) {
			addOperationMetricsDefinitionActionizer.run(this, null);
		}
	}

	public void addEdgeMetricsDefinition() {
		if (addEdgeMetricsDefinitionActionizer != null) {
			addEdgeMetricsDefinitionActionizer.run(this, null);
		}
	}

	public void addArtefactMetricsDefinition() {
		if (addArtefactMetricsDefinitionActionizer != null) {
			addArtefactMetricsDefinitionActionizer.run(this, null);
		}
	}

	public void deleteMetricsDefinition(MetricsDefinition definition) {
		if (deleteMetricsDefinitionActionizer != null) {
			deleteMetricsDefinitionActionizer.run(definition, null);
		}
	}

	public void createDefaultProcessMetricsDefinitions() {
		MetricsDefinition md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("process_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("process_time_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("lead_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("lead_time_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("percent_complete_and_accurate"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("percent_complete_and_accurate_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("demand_rate"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("demand_rate_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("inventory"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("inventory_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("typical_batch_size_or_practices"));
		md.setType(MetricsType.NUMBER);
		md.setDescription(FlexoLocalization.localizedForKey("typical_batch_size_or_practices_description"));
		addToProcessMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("available_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("available_time_description"));
		addToProcessMetricsDefinitions(md);
	}

	public void createDefaultActivityMetricsDefinitions() {
		MetricsDefinition md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("process_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("process_time_description"));
		addToActivityMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("lead_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("lead_time_description"));
		addToActivityMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("percent_complete_and_accurate"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("percent_complete_and_accurate_description"));
		addToActivityMetricsDefinitions(md);
	}

	public void createDefaultOperationMetricsDefinitions() {
		MetricsDefinition md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("process_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("process_time_description"));
		addToOperationMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("lead_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("lead_time_description"));
		addToOperationMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("percent_complete_and_accurate"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("percent_complete_and_accurate_description"));
		addToOperationMetricsDefinitions(md);
	}

	public void createDefaultEdgeMetricsDefinitions() {
		MetricsDefinition md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("probability"));
		md.setType(MetricsType.DOUBLE);
		md.setDescription(FlexoLocalization.localizedForKey("probability_description"));
		md.setAlwaysDefined(true);
		addToEdgeMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("rate"));
		md.setType(MetricsType.NUMBER);
		md.setDescription(FlexoLocalization.localizedForKey("rate_description"));
		addToEdgeMetricsDefinitions(md);
	}

	public void createDefaultArtefactMetricsDefinitions() {
		MetricsDefinition md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("process_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("process_time_description"));
		addToArtefactMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("lead_time"));
		md.setType(MetricsType.TIME);
		md.setDescription(FlexoLocalization.localizedForKey("lead_time_description"));
		addToArtefactMetricsDefinitions(md);
		md = new MetricsDefinition(getProject(), this);
		md.setName(FlexoLocalization.localizedForKey("percent_complete_and_accurate"));
		md.setType(MetricsType.TEXT);
		md.setDescription(FlexoLocalization.localizedForKey("percent_complete_and_accurate_description"));
		addToArtefactMetricsDefinitions(md);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.WORKFLOW_INSPECTOR;
	}

	public void updateProcessModelObjects(MetricsDefinition metricsDefinition) {
		if (isDeserializing()) {
			return;
		}
		if (metricsDefinition.isProcessMetricsDefinition()) {
			for (FlexoProcess process : getAllLocalFlexoProcesses()) {
				updateMetricsValueOwner(metricsDefinition, process);
			}
		} else if (metricsDefinition.isActivityMetricsDefinition()) {
			for (FlexoProcess process : getAllLocalFlexoProcesses()) {
				for (AbstractActivityNode activity : process.getAllEmbeddedAbstractActivityNodes()) {
					updateMetricsValueOwner(metricsDefinition, activity);
				}
			}
		} else if (metricsDefinition.isOperationMetricsDefinition()) {
			for (FlexoProcess process : getAllLocalFlexoProcesses()) {
				for (OperationNode operation : process.getAllEmbeddedOperationNodes()) {
					updateMetricsValueOwner(metricsDefinition, operation);
				}
			}
		} else if (metricsDefinition.isEdgeMetricsDefinition()) {
			for (FlexoProcess process : getAllLocalFlexoProcesses()) {
				for (FlexoPostCondition postCondition : process.getAllPostConditions()) {
					updateMetricsValueOwner(metricsDefinition, postCondition);
				}
			}
		} else if (metricsDefinition.isArtefactMetricsDefinition()) {
			for (FlexoProcess process : getAllLocalFlexoProcesses()) {
				for (WKFArtefact artefact : process.getAllEmbeddedArtefacts()) {
					updateMetricsValueOwner(metricsDefinition, artefact);
				}
			}
		}
	}

	/**
	 * @param metricsDefinition
	 * @param alwaysDefined
	 * @param process
	 */
	private void updateMetricsValueOwner(MetricsDefinition metricsDefinition, MetricsValueOwner owner) {
		MetricsValue found = null;
		for (MetricsValue mv : owner.getMetricsValues()) {
			if (mv.getMetricsDefinition() == metricsDefinition) {
				found = mv;
				break;
			}
		}
		boolean alwaysDefined = metricsDefinition.getAlwaysDefined();
		if (alwaysDefined && found == null) {
			MetricsValue mv = new MetricsValue(owner.getProcess());
			mv.setMetricsDefinition(metricsDefinition);
			owner.addToMetricsValues(mv);
		} else if (!alwaysDefined && found != null && found.getValue() == null) {
			found.delete();
		}
	}

	public String getProjectURI() {
		if (isCache()) {
			return getFlexoResource().getProjectURI();
		} else {
			return getProject().getURI();
		}
	}

	public Role getCachedRole(FlexoModelObjectReference<Role> reference) {
		if (reference.getObject() != null) {
			return reference.getObject();
		} else {
			String projectURI = reference.getEnclosingProjectIdentifier();
			if (projectURI != null) {
				ProjectData data = getProject().getProjectData();
				if (data != null) {
					FlexoProjectReference projectRef = data.getProjectReferenceWithURI(projectURI, true);
					if (projectRef != null) {
						return projectRef.getWorkflow().getRoleList().getRoleWithFlexoID(reference.getFlexoID());
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isCache() {
		return getFlexoResource().isCache();
	}

	@Override
	public FlexoModelObject getUncachedObject() {
		return super.getUncachedObject();
	}

	@Override
	public FlexoProcessNode getProcessNode() {
		return null;
	}

}
