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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.AddDocType;
import org.openflexo.foundation.cg.action.ImportTOCTemplate;
import org.openflexo.foundation.rm.FlexoPTOCResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoPTOCBuilder;


public class PTOCData extends PTOCObject implements XMLStorageResourceData {

	private FlexoPTOCResource resource;

	private FlexoProject project;

	private Vector<PTOCRepository> repositories;

	public PTOCData(FlexoPTOCBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
		builder.ptocData = this;
		resource = builder.resource;
	}

	public PTOCData(FlexoProject project){
		super(project);
		this.project = project;
		repositories = new Vector<PTOCRepository>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		for(PTOCRepository rep:getRepositories()) {
			String attempt = rep.getTitle();
			int i = 1;
			while (getRepositoryWithTitle(attempt)!=null && getRepositoryWithTitle(attempt)!=rep)
				attempt=rep.getTitle()+"-"+i++;
			rep.setTitle(attempt);
		}
		super.finalizeDeserialization(builder);
	}

	@Override
	public PTOCData getData() {
		return this;
	}

	@Override
	public String getClassNameKey() {
		return "toc_data";
	}

	//TODO_MOS Add available actions on PTOCData
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
//		v.add(AddTOCRepository.actionType);
//		v.add(AddDocType.actionType);
//		v.add(ImportTOCTemplate.actionType);
		return v;
	}

	@Override
	public FlexoPTOCResource getFlexoResource() {
		return resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		this.resource = (FlexoPTOCResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	@Override
	public void setProject(FlexoProject project) {
		this.project = project;
	}

	@Override
	public String getFullyQualifiedName() {
		return "PTOC-DATA";
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return getFlexoResource();
	}

	@Override
	public void save() throws SaveResourceException {
		resource.saveResourceData();
	}

	public Vector<PTOCRepository> getRepositories() {
		return repositories;
	}

	public void setRepositories(Vector<PTOCRepository> repositories) {
		this.repositories = repositories;
	}

	public void addToRepositories(PTOCRepository repository) {
		if (!repositories.contains(repository)) {
			repositories.add(repository);
			setChanged();
			this.
			notifyObservers(new PTOCModification("repositories",null,repository));
		}
	}

	public void removeFromRepositories(PTOCRepository repository){
		repositories.remove(repository);
		setChanged();
		notifyObservers(new PTOCModification("repositories", repository, null));
	}

	public static PTOCData createNewPTOCData(FlexoProject project) {
		PTOCData newCG = new PTOCData(project);
        if (logger.isLoggable(Level.INFO))
            logger.info("createNewTOCData(), project=" + project + " " + newCG);

        File cgFile = ProjectRestructuration.getExpectedPTOCFile(project);
        FlexoProjectFile generatedCodeFile = new FlexoProjectFile(cgFile, project);
        FlexoPTOCResource cgRes;
        try {
            cgRes = new FlexoPTOCResource(project, newCG);
            cgRes.setResourceFile(generatedCodeFile);
        } catch (InvalidFileNameException e2) {
            e2.printStackTrace();
            generatedCodeFile = new FlexoProjectFile("PTOC");
            generatedCodeFile.setProject(project);
            try {
            	cgRes = new FlexoPTOCResource(project, newCG);
                cgRes.setResourceFile(generatedCodeFile);
            } catch (InvalidFileNameException e) {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("Could not create PTOC.");
                e.printStackTrace();
                return null;
            }
        }

        try {
            cgRes.saveResourceData();
            project.registerResource(cgRes);
        } catch (Exception e1) {
            // Warns about the exception
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
            e1.printStackTrace();
            System.exit(-1);
        }

        return newCG;
	}

	public PTOCRepository getRepositoryWithTitle(String title) {
		if (title==null)
			return null;
		for (PTOCRepository rep : getRepositories()) {
			if (title.equals(rep.getTitle()))
				return rep;
		}
		return null;
	}

	public PTOCRepository getRepositoryWithIdentifier(String uid,long flexoID){
		if(uid==null)
			return null;
		Enumeration<PTOCRepository> en = getRepositories().elements();
		while(en.hasMoreElements()){
			PTOCRepository rep = en.nextElement();
			if(uid.equals(rep.getUserIdentifier()) && flexoID==rep.getFlexoID())
				return rep;
		}
		return null;
	}

}
