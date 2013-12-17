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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.FlexoXMLSerializableObject;
import org.openflexo.foundation.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.XMLSerializationService;
import org.openflexo.foundation.xml.XMLUtils2;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents the resource related to the resource manager. This resource acts as the one associated to the project itself.
 * 
 * @author sguerin
 * 
 */
public class FlexoRMResource extends FlexoXMLStorageResource<FlexoProject> {

	protected static final Logger logger = Logger.getLogger(FlexoRMResource.class.getPackage().getName());

	private static final String RM_TAG_REGEXP = "<RMResource[^/>]*?version\\s*=\\s*\"([0-9\\.]*)\"[^/>]*?>.*?</RMResource>";

	private static final Pattern RM_TAG_PATTERN = Pattern.compile(RM_TAG_REGEXP, Pattern.DOTALL);

	private static final int VERSION_GROUP = 1;

	private File rmFile;

	private File projectDirectory;

	private boolean requireDependenciesRebuild = false;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoRMResource(FlexoProjectBuilder builder) {
		super(builder);
		builder.notifyResourceLoading(this);
	}

	public FlexoRMResource(FlexoProject aProject, FlexoServiceManager serviceManager) {
		super(aProject, serviceManager);
	}

	public FlexoRMResource(File rmFile, File projectDirectory, FlexoServiceManager serviceManager) {
		super((FlexoProject) null, serviceManager);
		this.rmFile = rmFile;
		this.projectDirectory = projectDirectory;
	}

