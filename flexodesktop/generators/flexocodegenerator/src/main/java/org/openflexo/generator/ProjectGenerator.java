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
package org.openflexo.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildListener;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.bpel.BPELWriter;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.ProjectInstanceTree;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.bpel.BPELGenerator;
import org.openflexo.generator.dm.DataModelGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.IOExceptionOccuredException;
import org.openflexo.generator.exception.PermissionDeniedException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.generator.ie.ComponentsGenerator;
import org.openflexo.generator.utils.BuildAnaGenerator;
import org.openflexo.generator.utils.FlexoReaderGenerator;
import org.openflexo.generator.utils.PrototypeProcessBusinessDataSamplesCreator;
import org.openflexo.generator.utils.ResourceGenerator;
import org.openflexo.generator.wkf.ControlGraphsGenerator;
import org.openflexo.generator.wkf.WorkflowContextGenerator;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.warbuilder.FlexoWarBuilder;


/**
 * Controller for Generator module
 *
 * @author sguerin
 */
public class ProjectGenerator extends AbstractProjectGenerator<CGRepository>
{

    protected static final Logger logger = Logger.getLogger(ProjectGenerator.class.getPackage().getName());

    private static final String CG_MACRO_LIBRARY_NAME = "VM_global_library.vm";

	private static final String GENERATE_COMPONENTS = "generate_page_components";
	private static final String GENERATE_UTILS = "generate_util_classes";
	private static final String GENERATE_APPLICATION_RESOURCES = "generate_application_resources";
	private static final String GENERATE_DATA_MODEL = "generating_data_model";
	private static final String GENERATE_READER = "generate_reader";
	private static final String GENERATE_WORKFLOW = "generate_workflow";
	private static final String GENERATE_CONTROL_GRAPHS = "generating_control_graph";
	private Hashtable<FlexoProcess,BPELWriter> processes=new Hashtable<FlexoProcess, BPELWriter>();

    private ComponentsGenerator componentsGenerator;

    private UtilsGenerator utilsGenerator;

    private ResourceGenerator resourceGenerator;

    private DataModelGenerator dataModelGenerator;

    private FlexoReaderGenerator readerGenerator;

    private ControlGraphsGenerator controlGraphsGenerator;

    private BPELGenerator bpelGenerator;
    
    private WorkflowContextGenerator workflowGenerator;

    protected FlexoProcess _currentProcess;

    private boolean hasBeenInitialized = false;

    private ProjectInstanceTree _projectInstanceTree = null;

    private Vector<BuildListener> buildListeners;

    // =============================================================
    // ======================== Constructor ========================
    // =============================================================

    /**
     * Default constructor
     *
     * @param workflowFile
     * @throws Exception
     */
    public ProjectGenerator(FlexoProject project, CGRepository repository) throws GenerationException
    {
        super(project, repository);
        if(repository==null) {
        	if (logger.isLoggable(Level.FINE))
				logger.fine("No target repository, this may happen during dynamic invocation of code generator within the context of the Data model edition");
        }

        if (repository != null) {
			if (repository.getTargetType() == CodeType.BPEL) {
				bpelGenerator = new BPELGenerator(this);
			} else {
				dataModelGenerator = new DataModelGenerator(this);
				componentsGenerator = new ComponentsGenerator(this);
				utilsGenerator = new UtilsGenerator(this);
				resourceGenerator = new ResourceGenerator(this);
				if (repository.includeReader())
					readerGenerator = new FlexoReaderGenerator(this);
				
				workflowGenerator = new WorkflowContextGenerator(this);
				// Not used yet
				controlGraphsGenerator = new ControlGraphsGenerator(this);
			}
		}

        if (getRootOutputDirectory() != null) {
        	if(!getResourceOutputDirectory().exists())
        		getRootOutputDirectory().mkdirs();
        	if(!getRootOutputDirectory().canWrite())
        		throw new PermissionDeniedException(getRootOutputDirectory(), this);
        }

        buildListeners = new Vector<BuildListener>();
    }

