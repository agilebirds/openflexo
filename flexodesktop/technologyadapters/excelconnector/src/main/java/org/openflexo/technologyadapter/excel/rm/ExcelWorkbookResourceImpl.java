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
package org.openflexo.technologyadapter.excel.rm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.excel.ExcelTechnologyContextManager;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;
import org.openflexo.technologyadapter.excel.model.io.BasicExcelModelConverter;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelModel;
import org.openflexo.toolbox.IProgress;

/**
 * Represents the resource associated to a {@link ExcelModel}
 * 
 * @author sguerin
 * 
 */
public abstract class ExcelWorkbookResourceImpl extends FlexoFileResourceImpl<ExcelWorkbook> implements ExcelWorkbookResource {

	private static final Logger logger = Logger.getLogger(ExcelWorkbookResourceImpl.class.getPackage().getName());

	private boolean isLoaded = false;

	private final boolean isXSSFFormat = false;

	/**
	 * Creates a new {@link ExcelModelResource} asserting this is an explicit creation: no file is present on file system<br>
	 * This method should not be used to retrieve the resource from a file in the file system, use
	 * {@link #retrieveOWLOntologyResource(File, OWLOntologyLibrary)} instead
	 * 
	 * @param ontologyURI
	 * @param owlFile
	 * @param ontologyLibrary
	 * @return
	 */
	public static ExcelWorkbookResource makeExcelWorkbookResource(String modelURI, File excelFile,
			ExcelTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(ExcelWorkbookResource.class);
			ExcelWorkbookResourceImpl returned = (ExcelWorkbookResourceImpl) factory.newInstance(ExcelWorkbookResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(excelFile.getName());
			returned.setFile(excelFile);
			returned.setURI(modelURI);
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerResource(returned);

			try {
				ExcelWorkbook resourceData = returned.loadResourceData(null);
				returned.setResourceData(resourceData);
				resourceData.setResource(returned);
				returned.save(null);
				returned.isLoaded = true;
			} catch (SaveResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidExcelFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Instanciates a new {@link OWLOntologyResource} asserting we are about to built a resource matching an existing file in the file
	 * system<br>
	 * 
	 */
	public static ExcelWorkbookResource retrieveExcelWorkbookResource(File modelFile, ExcelTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(ExcelWorkbookResource.class);
			ExcelWorkbookResourceImpl returned = (ExcelWorkbookResourceImpl) factory.newInstance(ExcelWorkbookResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(modelFile.getName());
			returned.setFile(modelFile);
			returned.setURI(modelFile.toURI().toString());
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerResource(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 * @throws FlexoException
	 */
	@Override
	public ExcelWorkbook loadResourceData(IProgress progress) throws InvalidExcelFormatException {

		ExcelWorkbook resourceData = null;
		Workbook wbOpenned = null;

		try {
			if (!getFile().exists()) {
				// Creates a new file
				getFile().createNewFile();
				if (getFile().getName().endsWith(".xls")) {
					wbOpenned = new HSSFWorkbook();
				} else {
					wbOpenned = new XSSFWorkbook();
				}
				// XSSFWorkbook wbOpenned = new XSSFWorkbook(fis);
				BasicExcelModelConverter converter = new BasicExcelModelConverter();
				resourceData = converter.convertExcelWorkbook(wbOpenned, getTechnologyAdapter());
				// TODO how to change this?
				resourceData.setResource(this/*retrieveExcelWorkbookResource(getFile(), getTechnologyContextManager())*/);
				setResourceData(resourceData);
				FileOutputStream fos = new FileOutputStream(getFile());
				wbOpenned.write(fos);
				fos.close();
			} else {
				FileInputStream fis = new FileInputStream(getFile());
				if (getFile().getName().endsWith(".xls")) {
					wbOpenned = new HSSFWorkbook(fis);
				} else {
					wbOpenned = new XSSFWorkbook(fis);
				}
				// XSSFWorkbook wbOpenned = new XSSFWorkbook(fis);
				BasicExcelModelConverter converter = new BasicExcelModelConverter();
				resourceData = converter.convertExcelWorkbook(wbOpenned, getTechnologyAdapter());
				// TODO how to change this?
				resourceData.setResource(this/*retrieveExcelWorkbookResource(getFile(), getTechnologyContextManager())*/);
				setResourceData(resourceData);
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e) {
			// TODO: load an XSSFWorkbook
			throw new InvalidExcelFormatException(this, e);
		}
		return resourceData;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public void save(IProgress progress) throws SaveResourceException {
		ExcelWorkbook resourceData;
		try {
			resourceData = getResourceData(progress);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (FlexoException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		}

		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			writeToFile(resourceData.getWorkbook());
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			resourceData.clearIsModified(false);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
			}
		}
	}

	/**
	 * Write file.
	 * 
	 * @throws SaveResourceException
	 */
	private void writeToFile(Workbook workbook) throws SaveResourceException {
		logger.info("Wrote " + getFile());
		FileOutputStream fileOut;

		try {
			fileOut = new FileOutputStream(getFile());
			workbook.write(fileOut);
			fileOut.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Class<ExcelWorkbook> getResourceDataClass() {
		return ExcelWorkbook.class;
	}
}
