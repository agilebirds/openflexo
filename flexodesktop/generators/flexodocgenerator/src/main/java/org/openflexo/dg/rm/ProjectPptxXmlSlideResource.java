package org.openflexo.dg.rm;

/**
 * TODO_MOS
 * MOS
 * @author MOSTAFA
 */
import org.openflexo.dg.pptx.DGPptxXMLGenerator;
import org.openflexo.dg.pptx.PptxTemplatesEnum;
import org.openflexo.foundation.ptoc.PSlide;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;

public class ProjectPptxXmlSlideResource extends ProjectPptxXmlFileResource {
	
	private PSlide slide;
	
	public ProjectPptxXmlSlideResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

	public ProjectPptxXmlSlideResource(DGPptxXMLGenerator<FlexoProject> generator, PptxTemplatesEnum pptxTemplate , PSlide slide)
	{
		super(generator, pptxTemplate);
		this.slide = slide ;
	}

}
