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
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.rm.DiagramPaletteResource;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;

public class DiagramPalette extends NamedViewPointObject implements XMLStorageResourceData<DiagramPalette>, Comparable<DiagramPalette> {

	static final Logger logger = Logger.getLogger(DiagramPalette.class.getPackage().getName());

	private int index;

	private Vector<DiagramPaletteElement> _elements;

	private ViewPoint _viewPoint;

	// private File xmlFile;
	private RelativePathFileConverter relativePathFileConverter;

	private DiagramPaletteResource resource;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation here is a org.openflexo.fge.DrawingGraphicalRepresentation.
	private Object graphicalRepresentation;

	/*public static DiagramPalette instanciateDiagramPalette(ViewPoint viewPoint, File paletteFile) {
		if (paletteFile.exists()) {
			FileInputStream inputStream = null;
			try {
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(paletteFile.getParentFile());
				inputStream = new FileInputStream(paletteFile);
				logger.info("Loading file " + paletteFile.getAbsolutePath());
				ViewPointBuilder builder = new ViewPointBuilder(viewPoint.getViewPointLibrary(), viewPoint);
				DiagramPalette returned = (DiagramPalette) XMLDecoder.decodeObjectWithMapping(inputStream, viewPoint.getViewPointLibrary()
						.get_VIEW_POINT_PALETTE_MODEL(), builder, new StringEncoder(StringEncoder.getDefaultInstance(),
						relativePathFileConverter));
				logger.info("Loaded file " + paletteFile.getAbsolutePath());
				returned.init(viewPoint, paletteFile);
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
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
			logger.severe("Not found: " + paletteFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}*/

	public static DiagramPalette newCalcPalette(ViewPoint calc, File paletteFile, Object graphicalRepresentation) {
		DiagramPalette palette = new DiagramPalette(null);
		palette.setIndex(calc.getPalettes().size());
		palette.setGraphicalRepresentation(graphicalRepresentation);
		palette.init(calc, paletteFile);
		return palette;
	}

	// Used during deserialization, do not use it
	public DiagramPalette(ViewPointBuilder builder) {
		super(builder);
		_elements = new Vector<DiagramPaletteElement>();
		if (builder != null) {
			_viewPoint = builder.getViewPoint();
		}
	}

	private boolean initialized = false;

	public void init(ViewPoint viewPoint, File paletteFile) {
		if (StringUtils.isEmpty(getName())) {
			setName(paletteFile.getName().substring(0, paletteFile.getName().length() - 8));
		}
		_viewPoint = viewPoint;
		// xmlFile = paletteFile;
		logger.info("Registering calc palette for calc " + viewPoint.getName());
		relativePathFileConverter = new RelativePathFileConverter(viewPoint.getResource().getDirectory());
		tryToLoadScreenshotImage();
		initialized = true;
	}

	@Override
	public void delete() {
		if (getViewPoint() != null) {
			getViewPoint().removeFromPalettes(this);
		}
		// logger.info("Deleting file " + xmlFile);
		// xmlFile.delete();
		super.delete();
		deleteObservers();
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return getElements();
	}

	@Override
	public String toString() {
		return "OntologyCalcPalette:" + (getViewPoint() != null ? getViewPoint().getName() : "null");
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		if (getResource().getFile() != null && !getResource().getFile().getName().startsWith(name)) {
			try {
				getResource().renameFileTo(name + ".palette");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFileNameException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ViewPoint getViewPoint() {
		return _viewPoint;
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return getViewPoint().getViewPointLibrary();
	}

	public Vector<DiagramPaletteElement> getElements() {
		return _elements;
	}

	public void setElements(Vector<DiagramPaletteElement> elements) {
		_elements = elements;
	}

	public void addToElements(DiagramPaletteElement obj) {
		obj.setPalette(this);
		_elements.add(obj);
		setChanged();
		notifyObservers(new CalcPaletteElementInserted(obj, this));
	}

	public boolean removeFromElements(DiagramPaletteElement obj) {
		obj.setPalette(null);
		boolean returned = _elements.remove(obj);
		setChanged();
		notifyObservers(new CalcPaletteElementRemoved(obj, this));
		return returned;
	}

	public Object getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getViewPointLibrary().get_VIEW_POINT_PALETTE_MODEL();
	}

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	public DiagramPaletteElement addPaletteElement(String name, Object graphicalRepresentation) {
		DiagramPaletteElement newElement = new DiagramPaletteElement(null);
		newElement.setName(name);
		newElement.setGraphicalRepresentation((ShapeGraphicalRepresentation<?>) graphicalRepresentation);
		addToElements(newElement);
		return newElement;
	}

	@Override
	public int compareTo(DiagramPalette o) {
		return index - o.index;
	}

	/*	public File getPaletteFile() {
			return xmlFile;
		}*/

	private ScreenshotImage screenshotImage;

	private File expectedScreenshotImageFile = null;

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null) {
			expectedScreenshotImageFile = new File(getResource().getFile().getParentFile(), getName() + ".palette.png");
		}
		return expectedScreenshotImageFile;
	}

