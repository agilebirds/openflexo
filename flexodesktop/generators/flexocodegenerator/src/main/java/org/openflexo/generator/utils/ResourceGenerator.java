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
package org.openflexo.generator.utils;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;

/**
 * @author gpolet
 * 
 */
public class ResourceGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {

	private static final Logger logger = FlexoLogger.getLogger(ResourceGenerator.class.getPackage().getName());

	public static final String WO_PROJECT_RELATIVE_PATH = "woproject";
	private static final String SETTINGS_RELATIVE_PATH = ".settings";
	private static final String[] PATTERNSET_FILE_NAMES = new String[] { "classes.exclude.patternset", "classes.include.patternset",
			"resources.exclude.patternset", "resources.include.patternset", "wsresources.exclude.patternset",
			"wsresources.include.patternset", "ant.classpaths.wo.wolocalroot.manual", "ant.frameworks.wo.wosystemroot.manual" };

	private Hashtable<Language, LocalizedFileGenerator> _localizedGenerator;
	private final Hashtable<String, PrototypeProcessBusinessDataSamplesGenerator> _prototypeProcessSamplesGenerator;
	private final Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>> _driverGenerator;
	private final Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>> _eclipseFilesGenerator;
	private JarResourcesGenerator _jarResourcesGenerator;
	private WebServerResourcesGenerator _webResourcesGenerator;
	private HelpGenerator _helpGenerator;
	private PackagedResourceToCopyGenerator<CGRepository> settingsGenerator;
	private PrototypeProcessBusinessDataSamplesCreator samplesCreator;

