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


public class PresentationRepository extends GenerationRepository
{

    private static final Logger logger = FlexoLogger.getLogger(DGRepository.class.getPackage().getName());

    private DocType docType;

    private String postProductName;

    private String docTitle;

    private String customer;

    private String version;

    private String author;

    private String reviewer;

    private String systemName;

    private String systemVersion;

	private Date lastUpdateDate;

    private ProjectExternalRepository postBuildRepository;

    
    private PTOCRepository ptocRepository;
    

    private final Format format = Format.PPTX ;

    
    private FlexoModelObjectReference<PTOCRepository> ptocRepositoryRef;

    private Vector<CGRepository> repositoriedUsingAsReader;

    /**
     * Create a new GeneratedCodeRepository.
     */
    public PresentationRepository(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
        initializeDeserialization(builder);
    }

    /**
     * Creates a new repository
     * @throws DuplicateCodeRepositoryNameException
     *
     */
    public PresentationRepository(GeneratedDoc generatedDoc, String name, DocType docType, File directory) throws DuplicateCodeRepositoryNameException
    {
        super(generatedDoc, name, directory);
        this.repositoriedUsingAsReader = new Vector<CGRepository>();
        this.docType = docType;
        
    }

    /**
     * @param generatedCode
     */
    public PresentationRepository(GeneratedOutput generatedCode)
    {
        super(generatedCode);
        this.repositoriedUsingAsReader = new Vector<CGRepository>();
    }

     @Override
    protected void deleteExternalRepositories() 
    {
    	if (getPostBuildRepository()!=null)
    		getProject().removeFromExternalRepositories(getPostBuildRepository());
    	super.deleteExternalRepositories();
    }
     

     //MOS I have modified this to fit PPTX
    @Override
    public boolean isEnabled() {
    	return super.isEnabled() 
    			&&  getPTocRepository()!=null;
    }

    /**
     * Overrides getGeneratedCode
     *
     * @see org.openflexo.foundation.cg.CGObject#getGeneratedCode()
     */
    public GeneratedDoc getGeneratedDoc()
    {
        return (GeneratedDoc) super.getGeneratedCode();
    }

    /**
     * Overrides getClassNameKey
     *
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
    public String getClassNameKey()
    {
        return "generated_doc_repository";
    }

    /**
     * Overrides getInspectorName
     *
     * @see org.openflexo.inspector.InspectableObject#getInspectorName()
     */
    @Override
	public String getInspectorName()
    {
    	
		
			return Inspectors.DG.DG_REPOSITORY_PPTX_INSPECTOR;
		
    }


    public CGSymbolicDirectory getPptxSymbolicDirectory()
    {
    	return getSymbolicDirectoryNamed(CGSymbolicDirectory.PPTX);
    }
    
    public CGSymbolicDirectory getJSSymbolicDirectory()
    {
    	return getSymbolicDirectoryNamed(CGSymbolicDirectory.JS_PROCESSES);
    }

    public CGSymbolicDirectory getSymbolicDirectory()
    {
    	return getSymbolicDirectoryNamed(CGSymbolicDirectory.JS_PROCESSES);
    }

    public CGSymbolicDirectory getFiguresSymbolicDirectory()
    {
        return getSymbolicDirectoryNamed(CGSymbolicDirectory.FIGURES);
    }



    public CGSymbolicDirectory getResourcesSymbolicDirectory()
    {
    	
    	return getSymbolicDirectoryNamed(CGSymbolicDirectory.RESOURCES);
    }

    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
    	Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
        v.add(AddGeneratedCodeRepository.actionType);
   		v.add(AddPTOCEntry.actionType);
    	
