package org.openflexo.dg.file;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 */

import org.openflexo.dg.rm.DocxXmlFileResource;
import org.openflexo.dg.rm.PptxXmlFileResource;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.file.AbstractCGFile;

public class DGPptxXmlFile extends AbstractCGFile {

	   public DGPptxXmlFile(GeneratedCodeBuilder builder)
	    {
	        this(builder.generatedCode);
	        initializeDeserialization(builder);
	     }
	    
	    public DGPptxXmlFile(GeneratedOutput generatedCode)
	    {
	        super(generatedCode);  
	    }

	    public DGPptxXmlFile(DGRepository repository, PptxXmlFileResource resource)
	    {
	        super(repository,resource);
	    }

		@Override
		public PptxXmlFileResource getResource() 
		{
			return (PptxXmlFileResource)super.getResource();
		}
}