    public boolean isPrototype() {
    	return getTarget()==CodeType.PROTOTYPE;
    }

    @Override
    public CGTemplates getDefaultTemplates() {
    	return getProject().getGeneratedCode().getTemplates();
    }

    @Override
    public CodeType getTarget() {
    	return (CodeType) super.getTarget();
    }

    public ProjectInstanceTree getProjectInstanceTree(){
    	if(_projectInstanceTree==null)
    		_projectInstanceTree = new ProjectInstanceTree(getProject());
    	return _projectInstanceTree;
    }

    @Override
	public Logger getGeneratorLogger()
    {
        return logger;
    }

    /**
     * This method is very important, because it is the way we must identify or
     * build all resources involved in code generation. After this list has been
     * built, we just let ResourceManager do the work.
     *
     * @param repository:
     *            repository where resources should be retrieved or built
     * @param resources:
     *            the list of resources we must retrieve or build
     */
    @Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources)
    {
        hasBeenInitialized = true;

        if (componentsGenerator != null) {
        	refreshProgressWindow(GENERATE_COMPONENTS,true);
        	componentsGenerator.buildResourcesAndSetGenerators(repository,resources);
        }

        if (utilsGenerator != null) {
        	refreshProgressWindow(GENERATE_UTILS, true);
        	utilsGenerator.buildResourcesAndSetGenerators(repository, resources);
        }

        if (resourceGenerator != null) {
        	refreshProgressWindow(GENERATE_APPLICATION_RESOURCES, true);
        	resourceGenerator.buildResourcesAndSetGenerators(repository, resources);
        }

        if (dataModelGenerator != null) {
        	refreshProgressWindow(GENERATE_DATA_MODEL,true);
        	dataModelGenerator.buildResourcesAndSetGenerators(repository,resources);
        }

        if (readerGenerator != null) {
        	refreshProgressWindow(GENERATE_READER,true);
        	readerGenerator.buildResourcesAndSetGenerators(repository,resources);
        }
        
        if (workflowGenerator != null) {
        	refreshProgressWindow(GENERATE_WORKFLOW,true);
        	workflowGenerator.buildResourcesAndSetGenerators(repository,resources);
        }

        /*if (controlGraphsGenerator != null) {
        	controlGraphsGenerator.buildResourcesAndSetGenerators(repository, resources);
        }*/

        if (bpelGenerator != null) {
        	bpelGenerator.buildResourcesAndSetGenerators(repository, resources);
        }

    }

	@Override
	public boolean hasBeenInitialized()
	{
		return hasBeenInitialized;
	}

    public File generateWar(boolean cleanImmediately) throws GenerationException {
		final File distDirectory = getRepository().getWarDirectory();
		if (distDirectory == null) {
			throw new GenerationException("You MUST define a target directory. Sorry.");
		}
		if (!distDirectory.exists())
			distDirectory.mkdirs();
		final File tmpOutputDir;
		try {
			tmpOutputDir = File.createTempFile("WAR-build-" + FileUtils.getValidFileName(getProject().getName()), null);
		} catch (IOException e2) {
			e2.printStackTrace();
			throw new GenerationException("Error while creating temporary war output directory in temporary directory",
					"error_while_generating", null, e2);
		}
		if (tmpOutputDir.exists()) {
			FileUtils.recursiveDeleteFile(tmpOutputDir);
		}
		tmpOutputDir.mkdirs();
		final File genTempOutputDir = new File(tmpOutputDir, getPrefix() + "Application");
		if (!genTempOutputDir.exists())
			genTempOutputDir.mkdir();
		BuildAnaGenerator buildanaGenerator = new BuildAnaGenerator(ProjectGenerator.this);
		buildanaGenerator.generate(true);
		FlexoWarBuilder warBuilder = new FlexoWarBuilder(getProject(), buildanaGenerator.getFile(), distDirectory,
				getTarget() == CodeType.PROTOTYPE);
		warBuilder.addBuildListeners(buildListeners);

		if (logger.isLoggable(Level.INFO))
			logger.info("Copying repository (" + getRepository().getDirectory().getAbsolutePath() + ") to temp dir: "
					+ genTempOutputDir.getAbsolutePath());
		try {
			FileUtils.copyContentDirToDir(getRepository().getDirectory(), genTempOutputDir);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException();
		}
		if (logger.isLoggable(Level.INFO))
			logger.info("Launching war creation. Working directory is " + tmpOutputDir.getAbsolutePath());
		try {
			warBuilder.makeWar(tmpOutputDir, genTempOutputDir,cleanImmediately);
		} catch (IOException e) {
			e.printStackTrace();
			setGenerationException(new IOExceptionOccuredException(e, ProjectGenerator.this));
		} catch (Exception e) {
			e.printStackTrace();
			setGenerationException(new WARBuildingException(e));
		}

		if (getGenerationException() != null) {
			GenerationException e = getGenerationException();
			resetGenerationException();
			throw e;
		}
		return new File(distDirectory, getRepository().getWarName() + ".war");
	}

    /**
	 * Generates the data model classes. Resources are not copied.
	 *
	 * @return - true
	 */
    protected boolean generateDataModel(boolean forceRegenerate) throws GenerationException
    {
    	if (dataModelGenerator != null) {
    		refreshProgressWindow(GENERATE_DATA_MODEL, true);
    		dataModelGenerator.generate(forceRegenerate);
    		return true;
    	}
    	return false;
    }

    protected boolean generateControlGraphs(boolean forceRegenerate) throws GenerationException
    {
    	if (dataModelGenerator != null) {
    		refreshProgressWindow(GENERATE_CONTROL_GRAPHS, true);
    		controlGraphsGenerator.generate(forceRegenerate);
    		return true;
    	}
    	return false;
    }

    /**
     * Generates the workflow: screens, popups, utils classes and copies
     * resources.
     *
     * @return - true if the workflow is effectively generated.
     */
    public boolean generateWorkflow(boolean forceRegenerate) throws GenerationException
    {
    	if (componentsGenerator != null) {
    		refreshProgressWindow(GENERATE_COMPONENTS,true);
    		componentsGenerator.generate(forceRegenerate);
    	}

/*    	if (popupsGenerator != null) {
    		refreshProgressWindow(GENERATE_POPUPS,true);
    		popupsGenerator.generate(forceRegenerate);
    	}

    	if (tabsGenerator != null) {
    		refreshProgressWindow(GENERATE_TABS,true);
    		tabsGenerator.generate(forceRegenerate);
    	}
*/
    	if (utilsGenerator != null) {
    		refreshProgressWindow(GENERATE_UTILS, true);
    		utilsGenerator.generate(forceRegenerate);
    	}

    	if (resourceGenerator != null) {
    		refreshProgressWindow(GENERATE_APPLICATION_RESOURCES, true);
    		resourceGenerator.generate(forceRegenerate);
    	}

    	return true;
    }

    public Properties collectComponentHelp()
    {
        Properties p = new Properties();
        Enumeration en = getProject().getFlexoComponentLibrary().getAllComponentList().elements();
        ComponentDefinition cd = null;
        IEWOComponent comp = null;
        while (en.hasMoreElements()) {
            cd = (ComponentDefinition) en.nextElement();
            comp = cd.getWOComponent();
            if (comp != null) {
                if (comp.hasHelpText()) {
                    p.put(cd.getComponentName(), comp.getHelpText());
                }
            }
        }
        return p;
    }

    public String getAfterLoginDA()
    {
//        return utilsGenerator.getDirectActionGenerator().getGeneratedClassName() + "/open"
//                + nameForOperation(getProject().getFlexoNavigationMenu().getRootMenu().getOperation());
        if (getProject().getFlexoNavigationMenu().getRootMenu().getOperation()==null) {
        	if (getProject().getFlexoWorkflow().getRootFlexoProcess().getAllOperationNodesWithComponent().size() > 0)
        		return getProject().getFlexoWorkflow().getRootFlexoProcess().getAllOperationNodesWithComponent().firstElement().getStaticDirectActionUrl();
        	else return "null";
        }
    	return getProject().getFlexoNavigationMenu().getRootMenu().getOperation().getStaticDirectActionUrl();
    }

