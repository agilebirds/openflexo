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
package org.openflexo.technologyadapter.diagram.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.InvalidFileNameException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.ScreenshotBuilder;
import org.openflexo.foundation.resource.ScreenshotBuilder.ScreenshotImage;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResourceImpl;

/**
 * Default implementation for a diagram in Openflexo build-in diagram technology<br>
 * 
 * @author sylvain
 * 
 */
public abstract class DiagramImpl extends DiagramContainerElementImpl<DrawingGraphicalRepresentation> implements Diagram {

	private static final Logger logger = Logger.getLogger(DiagramImpl.class.getPackage().getName());

	private boolean screenshotModified = false;
	private ScreenshotImage<Diagram> screenshotImage;
	private File expectedScreenshotImageFile = null;

	public static DiagramResource newDiagramResource(String diagramName, String diagramTitle, String uri, File diagramFile,
			DiagramSpecification diagramSpecification, FlexoServiceManager serviceManager) throws InvalidFileNameException,
			SaveResourceException {

		DiagramResource newDiagramResource = DiagramResourceImpl.makeDiagramResource(diagramName, uri, diagramFile, diagramSpecification,
				serviceManager);

		Diagram newDiagram = newDiagramResource.getFactory().makeNewDiagram(diagramSpecification);
		newDiagramResource.setResourceData(newDiagram);
		newDiagram.setResource(newDiagramResource);
		diagramSpecification.addToExampleDiagrams(newDiagram);

		newDiagram.setTitle(diagramTitle);

		newDiagramResource.save(null);

		return newDiagramResource;
	}

	@Override
	public DiagramSpecification getMetaModel() {
		return getDiagramSpecification();
	}

	@Override
	public DiagramTechnologyAdapter getTechnologyAdapter() {
		return getResource().getServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null && getResource() instanceof FlexoFileResource) {
			expectedScreenshotImageFile = new File(((FlexoFileResource<Diagram>) getResource()).getFile().getParentFile(), getName()
					+ ".diagram.png");
		}
		return expectedScreenshotImageFile;
	}

	private ScreenshotImage<Diagram> buildAndSaveScreenshotImage() {
		ScreenshotBuilder<Diagram> builder = new ScreenshotBuilder<Diagram>() {
			@Override
			public String getScreenshotName(Diagram o) {
				return o.getName();
			}

			@Override
			public JComponent getScreenshotComponent(Diagram object) {
				/*ExternalVPMModule vpmModule = null;
				try {
					IModuleLoader moduleLoader = getViewPointLibrary().getServiceManager().getService(IModuleLoader.class);
					if (moduleLoader != null) {
						vpmModule = moduleLoader.getVPMModuleInstance();
					}
				} catch (ModuleLoadingException e) {
					logger.warning("cannot load VPM module (and so can't create screenshoot." + e.getMessage());
					e.printStackTrace();
				}

				if (vpmModule == null) {
					return null;
				}

				logger.info("Building " + getExpectedScreenshotImageFile().getAbsolutePath());

				JComponent c = vpmModule.createScreenshotForExampleDiagram(this);
				c.setOpaque(true);
				c.setBackground(Color.WHITE);
				JFrame frame = new JFrame();
				frame.setBackground(Color.WHITE);
				frame.setUndecorated(true);
				frame.getContentPane().add(c);
				frame.pack();
				c.validate();*/
				return null;
			}
		};

		screenshotImage = builder.getImage(this);
		try {
			logger.info("Saving " + getExpectedScreenshotImageFile().getAbsolutePath());
			ImageUtils.saveImageToFile(screenshotImage.image, getExpectedScreenshotImageFile(), ImageType.PNG);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + getExpectedScreenshotImageFile().getAbsolutePath());
		}
		screenshotModified = false;
		getPropertyChangeSupport().firePropertyChange("screenshotImage", null, screenshotImage);
		return screenshotImage;

	}

	private ScreenshotImage<Diagram> tryToLoadScreenshotImage() {
		// TODO
		/*if (getExpectedScreenshotImageFile() != null && getExpectedScreenshotImageFile().exists()) {
			BufferedImage bi = ImageUtils.loadImageFromFile(getExpectedScreenshotImageFile());
			if (bi != null) {
				logger.info("Read " + getExpectedScreenshotImageFile().getAbsolutePath());
				screenshotImage = ScreenshotGenerator.trimImage(bi);
				screenshotModified = false;
				return screenshotImage;
			}
		}*/
		return null;
	}

	public ScreenshotImage<Diagram> getScreenshotImage() {
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
		super.setIsModified();
		if (!isModified()) {
			logger.info(">>>>>>>>>>>>>>> Diagram " + this + " has been modified !!!");
		}
		screenshotModified = true;
	}

	@Override
	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return null;
	}

	@Override
	public void setName(String name) {
		if (requireChange(getName(), name)) {
			if (getResource() != null) {
				getResource().setName(name);
			}
		}
	}

	@Override
	public String getURI() {
		if (getResource() != null) {
			return getResource().getURI();
		}
		return null;
	}

	@Override
	public void setURI(String uri) {
		if (requireChange(getURI(), uri)) {
			if (getResource() != null) {
				getResource().setURI(uri);
			}
		}
	}

	@Override
	public DiagramFactory getDiagramFactory() {
		return ((DiagramResource) getResource()).getFactory();
	}

}
