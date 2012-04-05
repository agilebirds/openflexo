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
package org.openflexo.foundation.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.openflexo.foundation.FlexoLinks;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;
import org.xml.sax.SAXException;

/**
 * Utility class allowing to perform efficient access to XML mappings. This class additionnaly handles versions of those mappings
 * 
 * @author sguerin
 * 
 */
public class FlexoXMLMappings {

	protected static final Logger logger = Logger.getLogger(FlexoXMLMappings.class.getPackage().getName());

	// private boolean isInitialized = false;

	private ModelVersions modelVersions = null;

	public FlexoXMLMappings() {
		initialize();
	}

	// Public API

	public XMLMapping getRMMapping() {
		return getMappingForClass(FlexoProject.class);
	}

	public XMLMapping getLinksMapping() {
		return getMappingForClass(FlexoLinks.class);
	}

	public XMLMapping getWKFMapping() {
		return getMappingForClass(FlexoProcess.class);
	}

	public XMLMapping getWorkflowMapping() {
		return getMappingForClass(FlexoWorkflow.class);
	}

	public XMLMapping getIEMapping() {
		return getMappingForClass(IEWOComponent.class);
	}

	public XMLMapping getDMMapping() {
		return getMappingForClass(DMModel.class);
	}

	public XMLMapping getComponentLibraryMapping() {
		return getMappingForClass(FlexoComponentLibrary.class);
	}

	public XMLMapping getShemaLibraryMapping() {
		return getMappingForClass(ViewLibrary.class);
	}

	public XMLMapping getShemaMapping() {
		return getMappingForClass(View.class);
	}

	public XMLMapping getGeneratedCodeMapping() {
		return getMappingForClass(GeneratedOutput.class);
	}

	public XMLMapping getGeneratedSourcesMapping() {
		return getMappingForClass(GeneratedSources.class);
	}

	public XMLMapping getImplementationModelMapping() {
		return getMappingForClass(ImplementationModel.class);
	}

	public XMLMapping getNavigationMenuMapping() {
		return getMappingForClass(FlexoNavigationMenu.class);
	}

	public XMLMapping getTOCMapping() {
		return getMappingForClass(TOCData.class);
	}

	public XMLMapping getWSMapping() {
		return getMappingForClass(FlexoWSLibrary.class);
	}

	/**
	 * Returns all available versions for given class, ordered in ascendant order
	 */
	public FlexoVersion[] getAvailableVersionsForClass(Class aClass) {
		if (modelVersions != null) {
			ClassModels modelsForClass = modelVersions.classModels.get(aClass.getName());
			if (modelsForClass != null) {
				return modelsForClass.getAvailableVersions();
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find latest model file version for class " + aClass.getName());
		}
		return null;
	}

	public XMLMapping getMappingForClass(Class aClass) {
		return getMappingForClassAndVersion(aClass, getLatestVersionForClass(aClass));
	}

	public XMLMapping getMappingForClassAndVersion(Class aClass, FlexoVersion version) {
		ClassModelVersion cmv = getClassModelVersion(aClass, version);
		if (cmv != null) {
			return cmv.getMapping();
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find model file for class " + aClass.getName() + " and version " + version);
		}
		return null;
	}

	public ClassModels getModelsForClass(Class aClass) {
		return modelVersions.classModels.get(aClass.getName());
	}

	public ClassModelVersion getClassModelVersion(Class aClass, FlexoVersion version) {
		if (modelVersions != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Searching ClassModelFlexoVersion for class " + aClass.getName());
			}
			ClassModels modelsForClass = getModelsForClass(aClass);
			if (modelsForClass != null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.finest("Searching ClassModelFlexoVersion for version " + version);
				}
				ClassModelVersion foundFlexoVersion = modelsForClass.classModelVersions.get(version);
				if (foundFlexoVersion != null) {
					return foundFlexoVersion;
				}
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find model file for class " + aClass.getName() + " and version " + version);
		}
		return null;
	}

	public FlexoVersion getLatestVersionForClass(Class aClass) {
		if (modelVersions != null && aClass != null) {
			ClassModels modelsForClass = modelVersions.classModels.get(aClass.getName());
			if (modelsForClass != null) {
				return modelsForClass.latestVersion;
			}
		}
		return null;
	}

