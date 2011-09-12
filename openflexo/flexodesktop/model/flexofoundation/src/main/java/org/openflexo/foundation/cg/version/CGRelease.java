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
package org.openflexo.foundation.cg.version;

import java.util.Comparator;
import java.util.Date;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.foundation.xml.GeneratedSourcesBuilder;


public class CGRelease extends CGObject {

	private String _name;
	private Date _date;
	private String _userId;
	private GenerationRepository _repository;
	private CGVersionIdentifier _versionIdentifier;
	
	/**
	 * Create a new release.
     */
    public CGRelease(GeneratedCodeBuilder builder)
    {
        this(builder.generatedCode);
        initializeDeserialization(builder);
     }
    
	/**
	 * Create a new release.
     */
    public CGRelease(GeneratedSourcesBuilder builder)
    {
        this(builder.generatedSources);
        initializeDeserialization(builder);
     }
    
    public CGRelease(GeneratedOutput generatedCode)
    {
        super(generatedCode);  
    }

    public CGRelease(GenerationRepository repository)
    {
        this(repository.getGeneratedCode());
        setCGRepository(repository);
     }

	@Override
	public GenerationStatus getGenerationStatus() 
	{
		// Not applicable
		return null;
	}

	@Override
	public boolean hasGenerationErrors() 
	{
		// Not applicable
		return false;
	}

	@Override
	public boolean needsModelReinjection()
	{
		// Not applicable
		return false;
	}

	@Override
	public boolean isContainedIn(CGObject obj) 
	{
		return (obj == getCGRepository());
	}

	@Override
	public boolean isEnabled() 
	{
		// Not applicable
		return false;
	}

	@Override
	public boolean needsRegeneration() 
	{
		// Not applicable
		return false;
	}

	@Override
	public String getClassNameKey() 
	{
        return "generated_code_release";
	}

	@Override
	public String getFullyQualifiedName()
	{
		return getCGRepository().getFullyQualifiedName()+getVersionIdentifier();
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.GENERATORS.CG_RELEASE_INSPECTOR;
	}


	public Date getDate()
	{
		return _date;
	}

	public void setDate(Date date)
	{
		_date = date;
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

	public GenerationRepository getCGRepository() 
	{
		return _repository;
	}

	public void setCGRepository(GenerationRepository repository)
	{
		_repository = repository;
	}

	public String getUserId()
	{
		return _userId;
	}

	public void setUserId(String userId)
	{
		_userId = userId;
	}

	public CGVersionIdentifier getVersionIdentifier() 
	{
		return _versionIdentifier;
	}

	public void setVersionIdentifier(CGVersionIdentifier versionIdentifier) 
	{
		_versionIdentifier = versionIdentifier;
		_versionIdentifier.type = CGVersionIdentifier.VersionType.Release;
	}
	
	public static final Comparator<CGRelease> COMPARATOR
	= new Comparator<CGRelease>() {
		@Override
		public int compare(CGRelease o1, CGRelease o2) 
		{
			return CGVersionIdentifier.COMPARATOR.compare(o1.getVersionIdentifier(), o2.getVersionIdentifier());
		}
		
	};

}
