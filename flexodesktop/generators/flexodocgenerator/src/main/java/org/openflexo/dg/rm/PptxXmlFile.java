package org.openflexo.dg.rm;
/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */
import java.io.File;

import org.openflexo.dg.docx.DGDocxXMLGenerator;
import org.openflexo.dg.pptx.DGPptxXMLGenerator;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.cg.TextFile;
import org.openflexo.generator.rm.GenerationAvailableFile;

public class PptxXmlFile extends TextFile implements GenerationAvailableFile{
	
	public PptxXmlFile(File f, PptxXmlFileResource resource)
	{
		super(f);
		try
		{
			setFlexoResource(resource);
		}
		catch (DuplicateResourceException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public PptxXmlFileResource getFlexoResource()
	{
		return (PptxXmlFileResource) super.getFlexoResource();
	}

	@Override
	public String getCurrentGeneration()
	{
		return getFlexoResource().getCurrentGeneration();
	}

	@Override
	public DGPptxXMLGenerator getGenerator()
	{
		return (DGPptxXMLGenerator) getFlexoResource().getGenerator();
	}
}
