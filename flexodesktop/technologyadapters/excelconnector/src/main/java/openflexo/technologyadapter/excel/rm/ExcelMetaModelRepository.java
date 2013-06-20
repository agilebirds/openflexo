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
package openflexo.technologyadapter.excel.rm;

import openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelMetaModel;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelModel;

/**
 * Excel Meta Model Repository.
 * 
 * @author sylvain
 */
public class ExcelMetaModelRepository extends
		MetaModelRepository<ExcelMetaModelResource, ExcelModel, ExcelMetaModel, ExcelTechnologyAdapter> {

	/**
	 * Constructor.
	 * 
	 * @param adapter
	 * @param resourceCenter
	 */
	public ExcelMetaModelRepository(ExcelTechnologyAdapter adapter, FlexoResourceCenter<?> resourceCenter) {
		super(adapter, resourceCenter);
	}
}
