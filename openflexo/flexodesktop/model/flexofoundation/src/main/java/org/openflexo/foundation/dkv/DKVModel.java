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
package org.openflexo.foundation.dkv;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dkv.action.AddDomainAction;
import org.openflexo.foundation.dkv.action.AddLanguageAction;
import org.openflexo.foundation.dkv.dm.DomainAdded;
import org.openflexo.foundation.dkv.dm.DomainRemoved;
import org.openflexo.foundation.dkv.dm.LanguageAdded;
import org.openflexo.foundation.dkv.dm.LanguageRemoved;
import org.openflexo.foundation.rm.FlexoDKVResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class DKVModel extends DKVObject implements XMLStorageResourceData
{

    private FlexoDKVResource resource;

    public static final Logger logger = FlexoLogger.getLogger(DKVModel.class.getPackage()
            .getName());

    public static DKVModel createNewDKVModel(FlexoProject project)
    {
        DKVModel dl = new DKVModel(project);
        File dkvFile = ProjectRestructuration.getExpectedDKVModelFile(project, project
                .getProjectName());
        FlexoProjectFile dkvModelFile = new FlexoProjectFile(dkvFile, project);
        FlexoDKVResource dkvRes;
        try {
            dkvRes = new FlexoDKVResource(project, dl, dkvModelFile);
        } catch (InvalidFileNameException e) {
            dkvModelFile = new FlexoProjectFile(FileUtils.getValidFileName(dkvModelFile.getRelativePath()));
            dkvModelFile.setProject(project);
            try {
                dkvRes = new FlexoDKVResource(project, dl, dkvModelFile);
            } catch (InvalidFileNameException e1) {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("Could not create DKV resource. Name: "+dkvModelFile.getRelativePath()+" is not valid. This should never happen.");
                return null;
            }
        }
        Language lg = new Language(dl,FlexoLocalization.localizedForKey("english"),true);
        lg.setIsoCode("EN");
        dl.addToLanguages(lg);
        try {
            dkvRes.saveResourceData();
            project.registerResource(dkvRes);
        } catch (Exception e1) {
            // Warns about the exception
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception raised: " + e1.getClass().getName()
                        + ". See console for details.");
            e1.printStackTrace();
        }
        return dl;
    }


    private DomainList domainList = new DomainList(this);

    private LanguageList languageList = new LanguageList(this);

     public DKVModel(FlexoDKVModelBuilder builder)
    {
        this(builder.getProject());
        builder.dkvModel=this;
        resource = builder.resource;
        initializeDeserialization(builder);
    }

    /**
     *
     */
    public DKVModel(FlexoProject project)
    {
        super(project);
        setProject(project);
        dkvModel=this;
        domainList = new DomainList(this);
        languageList = new LanguageList(this);
    }

    /**
     * Overrides getXMLMapping
     * @see org.openflexo.foundation.dkv.DKVObject#getXMLMapping()
     */
    @Override
    public XMLMapping getXMLMapping()
    {
        return getProject().getXmlMappings().getDKVMapping();
    }
    
    /**
     * Overrides getFullyQualifiedName
     *
     * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
     */
    @Override
    public String getFullyQualifiedName()
    {
        return "DOMAINLIST" + getProject().getProjectName();
    }

    /**
     * Overrides getFlexoXMLFileResource
     *
     * @see org.openflexo.foundation.rm.XMLStorageResourceData#getFlexoXMLFileResource()
     */
    @Override
	public FlexoXMLStorageResource getFlexoXMLFileResource()
    {
        return getFlexoResource();
    }

    public boolean isDomainNameLegal(String domainName) throws DuplicateDKVObjectException, EmptyStringException
    {
        if (domainName==null)
            throw new NullPointerException();
        if (domainName.trim().length()==0)
            throw new EmptyStringException();
        Enumeration en = getDomains().elements();
        while (en.hasMoreElements()) {
            Domain dom = (Domain) en.nextElement();
            if (dom.getName().equals(domainName))
                throw new DuplicateDKVObjectException(dom);
        }
       return true;
    }

    public Domain addDomainNamed(String domainName) throws DuplicateDKVObjectException, EmptyStringException
    {
        if (domainName==null)
            throw new NullPointerException();
        if (domainName.trim().length()==0)
            throw new EmptyStringException();
        Enumeration en = getDomains().elements();
        while (en.hasMoreElements()) {
            Domain dom = (Domain) en.nextElement();
            if (dom.getName().equals(domainName))
                throw new DuplicateDKVObjectException(dom);
        }
        Domain dom = new Domain(this);
        dom.setName(domainName);
        addToDomains(dom);
        addObserver(dom);
        return dom;
    }

    public boolean isLanguageNameLegal(String lgName) throws DuplicateDKVObjectException, EmptyStringException
    {
        if (lgName==null)
            throw new NullPointerException();
        if (lgName.trim().length()==0)
            throw new EmptyStringException();
        Enumeration en = getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            if (lg.getName().equals(lgName))
                throw new DuplicateDKVObjectException(lg);
        }
        return true;
   }

    public Language addLanguageNamed(String lgName) throws DuplicateDKVObjectException, EmptyStringException
    {
        if (lgName==null)
            throw new NullPointerException();
        if (lgName.trim().length()==0)
            throw new EmptyStringException();
        Enumeration en = getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            if (lg.getName().equals(lgName))
                throw new DuplicateDKVObjectException(lg);
        }
        Language lg = new Language(this);
        lg.setName(lgName);
        addToLanguages(lg);
        return lg;
    }

    public void addToDomains(Domain dom)
    {
        DomainAdded da = getDomainList().addToDomains(dom);
        sortedDomains = null;
        setChanged();
        notifyObservers(da);
     }

    public void removeFromDomains(Domain dom)
    {
        DomainRemoved dr = getDomainList().removeFromDomains(dom);
        sortedDomains = null;
        setChanged();
        notifyObservers(dr);
    }

    public void addToLanguages(Language lg)
    {
        LanguageAdded la = getLanguageList().addToLanguages(lg);
        setChanged();
        notifyObservers(la);
    }

    public void removeFromLanguage(Language lg)
    {
        LanguageRemoved lr = getLanguageList().removeFromLanguage(lg);
         setChanged();
        notifyObservers(lr);
    }

    public Vector<Domain> getDomains()
    {
        return getDomainList().getDomains();
    }

    @Override
    public void setName(String name) throws DuplicateDKVObjectException
    {
    	if(areSameValue(name, this.name))return;
    	sortedDomains = null;
    	super.setName(name);
    }

    private Vector<Domain> sortedDomains;

    public Vector<Domain> getSortedDomains(){
    	if(sortedDomains==null){
    		sortedDomains = (Vector<Domain>)getDomains().clone();
    		Collections.sort(sortedDomains, new Comparator<Domain>(){
    			@Override
				public int compare(Domain o1, Domain o2)
    			{
    				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    			}
    		});
    	}
        return sortedDomains;
    }
    public void setDomains(Vector<Domain> domains)
    {
        getDomainList().setDomains(domains);
    }

    public Vector<Language> getLanguages()
    {
        return getLanguageList().getLanguages();
    }

    public void setLanguages(Vector<Language> languages)
    {
        getLanguageList().setLanguages(languages);
    }

    public Language getMainLanguage()
    {
        for (Language l : getLanguages())
            if (l.getIsMain())
                return l;
        getLanguages().firstElement().setIsMain(true);
        return getLanguages().firstElement();
    }

    public class DomainList extends DKVObject {

        protected Vector<Domain> domains;

        /**
         * @param dl
         */
        public DomainList(DKVModel dl)
        {
            super(dl);
            domains = new Vector<Domain>();
        }

        public Vector<Domain> getDomains()
        {
            return domains;
        }

        public void setDomains(Vector<Domain> someDomains)
        {
            domains = someDomains;
        }

        /**
         * Overrides getFullyQualifiedName
         * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
         */
        @Override
        public String getFullyQualifiedName()
        {
            return "DOMAIN_LIST";
        }

        /**
         * Overrides getClassNameKey
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
        public String getClassNameKey()
        {
            return "domain_list";
        }

        /**
         * Overrides isDeleteAble
         * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
         */
        @Override
        public boolean isDeleteAble()
        {
            return false;
        }
        @Override
        public void undelete(){
        	//nothing to do since it can not be deleted :-)
        }
        /**
         * Overrides getSpecificActionListForThatClass
         * @see org.openflexo.foundation.dkv.DKVObject#getSpecificActionListForThatClass()
         */
        @Override
        protected Vector<FlexoActionType> getSpecificActionListForThatClass()
        {
            Vector<FlexoActionType> v= super.getSpecificActionListForThatClass();
            v.add(AddDomainAction.actionType);
            return v;
        }

        public DomainAdded addToDomains(Domain dom)
        {
            if (domains.contains(dom)) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Attempt to insert twice the same domain.");
                return null;
            }
            domains.add(dom);
            DomainAdded da = new DomainAdded(dom);
           getDomainList().setChanged();
            getDomainList().notifyObservers(da);
            return da;
         }

        public DomainRemoved removeFromDomains(Domain dom)
        {
            domains.remove(dom);
            DomainRemoved dr = new DomainRemoved(dom);
            getDomainList().setChanged();
            getDomainList().notifyObservers(dr);
            return dr;
        }

		@Override
		public Vector getAllEmbeddedValidableObjects() {
			Vector reply = new Vector();
			reply.addAll(getDomains());
			return reply;
		}


    }

    public class LanguageList extends DKVObject {

        protected Vector<Language> languages;

       /**
         * @param dl
         */
        public LanguageList(DKVModel dl)
        {
            super(dl);
            languages = new Vector<Language>();
        }

        public Vector<Language> getLanguages()
        {
            return languages;
        }

        public void setLanguages(Vector<Language> someLanguages)
        {
            languages = someLanguages;
        }


        /**
         * Overrides getFullyQualifiedName
         * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
         */
        @Override
        public String getFullyQualifiedName()
        {
            return "LANGUAGE_LIST";
        }

        /**
         * Overrides getClassNameKey
         * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
         */
        @Override
        public String getClassNameKey()
        {
            return "language_list";
        }

        /**
         * Overrides isDeleteAble
         * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
         */
        @Override
        public boolean isDeleteAble()
        {
            return false;
        }
        @Override
        public void undelete(){
        	//nothing to do since it can not be deleted :-)
        }
        /**
         * Overrides getSpecificActionListForThatClass
         * @see org.openflexo.foundation.dkv.DKVObject#getSpecificActionListForThatClass()
         */
        @Override
        protected Vector<FlexoActionType> getSpecificActionListForThatClass()
        {
            Vector<FlexoActionType> v= super.getSpecificActionListForThatClass();
            v.add(AddLanguageAction.actionType);
            return v;
        }

        public LanguageAdded addToLanguages(Language lg)
        {
            if (languages.contains(lg)) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Attempt to insert twice the same language.");
                return null;
            }
            languages.add(lg);
             LanguageAdded la = new LanguageAdded(lg);
            getLanguageList().setChanged();
            getLanguageList().notifyObservers(la);
            return la;
        }

        public LanguageRemoved removeFromLanguage(Language lg)
        {
            if (!languages.contains(lg)) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Attempt to remove a language that can not be found: "
                            + lg.getName());
                return null;
            }
            languages.remove(lg);
            LanguageRemoved lr = new LanguageRemoved(lg);
             getLanguageList().setChanged();
            getLanguageList().notifyObservers(lr);
            return lr;
        }

        @Override
        public Vector getAllEmbeddedValidableObjects() {
			Vector reply = new Vector();
			reply.addAll(getLanguages());
			return reply;
		}


    }

    public static Logger getLogger()
    {
        return logger;
    }

    public DomainList getDomainList()
    {
        return domainList;
    }

    public LanguageList getLanguageList()
    {
        return languageList;
    }

    public Domain getDomainNamed(String name)
    {
        Enumeration en = getDomains().elements();
        while (en.hasMoreElements()) {
            Domain dom = (Domain) en.nextElement();
            if (dom.getName().equals(name))
                return dom;
        }
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Domain "+name+" could not be found.");
        return null;
    }

    public String getNextDomainName(){
    	int i = 0;
    	while(true){
    		if(getDomainNamed("Domain-"+i)==null)return "Domain-"+i;
    		i++;
    	}
    }

    public Language getLanguageNamed(String lg_name)
    {
        Enumeration en = getLanguages().elements();
        while (en.hasMoreElements()) {
            Language lg = (Language) en.nextElement();
            if (lg.getName().equals(lg_name))
                return lg;
        }
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Language "+lg_name+" could not be found.");
        return null;
    }

    @Override
	public FlexoDKVResource getFlexoResource()
    {
        return resource;
    }

    @Override
	public void setFlexoResource(FlexoResource resource)
    {
        this.resource = (FlexoDKVResource)resource;
    }

    /**
     * Overrides save
     *
     * @see org.openflexo.foundation.rm.FlexoResourceData#save()
     */
    @Override
	public void save() throws SaveResourceException
    {
        resource.saveResourceData();
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
    public String getClassNameKey()
    {
        return "dkv_model";
    }

    /**
     * Overrides isDeleteAble
     * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
     */
    @Override
    public boolean isDeleteAble()
    {
        return false;
    }

    @Override
    public void undelete(){
    	//nothing to do since it can not be deleted :-)
    }

    /**
     * Overrides getSpecificActionListForThatClass
     * @see org.openflexo.foundation.dkv.DKVObject#getSpecificActionListForThatClass()
     */
    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
        Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
        v.add(AddDomainAction.actionType);
        v.add(AddLanguageAction.actionType);
        return v;
    }

	@Override
    public Vector getAllEmbeddedValidableObjects() {
		Vector answer = new Vector();
		answer.addAll(getDomains());
		answer.addAll(getLanguages());
        return answer;
	}


 }
