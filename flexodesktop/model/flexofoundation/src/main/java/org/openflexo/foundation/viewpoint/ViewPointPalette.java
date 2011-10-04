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
package org.openflexo.foundation.viewpoint;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jdom.JDOMException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.StringEncoder.Converter;


public class ViewPointPalette extends ViewPointObject implements Comparable<ViewPointPalette> {

	private static final Logger logger = Logger.getLogger(ViewPointPalette.class.getPackage().getName());

	private int index;

	private String name;
	private String description;
	private Vector<ViewPointPaletteElement> _elements;
	
	private ViewPoint _calc;
	
	private File _paletteFile;
	private RelativePathFileConverter relativePathFileConverter;

    // We dont want to import graphical engine in foundation
    // But you can assert graphical representation here is a org.openflexo.fge.DrawingGraphicalRepresentation.
	private Object graphicalRepresentation;
	
	public static ViewPointPalette instanciateCalcPalette(ViewPoint calc, File paletteFile) 
	{
		if (paletteFile.exists()) {
			Converter<File> previousConverter = null;
			FileInputStream inputStream = null;
			try {
				previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(paletteFile.getParentFile());
				StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
				inputStream = new FileInputStream(paletteFile);
				logger.info("Loading file "+paletteFile.getAbsolutePath());
				ViewPointPalette returned = (ViewPointPalette)XMLDecoder.decodeObjectWithMapping(inputStream, calc.getViewPointLibrary().get_VIEW_POINT_PALETTE_MODEL());
				returned.init(calc,paletteFile);
				return returned;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidXMLDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				StringEncoder.getDefaultInstance()._addConverter(previousConverter);
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
		else {
			logger.severe("Not found: "+paletteFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}
	
	public static ViewPointPalette newCalcPalette(ViewPoint calc, File paletteFile, Object graphicalRepresentation) 
	{
		ViewPointPalette palette = new ViewPointPalette();
		palette.setIndex(calc.getPalettes().size());
		palette.setGraphicalRepresentation(graphicalRepresentation);
		palette.init(calc, paletteFile);
		return palette;
	}
	
	// Used during deserialization, do not use it
	public ViewPointPalette() 
	{
		super();
		_elements = new Vector<ViewPointPaletteElement>();
	}
	
	private boolean initialized = false;
	
	private void init (ViewPoint calc, File paletteFile) 
	{
		if (StringUtils.isEmpty(name)) {
			name = paletteFile.getName().substring(0,paletteFile.getName().length()-8);
		}
		_calc = calc;
		_paletteFile = paletteFile;
		logger.info("Registering calc palette for calc "+calc.getName());
		relativePathFileConverter = new RelativePathFileConverter(calc.getViewPointDirectory());
		tryToLoadScreenshotImage();
		initialized = true;
	}
	
	@Override
	public void delete()
	{
		if (getCalc() != null) {
			getCalc().removeFromCalcPalettes(this);
		}
		_paletteFile.delete();
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey()
	{
		return "calc_palette";
	}

	@Override
	public String toString() 
	{
		return "OntologyCalcPalette:"+ (getCalc() != null ? getCalc().getName() : "null");
	}

	public int getIndex() 
	{
		return index;
	}

	public void setIndex(int index) 
	{
		this.index = index;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public ViewPoint getCalc() 
	{
		return _calc;
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() 
	{
		return getCalc().getViewPointLibrary();
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.CALC_PALETTE_INSPECTOR;
	}

	public Vector<ViewPointPaletteElement> getElements() 
	{
		return _elements;
	}

	public void setElements(Vector<ViewPointPaletteElement> elements)
	{
		_elements = elements;
	}

	public void addToElements(ViewPointPaletteElement obj)
	{
		obj.setPalette(this);
		_elements.add(obj);
		setChanged();
		notifyObservers(new CalcPaletteElementInserted(obj,this));
	}

	public boolean removeFromElements(ViewPointPaletteElement obj) 
	{
		obj.setPalette(null);
		boolean returned = _elements.remove(obj);
		setChanged();
		notifyObservers(new CalcPaletteElementRemoved(obj,this));
		return returned;
	}

	public Object getGraphicalRepresentation() 
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		this.graphicalRepresentation = graphicalRepresentation;
	}

	@Override
	public XMLMapping getXMLMapping() 
	{
		return getViewPointLibrary().get_VIEW_POINT_PALETTE_MODEL();
	}
	
	@Override
	public void saveToFile(File aFile)
	{
		Converter<File> previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
		StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
		super.saveToFile(aFile);
		StringEncoder.getDefaultInstance()._addConverter(previousConverter);
		clearIsModified(true);
	}

	public void save()
	{		
		logger.info("Saving palette to "+_paletteFile.getAbsolutePath()+"...");
		
		File dir = _paletteFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temporaryFile = null;
		try {
			makeLocalCopy();
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			saveToFile(temporaryFile);
			temporaryFile.renameTo(_paletteFile);	
			clearIsModified(true);
			buildAndSaveScreenshotImage();
			logger.info("Saved palette to "+_paletteFile.getAbsolutePath()+". Done.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not save palette to "+_paletteFile.getAbsolutePath());
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
		}
	}

	private void makeLocalCopy() throws IOException
	{
		if ((_paletteFile != null) && (_paletteFile.exists())) {
			String localCopyName = _paletteFile.getName() + "~";
			File localCopy = new File(_paletteFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(_paletteFile, localCopy);
		}
	}

	public ViewPointPaletteElement addPaletteElement(String name, Object graphicalRepresentation) 
	{
		ViewPointPaletteElement newElement = new ViewPointPaletteElement();
		newElement.setName(name);
		newElement.setGraphicalRepresentation(graphicalRepresentation);
		addToElements(newElement);
		return newElement;
	}
	
	public static class RelativePathFileConverter extends Converter<File>
	{
		private final File relativePath;

		public RelativePathFileConverter(File aRelativePath) 
		{
			super(File.class);
			relativePath = aRelativePath;
		}

		@Override
		public File convertFromString(String value) 
		{
			return new File(relativePath,value);
		}

		@Override
		public String convertToString(File value) 
		{
			try {
				return FileUtils.makeFilePathRelativeToDir(value, relativePath);
			} catch (IOException e) {
				logger.warning("IOException while computing relative path for "+value+" relative to "+relativePath);
				return value.getAbsolutePath();
			}
		}
		
	}


	@Override
	public int compareTo(ViewPointPalette o) 
	{
		return index-o.index;
	}
	
	public File getPaletteFile()
	{
		return _paletteFile;
	}
	
	private ScreenshotImage screenshotImage;
	
	private File expectedScreenshotImageFile = null;
	
	private File getExpectedScreenshotImageFile()
	{
		if (expectedScreenshotImageFile == null) {
			expectedScreenshotImageFile = new File(getPaletteFile().getParentFile(),getName()+".palette.png");
		}
		return expectedScreenshotImageFile;
	}
	
	private ScreenshotImage buildAndSaveScreenshotImage()
	{
		ExternalCEDModule cedModule = ExternalModuleDelegater.getModuleLoader() != null ? ExternalModuleDelegater.getModuleLoader().getCEDModuleInstance() : null;

		if (cedModule == null) {
			return null;
		}

		logger.info("Building "+getExpectedScreenshotImageFile().getAbsolutePath());

		JComponent c = cedModule.createScreenshotForPalette(this);
		BufferedImage bi = ImageUtils.createImageFromComponent(c);
		// Do not trim, we want all palette
		screenshotImage = ScreenshotGenerator.makeImage(bi);

		try {
			logger.info("Saving "+getExpectedScreenshotImageFile().getAbsolutePath());
			ImageUtils.saveImageToFile(screenshotImage.image,getExpectedScreenshotImageFile(),ImageType.PNG);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save "+getExpectedScreenshotImageFile().getAbsolutePath());
		}
		
		cedModule.finalizeScreenshotGeneration();

		screenshotModified = false;

		return screenshotImage;

	}
	
	private ScreenshotImage tryToLoadScreenshotImage()
	{
		if (getExpectedScreenshotImageFile().exists()) {
			BufferedImage bi = ImageUtils.loadImageFromFile(getExpectedScreenshotImageFile());
			if (bi != null) {
				logger.fine("Read "+getExpectedScreenshotImageFile().getAbsolutePath());
				screenshotImage = ScreenshotGenerator.makeImage(bi);
				screenshotModified = false;
				return screenshotImage;
			}
		}
		return null;
	}
	
	public ScreenshotImage getScreenshotImage()
	{
		if ((screenshotImage == null) || screenshotModified) {
			if (screenshotModified) {
				logger.info("Rebuilding screenshot for "+this+" because screenshot is modified");
			}
			buildAndSaveScreenshotImage();
		}
		return screenshotImage;
	}

	@Override
	public synchronized void setIsModified()
	{
		if (initialized) {
			if (!isModified()) {
				logger.info(">>>>>>>>>>>>>>> Palette "+this+" has been modified !!!");
			}
			super.setIsModified();
			screenshotModified = true;
		}
	}
	
	private boolean screenshotModified = false;
}
