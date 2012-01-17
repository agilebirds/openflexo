package org.openflexo.dg.rm;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.docx.DGDocxXMLGenerator;
import org.openflexo.dg.file.DGDocxXmlFile;
import org.openflexo.dg.file.DGPptxXmlFile;
import org.openflexo.dg.pptx.DGPptxXMLGenerator;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

public class PptxXmlFileResource<G extends DGPptxXMLGenerator<? extends FlexoModelObject>> extends TextFileResource<G, DGPptxXmlFile> implements GenerationAvailableFileResource, FlexoObserver {

	

	protected static final Logger logger = FlexoLogger.getLogger(PptxXmlFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public PptxXmlFileResource(FlexoProjectBuilder builder)
	{
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public PptxXmlFileResource(FlexoProject aProject)
	{
		super(aProject);
	}

	@Override
	public String getFileName()
	{
		try
		{
			String fileName = super.getFileName();
			if (fileName != null) {
				return fileName;
			}
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected PptxXmlFile createGeneratedResourceData()
	{
		return new PptxXmlFile(getFile(), this);
	}

	@Override
	public DocxXmlFile getGeneratedResourceData()
	{
		return (DocxXmlFile) super.getGeneratedResourceData();
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
		if (resource instanceof TemplateLocator)
		{
			return ((TemplateLocator) resource).needsUpdateForResource(this);
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	/**
	 * Overrides getFileFormat
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getFileFormat()
	 */
	@Override
	public FileFormat getResourceFormat()
	{
		return FileFormat.XML;
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType()
	{
		return ResourceType.DOCXXML_FILE;
	}

	/**
	 * Overrides setFileFormat
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#setFileFormat(org.openflexo.foundation.rm.ResourceFormat)
	 */
	@Override
	public void setResourceFormat(FileFormat format)
	{
		if (format != FileFormat.XML) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Who tried that? This is strictly forbidden. Yes you can thank me for preventing this kind of problem...");
			}
		}
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getGenerator().getObject())
		{
			if ((dataModification.propertyName() != null) && dataModification.equals("dontGenerate"))
			{
				if (getGenerator().getObject().getDontGenerate())
				{
					logger.info("Handle dont generate for object");
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					getCGFile().getRepository().refresh();
				}
			}
		}
	}


}
