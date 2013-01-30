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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.diagram.rm.DiagramPaletteResource;
import org.openflexo.foundation.view.diagram.rm.DiagramPaletteResourceImpl;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteElementRemoved;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.StringEncoder;

public class DiagramPalette extends DiagramPaletteObject implements XMLStorageResourceData<DiagramPalette>, Comparable<DiagramPalette> {

	static final Logger logger = Logger.getLogger(DiagramPalette.class.getPackage().getName());

	private int index;
	private Vector<DiagramPaletteElement> _elements;
	private DiagramSpecification diagramSpecification;
	private RelativePathFileConverter relativePathFileConverter;
	private DiagramPaletteResource resource;
	private DrawingGraphicalRepresentation<?> graphicalRepresentation;

	private boolean initialized = false;
	private StringEncoder encoder;
	private ScreenshotImage screenshotImage;
	private File expectedScreenshotImageFile = null;
	private boolean screenshotModified = false;

	public static DiagramPalette newDiagramPalette(DiagramSpecification diagramSpecification, String diagramPaletteName,
			DrawingGraphicalRepresentation<?> graphicalRepresentation, ViewPointLibrary viewPointLibrary) {
		DiagramPaletteResource edRes = DiagramPaletteResourceImpl.makeDiagramPaletteResource(diagramSpecification.getResource(),
				diagramPaletteName, viewPointLibrary);
		DiagramPalette diagramPalette = new DiagramPalette(null);
		diagramPalette.setGraphicalRepresentation(graphicalRepresentation);
		diagramPalette.init(diagramSpecification, diagramPaletteName);
		edRes.setResourceData(diagramPalette);
		diagramPalette.setResource(edRes);
		diagramPalette.save();
		return diagramPalette;
	}

	// Used during deserialization, do not use it
	public DiagramPalette(DiagramPaletteBuilder builder) {
		super(builder);
		_elements = new Vector<DiagramPaletteElement>();
		if (builder != null) {
			builder.diagramPalette = this;
			diagramSpecification = builder.diagramSpecification;
			resource = builder.resource;
		}
	}

	@Override
	public DiagramPalette getPalette() {
		return this;
	}

	public void init(DiagramSpecification diagramSpecification, String diagramPaletteName) {
		setName(diagramPaletteName);
		this.diagramSpecification = diagramSpecification;
		// xmlFile = paletteFile;
		logger.info("Registering diagram palette for viewpoint " + getViewPoint().getName());
		relativePathFileConverter = new RelativePathFileConverter(getViewPoint().getResource().getDirectory());
		tryToLoadScreenshotImage();
		initialized = true;
	}

	@Override
	public DiagramSpecification getVirtualModel() {
		return diagramSpecification;
	}

	public DiagramSpecification getDiagramSpecification() {
		return diagramSpecification;
	}

	@Override
	public void delete() {
		if (getVirtualModel() != null) {
			getVirtualModel().removeFromPalettes(this);
		}
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
		return "DiagramPalette:" + (getVirtualModel() != null ? getVirtualModel().getName() : "null");
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
		if (!isDeserializing() && getResource() != null && getResource().getFile() != null
				&& !getResource().getFile().getName().startsWith(name)) {
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
	public ViewPointLibrary getViewPointLibrary() {
		return getVirtualModel().getViewPointLibrary();
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
		notifyObservers(new DiagramPaletteElementInserted(obj, this));
	}

	public boolean removeFromElements(DiagramPaletteElement obj) {
		obj.setPalette(null);
		boolean returned = _elements.remove(obj);
		setChanged();
		notifyObservers(new DiagramPaletteElementRemoved(obj, this));
		return returned;
	}

	public DrawingGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(DrawingGraphicalRepresentation<?> graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

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

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null && getResource() != null) {
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
		if (getExpectedScreenshotImageFile() != null && getExpectedScreenshotImageFile().exists()) {
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

	@Override
	public BindingModel getBindingModel() {
		return getVirtualModel().getBindingModel();
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
