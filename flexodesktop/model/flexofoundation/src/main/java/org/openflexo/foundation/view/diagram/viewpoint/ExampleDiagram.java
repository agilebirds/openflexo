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
package org.openflexo.foundation.view.diagram.viewpoint;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.view.diagram.rm.ExampleDiagramResource;
import org.openflexo.foundation.viewpoint.DiagramSpecification;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.StringUtils;

public class ExampleDiagram extends ExampleDiagramObject implements XMLStorageResourceData<ExampleDiagram> {

	private static final Logger logger = Logger.getLogger(ExampleDiagram.class.getPackage().getName());

	private ExampleDiagramResource resource;
	private DiagramSpecification diagramSpecification;
	private boolean initialized = false;

	private boolean screenshotModified = false;
	private ScreenshotImage screenshotImage;
	private File expectedScreenshotImageFile = null;

	public static ExampleDiagram newExampleDiagram(DiagramSpecification diagramSpecification, File shemaFile,
			DrawingGraphicalRepresentation<ExampleDiagram> graphicalRepresentation) {
		ExampleDiagram shema = new ExampleDiagram(null);
		shema.setGraphicalRepresentation(graphicalRepresentation);
		shema.init(diagramSpecification, shemaFile);
		return shema;
	}

	public ExampleDiagram(ExampleDiagramBuilder builder) {
		super(builder);
		if (builder != null) {
			builder.exampleDiagram = this;
			diagramSpecification = builder.diagramSpecification;
			resource = builder.resource;
		}
	}

	@Override
	public DrawingGraphicalRepresentation<?> getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation<?>) super.getGraphicalRepresentation();
	}

	public void init(DiagramSpecification diagramSpecification, File diagramFile) {
		if (StringUtils.isEmpty(getName())) {
			setName(diagramFile.getName().substring(0, diagramFile.getName().length() - 6));
		}
		this.diagramSpecification = diagramSpecification;
		logger.info("Registering example diagram for viewpoint " + diagramSpecification.getViewPoint().getName());
		tryToLoadScreenshotImage();
		initialized = true;
	}

	@Override
	public void delete() {
		if (getVirtualModel() != null) {
			getVirtualModel().removeFromExampleDiagrams(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public DiagramSpecification getVirtualModel() {
		return diagramSpecification;
	}

	public DiagramSpecification getDiagramSpecification() {
		return diagramSpecification;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getVirtualModel().getViewPoint();
	}

	@Override
	public ExampleDiagram getExampleDiagram() {
		return this;
	}

	@Override
	public boolean isContainedIn(ExampleDiagramObject o) {
		return o == this;
	}

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null) {
			expectedScreenshotImageFile = new File(getResource().getFile().getParentFile(), getName() + ".diagram.png");
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
	public void save() {
		logger.info("Saving ExampleDiagram to " + getResource().getFile().getAbsolutePath() + "...");

		try {
			getResource().save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}

	}

}
