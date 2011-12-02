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
package org.openflexo.foundation.ie.cl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.IEMonitoringScreen;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoMonitoringScreenResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.toolbox.FileUtils;

public class MonitoringScreenDefinition extends ComponentDefinition implements Serializable {
	private static final Logger logger = Logger.getLogger(MonitoringScreenDefinition.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 * 
	 * @throws DuplicateResourceException
	 */
	private FlexoProcess _process;
	private FlexoModelObjectReference<FlexoProcess> processReference;

	public MonitoringScreenDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException {
		this(null, builder.componentLibrary, null, builder.getProject(), null);
		initializeDeserialization(builder);
	}

	public MonitoringScreenDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject project, FlexoProcess p) throws DuplicateResourceException {
		super(aComponentName, componentLibrary, aFolder, project);
		_process = p;
	}

	public FlexoModelObjectReference<FlexoProcess> getProcessReference() {
		if (getProcess() != null) {
			return processReference = new FlexoModelObjectReference<FlexoProcess>(getProject(), getProcess());
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("MonitoringScreen has no process!");
		}
		return processReference;
	}

	public void setProcessReference(FlexoModelObjectReference<FlexoProcess> ref) {
		this.processReference = ref;
	}

	public FlexoProcess getProcess() {
		if (_process == null && processReference != null) {
			_process = processReference.getObject(true);
			if (_process != null) {
				_process.addObserver(this);
			}
		}
		return _process;
	}

	public void setProcess(FlexoProcess process) {
		_process = process;
		processReference = null;
	}

	@Override
	public IEWOComponent createNewComponent() {
		return new IEMonitoringScreen(this, getProject());
	}

	@Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists) {
		if (getProject() != null) {
			FlexoComponentResource returned = getProject().getFlexoMonitoringScreenResource(getName());
			if (returned == null && createIfNotExists) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Creating new monitoring screen resource !");
				}
				// FlexoProcessResource processRes =
				// getProject().getFlexoProcessResource(getProcess().getName());
				File componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(),
						this), _componentName + ".woxml");
				FlexoProjectFile resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
				FlexoMonitoringScreenResource compRes = null;
				try {
					compRes = new FlexoMonitoringScreenResource(getProject(), _componentName, getProject()
							.getFlexoComponentLibraryResource(), resourceComponentFile);
				} catch (InvalidFileNameException e1) {
					boolean ok = false;
					for (int i = 0; i < 100 && !ok; i++) {
						try {
							componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject()
									.getProjectDirectory(), this), FileUtils.getValidFileName(_componentName) + i + ".woxml");
							resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
							compRes = new FlexoMonitoringScreenResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
							ok = true;
						} catch (InvalidFileNameException e) {

						}
					}
					if (!ok) {
						componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(
								getProject().getProjectDirectory(), this), FileUtils.getValidFileName(_componentName) + getFlexoID()
								+ ".woxml");
						resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
						try {
							compRes = new FlexoMonitoringScreenResource(getProject(), _componentName, getProject()
									.getFlexoComponentLibraryResource(), resourceComponentFile);
						} catch (InvalidFileNameException e) {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("This should really not happen.");
							}
							return null;
						}
					}
				}
				if (compRes == null) {
					return null;
				}
				compRes.setResourceData(new IEMonitoringScreen(this, getProject()));
				try {
					compRes.getResourceData().setFlexoResource(compRes);
					getProject().registerResource(compRes);
				} catch (DuplicateResourceException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
					return null;
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Registered component " + _componentName + " file: " + componentFile);
				}
				returned = compRes;
			}
			return returned;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("getProject()==null for a ScreenComponentDefinition !");
			}
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		return "ScreenComponentDefinition.inspector";
	}

	@Override
	public IEMonitoringScreen getWOComponent() {
		return (IEMonitoringScreen) super.getWOComponent();
	}

	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	@Override
	public void update(FlexoObservable o, DataModification arg) {
		if (o == _process && arg instanceof ObjectDeleted) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Monitoring screen '" + getName() + "' received delete notification from process " + o);
			}
			delete();
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "screen_component_definition";
	}

	@Override
	public List<OperationNode> getAllOperationNodesLinkedToThisComponent() {
		return new ArrayList<OperationNode>();
	}

}