//    public WorkflowGenerator getWorkflowGenerator()
//    {
//        return workflowGenerator;
//    }

     public ComponentsGenerator getComponentsGenerator()
     {
         return componentsGenerator;
     }

    public DataModelGenerator getDataModelGenerator()
    {
        return dataModelGenerator;
    }

    public File getResourceOutputDirectory()
    {
        return getRootOutputDirectory();
    }
    public File getWebResourceOutputDirectory()
    {
        return getRepository().getWebResourcesSymbolicDirectory().getDirectory().getFile();
    }

    @Override
	public File getRootOutputDirectory()
    {
    	if (getRepository() == null)
    		return null;
        return getRepository().getDirectory();
    }

	public void addBuildListener(BuildListener listener)
	{
	    buildListeners.add(listener);
	}

	public void removeBuildListener(BuildListener listener)
	{
	    buildListeners.remove(listener);
	}

    public UtilsGenerator getUtilsGenerator()
    {
        return utilsGenerator;
    }

	public BPELWriter getInstance(FlexoProcess p, boolean forceNew) {
		if (processes.containsKey(p) && ! forceNew) {
			System.out.println("returning old BPELWriter for process : "+p.getName());
			return processes.get(p);
		}
		else {
			System.out.println("returning new BPELWriter for process : "+p.getName());

			if (processes.containsKey(p) && forceNew) {
				processes.remove(p);
			}
			BPELWriter newWriter= new BPELWriter(p);
			processes.put(p,newWriter);
			return newWriter;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>();
		try {
			result.add(templateWithName(CG_MACRO_LIBRARY_NAME));
		} catch (TemplateNotFoundException e) {
			logger.warning("Should include velocity macro template for project generator but template is not found '" + CG_MACRO_LIBRARY_NAME + "'");
			e.printStackTrace();
		}
		return result;
	}
	
	public String getPrototypeProcessInstance_Id_Key()
	{
		return PrototypeProcessBusinessDataSamplesCreator.PROCESSINSTANCE_ID_KEY;
	}
	
	public String getPrototypeProcessInstance_Status_Key()
	{
		return FlexoProcess.PROCESSINSTANCE_STATUS_KEY;
	}
	
	public String getPrototypeProcessInstance_CreationDate_Key()
	{
		return FlexoProcess.PROCESSINSTANCE_CREATIONDATE_KEY;
	}
	
	public String getPrototypeProcessInstance_LastUpdateDate_Key()
	{
		return FlexoProcess.PROCESSINSTANCE_LASTUPDATEDATE_KEY;
	}
	
	public String getWorkflowClassPackage()
	{
		return WorkflowContextGenerator.PACKAGENAME;
	}
	
	public DMEntity getProcessBusinessDataBaseEntity()
	{
		return getProject().getDataModel().getProcessBusinessDataRepository().getProcessBusinessDataEntity();
	}
	
	public String getProcessBusinessDataPackageImports()
	{
		StringBuilder sb = new StringBuilder();
		for(DMPackage dmPackage : getProject().getDataModel().getProcessBusinessDataRepository().getPackages().values())
		{
			if(!dmPackage.isDefaultPackage())
				sb.append("import " + dmPackage.getFullyQualifiedName() + ".*;");
		}
		return sb.toString();
	}
}