	public FlexoVersion getVersionForClassAndRelease(Class aClass, FlexoVersion releaseVersion) {
		if (modelVersions != null && aClass != null) {
			ReleaseModels modelsForRelease = modelVersions.releaseModels.get(releaseVersion);
			if (modelsForRelease != null) {
				if (modelsForRelease.classModels.get(aClass.getName()) != null) {
					return modelsForRelease.classModels.get(aClass.getName()).version;
				}
			}
		}
		return null;
	}

	private static final Vector<FlexoVersion> _releaseVersions = new Vector<FlexoVersion>();

	public static Vector<FlexoVersion> getReleaseVersions() {
		if (_releaseVersions.size() == 0) {
			for (ReleaseModels rm : new FlexoXMLMappings().modelVersions.releaseModels.values()) {
				_releaseVersions.add(rm.identifier);
			}
			Collections.sort(_releaseVersions, FlexoVersion.comparator);
		}
		return _releaseVersions;
	}

	public static final FlexoVersion latestRelease() {
		return getReleaseVersions().lastElement();
	}

	private static XMLMapping getVersionningModel() {
		// File flexoFoundationDirectory = getFlexoFoundationDirectory();
		// File versionningModelFile = new File (flexoFoundationDirectory,
		// "Models/FlexoVersionningModel.xml");
		File versionningModelFile = new FileResource("Models/VersionningModel.xml");
		try {
			return new XMLMapping(versionningModelFile);
		} catch (InvalidModelException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (IOException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (SAXException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		}
		return null;
	}

	public void initialize() {
		// We use here a dedicated and resetted String Converter
		// Fix a bug where relative path converter overrided File converter, and causing big issues
		StringEncoder stringEncoder = new StringEncoder();
		stringEncoder._addConverter(FlexoVersion.converter);

		// Register all declared string converters
		// FlexoObject.initialize(true);

		// The next line ensure that the FlexoVersion converter is well registered by XMLCoDe
		// FlexoVersion registerMyConverter = new FlexoVersion("1.0");
		// registerMyConverter.toString();
		// File flexoFoundationDirectory = getFlexoFoundationDirectory();
		// File modelFlexoVersionFile = new File (flexoFoundationDirectory,
		// "Models/ModelFlexoVersions.xml");

		File modelFlexoVersionFile = new FileResource("Models/ModelVersions.xml");
		try {
			modelVersions = (ModelVersions) XMLDecoder.decodeObjectWithMappingAndStringEncoder(new FileInputStream(modelFlexoVersionFile),
					getVersionningModel(), stringEncoder);
			Iterator<ClassModels> i = modelVersions.classModels.values().iterator();
			while (i.hasNext()) {
				ClassModels cm = i.next();
				if (cm.getAvailableVersions().length == 0) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("There are no available versions for " + cm.name);
					}
					continue;
				}
				FlexoVersion biggest = cm.getAvailableVersions()[0];
				for (int j = 0; j < cm.availableFlexoVersions.length; j++) {
					FlexoVersion v = cm.availableFlexoVersions[j];
					if (v.isGreaterThan(biggest)) {
						biggest = v;
					}
				}
				if (!cm.latestVersion.equals(biggest)) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("The latest version is " + cm.latestVersion + " but the greatest version is " + biggest
								+ " for class model named " + cm.name);
					}
				}
			}
		} catch (InvalidXMLDataException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (InvalidObjectSpecificationException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (JDOMException e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		} catch (IOException e) {
			// Warns about the exception
			logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.FINE)) {
			for (ReleaseModels release : modelVersions.releaseModels.values()) {
				logger.fine("Release " + release.identifier);
				for (ReleaseClassModel cm : release.classModels.values()) {
					logger.fine("Class " + cm.name + " version: " + cm.version);
				}
			}
		}
	}

	public static class ModelVersions implements XMLSerializable {
		/**
		 * Hashtable where are stored ClassModels objects related to key ClassModels.className
		 */
		public Hashtable<String, ClassModels> classModels = new Hashtable<String, ClassModels>();

		/**
		 * Hashtable where are stored ClassModels objects related to key ReleaseModels.identifier
		 */
		public Hashtable<FlexoVersion, ReleaseModels> releaseModels = new Hashtable<FlexoVersion, ReleaseModels>();
	}

	public static class ReleaseModels implements XMLSerializable {
		public FlexoVersion identifier;

		/**
		 * Hashtable where are stored ReleaseClassModel objects related to key ReleaseClassModel.name
		 */
		public Hashtable<String, ReleaseClassModel> classModels = new Hashtable<String, ReleaseClassModel>();
	}

	public static class ReleaseClassModel implements XMLSerializable {
		public String name;

		public FlexoVersion version;
	}

	public static class ClassModels implements XMLSerializable {
		public String name;

		public FlexoVersion latestVersion;

		protected FlexoVersion[] availableFlexoVersions;

		/**
		 * Hashtable where are stored ClassModelFlexoVersion objects related to key ClassModelFlexoVersion.id
		 */
		public Hashtable<String, ClassModelVersion> classModelVersions = new Hashtable<String, ClassModelVersion>();

		/**
		 * Return all available versions, ordered in ascendant order
		 * 
		 * @return
		 */
		public FlexoVersion[] getAvailableVersions() {
			if (availableFlexoVersions == null) {
				Vector<FlexoVersion> availableFlexoVersionsVector = new Vector<FlexoVersion>();
				for (Enumeration<ClassModelVersion> e = classModelVersions.elements(); e.hasMoreElements();) {
					ClassModelVersion next = e.nextElement();
					availableFlexoVersionsVector.add(next.version);
				}
				Collections.sort(availableFlexoVersionsVector, FlexoVersion.comparator);
				availableFlexoVersions = new FlexoVersion[availableFlexoVersionsVector.size()];
				for (int i = 0; i < availableFlexoVersionsVector.size(); i++) {
					availableFlexoVersions[i] = availableFlexoVersionsVector.elementAt(i);
				}
			}
			return availableFlexoVersions;
		}

	}

	public static class ClassModelVersion implements XMLSerializable {
		public FlexoVersion version;

		public File modelFile;

		public boolean needsManualConversion = false;

		public FlexoVersion toVersion = null;

		private XMLMapping mapping = null;

		protected File getModelFile() {
			// return new
			// File(getFlexoFoundationDirectory(),"Models"+File.separator+modelFile.getPath());
			return new FileResource("Models/" + modelFile.getPath());
		}

		@Override
		public String toString() {
			return "ClassModelVersion: file " + modelFile + " version " + version;
		}

		public XMLMapping getMapping() {
			if (mapping == null) {
				if (getModelFile().exists() && !getModelFile().isDirectory()) {
					try {
						mapping = new XMLMapping(getModelFile());
					} catch (InvalidModelException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Model version " + version + ", exception raised: " + e.getClass().getName()
									+ ". See console for details. (File is " + getModelFile().getAbsolutePath() + ")");
						}
						e.printStackTrace();
					} catch (IOException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.(File is "
									+ getModelFile().getAbsolutePath() + ")\"");
						}
						e.printStackTrace();
					} catch (SAXException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.(File is "
									+ getModelFile().getAbsolutePath() + ")\"");
						}
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.(File is "
									+ getModelFile().getAbsolutePath() + ")\"");
						}
						e.printStackTrace();
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Model file " + getModelFile().getAbsolutePath() + " doesn't exist or is not a File");
					}
				}
			}
			return mapping;
		}
	}

	/**
	 * @return
	 */
	public XMLMapping getDKVMapping() {
		return getMappingForClass(DKVModel.class);
	}

	private static final ClassModelVersion rmTSModelVersion = new ClassModelVersion() {
		@Override
		protected File getModelFile() {
			return new FileResource("Models/RMModel/RMModel_TS.xml");
		}
	};

	public static XMLMapping getRMTSMapping() {
		return rmTSModelVersion.getMapping();
	}

	public static ClassModelVersion getRMTSModelVersion() {
		return rmTSModelVersion;
	}

}
