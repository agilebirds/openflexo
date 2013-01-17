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
package org.openflexo.foundation.rm;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.XMLUtils;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FlexoVersion;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoWorkflowResource extends FlexoXMLStorageResource<FlexoWorkflow> {

	private static final Logger logger = Logger.getLogger(FlexoWorkflowResource.class.getPackage().getName());

	private String projectURI;

	private ResourceType resourceType = ResourceType.WORKFLOW;

	private String name;

	private boolean useProjectName;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoWorkflowResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	private FlexoWorkflowResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoWorkflowResource(FlexoProject aProject, FlexoWorkflow workflow, FlexoProjectFile workflowFile)
			throws InvalidFileNameException {
		this(aProject);
		_resourceData = workflow;
		setResourceFile(workflowFile);
		workflow.setFlexoResource(this);
	}

	public FlexoWorkflowResource(FlexoProject aProject, FlexoWorkflow workflow, FlexoProjectFile workflowFile, ResourceType resourceType,
			String name) throws InvalidFileNameException {
		this(aProject);
		this.resourceType = resourceType;
		this.name = name;
		this._resourceData = workflow;
		setResourceFile(workflowFile);

	}

	@Override
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public void replaceWithWorkflow(FlexoWorkflow workflow) {
		_resourceData = workflow;
		setChanged();
	}

	@Override
	public String getName() {
		if (name != null) {
			return name;
		}
		return getProject().getProjectName();
	}

	@Override
	public void setName(String aName) {
		this.name = aName;
	}

	public boolean isUseProjectName() {
		return useProjectName;
	}

	public void setUseProjectName(boolean useProjectName) {
		this.useProjectName = useProjectName;
		if (useProjectName) {
			name = null;
		}
	}

	@Override
	public Class getResourceDataClass() {
		return FlexoWorkflow.class;
	}

	@Override
	public FlexoWorkflow performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		FlexoWorkflow workflow;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_workflow"));
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("loadResourceData() in FlexoWorkflowResource");
		}
		workflow = super.performLoadResourceData(progress, loadingHandler);
		// Load all processes
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_processes"));
			progress.resetSecondaryProgress(project.getFlexoWorkflow().allLocalProcessesCount());
		}
		if (!isCache()) {
			loadProcesses(progress, workflow, workflow.allImportedProcessNodes());
			loadProcesses(progress, workflow, workflow.allLocalProcessNodes());
			for (Enumeration<FlexoProcessNode> e = workflow.allLocalProcessNodes(); e.hasMoreElements();) {
				FlexoProcessNode nextProcessNode = e.nextElement();
				FlexoProcess next = nextProcessNode.getProcess();
				if (next != null) {
					if (next.getProcessDMEntity() != null) {
						next.getProcessDMEntity().updateParentProcessPropertyIfRequired();
					}
					next.finalizeProcessBindings();
					next.resolveObjectReferences();
					next.clearIsModified(true);
					next.setLastUpdate(null);// resets last update date.
				} else {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("No FlexoProcess linked with processsNode : '" + nextProcessNode.getName()
								+ "' !!! Could not load process, removing process node !");
					}
					nextProcessNode.delete();
				}
			}
		}
		return workflow;
	}

	public boolean isCache() {
		return getProjectURI() != null && !getProjectURI().equals(getProject().getURI());
	}

	@Override
	protected void saveResourceData(boolean clearIsModified)
			throws org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException, SaveResourcePermissionDeniedException {
		super.saveResourceData(clearIsModified && getResourceData().getProject() == getProject());
	}

	/**
	 * @param progress
	 * @param workflow
	 */
	private void loadProcesses(FlexoProgress progress, FlexoWorkflow workflow, Enumeration<FlexoProcessNode> en) {
		for (; en.hasMoreElements();) {
			FlexoProcessNode next = en.nextElement();
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("loading_process") + " " + next.getName());
			}
			if (project.getFlexoProcessResource(next.getProcessResourceName()) != null) {
				FlexoProcess process = project.getFlexoProcessResource(next.getProcessResourceName()).getResourceData();
				next.setProcess(process);
			}
		}
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This method implements conversion from v0.1 to v1.0
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was successful
	 */
	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion vers1, FlexoVersion vers2) {
		if (vers1.equals(new FlexoVersion("0.1")) && vers2.equals(new FlexoVersion("1.0"))) {
			try {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Start converting from 0.1 to 1.0");
				}
				Document _document = null;
				Element root_Element = null;
				_document = XMLUtils.getJDOMDocument(getResourceFile().getFile());
				root_Element = _document.getRootElement();
				Element newWorkflowElement = new Element("flexoworkflow");
				newWorkflowElement.setAttribute("workflowName", getName());
				newWorkflowElement.setContent(root_Element.clone());
				_document.setRootElement(newWorkflowElement);
				FileWritingLock lock = willWriteOnDisk();
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Conversion from 0.1 to 1.0 SUCCESSFULL. Save the file.");
				}
				boolean returned = XMLUtils.saveXMLFile(_document, getResourceFile().getFile());
				hasWrittenOnDisk(lock);
				return returned;
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
			return false;
		} else if (vers2.equals("2.1")) {
			FlexoWorkflow wkf = getResourceData();
			wkf.createDefaultProcessMetricsDefinitions();
			wkf.createDefaultActivityMetricsDefinitions();
			wkf.createDefaultOperationMetricsDefinitions();
			wkf.createDefaultEdgeMetricsDefinitions();
			try {
				getResourceData().save();
				getProject().save();
			} catch (SaveResourceException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return super.convertResourceFileFromVersionToVersion(vers1, vers2);
		}
	}

	@Override
	public Object instanciateNewBuilder() {
		FlexoWorkflowBuilder builder = new FlexoWorkflowBuilder(this);
		builder.workflow = _resourceData;
		return builder;
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

	public String getProjectURI() {
		return projectURI;
	}

	public void setProjectURI(String projectURI) {
		this.projectURI = projectURI;
	}

}