	/**
     * 
     */
	public ResourceGenerator(ProjectGenerator prjGenerator) {
		super(prjGenerator, null);
		_localizedGenerator = new Hashtable<Language, LocalizedFileGenerator>();
		_driverGenerator = new Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>>();
		_eclipseFilesGenerator = new Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>>();
		_prototypeProcessSamplesGenerator = new Hashtable<String, PrototypeProcessBusinessDataSamplesGenerator>();
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	/**
     * 
     */
	@Override
	public void generate(boolean forceRegenerate) throws GenerationException {
		startGeneration();
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Generating resources");
		}
		getWebResourcesGenerator().generate(forceRegenerate);
		getJarResourcesGenerator().generate(forceRegenerate);
		if (getTarget() != CodeType.PROTOTYPE) {
			for (PackagedResourceToCopyGenerator generator : _driverGenerator.values()) {
				generator.generate(forceRegenerate);
			}
		} else {
			for (PrototypeProcessBusinessDataSamplesGenerator generator : _prototypeProcessSamplesGenerator.values()) {
				generator.generate(forceRegenerate);
			}
		}
		for (PackagedResourceToCopyGenerator generator : _eclipseFilesGenerator.values()) {
			generator.generate(forceRegenerate);
		}
		for (Language lg : getProject().getDKVModel().getLanguages()) {
			getGenerator(lg).generate(forceRegenerate);
		}
		if (!getRepository().includeReader()) {
			getHelpGenerator().generate(forceRegenerate);
		}
		settingsGenerator.generate(forceRegenerate);
		stopGeneration();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		Hashtable<Language, LocalizedFileGenerator> lgHash = new Hashtable<Language, LocalizedFileGenerator>();
		for (Language lg : getProject().getDKVModel().getLanguages()) {
			if (lg.getIsoCode() != null && lg.isoCodeIsUnique()) {
				LocalizedFileGenerator generator = getGenerator(lg);
				if (generator != null) {
					lgHash.put(lg, generator);
					generator.buildResourcesAndSetGenerators(repository, resources);
				}
			}
		}
		_localizedGenerator.clear();
		_localizedGenerator = lgHash;
		if (!repository.includeReader()) {
			getHelpGenerator().buildResourcesAndSetGenerators(repository, resources);
		}
		getWebResourcesGenerator().buildResourcesAndSetGenerators(repository, resources);
		getJarResourcesGenerator().buildResourcesAndSetGenerators(repository, resources);

		if (getTarget() != CodeType.PROTOTYPE) {
			FileResource[] drivers = getProject().getDataModel().findDriverFiles();
			if (drivers.length > 0) {
				for (FileResource wsRes : drivers) {
					PackagedResourceToCopyGenerator<CGRepository> generator = getGenerator(wsRes);
					if (wsRes == null || !wsRes.exists()) {
						continue;
					}
					refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + wsRes.getName(), false);
					if (generator != null) {
						generator.buildResourcesAndSetGenerators(repository, resources);
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not instanciate WebResourceGenerator for " + wsRes);
						}
					}
				}
			}
		} else {
			// Generate Process Instance samples csv files
			_prototypeProcessSamplesGenerator.clear();
			samplesCreator = null;
			for (String businessDataKey : getSamplesCreator().getAllBusinessDataKeys()) {
				refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + businessDataKey, false);
				PrototypeProcessBusinessDataSamplesGenerator generator = getGeneratorForPrototypeProcessSample(businessDataKey);
				generator.buildResourcesAndSetGenerators(repository, resources);
			}
		}

		for (int i = 0; i < PATTERNSET_FILE_NAMES.length; i++) {
			String s = PATTERNSET_FILE_NAMES[i];
			FileResource orig = new FileResource("Config/Generator/Resources/" + s);
			PackagedResourceToCopyGenerator<CGRepository> generator = getGeneratorForEclipseResource(orig);
			if (orig == null || !orig.exists()) {
				continue;
			}
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + orig.getName(), false);
			if (generator != null) {
				generator.buildResourcesAndSetGenerators(repository, resources);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate PackagedResourceGenerator for " + orig);
				}
			}
		}
		if (settingsGenerator == null) {
			FileResource orig = new FileResource("Config/Generator/Resources/org.eclipse.core.resources.prefs");

			settingsGenerator = new PackagedResourceToCopyGenerator<CGRepository>(getProjectGenerator(), FileFormat.UNKNOWN_BINARY_FILE,
					ResourceType.COPIED_FILE, orig, getRepository().getProjectSymbolicDirectory(), SETTINGS_RELATIVE_PATH);

			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + orig.getName(), false);
		}
		settingsGenerator.buildResourcesAndSetGenerators(repository, resources);

	}

	private LocalizedFileGenerator getGenerator(Language lg) {
		if (_localizedGenerator.get(lg) == null) {
			_localizedGenerator.put(lg, new LocalizedFileGenerator(getProjectGenerator(), lg));
		}
		return _localizedGenerator.get(lg);
	}

	private PrototypeProcessBusinessDataSamplesCreator getSamplesCreator() {
		if (samplesCreator == null) {
			samplesCreator = new PrototypeProcessBusinessDataSamplesCreator(getProject());
		}
		return samplesCreator;
	}

	private PrototypeProcessBusinessDataSamplesGenerator getGeneratorForPrototypeProcessSample(String businessDataKey) {
		if (_prototypeProcessSamplesGenerator.get(businessDataKey) == null) {
			_prototypeProcessSamplesGenerator.put(businessDataKey, new PrototypeProcessBusinessDataSamplesGenerator(getProjectGenerator(),
					businessDataKey, getSamplesCreator()));
		}
		return _prototypeProcessSamplesGenerator.get(businessDataKey);
	}

	private PackagedResourceToCopyGenerator<CGRepository> getGenerator(FileResource lg) {
		if (_driverGenerator.get(lg) == null) {
			_driverGenerator.put(lg, new PackagedResourceToCopyGenerator<CGRepository>(getProjectGenerator(),
					FileFormat.UNKNOWN_BINARY_FILE, ResourceType.COPIED_FILE, lg, getProjectGenerator().getRepository()
							.getLibSymbolicDirectory(), ""));
		}
		return _driverGenerator.get(lg);
	}

	private PackagedResourceToCopyGenerator<CGRepository> getGeneratorForEclipseResource(FileResource lg) {
		if (_eclipseFilesGenerator.get(lg) == null) {
			_eclipseFilesGenerator.put(lg, new PackagedResourceToCopyGenerator<CGRepository>(getProjectGenerator(),
					FileFormat.UNKNOWN_BINARY_FILE, ResourceType.COPIED_FILE, lg, getProjectGenerator().getRepository()
							.getProjectSymbolicDirectory(), "woproject"));
		}
		return _eclipseFilesGenerator.get(lg);
	}

	private HelpGenerator getHelpGenerator() {
		if (_helpGenerator == null) {
			_helpGenerator = new HelpGenerator(getProjectGenerator());
		}
		return _helpGenerator;
	}

	private WebServerResourcesGenerator getWebResourcesGenerator() {
		if (_webResourcesGenerator == null) {
			_webResourcesGenerator = new WebServerResourcesGenerator(getProjectGenerator());
		}
		return _webResourcesGenerator;
	}

	private JarResourcesGenerator getJarResourcesGenerator() {
		if (_jarResourcesGenerator == null) {
			_jarResourcesGenerator = new JarResourcesGenerator(getProjectGenerator());
		}
		return _jarResourcesGenerator;
	}

}
