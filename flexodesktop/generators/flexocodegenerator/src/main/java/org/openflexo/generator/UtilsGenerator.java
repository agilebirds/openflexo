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
package org.openflexo.generator;

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.cayenne.access.SQLGenerator;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.utils.AntUserFrameworks;
import org.openflexo.generator.utils.ApplicationConfGenerator;
import org.openflexo.generator.utils.ApplicationConfProdGenerator;
import org.openflexo.generator.utils.BuildPropertiesGenerator;
import org.openflexo.generator.utils.BuildXmlGenerator;
import org.openflexo.generator.utils.ConfigGenerator;
import org.openflexo.generator.utils.ConstantsGenerator;
import org.openflexo.generator.utils.CustomComponentGenerator;
import org.openflexo.generator.utils.DefaultApplicationConfGenerator;
import org.openflexo.generator.utils.DirectActionGenerator;
import org.openflexo.generator.utils.DotClasspathGenerator;
import org.openflexo.generator.utils.DotProjectGenerator;
import org.openflexo.generator.utils.HeaderFooterGenerator;
import org.openflexo.generator.utils.HelpPopupGenerator;
import org.openflexo.generator.utils.LocalizedStringGenerator;
import org.openflexo.generator.utils.PopupHeaderFooterGenerator;
import org.openflexo.generator.utils.ResourceGenerator;
import org.openflexo.generator.utils.UserServiceGenerator;
import org.openflexo.generator.utils.UtilGenerator;
import org.openflexo.generator.utils.WOApplicationGenerator;
import org.openflexo.generator.utils.WODirectActionGenerator;
import org.openflexo.generator.utils.WOMainGenerator;
import org.openflexo.generator.utils.WOSessionGenerator;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class UtilsGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {
	private static final Logger logger = FlexoLogger.getLogger(ResourceGenerator.class.getPackage().getName());

	private HeaderFooterGenerator headerFooterGenerator;
	private DirectActionGenerator directActionGenerator;
	private ConfigGenerator configGenerator;
	private ConstantsGenerator constantsGenerator;
	private UtilGenerator utilGenerator;
	private DefaultApplicationConfGenerator defaultApplicationConfGenerator;
	private ApplicationConfGenerator applicationConfGenerator;
	private ApplicationConfProdGenerator applicationConfProdGenerator;
	private DotClasspathGenerator dotClasspathGenerator;
	private DotProjectGenerator dotProjectGenerator;
	private BuildXmlGenerator buildXmlGenerator;
	private CustomComponentGenerator customComponentGenerator;
	private WOApplicationGenerator woApplicationGenerator;
	private WODirectActionGenerator woDirectActionGenerator;
	private WOMainGenerator woMainGenerator;
	private HelpPopupGenerator helpPopupGenerator;
	private LocalizedStringGenerator localizedStringGenerator;
	private WOSessionGenerator woSessionGenerator;
	private UserServiceGenerator userServiceGenerator;
	private SQLGenerator sqlGenerator;
	private SQLGenerator sqlGenerator2;
	private AntUserFrameworks antUserFrameworksGenerator;
	private BuildPropertiesGenerator buildPropertiesGenerator;
	private PopupHeaderFooterGenerator popupHeaderFooterGenerator;

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public UtilsGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null);

		headerFooterGenerator = new HeaderFooterGenerator(projectGenerator);
		popupHeaderFooterGenerator = new PopupHeaderFooterGenerator(projectGenerator);
		directActionGenerator = new DirectActionGenerator(projectGenerator);
		configGenerator = new ConfigGenerator(projectGenerator);
		utilGenerator = new UtilGenerator(projectGenerator);
		defaultApplicationConfGenerator = new DefaultApplicationConfGenerator(projectGenerator);
		applicationConfGenerator = new ApplicationConfGenerator(projectGenerator);
		applicationConfProdGenerator = new ApplicationConfProdGenerator(projectGenerator);
		dotClasspathGenerator = new DotClasspathGenerator(projectGenerator);
		dotProjectGenerator = new DotProjectGenerator(projectGenerator);
		buildXmlGenerator = new BuildXmlGenerator(projectGenerator);
		buildPropertiesGenerator = new BuildPropertiesGenerator(projectGenerator);
		customComponentGenerator = new CustomComponentGenerator(projectGenerator);
		woApplicationGenerator = new WOApplicationGenerator(projectGenerator);
		woDirectActionGenerator = new WODirectActionGenerator(projectGenerator);
		woMainGenerator = new WOMainGenerator(projectGenerator);
		if (projectGenerator != null && projectGenerator.getTarget() == CodeType.PROTOTYPE
				&& !projectGenerator.getRepository().includeReader()) {
			helpPopupGenerator = new HelpPopupGenerator(projectGenerator);
		}
		localizedStringGenerator = new LocalizedStringGenerator(projectGenerator);
		woSessionGenerator = new WOSessionGenerator(projectGenerator);
		userServiceGenerator = new UserServiceGenerator(projectGenerator);
		constantsGenerator = new ConstantsGenerator(projectGenerator);
		antUserFrameworksGenerator = new AntUserFrameworks(projectGenerator);
		if (projectGenerator != null && projectGenerator.getTarget() != CodeType.PROTOTYPE) {
			sqlGenerator = new SQLGenerator(projectGenerator, false);
			sqlGenerator2 = new SQLGenerator(projectGenerator, true);
		}
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		resetSecondaryProgressWindow(25);
		headerFooterGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		popupHeaderFooterGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		configGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		utilGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		directActionGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		defaultApplicationConfGenerator.buildResourcesAndSetGenerators(repository, resources);
		applicationConfGenerator.buildResourcesAndSetGenerators(repository, resources);
		applicationConfProdGenerator.buildResourcesAndSetGenerators(repository, resources);
		dotClasspathGenerator.buildResourcesAndSetGenerators(repository, resources);
		dotProjectGenerator.buildResourcesAndSetGenerators(repository, resources);
		buildXmlGenerator.buildResourcesAndSetGenerators(repository, resources);
		customComponentGenerator.buildResourcesAndSetGenerators(repository, resources);
		woApplicationGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		woDirectActionGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		woMainGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		if (projectGenerator != null && projectGenerator.getTarget() == CodeType.PROTOTYPE
				&& !projectGenerator.getRepository().includeReader() && helpPopupGenerator != null) {
			helpPopupGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		}
		localizedStringGenerator.buildResourcesAndSetGenerators(repository, resources); // OK
		woSessionGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		userServiceGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
		constantsGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir???
		antUserFrameworksGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir???
		buildPropertiesGenerator.buildResourcesAndSetGenerators(repository, resources);
		if (projectGenerator.getTarget() != CodeType.PROTOTYPE) {
			if (sqlGenerator == null) {
				sqlGenerator = new SQLGenerator((ProjectGenerator) projectGenerator, false);
			}
			sqlGenerator.buildResourcesAndSetGenerators(repository, resources); // A voir
			if (sqlGenerator2 == null) {
				sqlGenerator2 = new SQLGenerator((ProjectGenerator) projectGenerator, true);
			}
			sqlGenerator2.buildResourcesAndSetGenerators(repository, resources); // A voir
		}
	}

	public DirectActionGenerator getDirectActionGenerator() {
		return directActionGenerator;
	}

	public AntUserFrameworks getAntUserFrameworksGenerator() {
		return antUserFrameworksGenerator;
	}

	public ApplicationConfGenerator getApplicationConfGenerator() {
		return applicationConfGenerator;
	}

	public BuildXmlGenerator getBuildXmlGenerator() {
		return buildXmlGenerator;
	}

	public ConfigGenerator getConfigGenerator() {
		return configGenerator;
	}

	public ConstantsGenerator getConstantsGenerator() {
		return constantsGenerator;
	}

	public CustomComponentGenerator getCustomComponentGenerator() {
		return customComponentGenerator;
	}

	public DefaultApplicationConfGenerator getDefaultApplicationConfGenerator() {
		return defaultApplicationConfGenerator;
	}

	public DotClasspathGenerator getDotClasspathGenerator() {
		return dotClasspathGenerator;
	}

	public DotProjectGenerator getDotProjectGenerator() {
		return dotProjectGenerator;
	}

	public HeaderFooterGenerator getHeaderFooterGenerator() {
		return headerFooterGenerator;
	}

	public HelpPopupGenerator getHelpPopupGenerator() {
		return helpPopupGenerator;
	}

	public LocalizedStringGenerator getLocalizedStringGenerator() {
		return localizedStringGenerator;
	}

	public SQLGenerator getSqlGenerator() {
		return sqlGenerator;
	}

	public SQLGenerator getSqlGenerator2() {
		return sqlGenerator2;
	}

	public UserServiceGenerator getUserServiceGenerator() {
		return userServiceGenerator;
	}

	public UtilGenerator getUtilGenerator() {
		return utilGenerator;
	}

	public WOApplicationGenerator getWoApplicationGenerator() {
		return woApplicationGenerator;
	}

	public WODirectActionGenerator getWoDirectActionGenerator() {
		return woDirectActionGenerator;
	}

	public WOMainGenerator getWoMainGenerator() {
		return woMainGenerator;
	}

	public WOSessionGenerator getWoSessionGenerator() {
		return woSessionGenerator;
	}
}
