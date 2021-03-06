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

import org.jdom2.JDOMException;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.gen.ScreenshotGenerator;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteElementRemoved;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.external.ExternalCEDModule;
import org.openflexo.module.external.IModuleLoader;
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
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class ViewPointPalette extends ViewPointObject implements Comparable<ViewPointPalette> {

	static final Logger logger = Logger.getLogger(ViewPointPalette.class.getPackage().getName());

	private static IModuleLoader moduleLoader;

	private int index;

	private String name;
	private String description;
	private Vector<ViewPointPaletteElement> _elements;

	private ViewPoint _viewPoint;

	private File _paletteFile;
	private RelativePathFileConverter relativePathFileConverter;

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation here is a org.openflexo.fge.DrawingGraphicalRepresentation.
	private Object graphicalRepresentation;

	public static IModuleLoader getModuleLoader() {
		return moduleLoader;
	}

	public static void setModuleLoader(IModuleLoader moduleLoader) {
		ViewPointPalette.moduleLoader = moduleLoader;
	}

	public static ViewPointPalette instanciateCalcPalette(ViewPoint calc, File paletteFile) {
		if (paletteFile.exists()) {
			FileInputStream inputStream = null;
			try {
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(paletteFile.getParentFile());
				inputStream = new FileInputStream(paletteFile);
				logger.info("Loading file " + paletteFile.getAbsolutePath());
				ViewPointBuilder builder = new ViewPointBuilder((ImportedOntology) calc.getViewpointOntology());
				ViewPointPalette returned = (ViewPointPalette) XMLDecoder.decodeObjectWithMapping(inputStream, calc.getViewPointLibrary()
						.get_VIEW_POINT_PALETTE_MODEL(), builder, new StringEncoder(StringEncoder.getDefaultInstance(),
						relativePathFileConverter));
				logger.info("Loaded file " + paletteFile.getAbsolutePath());
				returned.init(calc, paletteFile);
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
	}

	public static ViewPointPalette newCalcPalette(ViewPoint calc, File paletteFile, Object graphicalRepresentation) {
		ViewPointPalette palette = new ViewPointPalette(null);
		palette.setIndex(calc.getPalettes().size());
		palette.setGraphicalRepresentation(graphicalRepresentation);
		palette.init(calc, paletteFile);
		return palette;
	}

	// Used during deserialization, do not use it
	public ViewPointPalette(ViewPointBuilder builder) {
		super(builder);
		_elements = new Vector<ViewPointPaletteElement>();
		if (builder != null) {
			_viewPoint = builder.getViewPoint();
		}
	}

	private boolean initialized = false;

	private void init(ViewPoint calc, File paletteFile) {
		if (StringUtils.isEmpty(name)) {
			name = paletteFile.getName().substring(0, paletteFile.getName().length() - 8);
		}
		_viewPoint = calc;
		_paletteFile = paletteFile;
		logger.info("Registering calc palette for calc " + calc.getName());
		relativePathFileConverter = new RelativePathFileConverter(calc.getViewPointDirectory());
		tryToLoadScreenshotImage();
		initialized = true;
	}

	@Override
	public void delete() {
		if (getViewPoint() != null) {
			getViewPoint().removeFromCalcPalettes(this);
		}
		logger.info("Deleting file " + _paletteFile);
		_paletteFile.delete();
		super.delete();
		deleteObservers();
	}

	@Override
	public String getClassNameKey() {
		return "calc_palette";
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) throws Exception {
		super.setName(name);
		if (_paletteFile != null && !_paletteFile.getName().startsWith(name)) {
			FileUtils.rename(_paletteFile, new File(_paletteFile.getParentFile(), name + ".palette"));
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getViewPoint() {
		return _viewPoint;
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return getViewPoint().getViewPointLibrary();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.CALC_PALETTE_INSPECTOR;
	}

	public Vector<ViewPointPaletteElement> getElements() {
		return _elements;
	}

	public void setElements(Vector<ViewPointPaletteElement> elements) {
		_elements = elements;
	}

	public void addToElements(ViewPointPaletteElement obj) {
		obj.setPalette(this);
		_elements.add(obj);
		setChanged();
		notifyObservers(new CalcPaletteElementInserted(obj, this));
	}

	public boolean removeFromElements(ViewPointPaletteElement obj) {
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

	@Override
	public void saveToFile(File aFile) {
		super.saveToFile(aFile);
		clearIsModified(true);
	}

	public void save() {
		logger.info("Saving palette to " + _paletteFile.getAbsolutePath() + "...");

		File dir = _paletteFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temporaryFile = null;
		try {
			makeLocalCopy();
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			saveToFile(temporaryFile);
			FileUtils.rename(temporaryFile, _paletteFile);
			clearIsModified(true);
			buildAndSaveScreenshotImage();
			logger.info("Saved palette to " + _paletteFile.getAbsolutePath() + ". Done.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not save palette to " + _paletteFile.getAbsolutePath());
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
		}
	}

	private void makeLocalCopy() throws IOException {
		if (_paletteFile != null && _paletteFile.exists()) {
			String localCopyName = _paletteFile.getName() + "~";
			File localCopy = new File(_paletteFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(_paletteFile, localCopy);
		}
	}

	public ViewPointPaletteElement addPaletteElement(String name, Object graphicalRepresentation) {
		ViewPointPaletteElement newElement = new ViewPointPaletteElement(null);
		newElement.setName(name);
		newElement.setGraphicalRepresentation((ShapeGraphicalRepresentation<?>) graphicalRepresentation);
		addToElements(newElement);
		return newElement;
	}

	@Override
	public int compareTo(ViewPointPalette o) {
		return index - o.index;
	}

	public File getPaletteFile() {
		return _paletteFile;
	}

	private ScreenshotImage screenshotImage;

	private File expectedScreenshotImageFile = null;

	private File getExpectedScreenshotImageFile() {
		if (expectedScreenshotImageFile == null) {
			expectedScreenshotImageFile = new File(getPaletteFile().getParentFile(), getName() + ".palette.png");
		}
		return expectedScreenshotImageFile;
	}

	private ScreenshotImage buildAndSaveScreenshotImage() {
		ExternalCEDModule cedModule = null;
		try {
			cedModule = getModuleLoader() != null ? getModuleLoader().getVPMModuleInstance() : null;
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

}
