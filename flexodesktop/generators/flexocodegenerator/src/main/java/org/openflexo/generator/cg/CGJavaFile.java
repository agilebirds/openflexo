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
package org.openflexo.generator.cg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.ModelReinjectableFile;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.MethodReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference.PropertyReference;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.JavaFile;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.foundation.xml.GeneratedCodeBuilder;
import org.openflexo.generator.FlexoComponentResourceGenerator;
import org.openflexo.generator.FlexoJavaResourceGenerator;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.generator.rm.ComponentJavaFileResource;
import org.openflexo.javaparser.FJPDMMapper;
import org.openflexo.javaparser.FJPDMSet;
import org.openflexo.javaparser.FJPDMSet.FJPPackageReference.FJPClassReference;
import org.openflexo.javaparser.FJPJavaClass;
import org.openflexo.javaparser.FJPJavaParseException;
import org.openflexo.javaparser.FJPJavaSource;
import org.openflexo.javaparser.FJPTypeResolver;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;

import com.thoughtworks.qdox.parser.ParseException;

public class CGJavaFile extends AbstractCGFile implements ModelReinjectableFile {

	private static final Logger logger = Logger.getLogger(CGJavaFile.class.getPackage().getName());

	public CGJavaFile(GeneratedCodeBuilder builder) {
		this(builder.generatedCode);
		initializeDeserialization(builder);
	}

	public CGJavaFile(GeneratedOutput generatedCode) {
		super(generatedCode);
	}

	public CGJavaFile(CGRepository repository, JavaFileResource resource) {
		super(repository, resource);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.CG.CG_JAVA_FILE_INSPECTOR;
	}

	@Override
	public JavaFileResource getResource() {
		return (JavaFileResource) super.getResource();
	}

	private FJPJavaSource _parsedJavaSource;
	private FJPJavaParseException _parseException;

	public FJPJavaSource getParsedJavaSource() {
		if (_parsedJavaSource == null && _parseException == null && getGeneratedResourceData() != null
				&& getGeneratedResourceData().hasCurrentDiskContent()) {
			try {
				// Date date0 = new Date();
				String currentDiskContent = getGeneratedResourceData().getCurrentDiskContent();
				String sourceName = getResource().getFileName();
				// Date date1 = new Date();
				_parsedJavaSource = new FJPJavaSource(sourceName, currentDiskContent, getProject().getDataModel().getClassLibrary());
				// Date date2 = new Date();
				// logger.info("Time for computing getParsedJavaSource() "+getFileName()+": "+(date2.getTime()-date1.getTime())+" ms (total="+(date2.getTime()-date0.getTime())+" ms)");
			} catch (ParseException e) {
				logger.info("Parse error");
				_parseException = new FJPJavaParseException(getResource().getFileName(), e);
			}
		}
		return _parsedJavaSource;
	}

	public FJPJavaParseException getParseException() {
		getParsedJavaSource();
		return _parseException;
	}

	@Override
	public void writeModifiedFile() throws SaveResourceException, FlexoException {
		Date date1 = new Date();
		super.writeModifiedFile();
		notifyJavaFileStructureChanged();
		if (supportModelReinjection() && !isMarkedForDeletion()) {
			Date date3 = new Date();
			saveKnownJavaStructure();
			Date date4 = new Date();
			logger.info("Time for CGJavaFile saveKnownJavaStructure() " + getFileName() + ": " + (date4.getTime() - date3.getTime())
					+ " ms");
		}
		Date date2 = new Date();
		logger.info("Total time for CGJavaFile writeModifiedFile() " + getFileName() + ": " + (date2.getTime() - date1.getTime()) + " ms");
	}

	@Override
	public void save() throws SaveResourceException {
		super.save();
		notifyJavaFileStructureChanged();
	}

	@Override
	public void notifyResourceChangedOnDisk() {
		super.notifyResourceChangedOnDisk();
		notifyJavaFileStructureChanged();
	}

	private void notifyJavaFileStructureChanged() {
		_parsedJavaSource = null;
		_parseException = null;
		setChanged();
		notifyObservers(new JavaFileStructureChanged(this));
	}

	public static class JavaFileStructureChanged extends CGDataModification {
		public JavaFileStructureChanged(CGJavaFile file) {
			super(null, file);
		}
	}

	@Override
	public DMEntity getModelEntity() {
		if (getGenerator() == null) {
			return null;
		}
		if (getGenerator() instanceof FlexoComponentResourceGenerator) {
			return ((FlexoComponentResourceGenerator) getGenerator()).getEntity();
		}
		if (getGenerator() instanceof FlexoJavaResourceGenerator) {
			return ((FlexoJavaResourceGenerator) getGenerator()).getEntity();
		}
		return null;
	}

