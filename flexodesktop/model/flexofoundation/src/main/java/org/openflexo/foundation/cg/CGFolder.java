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
package org.openflexo.foundation.cg;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.rm.cg.GenerationStatus;


public class CGFolder extends CGObject implements CGPathElement {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CGFolder.class.getPackage().getName());

	private GenerationRepository _repository;
	private CGPathElement _parent;
	private Vector<CGFolder> _subFolders;
	private Vector<CGFile> _files;
	private String _name;
	
    public CGFolder(GenerationRepository repository, String name, CGPathElement parent)
    {
    	super(repository.getGeneratedCode());  
    	_subFolders = new Vector<CGFolder>();
    	_files = new Vector<CGFile>();
    	setName(name);
    	setParent(parent);
    	//logger.info("Created new CGFolder "+toString());
    }

	@Override
	public String getClassNameKey()
	{
		return "cg_directory";
	}

	@Override
	public String getFullyQualifiedName() 
	{
		return getParent().getFullyQualifiedName()+"."+getName();
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.GENERATORS.CG_FOLDER_INSPECTOR;
	}

	@Override
	public CGPathElement getParent() {
		return _parent;
	}

	public void setParent(CGPathElement parent) 
	{
		_parent = parent;
	}

	@Override
	public synchronized Vector<CGFile> getFiles() 
	{
		return _files;
	}

    public CGFile[] getSortedFiles()
    {
        CGFile[] files = getFiles().toArray(new CGFile[getFiles().size()]);
        Arrays.sort(files, new Comparator<CGFile>() {

            @Override
			public int compare(CGFile o1, CGFile o2)
            {
                return o1.getFileName().toLowerCase().compareTo(o2.getFileName().toLowerCase());
            }
            
        });
        return files;
    }
    
	@Override
	public Vector<CGFolder> getSubFolders() 
	{
		return _subFolders;
	}

    public CGFolder[] getSortedSubFolders()
    {
        CGFolder[] folders = getSubFolders().toArray(new CGFolder[getSubFolders().size()]);
        Arrays.sort(folders, new Comparator<CGFolder>() {

            @Override
			public int compare(CGFolder o1, CGFolder o2)
            {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
            
        });
        return folders;
    }
    
	protected boolean isEnabled = false;
	
	@Override
	public boolean isEnabled()
	{
		return isEnabled;
	}

	public void setEnabled(boolean enabled)
	{
		isEnabled = enabled;
	}

	protected void clearFiles()
	{
		hasGenerationErrors = false;
		needsRegeneration = false;
		needsModelReinjection = false;
		generationStatus = GenerationStatus.UpToDate;
		for (CGFolder folder : _subFolders) {
			folder.clearFiles();
		}
		_files.clear();
		isEnabled = false;
	}

	@Override
	public String getName() 
	{
		return _name;
	}

	@Override
	public void setName(String name)
	{
		_name = name;
	}

	@Override
	public CGFolder getDirectoryNamed(String aName)
	{
		for (CGFolder dir : getSubFolders()) {
			if (dir.getName().equals(aName)) return dir;
		}
		return null;
	}

	public CGSymbolicDirectory getSymbolicDirectory()
	{
		CGPathElement current = this;
		while (!(current instanceof CGSymbolicDirectory) && (current.getParent() != null)) {
			current = current.getParent();
		}
		if (current instanceof CGSymbolicDirectory) {
			return (CGSymbolicDirectory)current;
		}
		return null;
	}
	
	public GenerationRepository getGeneratedCodeRepository() 
	{
		return _repository;
	}


	@Override
	public boolean hasGenerationErrors()
	{
		if (getGeneratedCodeRepository() != null)
			getGeneratedCodeRepository().ensureStructureIsUpToDate();
		return hasGenerationErrors;
	}

	@Override
	public boolean needsRegeneration()
	{
		if (getGeneratedCodeRepository() != null)
			getGeneratedCodeRepository().ensureStructureIsUpToDate();
		return needsRegeneration;
	}

	@Override
	public boolean needsModelReinjection()
	{
		if (getGeneratedCodeRepository() != null)
			getGeneratedCodeRepository().ensureStructureIsUpToDate();
		return needsModelReinjection;
	}

	@Override
	public GenerationStatus getGenerationStatus()
	{
		if (getGeneratedCodeRepository() != null)
			getGeneratedCodeRepository().ensureStructureIsUpToDate();
		return generationStatus;
	}

	@Override
	public String toString()
	{
		return super.toString()+"["+getName()+"]";
	}
	
	// ==========================================================================
    // ========================== Embedding implementation  =====================
    // ==========================================================================

	public boolean isContainedInFolder(CGFolder folder)
	{
		CGPathElement current = this;
		while ((current != folder) && (current.getParent() != null)) {
			current = current.getParent();
		}
		return (current == folder);
	}
	
    @Override
	public boolean isContainedIn(CGObject obj)
    {
    	if (obj instanceof GeneratedOutput) {
    		return (obj == getGeneratedCode());
    	}
    	else if (obj instanceof GenerationRepository) {
    		return (obj == _repository);
    	}
    	else if (obj instanceof CGSymbolicDirectory) {
    		return (obj == getSymbolicDirectory());
    	}
    	else if (obj instanceof CGFolder) {
    		return isContainedInFolder((CGFolder)obj);
    	}
    	return false;
    }


}
