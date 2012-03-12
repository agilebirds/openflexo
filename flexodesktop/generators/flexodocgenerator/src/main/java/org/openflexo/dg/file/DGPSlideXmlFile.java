package org.openflexo.dg.file;

/**
 * MOS
 * @author MOSTAFA
 * TODO_MOS
 * 
 * This class represents every file that directly depends on a PSlide element of a PTOC
 * It is necessary to add this class in order to be able to generate slides 
 * It's different from docx, because when generating a docx document we only have to
 * write on the document.xml.vm template what ever the TOC is composed of. 
 * Whereas when generating pptx presentations, we must generate
 * a slide file for every PSlide. Thus, this class has a PSlideref attribute to be able
 * to properly save and reload the concerned resources.   
 */


import org.openflexo.dg.rm.PptxXmlFileResource;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.PresentationRepository;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.ptoc.PSlide;
import org.openflexo.foundation.ptoc.PTOCModification;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.file.AbstractCGFile;

public class DGPSlideXmlFile extends DGPptxXmlFile {

	
	private FlexoModelObjectReference<PSlide> pSlideRef;
	private PSlide pSlide;
	
	   public DGPSlideXmlFile(GeneratedCodeBuilder builder)
	    {
	        super(builder);
	     }
	    
	    public DGPSlideXmlFile(GeneratedOutput generatedCode)
	    {
	        super(generatedCode);  
	    }

	    public DGPSlideXmlFile(PresentationRepository repository, PptxXmlFileResource resource, PSlide slide)
	    {
	        super(repository,resource);
	        setPSlide(slide);
	    }

	    
	    public PSlide getPSlide() {
			if (pSlide==null && pSlideRef!=null) {
				pSlide = pSlideRef.getObject(true);
				if (pSlide!=null && !isSerializing()) {
					setChanged();
					notifyObservers(new CGDataModification("pSlide", null, pSlide));
				}
			}
			return pSlide;
		}

		public void setPSlide(PSlide slide) {
			if (slide==this.pSlide)
				return;
			this.pSlide = slide;
			if (pSlideRef!=null) {
				pSlideRef.delete();
				pSlideRef = null;
			}
			if (pSlide!=null)
				pSlideRef = new FlexoModelObjectReference<PSlide>(getProject(),slide);
			else
				pSlideRef = null;
			setChanged();
			notifyObservers(new CGDataModification("pSlide", slide, pSlide));
			
		}

		public FlexoModelObjectReference<PSlide> getPSlideRef() {
			return pSlideRef;
		}

		public void setPSlideRef(FlexoModelObjectReference<PSlide> pSlideRef) {
			this.pSlideRef = pSlideRef;
		}
		
}