	@Override
	public boolean supportModelReinjection() {
		if (getGenerator() == null) {
			return false;
		}
		if (getModelEntity() == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean needsModelReinjection() {
		if (!supportModelReinjection()) {
			return false;
		}
		return getResource().getLastModelReinjectingDate().before(getResource().getDiskLastModifiedDate());
	}

	public Date getLastModelReinjectingDate() {
		if (getResource() != null && getResource().getResourceFile() != null) {
			return getResource().getLastModelReinjectingDate();
		}
		return null;
	}

	public String getLastModelReinjectingDateAsString() {
		if (getLastModelReinjectingDate() != null) {
			if (getLastModelReinjectingDate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastModelReinjectingDate());
		}
		return "???";
	}

	/**
	 * This method is called whenever we have to store known Java model. Since known properties and methods are stored in the data model, we
	 * juste have to store here properties and methods that we explicitely choose to ignore. At this point we just do a comparison between
	 * parsed java source and related entity to store the delta.
	 * 
	 */
	private void saveKnownJavaStructure() {
		if (supportModelReinjection()) {
			if (_propertiesKnownAndIgnored == null) {
				_propertiesKnownAndIgnored = new Vector<String>();
			}
			if (_methodsKnownAndIgnored == null) {
				_methodsKnownAndIgnored = new Vector<String>();
			}
			_propertiesKnownAndIgnored.clear();
			_methodsKnownAndIgnored.clear();
			File javaStructureFile = getResource().getJavaModelFile();
			StringBuffer contentToWriteOnDisk = new StringBuffer();
			// Date date1 = new Date();
			if (getModelEntity() == null || getParsedJavaSource() == null) {
				return;
			}
			FJPDMSet parsedJavaStructure = new FJPDMSet(getProject(), "java_structure", getParsedJavaSource(), getModelEntity());
			// Date date2 = new Date();
			// logger.info("Time for parsedJavaStructure "+getFileName()+": "+(date2.getTime()-date1.getTime())+" ms");
			FJPClassReference classReference = parsedJavaStructure.getClassReference(getParsedJavaSource().getRootClass());
			for (PropertyReference propertyReference : classReference.getProperties()) {
				if (propertyReference.isNewlyDiscovered()) {
					_propertiesKnownAndIgnored.add(propertyReference.getName());
					contentToWriteOnDisk.append(propertyReference.getName() + "\n");
				}
			}
			for (MethodReference methodReference : classReference.getMethods()) {
				if (methodReference.isNewlyDiscovered()) {
					_methodsKnownAndIgnored.add(methodReference.getSignature());
					contentToWriteOnDisk.append(methodReference.getSignature() + "\n");
				}
			}
			try {
				FileUtils.saveToFile(javaStructureFile, contentToWriteOnDisk.toString());
			} catch (IOException e) {
				logger.warning("Could not write " + javaStructureFile + " reason " + e.getMessage());
				e.printStackTrace();
			}
			javaStructureNeedsToBeReloaded = false;
		}
	}

	private void loadJavaStructure() {
		javaStructureNeedsToBeReloaded = false;
		if (_propertiesKnownAndIgnored == null) {
			_propertiesKnownAndIgnored = new Vector<String>();
		}
		if (_methodsKnownAndIgnored == null) {
			_methodsKnownAndIgnored = new Vector<String>();
		}
		_propertiesKnownAndIgnored.clear();
		_methodsKnownAndIgnored.clear();
		if (getResource().getJavaModelFile().exists()) {
			try {
				String content = FileUtils.fileContents(getResource().getJavaModelFile());
				BufferedReader rdr = new BufferedReader(new StringReader(content));
				for (;;) {
					String line = null;
					try {
						line = rdr.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (line == null) {
						break;
					}
					if (line.indexOf("(") >= 0) {
						_methodsKnownAndIgnored.add(line);
					} else {
						_propertiesKnownAndIgnored.add(line);
					}
				}
			} catch (IOException e) {
				logger.warning("Could not load " + getResource().getJavaModelFile() + " reason " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Vector<String> _propertiesKnownAndIgnored;
	private Vector<String> _methodsKnownAndIgnored;
	private boolean javaStructureNeedsToBeReloaded = true;

	@Override
	public JavaFile getGeneratedResourceData() {
		return (JavaFile) super.getGeneratedResourceData();
	}

	public Vector<String> getPropertiesKnownAndIgnored() {
		if (javaStructureNeedsToBeReloaded) {
			loadJavaStructure();
		}
		return _propertiesKnownAndIgnored;
	}

	public Vector<String> getMethodsKnownAndIgnored() {
		if (javaStructureNeedsToBeReloaded) {
			loadJavaStructure();
		}
		return _methodsKnownAndIgnored;
	}

	public void updateModel(FJPDMSet updateContext) throws FJPTypeResolver.CrossReferencedEntitiesException,
			FJPTypeResolver.UnresolvedTypeException {
		FJPJavaSource parsedSource = getParsedJavaSource();
		FJPJavaClass parsedClass = parsedSource.getRootClass();
		ClassReference classReference = updateContext.getClassReference(parsedClass);
		DMEntity entity = getModelEntity();
		if (classReference != null && classReference.isSelected() && entity != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Update " + getFileName() + " according to " + classReference);
			}
			Vector<DMObject> newObjects = parsedClass.update(entity, classReference, updateContext, parsedSource);
			updateIsTemplateStatus(newObjects);
			propagateModelReinjectionInRM(entity);
			getResource().setLastGenerationCheckedDate(new Date());
			getResource().setLastModelReinjectingDate(new Date());
			getResource().notifyResourceStatusChanged();
			saveKnownJavaStructure();
			// Silently regenerate file, and store it as LAST_GENERATED
			updateLastGeneratedContent();
		}
	}

	/**
	 * Maybe some explanation required here. Reinjection in model will result here in two concurrent changes: one in the model itself, and
	 * one resulting from the necessary previous acceptation. This situation will result as a conflict occurring in the next generation
	 * iteration (normally changes are both side the same and this conflict must be automatically resolved). The goal here is to avoid this
	 * situation, by asserting that reinjection is enough and round-trip for the related reinjected elements is no more necessary, so we
	 * just re-run silently the code generation, and put the result as the LAST_GENERATED (this will shortcut round-trip for the next
	 * generation, and thus prevent from a conflict to appear).
	 * 
	 * @param entity
	 * @param aClassReference
	 */
	private void updateLastGeneratedContent() {
		if (getGenerator() != null) {
			getGenerator().refreshConcernedResources();
			getGenerator().silentlyGenerateCode();
			String newContent = getResource().getCurrentGeneration();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Obtaining as new LAST_GENERATED: " + newContent);
			}
			try {
				getGeneratedResourceData().saveAsLastGenerated(newContent);
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("Unexpected exception: " + e);
			}
		}

	}

	private void updateIsTemplateStatus(Vector<DMObject> newObjects) {
		FJPJavaSource pureGenerationSource;
		if (getGeneratedResourceData() != null && getGeneratedResourceData().getCurrentGeneration() != null) {
			try {
				String pureGeneration = getGeneratedResourceData().getCurrentGeneration();
				String sourceName = getResource().getFileName();
				pureGenerationSource = new FJPJavaSource(sourceName, pureGeneration, getProject().getDataModel().getClassLibrary());
				FJPJavaClass parsedClass = pureGenerationSource.getRootClass();
				Vector<String> excludedSignatures = new Vector<String>();
				Vector<DMProperty> parsedProperties = FJPDMMapper.searchForProperties(parsedClass, getProject().getDataModel(), null,
						pureGenerationSource, true, false, excludedSignatures);
				Vector<DMMethod> parsedMethods = FJPDMMapper.searchForMethods(parsedClass, getProject().getDataModel(), null,
						pureGenerationSource, false, excludedSignatures);
				for (DMObject o : newObjects) {
					if (o instanceof DMProperty) {
						DMProperty newProperty = (DMProperty) o;
						boolean found = false;
						for (DMProperty p : parsedProperties) {
							if (p.getName().equals(newProperty.getName())) {
								found = true;
							}
						}
						if (!found) {
							logger.info("New property " + newProperty + " seems to be template-EXTERNAL");
						} else {
							logger.info("New property " + newProperty + " seems to be template-BUILT-IN");
						}
						newProperty.setIsStaticallyDefinedInTemplate(found);
					} else if (o instanceof DMMethod) {
						DMMethod newMethod = (DMMethod) o;
						boolean found = false;
						for (DMMethod m : parsedMethods) {
							if (m.getSignature().equals(newMethod.getSignature())) {
								found = true;
							}
						}
						if (!found) {
							logger.info("New method " + newMethod + " seems to be template-EXTERNAL");
						} else {
							logger.info("New method " + newMethod + " seems to be template-BUILT-IN");
						}
						newMethod.setIsStaticallyDefinedInTemplate(found);
					}
				}
			} catch (ParseException e) {
				logger.info("Parse error");
				_parseException = new FJPJavaParseException(getResource().getFileName(), e);
			}
		}
	}

	private void propagateModelReinjectionInRM(DMEntity entity) {
		if (getResource() instanceof ComponentJavaFileResource) {
			ComponentJavaFileResource res = (ComponentJavaFileResource) getResource();
			ComponentDefinition cd = res.getComponentDefinition();
			if (cd != null && cd.getComponentResource() != null) {
				try {
					cd.getComponentResource().backwardSynchronizeWith(entity.getDMModel().getFlexoResource());
				} catch (FlexoException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void clearParsingData() {
		_parsedJavaSource = null;
		_parseException = null;
	}

}
