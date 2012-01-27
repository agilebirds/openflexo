package org.openflexo.dg.rm;

import java.util.Date;
import java.util.logging.Level;


import org.openflexo.dg.pptx.DGPptxXMLGenerator;
import org.openflexo.dg.pptx.PptxTemplatesEnum;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoTOCResource;
import org.openflexo.foundation.rm.GeneratedDocResource;
import org.openflexo.foundation.wkf.FlexoProcess;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */

public class ProjectPptxXmlFileResource extends PptxXmlFileResource<DGPptxXMLGenerator<FlexoProject>> implements FlexoObserver
{
	private static final String RELS_SUFFIX = ".FILERELS";

	private boolean isObserverRegistered = false;

	public ProjectPptxXmlFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

	public ProjectPptxXmlFileResource(DGPptxXMLGenerator<FlexoProject> generator, PptxTemplatesEnum pptxTemplate)
	{
		super(generator.getProject());
		setGenerator(generator);
		generator.addPptxResource(this, pptxTemplate);
		registerObserverWhenRequired();
	}

	@Override
	public String getName()
	{
		if(super.getName() == null && getCGFile()!=null && getCGFile().getRepository() != null && getGenerator() != null && getGenerator().getPptxTemplateForResource(this) != null)
			setName(nameForRepositoryAndPptxTemplate(getCGFile().getRepository(), getGenerator().getPptxTemplateForResource(this)));

		return super.getName();
	}

	public void registerObserverWhenRequired()
	{
		if ((!isObserverRegistered) && (getProject() != null))
		{
			isObserverRegistered = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("*** addObserver " + getFileName() + " for " + getProject());
			getProject().addObserver(this);
			
		}
	}

	public static String nameForRepositoryAndPptxTemplate(GenerationRepository repository, PptxTemplatesEnum pptxTemplate)
	{
		return repository.getName() + ".PROJECT_PPTX." + pptxTemplate;
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies()
	{
		//MARKER 1.5.1
		super.rebuildDependancies();
		
		if(getGenerator()!=null && getGenerator().getPptxTemplateForResource(this).getIsFullProjectDependent())
		{
			for(FlexoProcess process : getProject().getAllLocalFlexoProcesses())
				addToDependantResources(process.getFlexoResource());
			addToDependantResources(getProject().getWorkflow().getFlexoResource());
			addToDependantResources(getProject().getDataModel().getFlexoResource());
			for(ComponentDefinition comp:getProject().getFlexoComponentLibrary().getAllComponentList())
				addToDependantResources(comp.getComponentResource());
			addToDependantResources(getProject().getFlexoComponentLibraryResource().getFlexoResource());
			addToDependantResources(getProject().getFlexoDKVResource());
		}
		
		//TODO_MOS must modify this to make it fit the pptx model
		addToDependantResources(getProject().getTOCResource());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{

	}

	/**
	 * Return dependancy computing between this resource, and an other resource,
	 * asserting that this resource is contained in this resource's dependant
	 * resources
	 *
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof GeneratedDocResource)
		{
			if (getGenerator() != null)
			{
				if (!requestDate.before(getGenerator().getRepository().getLastUpdateDate()))
				{
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for DGRepository " + getRepository());
					return false;
				}
			}
		}
		else if (resource instanceof FlexoTOCResource)
		{
			if (getGenerator() != null && getGenerator().getRepository().getTocRepository() != null)
			{
				if (!requestDate.before(getGenerator().getRepository().getTocRepository().getLastUpdateDate()))
				{
					if (logger.isLoggable(Level.FINER))
						logger.finer("OPTIMIST DEPENDANCY CHECKING for TOC ENTRY " + getRepository());
					return false;
				}
			}
			return true;
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public String getFileName()
	{
		if(getFile() != null)
			return getFile().getName();

    	if (getGenerator() != null)
            return getGenerator().getFileNameForResource(this);

    	return null;
	}

	@Override
	public String getGenerationResultKey()
	{
		if(getGenerator() != null)
			return getGenerator().getGenerationResultKeyForResource(this);
		logger.warning("Attempt to get a GenerationResultKey on a resource without generator !");
		return null;
	}

}
