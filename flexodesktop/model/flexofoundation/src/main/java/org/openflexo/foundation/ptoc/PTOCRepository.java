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
package org.openflexo.foundation.ptoc;

/**
 * MOS
 * @author MOSTAFA
 * 
 * TODO_MOS
 * 
 */


import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.ptoc.action.RemovePTOCEntry;
import org.openflexo.foundation.ptoc.action.RemovePTOCRepository;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.foundation.toc.action.RemoveTOCEntry;
import org.openflexo.foundation.toc.action.RemoveTOCRepository;
import org.openflexo.foundation.xml.FlexoPTOCBuilder;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLDecoder;


public class PTOCRepository extends PTOCEntry {

	private DocType docType;

	private String docTypeAsString;

	private String docTitle;

    private String customer;

    private String version;

    private String author;

    private String reviewer;

    private String systemName;

    private String systemVersion;

    private boolean useEmbeddedEvents = true;

    private ImageFile logo;
    
    
    

	public PTOCRepository(PTOCData data, DocType docType, PTOCRepository tocTemplate) {
		this(data);
		this.docType = docType!=null?docType: data.getProject().getDocTypes().firstElement();
		if(tocTemplate==null){
			PTOCRepository defaultTocTemplate = null;
			if(docType.getName().equals(DefaultDocType.Technical.name())){
				defaultTocTemplate = loadPTOCTemplate("SRS");
			}else{
				defaultTocTemplate = loadPTOCTemplate("BRS");
			}
			createUnitsFromTemplate(defaultTocTemplate);
		}
		else createUnitsFromTemplate(tocTemplate);
	}
	
