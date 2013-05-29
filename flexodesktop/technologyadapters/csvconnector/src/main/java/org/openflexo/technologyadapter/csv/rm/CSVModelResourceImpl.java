/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.csv.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.csv.CSVTechnologyAdapter;
import org.openflexo.technologyadapter.csv.CSVTechnologyContextManager;
import org.openflexo.technologyadapter.csv.model.CSVModel;
import org.openflexo.toolbox.IProgress;

public abstract class CSVModelResourceImpl extends FlexoFileResourceImpl<CSVModel>
  implements CSVModelResource
{
	private static final Logger logger = Logger.getLogger(CSVModelResourceImpl.class.getPackage().getName());

  public static CSVModelResource makeCSVModelResource(String modelURI, File modelFile, CSVTechnologyContextManager technologyContextManager)
  {
    try
    {
	 ModelFactory factory = new ModelFactory(CSVModelResource.class);
     CSVModelResourceImpl returned = (CSVModelResourceImpl)factory.newInstance(CSVModelResource.class);
     returned.setName(modelFile.getName());
     returned.setFile(modelFile);
     returned.setURI(modelURI);
     returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
     returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
     returned.setTechnologyContextManager(technologyContextManager);
     technologyContextManager.registerModel(returned);

     return returned;
    } catch (ModelDefinitionException e) {
     e.printStackTrace();
    }
   return null;
  }

  public static CSVModelResource retrieveCSVModelResource(File modelFile, CSVTechnologyContextManager technologyContextManager)
  {
    try
    {
     ModelFactory factory = new ModelFactory(CSVModelResource.class);
     CSVModelResourceImpl returned = (CSVModelResourceImpl)factory.newInstance(CSVModelResource.class);
     returned.setName(modelFile.getName());
     returned.setFile(modelFile);
     returned.setURI(modelFile.toURI().toString());
     returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
     returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
     returned.setTechnologyContextManager(technologyContextManager);
     technologyContextManager.registerModel(returned);
     return returned;
    } catch (ModelDefinitionException e) {
     e.printStackTrace();
    }
   return null;
  }

  public CSVModel loadResourceData(IProgress progress)
    throws ResourceLoadingCancelledException, ResourceDependencyLoopException, FileNotFoundException, FlexoException
  {
   CSVModel returned = new CSVModel(getURI(), getFile(), (CSVTechnologyAdapter)getTechnologyAdapter());
   returned.loadWhenUnloaded();
   return returned;
  }

  public void save(IProgress progress)
    throws SaveResourceException
  {
    try
    {
     resourceData = (CSVModel)getResourceData(progress);
    }
    catch (FileNotFoundException e)
    {
      CSVModel resourceData;
     e.printStackTrace();
     throw new SaveResourceException(this);
    } catch (ResourceLoadingCancelledException e) {
     e.printStackTrace();
     throw new SaveResourceException(this);
    } catch (ResourceDependencyLoopException e) {
     e.printStackTrace();
     throw new SaveResourceException(this);
    } catch (FlexoException e) {
     e.printStackTrace();
     throw new SaveResourceException(this);
    }
    CSVModel resourceData = null;
    
   if (!hasWritePermission()) {
     if (logger.isLoggable(Level.WARNING)) {
       logger.warning("Permission denied : " + getFile().getAbsolutePath());
      }
     throw new SaveResourcePermissionDeniedException(this);
    }
   if (resourceData != null) {
     FlexoFileResource.FileWritingLock lock = willWriteOnDisk();
     writeToFile();
     hasWrittenOnDisk(lock);
     notifyResourceStatusChanged();
     resourceData.clearIsModified(false);
     if (logger.isLoggable(Level.INFO))
       logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
    }
  }

  public CSVModel getModelData()
  {
    try
    {
     return (CSVModel)getResourceData(null);
    } catch (ResourceLoadingCancelledException e) {
     e.printStackTrace();
     return null;
    } catch (FileNotFoundException e) {
     e.printStackTrace();
     return null;
    } catch (ResourceDependencyLoopException e) {
     e.printStackTrace();
     return null;
    } catch (FlexoException e) {
     e.printStackTrace();
   }return null;
  }

  public CSVModel getModel()
  {
   return getModelData();
  }

  private void writeToFile() throws SaveResourceException {
   FileOutputStream out = null;
    try {
     out = new FileOutputStream(getFile());
     StreamResult result = new StreamResult(out);
     TransformerFactory factory = TransformerFactory.newInstance(
       "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);

     Transformer transformer = factory.newTransformer();
     
    }
    catch (FileNotFoundException e)
    {
     e.printStackTrace();
     throw new SaveResourceException(this);
    } catch (TransformerConfigurationException e) {
     e.printStackTrace();
     throw new SaveResourceException(this);
    } finally {
     IOUtils.closeQuietly(out);
    }

   logger.info("Wrote " + getFile());
  }

  public Class<CSVModel> getResourceDataClass()
  {
   return CSVModel.class;
  }
}
