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
package org.openflexo.foundation.rm;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.utils.FlexoProjectFile;


/**
 * Represents all generated code
 * 
 * @author sguerin
 */
public class GeneratedDocResource extends FlexoGeneratedOutputResource<GeneratedDoc> implements Serializable
{
    protected static final Logger logger = Logger.getLogger(GeneratedDocResource.class
            .getPackage().getName());

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public GeneratedDocResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
    }

    public GeneratedDocResource(FlexoProject aProject)
    {
        super(aProject);
        if (aProject != null) {
            try {
                setResourceFile(new FlexoProjectFile(ProjectRestructuration
                        .getExpectedGeneratedDocFile(aProject), aProject));
            } catch (InvalidFileNameException e) {
                FlexoProjectFile f = new FlexoProjectFile("GeneratedDoc");
                f.setProject(aProject);
                try {
                    setResourceFile(f);
                } catch (InvalidFileNameException e1) {
                    if (logger.isLoggable(Level.SEVERE))
                        logger.severe("This should not happen.");
                    e1.printStackTrace();
                }
            }
        }

     }

    public GeneratedDocResource(FlexoProject aProject,
            FlexoProjectFile generatedCodeFile) throws InvalidFileNameException
    {
        super(aProject);
        setResourceFile(generatedCodeFile);
    }

    public GeneratedDocResource(FlexoProject aProject, GeneratedDoc cg,
            FlexoProjectFile generatedCodeFile) throws InvalidFileNameException
    {
        this(aProject, generatedCodeFile);
        _resourceData = cg;
        try {
            cg.setFlexoResource(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.GENERATED_DOC;
    }

    @Override
	public Class getResourceDataClass()
    {
        return GeneratedDoc.class;
    }

}
