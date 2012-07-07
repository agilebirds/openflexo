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
package org.openflexo.foundation.dm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMType.DMTypeStringConverter;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.action.CreateProjectRepository;
import org.openflexo.foundation.dm.action.ImportExternalDatabaseRepository;
import org.openflexo.foundation.dm.action.ImportJARFileRepository;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.foundation.dm.action.UpdateLoadableDMEntity;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DiagramCreated;
import org.openflexo.foundation.dm.dm.DiagramDeleted;
import org.openflexo.foundation.dm.dm.RepositoryRegistered;
import org.openflexo.foundation.dm.dm.RepositoryUnregistered;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEOProperty;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOEntityCodeGenerator;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.dm.eo.model.EOEntity;
import org.openflexo.foundation.dm.eo.model.EOModel;
import org.openflexo.foundation.dm.eo.model.EOModelGroup;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoDMResource;
import org.openflexo.foundation.rm.FlexoJarResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.stats.DMModelStatistics;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents the whole data model for the project
 * 
 * @author sguerin
 * 
 */
public class DMModel extends DMObject implements XMLStorageResourceData {

	static final Logger logger = Logger.getLogger(DMModel.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private JDKRepository jdkRepository;

	private ComponentRepository componentRepository;

	private ProcessInstanceRepository processInstanceRepository;

	private ProcessBusinessDataRepository processBusinessDataRepository;

	private WORepository woRepository;

	private EOPrototypeRepository eoPrototypeRepository;

	private FlexoExecutionModelRepository executionModelRepository;

	private Vector<ProjectRepository> projectRepositories;

	private Vector<ProjectDatabaseRepository> projectDatabaseRepositories;

	private Vector<ExternalRepository> externalRepositories;

	private Vector<DenaliFoundationRepository> denaliFoundationRepositories;

	private Vector<ExternalDatabaseRepository> externalDatabaseRepositories;

	private Vector<ThesaurusRepository> thesaurusRepositories;

	private Vector<ThesaurusDatabaseRepository> thesaurusDatabaseRepositories;

	private Vector<RationalRoseRepository> rationalRoseRepositories;

	private Vector<WSDLRepository> wsdlRepositories;

	private Vector<XMLSchemaRepository> xmlSchemaRepositories;

	private Hashtable<String, DMEntity> entities;

	private Hashtable<String, DMRepository> repositories;

	private transient FlexoProject _project;

	private transient FlexoDMResource _resource;

	private transient EOModelGroup _modelGroup = null;

	private InternalRepositoryFolder _internalRepositoryFolder;

	private PersistantDataRepositoryFolder _persistantDataRepositoryFolder;

	private NonPersistantDataRepositoryFolder _nonPersistantDataRepositoryFolder;

	private LibraryRepositoryFolder _libraryRepositoryFolder;

	private String _globalDefaultConnectionString;

	private String _globalDefaultUsername;

	private String _globalDefaultPassword;

	private DMModelStatistics statistics;

	private DMTypeStringConverter dmTypeConverter;

	private CachedEntitiesForTypes cachedEntitiesForTypes;

	private Hashtable<DMType, Vector<DMTranstyper>> _declaredTranstypers;

	private Vector<ERDiagram> diagrams;

	static {
		// Register JavaParser if found in classpath (otherwise abort)
		installJavaParser();
	}

	private static void installJavaParser() {
		Class<?> javaParserClass;
		try {
			javaParserClass = Class.forName("org.openflexo.javaparser.JavaParser");
			Method initMethod = javaParserClass.getMethod("init", new Class[0]);
			initMethod.invoke(javaParserClass, new Object[0]);
			logger.info("JavaParser sucessfully initialized.");
		} catch (ClassNotFoundException e) {
			logger.warning("JavaParser not found.");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void installEOGenerators() {
		Class<?> eoEntityGeneratorClass;
		try {
			eoEntityGeneratorClass = Class.forName("org.openflexo.generator.dm.DefaultEOEntityGenerator");
			Method initMethod = eoEntityGeneratorClass.getMethod("init", new Class[] { DMModel.class });
			initMethod.invoke(eoEntityGeneratorClass, new Object[] { this });
			logger.info("DefaultEOEntityGenerator sucessfully initialized.");
		} catch (ClassNotFoundException e) {
			logger.warning("DefaultEOEntityGenerator not found.");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public DMModel(FlexoDMBuilder builder) {
		this(builder.getProject());
		builder.dmModel = this;
		_resource = builder.resource;
		initializeDeserialization(builder);
	}

	/**
	 * Create a new DataModel.
	 */
	public DMModel(FlexoProject project) {
		super(project);
		dmTypeConverter = new DMTypeStringConverter(this);
		project.getStringEncoder()._addConverter(dmTypeConverter);
		cachedEntitiesForTypes = new CachedEntitiesForTypes();
		_project = project;
		_dmModel = this;
		entities = new Hashtable<String, DMEntity>();
		repositories = new Hashtable<String, DMRepository>();
		projectRepositories = new Vector<ProjectRepository>();
		projectDatabaseRepositories = new Vector<ProjectDatabaseRepository>();
		externalRepositories = new Vector<ExternalRepository>();
		denaliFoundationRepositories = new Vector<DenaliFoundationRepository>();
		externalDatabaseRepositories = new Vector<ExternalDatabaseRepository>();
		thesaurusRepositories = new Vector<ThesaurusRepository>();
		thesaurusDatabaseRepositories = new Vector<ThesaurusDatabaseRepository>();
		rationalRoseRepositories = new Vector<RationalRoseRepository>();
		wsdlRepositories = new Vector<WSDLRepository>();
		xmlSchemaRepositories = new Vector<XMLSchemaRepository>();
		_modelGroup = new EOModelGroup();
		// GPO: The next line can be ignored I think, but I leave it for now
		EOModelGroup.setDefaultGroup(_modelGroup);
		_internalRepositoryFolder = new InternalRepositoryFolder(this);
		_persistantDataRepositoryFolder = new PersistantDataRepositoryFolder(this);
		_nonPersistantDataRepositoryFolder = new NonPersistantDataRepositoryFolder(this);
		_libraryRepositoryFolder = new LibraryRepositoryFolder(this);
		_declaredTranstypers = new Hashtable<DMType, Vector<DMTranstyper>>();
		diagrams = new Vector<ERDiagram>();
		installEOGenerators();
	}

	/**
	 * Creates and returns a newly created data model with resource management (creates the resource)
	 * 
	 * @return a newly created DMModel
	 */
	public static DMModel createNewDMModel(FlexoProject project) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewDMModel(), project=" + project);
		}
		DMModel newDMModel = new DMModel(project);
		project.setBuildingDataModel(newDMModel);

		File dmFile = ProjectRestructuration.getExpectedDataModelFile(project, project.getProjectName());
		FlexoProjectFile dataModelFile = new FlexoProjectFile(dmFile, project);
		FlexoDMResource dmRes;
		try {
			dmRes = new FlexoDMResource(project, newDMModel, dataModelFile);
		} catch (InvalidFileNameException e) {
			dataModelFile = new FlexoProjectFile(ProjectRestructuration.DATA_MODEL_DIR + "/DataModel.dm");
			dataModelFile.setProject(project);
			try {
				dmRes = new FlexoDMResource(project, newDMModel, dataModelFile);
			} catch (InvalidFileNameException e1) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("This should not happen");
				}
				return null;
			}
		}
		// newDMModel.initializeDefaultRepositories(dmRes);
		try {
			// dmRes.saveResourceData();
			project.registerResource(dmRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		newDMModel.initializeDefaultRepositories(dmRes);

		try {
			dmRes.saveResourceData();
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newDMModel;
	}

	/**
	 * Creates and returns a newly created data model with a provided resource
	 * 
	 * @return a newly created DMModel
	 */
	public static DMModel createNewDMModel(FlexoProject project, FlexoDMResource dmRes) {
		DMModel newDMModel = new DMModel(project);
		newDMModel.setFlexoResource(dmRes);
		newDMModel.initializeDefaultRepositories(dmRes);
		return newDMModel;
	}

	private void initializeDefaultRepositories(FlexoDMResource dmRes) {
		jdkRepository = JDKRepository.createNewJDKRepository(this);
		loadInitializingModel();
		componentRepository = ComponentRepository.createNewComponentRepository(this);
		processInstanceRepository = ProcessInstanceRepository.createNewProcessInstanceRepository(this);
		woRepository = WORepository.createNewWORepository(this);
		eoPrototypeRepository = EOPrototypeRepository.createNewEOPrototypeRepository(this, dmRes);
		executionModelRepository = FlexoExecutionModelRepository.createNewExecutionModelRepository(this, dmRes);
		initializeProcessBusinessDataRepository();
	}

	public void initializeProcessBusinessDataRepository() {
		if (processBusinessDataRepository == null) {
			processBusinessDataRepository = ProcessBusinessDataRepository.createNewProcessBusinessDataRepository(this);
		}
	}

	@Override
	public void initializeDeserialization(Object builder) {
		super.initializeDeserialization(builder);
		getDmTypeConverter().dataModelStartDeserialization(this);
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Finalize deserialization for DMModel");
		}

		// Little hack allowing
		// temporary storing of deserialized data model
		// (avoid loop of DataModel loading whene trying to access project.getDataModel()
		// somewhere in contained objects deserialization
		if (builder instanceof FlexoDMBuilder) {
			setProject(((FlexoDMBuilder) builder).getProject());
			((FlexoDMBuilder) builder).getProject().getFlexoDMResource()._setDeserializingDataModel(this);
		}

		// Load required EOModels
		Enumeration en = getDMEORepositories().elements();
		while (en.hasMoreElements()) {
			DMEORepository dmEORepository = (DMEORepository) en.nextElement();
			for (Enumeration en2 = dmEORepository.getDMEOModels().elements(); en2.hasMoreElements();) {
				DMEOModel next = (DMEOModel) en2.nextElement();
				try {
					next.loadEOModel();
				} catch (InvalidEOModelFileException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning(e.getMessage());
					}
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("EOModel " + next.getName() + " contains " + next.getEntities().size() + " entities.");
				}
			}
		}

		// Load required EOEntities and their contents
		en = getDMEORepositories().elements();
		while (en.hasMoreElements()) {
			DMEORepository dmEORepository = (DMEORepository) en.nextElement();
			for (DMEntity next : dmEORepository.getEntities().values()) {
				if (next instanceof DMEOEntity) {
					DMEOEntity dmEOEntity = (DMEOEntity) next;
					dmEORepository.internallyRegisterDMEOEntity(dmEOEntity);
					dmEOEntity.finalizePropertiesRegistering();
				}
			}
		}

		// Performs EOModel synchronization
		en = getDMEORepositories().elements();
		while (en.hasMoreElements()) {
			DMEORepository dmEORepository = (DMEORepository) en.nextElement();
			for (Enumeration en2 = dmEORepository.getDMEOModels().elements(); en2.hasMoreElements();) {
				DMEOModel next = (DMEOModel) en2.nextElement();
				try {
					next.updateFromEOModel();
				} catch (EOAccessException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				}
			}
		}

		// Now, deserialize types
		getDmTypeConverter().dataModelFinishDeserialization(this);

		// Done. Call super
		super.finalizeDeserialization(builder);
	}

	public EOModelGroup getModelGroup() {
		return _modelGroup;
	}

	/**
	 * @param selectedDMPackage
	 * @return
	 */
	public String getNextDefautEntityName(DMPackage aPackage) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_entity_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (entities.get(aPackage + "." + testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	/**
	 * @param fullQualifiedName
	 * @return
	 */
	public String getNextDefautEntityName(DMPackage aPackage, String fullQualifiedName) {
		String baseName = fullQualifiedName;
		String testMe = baseName;
		int test = 0;
		while (entities.get(aPackage + "." + testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	/**
	 * @param anEntity
	 * @return
	 */
	public String getNextDefautMethodName(DMEntity anEntity) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_method_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (anEntity.getDeclaredMethodNamed(testMe).size() > 0) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	/**
	 * @param anEntity
	 * @return
	 */
	public String getNextDefautPropertyName(DMEntity anEntity) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_property_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (anEntity.getDMProperty(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	public String getNextDefautRelationshipName(DMEntity anEntity) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_relationship_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (anEntity.getDMProperty(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	public String getNextDefautAttributeName(DMEntity anEntity) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_attribute_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (anEntity.getDMProperty(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	/**
	 * @param anEntity
	 * @return
	 */
	public String getNextDefautTranstyperName(DMEntity anEntity) {
		String baseName = FlexoLocalization.localizedForKeyAndLanguage("default_new_transtyper_name", Language.ENGLISH);
		String testMe = baseName;
		int test = 0;
		while (anEntity.getDMTranstyper(testMe) != null) {
			test++;
			testMe = baseName + test;
		}
		return testMe;
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public FlexoDMResource getFlexoResource() {
		return _resource;
	}

	@Override
	public FlexoDMResource getFlexoXMLFileResource() {
		return _resource;
	}

	/**
	 * Overrides getXMLMapping
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#getXMLMapping()
	 */
	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getDMMapping();
	}

	@Override
	public String getFullyQualifiedName() {
		return getProject() != null ? getProject().getProjectName() + ".DMMODEL" : "DMMODEL";
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		_resource = (FlexoDMResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	/**
	 * Save this object using ResourceManager scheme
	 * 
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 * @see org.openflexo.foundation.rm.FlexoResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		_resource.saveResourceData();
	}

	/**
	 * Return String uniquely identifying inspector template which must be applied when trying to inspect this object
	 * 
	 * @return a String value
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_MODEL_INSPECTOR;
	}

	/**
	 * Return a Vector of embedded DMObjects at this level.
	 * 
	 * @return a Vector of embedded DMRepository instances
	 */
	@Override
	public Vector<DMRepository> getEmbeddedDMObjects() {
		Vector<DMRepository> returned = new Vector<DMRepository>();
		returned.addAll(getOrderedChildren());
		return returned;
	}

	// ==========================================================================
	// ======================== Common Instance methods
	// =========================
	// ==========================================================================

	@Override
	public String getName() {
		return "data_model";
	}

	@Override
	public void setName(String name) {
		// Not allowed
	}

	/**
	 * Overrides isNameValid
	 * 
	 * @see org.openflexo.foundation.dm.DMObject#isNameValid()
	 */
	@Override
	public boolean isNameValid() {
		return true;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(CreateProjectRepository.actionType);
		returned.add(CreateProjectDatabaseRepository.actionType);
		returned.add(ImportExternalDatabaseRepository.actionType);
		returned.add(ImportJARFileRepository.actionType);
		returned.add(ImportRationalRoseRepository.actionType);
		returned.add(UpdateLoadableDMEntity.actionType);
		return returned;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	public InternalRepositoryFolder getInternalRepositoryFolder() {
		return _internalRepositoryFolder;
	}

	public LibraryRepositoryFolder getLibraryRepositoryFolder() {
		return _libraryRepositoryFolder;
	}

	public NonPersistantDataRepositoryFolder getNonPersistantDataRepositoryFolder() {
		return _nonPersistantDataRepositoryFolder;
	}

	public PersistantDataRepositoryFolder getPersistantDataRepositoryFolder() {
		return _persistantDataRepositoryFolder;
	}

	// ==========================================================================
	// ====================== Common access to DMEntity
	// =========================
	// ==========================================================================

	public Hashtable<String, DMEntity> getEntities() {
		return entities;
	}

	public DMEntity getEntityNamed(String fullyQualifiedName) {
		return entities.get(fullyQualifiedName);
	}

	private boolean _packagesNeedsReordering = true;
	private Vector<DMPackage> _allOrderedPackages = new Vector<DMPackage>();

	public synchronized Vector<DMPackage> getAllOrderedPackages() {
		if (_packagesNeedsReordering) {
			_allOrderedPackages.clear();
			Hashtable<String, DMPackage> allPackages = new Hashtable<String, DMPackage>();
			for (DMEntity entity : entities.values()) {
				String packageName = entity.getPackage().getName();
				DMPackage p = allPackages.get(packageName);
				if (p == null) {
					p = new DMPackage(this, getJDKRepository(), packageName);
					allPackages.put(packageName, p);
				}
				p.registerEntity(entity);
			}
			_allOrderedPackages.addAll(allPackages.values());
			Collections.sort(_allOrderedPackages, DMRepository.packageComparator);
			_packagesNeedsReordering = false;
		}
		return _allOrderedPackages;
	}

	public synchronized boolean registerEntity(DMEntity entity) {
		if (entities.get(entity.getFullyQualifiedName()) == null) {
			entities.put(entity.getFullyQualifiedName(), entity);
			_packagesNeedsReordering = true;
			// Add this new entity to the class library
			getClassLibrary().add(entity.getFullQualifiedName());
			// This new entity register may resolve some unresolved types
			getClassLibrary().clearUnresolvedTypesForNewRegisteredEntity(entity);
			return true;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to register an already registered entity: " + entity.getFullyQualifiedName());
			}
			return false;
		}
	}

	public synchronized void unregisterEntity(DMEntity entity) {
		if (entities.get(entity.getFullyQualifiedName()) != null) {
			entities.remove(entity.getFullyQualifiedName());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to unregister a non registered entity: " + entity.getFullyQualifiedName());
			}
		}
	}

	public synchronized void unregisterEntity(String entityFullyQualifiedName) {
		if (entities.get(entityFullyQualifiedName) != null) {
			entities.remove(entityFullyQualifiedName);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to unregister a non registered entity: " + entityFullyQualifiedName);
			}
		}
	}

	// ==========================================================================
	// =================== Common access to DMRepository
	// ========================
	// ==========================================================================

	public Hashtable<String, DMRepository> getRepositories() {
		return repositories;
	}

	public Vector<DMEORepository> getDMEORepositories() {
		Vector<DMEORepository> returned = new Vector<DMEORepository>();
		for (Enumeration en = getRepositories().elements(); en.hasMoreElements();) {
			DMRepository repository = (DMRepository) en.nextElement();
			if (repository instanceof DMEORepository) {
				returned.add((DMEORepository) repository);
			}
		}
		return returned;
	}

	public DenaliFoundationRepository getDenaliFoundationRepository() {
		return (DenaliFoundationRepository) getRepositoryNamed(DenaliFoundationRepository.DENALI_FOUNDATION_REPOSITORY_NAME);
	}

	public DenaliFoundationRepository getDenaliFlexoRepository() {
		return (DenaliFoundationRepository) getRepositoryNamed(DenaliFoundationRepository.DENALI_FLEXO_REPOSITORY_NAME);
	}

	public DMRepository getRepositoryNamed(String aRepositoryName) {
		return repositories.get(getFullyQualifiedName() + "." + aRepositoryName);
	}

	public ExternalRepository getExternalRepository(FlexoJarResource resource) {
		for (Enumeration<ExternalRepository> en = externalRepositories.elements(); en.hasMoreElements();) {
			ExternalRepository next = en.nextElement();
			if (next.getJarResource() == resource) {
				return next;
			}
		}
		return null;
	}

	public void registerRepository(DMRepository repository) {
		if (repository.getName() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to register an unnamed directory !. Will do it anyway with name=NULL.");
			}
		}
		try {
			if (repositories.get(repository.getFullyQualifiedName()) == null) {
				repositories.put(repository.getFullyQualifiedName(), repository);
				needsReordering = true;
				updateRepositoryFolders();
				setChanged();
				notifyObservers(new RepositoryRegistered(repository));
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Try to register an already registered repository: " + repository.getFullyQualifiedName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unregisterRepository(DMRepository repository) {
		if (repositories.get(repository.getFullyQualifiedName()) != null) {
			repositories.remove(repository.getFullyQualifiedName());
			needsReordering = true;
			updateRepositoryFolders();
			setChanged();
			notifyObservers(new RepositoryUnregistered(repository));
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to unregister a non registered repository: " + repository.getFullyQualifiedName());
			}
		}
	}

	// ==========================================================================
	// ==================== Unique instance repositories
	// ========================
	// ==========================================================================

	public WORepository getWORepository() {
		return woRepository;
	}

	public void setWORepository(WORepository woRepository) {
		this.woRepository = woRepository;
		registerRepository(woRepository);
	}

	public ComponentRepository getComponentRepository() {
		return componentRepository;
	}

	public void setComponentRepository(ComponentRepository compRepository) {
		this.componentRepository = compRepository;
		registerRepository(componentRepository);
	}

	public ProcessInstanceRepository getProcessInstanceRepository() {
		return processInstanceRepository;
	}

	public void setProcessInstanceRepository(ProcessInstanceRepository piRepository) {
		this.processInstanceRepository = piRepository;
		registerRepository(piRepository);
	}

	public ProcessBusinessDataRepository getProcessBusinessDataRepository() {
		initializeProcessBusinessDataRepository();
		return processBusinessDataRepository;
	}

	public void setProcessBusinessDataRepository(ProcessBusinessDataRepository repository) {
		this.processBusinessDataRepository = repository;
		registerRepository(repository);
	}

	public JDKRepository getJDKRepository() {
		return jdkRepository;
	}

	public void setJDKRepository(JDKRepository jdkRep) {
		this.jdkRepository = jdkRep;
		registerRepository(jdkRep);
	}

	public EOPrototypeRepository getEOPrototypeRepository() {
		return eoPrototypeRepository;
	}

	public void setEOPrototypeRepository(EOPrototypeRepository eoPrototypeRepository) {
		this.eoPrototypeRepository = eoPrototypeRepository;
		registerRepository(eoPrototypeRepository);
	}

	public FlexoExecutionModelRepository getExecutionModelRepository() {
		return executionModelRepository;
	}

	public void setExecutionModelRepository(FlexoExecutionModelRepository executionRepository) {
		this.executionModelRepository = executionRepository;
		registerRepository(executionRepository);
	}

	// ==========================================================================
	// ================== Multiple instance repositories
	// ========================
	// ==========================================================================

	public Vector<DenaliFoundationRepository> getDenaliFoundationRepositories() {
		return denaliFoundationRepositories;
	}

	public void setDenaliFoundationRepositories(Vector<DenaliFoundationRepository> denaliFoundRepositories) {
		this.denaliFoundationRepositories = denaliFoundRepositories;
	}

	public void addToDenaliFoundationRepositories(DenaliFoundationRepository repository) {
		denaliFoundationRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromDenaliFoundationRepositories(DenaliFoundationRepository repository) {
		denaliFoundationRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ExternalDatabaseRepository> getExternalDatabaseRepositories() {
		return externalDatabaseRepositories;
	}

	public void setExternalDatabaseRepositories(Vector<ExternalDatabaseRepository> externalDBRepositories) {
		this.externalDatabaseRepositories = externalDBRepositories;
	}

	public void addToExternalDatabaseRepositories(ExternalDatabaseRepository repository) {
		externalDatabaseRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromExternalDatabaseRepositories(ExternalDatabaseRepository repository) {
		externalDatabaseRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ExternalRepository> getExternalRepositories() {
		return externalRepositories;
	}

	public void setExternalRepositories(Vector<ExternalRepository> externalRep) {
		this.externalRepositories = externalRep;
	}

	public void addToExternalRepositories(ExternalRepository repository) {
		externalRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromExternalRepositories(ExternalRepository repository) {
		externalRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ProjectDatabaseRepository> getProjectDatabaseRepositories() {
		return projectDatabaseRepositories;
	}

	public void setProjectDatabaseRepositories(Vector<ProjectDatabaseRepository> projectDBRepositories) {
		this.projectDatabaseRepositories = projectDBRepositories;
	}

	public void addToProjectDatabaseRepositories(ProjectDatabaseRepository repository) {
		projectDatabaseRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromProjectDatabaseRepositories(ProjectDatabaseRepository repository) {
		projectDatabaseRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ProjectRepository> getProjectRepositories() {
		return projectRepositories;
	}

	public void setProjectRepositories(Vector<ProjectRepository> projectReps) {
		this.projectRepositories = projectReps;
	}

	public void addToProjectRepositories(ProjectRepository repository) {
		projectRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromProjectRepositories(ProjectRepository repository) {
		projectRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<RationalRoseRepository> getRationalRoseRepositories() {
		return rationalRoseRepositories;
	}

	public void setRationalRoseRepositories(Vector<RationalRoseRepository> rrRepositories) {
		this.rationalRoseRepositories = rrRepositories;
	}

	public void addToRationalRoseRepositories(RationalRoseRepository repository) {
		rationalRoseRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromRationalRoseRepositories(RationalRoseRepository repository) {
		rationalRoseRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<WSDLRepository> getWSDLRepositories() {
		return wsdlRepositories;
	}

	public void setWSDLRepositories(Vector<WSDLRepository> webServiceRepositories) {
		this.wsdlRepositories = webServiceRepositories;
	}

	public void addToWSDLRepositories(WSDLRepository repository) {
		wsdlRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromWSDLRepositories(WSDLRepository repository) {
		wsdlRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<XMLSchemaRepository> getXmlSchemaRepositories() {
		return xmlSchemaRepositories;
	}

	public void setXmlSchemaRepositories(Vector<XMLSchemaRepository> xmlRepositories) {
		this.xmlSchemaRepositories = xmlRepositories;
	}

	public void addToXmlSchemaRepositories(XMLSchemaRepository repository) {
		xmlSchemaRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromXmlSchemaRepositories(XMLSchemaRepository repository) {
		xmlSchemaRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ThesaurusDatabaseRepository> getThesaurusDatabaseRepositories() {
		return thesaurusDatabaseRepositories;
	}

	public void setThesaurusDatabaseRepositories(Vector<ThesaurusDatabaseRepository> thesaurusDBRepositories) {
		this.thesaurusDatabaseRepositories = thesaurusDBRepositories;
	}

	public void addToThesaurusDatabaseRepositories(ThesaurusDatabaseRepository repository) {
		thesaurusDatabaseRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromThesaurusDatabaseRepositories(ThesaurusDatabaseRepository repository) {
		thesaurusDatabaseRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	public Vector<ThesaurusRepository> getThesaurusRepositories() {
		return thesaurusRepositories;
	}

	public void setThesaurusRepositories(Vector<ThesaurusRepository> thesaurusReps) {
		this.thesaurusRepositories = thesaurusReps;
	}

	public void addToThesaurusRepositories(ThesaurusRepository repository) {
		thesaurusRepositories.add(repository);
		registerRepository(repository);
	}

	public void removeFromThesaurusRepositories(ThesaurusRepository repository) {
		thesaurusRepositories.remove(repository);
		// unregisterRepository(repository);
	}

	// ==========================================================================
	// ======================= Named Access to DMEntity
	// =========================
	// ==========================================================================

	public DMEntity getDMEntity(Class aClass) {
		return getEntities().get(aClass.getName());
	}

	public DMEntity getDMEntity(Class aClass, boolean tryToLoadEntityIfNonExistant) {
		DMEntity returned = getDMEntity(aClass);
		if (returned == null) {
			if (tryToLoadEntityIfNonExistant) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Class " + aClass.getName() + " not imported in DataModel: try to dynamically load it.");
				}
				for (ExternalRepository repository : getExternalRepositories()) {
					if (repository.getJarLoader() != null && repository.getJarLoader().contains(aClass.getName())) {
						return LoadableDMEntity.createLoadableDMEntity(repository, aClass);

					}
				}
			} else {
				logger.warning("Sorry: class " + aClass.getName() + " not imported in DataModel");
			}
		}
		return returned;
	}

	public DMEntity getDMEntity(String packageName, String className) {
		if (packageName == null) {
			DMEntity e = null;
			e = getEntities().get(DMPackage.DEFAULT_PACKAGE_NAME + "." + className);
			if (e == null) {
				return getEntities().get(className);
			} else {
				return e;
			}
		}
		return getEntities().get(packageName + "." + className);
	}

	public DMEntity getDMEntity(String fullQualifiedName) {
		DMEntity returned = getEntities().get(fullQualifiedName);
		if (returned == null) {
			returned = getEntities().get(DMPackage.DEFAULT_PACKAGE_NAME + "." + fullQualifiedName);
		}
		return returned;
	}

	public Vector<DMEntity> getDMEntitiesWithName(String aName) {
		Vector<DMEntity> returned = new Vector<DMEntity>();
		for (DMEntity e : getEntities().values()) {
			if (e.getName().equalsIgnoreCase(aName)) {
				returned.add(e);
			}
		}
		return returned;
	}

	public DMEOEntity getDMEOEntity(EOEntity eoEntity) {
		for (Enumeration<DMRepository> en = repositories.elements(); en.hasMoreElements();) {
			DMRepository next = en.nextElement();
			if (next instanceof DMEORepository && ((DMEORepository) next).getDMEOEntity(eoEntity) != null) {
				return ((DMEORepository) next).getDMEOEntity(eoEntity);
			}
		}
		return null;
	}

	public DMEOModel getDMEOModel(EOModel eoModel) {
		for (Enumeration<DMRepository> en = repositories.elements(); en.hasMoreElements();) {
			DMRepository next = en.nextElement();
			if (next instanceof DMEORepository) {
				for (Enumeration en2 = ((DMEORepository) next).getDMEOModels().elements(); en2.hasMoreElements();) {
					DMEOModel next2 = (DMEOModel) en2.nextElement();
					if (next2.getEOModel() == eoModel) {
						return next2;
					}
				}
			}
		}
		return null;
	}

	public Vector<DMEOModel> getAllDMEOModel() {
		Vector<DMEOModel> reply = new Vector<DMEOModel>();
		for (Enumeration<DMRepository> en = repositories.elements(); en.hasMoreElements();) {
			DMRepository next = en.nextElement();
			if (next instanceof DMEORepository) {
				for (Enumeration en2 = ((DMEORepository) next).getDMEOModels().elements(); en2.hasMoreElements();) {
					DMEOModel next2 = (DMEOModel) en2.nextElement();
					reply.add(next2);
				}
			}
		}
		return reply;
	}

	public Vector getDMEntities(String className) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Not implemented !");
		}
		return new Vector();
	}

	// ==========================================================================
	// ============================= Sorting stuff
	// ==============================
	// ==========================================================================

	private Vector<DMRepository> orderedRepositories;

	private boolean needsReordering = true;

	@Override
	public synchronized Vector<DMRepository> getOrderedChildren() {
		if (needsReordering) {
			reorderRepositories();
		}
		return orderedRepositories;
	}

	private synchronized void reorderRepositories() {
		if (needsReordering) {
			if (orderedRepositories != null) {
				orderedRepositories.removeAllElements();
			} else {
				orderedRepositories = new Vector<DMRepository>();
			}
			orderedRepositories.addAll(repositories.values());
			Collections.sort(orderedRepositories, repositoryComparator);
			needsReordering = false;
			updateRepositoryFolders();
		}
	}

	private synchronized void updateRepositoryFolders() {
		_internalRepositoryFolder.updateInternalRepresentation();
		_persistantDataRepositoryFolder.updateInternalRepresentation();
		_nonPersistantDataRepositoryFolder.updateInternalRepresentation();
		_libraryRepositoryFolder.updateInternalRepresentation();
	}

	private static final RepositoryComparator repositoryComparator = new RepositoryComparator();

	/**
	 * Used to sort repositories according to following order: - JDKRepository - WORepository - ComponentRepository - ProjectRepositories,
	 * sorted by alphabetic name - ProjectDatabaseRepositories, sorted by alphabetic name - DenaliFoundationRepositories, sorted by
	 * alphabetic name - ExternalDatabaseRepositories, sorted by alphabetic name - ExternalRepositories, sorted by alphabetic name -
	 * ThesaurusRepositories, sorted by alphabetic name - ThesaurusDatabaseRepositories, sorted by alphabetic name
	 * 
	 * @author sguerin
	 * 
	 */
	private static class RepositoryComparator implements Comparator<DMRepository> {

		public RepositoryComparator() {
			super();
		}

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(DMRepository o1, DMRepository o2) {

			DMRepository rep1 = o1;
			DMRepository rep2 = o2;
			if (rep1.getOrder() != rep2.getOrder()) {
				return rep1.getOrder() - rep2.getOrder();
			} else { // Same king, order relating to name
				if (rep1.getName() == null || rep2.getName() == null) {
					return 0;
				} else {
					String s1 = rep1.getName();
					String s2 = rep2.getName();
					if (s1 != null && s2 != null) {
						return Collator.getInstance().compare(s1, s2);
					} else {
						return 0;
					}
				}
			}
		}

	}

	public void loadInitializingModel() {
		File configFile = new FileResource("Library/JarLibraries/DataModel.init");
		String configFileContent;
		try {
			configFileContent = FileUtils.fileContents(configFile);
		} catch (IOException e1) {
			logger.warning("Could not import initializing model: cannot read Library/JarLibraries/DataModel.init");
			e1.printStackTrace();
			return;
		}

		BufferedReader rdr = new BufferedReader(new StringReader(configFileContent));
		String jarFile = null;
		Vector<String> classesToImport = null;
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				break;
			}
			line = line.trim();
			if (line.length() > 0 && line.charAt(0) != '#') {
				// This line is meaningfull
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					String next = st.nextToken();
					if (next.equalsIgnoreCase("import")) {
						if (jarFile == null) {
							jarFile = st.nextToken();
							classesToImport = new Vector<String>();
						} else {
							importClasses(jarFile, classesToImport);
							jarFile = st.nextToken();
							classesToImport = new Vector<String>();
						}
					} else if (jarFile != null && classesToImport != null) {
						classesToImport.add(next);
					}
				}

			}
		}
		if (jarFile != null) {
			importClasses(jarFile, classesToImport);
		}
	}

	private void importClasses(String jarFileName, Vector<String> classNameToImport) {
		FileResource importedJar = new FileResource(jarFileName);

		if (importedJar.exists()) {
			logger.info("Importing " + importedJar.getAbsolutePath());
			DMSet classesToImport = new DMSet(getProject(), importedJar, false, null);
			for (String s : classNameToImport) {
				ClassReference cr = classesToImport.getClassReference(s);
				if (cr != null) {
					logger.info("Importing " + s);
					classesToImport.addToSelectedObjects(cr);
				} else {
					logger.info("Cannot import " + s);
				}
			}
			try {
				ExternalRepository javaFoundationRepository = ExternalRepository.createNewExternalRepository(getDMModel(), importedJar,
						classesToImport);
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
		} else {
			logger.warning("Could not import " + jarFileName + " : file not found");
		}

	}

	public DMEntity getDefaultParentDMEOEntity() {
		DMEntity wdlGenericRecord = getDMEntity("org.openflexo.core.woapp", "WDLGenericRecord");
		if (wdlGenericRecord != null) {
			return wdlGenericRecord;
		}
		DMEntity eoGenericRecord = getDMEntity("com.webobjects.eocontrol", "EOGenericRecord");
		if (eoGenericRecord != null) {
			return eoGenericRecord;
		}
		return null;
	}

	private DMClassLibrary _classLibrary;

	public DMClassLibrary getClassLibrary() {
		if (_classLibrary == null) {
			_classLibrary = new DMClassLibrary(this);
		}
		return _classLibrary;
	}

	public void close() {
		if (_classLibrary != null) {
			_classLibrary.close();
		}
		// Hack to clear all references and avoid too big memory leaks.
		jdkRepository = null;
		componentRepository = null;
		processInstanceRepository = null;
		processBusinessDataRepository = null;
		woRepository = null;
		eoPrototypeRepository = null;
		executionModelRepository = null;
		projectRepositories = null;
		projectDatabaseRepositories = null;
		externalRepositories = null;
		denaliFoundationRepositories = null;
		externalDatabaseRepositories = null;
		thesaurusRepositories = null;
		thesaurusDatabaseRepositories = null;
		rationalRoseRepositories = null;
		wsdlRepositories = null;
		xmlSchemaRepositories = null;
		entities = null;
		repositories = null;
		_project = null;
		_resource = null;
		_modelGroup = null;
		_internalRepositoryFolder = null;
		_persistantDataRepositoryFolder = null;
		_nonPersistantDataRepositoryFolder = null;
		_libraryRepositoryFolder = null;
		_globalDefaultConnectionString = null;
		_globalDefaultUsername = null;
		_globalDefaultPassword = null;
		statistics = null;
		dmTypeConverter = null;
		cachedEntitiesForTypes = null;
		_declaredTranstypers = null;
		diagrams = null;
		_classLibrary = null;
	}

	@Override
	public DMObject getParent() {
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dm_model";
	}

	public String getGlobalDefaultConnectionString() {
		return _globalDefaultConnectionString;
	}

	public void setGlobalDefaultConnectionString(String globalDefaultConnectionString) {
		_globalDefaultConnectionString = globalDefaultConnectionString;
		setChanged();
	}

	public String getGlobalDefaultPassword() {
		return _globalDefaultPassword;
	}

	public void setGlobalDefaultPassword(String globalDefaultPassword) {
		_globalDefaultPassword = globalDefaultPassword;
		setChanged();
	}

	public String getGlobalDefaultUsername() {
		return _globalDefaultUsername;
	}

	public void setGlobalDefaultUsername(String globalDefaultUserName) {
		_globalDefaultUsername = globalDefaultUserName;
		setChanged();
	}

	public static boolean canFindAnAdapter(String connectionStr) {
		return findDBMSName(connectionStr) != null;
	}

	public boolean isFrontbase() {
		return getAllDBMSNames().indexOf("frontbase") > -1;
	}

	public boolean isMysql() {
		return getAllDBMSNames().indexOf("mysql") > -1;
	}

	public boolean isOracle() {
		return getAllDBMSNames().indexOf("oracle") > -1;
	}

	public boolean isPostgres() {
		return getAllDBMSNames().indexOf("postgres") > -1;
	}

	public String findDriverName(String dbmsName) {
		if (dbmsName == null) {
			return null;
		}
		FileResource driverDirectory = new FileResource("Config/Generator/Libraries/JDBCDrivers/" + dbmsName);
		if (driverDirectory.exists()) {
			File[] content = driverDirectory.listFiles();
			if (content.length > 0) {
				for (int i = 0; i < content.length; i++) {
					if (content[i].getName().endsWith(".jar")) {
						return content[i].getName();
					}
				}
			}
		}
		return null;
	}

	private FileResource[] findDriverFile(String dbmsName) {
		if (dbmsName == null) {
			return null;
		}
		FileResource driverDirectory = new FileResource("Config/Generator/Libraries/JDBCDrivers/" + dbmsName);
		if (driverDirectory.exists()) {
			File[] found = driverDirectory.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.getName().toLowerCase().endsWith(".jar");
				}

			});
			if (found != null && found.length > 0) {
				FileResource[] reply = new FileResource[found.length];
				int i = 0;
				for (File f : found) {
					reply[i] = new FileResource("Config/Generator/Libraries/JDBCDrivers/" + dbmsName + "/" + f.getName());
					i++;
				}
				return reply;
			}
		}
		return null;
	}

	public FileResource[] findDriverFiles() {
		Vector<String> v = getAllDBMSNames();
		Vector<FileResource> tmp = new Vector<FileResource>();
		for (int i = 0; i < v.size(); i++) {
			FileResource[] f = findDriverFile(v.get(i));
			if (f != null) {
				for (FileResource file : f) {
					tmp.add(file);
				}
			}
		}
		FileResource[] reply = new FileResource[tmp.size()];
		return tmp.toArray(reply);
	}

	private Vector<String> getAllDBMSNames() {
		Vector<String> v = new Vector<String>();
		if (findDBMSName(getGlobalDefaultConnectionString()) != null) {
			v.add(findDBMSName(getGlobalDefaultConnectionString()));
		}
		Enumeration<DMEOModel> en = getAllDMEOModel().elements();
		while (en.hasMoreElements()) {
			DMEOModel model = en.nextElement();
			if (model.getDMModel().getExecutionModelRepository().getExecutionModelEOModel() == model || !model.isNotPrototypes()) {
				continue;
			}
			String dbms = findDBMSName(model.getConnectionDictionary().toString());
			if (dbms != null && !v.contains(dbms)) {
				v.add(dbms);
			}
		}
		return v;
	}

	private static String findDBMSName(String connectionStr) {
		if (connectionStr == null) {
			return null;
		}
		if (connectionStr.toLowerCase().indexOf("oracle") > -1) {
			return "oracle";
		}
		if (connectionStr.toLowerCase().indexOf("frontbase") > -1) {
			return "frontbase";
		}
		if (connectionStr.toLowerCase().indexOf("postgres") > -1) {
			return "postgres";
		}
		if (connectionStr.toLowerCase().indexOf("mysql") > -1) {
			return "mysql";
		}
		if (connectionStr.toLowerCase().indexOf("derby") > -1) {
			return "derby";
		}
		if (connectionStr.toLowerCase().indexOf("hsql") > -1) {
			return "hsql";
		}
		if (connectionStr.toLowerCase().indexOf("h2") > -1) {
			return "h2";
		}
		return null;
	}

	public static class DMModelMustDefineASuitableConnectionString extends ValidationRule {
		public DMModelMustDefineASuitableConnectionString() {
			super(DMModel.class, "dmmodel_must_define_a_suitable_connection_string");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final DMModel dmmodel = (DMModel) object;
			if (dmmodel.getAllDMEOModel().size() > 0 && !DMModel.canFindAnAdapter(dmmodel.getGlobalDefaultConnectionString())) {
				ValidationError error = new ValidationError(this, object, "dmmodel_must_define_a_suitable_connection_string");
				return error;
			}
			return null;
		}

		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return !CodeType.PROTOTYPE.equals(targetType);
		}

	}

	public DMModelStatistics getStatistics() {
		if (statistics == null) {
			statistics = new DMModelStatistics(this);
		}
		return statistics;
	}

	public DMEOPrototype getPrototypeNamed(String protoName) {
		if (eoPrototypeRepository != null) {
			return eoPrototypeRepository.getPrototypeNamed(protoName);
		}
		return null;
	}

	public Vector<DMEOPrototype> getAllEOPrototypes() {
		if (eoPrototypeRepository != null) {
			return eoPrototypeRepository.getPrototypes();
		}
		return null;
	}

	public DMTypeStringConverter getDmTypeConverter() {
		return dmTypeConverter;
	}

	public class CachedEntitiesForTypes extends Hashtable<String, DMEntity> {

		CachedEntitiesForTypes() {
			super();
		}

	}

	public CachedEntitiesForTypes getCachedEntitiesForTypes() {
		return cachedEntitiesForTypes;
	}

	public DMEOModel getDMEOModelNamed(String eomodelName) {
		Iterator<DMEOModel> it = getAllDMEOModel().iterator();
		DMEOModel candidate = null;
		while (it.hasNext()) {
			candidate = it.next();
			if (candidate.getName().equals(eomodelName)) {
				return candidate;
			}
		}
		return null;
	}

	public ProjectDatabaseRepository getProjectDatabaseRepositoryName(String projectDataBaseRepositoryName) {
		Iterator<ProjectDatabaseRepository> it = getProjectDatabaseRepositories().iterator();
		ProjectDatabaseRepository candidate = null;
		while (it.hasNext()) {
			candidate = it.next();
			if (candidate.getName().equals(projectDataBaseRepositoryName)) {
				return candidate;
			}
		}
		return null;
	}

	public String findNextDefaultEOModelName() {
		int i = 2;
		String baseName = getProject().getName() + "Model";
		String tryMe = baseName;
		while (getDMEOModelNamed(tryMe) != null) {
			tryMe = baseName + i;
			i++;
		}
		return tryMe;
	}

	private EOEntityCodeGenerator eoEntityCodeGenerator;

	public void setEOEntityCodeGenerator(EOEntityCodeGenerator suppliedEOEntityCodeGenerator) {
		eoEntityCodeGenerator = suppliedEOEntityCodeGenerator;
	}

	public EOEntityCodeGenerator getEOEntityCodeGenerator() {
		return eoEntityCodeGenerator;
	}

	private boolean _EOCodeGenerationActivated = false;

	public boolean getEOCodeGenerationAvailable() {
		return eoEntityCodeGenerator != null;
	}

	public boolean getEOCodeGenerationActivated() {
		return _EOCodeGenerationActivated && getEOCodeGenerationAvailable();
	}

	public void setEOCodeGenerationActivated(boolean isEOCodeGenerationActivated) {
		_EOCodeGenerationActivated = isEOCodeGenerationActivated;
	}

	public void activateEOCodeGeneration() {
		if (_EOCodeGenerationActivated == false) {
			logger.info("Activate EO code generation");
			_EOCodeGenerationActivated = true;
			setChanged();
			notifyObservers(new DMAttributeDataModification("EOCodeGenerationActivated", false, true));
			for (DMEntity e : getEntities().values()) {
				if (e instanceof DMEOEntity) {
					for (DMProperty p : e.getProperties().values()) {
						// Call setIsModified on all DMEOProperties for the whole DataModel
						// in order to have up-to-date generated code
						if (p instanceof DMEOProperty) {
							((DMEOProperty) p).codeGenerationHasBeenActivated();
						}
					}
				}
			}
		}
	}

	public void desactivateEOCodeGeneration() {
		if (_EOCodeGenerationActivated == true) {
			logger.info("Desactivate EO code generation");
			_EOCodeGenerationActivated = false;
			setChanged();
			notifyObservers(new DMAttributeDataModification("EOCodeGenerationActivated", true, false));
		}
	}

	public void addToDeclaredTranstypers(DMTranstyper aTranstyper) {
		if (aTranstyper.getReturnedType() == null) {
			return;
		}
		if (_declaredTranstypers.get(aTranstyper.getReturnedType()) == null) {
			_declaredTranstypers.put(aTranstyper.getReturnedType(), new Vector<DMTranstyper>());
		}
		Vector<DMTranstyper> list = _declaredTranstypers.get(aTranstyper.getReturnedType());
		if (!list.contains(aTranstyper)) {
			list.add(aTranstyper);
		}
	}

	public void removeFromDeclaredTranstypers(DMTranstyper aTranstyper) {
		removeFromDeclaredTranstypersForType(aTranstyper, aTranstyper.getReturnedType());
	}

	private void removeFromDeclaredTranstypersForType(DMTranstyper aTranstyper, DMType aType) {
		if (aType == null) {
			return;
		}
		if (_declaredTranstypers.get(aType) != null) {
			_declaredTranstypers.get(aType).remove(aTranstyper);
		}
	}

	public void notifyTranstyperTypeChanged(DMTranstyper aTranstyper, DMType oldType, DMType newType) {
		if (aTranstyper.getReturnedType() == null) {
			logger.warning("Transtyper with null type");
			return;
		}
		if (!aTranstyper.getReturnedType().equals(newType)) {
			logger.warning("Strange data: " + aTranstyper.getReturnedType() + " different from " + newType);
		}

		removeFromDeclaredTranstypersForType(aTranstyper, oldType);
		addToDeclaredTranstypers(aTranstyper);
	}

	private static final Vector<DMTranstyper> EMPTY_TRANSTYPER_VECTOR = EmptyVector.EMPTY_VECTOR(DMTranstyper.class);

	public Vector<DMTranstyper> getDMTranstypers(DMType type) {
		if (type == null) {
			return EMPTY_TRANSTYPER_VECTOR;
		}
		if (_declaredTranstypers.get(type) == null) {
			_declaredTranstypers.put(type, new Vector<DMTranstyper>());
		}
		return _declaredTranstypers.get(type);
	}

	public Vector<ERDiagram> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(Vector<ERDiagram> diagrams) {
		this.diagrams = diagrams;
	}

	public void addToDiagrams(ERDiagram diagram) {
		diagrams.add(diagram);
		setChanged();
		notifyObservers(new DiagramCreated(diagram));
	}

	public void removeFromDiagrams(ERDiagram diagram) {
		diagrams.remove(diagram);
		setChanged();
		notifyObservers(new DiagramDeleted(diagram));
	}

	public ERDiagram getDiagramWithName(String diagramName) {
		return getDiagramWithName(diagramName, true);
	}

	public ERDiagram getDiagramWithName(String diagramName, boolean caseSensitive) {
		for (ERDiagram d : getDiagrams()) {
			if (caseSensitive && diagramName.equals(d.getName())) {
				return d;
			} else if (!caseSensitive && diagramName.equalsIgnoreCase(d.getName())) {
				return d;
			}
		}
		return null;
	}

}