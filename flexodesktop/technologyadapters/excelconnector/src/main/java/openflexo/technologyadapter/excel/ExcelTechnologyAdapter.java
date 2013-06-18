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
package openflexo.technologyadapter.excel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.excel.model.ExcelModel;
import org.openflexo.technologyadapter.excel.model.ExcelMetaModel;

/**
 * This class defines and implements the E technology adapter
 * 
 * @author sylvain, vincent, Christophe
 * 
 */

public class ExcelTechnologyAdapter extends TechnologyAdapter<ExcelModel, ExcelMetaModel> {

	
	@Override
	public String getName() {
		return "Excel technology adapter";
	}

	@Override
	public BasicExcelModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new BasicExcelModelSlot(viewPoint, this);
	}

	@Override
	public BasicExcelModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new BasicExcelModelSlot(virtualModel, this);
	}

	@Override
	public String retrieveMetaModelURI(
			File aMetaModelFile,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<ExcelMetaModel> retrieveMetaModelResource(
			File aMetaModelFile,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidModelFile(
			File aModelFile,
			FlexoResource<ExcelMetaModel> metaModelResource,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidModelFile(
			File aModelFile,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String retrieveModelURI(
			File aModelFile,
			FlexoResource<ExcelMetaModel> metaModelResource,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<ExcelModel> retrieveModelResource(
			File aModelFile,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<ExcelModel> retrieveModelResource(
			File aModelFile,
			FlexoResource<ExcelMetaModel> metaModelResource,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<ExcelModel> createEmptyModel(
			FileSystemBasedResourceCenter resourceCenter,
			String relativePath,
			String filename,
			String modelUri,
			FlexoResource<ExcelMetaModel> metaModelResource,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<ExcelModel> createEmptyModel(
			FlexoProject project,
			String filename,
			String modelUri,
			FlexoResource<ExcelMetaModel> metaModelResource,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends ExcelModel>> ModelRepository<R, ExcelModel, ExcelMetaModel, ? extends TechnologyAdapter<ExcelModel, ExcelMetaModel>> createModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends ExcelMetaModel>> MetaModelRepository<R, ExcelModel, ExcelMetaModel, ? extends TechnologyAdapter<ExcelModel, ExcelMetaModel>> createMetaModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyContextManager<ExcelModel, ExcelMetaModel> createTechnologyContextManager(
			FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedMetaModelExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedModelExtension(
			FlexoResource<ExcelMetaModel> metaModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidMetaModelFile(
			File aMetaModelFile,
			TechnologyContextManager<ExcelModel, ExcelMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