	/**
	 * Constructor called when building RM file !!!
	 * 
	 * @param aProject
	 * @param rmFile
	 */
	public FlexoRMResource(FlexoProject aProject, FlexoProjectFile rmFile, FlexoServiceManager serviceManager) {
		super(aProject, serviceManager);
		try {
			setResourceFile(rmFile);
		} catch (InvalidFileNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_resourceData = project;
	}

	@Override
	public File getFile() {
		if (resourceFile == null) {
			return rmFile;
		} else {
			return super.getFile();
		}
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.RM;
	}

	@Override
	public String getName() {
		if (getProject() == null) {
			String returned = rmFile.getName();
			if (returned.endsWith(".rmxml")) {
				returned = returned.substring(0, returned.length() - 6);
			}
			return returned;
		} else {
			return getProject().getProjectName();
		}
	}

	@Override
	public Class<FlexoProject> getResourceDataClass() {
		return FlexoProject.class;
	}

	@Override
	public FlexoProject getResourceData() {
		if (project == null) {
			if (_resourceData != null) {
				project = _resourceData;
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("We should never get here.");
				}
				try {
					logger.warning("You should retrieve a ResourceCenter here !!!");
					project = loadProject(null, getLoadingHandler(), null);
				} catch (ProjectLoadingCancelledException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.log(Level.WARNING, "Project loading cancel exception.", e);
					}
					e.printStackTrace();
				}
			}
		}
		return project;
	}

	protected void init(FlexoProject aProject, File aProjectDirectory, FlexoProjectFile aResourceFile) throws InvalidFileNameException {
		try {
			aProject.removeResourceWithKey(getResourceIdentifier());
			aProject.setFlexoResource(this);
			aProject.registerResource(this);
			for (FlexoResource<? extends FlexoResourceData> res : aProject) {
				res.getDependentResources().update();
				res.getAlteredResources().update();
				res.getSynchronizedResources().update();
			}
			if (!aProject.hasBackwardSynchronizationBeenPerformed()) {
				aProject.clearIsModified(true);
			}
		} catch (DuplicateResourceException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		}
		_resourceData = aProject;
		projectDirectory = aProjectDirectory;
		project.setProjectDirectory(projectDirectory, false);
		setResourceFile(aResourceFile);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Version of RM seems to be " + getXmlVersion());
		}
		if (getXmlVersion().isLesserThan(new FlexoVersion("2.0")) || aProject.rebuildDependanciesIsRequired()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("I will perform dependancies rebuilding");
			}

		}

		// Sets now the version to be the latest one
		setXmlVersion(latestVersion());
		/*try {
		    saveResourceData();
		} catch (SaveXMLResourceException e) {
		    e.printStackTrace();
		} catch (SaveResourcePermissionDeniedException e) {
		    e.printStackTrace();
		}*/
	}

	private FlexoProgress _loadProjectProgress;
	private ProjectLoadingHandler _loadingHandler;

	private XMLSerializationService xmlMappings;

	private boolean isInitializingProject = false;

	private FlexoServiceManager serviceManager;

	private FlexoProjectReferenceLoader projectReferenceLoader;

	public boolean isInitializingProject() {
		return isInitializingProject;
	}

	public FlexoProject loadProject(FlexoProgress progress, ProjectLoadingHandler loadingHandler, FlexoServiceManager serviceManager)
			throws RuntimeException, ProjectLoadingCancelledException {
		this.serviceManager = serviceManager;
		FlexoRMResource rmRes = null;
		try {
			isInitializingProject = true;
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading project...");
			}
			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("loading_project"));
				_loadProjectProgress = progress;
			}
			try {
				findAndSetRMVersion();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				_loadingHandler = loadingHandler;
				projectDirectory = getFile().getParentFile();
				performLoadWithPreviousVersion = false;
				project = performLoadResourceData(progress, loadingHandler);
				project.setXmlMappings(getXmlMappings());
				xmlMappings = null;
			} catch (FlexoFileNotFoundException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("File " + getFile().getName() + " NOT found");
				}
				e.printStackTrace();
				_loadProjectProgress = null;
				return null;
			}

			//
			// !!!!!!!!!!!!!! BE CAREFUL BIG TRICK HERE !!!!!!!!!!!!!!!!
			// We have here initialized a new RM resource in order to
			// instanciate the project
			// But deserializing the project itself causes a new instance of RM
			// Resource to
			// be instancied ! So.... we need here to forget the initial
			// instance and to
			// register the one which comes from deserialization.
			//
			rmRes = (FlexoRMResource) project.resourceForKey(ResourceType.RM, project.getProjectName());
			rmRes.isInitializingProject = true;
			if (rmRes.resourceFile == null) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Resource :" + rmRes.getFullyQualifiedName() + " has no file !!!!");
				}
			}
			// rmRes is the good resource. 'this' needs to be forgotten
			try {
				rmRes.init(project, project.getProjectDirectory(), rmRes.resourceFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("The name of this project is invalid: " + e.getMessage());
				}
			}

			// projectDirectory = getFile().getParentFile();
			// project.setProjectDirectory(projectDirectory);
			setResourceFile(new FlexoProjectFile(getFile(), project));
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Getting:\n" + getResourceXMLRepresentation());
			}

			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("loading_time_stamps"));
			}
			FlexoProject tempProject = loadTSFile();
			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("updating_time_stamps"));
			}
			updateTS(tempProject, project);
			// Remove all storage resources with non-existant files
			List<FlexoStorageResource<? extends StorageResourceData>> resourcesToRemove = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
			for (FlexoStorageResource<? extends StorageResourceData> resource : project.getStorageResources()) {
				if (resource.getFile() == null || !resource.getFile().exists()
				// Petite bidouille en attendant une meilleure gestion de ce truc
						&& !resource.getResourceIdentifier().equals("POPUP_COMPONENT.WDLDateAssistant")) {
					resource.recoverFile();// Attempt to fix problems
					if (resource.getFile() == null || !resource.getFile().exists()) {
						resourcesToRemove.add(resource);
					}
				}
			}
			for (FlexoStorageResource<? extends StorageResourceData> resource : resourcesToRemove) {
				logger.warning("Delete resource " + resource + " which has a non-existent file");
				resource.delete();
			}

			loadingHandler.loadAndConvertAllOldResourcesToLatestVersion(project, progress);

			// Load the data model
			/*if (!project.getFlexoDMResource().isLoaded()) {
				project.getFlexoDMResource().loadResourceData(progress, loadingHandler);
			}*/
			// Load the DKV
			/*if (!project.getFlexoDKVResource().isLoaded()) {
				project.getFlexoDKVResource().loadResourceData(progress, loadingHandler);
			}*/
			// Load the component library
			/*
			if (!project.getFlexoComponentLibraryResource().isLoaded()) {
				project.getFlexoComponentLibraryResource().loadResourceData(progress, loadingHandler);
			}*/
			// Load the workflow
			/*if (!project.getFlexoWorkflowResource().isLoaded()) {
				project.getFlexoWorkflowResource().loadResourceData(progress, loadingHandler);
			}*/
			// Load the navigation menu
			/*if (!project.getFlexoNavigationMenuResource().isLoaded()) {
				project.getFlexoNavigationMenuResource().loadResourceData(progress, loadingHandler);
			}*/
			// Load the TOC's, it is loaded at the end so that it can resolve a maximum of model object reference.
			/*if (!project.getTOCResource().isLoaded()) {
				project.getTOCResource().loadResourceData(progress, loadingHandler);
			}*/

			// After loading the resources, we clear the isModified flag on RMResource (since basically we haven't changed anything yet)
			if (!project.hasBackwardSynchronizationBeenPerformed()) {
				project.clearIsModified(false);
			}

			// Look-up observed object for screenshot resources
			// (pas terrible comme technique, mais on verra plus tard)
			/*for (ScreenshotResource resource : project.getResourcesOfClass(ScreenshotResource.class)) {
				if (resource.getSourceReference() == null) {
					resource.delete();
				}
			}*/
			// Project data contains information about imported projects, so let's load directly.
			getProject().getProjectData();

			if (requireDependenciesRebuild) {
				getProject().rebuildDependencies();
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Dependencies rebuilding has been performed. Save RM file.");
				}
			}
			try {
				if (project.isModified()) {
					project.getFlexoRMResource().saveResourceData();
					// Et surtout pas saveResourceData() car cette resource est a oublier, ne l'oublions pas ;-)
				}
			} catch (SaveXMLResourceException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (SaveResourcePermissionDeniedException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading project... DONE.");
			}
			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("loading_ui"));
			}
			_loadProjectProgress = null;
			return project;
		} catch (LoadXMLResourceException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not load project: " + getFile().getAbsolutePath() + " : exception raised: " + e.getClass().getName()
						+ ". See console for details.");
				logger.severe(e.getStackTrace()[0].toString());
			}
			if (_loadingHandler != null) {
				_loadingHandler.notifySevereLoadingFailure(this, e);
			}
			// Exit application
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Exiting application...");
			}
			_loadProjectProgress = null;
			throw new RuntimeException(e.getMessage());
		} catch (XMLOperationException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
			// Exit application
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Exiting application...");
			}
			_loadProjectProgress = null;
			throw new RuntimeException(e.getMessage());
		} catch (FlexoException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
			// Exit application
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Exiting application...");
			}
			_loadProjectProgress = null;
			throw new RuntimeException(e.getMessage());
		} finally {
			if (rmRes != null) {
				rmRes.isInitializingProject = false;
			}
			isInitializingProject = false;
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void findAndSetRMVersion() throws IOException {
		try {
			String s = FileUtils.fileContents(getFile());
			String version = null;
			Matcher m = RM_TAG_PATTERN.matcher(s);
			if (m.find()) {
				version = m.group(VERSION_GROUP);
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("String that matched:\n" + m.group());
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("RM version is " + version);
				}
				setXmlVersion(new FlexoVersion(version));
			} else if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find the RMResource version in file: " + getFile().getAbsolutePath());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Overrides convertResourceFileFromVersionToVersion
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#convertResourceFileFromVersionToVersion(org.openflexo.foundation.xml.XMLSerializationService.Version,
	 *      org.openflexo.foundation.xml.XMLSerializationService.Version)
	 */
	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Trying conversion from " + v1 + " to " + v2);
		}
		if (v1.isLesserThan(new FlexoVersion("2.0"))) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Will rebuild dependancies later");
			}
			requireDependenciesRebuild = true;
			return true;
		}
		if (v1.equals(new FlexoVersion("3.1")) && v2.equals(new FlexoVersion("3.2"))) {
			return convertFrom31To32();
		} else if (v1.equals(new FlexoVersion("3.4")) && v2.equals(new FlexoVersion("3.5"))) {
			return convertFrom34To35();
		} else if (v1.equals(new FlexoVersion("3.5")) && v2.equals(new FlexoVersion("4.0"))) {
			return convertFrom35to40();
		} else if (v2.equals(new FlexoVersion("5.0"))) {
			return convertProjectToNewPackages();
		} else if (v1.equals(new FlexoVersion("5.0")) && v2.equals(new FlexoVersion("6.0"))) {
			return convertFrom50To60();
		} else {
			return super.convertResourceFileFromVersionToVersion(v1, v2);
		}
	}

	private boolean convertProjectToNewPackages() {
		NewPackageConverter converter = new NewPackageConverter(getFile().getParentFile());
		return converter.convert();
	}

	private boolean convertFrom35to40() {
		File keyValueAssistant = new File(getFile().getParentFile(), "Popups/WDLKeyValueAssistant.woxml");
		if (keyValueAssistant.exists()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Removing key value assistant. At next load of project, the resource should automatically remove itself");
			}
			keyValueAssistant.delete();
		}
		_resourceData.initJavaFormatter();
		return _resourceData.getCustomWidgetPalette().convertTopSequenceToWidgetSequence();
	}

	private boolean convertFrom34To35() {
		try {
			Document document = XMLUtils2.getJDOMDocument(getFile());
			Iterator<Element> tableElementIterator = document.getDescendants(new ElementFilter("TextFileResource"));
			while (tableElementIterator.hasNext()) {
				Element el = tableElementIterator.next();
				if (el.getAttribute("genericTypingClassName") != null && el.getAttribute("genericTypingClassName").getValue() != null
						&& el.getAttribute("genericTypingClassName").getValue().equals("org.openflexo.generator.rm.PListFileResource")) {
					el.setAttribute("genericTypingClassName", "org.openflexo.generator.rm.EOEntityPListFileResource");
				}
			}
			tableElementIterator = document.getDescendants(new ElementFilter("RMResource"));
			while (tableElementIterator.hasNext()) {
				tableElementIterator.next().setAttribute("version", "3.5.0");
			}
			// saveResourceDataWithVersion(new Version("3.5.0"));

			FileWritingLock lock = willWriteOnDisk();
			boolean returned = XMLUtils2.saveXMLFile(document, getFile());
			hasWrittenOnDisk(lock);
			return returned;

		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return
	 */
	private boolean convertFrom31To32() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Starting conversion of screenshot resources name.");
		}
		for (ScreenshotResource resource : _resourceData.getResourcesOfClass(ScreenshotResource.class)) {
			resource.getName();
		}
		try {
			this.saveResourceDataWithVersion(new FlexoVersion("3.2.0"));
		} catch (SaveXMLResourceException e) {
			e.printStackTrace();
			return false;
		} catch (SaveResourcePermissionDeniedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	private boolean convertFrom50To60() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Starting conversion from version 5.0 to 6.0 of RMXML.");
		}
		convertFrom50To60(getFile());
		convertFrom50To60(getTSFile());
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Finished successfully conversion from version 5.0 to 6.0 of RMXML.");
		}
		return true;
	}

	private boolean convertFrom50To60(File file) {
		SAXBuilder parser = new SAXBuilder();
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(file);
			Document document = parser.build(in);
			in.close();
			removeElementsWithName(document, "ProjectOntology");
			removeElementsWithName(document, "ShemaLibrary");
			removeElementsWithName(document, "View");
			Format prettyFormat = Format.getPrettyFormat();
			prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
			XMLOutputter outputter = new XMLOutputter(prettyFormat);
			out = new FileOutputStream(file);
			outputter.output(document, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (JDOMException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		return true;
	}

	private void removeElementsWithName(Document document, String name) {
		IteratorIterable<Element> children = document.getDescendants(new ElementFilter(name));
		while (children.hasNext()) {
			children.next();
			children.remove();
		}

	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that project deserializing
	 * requires a FlexoProjectBuilder instance
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Returns the required newly instancied FlexoProjectBuilder
	 * 
	 * @return boolean
	 */
	@Override
	public FlexoProjectBuilder instanciateNewBuilder() {
		FlexoProjectBuilder returned = new FlexoProjectBuilder(serviceManager);
		returned.loadingHandler = _loadingHandler;
		returned.projectDirectory = projectDirectory;
		returned.progress = _loadProjectProgress;
		returned.serviceManager = serviceManager;
		returned.setProjectReferenceLoader(projectReferenceLoader);
		return returned;
	}

	/**
	 * Overrides saveResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#saveResourceData()
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveXMLResourceException, SaveResourcePermissionDeniedException {
		StringEncoder encoder = getProject() != null ? getProject().getStringEncoder() : StringEncoder.getDefaultInstance();
		String s = encoder._getDateFormat();
		String s1 = StringEncoder.getDefaultInstance()._getDateFormat();
		try {
			StringEncoder.getDefaultInstance()._setDateFormat("HH:mm:ss dd/MM/yyyy SSS");
			encoder._setDateFormat("HH:mm:ss dd/MM/yyyy SSS");
			if (!isInitializingProject && getProject() != null) {
				getProject().checkResourceIntegrity();
			}
			super.saveResourceData(clearIsModified);
			if (getProject() != null) {
				getProject().writeDotVersion();
			}
			saveTSFile();
			if (!isInitializingProject && getProject() != null) {
				getProject().deleteFilesToBeDeleted();
			}
		} catch (SaveXMLResourceException e) {
			throw e;
		} catch (SaveResourcePermissionDeniedException e) {
			throw e;
		} finally {
			if (s != null) {
				encoder._setDateFormat(s);
			}
			if (s1 != null) {
				StringEncoder.getDefaultInstance()._setDateFormat(s1);
			}
		}
	}

	public void saveTimeStampFile() {

		String s = StringEncoder.getDateFormat();
		try {
			StringEncoder.setDateFormat("HH:mm:ss dd/MM/yyyy SSS");
			saveTSFile();
			getProject().setTimestampsHaveBeenLoaded(true);
		} catch (SaveXMLResourceException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				StringEncoder.setDateFormat(s);
			}
		}
	}

	private void saveTSFile() throws SaveXMLResourceException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("SAVE RM/TS file " + getTSFile().getAbsolutePath());
		}
		FileOutputStream out = null;
		File temporaryFile = null;

		try {
			File dir = getFile().getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// Using temporary file
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Creating temp file " + temporaryFile.getAbsolutePath());
			}
			out = new FileOutputStream(temporaryFile);
			FlexoXMLSerializableObject dataToSerialize = getResourceData();
			dataToSerialize.initializeSerialization();
			XMLCoder.encodeObjectWithMapping(dataToSerialize, XMLSerializationService.getRMTSMapping(), out);
			dataToSerialize.finalizeSerialization();
			out.flush();
			out.close();
			// Renaming temporary file
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Renaming temp file " + temporaryFile.getAbsolutePath() + " to " + getFile().getAbsolutePath());
			}
			// temporaryFile.renameTo(getFile());
			FileUtils.rename(temporaryFile, getTSFile());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Succeeding to save RM/TS file");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				e.printStackTrace();
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) { // Ignore
			}
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to save resource RM/TS file");
			}
			throw new SaveXMLResourceException(this, e, null);
		}
	}

	public File getTSFile() {
		return new File(getFile().getParentFile(), getFile().getName() + ".ts");
	}

	private FlexoProject loadTSFile() throws LoadXMLResourceException {
		if (!getTSFile().exists()) {
			return null;
		}
		FlexoProject tempProject = null;
		try {
			XMLMapping mapping = XMLSerializationService.getRMTSMapping();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Start loading RM/TS file " + getTSFile().getAbsolutePath());
			}
			tempProject = (FlexoProject) XMLDecoder.decodeObjectWithMapping(new FileInputStream(getTSFile()), mapping,
					instanciateNewBuilder(), StringEncoder.getDefaultInstance());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Stop loading RM/TS file");
			}
			return tempProject;
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("FAILED loading RM/TS file, Exception: " + e.getTargetException().getMessage());
			}
			e.getTargetException().printStackTrace();
			e.printStackTrace();
			throw new LoadXMLResourceException((FlexoXMLStorageResource) this, e.getTargetException().getMessage());
		} catch (Exception e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("FAILED loading RM/TS file, Exception: " + e.getMessage());
			}
			e.printStackTrace();
			throw new LoadXMLResourceException((FlexoXMLStorageResource) this, e.getMessage());
		}
	}

	private void updateTS(FlexoProject tempProject, FlexoProject currentProject) {
		currentProject.setTimestampsHaveBeenLoaded(true);
		if (tempProject == null) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateTS ");
		}
		for (FlexoResource<? extends FlexoResourceData> tempResource : new ArrayList<FlexoResource<? extends FlexoResourceData>>(
				tempProject.getResources().values())) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateTSForResource" + tempResource);
			}
			FlexoResource<? extends FlexoResourceData> resource = currentProject.resourceForKey(tempResource.getResourceIdentifier());
			if (resource != null) {
				updateTSForResource(resource, currentProject, tempResource);
			}
		}
	}

	private void updateTSForResource(FlexoResource<? extends FlexoResourceData> resource, FlexoProject currentProject,
			FlexoResource<? extends FlexoResourceData> tempResource) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateTSForResource" + resource + " entries=" + tempResource.getLastSynchronizedForResources().size());
		}
		for (LastSynchronizedWithResourceEntry entry : tempResource.getLastSynchronizedForResources().values()) {
			FlexoResource<? extends FlexoResourceData> tempOriginResource = entry.getOriginResource();
			FlexoResource<? extends FlexoResourceData> tempBSResource = entry.getResource();
			FlexoResource<? extends FlexoResourceData> originResource = currentProject.resourceForKey(tempOriginResource
					.getResourceIdentifier());
			FlexoResource<? extends FlexoResourceData> bsResource = currentProject.resourceForKey(tempBSResource.getResourceIdentifier());
			if (bsResource != null) {
				LastSynchronizedWithResourceEntry newEntry = new LastSynchronizedWithResourceEntry(originResource, bsResource,
						entry.getDate());
				resource.setLastSynchronizedForResourcesForKey(newEntry, bsResource);
			}
		}
		// Dont forget to set lastWrittenOnDisk !!!!!
		if (resource instanceof FlexoFileResource && tempResource instanceof FlexoFileResource) {
			((FlexoFileResource<? extends FlexoResourceData>) resource)
					._setLastWrittenOnDisk(((FlexoFileResource<? extends FlexoResourceData>) tempResource)._getLastWrittenOnDisk());
		}
		// And lastKnownMemoryUpdate for Storage resource
		if (resource instanceof FlexoStorageResource && tempResource instanceof FlexoStorageResource) {
			((FlexoStorageResource<? extends FlexoResourceData>) resource)
					.setLastKnownMemoryUpdate(((FlexoStorageResource<? extends FlexoResourceData>) tempResource).getLastKnownMemoryUpdate());
		}
	}

	/**
	 * Overrides saveResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#saveResourceData(org.openflexo.foundation.xml.XMLSerializationService.Version)
	 */
	/*@Override
	protected void saveResourceData(FlexoVersion version) throws SaveXMLResourceException
	{
	    super.saveResourceData(version);
	}*/

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

	public void setProjectReferenceLoader(FlexoProjectReferenceLoader projectReferenceLoader) {
		this.projectReferenceLoader = projectReferenceLoader;
	}

}
