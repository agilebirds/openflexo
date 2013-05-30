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

package org.openflexo.technologyadapter.csv;

import java.io.File;
import java.util.logging.Logger;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.csv.model.CSVMetaModel;
import org.openflexo.technologyadapter.csv.model.CSVModel;

public class CSVTechnologyAdapter extends TechnologyAdapter<CSVModel, CSVMetaModel>
{
 private static String CSV_FILE_EXTENSION = ".csv";

 protected static final Logger logger = Logger.getLogger(CSVTechnologyAdapter.class.getPackage().getName());

  public CSVTechnologyAdapter()
    throws TechnologyAdapterInitializationException
  {
  }

  public String getName()
  {
   return new String("CSV Technology Adapter");
  }

  public ModelSlot<CSVModel, CSVMetaModel> createNewModelSlot(ViewPoint viewPoint)
  {
   return null;
  }

  public ModelSlot<CSVModel, CSVMetaModel> createNewModelSlot(VirtualModel<?> virtualModel)
  {
   return null;
  }

  public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return false;
  }

  public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return null;
  }

  public FlexoResource<CSVMetaModel> retrieveMetaModelResource(File aMetaModelFile, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return null;
  }

  public boolean isValidModelFile(File aModelFile, FlexoResource<CSVMetaModel> metaModelResource, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   logger.info("Est-ce que c'est un CSV: " + aModelFile.getAbsolutePath());
   return aModelFile.getName().endsWith(CSV_FILE_EXTENSION);
  }

  public boolean isValidModelFile(File aModelFile, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   logger.info("Est-ce que c'est un CSV: " + aModelFile.getAbsolutePath());
   return aModelFile.getName().endsWith(CSV_FILE_EXTENSION);
  }

  public String retrieveModelURI(File aModelFile, FlexoResource<CSVMetaModel> metaModelResource, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return null;
  }

  public FlexoResource<CSVModel> retrieveModelResource(File aModelFile, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   logger.info("Nouveau CSV à prendre en compte: " + aModelFile.getAbsolutePath());

   return null;
  }

  public FlexoResource<CSVModel> retrieveModelResource(File aModelFile, FlexoResource<CSVMetaModel> metaModelResource, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   logger.info("Nouveau CSV à prendre en compte: " + aModelFile.getAbsolutePath());

   return null;
  }

  public FlexoResource<CSVModel> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename, String modelUri, FlexoResource<CSVMetaModel> metaModelResource, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return null;
  }

  public FlexoResource<CSVModel> createEmptyModel(FlexoProject project, String filename, String modelUri, FlexoResource<CSVMetaModel> metaModelResource, TechnologyContextManager<CSVModel, CSVMetaModel> technologyContextManager)
  {
   return null;
  }

  public <R extends FlexoResource<? extends CSVModel>> ModelRepository<R, CSVModel, CSVMetaModel, ? extends TechnologyAdapter<CSVModel, CSVMetaModel>> createModelRepository(FlexoResourceCenter resourceCenter)
  {
   return null;
  }

  public <R extends FlexoResource<? extends CSVMetaModel>> MetaModelRepository<R, CSVModel, CSVMetaModel, ? extends TechnologyAdapter<CSVModel, CSVMetaModel>> createMetaModelRepository(FlexoResourceCenter resourceCenter)
  {
   return null;
  }

  public TechnologyContextManager<CSVModel, CSVMetaModel> createTechnologyContextManager(FlexoResourceCenterService service)
  {
   return null;
  }

  public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory()
  {
   return null;
  }

  public String getExpectedMetaModelExtension()
  {
   return null;
  }

  public String getExpectedModelExtension(FlexoResource<CSVMetaModel> metaModel)
  {
   return CSV_FILE_EXTENSION;
  }
}
