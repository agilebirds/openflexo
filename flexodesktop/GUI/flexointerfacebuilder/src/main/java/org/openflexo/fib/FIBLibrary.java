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
package org.openflexo.fib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class FIBLibrary {

	static final Logger logger = Logger.getLogger(FIBLibrary.class.getPackage().getName());

	protected static XMLMapping _fibMapping;
	private static FIBLibrary _current;

	private Map<String, FIBComponent> _fibDefinitions;
	private Map<File, FIBComponent> fibFileDefinitions;

	private BindingFactory bindingFactory = new JavaBindingFactory();

	private FIBLibrary() {
		super();
		_fibDefinitions = new Hashtable<String, FIBComponent>();
		fibFileDefinitions = new Hashtable<File, FIBComponent>();
	}

	protected static FIBLibrary createInstance() {
		_current = new FIBLibrary();

		StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);
		StringEncoder.getDefaultInstance()._addConverter(ComponentConstraints.CONVERTER);

		return _current;
	}

	public static FIBLibrary instance() {
		if (_current == null) {
			createInstance();
		}
		return _current;
	}

	public static boolean hasInstance() {
		return _current != null;
	}

	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	public static XMLMapping getFIBMapping() {
		if (_fibMapping == null) {
			// File mappingFile = new File
			// ("../FlexoInspector/Models/InspectorModel.xml");
			File mappingFile = new FileResource("Models/FIBModel.xml");
			if (!mappingFile.exists()) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not find file: " + mappingFile.getAbsolutePath());
				}
			}
			try {
				_fibMapping = new XMLMapping(mappingFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Exception raised: " + e + " for file " + mappingFile.getAbsolutePath() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Exception raised: " + e.getClass().getName() + " for file " + mappingFile.getAbsolutePath()
							+ ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return _fibMapping;
	}

	public boolean componentIsLoaded(File fibFile) {
		return fibFileDefinitions.get(fibFile) != null;
	}

	public boolean componentIsLoaded(String fibResourcePath) {
		return _fibDefinitions.get(fibResourcePath) != null;
	}

	public FIBComponent retrieveFIBComponent(File fibFile) {
		return retrieveFIBComponent(fibFile, true);
	}

	public FIBComponent retrieveFIBComponent(File fibFile, boolean useCache) {
		FIBComponent fibComponent = fibFileDefinitions.get(fibFile);
		if (!useCache || fibComponent == null || fibComponent.getLastModified().getTime() < fibFile.lastModified()) {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Load " + fibFile.getAbsolutePath());
			}
			RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(fibFile.getParentFile());
			Converter<File> previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
			StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);

			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(fibFile);
				FIBComponent component = loadFIBComponent(inputStream);
				component.setLastModified(new Date(fibFile.lastModified()));
				component.setDefinitionFile(fibFile.getAbsolutePath());
				fibFileDefinitions.put(fibFile, component);
				return component;
			} catch (FileNotFoundException e) {
				logger.warning("Not found: " + fibFile.getAbsolutePath());
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (JDOMException e) {
				e.printStackTrace();
				return null;
			} finally {
				IOUtils.closeQuietly(inputStream);
				StringEncoder.getDefaultInstance()._addConverter(previousConverter);
			}
		}
		return fibComponent;
	}

	public void removeFIBComponentFromCache(File fibFile) {
		fibFileDefinitions.remove(fibFile);
	}

	public FIBComponent retrieveFIBComponent(String fibResourcePath) {
		return retrieveFIBComponent(fibResourcePath, true);
	}

	public FIBComponent retrieveFIBComponent(String fibResourcePath, boolean useCache) {
		InputStream inputStream = getClass().getResourceAsStream(fibResourcePath);
		try {
			return retrieveFIBComponent(fibResourcePath, inputStream, useCache);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private FIBComponent retrieveFIBComponent(String fibIdentifier, InputStream inputStream, boolean useCache) {
		if (!useCache || _fibDefinitions.get(fibIdentifier) == null) {

			try {
				if (getFIBMapping() != null) {
					FIBComponent fibComponent = loadFIBComponent(inputStream);
					fibComponent.setDefinitionFile(fibIdentifier);
					_fibDefinitions.put(fibIdentifier, fibComponent);
				}
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			}
		}
		return _fibDefinitions.get(fibIdentifier);
	}

	private FIBComponent loadFIBComponent(InputStream inputStream) throws IOException, JDOMException {
		return (FIBComponent) XMLDecoder.decodeObjectWithMapping(inputStream, getFIBMapping(), this);
	}

	public static void save(FIBComponent component, File file) {
		logger.info("Save to file " + file.getAbsolutePath());

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			saveComponentToStream(component, file, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}

	}

	public static void saveComponentToStream(FIBComponent component, File file, OutputStream stream) {
		RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(file.getParentFile());
		XMLCoder coder = new XMLCoder(getFIBMapping(), new StringEncoder(StringEncoder.getDefaultInstance(), relativePathFileConverter));
		try {
			coder.encodeObject(component, stream);
			logger.info("Succeeded to save: " + file);
			// System.out.println("> "+coder.encodeObject(component));
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
