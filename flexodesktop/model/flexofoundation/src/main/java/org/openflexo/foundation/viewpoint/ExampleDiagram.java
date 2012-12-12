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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jdom2.JDOMException;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.ExampleDiagramResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;

public class ExampleDiagram extends ExampleDiagramObject implements XMLStorageResourceData<ExampleDiagram> {

	private static final Logger logger = Logger.getLogger(ExampleDiagram.class.getPackage().getName());

	private ExampleDiagramResource resource;

	public static ExampleDiagram instanciateExampleDiagram(ViewPoint viewPoint, File shemaFile) {
		if (shemaFile.exists()) {
			FileInputStream inputStream = null;
			try {
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(shemaFile.getParentFile());
				StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
				inputStream = new FileInputStream(shemaFile);
				logger.info("Loading file " + shemaFile.getAbsolutePath());
				ViewPointBuilder builder = new ViewPointBuilder(viewPoint.getViewPointLibrary(), viewPoint);
				ExampleDiagram returned = (ExampleDiagram) XMLDecoder.decodeObjectWithMapping(inputStream, viewPoint.getViewPointLibrary()
						.get_EXAMPLE_DRAWING_MODEL(), builder, new StringEncoder(StringEncoder.getDefaultInstance(),
						relativePathFileConverter));
				returned.init(viewPoint, shemaFile);
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
			logger.severe("Not found: " + shemaFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}

	public static ExampleDiagram newShema(ViewPoint calc, File shemaFile,
			DrawingGraphicalRepresentation<ExampleDiagram> graphicalRepresentation) {
		ExampleDiagram shema = new ExampleDiagram(null);
		shema.setGraphicalRepresentation(graphicalRepresentation);
		shema.init(calc, shemaFile);
		return shema;
	}

	private ViewPoint _viewpoint;
	private File xmlFile;
	private RelativePathFileConverter relativePathFileConverter;

	public ExampleDiagram(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			_viewpoint = builder.getViewPoint();
		}
	}

	@Override
	public DrawingGraphicalRepresentation<?> getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation<?>) super.getGraphicalRepresentation();
	}

	private boolean initialized = false;

	private void init(ViewPoint viewpoint, File drawingFile) {
		if (StringUtils.isEmpty(getName())) {
			setName(drawingFile.getName().substring(0, drawingFile.getName().length() - 6));
		}
		_viewpoint = viewpoint;
		xmlFile = drawingFile;
		logger.info("Registering example diagram for viewpoint " + viewpoint.getName());
		relativePathFileConverter = new RelativePathFileConverter(viewpoint.getViewPointDirectory());
		tryToLoadScreenshotImage();
		initialized = true;
	}

	@Override
	public void delete() {
		if (getViewPoint() != null) {
			getViewPoint().removeFromExampleDiagrams(this);
		}
		xmlFile.deleteOnExit();
		super.delete();
		deleteObservers();
	}

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	@Override
	public ViewPoint getViewPoint() {
		return _viewpoint;
	}

	@Override
	public ExampleDiagram getShema() {
		return this;
	}

	public File getXMLFile() {
		return xmlFile;
	}

	@Override
	public boolean isContainedIn(ExampleDiagramObject o) {
		return o == this;
	}

	private ScreenshotImage screenshotImage;

	private File expectedScreenshotImageFile = null;

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null) {
			expectedScreenshotImageFile = new File(getXMLFile().getParentFile(), getName() + ".shema.png");
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

		JComponent c = cedModule.createScreenshotForShema(this);
		c.setOpaque(true);
		c.setBackground(Color.WHITE);
		JFrame frame = new JFrame();
		frame.setBackground(Color.WHITE);
		frame.setUndecorated(true);
		frame.getContentPane().add(c);
		frame.pack();
		c.validate();
		BufferedImage bi = ImageUtils.createImageFromComponent(c);

		screenshotImage = ScreenshotGenerator.trimImage(bi);
		try {
			logger.info("Saving " + getExpectedScreenshotImageFile().getAbsolutePath());
			ImageUtils.saveImageToFile(screenshotImage.image, getExpectedScreenshotImageFile(), ImageType.PNG);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + getExpectedScreenshotImageFile().getAbsolutePath());
		}
		cedModule.finalizeScreenshotGeneration();
		screenshotModified = false;
		getPropertyChangeSupport().firePropertyChange("screenshotImage", null, screenshotImage);
		return screenshotImage;

	}

	private ScreenshotImage tryToLoadScreenshotImage() {
		if (getExpectedScreenshotImageFile().exists()) {
			BufferedImage bi = ImageUtils.loadImageFromFile(getExpectedScreenshotImageFile());
			if (bi != null) {
				logger.info("Read " + getExpectedScreenshotImageFile().getAbsolutePath());
				screenshotImage = ScreenshotGenerator.trimImage(bi);
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
			super.setIsModified();
			if (!isModified()) {
				logger.info(">>>>>>>>>>>>>>> Shema " + this + " has been modified !!!");
			}
			screenshotModified = true;
		}
	}

	private boolean screenshotModified = false;

	// Implementation of XMLStorageResourceData

	@Override
	public FlexoStorageResource<ExampleDiagram> getFlexoResource() {
		return (FlexoStorageResource<ExampleDiagram>) getResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		setResource((ExampleDiagramResource) resource);
	}

	@Override
	public ExampleDiagramResource getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<ExampleDiagram> resource) {
		this.resource = (ExampleDiagramResource) resource;
	}

	@Override
	public ExampleDiagramResource getFlexoXMLFileResource() {
		return getResource();
	}

	@Override
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
	}

	@Override
	public void save() {
		logger.info("Saving ExampleDiagram to " + xmlFile.getAbsolutePath() + "...");

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

		File dir = xmlFile.getParentFile();
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

		clearIsModified(false);
	}

	private void makeLocalCopy() throws IOException {
		if (xmlFile != null && xmlFile.exists()) {
			String localCopyName = xmlFile.getName() + "~";
			File localCopy = new File(xmlFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(xmlFile, localCopy);
		}
	}

}
