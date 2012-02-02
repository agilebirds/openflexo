package org.openflexo.foundation.cg;

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
/**
 * MOS
 * @author MOSTAFA
 *
 */

import java.io.File;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.PostBuildStart;
import org.openflexo.foundation.cg.dm.PostBuildStop;
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.ptoc.PTOCModification;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.ptoc.action.AddPTOCEntry;
import org.openflexo.foundation.rm.ProjectExternalRepository;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCModification;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.action.AddTOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;


public class PresentationRepository extends DGRepository
{

    private static final Logger logger = FlexoLogger.getLogger(DGRepository.class.getPackage().getName());

    
    
    private PTOCRepository ptocRepository;
    


    
    private FlexoModelObjectReference<PTOCRepository> ptocRepositoryRef;


    /**
     * Create a new GeneratedCodeRepository.
     */
    public PresentationRepository(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
    }

    /**
     * Creates a new repository
     * @throws DuplicateCodeRepositoryNameException
     *
     */
    public PresentationRepository(GeneratedDoc generatedDoc, String name, DocType docType, File directory) throws DuplicateCodeRepositoryNameException
    {
        super(generatedDoc, name, docType, Format.PPTX, directory);
       
        
    }

    /**
     * @param generatedCode
     */
    public PresentationRepository(GeneratedOutput generatedCode)
    {
        super(generatedCode);
    }


     // I have modified this to fit PPTX
    @Override
    public boolean isEnabled() {
    	return isConnected() && getSourceCodeRepository().isConnected() 
    			&&  getPTocRepository()!=null;
    }


    
    @Override
    public String getAuthor()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getAuthor();
    	}
        return author;
    }

    @Override
    public String getCustomer()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getCustomer();
    	}
        return customer;
    }

    @Override
    public String getDocTitle()
    {
    	if(getPTocRepository()!=null)
    		return  getPTocRepository().getDocTitle();
    	
        return docTitle;
    }
    
    @Override
    public String getReviewer()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getReviewer();
    	}
        return reviewer;
    }

    @Override
    public String getVersion()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getVersion();
    	}
        return version;
    }

    @Override
    public String getSystemName()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getSystemName();
    	}
        return systemName;
    }

    @Override
    public String getSystemVersion()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getSystemVersion();
    	}
        return systemVersion;
    }

    @Override
    public ImageFile getLogo() {
    	if (getPTocRepository()!=null)
    		return getPTocRepository().getLogo();
    	return null;
    }


    //TODO_MOS understand the use of this
//    public TOCEntry getTOCEntryWithID(DocSection id) {
//    	if (getTocRepository()!=null)
//    		return getTocRepository().getTOCEntryWithID(id);
//    	else
//    		return null;
//    }
//
//    public TOCEntry getTOCEntryForObject(FlexoModelObject object) {
//    	if (getTocRepository()!=null)
//    		return getTocRepository().getTOCEntryForObject(object);
//    	else
//    		return null;
//    }

	
	public PTOCRepository getPTocRepository() {
		if (ptocRepository==null && ptocRepositoryRef!=null) {
			ptocRepository = ptocRepositoryRef.getObject(true);
			if (ptocRepository!=null && !isSerializing()) {
				setChanged();
				notifyObservers(new CGRepositoryConnected(this));
			}
		}
		return ptocRepository;
	}

	public void setPTocRepository(PTOCRepository tocRepository) {
		if (tocRepository==this.ptocRepository)
			return;
		this.ptocRepository = tocRepository;
		if (ptocRepositoryRef!=null) {
			ptocRepositoryRef.delete();
			ptocRepositoryRef = null;
		}
		if (ptocRepository!=null)
			ptocRepositoryRef = new FlexoModelObjectReference<PTOCRepository>(getProject(),tocRepository);
		else
			ptocRepositoryRef = null;
		setChanged();
		notifyObservers(new PTOCModification(null,tocRepository));
		if (ptocRepository!=null) {
			setChanged();
			notifyObservers(new CGRepositoryConnected(this));
		}
	}
	
	public FlexoModelObjectReference getPtocRepositoryRef() {
		return ptocRepositoryRef;
	}

	public void setPtocRepositoryRef(FlexoModelObjectReference<PTOCRepository> tocRepositoryRef) {
		this.ptocRepositoryRef = tocRepositoryRef;
	}
	
	

}