	private ScreenshotImage buildAndSaveScreenshotImage() {
		ExternalCEDModule cedModule = null;
		try {
			cedModule = getProject().getModuleLoader() != null ? getProject().getModuleLoader().getVPMModuleInstance() : null;
		} catch (ModuleLoadingException e) {
			logger.warning("cannot load CED module (and so can't create screenshoot." + e.getMessage());
			e.printStackTrace();
		}

		if (cedModule == null) {
			return null;
		}

		logger.info("Building " + getExpectedScreenshotImageFile().getAbsolutePath());

		JComponent c = cedModule.createScreenshotForPalette(this);
		BufferedImage bi = ImageUtils.createImageFromComponent(c);
		// Do not trim, we want all palette
		screenshotImage = ScreenshotGenerator.makeImage(bi);

		try {
			logger.info("Saving " + getExpectedScreenshotImageFile().getAbsolutePath());
			ImageUtils.saveImageToFile(screenshotImage.image, getExpectedScreenshotImageFile(), ImageType.PNG);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + getExpectedScreenshotImageFile().getAbsolutePath());
		}

		cedModule.finalizeScreenshotGeneration();

		screenshotModified = false;

		return screenshotImage;

	}

	private ScreenshotImage tryToLoadScreenshotImage() {
		if (getExpectedScreenshotImageFile().exists()) {
			BufferedImage bi = ImageUtils.loadImageFromFile(getExpectedScreenshotImageFile());
			if (bi != null) {
				logger.fine("Read " + getExpectedScreenshotImageFile().getAbsolutePath());
				screenshotImage = ScreenshotGenerator.makeImage(bi);
				screenshotModified = false;
				return screenshotImage;
			}
		}
		return null;
	}

	public ScreenshotImage getScreenshotImage() {
		if (screenshotImage == null || screenshotModified) {
			if (screenshotModified) {
				logger.info("Rebuilding screenshot for " + this + " because screenshot is modified");
			}
			buildAndSaveScreenshotImage();
		}
		return screenshotImage;
	}

	@Override
	public synchronized void setIsModified() {
		if (initialized) {
			if (!isModified()) {
				logger.info(">>>>>>>>>>>>>>> Palette " + this + " has been modified !!!");
			}
			super.setIsModified();
			screenshotModified = true;
		}
	}

	private boolean screenshotModified = false;

	@Override
	public BindingModel getBindingModel() {
		return getViewPoint().getBindingModel();
	}

	@Override
	public String getLanguageRepresentation() {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

	// Implementation of XMLStorageResourceData

	@Override
	public FlexoStorageResource<DiagramPalette> getFlexoResource() {
		return (FlexoStorageResource<DiagramPalette>) getResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		setResource((DiagramPaletteResource) resource);
	}

	@Override
	public DiagramPaletteResource getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<DiagramPalette> resource) {
		this.resource = (DiagramPaletteResource) resource;
	}

	@Override
	public DiagramPaletteResource getFlexoXMLFileResource() {
		return getResource();
	}

	/*@Override
	public void saveToFile(File aFile) {
		FileOutputStream out = null;
		try {
			if (!aFile.getParentFile().exists()) {
				aFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(aFile);
			XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), out, getStringEncoder());
			out.flush();
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		clearIsModified(true);
	}*/

	@Override
	public void save() {
		logger.info("Saving ExampleDiagram to " + getResource().getFile().getAbsolutePath() + "...");

		// Following was used to debug (display purpose only)
		/*Converter<File> previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
		StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
		try {
			System.out.println("File: " + XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), getStringEncoder()));
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateSerializationIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringEncoder.getDefaultInstance()._addConverter(previousConverter);
		 */

		/*File dir = xmlFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temporaryFile = null;
		try {
			makeLocalCopy();
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			saveToFile(temporaryFile);
			FileUtils.rename(temporaryFile, xmlFile);
			clearIsModified(true);
			logger.info("Saved ViewPoint to " + xmlFile.getAbsolutePath() + ". Done.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not save ViewPoint to " + xmlFile.getAbsolutePath());
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
		}

		clearIsModified(false);*/

		try {
			getResource().save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}

	}

	/*private void makeLocalCopy() throws IOException {
		if (xmlFile != null && xmlFile.exists()) {
			String localCopyName = xmlFile.getName() + "~";
			File localCopy = new File(xmlFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(xmlFile, localCopy);
		}
	}*/

}