	private PTOCRepository loadPTOCTemplate(String templateName){
		String tocTemplateFileName = templateName+".xml";
		File tocTemplateFile = new FileResource("Config/PTOCTemplates/"+tocTemplateFileName);
		try {
			PTOCRepository tocTemplate = (PTOCRepository) XMLDecoder.decodeObjectWithMappingFile(new FileInputStream(tocTemplateFile), new FileResource("Models/PTOCModel/ptoc_template_0.1.xml"),new FlexoPTOCBuilder(null));
			return tocTemplate;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	private void createUnitsFromTemplate(PTOCRepository tocTemplate) {
		Enumeration<PTOCUnit> en = tocTemplate.getSortedPTocUnits();
		while(en.hasMoreElements()){
			addToPTocUnits(PTOCUnit.cloneUnitFromTemplate(this, en.nextElement()));
		}
	}

	private PTOCRepository(PTOCData data) {
		super(data);
	}

	public PTOCRepository(FlexoPTOCBuilder builder) {
		this(builder.ptocData);
		initializeDeserialization(builder);
	}

	@Override
	public void delete() {
		super.delete();
		getData().removeFromRepositories(this);
		docType = null;
	}

	@Override
	public PTOCRepository getRepository() {
		return this;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.remove(RemovePTOCEntry.actionType);
		v.add(RemovePTOCRepository.actionType);
		return v;
	}

	@Override
	public String getClassNameKey() {
		return "table_of_content";
	}

	@Override
	public String getFullyQualifiedName() {
		return "PTOC-"+getTitle();
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

    @Override
    public String getInspectorName() {
    	return Inspectors.DE.DE_PTOC_REPOSITORY_INSPECTOR;
    }

    public void notifyDocumentChanged(PTOCUnit cause) {
    	setChanged();
    	notifyObservers(new PTOCModification(null, cause));
    }

    public String buildDocument() {
		return buildDocument(null);
	}

	public String buildDocument(File cssFile) {
    	StringBuilder sb = new StringBuilder("<HTML>");
    	if (cssFile!=null) {
    		try {
				URL cssURL = cssFile.toURI().toURL();
				sb.append("<HEAD>");
				sb.append("<LINK REL=StyleSheet HREF=\"").append(cssURL).append("\" TITLE=\"Contemporary\" TYPE=\"text/css\">");
				sb.append("</HEAD>");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
    	}
    	sb.append("<BODY>");
    	Enumeration<PTOCUnit> en = getSortedPTocUnits();
    	//TODO_MOS
//		while(en.hasMoreElements()){
//			PTOCUnit unit = en.nextElement();
//			if(unit instanceof PSlide)
//				((PSlide) unit).printHTML(sb);
//		}
    	sb.append("</BODY></HTML>");
    	return sb.toString();
    }

	public PTOCEntry createObjectEntry(FlexoModelObject modelObject) {
		PTOCEntry reply = new PTOCEntry(getData(),modelObject);
		return reply;
	}
	
	public PSlide createPSlide(FlexoModelObject modelObject , PSlideType type) {
		PSlide reply = new PSlide(getData(),modelObject , type);
		return reply;
	}

//	public PSlide createObjectSlide(FlexoModelObject modelObject,DocSection identifier) {
//		PSlide reply = new PSlide(getData(),modelObject,identifier);
//		return reply;
//	}

	public PTOCEntry createDefaultEntry(String title) {
		PTOCEntry entry = new PTOCEntry(getData() );
    	entry.setTitle(title);
//    	
//
//    	if(!entry.isReadOnly()){
//	    	try {
//	    		entry.setContent(identifier.getDefaultContent(getDocType().getName()));
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//				if (logger.isLoggable(Level.SEVERE))
//					logger.severe("This should not happen! It means somebody has moved the setIsReadOnly(true) above the setContent() call.");
//			}
//		}

    	return entry;
	}
	
//TODO_MOS	
	public void printHTML(StringBuilder sb) {
//		if (getTitle()!=null) {
//			sb.append("<H").append(getLevel()).append(">");
//			sb.append(getTitleNumber()).append(" ");
//			sb.append(getTitle());
//			sb.append("</H").append(getLevel()).append(">");
//		}
//		if (isReadOnly()) {
//			sb.append(FlexoLocalization.localizedForKey("documentation_from_your_project_will_be_automatically_inserted_here"));
//		} else {
//			if (getContent()!=null)
//				sb.append(getContent());
//		}
//		Enumeration<PTOCEntry> en = getSortedTocEntries();
//		while(en.hasMoreElements())
//			en.nextElement().printHTML(sb);
	}


    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
        setChanged();
        notifyObservers(new CGDataModification("author", null, author));
    }

    public String getCustomer()
    {
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
        setChanged();
        notifyObservers(new CGDataModification("customer", null, customer));
    }

    public String getDocTitle()
    {
        return docTitle;
    }

    public void setDocTitle(String docTitle)
    {
        this.docTitle = docTitle;
        setChanged();
        notifyObservers(new CGDataModification("docTitle", null, docTitle));
    }

    public String getReviewer()
    {
        return reviewer;
    }

    public void setReviewer(String reviewer)
    {
        String old = this.reviewer;
        this.reviewer = reviewer;
        setChanged();
        notifyObservers(new CGDataModification("reviewer", old, reviewer));
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        String old = this.version;
        this.version = version;
        setChanged();
        notifyObservers(new CGDataModification("version", old, version));
    }

    public String getSystemName()
    {
        return systemName;
    }

    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
        setChanged();
        notifyObservers(new CGDataModification("systemName", null, systemName));
    }

    public String getSystemVersion()
    {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion)
    {
        this.systemVersion = systemVersion;
        setChanged();
        notifyObservers(new CGDataModification("systemVersion", null, systemVersion));
    }

    public boolean getUseEmbeddedEvents() {
    	return useEmbeddedEvents;
    }

    public void setUseEmbeddedEvents(boolean useEmbeddedEvents) {
    	this.useEmbeddedEvents = useEmbeddedEvents;
    	setChanged();
    	notifyObservers(new CGDataModification("useEmbeddedEvents", null, useEmbeddedEvents));
    }

    public ImageFile getLogo() {
		return logo;
	}

    public void setLogo(ImageFile logo) {
		this.logo = logo;
    	setChanged();
    	notifyObservers(new CGDataModification("logo", null, logo));
	}

	public Vector<PSlide> getOrderedSlides() {
		Vector<PSlide> returned = new Vector<PSlide>();
		DFSPath(returned);
		return returned;
	}
}
