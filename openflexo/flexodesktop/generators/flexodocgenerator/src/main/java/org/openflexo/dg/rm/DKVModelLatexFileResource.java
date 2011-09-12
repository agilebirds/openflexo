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
package org.openflexo.dg.rm;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class DKVModelLatexFileResource extends LatexFileResource<DGLatexGenerator<DKVModel>> implements FlexoObserver
{
    protected static final Logger logger = FlexoLogger.getLogger(DKVModelLatexFileResource.class.getPackage().getName());

    /**
     * @param builder
     */
    public DKVModelLatexFileResource(FlexoProjectBuilder builder)
    {
        super(builder);
    }

    /**
     * @param aProject
     */
    public DKVModelLatexFileResource(FlexoProject aProject)
    {
    	super(aProject);
    }

    private boolean isObserverRegistered = false;

    @Override
	public String getName()
    {
    	if (getCGFile()==null || getCGFile().getRepository()==null || getDKV()==null)
            return super.getName();
    	registerObserverWhenRequired();
    	if (super.getName()==null)
    		setName(nameForRepositoryAndDKV(getCGFile().getRepository(), getDKV()));
    	return nameForRepositoryAndDKV(getCGFile().getRepository(), getDKV());
    }

    public void registerObserverWhenRequired()
    {
    	if ((!isObserverRegistered) && (getDKV() != null)) {
    		isObserverRegistered = true;
            if (logger.isLoggable(Level.FINE))
                logger.fine("*** addObserver "+getFileName()+" for "+getProject());
            getDKV().addObserver(this);
    	}
    }

    public static String nameForRepositoryAndDKV(GenerationRepository repository, DKVModel dkvModel)
    {
    	return repository.getName()+".DKVMODEL_LATEX."+dkvModel.getName();
    }

    public DKVModel getDKV()
    {
    	if (getGenerator() != null)
            return getGenerator().getObject();
    	return null;
    }

    @Override
	protected LatexFile createGeneratedResourceData()
    {
        return new LatexFile(getFile(),this);
    }

    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        addToDependantResources(getProject().getFlexoDKVResource());
   }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getDKV()) {
			if (dataModification instanceof AttributeDataModification) {
				if (((AttributeDataModification)dataModification).getAttributeName().equals("name")) {
					logger.info("Building new resource after process renaming");
					DGLatexGenerator<DKVModel> generator = getGenerator();
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					generator.refreshConcernedResources();
					generator.getRepository().refresh();
					observable.deleteObserver(this);
					isObserverRegistered = false;
				}
			}
		}
	}

}
