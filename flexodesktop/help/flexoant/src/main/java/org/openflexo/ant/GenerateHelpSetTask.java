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
package org.openflexo.ant;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoMainLocalizer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.ResourceLocator;

public class GenerateHelpSetTask extends Task {
	private static final Logger logger = FlexoLogger.getLogger(GenerateHelpSetTask.class.getPackage().getName());
	private String baseName;
	private Vector<HelpLanguage> languages;

	public void setResourceDir(File resourceDir) {
		if (resourceDir != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Setting resource path to " + resourceDir.getAbsolutePath());
			}
			ResourceLocator.resetFlexoResourceLocation(resourceDir);
		}
	}

	public GenerateHelpSetTask() {
		super();
	}

	public void setBaseName(String base) {
		this.baseName = base;
	}

	// The method executing the task
	@Override
	public void execute() throws BuildException {
		FlexoLocalization.initWith(new FlexoMainLocalizer());
		System.out.println(baseName + ": This class has been loaded by " + getClass().getClassLoader());
		GenerateHelpSet action = GenerateHelpSet.actionType.makeNewAction(DocResourceManager.instance().getDocResourceCenter(), null,
				new DefaultFlexoEditor(null));
		action.setNote("none");
		action.setBaseName(baseName);
		System.out.println("Helpset directory is " + action.getHelpsetDirectory().getAbsolutePath());
		for (HelpLanguage language : languages) {
			for (UserType userType : language.getDistributions()) {
				Language lg = DocResourceManager.instance().getDocResourceCenter().getLanguageNamed(language.getIsoCode());
				String title = language.getTitle();
				action.addToGeneration(title, lg, userType.getIdentifier(), userType.getDocumentationFolders());
				StringBuilder sb = new StringBuilder("Considering the following folders for " + userType.getName());
				for (DocItemFolder folder : userType.getDocumentationFolders()) {
					sb.append("\n");
					sb.append("* ").append(folder.getName());
				}
				System.out.println(sb.toString());
			}
		}
		action.doAction();
	}

	public void addConfiguredLanguage(HelpLanguage lg) {
		if (languages == null) {
			languages = new Vector<HelpLanguage>();
		}

		languages.add(lg);
	}

}