    	return v;
    }


    public ProjectExternalRepository getPostBuildRepository()
    {
        if (postBuildRepository == null) {
            postBuildRepository = getProject().getExternalRepositoryWithKey(getName() + getFormat().getPostBuildKey());
            if (postBuildRepository==null)
                postBuildRepository = getProject().setDirectoryForRepositoryName(getName() + getFormat().getPostBuildKey(),getDirectory()!=null?getDirectory().getParentFile():new File(System.getProperty("user.home") + "/"+getFormat().getPostBuildKey()+"/" + getName()));
            if (postBuildRepository.getDirectory()==null)
                postBuildRepository.setDirectory(getDirectory()!=null?getDirectory().getParentFile():new File(System.getProperty("user.home") + "/"+getFormat().getPostBuildKey()+"/" + getName()));
        }
        return postBuildRepository;
    }

    private String docTypeAsString;

	public Date getLastUpdateDate() {
		if (lastUpdateDate == null)
			lastUpdateDate = getLastUpdate();
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public DocType getTarget() {
		return getDocType();
	}

    public DocType getDocType()
    {
        if (docType == null && getProject().getDocTypes().size() > 0)
            docType = getProject().getDocTypes().get(0);
        if (docTypeAsString!=null) {
        	DocType dt = getProject().getDocTypeNamed(docTypeAsString);
        	if (dt!=null) {
        		docType = dt;
        		docTypeAsString = null;
        	}
        }
        return docType;
    }

    public void setDocType(DocType docType)
    {
    	if (docType!=null) {
    		this.docType = docType;
    		setChanged();
    		notifyObservers(new CGDataModification("docType", null, docType));
    	}
    }

    public String getDocTypeAsString()
    {
        if (getDocType()!=null)
        	return getDocType().getName();
        else
        	return null;
    }

    public void setDocTypeAsString(String docType)
    {
        this.docTypeAsString = docType;
    }

    /**
     * @return
     */
    public File getPostBuildDirectory()
    {
        return getPostBuildRepository().getDirectory();
    }

    /**
     * @param selectedFile
     */
    public void setPostBuildDirectory(File selectedFile)
    {
    	if (getPostBuildRepository() != null) {
    		File old = getPostBuildRepository().getDirectory();
    		getPostBuildRepository().setDirectory(selectedFile);
    		setChanged();
    		notifyObservers(new CGDataModification("pdfDirectory",old,selectedFile));
    	}
    	else if (logger.isLoggable(Level.WARNING))
    		logger.warning("pdf repository is null");
    }

    public String getPostProductName()
    {
        if (postProductName == null)
            postProductName = getName()+getFormat().getPostBuildFileExtension();
        return postProductName;
    }

    public void setPostProductName(String pdfName)
    {
        this.postProductName = pdfName;
        setChanged();
        notifyObservers(new CGDataModification("pdfName", null, pdfName));
    }

    public String getAuthor()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getAuthor();
    	}
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("author", null, author));
    }

    public String getCustomer()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getCustomer();
    	}
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("customer", null, customer));
    }

    public String getDocTitle()
    {
    	if(getPTocRepository()!=null)
    		return  getPTocRepository().getDocTitle();
    	
        return docTitle;
    }

    public void setDocTitle(String docTitle)
    {
        this.docTitle = docTitle;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("docTitle", null, docTitle));
    }

    public String getReviewer()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getReviewer();
    	}
        return reviewer;
    }

    public void setReviewer(String reviewer)
    {
        String old = this.reviewer;
        this.reviewer = reviewer;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("reviewer", old, reviewer));
    }

    public String getVersion()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getVersion();
    	}
        return version;
    }

    public void setVersion(String version)
    {
        String old = this.version;
        this.version = version;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("version", old, version));
    }

    public String getSystemName()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getSystemName();
    	}
        return systemName;
    }

    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("systemName", null, systemName));
    }

    public String getSystemVersion()
    {
    	if(getPTocRepository()!=null){
    		return getPTocRepository().getSystemVersion();
    	}
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion)
    {
        this.systemVersion = systemVersion;
        lastUpdateDate = new Date();
        setChanged();
        notifyObservers(new CGDataModification("systemVersion", null, systemVersion));
    }

    public ImageFile getLogo() {
    	if (getPTocRepository()!=null)
    		return getPTocRepository().getLogo();
    	return null;
    }

    public File getPostBuildFile()
    {
        if (!getPostProductName().endsWith(getFormat().getPostBuildFileExtension()))
            setPostProductName(getPostProductName()+getFormat().getPostBuildFileExtension());
        return new File(getPostBuildDirectory(),getPostProductName());
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

    /**
     *
     */
    public void notifyPostBuildStop()
    {
        setChanged(false);
        notifyObservers(new PostBuildStop());
    }

    /**
     *
     */
    public void notifyPostBuildStart()
    {
        setChanged(false);
        notifyObservers(new PostBuildStart());
    }

    public String getLocalizedPdfInfoString()
    {
        return FlexoLocalization.localizedForKey("pdf_info_string");
    }


	
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
	
	
	

	@Override
	public Format getFormat() {

		return format;
	}



	public Vector<CGRepository> getRepositoriedUsingAsReader() {
		return repositoriedUsingAsReader;
	}

	public void addToRepositoriedUsingAsReader(CGRepository repository) {
		if (!repositoriedUsingAsReader.contains(repository)) {
			repositoriedUsingAsReader.add(repository);
		}
	}

	public void removeFromRepositoriedUsingAsReader(CGRepository repository) {
		repositoriedUsingAsReader.remove(repository);
	}

	@Override
	public void setFormat(Format format) {
		// Nothing to do !!!
		
	}

}
